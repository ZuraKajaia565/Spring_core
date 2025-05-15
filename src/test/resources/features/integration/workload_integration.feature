Feature: Workload Service Integration

  Scenario: Adding a training updates trainer workload
    Given both GymCRM and Workload services are running
    And a trainer with username "trainer1" exists in GymCRM
    When a new training with duration 60 minutes is added for the trainer
    Then the workload service is notified about the new training
    And the trainer's workload is updated to 60 minutes

  Scenario: Updating a training updates trainer workload
    Given both GymCRM and Workload services are running
    And a trainer with username "trainer1" exists in GymCRM
    And the trainer has a training with 60 minutes
    When the training duration is updated to 90 minutes
    Then the workload service is notified about the updated training
    And the trainer's workload is updated to 90 minutes

  Scenario: Deleting a training updates trainer workload
    Given both GymCRM and Workload services are running
    And a trainer with username "trainer1" exists in GymCRM
    And the trainer has a training with 60 minutes
    When the training is deleted
    Then the workload service is notified about the deleted training
    And the trainer's workload is updated to 0 minutes
