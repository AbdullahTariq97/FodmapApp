package com.sky.fodmap.service.service;

import com.datastax.driver.core.Session;
import com.sky.fodmap.service.models.DownstreamAddress;
import com.sky.fodmap.service.models.DownstreamDto;
import com.sky.fodmap.service.utilities.Client;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.cassandra.config.CassandraSessionFactoryBean;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class ReadinessServiceTest {

    private Client client;

    private ReadinessService readinessService;

    @MockBean
    private CircuitBreakerRegistry circuitBreakerRegistryMock;

    private List<DownstreamAddress> listOfDownstreamAddresses = List.of(new DownstreamAddress("HeightApp", "http://localhost:9000/heightapp")
            , new DownstreamAddress("SleepApp", "http://localhost:9000/sleepapp"));

    @BeforeEach
    public void setup(){
        client = mock(Client.class);
        readinessService = new ReadinessService(client, listOfDownstreamAddresses,circuitBreakerRegistryMock);
    }

    @Test
    public void givenResponseBodyContainsOKForBothApps_shouldReturnApporpriateMap() throws IOException, InterruptedException {
        // Given
        List<String> listOfDownstreamNames = listOfDownstreamAddresses.stream().map(DownstreamAddress::getName).collect(Collectors.toList());

        HttpResponse<String> responseMock = mock(HttpResponse.class);
        when(responseMock.body()).thenReturn("OK");
        when(client.sendHttpRequest("http://localhost:9000/heightapp")).thenReturn(responseMock);
        when(client.sendHttpRequest("http://localhost:9000/sleepapp")).thenReturn(responseMock);

        // When
        Map<String, DownstreamDto> downstreamResponseMap = readinessService.getServices();

        // Then
        assertEquals(downstreamResponseMap.size(), listOfDownstreamAddresses.size());

        assertTrue(downstreamResponseMap.entrySet().stream().anyMatch(
                (set) -> listOfDownstreamNames.contains(set.getKey())
                        && set.getValue()
                        .equals(new DownstreamDto(true, Collections.singletonMap("response", null)))));

    }

    @Test
    public void givenResponseBodyContainsOkaForHeightAppOnly_shouldReturnAppropriateResponse() throws IOException, InterruptedException {
        // Given
        List<String> listOfDownstreamNames = listOfDownstreamAddresses.stream().map(DownstreamAddress::getName).collect(Collectors.toList());
        listOfDownstreamNames.forEach(System.out::println);

        HttpResponse<String> responseMock1 = mock(HttpResponse.class);
        HttpResponse<String> responseMock2 = mock(HttpResponse.class);

        when(responseMock1.body()).thenReturn("OK");
        when(responseMock2.body()).thenReturn("Other than OK");

        when(client.sendHttpRequest("http://localhost:9000/heightapp")).thenReturn(responseMock1);
        when(client.sendHttpRequest("http://localhost:9000/sleepapp")).thenReturn(responseMock2);

        // When
        Map<String, DownstreamDto> downstreamResponseMap = readinessService.getServices();

        // Then
        assertTrue(downstreamResponseMap.entrySet().stream().anyMatch(
                (set) -> listOfDownstreamNames.contains(set.getKey()) && set.getValue().equals(new DownstreamDto(true, Collections.singletonMap("response", null)))));

        assertTrue(downstreamResponseMap.entrySet().stream().anyMatch(
                (set) -> listOfDownstreamNames.contains(set.getKey()) && set.getValue().equals(new DownstreamDto(false, Collections.singletonMap("response", "Other than OK")))));
    }

    @ParameterizedTest
    @MethodSource("exceptions")
    public void givenClientThrowsIOException_shouldReturnAppropriateResponse(Class exceptionToThrow, String exceptionName) throws IOException, InterruptedException {
        // Given
        List<String> listOfDownstreamNames = listOfDownstreamAddresses.stream().map(DownstreamAddress::getName).collect(Collectors.toList());

        HttpResponse<String> responseMock1 = mock(HttpResponse.class);

        when(responseMock1.body()).thenReturn("OK");

        when(client.sendHttpRequest("http://localhost:9000/heightapp")).thenReturn(responseMock1);
        when(client.sendHttpRequest("http://localhost:9000/sleepapp")).thenThrow(exceptionToThrow);

        // When
        Map<String, DownstreamDto> downstreamResponseMap = readinessService.getServices();

        // Then
        assertTrue(downstreamResponseMap.entrySet().stream().anyMatch(
                (set) -> listOfDownstreamNames.contains(set.getKey())
                        && set.getValue()
                        .equals(new DownstreamDto(true, Collections.singletonMap("response", null)))));

        assertTrue(downstreamResponseMap.entrySet().stream().anyMatch(
                (set) -> listOfDownstreamNames.contains(set.getKey())
                        && set.getValue()
                        .equals(new DownstreamDto(false, Collections.singletonMap("response", exceptionName)))));

    }

    public static Stream<Arguments> exceptions(){
        return Stream.of(Arguments.of(IOException.class, "java.io.IOException"),
                Arguments.of(InterruptedException.class, "java.lang.InterruptedException"));
    }

    @Test
    public void whenReadinessEndpointCalled_shouldInvokeCircuitBreaker() throws IOException, InterruptedException {
        // Given
        HttpResponse<String> responseMock = mock(HttpResponse.class);
//        when(responseMock.body()).thenReturn("OK");
//        when(client.sendHttpRequest("http://localhost:9000/heightapp")).thenReturn(responseMock);
//        when(client.sendHttpRequest("http://localhost:9000/sleepapp")).thenReturn(responseMock);

        CircuitBreaker circuitBreakerMock = mock(CircuitBreaker.class);
        when(circuitBreakerRegistryMock.circuitBreaker("circuit-breaker")).thenReturn(circuitBreakerMock);

        MockedStatic<CircuitBreaker> circuitBreakerStaticMock = mockStatic(CircuitBreaker.class);

        // When
        readinessService.getServices();

        // Then
        verify(circuitBreakerRegistryMock).circuitBreaker("circuit-breaker");

        circuitBreakerStaticMock.verify(() -> CircuitBreaker
                    .decorateCallable(eq(circuitBreakerMock),any()), times(2));

        circuitBreakerStaticMock.close();

        // When the readinessService.getService method is called it does not have
//        try(MockedStatic<CircuitBreaker> circuitBreakerStaticMock = mockStatic(CircuitBreaker.class)){
//            circuitBreakerStaticMock.verify(() -> CircuitBreaker
//                    .decorateCallable(eq(circuitBreakerMock),any()));
//        }

    }
}
