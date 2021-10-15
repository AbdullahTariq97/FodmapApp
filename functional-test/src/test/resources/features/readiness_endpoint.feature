Feature: Returns information about downstreams
  Scenario: when the downstream is not healthy
    Given that the downstream "HeightApp" is not healthy
    When the "/readiness" endpoint is polled
    Then status code of 200 should be returned
    And the response body matching "height-app-not-healthy.json" should be returned

  Scenario: when the downstream request has failed
    Given that the downstream "HeightApp" has failed
    When the "/readiness" endpoint is polled
    Then status code of 200 should be returned
    And the response body matching "height-app-failed.json" should be returned

  Scenario: when the downstream is healthy
    Given that the downstream "HeightApp" is healthy
    When the "/readiness" endpoint is polled
    Then status code of 200 should be returned
    And the response body matching "height-app-healthy.json" should be returned

