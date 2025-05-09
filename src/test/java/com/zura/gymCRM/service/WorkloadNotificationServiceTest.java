package com.zura.gymCRM.service;

import com.zura.gymCRM.client.WorkloadServiceClient;
import com.zura.gymCRM.dto.WorkloadRequest;
import com.zura.gymCRM.entities.Trainer;
import com.zura.gymCRM.entities.Training;
import com.zura.gymCRM.entities.TrainingType;
import com.zura.gymCRM.entities.User;
import com.zura.gymCRM.exceptions.WorkloadServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkloadNotificationServiceTest {

    @Mock
    private WorkloadServiceClient workloadServiceClient;

    @InjectMocks
    private WorkloadNotificationService workloadNotificationService;

    private Training training;
    private Trainer trainer;
    private User trainerUser;
    private final String username = "trainer1";
    private final int trainingDuration = 60;
    private final Date trainingDate = new Date();

    @BeforeEach
    void setUp() {
        // Set up test data
        trainerUser = new User();
        trainerUser.setUsername(username);
        trainerUser.setFirstName("John");
        trainerUser.setLastName("Doe");
        trainerUser.setIsActive(true);

        TrainingType trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName("Strength");

        trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUser(trainerUser);
        trainer.setSpecialization(trainingType);

        training = new Training();
        training.setId(1L);
        training.setTrainer(trainer);
        training.setTrainingName("Test Training");
        training.setTrainingType(trainingType);
        training.setTrainingDate(trainingDate);
        training.setTrainingDuration(trainingDuration);
    }

    @Test
    void notifyTrainingCreated_Success() {
        // Arrange
        ResponseEntity<Void> successResponse = new ResponseEntity<>(HttpStatus.OK);

        when(workloadServiceClient.updateWorkload(
                eq(username),
                anyInt(), // year
                anyInt(), // month
                any(WorkloadRequest.class),
                anyString() // transactionId
        )).thenReturn(successResponse);

        // Act & Assert
        assertDoesNotThrow(() -> workloadNotificationService.notifyTrainingCreated(training));

        verify(workloadServiceClient).updateWorkload(
                eq(username),
                eq(LocalDate.ofInstant(trainingDate.toInstant(), ZoneId.systemDefault()).getYear()),
                eq(LocalDate.ofInstant(trainingDate.toInstant(), ZoneId.systemDefault()).getMonthValue()),
                any(WorkloadRequest.class),
                anyString()
        );
    }

    @Test
    void notifyTrainingCreated_FailureResponse() {
        // Arrange
        ResponseEntity<Void> failureResponse = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        when(workloadServiceClient.updateWorkload(
                anyString(),
                anyInt(),
                anyInt(),
                any(WorkloadRequest.class),
                anyString()
        )).thenReturn(failureResponse);

        // Act & Assert
        WorkloadServiceException exception = assertThrows(WorkloadServiceException.class,
                () -> workloadNotificationService.notifyTrainingCreated(training));

        assertTrue(exception.getMessage().contains("Failed to notify workload service"));

        verify(workloadServiceClient).updateWorkload(
                anyString(),
                anyInt(),
                anyInt(),
                any(WorkloadRequest.class),
                anyString()
        );
    }

    @Test
    void notifyTrainingCreated_Exception() {
        // Arrange
        when(workloadServiceClient.updateWorkload(
                anyString(),
                anyInt(),
                anyInt(),
                any(WorkloadRequest.class),
                anyString()
        )).thenThrow(new RuntimeException("Test exception"));

        // Act & Assert
        WorkloadServiceException exception = assertThrows(WorkloadServiceException.class,
                () -> workloadNotificationService.notifyTrainingCreated(training));

        assertTrue(exception.getMessage().contains("Failed to notify workload service"));
        assertTrue(exception.getCause().getMessage().contains("Test exception"));

        verify(workloadServiceClient).updateWorkload(
                anyString(),
                anyInt(),
                anyInt(),
                any(WorkloadRequest.class),
                anyString()
        );
    }

    @Test
    void notifyTrainingUpdated_Success() {
        // Arrange
        ResponseEntity<Void> successResponse = new ResponseEntity<>(HttpStatus.OK);

        when(workloadServiceClient.updateWorkload(
                eq(username),
                anyInt(),
                anyInt(),
                any(WorkloadRequest.class),
                anyString()
        )).thenReturn(successResponse);

        // Act & Assert
        assertDoesNotThrow(() -> workloadNotificationService.notifyTrainingUpdated(training));

        verify(workloadServiceClient).updateWorkload(
                eq(username),
                eq(LocalDate.ofInstant(trainingDate.toInstant(), ZoneId.systemDefault()).getYear()),
                eq(LocalDate.ofInstant(trainingDate.toInstant(), ZoneId.systemDefault()).getMonthValue()),
                any(WorkloadRequest.class),
                anyString()
        );
    }

    @Test
    void notifyTrainingDeleted_Success() {
        // Arrange
        ResponseEntity<Void> successResponse = new ResponseEntity<>(HttpStatus.OK);

        when(workloadServiceClient.deleteWorkload(
                eq(username),
                anyInt(),
                anyInt(),
                anyString()
        )).thenReturn(successResponse);

        // Act & Assert
        assertDoesNotThrow(() -> workloadNotificationService.notifyTrainingDeleted(training));

        verify(workloadServiceClient).deleteWorkload(
                eq(username),
                eq(LocalDate.ofInstant(trainingDate.toInstant(), ZoneId.systemDefault()).getYear()),
                eq(LocalDate.ofInstant(trainingDate.toInstant(), ZoneId.systemDefault()).getMonthValue()),
                anyString()
        );
    }

    @Test
    void notifyTrainingDeleted_FailureResponse() {
        // Arrange
        ResponseEntity<Void> failureResponse = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        when(workloadServiceClient.deleteWorkload(
                anyString(),
                anyInt(),
                anyInt(),
                anyString()
        )).thenReturn(failureResponse);

        // Act & Assert
        WorkloadServiceException exception = assertThrows(WorkloadServiceException.class,
                () -> workloadNotificationService.notifyTrainingDeleted(training));

        assertTrue(exception.getMessage().contains("Failed to notify workload service"));

        verify(workloadServiceClient).deleteWorkload(
                anyString(),
                anyInt(),
                anyInt(),
                anyString()
        );
    }

    @Test
    void notifyTrainingDeleted_Exception() {
        // Arrange
        when(workloadServiceClient.deleteWorkload(
                anyString(),
                anyInt(),
                anyInt(),
                anyString()
        )).thenThrow(new RuntimeException("Test exception"));

        // Act & Assert
        WorkloadServiceException exception = assertThrows(WorkloadServiceException.class,
                () -> workloadNotificationService.notifyTrainingDeleted(training));

        assertTrue(exception.getMessage().contains("Failed to notify workload service"));
        assertTrue(exception.getCause().getMessage().contains("Test exception"));

        verify(workloadServiceClient).deleteWorkload(
                anyString(),
                anyInt(),
                anyInt(),
                anyString()
        );
    }
}