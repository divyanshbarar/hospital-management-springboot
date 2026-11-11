package com.divyansh.HospitalManagement.repository;

import com.divyansh.HospitalManagement.entity.Patient;
import com.divyansh.HospitalManagement.entity.type.BloodGroupType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Repository Layer: Spring Data JPA abstraction over the database for Patient entity
// Next Flow: Implements database operations dynamically at runtime and interacts directly with PostgreSQL
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    // Custom query method: Finds a patient by exact email address
    // Next Flow: Spring Data JPA generates: SELECT * FROM patient WHERE email = ?
    Optional<Patient> findByEmail(String email);

    // Checks whether an email already exists in the database
    // Next Flow: Spring Data JPA generates: SELECT EXISTS (SELECT 1 FROM patient WHERE email = ?)
    boolean existsByEmail(String email);

    // Checks whether an email exists for any other patient (used during update operations)
    // Next Flow: Spring Data JPA generates: SELECT EXISTS (SELECT 1 FROM patient WHERE email = ? AND id <> ?)
    boolean existsByEmailAndIdNot(String email, Long id);

    // Filters patients whose name contains search text (case-insensitive) with pagination & sorting
    // Next Flow: Spring Data JPA generates: SELECT * FROM patient WHERE LOWER(name) LIKE LOWER(?) LIMIT ? OFFSET ?
    Page<Patient> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Filters patients matching a specific blood group with pagination & sorting
    // Next Flow: Spring Data JPA generates: SELECT * FROM patient WHERE blood_group = ? LIMIT ? OFFSET ?
    Page<Patient> findByBloodGroup(BloodGroupType bloodGroup, Pageable pageable);

    // Filters patients matching both name search and blood group with pagination & sorting
    // Next Flow: Spring Data JPA generates: SELECT * FROM patient WHERE LOWER(name) LIKE LOWER(?) AND blood_group = ? LIMIT ? OFFSET ?
    Page<Patient> findByNameContainingIgnoreCaseAndBloodGroup(String name, BloodGroupType bloodGroup, Pageable pageable);
}
