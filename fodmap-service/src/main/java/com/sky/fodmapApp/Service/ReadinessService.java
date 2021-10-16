package com.sky.fodmapApp.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.sky.fodmapApp.Models.DownstreamAddress;
import com.sky.fodmapApp.Models.DownstreamServiceDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

@Slf4j
@Service
public class ReadinessService {

    private Environment environment;

    private ObjectMapper objectMapper;

    // Constructor 1
    // @Autowired looks are the data types of the parameters in the consutructor and tries to find beans of the same type and inject them in
    // @Autowire annotation looks for beans for the Environment type and injects and sets value for environment instance variable
    // The value of the objectMapper instance variable is set to reference ObjectMapper instance
    // This contructor will set the default values for the instance variables
    @Autowired
    public ReadinessService(Environment environment){
        this.environment = environment;
        this.objectMapper = new ObjectMapper(new YAMLFactory());
    }

    // Constructor 2
    // This overloaded constructor allows us to modify the default values of the instance variables.
    // This way we can inject mocks for our dependencies when required
    public ReadinessService(Environment environment, ObjectMapper objectMapper){
        this.environment = environment;
        this.objectMapper = objectMapper;
    }

    public List<DownstreamAddress> getDownstreamAddresses(){

        String fileName = Optional.ofNullable(environment.getProperty("files.services")).orElse("");

        if (fileName.isEmpty()) {
            return Collections.emptyList();
        }

        Optional<InputStream> str = Optional.ofNullable(getClass().getClassLoader().getResourceAsStream(fileName));

        if(str.isEmpty()){
            return Collections.emptyList();
        } else {
            try {
                return objectMapper.readValue(str.get(), new TypeReference<List<DownstreamAddress>>() {});
            } catch (IOException e) {
                log.error(e.getMessage());
                return Collections.emptyList();
            }
        }
    }

    public Map<String, DownstreamServiceDTO> getServices(){

        Map<String, DownstreamServiceDTO> downstreams = new HashMap<>();

        List<DownstreamAddress> listOfDownstreamAddresses = getDownstreamAddresses();

        for(DownstreamAddress element : listOfDownstreamAddresses){
            DownstreamServiceDTO downstreamServiceDTO = new DownstreamServiceDTO();
            // Default value of downstreamServiceDTO attribute isHealthy is false;
            downstreamServiceDTO.setHealthy(false);

            // Need to make a call to each downstream URL. If response body oka, set isHealthy to true. If not oka, set to false and set value of additionalProp1
            try {
                // This creates a URL object
                URL url = new URL(element.getAddress());

                // The URL object needs to become part of a Http request
                HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(url.toString())).build();

                // This is the client that sends http request to the downstream
                HttpClient httpClient = HttpClient.newHttpClient();
                HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

                Map<String, String> response = new HashMap<>();

                // If downstream response body is OK then set property of DownstreamServiceDTO accordingly
                if (httpResponse.body().equalsIgnoreCase("OK")) {
                    downstreamServiceDTO.setHealthy(true);
                    response.put("response", null);
                    downstreamServiceDTO.setAdditionalProp1(response);
                } else {
                    downstreamServiceDTO.setHealthy(false);
                    response.put("response", httpResponse.body());
                    downstreamServiceDTO.setAdditionalProp1(response);
                }
                downstreams.put(element.getName(), downstreamServiceDTO);

            } catch (IOException | InterruptedException e) {
                // need to set healthy to false
                Map<String, String> response = new HashMap<>();
                response.put("response", e.getCause().toString());
                downstreamServiceDTO.setAdditionalProp1(response);
                log.error("Interrupted Exception or IO Exception");
                downstreams.put(element.getName(), downstreamServiceDTO);
            }
        }
        return downstreams;
    }
}
