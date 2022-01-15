package com.sky.fodmap.service.configuration;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfiguration {

    @Bean
    public CollectorRegistry collectorRegistry() {
        return new CollectorRegistry();
    }

    @Bean
    public Counter counter(CollectorRegistry collectorRegistry){
        return Counter.build().name("fodmap_downstreams")
                .help("fodmap_help")
                .labelNames("appName", "success")
                .register(collectorRegistry);
    }
}
