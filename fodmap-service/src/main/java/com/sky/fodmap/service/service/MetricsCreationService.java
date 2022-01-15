package com.sky.fodmap.service.service;

import io.prometheus.client.Counter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MetricsCreationService {

    private final Counter counter;

    @Autowired
    public MetricsCreationService(Counter counter){
        this.counter = counter;
    }

    public void incrementSuccessMetric(String downstreamName) {
        counter.labels(downstreamName, "true").inc();
    }

    public void incrementFailureMetric(String downstreamName) {
        counter.labels(downstreamName, "false").inc();
    }
}
