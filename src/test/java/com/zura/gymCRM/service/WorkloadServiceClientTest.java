package com.zura.gymCRM.client;

import com.zura.gymCRM.dto.WorkloadRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkloadServiceClientTest {

    @Mock
    private WorkloadServiceClient workloadServiceClient;

    private final String username = "trainer1";
    private final int year = 2025;
    private final int month = 5;
    private final int duration = 60;
    private final String transactionId = "test-transaction-id";
    private WorkloadRequest workloadRequest;

    @BeforeEach
    void setUp() {
        workloadRequest = new WorkloadRequest();
        workloadRequest.setFirstName("John");
        workloadRequest.setLastName("Doe");
        workloadRequest.setActive(true);
        workloadRequest.setTrainingDuration(duration);
    }

    @Test
    void updateWorkload_Success() {
        // Arrange
        ResponseEntity<Void> mockResponse = ResponseEntity.ok().build();
        when(workloadServiceClient.updateWorkload(
                anyString(), anyInt(), anyInt(), any(WorkloadRequest.class), anyString()
        )).thenReturn(mockResponse);

        // Act
        ResponseEntity<Void> response = workloadServiceClient.updateWorkload(
                username, year, month, workloadRequest, transactionId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(workloadServiceClient).updateWorkload(
                username, year, month, workloadRequest, transactionId);
    }


    @Test
    void deleteWorkload_Success() {
        // Arrange
        ResponseEntity<Void> mockResponse = ResponseEntity.ok().build();
        when(workloadServiceClient.deleteWorkload(
                anyString(), anyInt(), anyInt(), anyString()
        )).thenReturn(mockResponse);

        // Act
        ResponseEntity<Void> response = workloadServiceClient.deleteWorkload(
                username, year, month, transactionId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(workloadServiceClient).deleteWorkload(
                username, year, month, transactionId);
    }


    @Test
    void addWorkload_Success() {
        // Arrange
        ResponseEntity<Void> mockResponse = ResponseEntity.ok().build();
        when(workloadServiceClient.addWorkload(
                anyString(), anyInt(), anyInt(), anyInt(), anyString()
        )).thenReturn(mockResponse);

        // Act
        ResponseEntity<Void> response = workloadServiceClient.addWorkload(
                username, year, month, duration, transactionId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(workloadServiceClient).addWorkload(
                username, year, month, duration, transactionId);
    }


}