package com.zura.gymCRM;

import static org.junit.jupiter.api.Assertions.*;

import com.zura.gymCRM.exceptions.AddException;
import com.zura.gymCRM.exceptions.NotFoundException;
import com.zura.gymCRM.facade.GymFacade;
import com.zura.gymCRM.model.Trainee;
import com.zura.gymCRM.model.Training;
import com.zura.gymCRM.model.TrainingType;
import com.zura.gymCRM.service.TraineeService;
import com.zura.gymCRM.service.TrainerService;
import com.zura.gymCRM.service.TrainingService;
import com.zura.gymCRM.storage.TraineeStorage;
import java.time.LocalDate;
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
  private GymFacade gymFacade;
  @Autowired private TraineeStorage traineeStorage;

  @BeforeEach
  void setUp() {
    gymFacade = new GymFacade(traineeService, trainerService, trainingService);
  }

  @Test
  @Order(1)
  void testAddTrainee_Success() {
    LocalDate birthDate = LocalDate.of(1995, 5, 20);

    Training testTraining1 =
        new Training(3, 4, "Strength", "Gym", LocalDate.now(), 60,
                     new TrainingType("Strength"));

    Trainee trainee1 = gymFacade.AddTrainee(
        4, "Zura", "Kajaia", true, birthDate, "New York", testTraining1);
    assertEquals(4, traineeStorage.getTraineeMap().get(4).getUserId());
    assertEquals("Zura", traineeStorage.getTraineeMap().get(4).getFirstName());
    assertEquals("Kajaia", traineeStorage.getTraineeMap().get(4).getLastName());
    assertEquals("Zura.Kajaia",
                 traineeStorage.getTraineeMap().get(4).getUserName());
    assertEquals(10,
                 traineeStorage.getTraineeMap().get(4).getPassword().length());
    assertNotEquals(null, traineeStorage.getTraineeMap().get(4));
  }

  @Test
  @Order(2)
  void testAddTrainee_Failure() {
    LocalDate birthDate = LocalDate.of(1995, 5, 20);

    Training testTraining1 =
        new Training(5, 4, "Strength", "Gym", LocalDate.now(), 60,
                     new TrainingType("Strength"));

    assertThrows(AddException.class, () -> {
      gymFacade.AddTrainee(4, "Zura", "Kajaia", true, birthDate, "New York",
                           testTraining1);
    });
  }

  @Test
  @Order(3)
  void testAddSameTrainee_Success() {
    LocalDate birthDate = LocalDate.of(1995, 5, 20);

    Training testTraining1 =
        new Training(6, 4, "Strength", "Gym", LocalDate.now(), 60,
                     new TrainingType("Strength"));

    Trainee trainee1 = gymFacade.AddTrainee(
        5, "Zura", "Kajaia", true, birthDate, "New York", testTraining1);
    assertEquals(5, traineeStorage.getTraineeMap().get(5).getUserId());
    assertEquals("Zura", traineeStorage.getTraineeMap().get(5).getFirstName());
    assertEquals("Kajaia", traineeStorage.getTraineeMap().get(5).getLastName());
    assertEquals("Zura.Kajaia1",
                 traineeStorage.getTraineeMap().get(5).getUserName());
    assertEquals(10,
                 traineeStorage.getTraineeMap().get(5).getPassword().length());
    assertNotEquals(null, traineeStorage.getTraineeMap().get(5));
  }

  @Test
  @Order(4)
  void testUpdateTrainee_Success() {
    System.out.println(traineeStorage.getTraineeMap().keySet());
    System.out.println(traineeStorage.getTraineeMap().get(1).getUserName());
    LocalDate birthDate = LocalDate.of(1995, 5, 20);

    Training testTraining1 =
        new Training(2, 8, "Strength", "Gym", LocalDate.now(), 60,
                     new TrainingType("Strength"));

    Trainee trainee1 = new Trainee(1, "John", "Doe", "John.Doe", "ssssssssss",
                                   true, birthDate, "New York", testTraining1);
    gymFacade.updateTrainee(trainee1);
    assertEquals(1, traineeStorage.getTraineeMap().get(1).getUserId());
    assertEquals(trainee1.getPassword(),
                 traineeStorage.getTraineeMap().get(1).getPassword());
  }

  @Test
  @Order(5)
  void testUpdateTrainee_Failure() {
    LocalDate birthDate = LocalDate.of(1995, 5, 20);

    Training testTraining1 =
        new Training(2, 9, "Strength", "Gym", LocalDate.now(), 60,
                     new TrainingType("Strength"));

    Trainee trainee1 = new Trainee(1, "John", "Doe", "John.Doe", "ssssss", true,
                                   birthDate, "New York", testTraining1);

    assertThrows(RuntimeException.class,
                 () -> { gymFacade.updateTrainee(trainee1); });
  }

  @Test
  @Order(6)
  void testDeleteTrainee() {
    int userId = 1;
    gymFacade.deleteTrainee(userId);
    assertThrows(NotFoundException.class,
                 () -> { gymFacade.selectTrainee(userId); });
  }

  @Test
  @Order(7)
  void testSelectTrainee_Success() {
    int userId = 2;

    Optional<Trainee> optionalTrainee = gymFacade.selectTrainee(userId);

    assertTrue(optionalTrainee.isPresent(), "Trainee should be present");
    Trainee trainee = optionalTrainee.get();
    assertEquals(2, trainee.getUserId());
    assertEquals("Jane", trainee.getFirstName());
    assertEquals("Smith", trainee.getLastName());
  }

  @Test
  @Order(8)
  void testSelectTrainee_Failure() {
    int userId = 7;
    assertThrows(NotFoundException.class,
                 () -> { gymFacade.selectTrainee(userId); });
  }
}
