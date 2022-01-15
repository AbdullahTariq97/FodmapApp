package com.sky.fodmap.service.controllers;

import com.sky.fodmap.service.models.ApplicationPropertiesDTO;
import com.sky.fodmap.service.models.ReadinessDto;
import com.sky.fodmap.service.service.PropertiesService;
import com.sky.fodmap.service.service.ReadinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReadinessController {

    @Autowired
    private PropertiesService propertiesService;

    @Autowired
    private ReadinessService readinessService;

    @GetMapping("/private/readiness")
    public ReadinessDto getDownstreamsStatus() {

        ReadinessDto readinessDTO = new ReadinessDto();

        ApplicationPropertiesDTO applicationPropertiesDTO = propertiesService.getProperties();

        readinessDTO.setApplicationName(applicationPropertiesDTO.getName());
        readinessDTO.setApplicationEnvironment(applicationPropertiesDTO.getEnvironment());
        readinessDTO.setApplicationVersion(applicationPropertiesDTO.getVersion());

        readinessDTO.setCheckResults(readinessService.getServices());

        return readinessDTO;
    }
}
