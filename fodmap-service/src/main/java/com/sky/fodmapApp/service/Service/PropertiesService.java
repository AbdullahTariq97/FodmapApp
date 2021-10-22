package com.sky.fodmapApp.service.Service;

import com.sky.fodmapApp.service.Models.ApplicationPropertiesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class PropertiesService {
    @Autowired
    private Environment environment;

    public com.sky.fodmapApp.service.Models.ApplicationPropertiesDTO getProperties(){
        com.sky.fodmapApp.service.Models.ApplicationPropertiesDTO applicationPropertiesDTO = new ApplicationPropertiesDTO();
        applicationPropertiesDTO.setName(environment.getProperty("app.name"));
        applicationPropertiesDTO.setEnvironment(Arrays.toString(environment.getActiveProfiles()));
        applicationPropertiesDTO.setVersion(environment.getProperty("app.version"));
        return applicationPropertiesDTO;
    }
}
