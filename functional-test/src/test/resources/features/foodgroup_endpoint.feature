Feature: should be able to return list of food items

  Scenario: should be able to return all food items in the database
    Given the database is populated with a record with following keys and values:
    | food_group | 'vegitable'                                                                    |
    | name       | 'argula'                                                                       |
    | data       | {overall_rating:'green',stratified_green:{'fructose':'green','lactose':'red'}} |
    When the "/food-groups" endpoint is polled
    Then status code of 200 should be returned
    Then the service should return list matching:
    | vegitable |