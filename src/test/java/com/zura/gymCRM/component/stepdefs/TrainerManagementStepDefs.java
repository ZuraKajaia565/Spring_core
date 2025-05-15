package com.zura.gymCRM.component.stepdefs;

import static org.junit.jupiter.api.Assertions.*;

import com.zura.gymCRM.dto.TrainerProfileResponse;
import com.zura.gymCRM.dto.TrainerRegistrationRequest;
import com.zura.gymCRM.dto.TrainerRegistrationResponse;
import com.zura.gymCRM.entities.Trainer;
import com.zura.gymCRM.entities.TrainingType;
import com.zura.gymCRM.entities.User;
import com.zura.gymCRM.exceptions.NotFoundException;
import com.zura.gymCRM.facade.GymFacade;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class TrainerManagementStepDefs {

  @Autowired private GymFacade gymFacade;

  private TrainerRegistrationRequest registrationRequest;
  private TrainerRegistrationResponse registrationResponse;
  private TrainerProfileResponse profileResponse;
  private String testUsername;
  private Exception thrownException;

  @Before
  public void setUp() {
    registrationRequest = null;
    registrationResponse = null;
    profileResponse = null;
    testUsername = null;
    thrownException = null;
  }

  @When("I register a new trainer with first name {string} and last name " +
        "{string} and specialization {string}")
  public void
  iRegisterANewTrainerWithFirstNameAndLastNameAndSpecialization(
      String firstName, String lastName, String specializationName) {
    try {
      // Find or create training type
      Optional<TrainingType> trainingTypeOpt =
          gymFacade.selectTrainingTypeByName(specializationName);
      TrainingType trainingType;

      if (trainingTypeOpt.isEmpty()) {
        throw new NotFoundException("Training type not found: " +
                                    specializationName);
      }

      trainingType = trainingTypeOpt.get();
      registrationRequest =
          new TrainerRegistrationRequest(firstName, lastName, trainingType);

      Trainer createdTrainer =
          gymFacade.addTrainer(firstName, lastName, true, trainingType);
      User user = createdTrainer.getUser();
      registrationResponse = new TrainerRegistrationResponse(
          user.getUsername(), user.getPassword());
      testUsername = user.getUsername();
    } catch (Exception e) {
      thrownException = e;
    }
  }

  @Then("the trainer is registered successfully")
  public void theTrainerIsRegisteredSuccessfully() {
    assertNull(thrownException,
               "No exception should be thrown during registration");
    assertNotNull(registrationResponse,
                  "Registration response should not be null");
  }

  @Then("the trainer has a valid username and password")
  public void theTrainerHasAValidUsernameAndPassword() {
    assertNotNull(registrationResponse.getUsername(),
                  "Username should not be null");
    assertNotNull(registrationResponse.getPassword(),
                  "Password should not be null");
    assertTrue(registrationResponse.getPassword().length() >= 10,
               "Password should be at least 10 characters");
  }

  @Given("a trainer with username {string} exists")
  public void aTrainerWithUsernameExists(String username) {
    testUsername = username;

    Optional<Trainer> existingTrainer =
        gymFacade.selectTrainerByUsername(username);
    if (existingTrainer.isEmpty()) {
      // Create trainer if it doesn't exist
      String firstName = "Jane";
      String lastName = "Smith";

      // Find Strength training type
      Optional<TrainingType> trainingTypeOpt =
          gymFacade.selectTrainingTypeByName("Strength");
      if (trainingTypeOpt.isEmpty()) {
        throw new NotFoundException("Training type 'Strength' not found");
      }

      Trainer createdTrainer = gymFacade.addTrainer(firstName, lastName, true,
                                                    trainingTypeOpt.get());

      // Override the username if needed (in a real system you'd need to handle
      // this differently)
      User user = createdTrainer.getUser();
      user.setUsername(username);
      gymFacade.updateTrainer(createdTrainer);
    }
  }

  @When("I request the trainer profile for username {string}")
  public void iRequestTheTrainerProfileForUsername(String username) {
    try {
      Optional<Trainer> trainerOpt =
          gymFacade.selectTrainerByUsername(username);
      if (trainerOpt.isPresent()) {
        Trainer trainer = trainerOpt.get();
        profileResponse = new TrainerProfileResponse(
            trainer.getUser().getFirstName(), trainer.getUser().getLastName(),
            trainer.getSpecialization().getTrainingTypeName(),
            trainer.getUser().getIsActive(),
            null // Trainees list omitted for simplicity
        );
      } else {
        thrownException =
            new NotFoundException("Trainer not found: " + username);
      }
    } catch (Exception e) {
      thrownException = e;
    }
  }

  @Then("the trainer profile is returned successfully")
  public void theTrainerProfileIsReturnedSuccessfully() {
    assertNull(thrownException,
               "No exception should be thrown when getting profile");
    assertNotNull(profileResponse, "Profile response should not be null");
  }

  @Then("the trainer first name is {string} and last name is {string}")
  public void theTrainerFirstNameIsAndLastNameIs(String firstName,
                                                 String lastName) {
    assertEquals(firstName, profileResponse.getFirstName(),
                 "First name should match");
    assertEquals(lastName, profileResponse.getLastName(),
                 "Last name should match");
  }

  @Then("the trainer specialization is {string}")
  public void theTrainerSpecializationIs(String specializationName) {
    assertEquals(specializationName, profileResponse.getSpecialization(),
                 "Specialization should match");
  }

  @When("I update the trainer active status to {string}")
  public void iUpdateTheTrainerActiveStatusTo(String activeStatus) {
    boolean isActive = Boolean.parseBoolean(activeStatus);

    try {
      Optional<Trainer> trainerOpt =
          gymFacade.selectTrainerByUsername(testUsername);
      if (trainerOpt.isPresent()) {
        Trainer trainer = trainerOpt.get();
        trainer.getUser().setIsActive(isActive);
        gymFacade.updateTrainer(trainer);
      } else {
        thrownException =
            new NotFoundException("Trainer not found: " + testUsername);
      }
    } catch (Exception e) {
      thrownException = e;
    }
  }

  @Then("the trainer is updated successfully")
  public void theTrainerIsUpdatedSuccessfully() {
    assertNull(thrownException, "No exception should be thrown during update");
  }

  @Then("the trainer status is inactive")
  public void theTrainerStatusIsInactive() {
    Optional<Trainer> trainerOpt =
        gymFacade.selectTrainerByUsername(testUsername);
    assertTrue(trainerOpt.isPresent(), "Trainer should exist");
    assertFalse(trainerOpt.get().getUser().getIsActive(),
                "Trainer should be inactive");
  }

  @Then("I receive a not found error for the trainer")
  public void iReceiveANotFoundErrorForTheTrainer() {
    assertNotNull(thrownException, "An exception should have been thrown");
    assertTrue(thrownException instanceof NotFoundException,
               "Exception should be NotFoundException but was " +
                   thrownException.getClass().getSimpleName());
  }
}
