```gherkin
Feature: Training Type Management

  Scenario: Get all training types
    When I request all training types
    Then the training types are returned successfully
    And the list includes at least "Strength" and "Cardio" types

  Scenario: Get training type by ID
    When I request the training type with ID "1"
    Then the training type is returned successfully
    And the training type name is "Strength"

  Scenario: Get training type by name
    When I request the training type with name "Cardio"
    Then the training type is returned successfully
    And the training type ID is "2"

  Scenario: Get non-existent training type
    When I request the training type with ID "999"
    Then I receive a not found error for the training type

  Scenario: Training types are read-only
    When I try to create a new training type named "New Type"
    Then the operation fails with an unsupported operation error

  Scenario: Training types cannot be deleted
    When I try to delete the training type with ID "1"
    Then the operation fails with an unsupported operation error
```