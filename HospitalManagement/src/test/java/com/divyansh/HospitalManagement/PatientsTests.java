package com.divyansh.HospitalManagement;

import com.divyansh.HospitalManagement.entity.Patient;
import com.divyansh.HospitalManagement.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;

// Direct Repository Integration Test: Verifies interaction between Spring Data JPA Repository and Database
@SpringBootTest
public class PatientsTests {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testPatientRepository() {
        // Query all patients from PostgreSQL database
        List<Patient> patientList = patientRepository.findAll();
        System.out.println("Fetched patients count: " + patientList.size());
        assertFalse(patientList.isEmpty(), "Patient list should not be empty after data.sql initialization");
    }

    @Test
    public void testCheckDbColumnsAndBloodGroups() {
        List<Map<String, Object>> columns = jdbcTemplate.queryForList(
                "SELECT column_name, data_type, udt_name FROM information_schema.columns WHERE table_name = 'patient'"
        );
        System.out.println("=== PATIENT TABLE COLUMNS IN POSTGRES ===");
        for (Map<String, Object> col : columns) {
            System.out.println(col);
        }

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT id, name, blood_group FROM patient"
        );
        System.out.println("=== PATIENT TABLE ROWS (ID, NAME, BLOOD_GROUP) ===");
        for (Map<String, Object> row : rows) {
            System.out.println(row);
        }
    }
}
