Feature: should be able to return list of food items

  Scenario: should be able to return all food items in the database
    Given the database is populated with a record with following keys and values:
    | food_group | 'vegitable'                                                                    |
    | name       | 'argula'                                                                       |
    | data       | {'green':{amount:75,fructose:'G',lactose:'G',manitol:'G',sorbitol:'G',gos:'G',fructan:'G'}} |
    When the "/user/food-groups" endpoint is polled with header:
      | Authorization | Basic dXNlcm5hbWU6cGFzc3dvcmQ= |
    Then status code of 200 should be returned
    Then the service should return list matching:
    | vegitable |

  Scenario: when API polled without valid header authentication should return error response
    Given the database is populated with a record with following keys and values:
      | food_group | 'vegitable'                                                                    |
      | name       | 'argula'                                                                       |
      | data       | {'green':{amount:75,fructose:'G',lactose:'G',manitol:'G',sorbitol:'G',gos:'G',fructan:'G'}} |
    When the "/user/food-groups" endpoint is polled with header:
      | Authorization | Basic dXNlcm5hbwdwc= |
    Then status code of 401 should be returned
    Then the service should return error response containing following keys and values:
      | Description | security failure |