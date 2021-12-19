package com.sky.fodmap.service.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.data.cassandra")
@Getter
@Setter
public class CassandraProperties {

    private int port;
    private String contactPoints;
    private String keyspaceName;
}
