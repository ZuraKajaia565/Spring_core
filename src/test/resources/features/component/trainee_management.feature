Feature: Trainee Management

  Scenario: Register a new trainee
    When I register a new trainee with first name "John" and last name "Doe"
    Then the trainee is registered successfully
    And the trainee has a valid username and password

  Scenario: Get trainee profile
    Given a trainee with username "john.doe" exists
    When I request the trainee profile for username "john.doe"
    Then the trainee profile is returned successfully
    And the trainee first name is "John" and last name is "Doe"

  Scenario: Update trainee profile
    Given a trainee with username "john.doe" exists
    When I update the trainee with new address "123 New Street"
    Then the trainee is updated successfully
    And the trainee address is "123 New Street"

  Scenario: Delete trainee
    Given a trainee with username "john.doe" exists
    When I delete the trainee with username "john.doe"
    Then the trainee is deleted successfully
    And the trainee no longer exists in the system

  Scenario: Attempt to get non-existent trainee
    When I request the trainee profile for username "nonexistent.user"
    Then I receive a not found error for the trainee
