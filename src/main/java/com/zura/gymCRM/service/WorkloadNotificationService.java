package com.zura.gymCRM.service;

import com.zura.gymCRM.dto.WorkloadUpdateDto;
import com.zura.gymCRM.security.JwtService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WorkloadNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(WorkloadNotificationService.class);

    @Value("${workload.service.url:http://localhost:8082/api/workload}")
    private String workloadServiceUrl;

    private final RestTemplate restTemplate;
    private final JwtService jwtService;

    @Autowired
    public WorkloadNotificationService(RestTemplate restTemplate, JwtService jwtService) {
        this.restTemplate = restTemplate;
        this.jwtService = jwtService;
    }

    @CircuitBreaker(name = "workloadService", fallbackMethod = "notifyWorkloadChangeFallback")
    public void notifyWorkloadChange(WorkloadUpdateDto request, UserDetails userDetails) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Generate JWT token for service-to-service communication
            if (userDetails != null) {
                String token = jwtService.generateToken(userDetails);
                headers.set("Authorization", "Bearer " + token);
                logger.debug("Added JWT token to workload service request");
            } else {
                logger.warn("No user details provided for workload notification");
            }

            // Add transaction ID for distributed tracing
            String transactionId = MDC.get("transactionId");
            if (transactionId != null && !transactionId.isEmpty()) {
                headers.set("X-Transaction-ID", transactionId);
                logger.debug("Added transaction ID to workload service request: {}", transactionId);
            }

            HttpEntity<WorkloadUpdateDto> entity = new HttpEntity<>(request, headers);

            logger.info("Sending notification to workload service for trainer: {}", request.getUsername());
            ResponseEntity<String> response = restTemplate.postForEntity(
                    workloadServiceUrl,
                    entity,
                    String.class);

            logger.info("Workload service notification successful with status: {}", response.getStatusCode());
        } catch (Exception e) {
            logger.error("Failed to notify workload service: {}", e.getMessage(), e);
            throw e; // Re-throw to trigger circuit breaker
        }
    }

    // Fallback method for circuit breaker
    public void notifyWorkloadChangeFallback(WorkloadUpdateDto request, UserDetails userDetails, Exception e) {
        logger.warn("Circuit breaker triggered for workload service notification. Request will be retried later. Error: {}", e.getMessage());
        // In a production system, you would queue the failed request for retry
    }
}