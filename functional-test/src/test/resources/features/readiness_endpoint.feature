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

  Scenario: when downstream call to HeightApp times-out should return appropriate response
    Given "HeightApp" is not responding in 1 sec
    Given that the downstream "SleepApp" is healthy
    When the "private/readiness" endpoint is polled
    Then status code of 200 should be returned
    And the response body matching "timeout.json" should be returned

  Scenario: when HeightApp is Up and SleepApp is Up should increment metric counters
    Given application is restarted
    Given that the downstream "HeightApp" is healthy
    Given that the downstream "SleepApp" is healthy
    When the "private/readiness" endpoint is polled
    When the "private/metrics" endpoint is polled
    Then the response body should contain string "fodmap_downstreams_total{appName=\"HeightApp\",success=\"true\",} 1"
    Then the response body should contain string "fodmap_downstreams_total{appName=\"SleepApp\",success=\"true\",} 1"

  Scenario: when HeightApp is Up and SleepApp is Down should increment metric counters
    Given application is restarted
    Given that the downstream "HeightApp" is healthy
    Given that the downstream "SleepApp" is not healthy
    When the "private/readiness" endpoint is polled
    When the "private/metrics" endpoint is polled
    Then the response body should contain string "fodmap_downstreams_total{appName=\"HeightApp\",success=\"true\",} 1"
    Then the response body should contain string "fodmap_downstreams_total{appName=\"SleepApp\",success=\"false\",} 1"


  Scenario: when HeightApp is Down and SleepApp is Down should increment metric counters
    Given application is restarted
    Given that the downstream "HeightApp" is not healthy
    Given that the downstream "SleepApp" is not healthy
    When the "private/readiness" endpoint is polled
    When the "private/metrics" endpoint is polled
    Then the response body should contain string "fodmap_downstreams_total{appName=\"HeightApp\",success=\"false\",} 1"
    Then the response body should contain string "fodmap_downstreams_total{appName=\"SleepApp\",success=\"false\",} 1"

  Scenario: when HeightApp is Up and SleepApp timesout should increment metric counters
    Given application is restarted
    Given that the downstream "HeightApp" is healthy
    Given "SleepApp" is not responding in 1 sec
    When the "private/readiness" endpoint is polled
    When the "private/metrics" endpoint is polled
    Then the response body should contain string "fodmap_downstreams_total{appName=\"HeightApp\",success=\"true\",} 1"
    Then the response body should contain string "fodmap_downstreams_total{appName=\"SleepApp\",success=\"false\",} 1"

  Scenario: when HeightApp is down and SleepApp timesout should increment metric counters
    Given application is restarted
    Given that the downstream "HeightApp" is not healthy
    Given "SleepApp" is not responding in 1 sec
    When the "private/readiness" endpoint is polled
    When the "private/metrics" endpoint is polled
    Then the response body should contain string "fodmap_downstreams_total{appName=\"HeightApp\",success=\"false\",} 1"
    Then the response body should contain string "fodmap_downstreams_total{appName=\"SleepApp\",success=\"false\",} 1"

Scenario: when HeightApp is down and SleepApp timesout should increment metric counters
  Given application is restarted
  Given "HeightApp" is not responding in 1 sec
  Given "SleepApp" is not responding in 1 sec
  When the "private/readiness" endpoint is polled
  When the "private/metrics" endpoint is polled
  Then the response body should contain string "fodmap_downstreams_total{appName=\"HeightApp\",success=\"false\",} 1"
  Then the response body should contain string "fodmap_downstreams_total{appName=\"SleepApp\",success=\"false\",} 1"

