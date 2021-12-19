Feature: should be able to return list of food items

  Scenario: should be able to return all food items in the database
    Given the database is populated with a record with following keys and values:
      | food_group | 'vegitable'                                                                     |
      | name       | 'argula'                                                                        |
      | data       | {'green':{amount:75,fructose:'G',lactose:'G',manitol:'G',sorbitol:'G',gos:'G',fructan:'G'}}|
    And the database is populated with a record with following keys and values:
      | food_group | 'fruit'                                                                        |
      | name       | 'apple granny smith'                                                                        |
      | data       | {'red':{amount:75,fructose:'R',lactose:'G',manitol:'G',sorbitol:'R',gos:'G',fructan:'G'},'amber':{amount:75,fructose:'R',lactose:'G',manitol:'G',sorbitol:'A',gos:'G',fructan:'G'},'green':{amount:75,fructose:'G',lactose:'G',manitol:'G',sorbitol:'G',gos:'G',fructan:'G'}}    |
    When the "/user/get-by-group-and-name/fruit/apple-granny-smith" endpoint is polled with header:
      | Authorization | Basic dXNlcm5hbWU6cGFzc3dvcmQ= |
    Then status code of 200 should be returned
    Then the service should return response containing following keys and values:
    | foodGroup | fruit                                                                                                                           |
    | name       | apple granny smith                                                                                                                          |
    | data       | {"red":{"amount":75,"fructose":"R","lactose":"G","manitol":"G","sorbitol":"R","gos":"G","fructan":"G"},"amber":{"amount":75,"fructose":"R","lactose":"G","manitol":"G","sorbitol":"A","gos":"G","fructan":"G"},"green":{"amount":75,"fructose":"G","lactose":"G","manitol":"G","sorbitol":"G","gos":"G","fructan":"G"}} |

  Scenario: when API polled without valid header authentication should return error response
    Given the database is populated with a record with following keys and values:
      | food_group | 'vegitable'                                                                     |
      | name       | 'argula'                                                                        |
      | data       | {'green':{amount:75,fructose:'G',lactose:'G',manitol:'G',sorbitol:'G',gos:'G',fructan:'G'}}|
    And the database is populated with a record with following keys and values:
      | food_group | 'fruit'                                                                        |
      | name       | 'apple granny smith'                                                                        |
      | data       | {'red':{amount:75,fructose:'R',lactose:'G',manitol:'G',sorbitol:'R',gos:'G',fructan:'G'},'amber':{amount:75,fructose:'R',lactose:'G',manitol:'G',sorbitol:'A',gos:'G',fructan:'G'},'green':{amount:75,fructose:'G',lactose:'G',manitol:'G',sorbitol:'G',gos:'G',fructan:'G'}}    |
    When the "/user/get-by-group-and-name/fruit/apple-granny-smith" endpoint is polled with header:
      | Authorization | Basic dXNlcm5hbWU6casswcsu= |
    Then status code of 401 should be returned
    Then the service should return error response containing following keys and values:
    | Description | security failure |
