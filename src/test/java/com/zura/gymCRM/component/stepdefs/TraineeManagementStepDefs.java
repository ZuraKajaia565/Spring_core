package com.zura.gymCRM.component.stepdefs;

import static org.junit.jupiter.api.Assertions.*;

import com.zura.gymCRM.dto.TraineeProfileResponse;
import com.zura.gymCRM.dto.TraineeRegistrationRequest;
import com.zura.gymCRM.dto.TraineeRegistrationResponse;
import com.zura.gymCRM.dto.UpdateTraineeRequest;
import com.zura.gymCRM.entities.Trainee;
import com.zura.gymCRM.entities.User;
import com.zura.gymCRM.exceptions.NotFoundException;
import com.zura.gymCRM.facade.GymFacade;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import java.util.Date;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest
@ActiveProfiles("test")
public class TraineeManagementStepDefs {

  @Autowired private GymFacade gymFacade;

  private TraineeRegistrationRequest registrationRequest;
  private TraineeRegistrationResponse registrationResponse;
  private TraineeProfileResponse profileResponse;
  private UpdateTraineeRequest updateRequest;
  private String testUsername;
  private Exception thrownException;

  @Before
  public void setUp() {
    registrationRequest = null;
    registrationResponse = null;
    profileResponse = null;
    updateRequest = null;
    testUsername = null;
    thrownException = null;
  }

  @When("I register a new trainee with first name {string} and last name " +
        "{string}")
  public void
  iRegisterANewTraineeWithFirstNameAndLastName(String firstName,
                                               String lastName) {
    registrationRequest = new TraineeRegistrationRequest(
        firstName, lastName, new Date(), "123 Main St");
    try {
      Trainee createdTrainee = gymFacade.addTrainee(firstName, lastName, true,
                                                    new Date(), "123 Main St");
      User user = createdTrainee.getUser();
      registrationResponse = new TraineeRegistrationResponse(
          user.getUsername(), user.getPassword());
      testUsername = user.getUsername();
    } catch (Exception e) {
      thrownException = e;
    }
  }

  @Then("the trainee is registered successfully")
  public void theTraineeIsRegisteredSuccessfully() {
    assertNull(thrownException,
               "No exception should be thrown during registration");
    assertNotNull(registrationResponse,
                  "Registration response should not be null");
  }

  @Then("the trainee has a valid username and password")
  public void theTraineeHasAValidUsernameAndPassword() {
    assertNotNull(registrationResponse.getUsername(),
                  "Username should not be null");
    assertNotNull(registrationResponse.getPassword(),
                  "Password should not be null");
    assertTrue(registrationResponse.getPassword().length() >= 10,
               "Password should be at least 10 characters");
  }

  @Given("a trainee with username {string} exists")
  public void aTraineeWithUsernameExists(String username) {
    testUsername = username;

    Optional<Trainee> existingTrainee =
        gymFacade.selectTraineeByusername(username);
    if (existingTrainee.isEmpty()) {
      // Create trainee if it doesn't exist
      String firstName = "John";
      String lastName = "Doe";
      Trainee createdTrainee = gymFacade.addTrainee(firstName, lastName, true,
                                                    new Date(), "123 Main St");

      // Override the username if needed (in a real system you'd need to handle
      // this differently)
      User user = createdTrainee.getUser();
      user.setUsername(username);
      gymFacade.updateTrainee(createdTrainee);
    }
  }

  @When("I request the trainee profile for username {string}")
  public void iRequestTheTraineeProfileForUsername(String username) {
    try {
      Optional<Trainee> trainee = gymFacade.selectTraineeByusername(username);
      if (trainee.isPresent()) {
        Trainee foundTrainee = trainee.get();
        profileResponse = new TraineeProfileResponse(
            foundTrainee.getUser().getUsername(),
            foundTrainee.getUser().getFirstName(),
            foundTrainee.getUser().getLastName(), foundTrainee.getDateOfBirth(),
            foundTrainee.getAddress(), foundTrainee.getUser().getIsActive(),
            null // Trainers list omitted for simplicity
        );
      } else {
        thrownException =
            new NotFoundException("Trainee not found: " + username);
      }
    } catch (Exception e) {
      thrownException = e;
    }
  }

  @Then("the trainee profile is returned successfully")
  public void theTraineeProfileIsReturnedSuccessfully() {
    assertNull(thrownException,
               "No exception should be thrown when getting profile");
    assertNotNull(profileResponse, "Profile response should not be null");
  }

  @Then("the trainee first name is {string} and last name is {string}")
  public void theTraineeFirstNameIsAndLastNameIs(String firstName,
                                                 String lastName) {
    assertEquals(firstName, profileResponse.getFirstName(),
                 "First name should match");
    assertEquals(lastName, profileResponse.getLastName(),
                 "Last name should match");
  }

  @When("I update the trainee with new address {string}")
  public void iUpdateTheTraineeWithNewAddress(String newAddress) {
    try {
      Optional<Trainee> traineeOpt =
          gymFacade.selectTraineeByusername(testUsername);
      if (traineeOpt.isPresent()) {
        Trainee trainee = traineeOpt.get();
        trainee.setAddress(newAddress);
        gymFacade.updateTrainee(trainee);
      } else {
        thrownException =
            new NotFoundException("Trainee not found: " + testUsername);
      }
    } catch (Exception e) {
      thrownException = e;
    }
  }

  @Then("the trainee is updated successfully")
  public void theTraineeIsUpdatedSuccessfully() {
    assertNull(thrownException, "No exception should be thrown during update");
  }

  @Then("the trainee address is {string}")
  public void theTraineeAddressIs(String expectedAddress) {
    Optional<Trainee> traineeOpt =
        gymFacade.selectTraineeByusername(testUsername);
    assertTrue(traineeOpt.isPresent(), "Trainee should exist");
    assertEquals(expectedAddress, traineeOpt.get().getAddress(),
                 "Address should be updated");
  }

  @When("I delete the trainee with username {string}")
  public void iDeleteTheTraineeWithUsername(String username) {
    try {
      gymFacade.deleteTraineeByUsername(username);
    } catch (Exception e) {
      thrownException = e;
    }
  }

  @Then("the trainee is deleted successfully")
  public void theTraineeIsDeletedSuccessfully() {
    assertNull(thrownException,
               "No exception should be thrown during deletion");
  }

  @Then("the trainee no longer exists in the system")
  public void theTraineeNoLongerExistsInTheSystem() {
    Optional<Trainee> traineeOpt =
        gymFacade.selectTraineeByusername(testUsername);
    assertTrue(traineeOpt.isEmpty(), "Trainee should not exist after deletion");
  }

  @Then("I receive a not found error for the trainee")
  public void iReceiveANotFoundErrorForTheTrainee() {
    assertNotNull(thrownException, "An exception should have been thrown");
    assertTrue(thrownException instanceof NotFoundException,
               "Exception should be NotFoundException but was " +
                   thrownException.getClass().getSimpleName());
  }
}
