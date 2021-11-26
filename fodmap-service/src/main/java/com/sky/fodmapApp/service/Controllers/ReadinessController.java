package com.sky.fodmapApp.service.Controllers;

import com.sky.fodmapApp.service.Models.ApplicationPropertiesDTO;
import com.sky.fodmapApp.service.Models.ReadinessDTO;
import com.sky.fodmapApp.service.Service.PropertiesService;
import com.sky.fodmapApp.service.Service.ReadinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

@Component
@Endpoint(id = "readiness")
public class ReadinessController {

    @Autowired
    private PropertiesService propertiesService;

    @Autowired
    private ReadinessService readinessService;

    @ReadOperation
    public ReadinessDTO readinessController() {

        ReadinessDTO readinessDTO = new ReadinessDTO();

        ApplicationPropertiesDTO applicationPropertiesDTO = propertiesService.getProperties();

        readinessDTO.setApplicationName(applicationPropertiesDTO.getName());
        readinessDTO.setApplicationEnvironment(applicationPropertiesDTO.getEnvironment());
        readinessDTO.setApplicationVersion(applicationPropertiesDTO.getVersion());

        readinessDTO.setCheckResults(readinessService.getServices());

        return readinessDTO;
    }
}
