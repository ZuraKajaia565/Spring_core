package com.zura.gymCRM.integration.stepdefs;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.zura.gymCRM.client.WorkloadServiceClient;
import com.zura.gymCRM.dto.TrainerWorkloadResponse;
import com.zura.gymCRM.dto.WorkloadRequest;
import com.zura.gymCRM.entities.Trainee;
import com.zura.gymCRM.entities.Trainer;
import com.zura.gymCRM.entities.Training;
import com.zura.gymCRM.entities.TrainingType;
import com.zura.gymCRM.entities.User;
import com.zura.gymCRM.facade.GymFacade;
import com.zura.gymCRM.messaging.WorkloadMessage;
import com.zura.gymCRM.messaging.WorkloadMessageProducer;
import com.zura.gymCRM.service.TrainingService;
import com.zura.gymCRM.service.WorkloadNotificationService;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest
@ActiveProfiles("test")
public class WorkloadIntegrationStepDefs {

  private static final Logger logger =
      LoggerFactory.getLogger(WorkloadIntegrationStepDefs.class);

  @Autowired private GymFacade gymFacade;

  @Autowired private TrainingService trainingService;

  @SpyBean private WorkloadNotificationService workloadNotificationService;

  @MockBean private WorkloadServiceClient workloadServiceClient;

  @MockBean private WorkloadMessageProducer workloadMessageProducer;

  @Captor private ArgumentCaptor<Training> trainingCaptor;

  @Captor private ArgumentCaptor<WorkloadMessage> messageCaptor;

  private String testTrainerUsername;
  private String testTraineeUsername;
  private Long testTrainingId;
  private Training testTraining;
  private int testDuration;
  private Exception thrownException;

  @Before
  public void setUp() {
    // Reset state
    testTrainerUsername = "trainer1";
    testTraineeUsername = "trainee1";
    testTrainingId = null;
    testTraining = null;
    testDuration = 0;
    thrownException = null;

    // Mock workload service responses
    ResponseEntity<Void> okResponse = ResponseEntity.ok().build();
    when(workloadServiceClient.updateWorkload(anyString(), anyInt(), anyInt(),
                                              any(WorkloadRequest.class),
                                              anyString()))
        .thenReturn(okResponse);
    when(workloadServiceClient.deleteWorkload(anyString(), anyInt(), anyInt(),
                                              anyString()))
        .thenReturn(okResponse);
    when(workloadServiceClient.addWorkload(anyString(), anyInt(), anyInt(),
                                           anyInt(), anyString()))
        .thenReturn(okResponse);

    // Reset mocks to clear any previous interactions
    reset(workloadNotificationService);
    reset(workloadMessageProducer);
    reset(workloadServiceClient);

    // Mock successful message delivery
    doNothing()
        .when(workloadMessageProducer)
        .sendWorkloadMessage(any(WorkloadMessage.class));
  }

  @After
  public void tearDown() {
    // Clean up any test data if needed
    if (testTrainingId != null) {
      try {
        trainingService.deleteTraining(testTrainingId);
      } catch (Exception e) {
        // Ignore exceptions during cleanup
        logger.warn("Exception during test cleanup: {}", e.getMessage());
      }
    }
  }

  @Given("both GymCRM and Workload services are running")
  public void bothGymCRMAndWorkloadServicesAreRunning() {
    // This is a mock test, so we'll just verify our mocks are set up
    assertNotNull(workloadServiceClient,
                  "Workload service client should be injected");
    assertNotNull(workloadMessageProducer,
                  "Workload message producer should be injected");
  }

  @Given("a trainer with username {string} exists in GymCRM")
  public void aTrainerWithUsernameExistsInGymCRM(String username) {
    testTrainerUsername = username;

    try {
      // Check if trainer exists
      Optional<Trainer> existingTrainer =
          gymFacade.selectTrainerByUsername(username);
      if (existingTrainer.isEmpty()) {
        // Create a new trainer
        Optional<TrainingType> trainingTypeOpt =
            gymFacade.selectTrainingTypeByName("Strength");
        if (trainingTypeOpt.isEmpty()) {
          throw new RuntimeException(
              "Training type 'Strength' not found in database");
        }

        TrainingType trainingType = trainingTypeOpt.get();
        Trainer trainer =
            gymFacade.addTrainer("Test", "Trainer", true, trainingType);

        // Set the username explicitly for testing
        User user = trainer.getUser();
        user.setUsername(username);
        gymFacade.updateTrainer(trainer);
      }

      // Also ensure a trainee exists for creating trainings
      Optional<Trainee> existingTrainee =
          gymFacade.selectTraineeByusername(testTraineeUsername);
      if (existingTrainee.isEmpty()) {
        Trainee trainee = gymFacade.addTrainee("Test", "Trainee", true,
                                               new Date(), "123 Main St");
        User user = trainee.getUser();
        user.setUsername(testTraineeUsername);
        gymFacade.updateTrainee(trainee);
      }
    } catch (Exception e) {
      thrownException = e;
      logger.error("Error setting up trainer/trainee: {}", e.getMessage(), e);
      fail("Setup failed: " + e.getMessage());
    }
  }

  @When("a new training with duration {int} minutes is added for the trainer")
  public void
  aNewTrainingWithDurationMinutesIsAddedForTheTrainer(int duration) {
    testDuration = duration;

    try {
      // Get trainer and trainee
      Optional<Trainer> trainerOpt =
          gymFacade.selectTrainerByUsername(testTrainerUsername);
      Optional<Trainee> traineeOpt =
          gymFacade.selectTraineeByusername(testTraineeUsername);

      assertTrue(trainerOpt.isPresent(), "Trainer should exist");
      assertTrue(traineeOpt.isPresent(), "Trainee should exist");

      Trainer trainer = trainerOpt.get();
      Trainee trainee = traineeOpt.get();

      // Create a training
      testTraining = gymFacade.addTraining(trainee, trainer, "Test Training",
                                           trainer.getSpecialization(),
                                           new Date(), duration);

      testTrainingId = testTraining.getId();
    } catch (Exception e) {
      thrownException = e;
      logger.error("Error adding training: {}", e.getMessage(), e);
      fail("Failed to add training: " + e.getMessage());
    }
  }

  @Then("the workload service is notified about the new training")
  public void theWorkloadServiceIsNotifiedAboutTheNewTraining() {
    // Verify the WorkloadNotificationService was called
    verify(workloadNotificationService)
        .notifyTrainingCreated(trainingCaptor.capture());

    // Verify the captured training matches our test training
    Training capturedTraining = trainingCaptor.getValue();
    assertEquals(
        testTrainingId, capturedTraining.getId(),
        "The correct training should be passed to notification service");
    assertEquals(testDuration, capturedTraining.getTrainingDuration(),
                 "Training duration should match");

    // We might also verify the message producer was called with the correct
    // message
    verify(workloadMessageProducer, atLeastOnce())
        .sendWorkloadMessage(messageCaptor.capture());

    // At least one of the captured messages should have the correct data
    boolean foundMatchingMessage = false;
    for (WorkloadMessage capturedMessage : messageCaptor.getAllValues()) {
      if (capturedMessage.getUsername().equals(testTrainerUsername) &&
          capturedMessage.getTrainingDuration() == testDuration) {
        foundMatchingMessage = true;
        break;
      }
    }

    assertTrue(
        foundMatchingMessage,
        "A message with the correct trainer and duration should be sent");
  }

  @Then("the trainer's workload is updated to {int} minutes")
  public void theTrainerSWorkloadIsUpdatedToMinutes(int expectedDuration) {
    // Since we've mocked the workload service client, we can verify it was
    // called correctly Extract year and month from the training date
    LocalDate trainingDate = testTraining.getTrainingDate()
                                 .toInstant()
                                 .atZone(ZoneId.systemDefault())
                                 .toLocalDate();

    int year = trainingDate.getYear();
    int month = trainingDate.getMonthValue();

    // Verify the workload client was called - either through direct API or via
    // message
    verify(workloadMessageProducer, atLeastOnce())
        .sendWorkloadMessage(argThat(
            message
            -> message.getUsername().equals(testTrainerUsername) &&
                   message.getYear() == year && message.getMonth() == month &&
                   message.getTrainingDuration() == expectedDuration));

    // Or verify the client was called directly
    verify(workloadServiceClient, atLeastOnce())
        .updateWorkload(eq(testTrainerUsername), eq(year), eq(month),
                        any(WorkloadRequest.class), anyString());
  }

  @Given("the trainer has a training with {int} minutes")
  public void theTrainerHasATrainingWithMinutes(int duration) {
    // First, create the training
    aNewTrainingWithDurationMinutesIsAddedForTheTrainer(duration);

    // Reset the mocks so we can verify future interactions
    reset(workloadNotificationService);
    reset(workloadMessageProducer);
    reset(workloadServiceClient);

    // Re-establish mock behavior
    ResponseEntity<Void> okResponse = ResponseEntity.ok().build();
    when(workloadServiceClient.updateWorkload(anyString(), anyInt(), anyInt(),
                                              any(WorkloadRequest.class),
                                              anyString()))
        .thenReturn(okResponse);
    when(workloadServiceClient.deleteWorkload(anyString(), anyInt(), anyInt(),
                                              anyString()))
        .thenReturn(okResponse);

    doNothing()
        .when(workloadMessageProducer)
        .sendWorkloadMessage(any(WorkloadMessage.class));
  }

  @When("the training duration is updated to {int} minutes")
  public void theTrainingDurationIsUpdatedToMinutes(int newDuration) {
    try {
      testDuration = newDuration;

      // Get the training
      Optional<Training> trainingOpt =
          trainingService.getTraining(testTrainingId);
      assertTrue(trainingOpt.isPresent(), "Training should exist");

      // Update the training
      Training training = trainingOpt.get();
      training.setTrainingDuration(newDuration);
      testTraining = trainingService.updateTraining(training);
    } catch (Exception e) {
      thrownException = e;
      logger.error("Error updating training: {}", e.getMessage(), e);
      fail("Failed to update training: " + e.getMessage());
    }
  }

  @Then("the workload service is notified about the updated training")
  public void theWorkloadServiceIsNotifiedAboutTheUpdatedTraining() {
    // Verify the notification service was called
    verify(workloadNotificationService)
        .notifyTrainingUpdated(trainingCaptor.capture());

    // Verify the captured training matches our test training
    Training capturedTraining = trainingCaptor.getValue();
    assertEquals(
        testTrainingId, capturedTraining.getId(),
        "The correct training should be passed to notification service");
    assertEquals(testDuration, capturedTraining.getTrainingDuration(),
                 "Updated training duration should match");

    // Also verify message producer was called
    verify(workloadMessageProducer, atLeastOnce())
        .sendWorkloadMessage(messageCaptor.capture());

    // One of the captured messages should have the correct data
    boolean foundMatchingMessage = false;
    for (WorkloadMessage capturedMessage : messageCaptor.getAllValues()) {
      if (capturedMessage.getUsername().equals(testTrainerUsername) &&
          capturedMessage.getTrainingDuration() == testDuration &&
          capturedMessage.getMessageType() ==
              WorkloadMessage.MessageType.CREATE_UPDATE) {
        foundMatchingMessage = true;
        break;
      }
    }

    assertTrue(foundMatchingMessage, "A message with the correct trainer and " +
                                     "updated duration should be sent");
  }

  @When("the training is deleted")
  public void theTrainingIsDeleted() {
    try {
      // Delete the training
      trainingService.deleteTraining(testTrainingId);
    } catch (Exception e) {
      thrownException = e;
      logger.error("Error deleting training: {}", e.getMessage(), e);
      fail("Failed to delete training: " + e.getMessage());
    }
  }

  @Then("the workload service is notified about the deleted training")
  public void theWorkloadServiceIsNotifiedAboutTheDeletedTraining() {
    // Verify the notification service was called
    verify(workloadNotificationService)
        .notifyTrainingDeleted(any(Training.class));

    // Also verify message producer was called with DELETE message type
    verify(workloadMessageProducer, atLeastOnce())
        .sendWorkloadMessage(
            argThat(message
                    -> message.getUsername().equals(testTrainerUsername) &&
                           message.getMessageType() ==
                               WorkloadMessage.MessageType.DELETE));

    // Or verify the client was called directly with delete
    LocalDate trainingDate = testTraining.getTrainingDate()
                                 .toInstant()
                                 .atZone(ZoneId.systemDefault())
                                 .toLocalDate();

    int year = trainingDate.getYear();
    int month = trainingDate.getMonthValue();

    verify(workloadServiceClient, atLeastOnce())
        .deleteWorkload(eq(testTrainerUsername), eq(year), eq(month),
                        anyString());
  }
}
