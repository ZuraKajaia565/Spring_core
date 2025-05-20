package com.zura.gymCRM.component.stepdefs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zura.gymCRM.component.helper.MockMvcAuthHelper;
import com.zura.gymCRM.dto.TrainerProfileResponse;
import com.zura.gymCRM.dto.TrainerRegistrationRequest;
import com.zura.gymCRM.dto.TrainerRegistrationResponse;
import com.zura.gymCRM.dto.UpdateTrainerRequest;
import com.zura.gymCRM.entities.Trainer;
import com.zura.gymCRM.entities.TrainingType;
import com.zura.gymCRM.entities.User;
import com.zura.gymCRM.facade.GymFacade;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;

public class TrainerManagementStepDefs {

  private static final Logger logger = LoggerFactory.getLogger(TrainerManagementStepDefs.class);

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private GymFacade gymFacade;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvcAuthHelper authHelper;

  private TrainingType trainingType;
  private TrainerRegistrationRequest registrationRequest;
  private MvcResult mvcResult;
  private int responseStatus;
  private String authToken;
  private Exception lastException;

  @Before
  public void setUp() {
    trainingType = null;
    registrationRequest = null;
    mvcResult = null;
    responseStatus = 0;
    lastException = null;

    // Set up a default authentication for all tests
    authHelper.setUpSecurityContext("test-user", "USER");
    authToken = authHelper.createJwtToken("test-user", "USER");
  }

  @Given("a training type {string} exists in the system")
  public void aTrainingTypeExistsInTheSystem(String trainingTypeName) {
    Optional<TrainingType> trainingTypeOpt = gymFacade.selectTrainingTypeByName(trainingTypeName);

    assertTrue(trainingTypeOpt.isPresent(), "Training type should exist: " + trainingTypeName);
    trainingType = trainingTypeOpt.get();
  }

  @When("I register a new trainer with the following details:")
  public void iRegisterANewTrainerWithTheFollowingDetails(DataTable dataTable) {
    try {
      Map<String, String> data = dataTable.asMap(String.class, String.class);

      // Get the training type by name
      String specializationName = data.get("specialization");
      Optional<TrainingType> trainingTypeOpt = gymFacade.selectTrainingTypeByName(specializationName);

      if (!trainingTypeOpt.isPresent()) {
        throw new RuntimeException("Training type not found: " + specializationName);
      }

      registrationRequest = new TrainerRegistrationRequest();
      registrationRequest.setFirstName(data.get("firstName"));
      registrationRequest.setLastName(data.get("lastName"));
      registrationRequest.setSpecialization(trainingTypeOpt.get());

      String requestBody = objectMapper.writeValueAsString(registrationRequest);

      mvcResult = mockMvc.perform(
                      MockMvcRequestBuilders.post("/api/trainers")
                              .header("Authorization", "Bearer " + authToken)
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(requestBody))
              .andReturn();

      responseStatus = mvcResult.getResponse().getStatus();
    } catch (Exception e) {
      logger.error("Error registering trainer: {}", e.getMessage(), e);
      lastException = e;
    }
  }

  @Then("the trainer is registered successfully")
  public void theTrainerIsRegisteredSuccessfully() {
    assertEquals(201, responseStatus, "HTTP Status should be 201 CREATED");
  }

  @Given("a trainer with username {string} exists in the system")
  public void aTrainerWithUsernameExistsInTheSystem(String username) {
    try {
      Optional<Trainer> trainerOpt = gymFacade.selectTrainerByUsername(username);

      if (trainerOpt.isEmpty()) {
        // Create a trainer if it doesn't exist
        // First get a training type
        Optional<TrainingType> trainingTypeOpt = gymFacade.selectTrainingTypeByName("Strength");

        if (!trainingTypeOpt.isPresent()) {
          throw new RuntimeException("Training type 'Strength' not found in database");
        }

        User user = new User();
        user.setFirstName("Jane");
        user.setLastName("Smith");
        user.setUsername(username); // Set explicit username for test
        user.setPassword("password123");
        user.setIsActive(true);

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(trainingTypeOpt.get());

        try {
          // Try to update or create the trainer
          Trainer savedTrainer = gymFacade.addTrainer(
                  user.getFirstName(),
                  user.getLastName(),
                  user.getIsActive(),
                  trainingTypeOpt.get()
          );

          // Set the username to the required value for testing
          savedTrainer.getUser().setUsername(username);
          gymFacade.updateTrainer(savedTrainer);
        } catch (Exception e) {
          logger.error("Error creating trainer: {}", e.getMessage(), e);
          lastException = e;
        }
      }

      // Verify the trainer exists
      trainerOpt = gymFacade.selectTrainerByUsername(username);
      if (!trainerOpt.isPresent()) {
        logger.warn("Trainer still doesn't exist after creation attempt: {}", username);
      }
    } catch (Exception e) {
      logger.error("Error setting up trainer: {}", e.getMessage(), e);
      lastException = e;
    }
  }

  @When("I request trainer profile information for {string}")
  public void iRequestTrainerProfileInformationFor(String username) {
    try {
      mvcResult = mockMvc.perform(
                      MockMvcRequestBuilders.get("/api/trainers/{username}", username)
                              .header("Authorization", "Bearer " + authToken)
                              .accept(MediaType.APPLICATION_JSON))
              .andReturn();

      responseStatus = mvcResult.getResponse().getStatus();
    } catch (Exception e) {
      logger.error("Error requesting trainer profile: {}", e.getMessage(), e);
      lastException = e;
    }
  }

  @Then("the system returns the trainer profile")
  public void theSystemReturnsTheTrainerProfile() {
    assertEquals(200, responseStatus, "HTTP Status should be 200 OK");
  }

  @And("the profile contains correct information:")
  public void theProfileContainsCorrectInformation(DataTable dataTable) {
    try {
      Map<String, String> expectedData = dataTable.asMap(String.class, String.class);

      String responseBody = mvcResult.getResponse().getContentAsString();
      TrainerProfileResponse profile = objectMapper.readValue(responseBody, TrainerProfileResponse.class);

      assertEquals(expectedData.get("firstName"), profile.getFirstName(), "First name should match");
      assertEquals(expectedData.get("lastName"), profile.getLastName(), "Last name should match");

      if (expectedData.containsKey("specialization")) {
        assertEquals(expectedData.get("specialization"), profile.getSpecialization(), "Specialization should match");
      }
    } catch (Exception e) {
      logger.error("Error verifying profile information: {}", e.getMessage(), e);
      lastException = e;
      fail("Failed to verify profile information: " + e.getMessage());
    }
  }

  @When("I update trainer {string} with the following information:")
  public void iUpdateTrainerWithTheFollowingInformation(String username, DataTable dataTable) {
    try {
      Map<String, String> data = dataTable.asMap(String.class, String.class);

      UpdateTrainerRequest updateRequest = new UpdateTrainerRequest();
      updateRequest.setFirstName(data.getOrDefault("firstName", "Jane"));
      updateRequest.setLastName(data.getOrDefault("lastName", "Smith"));

      // Use existing specialization if not specified
      String specializationName = data.getOrDefault("specialization", "Strength");
      updateRequest.setSpecialization(specializationName);

      updateRequest.setIsActive(Boolean.parseBoolean(data.getOrDefault("isActive", "true")));

      String requestBody = objectMapper.writeValueAsString(updateRequest);

      mvcResult = mockMvc.perform(
                      MockMvcRequestBuilders.put("/api/trainers/{username}", username)
                              .header("Authorization", "Bearer " + authToken)
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(requestBody))
              .andReturn();

      responseStatus = mvcResult.getResponse().getStatus();
    } catch (Exception e) {
      logger.error("Error updating trainer: {}", e.getMessage(), e);
      lastException = e;
    }
  }

  @Then("the trainer profile is updated successfully")
  public void theTrainerProfileIsUpdatedSuccessfully() {
    assertEquals(200, responseStatus, "HTTP Status should be 200 OK");
  }

  @Given("the trainer is active")
  public void theTrainerIsActive() {
    // Nothing to do, trainer is active by default
  }

  @When("I deactivate trainer {string}")
  public void iDeactivateTrainer(String username) {
    try {
      String requestBody = "{\"active\": false}";

      mvcResult = mockMvc.perform(
                      MockMvcRequestBuilders.patch("/api/trainers/{username}/status", username)
                              .header("Authorization", "Bearer " + authToken)
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(requestBody))
              .andReturn();

      responseStatus = mvcResult.getResponse().getStatus();
    } catch (Exception e) {
      logger.error("Error deactivating trainer: {}", e.getMessage(), e);
      lastException = e;
    }
  }

  @Then("the trainer is deactivated successfully")
  public void theTrainerIsDeactivatedSuccessfully() {
    assertEquals(200, responseStatus, "HTTP Status should be 200 OK");
  }

  @And("the trainer status is set to inactive")
  public void theTrainerStatusIsSetToInactive() {
    try {
      String responseBody = mvcResult.getResponse().getContentAsString();
      Map<String, Object> response = objectMapper.readValue(responseBody, Map.class);

      String username = (String) response.get("username");
      Optional<Trainer> trainerOpt = gymFacade.selectTrainerByUsername(username);

      assertTrue(trainerOpt.isPresent(), "Trainer should exist");
      assertFalse(trainerOpt.get().getUser().getIsActive(), "Trainer should be inactive");
    } catch (Exception e) {
      logger.error("Error checking trainer status: {}", e.getMessage(), e);
      lastException = e;
      fail("Failed to check trainer status: " + e.getMessage());
    }
  }

  @Then("the registration fails with an error message")
  public void theRegistrationFailsWithAnErrorMessage() {
    assertNotEquals(201, responseStatus, "HTTP Status should not be 201 CREATED");
    assertTrue(responseStatus >= 400, "HTTP Status should be an error code");
  }

  @And("the error indicates that the specialization does not exist")
  public void theErrorIndicatesThatTheSpecializationDoesNotExist() {
    try {
      String responseBody = mvcResult.getResponse().getContentAsString();
      assertTrue(responseBody.contains("specialization") || responseBody.contains("Invalid specialization"),
              "Error should mention specialization issue: " + responseBody);
    } catch (Exception e) {
      logger.error("Error checking error message: {}", e.getMessage(), e);
      lastException = e;
      fail("Failed to check error message: " + e.getMessage());
    }
  }

  @Then("the system returns a not found error")
  public void theSystemReturnsANotFoundError() {
    assertTrue(responseStatus == 400 || responseStatus == 404,
            "HTTP Status should be 400 or 404, but was: " + responseStatus);
  }
}