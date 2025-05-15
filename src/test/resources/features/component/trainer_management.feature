Feature: Trainer Management

  Scenario: Register a new trainer
    When I register a new trainer with first name "Jane" and last name "Smith" and specialization "Strength"
    Then the trainer is registered successfully
    And the trainer has a valid username and password

  Scenario: Get trainer profile
    Given a trainer with username "jane.smith" exists
    When I request the trainer profile for username "jane.smith"
    Then the trainer profile is returned successfully
    And the trainer first name is "Jane" and last name is "Smith"
    And the trainer specialization is "Strength"

  Scenario: Update trainer profile
    Given a trainer with username "jane.smith" exists
    When I update the trainer active status to "false"
    Then the trainer is updated successfully
    And the trainer status is inactive

  Scenario: Attempt to get non-existent trainer
    When I request the trainer profile for username "nonexistent.trainer"
    Then I receive a not found error for the trainer
