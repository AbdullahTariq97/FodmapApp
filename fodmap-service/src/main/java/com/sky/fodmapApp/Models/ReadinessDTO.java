package com.sky.fodmapApp.Models;

import com.sky.fodmapApp.Models.DownstreamServiceDTO;
import lombok.Data;

import java.util.Map;

@Data
public class ReadinessDTO {
    private String applicationName;
    private String applicationEnvironment;
    private String applicationVersion;
    private Map<String, DownstreamServiceDTO> checkResults;
}
