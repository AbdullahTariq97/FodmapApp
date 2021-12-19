package com.sky.fodmap.service.service;

import com.sky.fodmap.service.models.DownstreamAddress;
import com.sky.fodmap.service.models.DownstreamDto;
import com.sky.fodmap.service.utilities.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ReadinessService {

    @Autowired
    private Client client;

    @Autowired
    public List<DownstreamAddress> listOfDownstreamAddresses;

    public Map<String, DownstreamDto> getServices(){

        Map<String, DownstreamDto> downstreamResponseMap = new HashMap<>();

        for(DownstreamAddress downstreamAddress : listOfDownstreamAddresses){

            DownstreamDto downstreamDto = new DownstreamDto();

            downstreamDto.setHealthy(false);

            try {
                HttpResponse<String> httpResponse = client.sendHttpRequest(downstreamAddress.getAddress());

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
            }
        }
        return downstreamResponseMap;
    }
}
