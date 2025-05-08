package com.zura.gymCRM.client;

import com.zura.gymCRM.dto.WorkloadRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Feign client for the Workload Service
 */
@FeignClient(name = "workload-service")
public interface WorkloadServiceClient {

    /**
     * Creates a workload entry for a trainer in the workload service
     */
    @PostMapping("/api/workloads/trainers/{username}")
    @CircuitBreaker(name = "workloadService", fallbackMethod = "createTrainerWorkloadFallback")
    ResponseEntity<Void> createTrainerWorkload(
            @PathVariable("username") String username,
            @RequestBody WorkloadRequest request,
            @RequestHeader("X-Transaction-ID") String transactionId);

    /**
     * Updates a workload entry for a trainer in the workload service
     */
    @PutMapping("/api/workloads/trainers/{username}")
    @CircuitBreaker(name = "workloadService", fallbackMethod = "updateTrainerWorkloadFallback")
    ResponseEntity<Void> updateTrainerWorkload(
            @PathVariable("username") String username,
            @RequestBody WorkloadRequest request,
            @RequestHeader("X-Transaction-ID") String transactionId);

    /**
     * Deletes a workload entry for a trainer in the workload service
     */
    @DeleteMapping("/api/workloads/trainers/{username}/year/{year}/month/{month}")
    @CircuitBreaker(name = "workloadService", fallbackMethod = "deleteTrainerWorkloadFallback")
    ResponseEntity<Void> deleteTrainerWorkload(
            @PathVariable("username") String username,
            @PathVariable("year") int year,
            @PathVariable("month") int month,
            @RequestHeader("X-Transaction-ID") String transactionId);

    // Fallback methods
    default ResponseEntity<Void> createTrainerWorkloadFallback(
            String username, WorkloadRequest request, String transactionId, Exception e) {
        // Log fallback and potentially queue for retry
        return ResponseEntity.status(503).build(); // Service Unavailable
    }

    default ResponseEntity<Void> updateTrainerWorkloadFallback(
            String username, WorkloadRequest request, String transactionId, Exception e) {
        // Log fallback and potentially queue for retry
        return ResponseEntity.status(503).build();
    }

    default ResponseEntity<Void> deleteTrainerWorkloadFallback(
            String username, int year, int month, String transactionId, Exception e) {
        // Log fallback and potentially queue for retry
        return ResponseEntity.status(503).build();
    }
}