package com.zura.gymCRM.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

/**
 * Request object for creating or updating trainer workload
 */
public class WorkloadRequest {

    @NotNull(message = "First name is required")
    private String firstName;

    @NotNull(message = "Last name is required")
    private String lastName;

    private boolean isActive;

    @NotNull(message = "Training date is required")
    private LocalDate trainingDate;

    @Positive(message = "Training duration must be positive")
    private int trainingDuration;

    // Default constructor
    public WorkloadRequest() {
    }

    // All-args constructor
    public WorkloadRequest(String firstName, String lastName, boolean isActive,
                           LocalDate trainingDate, int trainingDuration) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.isActive = isActive;
        this.trainingDate = trainingDate;
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

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDate getTrainingDate() {
        return trainingDate;
    }

    public void setTrainingDate(LocalDate trainingDate) {
        this.trainingDate = trainingDate;
    }

    public int getTrainingDuration() {
        return trainingDuration;
    }

    public void setTrainingDuration(int trainingDuration) {
        this.trainingDuration = trainingDuration;
    }
}