package com.zura.gymCRM.component.stepdefs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zura.gymCRM.dto.TraineeRegistrationResponse;
import com.zura.gymCRM.dto.TrainerRegistrationResponse;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class CommonStepDefs {

    private static final Logger logger = LoggerFactory.getLogger(CommonStepDefs.class);

    @Autowired
    private StepDataContext stepDataContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Then("the system returns a not found error")
    public void theSystemReturnsANotFoundError() {
        int responseStatus = stepDataContext.getResponseStatus();
        logger.info("Checking if response status indicates not found: {}", responseStatus);
        assertTrue(responseStatus == 400 || responseStatus == 404,
                "HTTP Status should be an error code (400 or 404), but was: " + responseStatus);
    }

    @And("the system returns a valid username and password")
    public void theSystemReturnsAValidUsernameAndPassword() {
        try {
            String responseBody = stepDataContext.getMvcResult().getResponse().getContentAsString();

            // Try to deserialize as TraineeRegistrationResponse first
            try {
                TraineeRegistrationResponse traineeResponse = objectMapper.readValue(responseBody, TraineeRegistrationResponse.class);
                assertNotNull(traineeResponse.getUsername(), "Username should not be null");
                assertNotNull(traineeResponse.getPassword(), "Password should not be null");
                assertTrue(traineeResponse.getPassword().length() >= 10, "Password should be at least 10 characters");
                return;
            } catch (Exception e) {
                logger.debug("Not a TraineeRegistrationResponse, trying TrainerRegistrationResponse");
            }

            // If that fails, try TrainerRegistrationResponse
            TrainerRegistrationResponse trainerResponse = objectMapper.readValue(responseBody, TrainerRegistrationResponse.class);
            assertNotNull(trainerResponse.getUsername(), "Username should not be null");
            assertNotNull(trainerResponse.getPassword(), "Password should not be null");
            assertTrue(trainerResponse.getPassword().length() >= 10, "Password should be at least 10 characters");
        } catch (Exception e) {
            logger.error("Error validating username/password: {}", e.getMessage(), e);
            fail("Failed to validate username/password: " + e.getMessage());
        }
    }
}