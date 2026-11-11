package com.divyansh.HospitalManagement.dto;

import com.divyansh.HospitalManagement.entity.type.BloodGroupType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDate;

// Request DTO: Captures incoming JSON payload from the client for creating/updating a Patient
// Next Flow: Received by PatientController (@Valid @RequestBody) -> Passed to PatientService
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientRequestDto {

    // Ensures name is provided and not just whitespace
    @NotBlank(message = "Patient name is required and cannot be blank")
    private String name;

    // Validates standard email pattern and non-blank requirement
    @NotBlank(message = "Patient email is required")
    @Email(message = "Please provide a valid email format (e.g., user@example.com)")
    private String email;

    // Gender string (e.g., MALE, FEMALE, OTHER)
    @NotBlank(message = "Gender is required")
    private String gender;

    // Date of birth must be a past date
    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    // Blood group must be one of the BloodGroupType enum constants
    @NotNull(message = "Blood group is required")
    private BloodGroupType bloodGroup;
}
