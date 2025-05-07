package com.zura.gymCRM.client;

import com.zura.gymCRM.dto.WorkloadUpdateRequest;
import com.zura.gymCRM.entities.Training;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Component
public class WorkloadServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(WorkloadServiceClient.class);

    private final RestTemplate restTemplate;

    @Value("${workload.service.url}")
    private String workloadServiceUrl;

    @Autowired
    public WorkloadServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Sends a request to the workload service to update a trainer's workload
     */
    @CircuitBreaker(name = "workloadService", fallbackMethod = "updateTrainerWorkloadFallback")
    public void updateTrainerWorkload(Training training, WorkloadUpdateRequest.ActionType actionType) {
        String transactionId = MDC.get("transactionId");
        if (transactionId == null) {
            transactionId = UUID.randomUUID().toString();
            MDC.put("transactionId", transactionId);
        }

        logger.info("Transaction ID: {} - Sending workload update to workload service for trainer: {}",
                transactionId, training.getTrainer().getUser().getUsername());

        try {
            // Get JWT token from security context
            String token = getCurrentUserToken();

            // Create request headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.set("X-Transaction-ID", transactionId);

            // Create workload update request
            WorkloadUpdateRequest request = createWorkloadRequest(training, actionType);

            // Create HTTP entity with headers and body
            HttpEntity<WorkloadUpdateRequest> requestEntity = new HttpEntity<>(request, headers);

            // Send request to workload service
            ResponseEntity<String> response = restTemplate.exchange(
                    workloadServiceUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class);

            logger.info("Transaction ID: {} - Workload service response: {} - {}",
                    transactionId, response.getStatusCode(), response.getBody());
        } catch (Exception e) {
            logger.error("Transaction ID: {} - Error sending workload update: {}",
                    transactionId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Fallback method for circuit breaker
     */
    public void updateTrainerWorkloadFallback(Training training, WorkloadUpdateRequest.ActionType actionType, Exception e) {
        String transactionId = MDC.get("transactionId");
        logger.error("Transaction ID: {} - Circuit breaker fallback: Failed to update trainer workload - {}",
                transactionId, e.getMessage());

        // Log the failed operation for later retry
        logger.info("Transaction ID: {} - Queuing failed workload update for later processing: Trainer={}, Action={}",
                transactionId, training.getTrainer().getUser().getUsername(), actionType);
    }

    /**
     * Creates a workload update request from a training entity
     */
    private WorkloadUpdateRequest createWorkloadRequest(Training training, WorkloadUpdateRequest.ActionType actionType) {
        // Convert Java util Date to LocalDate
        LocalDate trainingDate = convertToLocalDate(training.getTrainingDate());

        return new WorkloadUpdateRequest(
                training.getTrainer().getUser().getUsername(),
                training.getTrainer().getUser().getFirstName(),
                training.getTrainer().getUser().getLastName(),
                training.getTrainer().getUser().getIsActive(),
                trainingDate,
                training.getTrainingDuration(),
                actionType
        );
    }

    /**
     * Gets the current user's JWT token from the security context
     */
    private String getCurrentUserToken() {
        // Get authentication from security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // In a real application, you would have a mechanism to retrieve the actual token
        // This is a simplified placeholder implementation
        return "jwt-token-placeholder";
    }

    /**
     * Converts java.util.Date to java.time.LocalDate
     */
    private LocalDate convertToLocalDate(Date dateToConvert) {
        return new java.sql.Date(dateToConvert.getTime()).toLocalDate();
    }
}