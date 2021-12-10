Feature: Returns information about downstreams

  Scenario: when SleepApp and HeightApp are both healthy
    Given that the downstream "HeightApp" is healthy
    Given that the downstream "SleepApp" is healthy
    When the "private/readiness" endpoint is polled
    Then status code of 200 should be returned
    And the response body matching "sleep-and-height-app-healthy.json" should be returned

  Scenario: when SleepApp and HeightApp are both unhealthy
    Given that the downstream "HeightApp" is not healthy
    Given that the downstream "SleepApp" is not healthy
    When the "private/readiness" endpoint is polled
    Then status code of 200 should be returned
    And the response body matching "sleep-and-height-app-not-healthy.json" should be returned

  Scenario: when HeightApp is healthy and SleepApp is not healthy
    Given that the downstream "HeightApp" is healthy
    Given that the downstream "SleepApp" is not healthy
    When the "private/readiness" endpoint is polled
    Then status code of 200 should be returned
    And the response body matching "height-app-healthy-sleep-app-unhealthy.json" should be returned