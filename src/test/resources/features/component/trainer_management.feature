Feature: Trainer Registration and Profile Management

  Scenario: Register a new trainer with valid data
    Given a training type "Strength" exists in the system
    When I register a new trainer with the following details:
      | firstName     | Jane        |
      | lastName      | Smith       |
      | specialization| Strength    |
    Then the trainer is registered successfully
    And the system returns a valid username and password
    And the trainer is set as active by default

  Scenario: Get trainer profile with valid username
    Given a trainer with username "jane.smith" exists in the system
    When I request trainer profile information for "jane.smith"
    Then the system returns the trainer profile
    And the profile contains correct information:
      | firstName     | Jane        |
      | lastName      | Smith       |
      | specialization| Strength    |

  Scenario: Update trainer profile with valid data
    Given a trainer with username "jane.smith" exists in the system
    When I update trainer "jane.smith" with the following information:
      | firstName | Jane        |
      | lastName  | Johnson     |
      | isActive  | true        |
    Then the trainer profile is updated successfully
    And the system returns the updated profile
    And the profile contains the new information:
      | lastName  | Johnson     |

  Scenario: Deactivate a trainer
    Given a trainer with username "jane.smith" exists in the system
    And the trainer is active
    When I deactivate trainer "jane.smith"
    Then the trainer is deactivated successfully
    And the trainer status is set to inactive

  Scenario: Get trainer profile with invalid username
    When I request trainer profile information for "nonexistent.trainer"
    Then the system returns a not found error

  Scenario: Register a trainer with non-existent specialization
    When I register a new trainer with the following details:
      | firstName     | Jane        |
      | lastName      | Smith       |
      | specialization| NonExistent |
    Then the registration fails with an error message
    And the error indicates that the specialization does not exist