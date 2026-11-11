package com.divyansh.HospitalManagement.entity;

import com.divyansh.HospitalManagement.entity.type.BloodGroupType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

// JPA Entity: Represents the "patient" table in the database
// Next Flow: Managed by PatientRepository via Spring Data JPA to execute database operations
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(
        name = "patient",
        uniqueConstraints = {
                // Next Flow: Enforces unique constraint on email column in the database
                @UniqueConstraint(name = "unique_patient_email", columnNames = "email")
        },
        indexes = {
                // Next Flow: Creates a DB index on birth_date for faster date-range queries
                @Index(name = "idx_patient_birth_date", columnList = "birth_date")
        }
)
public class Patient {

    // Primary Key with auto-incrementing identity strategy in PostgreSQL
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Patient's full name
    @Column(nullable = false)
    private String name;

    // Patient's unique email address
    @Column(nullable = false, unique = true)
    private String email;

    // Patient's gender (e.g., MALE, FEMALE, OTHER)
    private String gender;

    // Patient's date of birth mapped to DB column "birth_date"
    @Column(name = "birth_date")
    private LocalDate birthDate;

    // Creation timestamp automatically set by Hibernate on first insert
    // Next Flow: Populated automatically by @CreationTimestamp when saved to DB
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = true)
    private LocalDateTime createdAt;

    // Blood group stored as Enum string in DB column "blood_group"
    @Enumerated(EnumType.STRING)
    @Column(name = "blood_group")
    private BloodGroupType bloodGroup;
}
