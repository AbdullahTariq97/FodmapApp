package com.sky.fodmap.service.service;

import com.sky.fodmap.service.configuration.CassandraConfig;
import com.sky.fodmap.service.configuration.MetricsConfiguration;
import com.sky.fodmap.service.repository.ItemRespository;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class MetricsCreationServiceTest {

    @MockBean
    private ItemRespository itemRespository;

    @MockBean
    private CassandraConfig cassandraConfig;

    private CollectorRegistry collectorRegistry;

    private MetricsCreationService metricsCreationService;

    // Given
    // We have to ensure the collector registry we pass to the counter bean injected in the MetricsCreationService
    // is the same one from which we attempt to obtain the value for the counter from after incrementing it
    // - Instantiate a CollectorRegistry and pass to the method that creates the counter in the MetricsConfig
    // - Pass the counter to the MetricsCreationService using constructor injection
    // - obtain the value of the counter from the registry that was passed to the counter
    @BeforeEach
    public void setupForCollectorRegistry(){
        collectorRegistry = new CollectorRegistry();
        MetricsConfiguration metricsConfiguration = new MetricsConfiguration();
        Counter counter = metricsConfiguration.counter(collectorRegistry);
        metricsCreationService = new MetricsCreationService(counter);
    }
    @Test
    public void whenSuccessMetricMethodInvoked_shouldIncrementCounterBy1(){
        // Then - before incrementing
        String[] labelValues = {"HeightApp", "true"};
        Optional<Double> countBefore = incrementCounter(labelValues);
        assertThat(countBefore).isEmpty();

        // When
        metricsCreationService.incrementSuccessMetric("HeightApp");

        // Then
        Optional<Double> countAfter = incrementCounter(labelValues);
        assertThat(countAfter.isPresent()).isTrue();
        assertThat(countAfter.get()).isEqualTo(1);
    }

    @Test
    public void whenFailureMetricMethodInvoked_shouldIncrementCounterBy1(){
        // Then - before incrementing
        String[] labelValues = {"HeightApp", "false"};
        Optional<Double> countBefore = incrementCounter(labelValues);
        assertThat(countBefore).isEmpty();

        // When
        metricsCreationService.incrementFailureMetric("HeightApp");

        // Then
        Optional<Double> countAfter = incrementCounter(labelValues);;
        assertThat(countAfter.isPresent()).isTrue();
        assertThat(countAfter.get()).isEqualTo(1);
    }

    private Optional<Double> incrementCounter(String[] labelValues){
        return Optional.ofNullable(collectorRegistry.getSampleValue("fodmap_downstreams_total",
                new String[] {"appName", "success"}, labelValues));
    }
}

