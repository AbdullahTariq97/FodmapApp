package com.sky.fodmap.service.configuration;

import com.sky.fodmap.service.models.DownstreamAddress;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class DownstreamInfoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "downstreams")
    public List<DownstreamAddress> listOfDownstreamAddresses(){
        return new ArrayList<>();
    }

}
