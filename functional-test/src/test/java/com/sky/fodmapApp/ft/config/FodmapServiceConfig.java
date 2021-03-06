package com.sky.fodmapApp.ft.config;

import com.sky.fodmap.service.FodmapApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Configuration
@DependsOn("cassandraSession")
public class FodmapServiceConfig {

    private ConfigurableApplicationContext configurableApplicationContext;

    @PostConstruct
    public void startupFodmapService(){
        configurableApplicationContext = SpringApplication.run(FodmapApplication.class);
    }

    @PreDestroy
    public void stopFodmapService(){
        configurableApplicationContext.close();
    }
}
