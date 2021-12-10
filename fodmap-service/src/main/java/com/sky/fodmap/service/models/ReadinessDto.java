package com.sky.fodmap.service.models;

import lombok.Data;

import java.util.Map;

@Data
public class ReadinessDto {

    private String applicationName;
    private String applicationEnvironment;
    private String applicationVersion;
    private Map<String, DownstreamDto> checkResults;
}
