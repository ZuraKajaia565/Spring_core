package com.zura.gymCRM.dto;

public class TrainingTypeResponse {
    private Long trainingTypeId;
    private String trainingTypeName;

    public TrainingTypeResponse(Long trainingTypeId, String trainingTypeName) {
        this.trainingTypeId = trainingTypeId;
        this.trainingTypeName = trainingTypeName;
    }

    public Long getTrainingTypeId() { return trainingTypeId; }

    public String getTrainingTypeName() { return trainingTypeName; }
}

