package com.zura.gymCRM;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.zura.gymCRM.entities.Trainee;
import com.zura.gymCRM.entities.Trainer;
import com.zura.gymCRM.entities.Training;
import com.zura.gymCRM.entities.TrainingType;
import com.zura.gymCRM.entities.User;
import com.zura.gymCRM.facade.GymFacade;
import com.zura.gymCRM.security.PasswordUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

@ExtendWith(MockitoExtension.class)
public class GymFacadeTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingService trainingService;

    @Mock
    private TrainingTypeService trainingTypeService;

    @Mock
    private PasswordUtil passwordUtil;

    @InjectMocks
    private GymFacade gymFacade;

    private User createMockUser(String firstName, String lastName, String username, String password, boolean isActive) {
        User user = new User();
        user.setId(1L);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword(password);
        user.setIsActive(isActive);
        return user;
    }

    private Trainee createMockTrainee(User user, Date dateOfBirth, String address) {
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUser(user);
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);
        trainee.setTrainers(new ArrayList<>());
        return trainee;
    }

    private Trainer createMockTrainer(User user, TrainingType specialization) {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUser(user);
        trainer.setSpecialization(specialization);
        trainer.setTrainees(new ArrayList<>());
        return trainer;
    }

    private TrainingType createMockTrainingType(Long id, String name) {
        TrainingType trainingType = new TrainingType();
        trainingType.setId(id);
        trainingType.setTrainingTypeName(name);
        return trainingType;
    }

    @BeforeEach
    public void setUp() {
        MDC.put("transactionId", "test-transaction-id");
    }

    @Test
    public void testAddTrainee_Success() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        boolean isActive = true;
        Date dateOfBirth = new Date();
        String address = "123 Main St";

        User mockUser = createMockUser(firstName, lastName, "John.Doe", "randomPass", isActive);
        Trainee mockTrainee = createMockTrainee(mockUser, dateOfBirth, address);

        when(traineeService.createTrainee(any(Trainee.class))).thenReturn(mockTrainee);

        // Act
        Trainee result = gymFacade.addTrainee(firstName, lastName, isActive, dateOfBirth, address);

        // Assert
        assertNotNull(result);
        assertEquals(firstName, result.getUser().getFirstName());
        assertEquals(lastName, result.getUser().getLastName());
        assertEquals(isActive, result.getUser().getIsActive());
        assertEquals(dateOfBirth, result.getDateOfBirth());
        assertEquals(address, result.getAddress());

        verify(traineeService, times(1)).createTrainee(any(Trainee.class));
    }

    @Test
    public void testAddTrainer_Success() {
        // Arrange
        String firstName = "Jane";
        String lastName = "Smith";
        boolean isActive = true;
        TrainingType specialization = createMockTrainingType(1L, "Strength");

        User mockUser = createMockUser(firstName, lastName, "Jane.Smith", "randomPass", isActive);
        Trainer mockTrainer = createMockTrainer(mockUser, specialization);

        when(trainerService.createTrainer(any(Trainer.class))).thenReturn(mockTrainer);

        // Act
        Trainer result = gymFacade.addTrainer(firstName, lastName, isActive, specialization);

        // Assert
        assertNotNull(result);
        assertEquals(firstName, result.getUser().getFirstName());
        assertEquals(lastName, result.getUser().getLastName());
        assertEquals(isActive, result.getUser().getIsActive());
        assertEquals(specialization, result.getSpecialization());

        verify(trainerService, times(1)).createTrainer(any(Trainer.class));
    }

    @Test
    public void testUpdateTrainee_Success() {
        // Arrange
        User mockUser = createMockUser("John", "Doe", "John.Doe", "password", true);
        Date dateOfBirth = new Date();
        Trainee mockTrainee = createMockTrainee(mockUser, dateOfBirth, "123 Main St");

        when(traineeService.selectTraineeByUsername("John.Doe")).thenReturn(Optional.of(mockTrainee));
        when(traineeService.updateTrainee(mockTrainee)).thenReturn(mockTrainee);

        // Act
        Trainee result = gymFacade.updateTrainee(mockTrainee);

        // Assert
        assertNotNull(result);
        assertEquals(mockTrainee.getUser().getUsername(), result.getUser().getUsername());

        verify(traineeService, times(1)).selectTraineeByUsername("John.Doe");
        verify(traineeService, times(1)).updateTrainee(mockTrainee);
    }


    @Test
    public void testUpdateTrainer_Success() {
        // Arrange
        User mockUser = createMockUser("Jane", "Smith", "Jane.Smith", "password", true);
        TrainingType specialization = createMockTrainingType(1L, "Cardio");
        Trainer mockTrainer = createMockTrainer(mockUser, specialization);

        when(trainerService.findTrainerByUsername("Jane.Smith")).thenReturn(Optional.of(mockTrainer));
        when(trainerService.updateTrainer(mockTrainer)).thenReturn(mockTrainer);

        // Act
        Trainer result = gymFacade.updateTrainer(mockTrainer);

        // Assert
        assertNotNull(result);
        assertEquals(mockTrainer.getUser().getUsername(), result.getUser().getUsername());

        verify(trainerService, times(1)).findTrainerByUsername("Jane.Smith");
        verify(trainerService, times(1)).updateTrainer(mockTrainer);
    }


    @Test
    public void testActivateTrainee_Success() {
        // Arrange
        String username = "John.Doe";

        when(traineeService.activateTrainee(username)).thenReturn(
                createMockTrainee(
                        createMockUser("John", "Doe", username, "password", true),
                        new Date(),
                        "123 Main St"
                )
        );

        // Act
        Trainee result = gymFacade.activateTrainee(username);

        // Assert
        assertNotNull(result);
        assertTrue(result.getUser().getIsActive());

        verify(traineeService, times(1)).activateTrainee(username);
    }

    @Test
    public void testDeactivateTrainee_Success() {
        // Arrange
        String username = "John.Doe";

        when(traineeService.deactivateTrainee(username)).thenReturn(
                createMockTrainee(
                        createMockUser("John", "Doe", username, "password", false),
                        new Date(),
                        "123 Main St"
                )
        );

        // Act
        Trainee result = gymFacade.deactivateTrainee(username);

        // Assert
        assertNotNull(result);
        assertFalse(result.getUser().getIsActive());

        verify(traineeService, times(1)).deactivateTrainee(username);
    }

    @Test
    public void testActivateTrainer_Success() {
        // Arrange
        String username = "Jane.Smith";

        when(trainerService.activateTrainer(username)).thenReturn(
                createMockTrainer(
                        createMockUser("Jane", "Smith", username, "password", true),
                        createMockTrainingType(1L, "Strength")
                )
        );

        // Act
        Trainer result = gymFacade.activateTrainer(username);

        // Assert
        assertNotNull(result);
        assertTrue(result.getUser().getIsActive());

        verify(trainerService, times(1)).activateTrainer(username);
    }

    @Test
    public void testDeactivateTrainer_Success() {
        // Arrange
        String username = "Jane.Smith";

        when(trainerService.deactivateTrainer(username)).thenReturn(
                createMockTrainer(
                        createMockUser("Jane", "Smith", username, "password", false),
                        createMockTrainingType(1L, "Strength")
                )
        );

        // Act
        Trainer result = gymFacade.deactivateTrainer(username);

        // Assert
        assertNotNull(result);
        assertFalse(result.getUser().getIsActive());

        verify(trainerService, times(1)).deactivateTrainer(username);
    }

    @Test
    public void testDeleteTraineeByUsername_Success() {
        // Arrange
        String username = "John.Doe";

        when(traineeService.selectTraineeByUsername(username)).thenReturn(
                Optional.of(createMockTrainee(
                        createMockUser("John", "Doe", username, "password", true),
                        new Date(),
                        "123 Main St"
                ))
        );

        doNothing().when(traineeService).deleteTraineeByUsername(username);

        // Act
        gymFacade.deleteTraineeByUsername(username);

        // Assert
        verify(traineeService, times(1)).selectTraineeByUsername(username);
        verify(traineeService, times(1)).deleteTraineeByUsername(username);
    }





    @Test
    public void testAddTraining_Success() throws ParseException {
        // Arrange
        User traineeUser = createMockUser("John", "Doe", "John.Doe", "password", true);
        User trainerUser = createMockUser("Jane", "Smith", "Jane.Smith", "password", true);

        Trainee trainee = createMockTrainee(traineeUser, new Date(), "123 Main St");
        TrainingType trainingType = createMockTrainingType(1L, "Strength");
        Trainer trainer = createMockTrainer(trainerUser, trainingType);

        String trainingName = "Morning Workout";
        Date trainingDate = new SimpleDateFormat("yyyy-MM-dd").parse("2025-01-01");
        int trainingDuration = 60;

        Training mockTraining = new Training();
        mockTraining.setId(1L);
        mockTraining.setTrainee(trainee);
        mockTraining.setTrainer(trainer);
        mockTraining.setTrainingName(trainingName);
        mockTraining.setTrainingType(trainingType);
        mockTraining.setTrainingDate(trainingDate);
        mockTraining.setTrainingDuration(trainingDuration);

        when(trainingService.createTraining(any(Training.class))).thenReturn(mockTraining);

        // Act
        Training result = gymFacade.addTraining(trainee, trainer, trainingName, trainingType, trainingDate, trainingDuration);

        // Assert
        assertNotNull(result);
        assertEquals(trainingName, result.getTrainingName());
        assertEquals(trainingDate, result.getTrainingDate());
        assertEquals(trainingDuration, result.getTrainingDuration());
        assertEquals(trainee, result.getTrainee());
        assertEquals(trainer, result.getTrainer());
        assertEquals(trainingType, result.getTrainingType());

        verify(trainingService, times(1)).createTraining(any(Training.class));
    }

    @Test
    public void testGetTraineeTrainingsByCriteria_Success() {
        // Arrange
        String username = "John.Doe";
        Date fromDate = new Date();
        Date toDate = new Date();
        String trainerName = "Jane.Smith";
        String trainingType = "Strength";

        List<Training> mockTrainings = new ArrayList<>();
        Training mockTraining = new Training();
        mockTraining.setId(1L);
        mockTrainings.add(mockTraining);

        when(traineeService.selectTraineeByUsername(username)).thenReturn(Optional.of(new Trainee()));
        when(traineeService.getTraineeTrainingsByCriteria(username, fromDate, toDate, trainerName, trainingType))
                .thenReturn(mockTrainings);

        // Act
        List<Training> result = gymFacade.getTraineeTrainingsByCriteria(username, fromDate, toDate, trainerName, trainingType);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(traineeService, times(1)).selectTraineeByUsername(username);
        verify(traineeService, times(1)).getTraineeTrainingsByCriteria(username, fromDate, toDate, trainerName, trainingType);
    }



    @Test
    public void testSelectAllTrainings_Success() {
        // Arrange
        List<TrainingType> mockTrainingTypes = Arrays.asList(
                createMockTrainingType(1L, "Strength"),
                createMockTrainingType(2L, "Cardio")
        );

        when(trainingTypeService.findAllTrainings()).thenReturn(mockTrainingTypes);

        // Act
        List<TrainingType> result = gymFacade.selectAllTrainings();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Strength", result.get(0).getTrainingTypeName());
        assertEquals("Cardio", result.get(1).getTrainingTypeName());

        verify(trainingTypeService, times(1)).findAllTrainings();
    }

    @Test
    public void testSelectTrainingTypeByID_Success() {
        // Arrange
        Long id = 1L;
        TrainingType mockTrainingType = createMockTrainingType(id, "Strength");

        when(trainingTypeService.findTrainingTypeById(id)).thenReturn(Optional.of(mockTrainingType));

        // Act
        Optional<TrainingType> result = gymFacade.selectTrainingTypeByID(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Strength", result.get().getTrainingTypeName());

        verify(trainingTypeService, times(1)).findTrainingTypeById(id);
    }

    @Test
    public void testSelectTrainingTypeByName_Success() {
        // Arrange
        String name = "Cardio";
        TrainingType mockTrainingType = createMockTrainingType(2L, name);

        when(trainingTypeService.findTrainingTypeByTrainingTypeName(name)).thenReturn(Optional.of(mockTrainingType));

        // Act
        Optional<TrainingType> result = gymFacade.selectTrainingTypeByName(name);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(name, result.get().getTrainingTypeName());

        verify(trainingTypeService, times(1)).findTrainingTypeByTrainingTypeName(name);
    }
}