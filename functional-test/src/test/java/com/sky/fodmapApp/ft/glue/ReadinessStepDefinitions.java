package com.sky.fodmapApp.ft.glue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
//import com.sky.fodmapApp.ft.config.CucumberSpringContextConfigration;
import com.sky.fodmapApp.Models.ReadinessDTO;
import com.sky.fodmapApp.ft.config.CucumberSpringContextConfigration;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = CucumberSpringContextConfigration.class)
public class ReadinessStepDefinitions {

    private HttpResponse<String> httpResponse;
    private static final int WIREMOCK_PORT = 9000;
    private static final WireMockServer wiremockServer = new WireMockServer(options().port(WIREMOCK_PORT));
    private static int counter = 0;

    @PostConstruct
    public void startupWiremockServer(){
        wiremockServer.start();
        WireMock.configureFor("localhost", WIREMOCK_PORT);
    }

    @PreDestroy
    public void shutDownWiremockServer(){
        wiremockServer.stop();
    }

    @Given("that the downstream {string} is healthy")
    public void that_the_downstream_is_healthy(String string) {
        stubFor(
                get(urlEqualTo("/" + string.toLowerCase()))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type","text/plain")
                                        .withStatus(200)
                                        .withBody("OK")
                        )
        );
    }

    @Given("that the downstream {string} is not healthy")
    public void that_the_downstream_is_not_healthy(String string) {
        stubFor(
                get(urlEqualTo("/" + string.toLowerCase()))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type","text/plain")
                                        .withStatus(500)
                                        .withBody("Downstream Not Healthy, internal server error")
                        )
        );
    }

    @Given("that the downstream {string} has failed")
    public void that_the_downstream_has_failed(String string) {
        wiremockServer.stop();
    }

    @When("the {string} endpoint is polled")
    public void the_endpoint_is_polled(String endPoint) {
        String urlString = UriComponentsBuilder
                .fromUriString("http://localhost:8088")
                .path(endPoint)
                .build()
                .toString();

        try {
            URL url = new URL(urlString);
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(url.toString())).build();
            HttpClient httpClient = HttpClient.newHttpClient();
            httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            System.out.println("The " + counter + " response is " + httpResponse.body());
            counter = counter + 1;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Then("status code of {int} should be returned")
    public void status_code_of_should_be_returned(Integer expectedResponseCode) {
        assertThat(httpResponse.statusCode()).isEqualTo(expectedResponseCode);
    }

    @Then("the response body matching {string} should be returned")
    public void the_response_body_matching_should_be_returned(String mappingFileName) {
        ObjectMapper objectMapper = new ObjectMapper();
        ReadinessDTO expectedReadinessDTO;
        Optional<InputStream> stream = Optional.ofNullable(getClass().getClassLoader().getResourceAsStream("features/expected-mappings" + mappingFileName));
        if(!stream.isEmpty()){
            try {
                ReadinessDTO actualReadinessDTO = objectMapper.readValue(httpResponse.body(), ReadinessDTO.class);
                expectedReadinessDTO = objectMapper.readValue(stream.get(), ReadinessDTO.class);
                assertThat(actualReadinessDTO).extracting("applicationName", "checkResults").containsExactly(expectedReadinessDTO.getApplicationName(), expectedReadinessDTO.getCheckResults());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}
