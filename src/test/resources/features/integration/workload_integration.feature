Feature: Workload Service Integration

  Scenario: Adding a training updates trainer workload
    Given both GymCRM and Workload services are running
    And a trainer with username "jane.smith" exists in GymCRM
    When a new training with duration 60 minutes is added for the trainer
    Then the workload service is notified about the new training
    And the trainer's workload is updated to 60 minutes

  Scenario: Updating a training updates trainer workload
    Given both GymCRM and Workload services are running
    And a trainer with username "jane.smith" exists in GymCRM
    And the trainer has a training with 60 minutes
    When the training duration is updated to 90 minutes
    Then the workload service is notified about the updated training
    And the trainer's workload is updated to 90 minutes

  Scenario: Deleting a training updates trainer workload
    Given both GymCRM and Workload services are running
    And a trainer with username "jane.smith" exists in GymCRM
    And the trainer has a training with 60 minutes
    When the training is deleted
    Then the workload service is notified about the deleted training
    And the trainer's workload is updated to 0 minutes

  Scenario: Workload service is unavailable but training operations succeed
    Given the workload service is unavailable
    And a trainer with username "jane.smith" exists in GymCRM
    When a new training with duration 60 minutes is added for the trainer
    Then the training is created successfully in GymCRM
    And the message is queued for delivery to workload service
    And the system logs the workload service unavailability

  Scenario: Multiple trainings affect cumulative workload
    Given both GymCRM and Workload services are running
    And a trainer with username "jane.smith" exists in GymCRM
    When a training with duration 60 minutes is added for the trainer
    And another training with duration 45 minutes is added for the same trainer
    Then the workload service is notified about both trainings
    And the trainer's total workload is updated to 105 minutes

  Scenario: Workload notifications contain correct training information
    Given both GymCRM and Workload services are running
    And a trainer with username "jane.smith" exists in GymCRM
    When a training with the following details is added:
      | trainerUsername | jane.smith     |
      | trainingName    | Morning Workout|
      | trainingDate    | 2025-06-15     |
      | trainingDuration| 60             |
    Then the workload notification contains the correct:
      | trainerUsername | jane.smith     |
      | year            | 2025           |
      | month           | 6              |
      | duration        | 60             |