package com.sky.fodmap.service.service;

import com.sky.fodmap.service.models.DownstreamAddress;
import com.sky.fodmap.service.models.DownstreamDto;
import com.sky.fodmap.service.utilities.Client;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@Slf4j
@Service
public class ReadinessService {

    private Client client;

    public List<DownstreamAddress> listOfDownstreamAddresses;

    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Autowired
    public ReadinessService(Client client, List<DownstreamAddress> downstreamAddressList, CircuitBreakerRegistry circuitBreakerRegistry){
        this.client = client;
        this.listOfDownstreamAddresses = downstreamAddressList;
        this.circuitBreakerRegistry = circuitBreakerRegistry;
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

                if (httpResponse.body().equals("OK")) {

                    downstreamDto.setHealthy(true);
                    downstreamDto.setAdditionalProp1(Collections.singletonMap("response",null));
                } else {

                    downstreamDto.setHealthy(false);
                    downstreamDto.setAdditionalProp1(Collections.singletonMap("response",httpResponse.body()));
                }

                downstreamResponseMap.put(downstreamAddress.getName(), downstreamDto);

            } catch (IOException | InterruptedException e) {
                System.out.println(e.getClass().getName());

                downstreamDto.setAdditionalProp1(Collections.singletonMap("response",e.getClass().getName()));

                log.error("Error connecting to downstream " + downstreamAddress.getName());

                downstreamResponseMap.put(downstreamAddress.getName(), downstreamDto);
            } catch (Exception e) {
                downstreamResponseMap.put(downstreamAddress.getName(), downstreamDto);
            }
        }
        return downstreamResponseMap;
    }
}
