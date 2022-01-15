package com.sky.fodmap.service.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DownstreamDto {

    private boolean isHealthy;
    private String response;
}
