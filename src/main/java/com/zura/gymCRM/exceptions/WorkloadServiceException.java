package com.zura.gymCRM.exceptions;

public class WorkloadServiceException extends RuntimeException {
    public WorkloadServiceException(String message) {
        super(message);
    }

    public WorkloadServiceException(String message, Throwable cause) {
        super(message, cause);
    }
} 