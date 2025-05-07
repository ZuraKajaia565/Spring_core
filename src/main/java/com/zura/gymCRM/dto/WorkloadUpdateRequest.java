package com.zura.gymCRM.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Request object for updating trainer workload in the workload service
 */
public class WorkloadUpdateRequest {
    @Setter
    @Getter
    private String username;
    @Setter
    @Getter
    private String firstName;
    @Setter
    @Getter
    private String lastName;
    private boolean isActive;
    @Setter
    @Getter
    private LocalDate trainingDate;
    @Setter
    @Getter
    private int trainingDuration;
    @Setter
    @Getter
    private ActionType actionType;

    public enum ActionType {
        ADD, DELETE
    }

    public WorkloadUpdateRequest() {
    }

    public WorkloadUpdateRequest(String username, String firstName, String lastName, boolean isActive,
                                 LocalDate trainingDate, int trainingDuration, ActionType actionType) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isActive = isActive;
        this.trainingDate = trainingDate;
        this.trainingDuration = trainingDuration;
        this.actionType = actionType;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

}