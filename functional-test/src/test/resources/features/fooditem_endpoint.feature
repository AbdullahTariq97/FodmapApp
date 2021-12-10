@my-test
Feature: should be able to return list of food items

  Scenario: should be able to return all food items in the database
    Given the database is populated with a record with following keys and values:
      | food_group | 'vegitable'                                                                    |
      | name       | 'argula'                                                                       |
      | data       | {overall_rating:'green',stratified_green:{'fructose':'green','lactose':'red'}} |
    Given the database is populated with a record with following keys and values:
      | food_group | 'fruit'                                                                        |
      | name       | 'apple'                                                                        |
      | data       | {overall_rating:'red',stratified_red:{'fructose':'red','lactose':'green'}}     |
    When the "get-by-group-and-name/fruit/apple" endpoint is polled
    Then status code of 200 should be returned
    Then the service should return response containing following keys and values:
    | foodGroup | fruit                                                                                                                           |
    | name       | apple                                                                                                                          |
    | data       | {"overallRating":"red", "stratifiedGreen":{}, "stratifiedAmber":{}, "stratifiedRed":{"fructose":"red","lactose":"green"}}       |
