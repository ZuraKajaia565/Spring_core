Feature: Trainee Registration and Profile Management

  Scenario: Register a new trainee with valid data
    When I register a new trainee with the following details:
      | firstName | John       |
      | lastName  | Doe        |
      | address   | 123 Main St|
      | dateOfBirth| 1990-01-01|
    Then the trainee is registered successfully
    And the system returns a valid username and password
    And the trainee is set as active by default

  Scenario: Get trainee profile with valid username
    Given a trainee with username "john.doe" exists in the system
    When I request trainee profile information for "john.doe"
    Then the system returns the trainee profile
    And the profile contains correct personal information:
      | firstName | John       |
      | lastName  | Doe        |
      | address   | 123 Main St|
      | dateOfBirth| 1990-01-01|

  Scenario: Update trainee profile with valid data
    Given a trainee with username "john.doe" exists in the system
    When I update trainee "john.doe" with the following information:
      | firstName | John       |
      | lastName  | Smith      |
      | address   | 456 Oak Dr |
    Then the trainee profile is updated successfully
    And the system returns the updated profile
    And the profile contains the new information:
      | lastName  | Smith      |
      | address   | 456 Oak Dr |

  Scenario: Deactivate a trainee
    Given a trainee with username "john.doe" exists in the system
    And the trainee is active
    When I deactivate trainee "john.doe"
    Then the trainee is deactivated successfully
    And the trainee status is set to inactive

  Scenario: Delete a trainee
    Given a trainee with username "john.doe" exists in the system
    When I delete trainee "john.doe"
    Then the trainee is deleted successfully
    And the trainee no longer exists in the system

  Scenario: Get trainee profile with invalid username
    When I request trainee profile information for "nonexistent.user"
    Then the system returns a not found error

  Scenario: Update trainee profile with invalid username
    When I update trainee "nonexistent.user" with the following information:
      | firstName | John       |
      | lastName  | Smith      |
    Then the system returns a not found error