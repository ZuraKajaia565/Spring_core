package com.zura.gymCRM;
/*
 * import static org.junit.jupiter.api.Assertions.*;
 *
 * import com.zura.gymCRM.exceptions.AddException;
 * import com.zura.gymCRM.exceptions.NotFoundException;
 * import com.zura.gymCRM.facade.GymFacade;
 * import com.zura.gymCRM.model.Trainer;
 * import com.zura.gymCRM.model.Training;
 * import com.zura.gymCRM.model.TrainingType;
 * import com.zura.gymCRM.service.TraineeService;
 * import com.zura.gymCRM.service.TrainerService;
 * import com.zura.gymCRM.service.TrainingService;
 * import com.zura.gymCRM.storage.TrainerStorage;
 * import java.time.LocalDate;
 * import java.util.Optional;
 * import org.junit.jupiter.api.BeforeEach;
 * import org.junit.jupiter.api.MethodOrderer;
 * import org.junit.jupiter.api.Order;
 * import org.junit.jupiter.api.Test;
 * import org.junit.jupiter.api.TestMethodOrder;
 * import org.springframework.beans.factory.annotation.Autowired;
 * import org.springframework.boot.test.context.SpringBootTest;
 *
 * @SpringBootTest
 *
 * @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
 * class GymCrmApplicationTrainerTests {
 *
 * @Autowired private TraineeService traineeService;
 *
 * @Autowired private TrainerService trainerService;
 *
 * @Autowired private TrainingService trainingService;
 * private GymFacade gymFacade;
 *
 * @Autowired private TrainerStorage trainerStorage;
 *
 * @BeforeEach
 * void setUp() {
 * gymFacade = new GymFacade(traineeService, trainerService, trainingService);
 * }
 *
 * @Test
 *
 * @Order(1)
 * void testAddTrainer_Success() {
 * Training testTraining1 =
 * new Training(13, 14, "Strength", "Gym", LocalDate.now(), 60,
 * new TrainingType("Strength"));
 *
 * Trainer trainer1 =
 * gymFacade.addTrainer(7, "Zazusha", "Kolmogorov", true, "Probability",
 * "science", testTraining1);
 * assertEquals(trainer1.getUserId(),
 * trainerStorage.getTrainerMap().get(7).getUserId());
 * assertEquals(trainer1.getFirstName(),
 * trainerStorage.getTrainerMap().get(7).getFirstName());
 * assertEquals(trainer1.getLastName(),
 * trainerStorage.getTrainerMap().get(7).getLastName());
 * assertEquals("Zazusha.Kolmogorov",
 * trainerStorage.getTrainerMap().get(7).getUserName());
 * assertEquals(10,
 * trainerStorage.getTrainerMap().get(7).getPassword().length());
 * assertNotEquals(null, trainerStorage.getTrainerMap().get(7));
 * }
 *
 * @Test
 *
 * @Order(2)
 * void testAddTrainer_Failure() {
 * Training testTraining1 =
 * new Training(5, 15, "Strength", "Gym", LocalDate.now(), 60,
 * new TrainingType("Strength"));
 *
 * assertThrows(AddException.class, () -> {
 * gymFacade.addTrainer(7, "Zazusha", "Kolmogorov", true, "Probability",
 * "science", testTraining1);
 * });
 * }
 *
 * @Test
 *
 * @Order(3)
 * void testAddSameTrainer_Success() {
 * Training testTraining1 =
 * new Training(16, 4, "Strength", "Gym", LocalDate.now(), 60,
 * new TrainingType("Strength"));
 *
 * Trainer trainer =
 * gymFacade.addTrainer(8, "Zazusha", "Kolmogorov", true, "Probability",
 * "science", testTraining1);
 * assertEquals(8, trainerStorage.getTrainerMap().get(8).getUserId());
 * assertEquals("Zazusha",
 * trainerStorage.getTrainerMap().get(8).getFirstName());
 * assertEquals("Kolmogorov",
 * trainerStorage.getTrainerMap().get(8).getLastName());
 * assertEquals("Zazusha.Kolmogorov1",
 * trainerStorage.getTrainerMap().get(8).getUserName());
 * assertEquals(10,
 * trainerStorage.getTrainerMap().get(8).getPassword().length());
 * assertNotEquals(null, trainerStorage.getTrainerMap().get(8));
 * }
 *
 * @Test
 *
 * @Order(4)
 * void testUpdateTrainer_Success() {
 *
 * Training testTraining1 =
 * new Training(2, 9, "Strength", "Gym", LocalDate.now(), 60,
 * new TrainingType("Strength"));
 *
 * Trainer trainer1 =
 * new Trainer(1, "Emily", "Brown", "Emily.Bron", "ssssssssss", true,
 * "wrestling", new TrainingType("sports"), testTraining1);
 * gymFacade.updateTrainer(trainer1);
 * assertEquals(1, trainerStorage.getTrainerMap().get(1).getUserId());
 * assertEquals(trainer1.getPassword(),
 * trainerStorage.getTrainerMap().get(1).getPassword());
 * }
 *
 * @Test
 *
 * @Order(5)
 * void testUpdateTrainer_Failure() {
 *
 * Training testTraining1 =
 * new Training(4, 10, "Strength", "Gym", LocalDate.now(), 60,
 * new TrainingType("Strength"));
 *
 * Trainer trainer1 =
 * new Trainer(1, "Emily", "Brown", "Emily.Bron", "sssss", true,
 * "wrestling", new TrainingType("sports"), testTraining1);
 * assertThrows(RuntimeException.class,
 * () -> { gymFacade.updateTrainer(trainer1); });
 * }
 *
 * @Test
 *
 * @Order(6)
 * void testSelectTrainee_Success() {
 * int userId = 2;
 *
 * Optional<Trainer> optionalTrainer = gymFacade.getTrainer(userId);
 *
 * assertTrue(optionalTrainer.isPresent(), "Trainer should be present");
 *
 * Trainer trainer = optionalTrainer.get();
 * assertEquals(2, trainer.getUserId());
 * assertEquals("Michael", trainer.getFirstName());
 * assertEquals("Green", trainer.getLastName());
 * }
 *
 * @Test
 *
 * @Order(7)
 * void testSelectTrainee_Failure() {
 * int userId = 17;
 * assertThrows(NotFoundException.class,
 * () -> { gymFacade.getTrainer(userId); });
 * }
 * }
 */
