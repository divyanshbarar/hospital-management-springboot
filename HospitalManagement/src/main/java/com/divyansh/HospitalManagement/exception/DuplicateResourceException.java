package com.divyansh.HospitalManagement.exception;

// Custom Runtime Exception thrown when attempting to insert or update duplicate unique resources (e.g., duplicate email)
// Next Flow: Thrown by PatientService -> Intercepted by GlobalExceptionHandler (GCE) -> Mapped to HTTP 409 CONFLICT
public class DuplicateResourceException extends RuntimeException {

    // Constructor with descriptive error message
    public DuplicateResourceException(String message) {
        super(message);
    }
}
