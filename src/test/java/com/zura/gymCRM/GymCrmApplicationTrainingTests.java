package com.zura.gymCRM;
/*
 * import static org.junit.jupiter.api.Assertions.*;
 *
 * import com.zura.gymCRM.exceptions.AddException;
 * import com.zura.gymCRM.exceptions.NotFoundException;
 * import com.zura.gymCRM.facade.GymFacade;
 * import com.zura.gymCRM.model.Training;
 * import com.zura.gymCRM.service.TraineeService;
 * import com.zura.gymCRM.service.TrainerService;
 * import com.zura.gymCRM.service.TrainingService;
 * import com.zura.gymCRM.storage.TrainingStorage;
 * import java.time.LocalDate;
 * import java.util.Arrays;
 * import java.util.List;
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
 * class GymCrmApplicationTrainingTests {
 *
 * @Autowired private TraineeService traineeService;
 *
 * @Autowired private TrainerService trainerService;
 *
 * @Autowired private TrainingService trainingService;
 * private GymFacade gymFacade;
 *
 * @Autowired private TrainingStorage trainingStorage;
 *
 * @BeforeEach
 * void setUp() {
 * gymFacade = new GymFacade(traineeService, trainerService, trainingService);
 * }
 *
 * @Test
 *
 * @Order(1)
 * void testAddTraining_Success() {
 * Training trainer1 = gymFacade.addTraining(10, 10, "Strength", "Gym",
 * LocalDate.now(), 60, "Strength");
 * List<Integer> myList = Arrays.asList(10, 10);
 * assertEquals(
 * "Gym", trainingStorage.getTrainingMap().get(myList).getTrainingType());
 * assertEquals(
 * 60, trainingStorage.getTrainingMap().get(myList).getTrainingDuration());
 * assertEquals(
 * "Strength",
 * trainingStorage.getTrainingMap().get(myList).getTrainingName());
 * assertNotEquals(null, trainingStorage.getTrainingMap().get(myList));
 * }
 *
 * @Test
 *
 * @Order(2)
 * void testAddTraining_Failure() {
 * assertThrows(AddException.class, () -> {
 * gymFacade.addTraining(10, 10, "Strength", "Gym", LocalDate.now(), 60,
 * "Strength");
 * });
 * }
 *
 * @Test
 *
 * @Order(3)
 * void testSelectTraining_Success() {
 *
 * Optional<Training> optionalTraining = gymFacade.getTraining(1, 1);
 *
 * assertTrue(optionalTraining.isPresent(), "Training should be present");
 *
 * optionalTraining.ifPresent(training -> {
 * assertEquals("Yoga", training.getTrainingName());
 * assertEquals("sports", training.getTrainingType());
 * assertEquals(60, training.getTrainingDuration());
 * });
 * }
 *
 * @Test
 *
 * @Order(4)
 * void testSelectTraining_Failure() {
 * assertThrows(NotFoundException.class,
 * () -> { gymFacade.getTraining(100, 100); });
 * }
 * }
 */
