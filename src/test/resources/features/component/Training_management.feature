Feature: Training Management

  Scenario: Create a new training with valid data
    Given a trainee with username "john.doe" exists in the system
    And a trainer with username "jane.smith" exists in the system
    When I create a training with the following details:
      | traineeName    | john.doe       |
      | trainerName    | jane.smith     |
      | trainingName   | Morning Workout|
      | trainingDate   | 2025-06-15     |
      | trainingDuration | 60           |
    Then the training is created successfully
    And the system returns the created training details
    And the training has the correct information

  Scenario: Update an existing training
    Given a training with id "1" exists in the system
    When I update the training with the following details:
      | trainingName   | Evening Workout|
      | trainingDate   | 2025-06-16     |
      | trainingDuration | 90           |
    Then the training is updated successfully
    And the system returns the updated training details
    And the training has the new information

  Scenario: Delete a training
    Given a training with id "1" exists in the system
    When I delete the training
    Then the training is deleted successfully
    And the training no longer exists in the system

  Scenario: Get trainings for a trainee
    Given a trainee with username "john.doe" exists in the system
    And the trainee has the following trainings:
      | trainingName   | trainingDate | trainingDuration |
      | Morning Workout| 2025-06-15   | 60               |
      | Evening Workout| 2025-06-16   | 90               |
    When I request trainings for trainee "john.doe"
    Then the system returns the list of trainings
    And the list contains all trainings for the trainee

  Scenario: Get trainings for a trainer
    Given a trainer with username "jane.smith" exists in the system
    And the trainer has the following trainings:
      | trainingName   | trainingDate | trainingDuration |
      | Morning Workout| 2025-06-15   | 60               |
      | Evening Workout| 2025-06-16   | 90               |
    When I request trainings for trainer "jane.smith"
    Then the system returns the list of trainings
    And the list contains all trainings for the trainer

  Scenario: Get trainings with date filter
    Given a trainee with username "john.doe" exists in the system
    And the trainee has the following trainings:
      | trainingName   | trainingDate | trainingDuration |
      | Morning Workout| 2025-06-15   | 60               |
      | Evening Workout| 2025-07-16   | 90               |
    When I request trainings for trainee "john.doe" between "2025-06-01" and "2025-06-30"
    Then the system returns the list of trainings
    And the list contains only trainings within the date range

  Scenario: Create training with non-existent trainee
    Given a trainer with username "jane.smith" exists in the system
    When I create a training with the following details:
      | traineeName    | nonexistent.user |
      | trainerName    | jane.smith     |
      | trainingName   | Morning Workout|
      | trainingDate   | 2025-06-15     |
      | trainingDuration | 60           |
    Then the training creation fails with an error
    And the error indicates that the trainee does not exist

  Scenario: Create training with non-existent trainer
    Given a trainee with username "john.doe" exists in the system
    When I create a training with the following details:
      | traineeName    | john.doe       |
      | trainerName    | nonexistent.trainer |
      | trainingName   | Morning Workout|
      | trainingDate   | 2025-06-15     |
      | trainingDuration | 60           |
    Then the training creation fails with an error
    And the error indicates that the trainer does not exist