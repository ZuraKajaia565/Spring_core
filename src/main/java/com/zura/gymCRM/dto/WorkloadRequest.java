package com.zura.gymCRM.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Request object for creating or updating trainer workload
 */
public class WorkloadRequest {

    @NotNull(message = "First name is required")
    private String firstName;

    @NotNull(message = "Last name is required")
    private String lastName;

    private boolean isActive;

    @Positive(message = "Training duration must be positive")
    private int trainingDuration;

    // Default constructor
    public WorkloadRequest() {
    }

    // All-args constructor
    public WorkloadRequest(String firstName, String lastName, boolean isActive,
                           int trainingDuration) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.isActive = isActive;
        this.trainingDuration = trainingDuration;
    }

    // Getters and setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getTrainingDuration() {
        return trainingDuration;
    }

    public void setTrainingDuration(int trainingDuration) {
        this.trainingDuration = trainingDuration;
    }
}