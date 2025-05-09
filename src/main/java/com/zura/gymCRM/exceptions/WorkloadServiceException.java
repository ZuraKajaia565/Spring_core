package com.zura.gymCRM.exceptions;

/**
 * Exception thrown when there's an error with the workload service
 */
public class WorkloadServiceException extends RuntimeException {

    public WorkloadServiceException(String message) {
        super(message);
    }

    public WorkloadServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}