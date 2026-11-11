package com.divyansh.HospitalManagement.service;

import com.divyansh.HospitalManagement.dto.PageResponseDto;
import com.divyansh.HospitalManagement.dto.PatientRequestDto;
import com.divyansh.HospitalManagement.dto.PatientResponseDto;
import com.divyansh.HospitalManagement.entity.Patient;
import com.divyansh.HospitalManagement.entity.type.BloodGroupType;
import com.divyansh.HospitalManagement.exception.DuplicateResourceException;
import com.divyansh.HospitalManagement.exception.ResourceNotFoundException;
import com.divyansh.HospitalManagement.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Service Layer: Implements business logic, validation rules, ModelMapper conversions, and transaction management
// Next Flow: Orchestrates between PatientController and PatientRepository
@Service
@RequiredArgsConstructor
public class PatientService {

    // Injected repository dependency for database queries
    private final PatientRepository patientRepository;

    // Injected ModelMapper dependency to convert between DTOs and Entities without boilerplate
    private final ModelMapper modelMapper;

    // Creates a new patient after verifying uniqueness of email
    // Next Flow: Validates business rules -> Uses ModelMapper to convert DTO to Entity -> Calls PatientRepository.save() -> Maps to Response DTO
    @Transactional
    public PatientResponseDto createPatient(PatientRequestDto requestDto) {
        // Business Rule: Ensure email is not already taken
        if (patientRepository.existsByEmail(requestDto.getEmail())) {
            throw new DuplicateResourceException("Patient with email '" + requestDto.getEmail() + "' already exists");
        }

        // ModelMapper Flow: Automatically copies matching fields from PatientRequestDto to Patient Entity
        Patient patient = modelMapper.map(requestDto, Patient.class);

        // Next Flow: Persists entity in PostgreSQL via PatientRepository
        Patient savedPatient = patientRepository.save(patient);

        // ModelMapper Flow: Automatically copies matching fields from persisted Entity to PatientResponseDto
        return modelMapper.map(savedPatient, PatientResponseDto.class);
    }

    // Retrieves a single patient by ID or throws ResourceNotFoundException if absent
    // Next Flow: Calls PatientRepository.findById() -> Maps to Response DTO via ModelMapper -> Returns to Controller
    @Transactional(readOnly = true)
    public PatientResponseDto getPatientById(Long id) {
        // Next Flow: Queries DB via PatientRepository; if empty, triggers ResourceNotFoundException handled by GCE
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));

        // ModelMapper Flow: Converts found Entity to PatientResponseDto
        return modelMapper.map(patient, PatientResponseDto.class);
    }

    // Fetches patients supporting filtering (name, bloodGroup), sorting (field + dir), and pagination (page + size)
    // Next Flow: Configures Pageable -> Queries PatientRepository -> Maps each entity via ModelMapper -> Constructs PageResponseDto
    @Transactional(readOnly = true)
    public PageResponseDto<PatientResponseDto> getAllPatients(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String name,
            BloodGroupType bloodGroup
    ) {
        // Configure dynamic sorting: ascending or descending
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.DESC.name())
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        // Create Pageable instance with requested page index, size, and sorting parameters
        Pageable pageable = PageRequest.of(page, size, sort);

        // Execute dynamic filtering query based on provided filter criteria
        Page<Patient> patientPage;
        if (name != null && !name.trim().isEmpty() && bloodGroup != null) {
            // Next Flow: Executes query filtered by both name search and blood group
            patientPage = patientRepository.findByNameContainingIgnoreCaseAndBloodGroup(name.trim(), bloodGroup, pageable);
        } else if (name != null && !name.trim().isEmpty()) {
            // Next Flow: Executes query filtered by name search only
            patientPage = patientRepository.findByNameContainingIgnoreCase(name.trim(), pageable);
        } else if (bloodGroup != null) {
            // Next Flow: Executes query filtered by blood group only
            patientPage = patientRepository.findByBloodGroup(bloodGroup, pageable);
        } else {
            // Next Flow: Executes standard paginated and sorted query without filters
            patientPage = patientRepository.findAll(pageable);
        }

        // ModelMapper Flow: Converts each Patient Entity in the Page stream to PatientResponseDto
        List<PatientResponseDto> content = patientPage.getContent()
                .stream()
                .map(patient -> modelMapper.map(patient, PatientResponseDto.class))
                .toList();

        // Wrap results into PageResponseDto containing data and pagination metadata
        return PageResponseDto.<PatientResponseDto>builder()
                .content(content)
                .pageNumber(patientPage.getNumber())
                .pageSize(patientPage.getSize())
                .totalElements(patientPage.getTotalElements())
                .totalPages(patientPage.getTotalPages())
                .isFirst(patientPage.isFirst())
                .isLast(patientPage.isLast())
                .build();
    }

    // Updates existing patient details by ID
    // Next Flow: Validates existence -> Checks email conflict -> ModelMapper updates fields -> Saves to DB -> Returns Response DTO
    @Transactional
    public PatientResponseDto updatePatient(Long id, PatientRequestDto requestDto) {
        // Next Flow: Check if patient exists; if not, throw ResourceNotFoundException
        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot update. Patient not found with id: " + id));

        // Business Rule: Check if updated email belongs to another patient
        if (patientRepository.existsByEmailAndIdNot(requestDto.getEmail(), id)) {
            throw new DuplicateResourceException("Email '" + requestDto.getEmail() + "' is already in use by another patient");
        }

        // ModelMapper Flow: Maps updated fields from Request DTO directly into existing Patient Entity
        modelMapper.map(requestDto, existingPatient);

        // Next Flow: Saves updated entity to PostgreSQL database via PatientRepository
        Patient updatedPatient = patientRepository.save(existingPatient);

        // ModelMapper Flow: Converts updated Entity to PatientResponseDto
        return modelMapper.map(updatedPatient, PatientResponseDto.class);
    }

    // Deletes a patient by ID
    // Next Flow: Verifies patient exists -> Calls PatientRepository.delete() -> Deletes record from DB
    @Transactional
    public void deletePatient(Long id) {
        // Next Flow: Validates existence before deletion
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot delete. Patient not found with id: " + id));

        // Next Flow: PatientRepository executes DELETE SQL in PostgreSQL
        patientRepository.delete(patient);
    }
}
