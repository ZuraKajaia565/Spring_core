Feature: Training Type Management
  As a gym administrator
  I want to manage training types
  So that I can organize workout sessions appropriately

  Scenario: Create a new training type
    Given I am logged in as an administrator
    When I create a new training type with name "Cardio" and description "Cardiovascular exercises"
    Then the training type should be successfully created
    And I should see the training type in the list