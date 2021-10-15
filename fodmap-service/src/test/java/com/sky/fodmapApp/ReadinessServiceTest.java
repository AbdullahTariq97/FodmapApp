package com.sky.fodmapApp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.sky.fodmapApp.Models.DownstreamAddress;
import com.sky.fodmapApp.Service.ReadinessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReadinessServiceTest {
    // Environment and ObjectMapper are both dependencies of the readinessService. these must be mocked

    // Creates mock of environment class
    Environment environment = mock(Environment.class);

    // Create spy of objectMapper class. Selected methods can be stubbed
    private ObjectMapper objectMapperSpy = Mockito.spy(new ObjectMapper(new YAMLFactory()));

    // Mocked dependencies inject through the constructor
    private ReadinessService readinessService = new ReadinessService(environment,objectMapperSpy);

    @Test
    public void shouldReturnListOfDownstreamAddresses(){
        when(environment.getProperty("files.services")).thenReturn("readiness/services-local.yml");
        List<DownstreamAddress> downstreamAddressList = readinessService.getDownstreamAddresses();
        assertThat(downstreamAddressList.get(0))
                .extracting("name", "address")
                .containsExactly("HeightApp", "http://localhost:9000/heightApp");
    }

    @Test
    public void whenNoPropertyLocationReturnedShouldReturnEmptyList(){
        when(environment.getProperty("files.services")).thenReturn("");
        List<DownstreamAddress> downstreamAddressList = readinessService.getDownstreamAddresses();
        assertTrue(downstreamAddressList.isEmpty());
    }

    @Test
    public void whenObjectMapperThrowsIOExceptionShouldReturnEmptyList() throws IOException {
        when(environment.getProperty("files.services")).thenReturn("readiness/services-local.yml");
        // Why use a spy for an object mapper?
        doThrow(new IOException()).when(objectMapperSpy).readValue(any(InputStream.class), any(TypeReference.class));
        assertTrue(readinessService.getDownstreamAddresses().isEmpty());
    }

   // Why isnt there unit tests for the getServices method?
}
