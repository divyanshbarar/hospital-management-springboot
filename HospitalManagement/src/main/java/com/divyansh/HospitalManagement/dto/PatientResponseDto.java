package com.divyansh.HospitalManagement.dto;

import com.divyansh.HospitalManagement.entity.type.BloodGroupType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

// Response DTO: Formats and exposes only safe, clean patient data to the client
// Next Flow: Constructed in PatientService from Patient entity -> Returned to PatientController -> Sent as JSON to client
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientResponseDto {

    // Unique database generated ID of the patient
    private Long id;

    // Full name of the patient
    private String name;

    // Patient's email address
    private String email;

    // Patient's gender
    private String gender;

    // Patient's birth date
    private LocalDate birthDate;

    // Assigned blood group
    private BloodGroupType bloodGroup;

    // Audit timestamp when the record was inserted into the database
    private LocalDateTime createdAt;
}
