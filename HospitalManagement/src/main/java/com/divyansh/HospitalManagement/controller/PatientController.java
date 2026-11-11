package com.divyansh.HospitalManagement.controller;

import com.divyansh.HospitalManagement.dto.PageResponseDto;
import com.divyansh.HospitalManagement.dto.PatientRequestDto;
import com.divyansh.HospitalManagement.dto.PatientResponseDto;
import com.divyansh.HospitalManagement.entity.type.BloodGroupType;
import com.divyansh.HospitalManagement.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// Controller Layer: Exposes RESTful HTTP endpoints, validates client requests, and sends HTTP responses
// Next Flow: Intercepts client HTTP requests -> Validates via @Valid -> Forwards request to PatientService
@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {

    // Service dependency injected automatically by Spring IOC
    private final PatientService patientService;

    // FLOW STEP 1: Create a new Patient
    // Next Flow: Validates request JSON with @Valid -> Delegates to PatientService.createPatient() -> Returns 201 Created
    @PostMapping
    public ResponseEntity<PatientResponseDto> createPatient(@Valid @RequestBody PatientRequestDto requestDto) {
        // Next Flow: Passes validated DTO to the Service layer
        PatientResponseDto createdPatient = patientService.createPatient(requestDto);
        return new ResponseEntity<>(createdPatient, HttpStatus.CREATED);
    }

    // FLOW STEP 1: Fetch a single Patient by ID
    // Next Flow: Delegates to PatientService.getPatientById() -> Returns 200 OK (or 404 via GCE if not found)
    @GetMapping("/{id}")
    public ResponseEntity<PatientResponseDto> getPatientById(@PathVariable Long id) {
        // Next Flow: Calls Service layer to look up patient
        PatientResponseDto patient = patientService.getPatientById(id);
        return ResponseEntity.ok(patient);
    }

    // FLOW STEP 1: Fetch all Patients with Pagination, Sorting, and Filtering
    // Next Flow: Extracts query params -> Calls PatientService.getAllPatients() -> Returns 200 OK with PageResponseDto
    @GetMapping
    public ResponseEntity<PageResponseDto<PatientResponseDto>> getAllPatients(
            // Pagination: 0-indexed page number (default: 0)
            @RequestParam(defaultValue = "0") int page,
            // Pagination: Number of records per page (default: 10)
            @RequestParam(defaultValue = "10") int size,
            // Sorting: Field to sort by (e.g. "name", "id", "birthDate")
            @RequestParam(defaultValue = "id") String sortBy,
            // Sorting direction: "asc" or "desc" (default: "asc")
            @RequestParam(defaultValue = "asc") String sortDir,
            // Filtering: Optional partial name search (case-insensitive)
            @RequestParam(required = false) String name,
            // Filtering: Optional blood group enum match (e.g. "A_POSITIVE")
            @RequestParam(required = false) BloodGroupType bloodGroup
    ) {
        // Next Flow: Forwards pagination, sorting, and filter params to Service layer
        PageResponseDto<PatientResponseDto> response = patientService.getAllPatients(page, size, sortBy, sortDir, name, bloodGroup);
        return ResponseEntity.ok(response);
    }

    // FLOW STEP 1: Update an existing Patient by ID
    // Next Flow: Validates payload with @Valid -> Delegates to PatientService.updatePatient() -> Returns 200 OK
    @PutMapping("/{id}")
    public ResponseEntity<PatientResponseDto> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody PatientRequestDto requestDto
    ) {
        // Next Flow: Passes ID and update payload to Service layer
        PatientResponseDto updatedPatient = patientService.updatePatient(id, requestDto);
        return ResponseEntity.ok(updatedPatient);
    }

    // FLOW STEP 1: Delete a Patient by ID
    // Next Flow: Delegates to PatientService.deletePatient() -> Returns 200 OK with confirmation message
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletePatient(@PathVariable Long id) {
        // Next Flow: Calls Service layer to perform deletion
        patientService.deletePatient(id);
        return ResponseEntity.ok(Map.of("message", "Patient successfully deleted with id: " + id));
    }
}
