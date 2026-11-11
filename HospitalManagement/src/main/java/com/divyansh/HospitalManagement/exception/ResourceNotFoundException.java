package com.divyansh.HospitalManagement.exception;

// Custom Runtime Exception thrown when a requested resource (e.g. Patient by ID) does not exist in the database
// Next Flow: Thrown by PatientService -> Intercepted by GlobalExceptionHandler (GCE) -> Mapped to HTTP 404 NOT_FOUND
public class ResourceNotFoundException extends RuntimeException {

    // Constructor with descriptive error message
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
