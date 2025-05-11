package com.zura.gymCRM.messaging;

import java.io.Serializable;

/**
 * Message object for communicating with the workload service via ActiveMQ
 */
public class WorkloadMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String firstName;
    private String lastName;
    private boolean isActive;
    private int year;
    private int month;
    private int trainingDuration;
    private MessageType messageType;
    private String transactionId;

    public enum MessageType {
        CREATE_UPDATE, DELETE
    }

    // Default constructor for serialization
    public WorkloadMessage() {
    }

    public WorkloadMessage(String username, String firstName, String lastName,
                           boolean isActive, int year, int month,
                           int trainingDuration, MessageType messageType,
                           String transactionId) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isActive = isActive;
        this.year = year;
        this.month = month;
        this.trainingDuration = trainingDuration;
        this.messageType = messageType;
        this.transactionId = transactionId;
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getTrainingDuration() {
        return trainingDuration;
    }

    public void setTrainingDuration(int trainingDuration) {
        this.trainingDuration = trainingDuration;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public String toString() {
        return "WorkloadMessage{" +
                "username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", isActive=" + isActive +
                ", year=" + year +
                ", month=" + month +
                ", trainingDuration=" + trainingDuration +
                ", messageType=" + messageType +
                ", transactionId='" + transactionId + '\'' +
                '}';
    }
}