package com.zura.gymCRM;

import com.zura.gymCRM.dao.TrainingRepository;
import com.zura.gymCRM.entities.Trainee;
import com.zura.gymCRM.entities.Trainer;
import com.zura.gymCRM.entities.Training;
import com.zura.gymCRM.entities.TrainingType;
import com.zura.gymCRM.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainingServiceTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private WorkloadNotificationService workloadNotificationService;

    @InjectMocks
    private TrainingService trainingService;

    private Training training;
    private Trainer trainer;
    private Trainee trainee;
    private TrainingType trainingType;
    private final Long trainingId = 1L;

    @BeforeEach
    void setUp() {
        // Set up common test data
        trainer = new Trainer();
        trainer.setId(1L);

        trainee = new Trainee();
        trainee.setId(1L);

        trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName("Strength");

        training = new Training();
        training.setId(trainingId);
        training.setTrainer(trainer);
        training.setTrainee(trainee);
        training.setTrainingType(trainingType);
        training.setTrainingName("Test Training");
        training.setTrainingDate(new Date());
        training.setTrainingDuration(60);
    }

    @Test
    void createTraining_Success() {
        // Arrange
        when(trainingRepository.save(any(Training.class))).thenReturn(training);
        doNothing().when(workloadNotificationService).notifyTrainingCreated(any(Training.class));

        // Act
        Training result = trainingService.createTraining(training);

        // Assert
        assertNotNull(result);
        assertEquals(trainingId, result.getId());
        verify(trainingRepository).save(training);
        verify(workloadNotificationService).notifyTrainingCreated(training);
    }

    @Test
    void updateTraining_Success() {
        // Arrange
        when(trainingRepository.existsById(trainingId)).thenReturn(true);
        when(trainingRepository.save(any(Training.class))).thenReturn(training);
        doNothing().when(workloadNotificationService).notifyTrainingUpdated(any(Training.class));

        // Act
        Training result = trainingService.updateTraining(training);

        // Assert
        assertNotNull(result);
        assertEquals(trainingId, result.getId());
        verify(trainingRepository).existsById(trainingId);
        verify(trainingRepository).save(training);
        verify(workloadNotificationService).notifyTrainingUpdated(training);
    }

    @Test
    void updateTraining_NotFound() {
        // Arrange
        when(trainingRepository.existsById(trainingId)).thenReturn(false);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> trainingService.updateTraining(training));
        verify(trainingRepository).existsById(trainingId);
        verify(trainingRepository, never()).save(any(Training.class));
        verify(workloadNotificationService, never()).notifyTrainingUpdated(any(Training.class));
    }

    @Test
    void deleteTraining_Success() {
        // Arrange
        when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(training));
        doNothing().when(trainingRepository).delete(any(Training.class));
        doNothing().when(workloadNotificationService).notifyTrainingDeleted(any(Training.class));

        // Act
        trainingService.deleteTraining(trainingId);

        // Assert
        verify(trainingRepository).findById(trainingId);
        verify(trainingRepository).delete(training);
        verify(workloadNotificationService).notifyTrainingDeleted(training);
    }

    @Test
    void deleteTraining_NotFound() {
        // Arrange
        when(trainingRepository.findById(trainingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> trainingService.deleteTraining(trainingId));
        verify(trainingRepository).findById(trainingId);
        verify(trainingRepository, never()).delete(any(Training.class));
        verify(workloadNotificationService, never()).notifyTrainingDeleted(any(Training.class));
    }

    @Test
    void getTraining_Success() {
        // Arrange
        when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(training));

        // Act
        Optional<Training> result = trainingService.getTraining(trainingId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(trainingId, result.get().getId());
        verify(trainingRepository).findById(trainingId);
    }

    @Test
    void getTraining_NotFound() {
        // Arrange
        when(trainingRepository.findById(trainingId)).thenReturn(Optional.empty());

        // Act
        Optional<Training> result = trainingService.getTraining(trainingId);

        // Assert
        assertFalse(result.isPresent());
        verify(trainingRepository).findById(trainingId);
    }
}