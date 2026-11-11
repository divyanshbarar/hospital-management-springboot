package com.divyansh.HospitalManagement.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

// Standardized Error Response Model: Sent back to client on any application error or exception
// Next Flow: Instantiated by GlobalExceptionHandler (GCE) -> Serialized into JSON response body
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    // Timestamp when the error occurred
    private LocalDateTime timestamp;

    // HTTP Status code (e.g., 400, 404, 409, 500)
    private int status;

    // HTTP Error phrase (e.g., "Not Found", "Bad Request", "Conflict")
    private String error;

    // Human-readable error description
    private String message;

    // The API URI request path that caused this error
    private String path;

    // Map of specific field validation errors (e.g., {"email": "Patient email is required"})
    private Map<String, String> validationErrors;
}
