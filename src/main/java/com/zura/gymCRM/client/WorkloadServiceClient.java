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
     * Creates or updates a workload entry for a trainer in the workload service
     */
    @PutMapping("/api/trainers/{username}/workloads/{year}/{month}")
    @CircuitBreaker(name = "workloadService", fallbackMethod = "updateWorkloadFallback")
    ResponseEntity<Void> updateWorkload(
            @PathVariable("username") String username,
            @PathVariable("year") int year,
            @PathVariable("month") int month,
            @RequestBody WorkloadRequest request,
            @RequestHeader("X-Transaction-ID") String transactionId);

    /**
     * Deletes a workload entry for a trainer in the workload service
     */
    @DeleteMapping("/api/trainers/{username}/workloads/{year}/{month}")
    @CircuitBreaker(name = "workloadService", fallbackMethod = "deleteWorkloadFallback")
    ResponseEntity<Void> deleteWorkload(
            @PathVariable("username") String username,
            @PathVariable("year") int year,
            @PathVariable("month") int month,
            @RequestHeader("X-Transaction-ID") String transactionId);

    /**
     * Adds to a workload entry for a trainer in the workload service
     */
    @PostMapping("/api/trainers/{username}/workloads/{year}/{month}/add")
    @CircuitBreaker(name = "workloadService", fallbackMethod = "addWorkloadFallback")
    ResponseEntity<Void> addWorkload(
            @PathVariable("username") String username,
            @PathVariable("year") int year,
            @PathVariable("month") int month,
            @RequestParam("duration") int duration,
            @RequestHeader("X-Transaction-ID") String transactionId);

    /**
     * Subtracts from a workload entry for a trainer in the workload service
     */
    @PostMapping("/api/trainers/{username}/workloads/{year}/{month}/subtract")
    @CircuitBreaker(name = "workloadService", fallbackMethod = "subtractWorkloadFallback")
    ResponseEntity<Void> subtractWorkload(
            @PathVariable("username") String username,
            @PathVariable("year") int year,
            @PathVariable("month") int month,
            @RequestParam("duration") int duration,
            @RequestHeader("X-Transaction-ID") String transactionId);

    // Fallback methods
    default ResponseEntity<Void> updateWorkloadFallback(
            String username, int year, int month, WorkloadRequest request,
            String transactionId, Exception e) {
        // Log fallback and potentially queue for retry
        return ResponseEntity.status(503).build(); // Service Unavailable
    }

    default ResponseEntity<Void> deleteWorkloadFallback(
            String username, int year, int month, String transactionId, Exception e) {
        // Log fallback and potentially queue for retry
        return ResponseEntity.status(503).build();
    }

    default ResponseEntity<Void> addWorkloadFallback(
            String username, int year, int month, int duration,
            String transactionId, Exception e) {
        // Log fallback and potentially queue for retry
        return ResponseEntity.status(503).build();
    }

    default ResponseEntity<Void> subtractWorkloadFallback(
            String username, int year, int month, int duration,
            String transactionId, Exception e) {
        // Log fallback and potentially queue for retry
        return ResponseEntity.status(503).build();
    }
}