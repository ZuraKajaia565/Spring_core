package com.zura.gymCRM.controller;

import com.zura.gymCRM.entities.Trainee;
import com.zura.gymCRM.entities.Trainer;
import com.zura.gymCRM.entities.Training;
import com.zura.gymCRM.entities.TrainingType;
import com.zura.gymCRM.exceptions.NotFoundException;
import com.zura.gymCRM.facade.GymFacade;
import com.zura.gymCRM.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainingControllerTest {

    @Mock
    private GymFacade gymFacade;

    @Mock
    private TrainingService trainingService;

    @InjectMocks
    private TrainingController trainingController;

    private Trainee trainee;
    private Trainer trainer;
    private TrainingType trainingType;
    private Training training;

    private final String traineeUsername = "trainee1";
    private final String trainerUsername = "trainer1";
    private final String trainingName = "Test Training";
    private final Date trainingDate = new Date();
    private final int trainingDuration = 60;
    private final Long trainingId = 1L;

    @BeforeEach
    void setUp() {
        // Set up test data
        trainee = new Trainee();
        trainee.setId(1L);

        trainer = new Trainer();
        trainer.setId(1L);

        trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName("Strength");

        training = new Training();
        training.setId(trainingId);
        training.setTrainer(trainer);
        training.setTrainee(trainee);
        training.setTrainingType(trainingType);
        training.setTrainingName(trainingName);
        training.setTrainingDate(trainingDate);
        training.setTrainingDuration(trainingDuration);
    }

    @Test
    void addTraining_Success() {
        // Arrange
        when(gymFacade.selectTraineeByusername(traineeUsername)).thenReturn(Optional.of(trainee));
        when(gymFacade.selectTrainerByUsername(trainerUsername)).thenReturn(Optional.of(trainer));
        when(gymFacade.addTraining(any(), any(), anyString(), any(), any(), anyInt())).thenReturn(training);

        // Act
        ResponseEntity<?> response = trainingController.addTraining(
                traineeUsername, trainerUsername, trainingName, trainingDate, trainingDuration);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Training added successfully", response.getBody());

        verify(gymFacade).selectTraineeByusername(traineeUsername);
        verify(gymFacade).selectTrainerByUsername(trainerUsername);
        verify(gymFacade).addTraining(trainee, trainer, trainingName, null, trainingDate, trainingDuration);
    }

    @Test
    void addTraining_TraineeNotFound() {
        // Arrange
        when(gymFacade.selectTraineeByusername(traineeUsername)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = trainingController.addTraining(
                traineeUsername, trainerUsername, trainingName, trainingDate, trainingDuration);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Trainee not found", response.getBody());

        verify(gymFacade).selectTraineeByusername(traineeUsername);
        verify(gymFacade, never()).selectTrainerByUsername(anyString());
        verify(gymFacade, never()).addTraining(any(), any(), anyString(), any(), any(), anyInt());
    }

    @Test
    void addTraining_TrainerNotFound() {
        // Arrange
        when(gymFacade.selectTraineeByusername(traineeUsername)).thenReturn(Optional.of(trainee));
        when(gymFacade.selectTrainerByUsername(trainerUsername)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = trainingController.addTraining(
                traineeUsername, trainerUsername, trainingName, trainingDate, trainingDuration);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Trainer not found", response.getBody());

        verify(gymFacade).selectTraineeByusername(traineeUsername);
        verify(gymFacade).selectTrainerByUsername(trainerUsername);
        verify(gymFacade, never()).addTraining(any(), any(), anyString(), any(), any(), anyInt());
    }

    @Test
    void addTraining_Exception() {
        // Arrange
        when(gymFacade.selectTraineeByusername(traineeUsername)).thenReturn(Optional.of(trainee));
        when(gymFacade.selectTrainerByUsername(trainerUsername)).thenReturn(Optional.of(trainer));
        when(gymFacade.addTraining(any(), any(), anyString(), any(), any(), anyInt()))
                .thenThrow(new RuntimeException("Test exception"));

        // Act
        ResponseEntity<?> response = trainingController.addTraining(
                traineeUsername, trainerUsername, trainingName, trainingDate, trainingDuration);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Error occurred while adding training"));

        verify(gymFacade).selectTraineeByusername(traineeUsername);
        verify(gymFacade).selectTrainerByUsername(trainerUsername);
        verify(gymFacade).addTraining(trainee, trainer, trainingName, null, trainingDate, trainingDuration);
    }

    @Test
    void updateTraining_Success() {
        // Arrange
        when(trainingService.getTraining(trainingId)).thenReturn(Optional.of(training));
        when(gymFacade.selectTraineeByusername(traineeUsername)).thenReturn(Optional.of(trainee));
        when(gymFacade.selectTrainerByUsername(trainerUsername)).thenReturn(Optional.of(trainer));
        when(trainingService.updateTraining(any(Training.class))).thenReturn(training);

        // Act
        ResponseEntity<?> response = trainingController.updateTraining(
                trainingId, traineeUsername, trainerUsername, trainingName, trainingDate, trainingDuration);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Training updated successfully", response.getBody());

        verify(trainingService).getTraining(trainingId);
        verify(gymFacade).selectTraineeByusername(traineeUsername);
        verify(gymFacade).selectTrainerByUsername(trainerUsername);
        verify(trainingService).updateTraining(any(Training.class));
    }

    @Test
    void updateTraining_TrainingNotFound() {
        // Arrange
        when(trainingService.getTraining(trainingId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = trainingController.updateTraining(
                trainingId, traineeUsername, trainerUsername, trainingName, trainingDate, trainingDuration);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(trainingService).getTraining(trainingId);
        verify(gymFacade, never()).selectTraineeByusername(anyString());
        verify(gymFacade, never()).selectTrainerByUsername(anyString());
        verify(trainingService, never()).updateTraining(any(Training.class));
    }

    @Test
    void deleteTraining_Success() {
        // Arrange
        doNothing().when(trainingService).deleteTraining(trainingId);

        // Act
        ResponseEntity<?> response = trainingController.deleteTraining(trainingId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Training deleted successfully", response.getBody());

        verify(trainingService).deleteTraining(trainingId);
    }

    @Test
    void deleteTraining_NotFound() {
        // Arrange
        doThrow(new NotFoundException("Training not found"))
                .when(trainingService).deleteTraining(trainingId);

        // Act
        ResponseEntity<?> response = trainingController.deleteTraining(trainingId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(trainingService).deleteTraining(trainingId);
    }

    @Test
    void deleteTraining_Exception() {
        // Arrange
        doThrow(new RuntimeException("Test exception"))
                .when(trainingService).deleteTraining(trainingId);

        // Act
        ResponseEntity<?> response = trainingController.deleteTraining(trainingId);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Error occurred while deleting training"));

        verify(trainingService).deleteTraining(trainingId);
    }
}