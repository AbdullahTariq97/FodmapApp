Feature: Returns information about downstreams and increment counters

  Scenario: when SleepApp and HeightApp are both healthy should return appropriate response
    Given that the downstream "HeightApp" is healthy
    Given that the downstream "SleepApp" is healthy
    When the "private/readiness" endpoint is polled
    Then status code of 200 should be returned
    And the response body matching "sleep-and-height-app-healthy.json" should be returned

  Scenario: when SleepApp and HeightApp are both unhealthy should return appropriate response
    Given that the downstream "HeightApp" is not healthy
    Given that the downstream "SleepApp" is not healthy
    When the "private/readiness" endpoint is polled
    Then status code of 200 should be returned
    And the response body matching "sleep-and-height-app-not-healthy.json" should be returned

  Scenario: when HeightApp is healthy and SleepApp is not healthy should return appropriate response
    Given that the downstream "HeightApp" is healthy
    Given that the downstream "SleepApp" is not healthy
    When the "private/readiness" endpoint is polled
    Then status code of 200 should be returned
    And the response body matching "height-app-healthy-sleep-app-unhealthy.json" should be returned

  Scenario: when downstream call to HeightApp times-out should return appropriate response
    Given "HeightApp" is not responding in 1 sec
    Given that the downstream "SleepApp" is healthy
    When the "private/readiness" endpoint is polled
    Then status code of 200 should be returned
    And the response body matching "timeout.json" should be returned

  Scenario: when HeightApp is Up and SleepApp is Up should increment metric counters
    Given that the downstream "HeightApp" is healthy
    Given that the downstream "SleepApp" is healthy
    When the "private/readiness" and then "private/metrics" endpoints are polled
    Then the  metric "fodmap_downstreams_total{appName=\"HeightApp\",success=\"true\",}" should increment by 1
    Then the  metric "fodmap_downstreams_total{appName=\"SleepApp\",success=\"true\",}" should increment by 1


  Scenario: when HeightApp is Up and SleepApp is Down should increment metric counters
    Given that the downstream "HeightApp" is healthy
    Given that the downstream "SleepApp" is not healthy
    When the "private/readiness" and then "private/metrics" endpoints are polled
    Then the  metric "fodmap_downstreams_total{appName=\"HeightApp\",success=\"true\",}" should increment by 1
    Then the  metric "fodmap_downstreams_total{appName=\"SleepApp\",success=\"false\",}" should increment by 1


  Scenario: when HeightApp is Down and SleepApp is Down should increment metric counters
    Given that the downstream "HeightApp" is not healthy
    Given that the downstream "SleepApp" is not healthy
    When the "private/readiness" and then "private/metrics" endpoints are polled
    Then the  metric "fodmap_downstreams_total{appName=\"HeightApp\",success=\"false\",}" should increment by 1
    Then the  metric "fodmap_downstreams_total{appName=\"SleepApp\",success=\"false\",}" should increment by 1

  Scenario: when HeightApp is Up and SleepApp times-out should increment metric counters
    Given that the downstream "HeightApp" is healthy
    Given "SleepApp" is not responding in 1 sec
    When the "/private/readiness" and then "/private/metrics" endpoints are polled
    Then the  metric "fodmap_downstreams_total{appName=\"HeightApp\",success=\"true\",}" should increment by 1
    Then the  metric "fodmap_downstreams_total{appName=\"SleepApp\",success=\"false\",}" should increment by 1

  Scenario: when HeightApp is down and SleepApp times-out should increment metric counters
    Given that the downstream "HeightApp" is not healthy
    Given "SleepApp" is not responding in 1 sec
    When the "/private/readiness" and then "/private/metrics" endpoints are polled
    Then the  metric "fodmap_downstreams_total{appName=\"HeightApp\",success=\"false\",}" should increment by 1
    Then the  metric "fodmap_downstreams_total{appName=\"SleepApp\",success=\"false\",}" should increment by 1

Scenario: when HeightApp is down and SleepApp times-out should increment metric counters
  Given "HeightApp" is not responding in 1 sec
  Given "SleepApp" is not responding in 1 sec
  When the "/private/readiness" and then "/private/metrics" endpoints are polled
  Then the  metric "fodmap_downstreams_total{appName=\"HeightApp\",success=\"false\",}" should increment by 1
  Then the  metric "fodmap_downstreams_total{appName=\"SleepApp\",success=\"false\",}" should increment by 1