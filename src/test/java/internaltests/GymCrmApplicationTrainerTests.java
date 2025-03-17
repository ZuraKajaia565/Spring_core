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
class GymCrmApplicationTrainerTests {
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
  public void testCreateTrainer_Success() {
    Optional<TrainingType> trainingType =
        trainingTypeRepository.findByTrainingTypeName("Cardio");
    Trainer savedTrainer =
        gymFacade.addTrainer("Zura", "Kajaia", true, trainingType.get());
    assertNotNull(savedTrainer.getId());
    assertEquals("Zura", savedTrainer.getUser().getFirstName());
    assertEquals("Kajaia", savedTrainer.getUser().getLastName());
    assertEquals("Zura.Kajaia", savedTrainer.getUser().getUsername());
    assertEquals(10, savedTrainer.getUser().getPassword().length());
  }

  @Test
  @Order(2)
  void testAddSameTrainer_Success() {
    Optional<TrainingType> trainingType =
        trainingTypeRepository.findByTrainingTypeName("Cardio");

    Trainer savedTrainer =
        gymFacade.addTrainer("Zura", "Kajaia", true, trainingType.get());
    assertNotNull(savedTrainer.getId());
    assertEquals("Zura", savedTrainer.getUser().getFirstName());
    assertEquals("Kajaia", savedTrainer.getUser().getLastName());
    assertEquals("Zura.Kajaia1", savedTrainer.getUser().getUsername());
    assertEquals(10, savedTrainer.getUser().getPassword().length());
  }

  @Test
  @Order(3)
  void testUpdateTrainer_Success() {
    Optional<TrainingType> trainingType =
        trainingTypeRepository.findByTrainingTypeName("Cardio");

    Trainer trainer = gymFacade.addTrainer("Bakuri", "Doe", true, trainingType.get());
    String username = "Bakuri.Doe";
    trainer.getUser().setUsername(username);
    trainer.getUser().setPassword("1234567898");
    gymFacade.updateTrainer(trainer);
    Trainer updatedTrainer = gymFacade.selectTrainerByUsername(username).get();
    assertEquals(username, updatedTrainer.getUser().getUsername());
    assertEquals("1234567898", updatedTrainer.getUser().getPassword());
  }

  @Test
  @Order(5)
  void testUpdateTrainer_Failure() {
    Optional<TrainingType> trainingType =
        trainingTypeRepository.findByTrainingTypeName("Cardio");

    Trainer trainer = gymFacade.addTrainer("Joni", "Doe", true, trainingType.get());
    String username = "John.DoeUpdated";
    trainer.getUser().setUsername(username);
    trainer.getUser().setPassword("1234567898");
    assertThrows(NotFoundException.class,
                 () -> { gymFacade.updateTrainer(trainer); });
  }

  @Test
  @Order(6)
  void testSelectTrainer_Success() {
    String username = "Bakuri.Doe";
    Trainer trainer = gymFacade.selectTrainerByUsername(username).get();
    assertEquals("Bakuri", trainer.getUser().getFirstName());
    assertEquals("Doe", trainer.getUser().getLastName());
  }

  @Test
  @Order(9)
  void testChangeTrainerPassword_Success() {
    Optional<TrainingType> trainingType =
        trainingTypeRepository.findByTrainingTypeName("Cardio");

    Trainer trainer = gymFacade.addTrainer("Bakuri", "Doe", true, trainingType.get());
    String username = "Bakuri.Doe1";
    String newPassword = "newPasswor";
    gymFacade.changeTrainerPassword(username, newPassword);
    Trainer updatedTrainer = gymFacade.selectTrainerByUsername(username).get();
    assertEquals(newPassword, updatedTrainer.getUser().getPassword());
  }

  @Test
  @Order(10)
  void testChangeTrainerPassword_Failure_TraineeNotFound() {
    String username = "nonexistent.username";
    String newPassword = "newPassword123";
    NotFoundException exception = assertThrows(NotFoundException.class, () -> {
      gymFacade.changeTrainerPassword(username, newPassword);
    });
  }

  @Test
  @Order(11)
  void testActivateTrainer() {
    Optional<TrainingType> trainingType =
        trainingTypeRepository.findByTrainingTypeName("Cardio");

    Trainer trainer =
        gymFacade.addTrainer("Zura", "Doeer", false, trainingType.get());
    String username = "Zura.Doeer";
    assertFalse(trainer.getUser().getIsActive());
    Trainer activatedTrainer = gymFacade.activateTrainer(username);
    assertTrue(activatedTrainer.getUser().getIsActive());
  }

  @Test
  @Order(12)
  void testDeactivateTrainer_Success() {
    Trainer trainer = gymFacade.selectTrainerByUsername("Bakuri.Doe").get();
    String username = "Bakuri.Doe";
    assertTrue(trainer.getUser().getIsActive());
    Trainer deactivatedTrainer = gymFacade.deactivateTrainer(username);
    assertFalse(deactivatedTrainer.getUser().getIsActive());
  }

  @Test
  @Order(13)
  void testGetTrainerTrainingsByCriteria_Success() {
    Optional<TrainingType> trainingType =
        trainingTypeRepository.findByTrainingTypeName("Cardio");

    Trainer trainer = gymFacade.addTrainer("Zaur", "Doe", true, trainingType.get());
    String username = trainer.getUser().getUsername();
    Trainee traineeA = gymFacade.addTrainee("Mate", "Kopaliani", true,
                                            new Date(), "xundadze street");

    Trainee traineeB = gymFacade.addTrainee("Revaz", "Goguadze", true,
                                            new Date(), "dadiani street");

    Date trainingDate = new Date();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(trainingDate);
    calendar.add(Calendar.HOUR, 2);
    Date toDate = calendar.getTime();
    gymFacade.addTraining(traineeA, trainer, "Cardio", trainingType.get(),
                          trainingDate, 45);
    gymFacade.addTraining(traineeB, trainer, "Strength", trainingType.get(),
                          trainingDate, 60);
    gymFacade.updateTraineeTrainerRelationship(traineeA.getUser().getUsername(),
                                               trainer.getUser().getUsername(),
                                               true);
    gymFacade.updateTraineeTrainerRelationship(traineeB.getUser().getUsername(),
                                               trainer.getUser().getUsername(),
                                               true);

    Date fromDate = trainingDate;
    List<Training> result = gymFacade.getTrainerTrainingsByCriteria(
            username, fromDate, toDate, "Mate.Kopaliani");

    assertNotNull(result);
    assertEquals(1, result.size(),
                 "Only one training session should match the criteria");
    assertEquals("Cardio",
                 result.get(0).getTrainingType().getTrainingTypeName(),
                 "Training type should match");
    assertEquals("Mate", result.get(0).getTrainee().getUser().getFirstName(),
                 "Trainer's first nameshould match ");
    assertEquals("Kopaliani",
                 result.get(0).getTrainee().getUser().getLastName(),
                 "Trainer's lastname should match");
  }

  @Test
  @Order(14)
  void testGetTrainerTrainingsByCriteria_Failure_TrainerNotFound() {
    String nonExistentUsername = "NonExistentUser";
    assertThrows(NotFoundException.class, () -> {
      gymFacade.getTrainerTrainingsByCriteria(
          nonExistentUsername, new Date(), new Date(), "TrainerA");
    });
  }
}
*/