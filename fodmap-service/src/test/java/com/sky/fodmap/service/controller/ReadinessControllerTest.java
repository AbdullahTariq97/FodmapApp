package com.sky.fodmap.service.controller;

import com.sky.fodmap.service.controllers.ReadinessController;
import com.sky.fodmap.service.models.ApplicationPropertiesDTO;
import com.sky.fodmap.service.models.DownstreamDto;
import com.sky.fodmap.service.models.ReadinessDto;
import com.sky.fodmap.service.service.PropertiesService;
import com.sky.fodmap.service.service.ReadinessService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReadinessControllerTest {

    @Mock
    private ReadinessService readinessService;

    @Mock
    private PropertiesService propertiesService;

    @InjectMocks
    private ReadinessController readinessController;

    @Test
    public void whenReadinessControllerMethodInvokedShouldReturnReadinessDTO(){
        ApplicationPropertiesDTO applicationPropertiesDTO = new ApplicationPropertiesDTO();
        applicationPropertiesDTO.setName("FodmapApp");
        applicationPropertiesDTO.setEnvironment("[]");
        applicationPropertiesDTO.setVersion("0.0.1-SNAPSHOT");
        when(propertiesService.getProperties()).thenReturn(applicationPropertiesDTO);

        DownstreamDto downstreamDto = new DownstreamDto(true, "OK");

        Map<String, DownstreamDto> downstreams = Map.of("HeightApp",downstreamDto);

        when(readinessService.getServices()).thenReturn(downstreams);

        // Act
        ReadinessDto readinessDTO = readinessController.getDownstreamsStatus();

        //Assert
        Assertions.assertThat(readinessDTO).extracting("applicationName", "applicationEnvironment", "applicationVersion", "checkResults")
                  .containsExactly("FodmapApp", "[]", "0.0.1-SNAPSHOT", downstreams);
    }
}
