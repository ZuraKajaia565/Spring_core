package com.zura.gymCRM.component.stepdefs;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zura.gymCRM.component.helper.MockMvcAuthHelper;
import com.zura.gymCRM.dto.TraineeProfileResponse;
import com.zura.gymCRM.dto.TraineeRegistrationRequest;
import com.zura.gymCRM.dto.TraineeRegistrationResponse;
import com.zura.gymCRM.dto.UpdateTraineeRequest;
import com.zura.gymCRM.entities.Trainee;
import com.zura.gymCRM.entities.Training;
import com.zura.gymCRM.entities.User;
import com.zura.gymCRM.exceptions.NotFoundException;
import com.zura.gymCRM.facade.GymFacade;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@AutoConfigureMockMvc
public class TraineeManagementStepDefs {

    private static final Logger logger = LoggerFactory.getLogger(TraineeManagementStepDefs.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GymFacade gymFacade;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StepDataContext stepDataContext;

    @Autowired
    private MockMvcAuthHelper authHelper;

    private TraineeRegistrationRequest registrationRequest;
    private MvcResult mvcResult;
    private int responseStatus;
    private Exception lastException;
    private String authToken;
    private String testUserDateOfBirth = "1990-01-01";

    @Before
    public void setUp() {
        registrationRequest = null;
        mvcResult = null;
        responseStatus = 0;
        lastException = null;

        // Set up a default authentication for all tests
        authHelper.setUpSecurityContext("test-user", "USER");
        authToken = authHelper.createJwtToken("test-user", "USER");
    }

    @When("I register a new trainee with the following details:")
    public void iRegisterANewTraineeWithTheFollowingDetails(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);

        try {
            registrationRequest = new TraineeRegistrationRequest();
            registrationRequest.setFirstName(data.get("firstName"));
            registrationRequest.setLastName(data.get("lastName"));
            registrationRequest.setAddress(data.get("address"));

            if (data.containsKey("dateOfBirth") && data.get("dateOfBirth") != null) {
                try {
                    testUserDateOfBirth = data.get("dateOfBirth");
                    Date dateOfBirth = DATE_FORMAT.parse(testUserDateOfBirth);
                    registrationRequest.setDateOfBirth(dateOfBirth);
                } catch (ParseException e) {
                    logger.error("Error parsing date: {}", e.getMessage());
                    throw new RuntimeException("Error parsing date", e);
                }
            }

            String requestBody = objectMapper.writeValueAsString(registrationRequest);

            mvcResult = mockMvc.perform(
                            MockMvcRequestBuilders.post("/api/trainees")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(requestBody))
                    .andReturn();

            responseStatus = mvcResult.getResponse().getStatus();
            stepDataContext.setResponseStatus(responseStatus);
            stepDataContext.setMvcResult(mvcResult);
        } catch (Exception e) {
            logger.error("Error in registering trainee: {}", e.getMessage(), e);
            lastException = e;
            stepDataContext.setLastException(e);
            fail("Failed to register trainee: " + e.getMessage());
        }
    }

    @Then("the trainee is registered successfully")
    public void theTraineeIsRegisteredSuccessfully() {
        assertEquals(201, responseStatus, "HTTP Status should be 201 CREATED");
    }

    @And("the trainee is set as active by default")
    public void theTraineeIsSetAsActiveByDefault() {
        try {
            String responseBody = mvcResult.getResponse().getContentAsString();
            TraineeRegistrationResponse response = objectMapper.readValue(responseBody, TraineeRegistrationResponse.class);

            // Get the created trainee to check if it's active
            Optional<Trainee> traineeOpt = gymFacade.selectTraineeByusername(response.getUsername());
            assertTrue(traineeOpt.isPresent(), "Trainee should exist in the system");
            assertTrue(traineeOpt.get().getUser().getIsActive(), "Trainee should be active by default");
        } catch (Exception e) {
            logger.error("Error checking if trainee is active: {}", e.getMessage(), e);
            fail("Failed to check if trainee is active: " + e.getMessage());
        }
    }

    @Given("a trainee with username {string} exists in the system")
    public void aTraineeWithUsernameExistsInTheSystem(String username) {
        try {
            Optional<Trainee> traineeOpt = gymFacade.selectTraineeByusername(username);

            if (traineeOpt.isEmpty()) {
                // Create a trainee if it doesn't exist
                logger.info("Creating test trainee with username: {}", username);

                // Create a user for the trainee
                User user = new User();
                user.setFirstName("John");
                user.setLastName("Doe");
                user.setUsername(username);
                user.setPassword("password123");
                user.setIsActive(true);

                // Create the trainee
                Trainee trainee = new Trainee();
                trainee.setUser(user);

                // Parse the test date of birth
                try {
                    trainee.setDateOfBirth(DATE_FORMAT.parse(testUserDateOfBirth));
                } catch (ParseException e) {
                    trainee.setDateOfBirth(new Date()); // fallback to current date
                    logger.error("Error parsing test date of birth: {}", e.getMessage());
                }

                trainee.setAddress("123 Main St");

                // Save the trainee
                gymFacade.addTrainee(
                        user.getFirstName(),
                        user.getLastName(),
                        user.getIsActive(),
                        trainee.getDateOfBirth(),
                        trainee.getAddress()
                );

                // Verify it was created
                traineeOpt = gymFacade.selectTraineeByusername(username);
                if (traineeOpt.isEmpty()) {
                    fail("Failed to create test trainee: " + username);
                }
            }

            // Now ensure the trainee has the correct data
            Trainee trainee = traineeOpt.get();

            // Update dateOfBirth if needed
            try {
                Date expectedDob = DATE_FORMAT.parse(testUserDateOfBirth);
                if (trainee.getDateOfBirth() == null ||
                        !DATE_FORMAT.format(expectedDob).equals(DATE_FORMAT.format(trainee.getDateOfBirth()))) {

                    trainee.setDateOfBirth(expectedDob);
                    gymFacade.updateTrainee(trainee);
                    logger.info("Updated trainee date of birth to: {}", testUserDateOfBirth);
                }
            } catch (ParseException e) {
                logger.error("Error updating date of birth: {}", e.getMessage());
            }
        } catch (Exception e) {
            logger.error("Error in trainee setup: {}", e.getMessage(), e);
            fail("Failed to set up trainee: " + e.getMessage());
        }
    }

    @When("I request trainee profile information for {string}")
    public void iRequestTraineeProfileInformationFor(String username) {
        try {
            mvcResult = mockMvc.perform(
                            MockMvcRequestBuilders.get("/api/trainees/{username}", username)
                                    .header("Authorization", "Bearer " + authToken)
                                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn();

            responseStatus = mvcResult.getResponse().getStatus();
            stepDataContext.setResponseStatus(responseStatus);
            stepDataContext.setMvcResult(mvcResult);
        } catch (Exception e) {
            logger.error("Error requesting trainee profile: {}", e.getMessage(), e);
            lastException = e;
            stepDataContext.setLastException(e);
            fail("Failed to request trainee profile: " + e.getMessage());
        }
    }

    @Then("the system returns the trainee profile")
    public void theSystemReturnsTheTraineeProfile() {
        assertEquals(200, responseStatus, "HTTP Status should be 200 OK");
    }

    @And("the profile contains correct personal information:")
    public void theProfileContainsCorrectPersonalInformation(DataTable dataTable) {
        try {
            if (mvcResult == null) {
                mvcResult = stepDataContext.getMvcResult();
                if (mvcResult == null) {
                    fail("MvcResult is null, could not retrieve it from context");
                }
            }

            Map<String, String> expectedData = dataTable.asMap(String.class, String.class);

            String responseBody = mvcResult.getResponse().getContentAsString();
            TraineeProfileResponse profile = objectMapper.readValue(responseBody, TraineeProfileResponse.class);

            assertEquals(expectedData.get("firstName"), profile.getFirstName(), "First name should match");
            assertEquals(expectedData.get("lastName"), profile.getLastName(), "Last name should match");
            assertEquals(expectedData.get("address"), profile.getAddress(), "Address should match");

            // Optional date of birth check
            if (expectedData.containsKey("dateOfBirth") && expectedData.get("dateOfBirth") != null) {
                testUserDateOfBirth = expectedData.get("dateOfBirth");
                Date expectedDob = DATE_FORMAT.parse(testUserDateOfBirth);
                // Note: Due to time component differences, compare just the date parts
                String expectedDateStr = DATE_FORMAT.format(expectedDob);
                String actualDateStr = DATE_FORMAT.format(profile.getDateOfBirth());
                assertEquals(expectedDateStr, actualDateStr, "Date of birth should match");
            }
        } catch (Exception e) {
            logger.error("Error validating profile information: {}", e.getMessage(), e);
            fail("Failed to validate profile information: " + e.getMessage());
        }
    }

    @When("I update trainee {string} with the following information:")
    public void iUpdateTraineeWithTheFollowingInformation(String username, DataTable dataTable) {
        try {
            Map<String, String> data = dataTable.asMap(String.class, String.class);

            UpdateTraineeRequest updateRequest = new UpdateTraineeRequest();
            updateRequest.setFirstName(data.getOrDefault("firstName", "John"));
            updateRequest.setLastName(data.getOrDefault("lastName", "Doe"));
            updateRequest.setIsActive(Boolean.parseBoolean(data.getOrDefault("isActive", "true")));

            if (data.containsKey("address")) {
                updateRequest.setAddress(data.get("address"));
            }

            if (data.containsKey("dateOfBirth")) {
                try {
                    testUserDateOfBirth = data.get("dateOfBirth");
                    Date dateOfBirth = DATE_FORMAT.parse(testUserDateOfBirth);
                    updateRequest.setDateOfBirth(dateOfBirth);
                } catch (ParseException e) {
                    logger.error("Error parsing date: {}", e.getMessage());
                }
            }

            String requestBody = objectMapper.writeValueAsString(updateRequest);

            mvcResult = mockMvc.perform(
                            MockMvcRequestBuilders.put("/api/trainees/{username}", username)
                                    .header("Authorization", "Bearer " + authToken)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(requestBody))
                    .andReturn();

            responseStatus = mvcResult.getResponse().getStatus();
            stepDataContext.setResponseStatus(responseStatus);
            stepDataContext.setMvcResult(mvcResult);
        } catch (Exception e) {
            logger.error("Error updating trainee: {}", e.getMessage(), e);
            lastException = e;
            stepDataContext.setLastException(e);
            fail("Failed to update trainee: " + e.getMessage());
        }
    }

    @Then("the trainee profile is updated successfully")
    public void theTraineeProfileIsUpdatedSuccessfully() {
        assertEquals(200, responseStatus, "HTTP Status should be 200 OK");
    }

    @And("the profile contains the new information:")
    public void theProfileContainsTheNewInformation(DataTable dataTable) {
        try {
            if (mvcResult == null) {
                mvcResult = stepDataContext.getMvcResult();
                if (mvcResult == null) {
                    fail("MvcResult is null, cannot validate profile update");
                }
            }

            Map<String, String> expectedData = dataTable.asMap(String.class, String.class);

            String responseBody = mvcResult.getResponse().getContentAsString();
            logger.info("Response content: {}", responseBody);

            // Check if response body is empty or invalid
            if (responseBody == null || responseBody.isEmpty()) {
                fail("Response body is empty, cannot validate profile update");
            }

            TraineeProfileResponse profile = objectMapper.readValue(responseBody, TraineeProfileResponse.class);

            for (Map.Entry<String, String> entry : expectedData.entrySet()) {
                switch (entry.getKey()) {
                    case "firstName":
                        assertEquals(entry.getValue(), profile.getFirstName(), "First name should match");
                        break;
                    case "lastName":
                        assertEquals(entry.getValue(), profile.getLastName(), "Last name should match");
                        break;
                    case "address":
                        assertEquals(entry.getValue(), profile.getAddress(), "Address should match");
                        break;
                    // Add more fields as needed
                }
            }
        } catch (Exception e) {
            logger.error("Error validating updated profile: {}", e.getMessage(), e);
            fail("Failed to validate updated profile: " + e.getMessage());
        }
    }

    @Given("the trainee is active")
    public void theTraineeIsActive() {
        // Nothing to do, trainee is active by default
    }

    @When("I deactivate trainee {string}")
    public void iDeactivateTrainee(String username) {
        try {
            String requestBody = "{\"active\": false}";

            mvcResult = mockMvc.perform(
                            MockMvcRequestBuilders.patch("/api/trainees/{username}/status", username)
                                    .header("Authorization", "Bearer " + authToken)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(requestBody))
                    .andReturn();

            responseStatus = mvcResult.getResponse().getStatus();
            stepDataContext.setResponseStatus(responseStatus);
            stepDataContext.setMvcResult(mvcResult);
        } catch (Exception e) {
            logger.error("Error deactivating trainee: {}", e.getMessage(), e);
            lastException = e;
            stepDataContext.setLastException(e);
            fail("Failed to deactivate trainee: " + e.getMessage());
        }
    }

    @Then("the trainee is deactivated successfully")
    public void theTraineeIsDeactivatedSuccessfully() {
        assertEquals(200, responseStatus, "HTTP Status should be 200 OK");
    }

    @And("the trainee status is set to inactive")
    public void theTraineeStatusIsSetToInactive() {
        try {
            String responseBody = mvcResult.getResponse().getContentAsString();
            Map<String, Object> response = objectMapper.readValue(responseBody, Map.class);

            String username = (String) response.get("username");
            Optional<Trainee> traineeOpt = gymFacade.selectTraineeByusername(username);

            assertTrue(traineeOpt.isPresent(), "Trainee should exist");
            assertFalse(traineeOpt.get().getUser().getIsActive(), "Trainee should be inactive");
        } catch (Exception e) {
            logger.error("Error checking trainee status: {}", e.getMessage(), e);
            fail("Failed to check trainee status: " + e.getMessage());
        }
    }

    @When("I delete trainee {string}")
    public void iDeleteTrainee(String username) {
        try {
            // First, we need to delete all associated trainings to avoid foreign key constraint violations
            Optional<Trainee> traineeOpt = gymFacade.selectTraineeByusername(username);
            if (traineeOpt.isPresent()) {
                Trainee trainee = traineeOpt.get();
                // Get all trainings for this trainee and delete them
                List<Training> trainings = gymFacade.getTraineeTrainingsByCriteria(username, null, null, null, null);
                if (trainings != null && !trainings.isEmpty()) {
                    for (Training training : trainings) {
                        try {
                            // Use service to delete each training
                            gymFacade.deleteTraining(training.getId());
                        } catch (Exception e) {
                            logger.warn("Error deleting training: {}", e.getMessage());
                        }
                    }
                }
            }

            // Now try to delete the trainee
            mvcResult = mockMvc.perform(
                            MockMvcRequestBuilders.delete("/api/trainees/{username}", username)
                                    .header("Authorization", "Bearer " + authToken))
                    .andReturn();

            responseStatus = mvcResult.getResponse().getStatus();
            stepDataContext.setResponseStatus(responseStatus);
            stepDataContext.setMvcResult(mvcResult);
        } catch (Exception e) {
            logger.error("Error deleting trainee: {}", e.getMessage(), e);
            lastException = e;
            stepDataContext.setLastException(e);
            fail("Failed to delete trainee: " + e.getMessage());
        }
    }

    @Then("the trainee is deleted successfully")
    public void theTraineeIsDeletedSuccessfully() {
        assertEquals(200, responseStatus, "HTTP Status should be 200 OK");
    }

    @And("the trainee no longer exists in the system")
    public void theTraineeNoLongerExistsInTheSystem() {
        try {
            String responseBody = mvcResult.getResponse().getContentAsString();
            Map<String, String> response = objectMapper.readValue(responseBody, Map.class);

            // Get the deleted username from the previous step
            Optional<Trainee> traineeOpt = gymFacade.selectTraineeByusername("john.doe"); // Use the username from the test

            assertTrue(traineeOpt.isEmpty(), "Trainee should not exist after deletion");
        } catch (Exception e) {
            logger.error("Error checking if trainee exists: {}", e.getMessage(), e);

            // This might be expected if the trainee was successfully deleted
            if (e instanceof NotFoundException) {
                logger.info("Trainee not found, as expected after deletion");
            } else {
                fail("Failed to check if trainee exists: " + e.getMessage());
            }
        }
    }
}