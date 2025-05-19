```gherkin
Feature: Training Management

  Scenario: Create a new training
    Given a trainee with username "john.doe" exists
    And a trainer with username "jane.smith" exists
    When I create a training with the following details:
      | trainee    | john.doe    |
      | trainer    | jane.smith  |
      | name       | Morning Workout |
      | date       | 2025-06-01  |
      | duration   | 60          |
    Then the training is created successfully
    And the training has the correct details

  Scenario: Update an existing training
    Given a training exists with id "{trainingId}"
    When I update the training with the following details:
      | name       | Updated Workout |
      | duration   | 90              |
    Then the training is updated successfully
    And the training has the new details

  Scenario: Delete a training
    Given a training exists with id "{trainingId}"
    When I delete the training
    Then the training is deleted successfully
    And the training no longer exists in the system

  Scenario: Get trainings for a trainee
    Given a trainee with username "john.doe" exists
    And the trainee has multiple trainings
    When I request the trainings for trainee "john.doe"
    Then the trainings are returned successfully
    And the list contains all trainings for the trainee

  Scenario: Get trainings for a trainer
    Given a trainer with username "jane.smith" exists
    And the trainer has multiple trainings
    When I request the trainings for trainer "jane.smith"
    Then the trainings are returned successfully
    And the list contains all trainings for the trainer

  Scenario: Create training with invalid duration
    Given a trainee with username "john.doe" exists
    And a trainer with username "jane.smith" exists
    When I create a training with an invalid duration of -10 minutes
    Then the training creation fails with a validation error

  Scenario: Update non-existent training
    When I try to update a training with id "999999"
    Then I receive a not found error for the training

  Scenario: Delete non-existent training
    When I try to delete a training with id "999999"
    Then I receive a not found error for the training
```