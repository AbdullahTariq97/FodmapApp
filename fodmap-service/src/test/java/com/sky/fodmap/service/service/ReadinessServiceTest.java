package com.sky.fodmap.service.service;

import com.sky.fodmap.service.configuration.CassandraConfig;
import com.sky.fodmap.service.models.DownstreamAddress;
import com.sky.fodmap.service.models.DownstreamDto;
import com.sky.fodmap.service.repository.ItemRespository;
import com.sky.fodmap.service.utilities.Client;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.prometheus.client.Counter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class ReadinessServiceTest {

    private Client clientMock;

    private ReadinessService readinessService;

    private CircuitBreaker circuitBreakerMock;

    private MetricsCreationService metricsCreationServiceMock;

    // Putting in MockBeans for item repo and cassandra config means we don't have to start cassandra service for mock mvc tests
    @MockBean
    private ItemRespository itemRespository;

    @MockBean
    private CassandraConfig cassandraConfig;

    @MockBean
    private CircuitBreakerRegistry circuitBreakerRegistryMock;

    private List<DownstreamAddress> listOfDownstreamAddresses = List.of(new DownstreamAddress("HeightApp", "http://localhost:9000/heightapp")
            , new DownstreamAddress("SleepApp", "http://localhost:9000/sleepapp"));


    @BeforeEach
    public void setup(){
        clientMock = mock(Client.class);
        metricsCreationServiceMock = mock(MetricsCreationService.class);
        readinessService = new ReadinessService(clientMock, listOfDownstreamAddresses,circuitBreakerRegistryMock, metricsCreationServiceMock);
        // All tests require circuitBreakerRegistry to be stubbed
        circuitBreakerMock = mock(CircuitBreaker.class);
        when(circuitBreakerRegistryMock.circuitBreaker("circuit-breaker")).thenReturn(circuitBreakerMock);
    }

    @Test
    public void givenResponseBodyContainsOKForBothApps_shouldReturnAppropriateMap() throws IOException, InterruptedException {
        // Given
        List<String> listOfDownstreamNames = listOfDownstreamAddresses.stream().map(DownstreamAddress::getName).collect(Collectors.toList());

        HttpResponse<String> responseMock = mock(HttpResponse.class);
        when(responseMock.statusCode()).thenReturn(200);
        when(clientMock.sendHttpRequest("http://localhost:9000/heightapp")).thenReturn(responseMock);
        when(clientMock.sendHttpRequest("http://localhost:9000/sleepapp")).thenReturn(responseMock);

        // When
        Map<String, DownstreamDto> downstreamResponseMap = readinessService.getServices();

        // Then
        assertEquals(downstreamResponseMap.size(), listOfDownstreamAddresses.size());

        assertTrue(downstreamResponseMap.entrySet().stream().allMatch(
                (set) -> listOfDownstreamNames.contains(set.getKey())));

        assertTrue(downstreamResponseMap.entrySet().stream().allMatch(
                (set) -> set.getValue().getResponse().equals("OK")
                && set.getValue().isHealthy()));
    }

    @ParameterizedTest
    @ValueSource(ints = {400,500})
    public void givenHeightAppHealthyAndSleepAppWith4xxOR5xx_shouldReturnAppropriateResponse(int downstreamStatusCode) throws IOException, InterruptedException {
        // Given
        List<String> listOfDownstreamNames = listOfDownstreamAddresses.stream().map(DownstreamAddress::getName).collect(Collectors.toList());

        HttpResponse<String> responseMock1 = mock(HttpResponse.class);
        HttpResponse<String> responseMock2 = mock(HttpResponse.class);

        when(responseMock1.statusCode()).thenReturn(200);
        when(responseMock2.statusCode()).thenReturn(downstreamStatusCode);

        when(clientMock.sendHttpRequest("http://localhost:9000/heightapp")).thenReturn(responseMock1);
        when(clientMock.sendHttpRequest("http://localhost:9000/sleepapp")).thenReturn(responseMock2);

        // When
        Map<String, DownstreamDto> downstreamResponseMap = readinessService.getServices();

        // Then
        assertThat(downstreamResponseMap.get("HeightApp"))
                .extracting("response", "isHealthy").containsExactly("OK", true);

        assertThat(downstreamResponseMap.get("SleepApp"))
                .extracting("response", "isHealthy").containsExactly(null, false);
    }

    @Test
    public void whenReadinessEndpointCalled_shouldInvokeCircuitBreaker() throws IOException, InterruptedException {
        // Given
        // Passes in static mock for class circuit breaker class. CircuitBreaker.method()
        MockedStatic<CircuitBreaker> circuitBreakerStaticMock = mockStatic(CircuitBreaker.class);

        // When
        readinessService.getServices();

        // Then
        verify(circuitBreakerRegistryMock).circuitBreaker("circuit-breaker");

        circuitBreakerStaticMock.verify(() -> CircuitBreaker
                    .decorateCallable(eq(circuitBreakerMock),any()), times(2));

        circuitBreakerStaticMock.close();
    }

    @Test
    public void givenHeightAppIsUp_whenReadinessEndpointCalled_shouldIncrementDownstreamSuccessCounterOnce() throws IOException, InterruptedException {
        HttpResponse<String> responseMock1 = mock(HttpResponse.class);
        HttpResponse<String> responseMock2 = mock(HttpResponse.class);

        when(responseMock1.statusCode()).thenReturn(200);
        when(responseMock2.statusCode()).thenReturn(500);

        when(clientMock.sendHttpRequest("http://localhost:9000/heightapp")).thenReturn(responseMock1);
        when(clientMock.sendHttpRequest("http://localhost:9000/sleepapp")).thenReturn(responseMock2);

        // When
        readinessService.getServices();

        // Then
        verify(metricsCreationServiceMock).incrementSuccessMetric("HeightApp");
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 500})
    public void givenHeightAppIsUpAndSleepAppDown_whenReadinessEndpointCalled_shouldIncrementDownstreamSuccessAndFailureCounterOnce(int failureCode) throws IOException, InterruptedException {
        HttpResponse<String> responseMock1 = mock(HttpResponse.class);
        HttpResponse<String> responseMock2 = mock(HttpResponse.class);

        when(responseMock1.statusCode()).thenReturn(200);
        when(responseMock2.statusCode()).thenReturn(failureCode);

        when(clientMock.sendHttpRequest("http://localhost:9000/heightapp")).thenReturn(responseMock1);
        when(clientMock.sendHttpRequest("http://localhost:9000/sleepapp")).thenReturn(responseMock2);

        // When
        readinessService.getServices();

        // Then
        verify(metricsCreationServiceMock).incrementSuccessMetric("HeightApp");
        verify(metricsCreationServiceMock).incrementFailureMetric("SleepApp");
    }

    @Test
    public void givenHeightAppAndSleepAppUp_shouldIncrementDownstreamSuccessAndFailureCounterOnce() throws IOException, InterruptedException {
        HttpResponse<String> responseMock1 = mock(HttpResponse.class);
        HttpResponse<String> responseMock2 = mock(HttpResponse.class);

        when(responseMock1.statusCode()).thenReturn(200);
        when(responseMock2.statusCode()).thenReturn(200);

        when(clientMock.sendHttpRequest("http://localhost:9000/heightapp")).thenReturn(responseMock1);
        when(clientMock.sendHttpRequest("http://localhost:9000/sleepapp")).thenReturn(responseMock2);

        // When
        readinessService.getServices();

        // Then
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(metricsCreationServiceMock, times(2)).incrementSuccessMetric(captor.capture());
        assertTrue(captor.getAllValues().contains("HeightApp"));
        assertTrue(captor.getAllValues().contains("SleepApp"));
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 500})
    public void givenHeightAppAndSleepAppDown_shouldIncrementDownstreamSuccessAndFailureCounterOnce(int failureCode) throws IOException, InterruptedException {
        HttpResponse<String> responseMock1 = mock(HttpResponse.class);
        HttpResponse<String> responseMock2 = mock(HttpResponse.class);

        when(responseMock1.statusCode()).thenReturn(failureCode);
        when(responseMock2.statusCode()).thenReturn(failureCode);

        when(clientMock.sendHttpRequest("http://localhost:9000/heightapp")).thenReturn(responseMock1);
        when(clientMock.sendHttpRequest("http://localhost:9000/sleepapp")).thenReturn(responseMock2);

        // When
        readinessService.getServices();

        // Then
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(metricsCreationServiceMock, times(2)).incrementFailureMetric(captor.capture());
        assertTrue(captor.getAllValues().contains("HeightApp"));
        assertTrue(captor.getAllValues().contains("SleepApp"));
    }
}
