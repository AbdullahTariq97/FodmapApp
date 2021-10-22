package com.sky.fodmapApp.service.Controllers;

import com.sky.fodmapApp.service.Models.ApplicationPropertiesDTO;
import com.sky.fodmapApp.service.Models.ReadinessDTO;
import com.sky.fodmapApp.service.Service.PropertiesService;
import com.sky.fodmapApp.service.Service.ReadinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReadinessController {

    @Autowired
    private PropertiesService propertiesService;

    @Autowired
    private ReadinessService readinessService;

    @GetMapping("/readiness")
    public ReadinessDTO readinessController(){
        ReadinessDTO readinessDTO = new ReadinessDTO();

        //Setting first three attributes of ReadinessDTO class
        //DTO's have thier properties set using service classes. The methods in service classes return an object of DTO type
        ApplicationPropertiesDTO applicationPropertiesDTO = propertiesService.getProperties();


        readinessDTO.setApplicationName(applicationPropertiesDTO.getName());
        readinessDTO.setApplicationEnvironment(applicationPropertiesDTO.getEnvironment());
        readinessDTO.setApplicationVersion(applicationPropertiesDTO.getVersion());

        //Now we set the last attribute for ReadinessDTO object
        readinessDTO.setCheckResults(readinessService.getServices());
        return readinessDTO;
    }
}
