package com.sky.fodmap.service.service;

import com.sky.fodmap.service.models.DownstreamAddress;
import com.sky.fodmap.service.models.DownstreamDto;
import com.sky.fodmap.service.utilities.Client;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

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

    // Requires spring context to be spun up to get the bean this type of bean from context
    // Taken bean from application context/ ioc container and injects it as spy into readiness service
    @SpyBean
    private List<DownstreamAddress> listOfDownstreamAddresses;

    @Mock
    private Client client;

    @InjectMocks
    private ReadinessService readinessService;

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
}
