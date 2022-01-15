package com.sky.fodmap.service.service;

import com.sky.fodmap.service.models.DownstreamAddress;
import com.sky.fodmap.service.models.DownstreamDto;
import com.sky.fodmap.service.utilities.Client;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ReadinessService {

    private Client client;

    public List<DownstreamAddress> listOfDownstreamAddresses;

    private CircuitBreakerRegistry circuitBreakerRegistry;

    private MetricsCreationService metricsCreationService;

    @Autowired
    public ReadinessService(Client client,
                            List<DownstreamAddress> downstreamAddressList,
                            CircuitBreakerRegistry circuitBreakerRegistry,
                            MetricsCreationService metricsCreationService){
        this.client = client;
        this.listOfDownstreamAddresses = downstreamAddressList;
        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.metricsCreationService = metricsCreationService;
    }

    public Map<String, DownstreamDto> getServices(){

        Map<String, DownstreamDto> downstreamResponseMap = new HashMap<>();

        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("circuit-breaker");

        for(DownstreamAddress downstreamAddress : listOfDownstreamAddresses){

            DownstreamDto downstreamDto = new DownstreamDto();

            downstreamDto.setHealthy(false);

            try {
                HttpResponse<String> httpResponse = CircuitBreaker
                        .decorateCallable(circuitBreaker,() ->  client.sendHttpRequest(downstreamAddress.getAddress())).call();

                if(HttpStatus.valueOf(httpResponse.statusCode()).is4xxClientError() ||
                        HttpStatus.valueOf(httpResponse.statusCode()).is5xxServerError()){
                    downstreamDto.setHealthy(false);
                    downstreamDto.setResponse(null);
                    metricsCreationService.incrementFailureMetric(downstreamAddress.getName());
                } else {
                    downstreamDto.setHealthy(true);
                    downstreamDto.setResponse("OK");
                    metricsCreationService.incrementSuccessMetric(downstreamAddress.getName());
                }

                downstreamResponseMap.put(downstreamAddress.getName(), downstreamDto);

            }
            catch (Exception e) {
                downstreamDto.setHealthy(false);
                downstreamDto.setResponse(null);
                downstreamResponseMap.put(downstreamAddress.getName(), downstreamDto);
                metricsCreationService.incrementFailureMetric(downstreamAddress.getName());
                log.error("Error connecting to downstream " + downstreamAddress.getName());
            }
        }
        return downstreamResponseMap;
    }
}
