package com.divyansh.HospitalManagement;

import com.divyansh.HospitalManagement.dto.PatientRequestDto;
import com.divyansh.HospitalManagement.entity.type.BloodGroupType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Integration Test: Verifies complete end-to-end flow from Controller -> Service -> Repository -> Database
@SpringBootTest
@AutoConfigureMockMvc
public class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @Test
    public void testCreatePatient_Success() throws Exception {
        // Unique email for each test run to avoid collision
        String uniqueEmail = "test." + UUID.randomUUID() + "@hospital.com";
        PatientRequestDto request = PatientRequestDto.builder()
                .name("Rohan Mehta")
                .email(uniqueEmail)
                .gender("MALE")
                .birthDate(LocalDate.of(1996, 4, 12))
                .bloodGroup(BloodGroupType.B_POSITIVE)
                .build();

        mockMvc.perform(post("/api/v1/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Rohan Mehta")))
                .andExpect(jsonPath("$.email", is(uniqueEmail)))
                .andExpect(jsonPath("$.bloodGroup", is("B_POSITIVE")));
    }

    @Test
    public void testCreatePatient_WithBloodGroupSymbol_Success() throws Exception {
        // Tests sending standard medical symbol format "O+" in JSON body
        String uniqueEmail = "symbol." + UUID.randomUUID() + "@hospital.com";
        String jsonPayload = """
                {
                    "name": "Simran Kaur",
                    "email": "%s",
                    "gender": "FEMALE",
                    "birthDate": "1998-09-14",
                    "bloodGroup": "O+"
                }
                """.formatted(uniqueEmail);

        mockMvc.perform(post("/api/v1/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.bloodGroup", is("O_POSITIVE")));
    }

    @Test
    public void testCreatePatient_WithInvalidBloodGroup_HandledByGCE() throws Exception {
        // Tests sending invalid blood group value like "XYZ" -> caught by GCE -> 400 Bad Request
        String uniqueEmail = "invalid." + UUID.randomUUID() + "@hospital.com";
        String jsonPayload = """
                {
                    "name": "Invalid Patient",
                    "email": "%s",
                    "gender": "MALE",
                    "birthDate": "1995-01-01",
                    "bloodGroup": "XYZ"
                }
                """.formatted(uniqueEmail);

        mockMvc.perform(post("/api/v1/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", containsString("Invalid blood group")));
    }

    @Test
    public void testCreatePatient_ValidationError_HandledByGCE() throws Exception {
        // Missing name and invalid email to trigger MethodArgumentNotValidException -> GCE
        PatientRequestDto invalidRequest = PatientRequestDto.builder()
                .name("") // blank!
                .email("invalid-email-address") // invalid pattern!
                .gender("MALE")
                .birthDate(LocalDate.of(2000, 1, 1))
                .bloodGroup(BloodGroupType.O_POSITIVE)
                .build();

        mockMvc.perform(post("/api/v1/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Failed")))
                .andExpect(jsonPath("$.validationErrors.name", notNullValue()))
                .andExpect(jsonPath("$.validationErrors.email", notNullValue()));
    }

    @Test
    public void testGetPatientById_NotFound_HandledByGCE() throws Exception {
        // Non-existent ID 999999 -> throws ResourceNotFoundException -> handled by GCE (404)
        mockMvc.perform(get("/api/v1/patients/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("Patient not found with id: 999999")));
    }

    @Test
    public void testGetAllPatients_WithPaginationAndSorting() throws Exception {
        // Tests pagination and sorting endpoint: page=0, size=3, sortBy=name, sortDir=asc
        mockMvc.perform(get("/api/v1/patients")
                        .param("page", "0")
                        .param("size", "3")
                        .param("sortBy", "name")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(lessThanOrEqualTo(3))))
                .andExpect(jsonPath("$.pageNumber", is(0)))
                .andExpect(jsonPath("$.pageSize", is(3)))
                .andExpect(jsonPath("$.totalElements", greaterThan(0)));
    }

    @Test
    public void testGetAllPatients_WithFiltering() throws Exception {
        // Filter by bloodGroup (O_POSITIVE)
        mockMvc.perform(get("/api/v1/patients")
                        .param("bloodGroup", "O_POSITIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", notNullValue()));
    }

    @Test
    public void testGetAllPatients_WithBloodGroupSymbolQueryParam() throws Exception {
        // Filter by bloodGroup using symbol "O+" in query parameter via converter
        mockMvc.perform(get("/api/v1/patients")
                        .param("bloodGroup", "O+"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", notNullValue()));
    }

    @Test
    public void testGetAllPatients_WithInvalidBloodGroupQueryParam_HandledByGCE() throws Exception {
        // Filter by invalid blood group in query param -> caught by GCE -> 400 Bad Request
        mockMvc.perform(get("/api/v1/patients")
                        .param("bloodGroup", "UNKNOWN_BG"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", containsString("Invalid blood group")));
    }
}
