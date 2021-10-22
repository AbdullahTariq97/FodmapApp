package com.sky.fodmapApp.service.Models;

import lombok.Data;
import java.util.Map;

@Data
public class DownstreamServiceDTO {
    private boolean isHealthy;
    private Map<String, String> additionalProp1;
}
