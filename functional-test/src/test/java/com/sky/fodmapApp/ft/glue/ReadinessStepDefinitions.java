package com.sky.fodmapApp.ft.glue;

import com.datastax.driver.core.Session;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.sky.fodmap.service.models.FoodItem;
import com.sky.fodmap.service.models.ReadinessDto;
import com.sky.fodmap.service.models.StratifiedData;
import com.sky.fodmapApp.ft.config.CucumberSpringContextConfigration;
import com.sky.fodmapApp.ft.utility.Client;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ContextConfiguration(classes = CucumberSpringContextConfigration.class)
public class ReadinessStepDefinitions {

    @Autowired
    private Session cassandraSession;

    @Autowired
    private Client client;

    private HttpResponse<String> httpResponse;
    private static final int WIREMOCK_PORT = 9000;
    private static final WireMockServer wiremockServer = new WireMockServer(options().port(WIREMOCK_PORT));
    private static final ObjectMapper objectMapper = new ObjectMapper();

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
                                        .withBody("response body of downstream")
                        )
        );
    }

    @When("the {string} endpoint is polled with header:")
    public void the_endpoint_is_polled(String endPoint, Map<String,String> requestHeader) {
        List<String> headersAndValuesList = new ArrayList<>();
        requestHeader.forEach((k,v) -> {
            headersAndValuesList.add(k);
            headersAndValuesList.add(v);
        });
        String[] headerAndValuesArray = headersAndValuesList.toArray(new String[headersAndValuesList.size()]);
        httpResponse = client.sendHttpRequest(endPoint, headerAndValuesArray);

    }

    @Then("status code of {int} should be returned")
    public void status_code_of_should_be_returned(Integer expectedResponseCode) {
        assertThat(httpResponse.statusCode()).isEqualTo(expectedResponseCode);
    }

    @Then("the response body matching {string} should be returned")
    public void the_response_body_matching_should_be_returned(String mappingFileName) {
        ObjectMapper objectMapper = new ObjectMapper();
        // Load the stream of expected ReadinessDTO's
        Optional<InputStream> stream = Optional.ofNullable(getClass().getClassLoader().getResourceAsStream("features/expected-mappings/" + mappingFileName));
        if(!stream.isEmpty()){
            try {
                ReadinessDto actualReadinessDto = objectMapper.readValue(httpResponse.body(), ReadinessDto.class);
                ReadinessDto expectedReadinessDto = objectMapper.readValue(stream.get(), ReadinessDto.class);
                assertThat(actualReadinessDto).extracting("applicationName", "checkResults").containsExactly(expectedReadinessDto.getApplicationName(), expectedReadinessDto.getCheckResults());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Then("the service should return response matching list below:")
    public void theServiceShouldReturnResponseMatchingListBelow(List<String> expectedFoodGroup) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> returnedListOfFoodGroups = objectMapper.convertValue(httpResponse.body(), new TypeReference<>() {});
        assertEquals(expectedFoodGroup, returnedListOfFoodGroups);
    }

    @Given("the database is populated with a record with following keys and values:")
    public void theDatabaseIsPopulatedWithARecordWithFollowingKeysAndValues(Map<String,String> expectedRecord) {
        // Deletes previous data in the food_item table
        cassandraSession.execute("TRUNCATE fodmap.food_item;");
        String commaSeperatedColumns = expectedRecord.keySet().toString().replace("[","").replace("]","");
        String commaSeperatedValues = expectedRecord.values().toString().replace("[","").replace("]","");

        cassandraSession.execute(String.format("INSERT INTO fodmap.food_item (%s) VALUES(%s);",commaSeperatedColumns, commaSeperatedValues));
    }

    @Then("the service should return list matching:")
    public void theServiceShouldReturnListMatching(List<String> expectedListOfFoodGroups) throws JsonProcessingException {
        JsonMapper jsonMapper = new JsonMapper();
        String expectedJson = jsonMapper.writeValueAsString(expectedListOfFoodGroups);
        assertThat(httpResponse.body()).isEqualTo(expectedJson);
    }

    @Then("the service should return response containing following keys and values:")
    public void theServiceShouldReturnResponseContainingFollowingKeysAndValues(Map<String,String> expectedFoodItem) throws JsonProcessingException {

        FoodItem returnedItem = objectMapper.readValue(httpResponse.body(), new TypeReference<>() {});
        Map<String,StratifiedData> expectedFodmapData = objectMapper.readValue(expectedFoodItem.get("data"), new TypeReference<>() {});

        FoodItem expectedItem = FoodItem.builder().name(expectedFoodItem.get("name")).foodGroup(expectedFoodItem.get("foodGroup")).data(expectedFodmapData).build();

        assertThat(returnedItem).usingRecursiveComparison().isEqualTo(expectedItem);
    }

    @Then("the service should return error response containing following keys and values:")
    public void error_response(Map<String,String> expectedErrorResponse) throws JsonProcessingException {
        Map<String,String> actualErrorResponse = objectMapper.readValue(httpResponse.body(), new TypeReference<>() {});
        assertThat(actualErrorResponse).usingRecursiveComparison().isEqualTo(expectedErrorResponse);
    }

    @When("the {string} endpoint is polled")
    public void theEndpointIsPolled(String endPoint) {
        httpResponse = client.sendHttpRequest(endPoint);
    }
}
