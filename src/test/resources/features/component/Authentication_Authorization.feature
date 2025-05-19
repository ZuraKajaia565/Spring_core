```gherkin
Feature: Authentication and Authorization

  Scenario: Successful login
    Given a user with username "john.doe" and password "password123" exists
    When I login with username "john.doe" and password "password123"
    Then the login is successful
    And I receive a valid JWT token

  Scenario: Failed login with incorrect password
    Given a user with username "john.doe" and password "password123" exists
    When I login with username "john.doe" and password "wrongpassword"
    Then the login fails with an authentication error

  Scenario: Failed login with non-existent user
    When I login with username "nonexistent.user" and password "password123"
    Then the login fails with an authentication error

  Scenario: Change password successfully
    Given I am authenticated as "john.doe"
    When I change my password from "password123" to "newpassword123"
    Then the password is changed successfully
    And I can login with the new password

  Scenario: Failed password change with incorrect old password
    Given I am authenticated as "john.doe"
    When I change my password from "wrongoldpassword" to "newpassword123"
    Then the password change fails with an authentication error

  Scenario: Successful logout
    Given I am authenticated as "john.doe"
    When I logout
    Then the logout is successful
    And my token is blacklisted

  Scenario: Access protected resource with valid token
    Given I am authenticated as "john.doe"
    When I access a protected resource
    Then I can access the resource successfully

  Scenario: Access protected resource with invalid token
    Given I have an invalid authentication token
    When I access a protected resource
    Then access is denied with an authentication error

  Scenario: Access protected resource with blacklisted token
    Given I am authenticated as "john.doe"
    And I have logged out
    When I access a protected resource
    Then access is denied with an authentication error

  Scenario: Too many failed login attempts
    Given a user with username "john.doe" exists
    When I make 4 failed login attempts for "john.doe"
    Then my IP is blocked for login attempts
    And I cannot login even with correct credentials until the block expires
```