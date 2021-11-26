package com.sky.fodmapApp.service;

import com.sky.fodmapApp.service.Controllers.ReadinessController;
import com.sky.fodmapApp.service.Models.ApplicationPropertiesDTO;
import com.sky.fodmapApp.service.Models.DownstreamServiceDTO;
import com.sky.fodmapApp.service.Models.ReadinessDTO;
import com.sky.fodmapApp.service.Service.PropertiesService;
import com.sky.fodmapApp.service.Service.ReadinessService;
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
        // Before we invoke the method we want to test, we will mock the dependencies for this method to isolate the logic and then stub their responses
        ApplicationPropertiesDTO applicationPropertiesDTO = new ApplicationPropertiesDTO();
        applicationPropertiesDTO.setName("FodmapApp");
        applicationPropertiesDTO.setEnvironment("[]");
        applicationPropertiesDTO.setVersion("0.0.1-SNAPSHOT");
        when(propertiesService.getProperties()).thenReturn(applicationPropertiesDTO);

        Map<String, DownstreamServiceDTO> downstreams = new HashMap<>();
        DownstreamServiceDTO downstreamServiceDTO = new DownstreamServiceDTO();
        downstreamServiceDTO.setHealthy(true);
        Map<String, String> additionalProperty1 = new HashMap<>();
        additionalProperty1.put("response", null);
        downstreamServiceDTO.setAdditionalProp1(additionalProperty1);
        downstreams.put("HeightApp", downstreamServiceDTO);
        when(readinessService.getServices()).thenReturn(downstreams);

        // Act
        ReadinessDTO readinessDTO = readinessController.readinessController();

        //Assert
        Assertions.assertThat(readinessDTO).extracting("applicationName", "applicationEnvironment", "applicationVersion", "checkResults")
                  .containsExactly("FodmapApp", "[]", "0.0.1-SNAPSHOT", downstreams);
    }
}
