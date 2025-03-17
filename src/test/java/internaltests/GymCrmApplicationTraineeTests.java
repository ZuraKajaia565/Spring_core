package internaltests;
/*
import static org.junit.jupiter.api.Assertions.*;

import com.zura.gymCRM.dao.TrainingTypeRepository;
import com.zura.gymCRM.entities.Trainee;
import com.zura.gymCRM.entities.Trainer;
import com.zura.gymCRM.entities.Training;
import com.zura.gymCRM.entities.TrainingType;
import com.zura.gymCRM.exceptions.NotFoundException;
import com.zura.gymCRM.facade.GymFacade;
import com.zura.gymCRM.service.TraineeService;
import com.zura.gymCRM.service.TrainerService;
import com.zura.gymCRM.service.TrainingService;
import com.zura.gymCRM.service.TrainingTypeService;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GymCrmApplicationTraineeTests {

  @Autowired private TraineeService traineeService;
  @Autowired private TrainerService trainerService;
  @Autowired private TrainingService trainingService;
  @Autowired private TrainingTypeRepository trainingTypeRepository;
  @Autowired private TrainingTypeService trainingTypeService;
  private GymFacade gymFacade;

  @BeforeEach
  void setUp() {
    gymFacade = new GymFacade(traineeService, trainerService, trainingService,
                              trainingTypeService);
  }

  @Test
  @Order(1)
  public void testCreateTrainee_Success() {
    Trainee savedTrainee = gymFacade.addTrainee("Alice", "Smith", true,
                                                new Date(), "xundadze street");
    assertNotNull(savedTrainee.getId());
    assertEquals("Alice", savedTrainee.getUser().getFirstName());
    assertEquals("Smith", savedTrainee.getUser().getLastName());
    assertEquals("Alice.Smith", savedTrainee.getUser().getUsername());
    assertEquals(10, savedTrainee.getUser().getPassword().length());
  }

  @Test
  @Order(2)
  void testAddSameTrainee_Success() {
    Trainee savedTrainee = gymFacade.addTrainee(
        "Alice", "Smith", true, new Date(), "Tabukashvili street");
    assertNotNull(savedTrainee.getId());
    assertEquals("Alice", savedTrainee.getUser().getFirstName());
    assertEquals("Smith", savedTrainee.getUser().getLastName());
    assertEquals("Alice.Smith1", savedTrainee.getUser().getUsername());
    assertEquals(10, savedTrainee.getUser().getPassword().length());
  }

  @Test
  @Order(3)
  void testUpdateTrainee_Success() {
    Trainee trainee =
        gymFacade.addTrainee("John", "Doe", true, new Date(), "Old Address");
    String username = "John.Doe";
    trainee.getUser().setUsername(username);
    trainee.getUser().setPassword("1234567898");
    trainee.setAddress("New Address");
    gymFacade.updateTrainee(trainee);
    Optional<Trainee> updatedTrainee =
        gymFacade.selectTraineeByusername(username);
    assertEquals(username, updatedTrainee.get().getUser().getUsername());
    assertEquals("1234567898", updatedTrainee.get().getUser().getPassword());
    assertEquals("New Address", updatedTrainee.get().getAddress());
  }

  @Test
  @Order(5)
  void testUpdateTrainee_Failure() {
    Trainee trainee =
        gymFacade.addTrainee("John", "Doe", true, new Date(), "Old Address");
    String username = "John.DoeUpdated";
    trainee.getUser().setUsername(username);
    trainee.getUser().setPassword("1234567898");
    trainee.setAddress("New Address");
    assertThrows(NotFoundException.class,
                 () -> { gymFacade.updateTrainee(trainee); });
  }

  @Test
  @Order(6)
  void testDeleteTrainee() {
    String username = "Alice.Smith";
    gymFacade.deleteTraineeByUsername(username);
    assertThrows(NotFoundException.class,
                 () -> { gymFacade.selectTraineeByusername(username); });
  }

  @Test
  @Order(7)
  void testSelectTrainee_Success() {
    String username = "Alice.smith1";
    Trainee trainee = gymFacade.selectTraineeByusername(username).get();
    assertEquals("Alice", trainee.getUser().getFirstName());
    assertEquals("Smith", trainee.getUser().getLastName());
  }

  @Test
  @Order(9)
  void testChangeTraineePassword_Success() {
    Trainee trainee =
        gymFacade.addTrainee("John", "Doe", true, new Date(), "Old Address");
    String username = "John.Doe";
    String newPassword = "newPasswor";
    gymFacade.changeTraineePassword(username, newPassword);
    Trainee updatedTrainee = gymFacade.selectTraineeByusername(username).get();
    assertEquals(newPassword, updatedTrainee.getUser().getPassword());
  }

  @Test

  @Order(10)
  void testChangeTraineePassword_Failure_TraineeNotFound() {
    String username = "nonexistent.username";
    String newPassword = "newPassword123";
    NotFoundException exception = assertThrows(NotFoundException.class, () -> {
      gymFacade.changeTraineePassword(username, newPassword);
    });
  }

  @Test

  @Order(11)
  void testActivateTrainee() {
    Trainee trainee = gymFacade.addTrainee("John", "Doeer", false, new Date(),
                                           "Some Address");
    String username = "John.Doeer";
    assertFalse(trainee.getUser().getIsActive());
    Trainee activatedTrainee = gymFacade.activateTrainee(username);
    assertTrue(activatedTrainee.getUser().getIsActive());
  }

  @Test

  @Order(12)
  void testDeactivateTrainee_Success() {
    Trainee trainee =
        gymFacade.addTrainee("Jane", "Smith", true, new Date(), "Some Address");
    String username = "Jane.Smith";
    assertTrue(trainee.getUser().getIsActive());
    Trainee deactivatedTrainee = gymFacade.deactivateTrainee(username);
    assertFalse(deactivatedTrainee.getUser().getIsActive());
  }

  @Test

  @Order(13)
  void testGetTraineeTrainingsByCriteria_Success() {
    Trainee trainee =
        gymFacade.addTrainee("Alice", "Smit", true, new Date(), "Some Address");
    String username = trainee.getUser().getUsername();
    Optional<TrainingType> trainingtype =
        trainingTypeRepository.findByTrainingTypeName("Strength");
    Trainer trainerA = gymFacade.addTrainer("Zura", "Doe2", true, trainingtype.get());
    Trainer trainerB =
        gymFacade.addTrainer("Mike", "Brown", true, trainingtype.get());
    Date trainingDate = new Date();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(trainingDate);
    calendar.add(Calendar.HOUR, 2); // Add 2 hours
    Date toDate = calendar.getTime();
    gymFacade.addTraining(trainee, trainerB, "Cardio", trainingtype.get(),
                          trainingDate, 45);
    gymFacade.addTraining(trainee, trainerA, "Strength", trainingtype.get(),
                          trainingDate, 60);
    gymFacade.updateTraineeTrainerRelationship(trainee.getUser().getUsername(),
                                               trainerA.getUser().getUsername(),
                                               true);
    Date fromDate = trainingDate;
    List<Training> result = gymFacade.getTraineeTrainingsByCriteria(
        username, fromDate, toDate, "Zura.Doe2", "Strength");
    assertNotNull(result);
    assertEquals(1, result.size(),
                 "Only one training session should match the criteria");
    assertEquals("Strength",
                 result.get(0).getTrainingType().getTrainingTypeName(),
                 "Training type should match");
    assertEquals("Zura", result.get(0).getTrainer().getUser().getFirstName(),
                 "Trainer's first nameshould match ");
    assertEquals("Doe2", result.get(0).getTrainer().getUser().getLastName(),
                 "Trainer's lastname should match");
  }

  @Test
  @Order(14)
  void testGetTraineeTrainingsByCriteria_Failure_TraineeNotFound() {
    String nonExistentUsername = "NonExistentUser";
    assertThrows(NotFoundException.class, () -> {
      gymFacade.getTraineeTrainingsByCriteria(
          nonExistentUsername, new Date(), new Date(), "TrainerA", "Strength");
    });
  }

  @Test
  @Order(15)
  void testGetUnassignedTrainersForTrainee() {
    String username = "Alice.Smit";

    List<Trainer> unssignedTrainers =
        gymFacade.getUnassignedTrainersForTrainee(username);

    List<Trainer> result =
        traineeService.getUnassignedTrainersForTrainee(username);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("Mike.Brown", result.get(0).getUser().getUsername());
  }
}
*/