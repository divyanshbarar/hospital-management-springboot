# 🏥 Hospital Management System (Spring Boot & JPA)

A clean, beginner-friendly Spring Boot project built specifically to learn and master the standard layered architecture in Spring Boot, Spring Data JPA, DTOs, Exception Handling (GCE), Filtering, Sorting, and Pagination.

---

## 📌 Architecture & Request Flow

The application follows the industry-standard multi-tier architecture:

```
+-----------------------------------------------------------------------------------+
|                                  HTTP Client                                      |
|                       (Browser / Postman / cURL / Frontend)                       |
+-----------------------------------------------------------------------------------+
         │                                                            ▲
         │ 1. HTTP Request (JSON Body / Query Params)                 │ 8. HTTP Response (JSON / Status Code)
         ▼                                                            │
+───────────────────────────────────────────────────────────────────────────────────+
| 🌐 CONTROLLER LAYER (PatientController.java)                                      |
|   - Exposes REST endpoints (@GetMapping, @PostMapping, etc.)                      |
|   - Validates incoming DTO via @Valid                                             |
|   - Next Flow: Calls Service layer methods                                        |
+───────────────────────────────────────────────────────────────────────────────────+
         │                                                            ▲
         │ 2. Validated Request DTO                                   │ 7. Response DTO
         ▼                                                            │
+───────────────────────────────────────────────────────────────────────────────────+
| ⚙️ SERVICE LAYER (PatientService.java)                                             |
|   - Business rules & validation (e.g. email uniqueness)                           |
|   - DTO <-> Entity mappings                                                       |
|   - Transaction management (@Transactional)                                       |
|   - Configures Pagination & Sorting (PageRequest, Sort)                           |
|   - Next Flow: Calls Repository layer methods                                     |
+───────────────────────────────────────────────────────────────────────────────────+
         │                                                            ▲
         │ 3. Entity (Patient) / Pageable                             │ 6. Entity / Page<Patient>
         ▼                                                            │
+───────────────────────────────────────────────────────────────────────────────────+
| 🗄️ REPOSITORY LAYER (PatientRepository.java)                                      |
|   - Extends Spring Data JpaRepository<Patient, Long>                              |
|   - Derived query methods (findByNameContainingIgnoreCase, findByBloodGroup, etc.)|
|   - Next Flow: Spring Data JPA generates SQL and executes on Database             |
+───────────────────────────────────────────────────────────────────────────────────+
         │                                                            ▲
         │ 4. SQL (SELECT, INSERT, UPDATE, DELETE)                    │ 5. SQL Result Set
         ▼                                                            │
+───────────────────────────────────────────────────────────────────────────────────+
| 🗃️ DATABASE (PostgreSQL)                                                          |
|   - Stores tables (patient), indexes, and constraints                             |
+───────────────────────────────────────────────────────────────────────────────────+
```

### 🚨 Exception Flow (GCE - Global Controller Exception Handler)
If any exception occurs anywhere in the stack (e.g. `ResourceNotFoundException`, `DuplicateResourceException`, validation failure):
```
Exception Thrown in Service / Controller
               │
               ▼
+─────────────────────────────────────────────────────────────+
| 🛡️ GlobalExceptionHandler (@RestControllerAdvice)           |
|    - Intercepts exceptions globally                         |
|    - Converts exception into standard ErrorResponse DTO     |
|    - Returns appropriate HTTP status (400, 404, 409, 500)   |
+─────────────────────────────────────────────────────────────+
               │
               ▼
        Client receives clean JSON Error Response
```

---

## 📂 Project Directory Structure

```
HospitalManagement/
├── src/
│   ├── main/
│   │   ├── java/com/divyansh/HospitalManagement/
│   │   │   ├── config/
│   │   │   │   └── ModelMapperConfig.java        # Defines ModelMapper @Bean for automatic DTO-Entity mapping
│   │   │   ├── controller/
│   │   │   │   └── PatientController.java        # REST API endpoints & input validation
│   │   │   ├── dto/
│   │   │   │   ├── PatientRequestDto.java        # Request payload with Bean Validation
│   │   │   │   ├── PatientResponseDto.java       # Clean response data for clients
│   │   │   │   └── PageResponseDto.java          # Wrapper for paginated results & metadata
│   │   │   ├── entity/
│   │   │   │   ├── Patient.java                  # JPA Entity mapped to 'patient' table
│   │   │   │   └── type/
│   │   │   │       └── BloodGroupType.java       # Blood group enum constants
│   │   │   ├── exception/
│   │   │   │   ├── ResourceNotFoundException.java # 404 Not Found exception
│   │   │   │   ├── DuplicateResourceException.java# 409 Conflict exception
│   │   │   │   ├── ErrorResponse.java            # Standard error response structure
│   │   │   │   └── GlobalExceptionHandler.java   # GCE: Global Controller Exception Handler
│   │   │   ├── repository/
│   │   │   │   └── PatientRepository.java        # Spring Data JPA interface + queries
│   │   │   ├── service/
│   │   │   │   └── PatientService.java           # Business logic, ModelMapper conversions & transactions
│   │   │   └── HospitalManagementApplication.java# Spring Boot bootstrap main class
│   │   └── resources/
│   │       ├── application.properties            # DB and Hibernate configurations
│   │       └── data.sql                          # Initial seed data for learning
│   └── test/
│       └── java/com/divyansh/HospitalManagement/
│           ├── HospitalManagementApplicationTests.java # Context loading test
│           ├── PatientsTests.java                      # Direct Repository test
│           └── PatientControllerTest.java              # End-to-end integration tests
└── pom.xml                                       # Maven build configuration
```

---

## 🔑 Key Concepts Explained

### 1. ModelMapper (Simplified DTO <-> Entity Mapping)
Instead of writing long, repetitive manual getter/setter lines:
- Configured as a Spring Bean in [`ModelMapperConfig.java`](file:///C:/Users/divya/Downloads/HospitalManagement/HospitalManagement/src/main/java/com/divyansh/HospitalManagement/config/ModelMapperConfig.java):
  ```java
  @Bean
  public ModelMapper modelMapper() {
      return new ModelMapper();
  }
  ```
- Injected into [`PatientService.java`](file:///C:/Users/divya/Downloads/HospitalManagement/HospitalManagement/src/main/java/com/divyansh/HospitalManagement/service/PatientService.java):
  ```java
  // Request DTO -> Entity
  Patient patient = modelMapper.map(requestDto, Patient.class);

  // Entity -> Response DTO
  PatientResponseDto response = modelMapper.map(patient, PatientResponseDto.class);

  // Update existing Entity from Request DTO
  modelMapper.map(requestDto, existingPatient);
  ```
- Copies matching properties automatically based on field names!

### 2. DTOs (Data Transfer Objects)
- **`PatientRequestDto`**: Contains only the fields clients are allowed to submit. Uses Jakarta validation annotations (`@NotBlank`, `@Email`, `@Past`, `@NotNull`).
- **`PatientResponseDto`**: Protects database schema internals by only returning fields the client needs.
- **`PageResponseDto<T>`**: Wraps page data (`content`) along with `pageNumber`, `pageSize`, `totalElements`, `totalPages`, `isFirst`, and `isLast`.

### 2. Spring Data JPA Repository
- Extends `JpaRepository<Patient, Long>` to inherit standard CRUD operations.
- Derived query methods using Spring Data naming conventions:
  - `findByNameContainingIgnoreCase(String name, Pageable pageable)`
  - `findByBloodGroup(BloodGroupType bloodGroup, Pageable pageable)`
  - `findByNameContainingIgnoreCaseAndBloodGroup(String name, BloodGroupType bloodGroup, Pageable pageable)`

### 3. Pagination, Sorting & Filtering
- Handled at `GET /api/v1/patients`:
  - `page`: Requested page index (0-based, default `0`).
  - `size`: Records per page (default `10`).
  - `sortBy`: Field name to sort on (e.g. `name`, `id`, `birthDate`, default `id`).
  - `sortDir`: Sort direction (`asc` or `desc`, default `asc`).
  - `name`: Case-insensitive partial name search (optional).
  - `bloodGroup`: Filter by specific blood group (optional).

### 4. Blood Group Type (Flexible Input Formats)
The [`BloodGroupType`](file:///C:/Users/divya/Downloads/HospitalManagement/HospitalManagement/src/main/java/com/divyansh/HospitalManagement/entity/type/BloodGroupType.java) enum supports multiple real-world notations:
- **Enum Names**: `"A_POSITIVE"`, `"B_NEGATIVE"`, `"O_POSITIVE"`, etc.
- **Medical Symbols**: `"A+"`, `"A-"`, `"B+"`, `"B-"`, `"AB+"`, `"AB-"`, `"O+"`, `"O-"`
- **Case-Insensitive**: `"a_positive"`, `"o+"`, `"ab+"`
- In URL query parameters (`?bloodGroup=O+` or `?bloodGroup=A_POSITIVE`), automatic conversion is handled by [`StringToBloodGroupTypeConverter`](file:///C:/Users/divya/Downloads/HospitalManagement/HospitalManagement/src/main/java/com/divyansh/HospitalManagement/config/StringToBloodGroupTypeConverter.java).
- If an invalid value is sent, the **GCE** intercepts it and returns a friendly 400 Bad Request error listing the allowed blood groups.

### 5. GCE (Global Controller Exception Handler)
- Uses `@RestControllerAdvice` on `GlobalExceptionHandler`.
- Uses `@ExceptionHandler(...)` to catch specific exceptions:
  - `ResourceNotFoundException` ➡️ **404 NOT FOUND**
  - `DuplicateResourceException` ➡️ **409 CONFLICT**
  - `MethodArgumentNotValidException` ➡️ **400 BAD REQUEST** (with detailed field-by-field error map)
  - `HttpMessageNotReadableException` ➡️ **400 BAD REQUEST** (invalid JSON body or invalid blood group)
  - `MethodArgumentTypeMismatchException` ➡️ **400 BAD REQUEST** (invalid query parameter value)
  - `IllegalArgumentException` ➡️ **400 BAD REQUEST**
  - `Exception` ➡️ **500 INTERNAL SERVER ERROR**

---

## 🚀 Running the Application

### Prerequisites
- Java 17+
- PostgreSQL running on `localhost:5432` with database `HospitalDb` (credentials in `application.properties`)

### Run with Maven Wrapper
From the `HospitalManagement/HospitalManagement` folder:
```powershell
.\mvnw.cmd spring-boot:run
```

### Run Tests
```powershell
.\mvnw.cmd test
```

---

## 📡 REST API Endpoints & Testing Guide

Base URL: `http://localhost:8080/api/v1/patients`

### 1. Create a Patient (`POST`)
**Request**:
```bash
curl -X POST http://localhost:8080/api/v1/patients \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Arjun Kapoor",
    "email": "arjun.kapoor@example.com",
    "gender": "MALE",
    "birthDate": "1994-06-25",
    "bloodGroup": "B_POSITIVE"
  }'
```
**Response (201 Created)**:
```json
{
  "id": 12,
  "name": "Arjun Kapoor",
  "email": "arjun.kapoor@example.com",
  "gender": "MALE",
  "birthDate": "1994-06-25",
  "bloodGroup": "B_POSITIVE",
  "createdAt": "2026-11-04T01:15:00"
}
```

---

### 2. Get Patient by ID (`GET`)
**Request**:
```bash
curl http://localhost:8080/api/v1/patients/1
```
**Response (200 OK)**:
```json
{
  "id": 1,
  "name": "Aarav Sharma",
  "email": "aarav.sharma@example.com",
  "gender": "MALE",
  "birthDate": "1990-05-10",
  "bloodGroup": "O_POSITIVE",
  "createdAt": "2026-11-04T01:05:15"
}
```

---

### 3. Get All Patients with Pagination, Sorting & Filtering (`GET`)
**Example 1: Pagination + Sorting**:
```bash
curl "http://localhost:8080/api/v1/patients?page=0&size=3&sortBy=name&sortDir=asc"
```

**Example 2: Filtering by Blood Group**:
```bash
curl "http://localhost:8080/api/v1/patients?bloodGroup=O_POSITIVE"
```

**Example 3: Filtering by Partial Name & Blood Group**:
```bash
curl "http://localhost:8080/api/v1/patients?name=aarav&bloodGroup=O_POSITIVE"
```

**Response (200 OK)**:
```json
{
  "content": [
    {
      "id": 1,
      "name": "Aarav Sharma",
      "email": "aarav.sharma@example.com",
      "gender": "MALE",
      "birthDate": "1990-05-10",
      "bloodGroup": "O_POSITIVE",
      "createdAt": "2026-11-04T01:05:15"
    }
  ],
  "pageNumber": 0,
  "pageSize": 3,
  "totalElements": 1,
  "totalPages": 1,
  "isFirst": true,
  "isLast": true
}
```

---

### 4. Update Patient Details (`PUT`)
**Request**:
```bash
curl -X PUT http://localhost:8080/api/v1/patients/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Aarav Sharma Updated",
    "email": "aarav.sharma@example.com",
    "gender": "MALE",
    "birthDate": "1990-05-10",
    "bloodGroup": "O_POSITIVE"
  }'
```
**Response (200 OK)**:
```json
{
  "id": 1,
  "name": "Aarav Sharma Updated",
  "email": "aarav.sharma@example.com",
  "gender": "MALE",
  "birthDate": "1990-05-10",
  "bloodGroup": "O_POSITIVE",
  "createdAt": "2026-11-04T01:05:15"
}
```

---

### 5. Delete Patient (`DELETE`)
**Request**:
```bash
curl -X DELETE http://localhost:8080/api/v1/patients/1
```
**Response (200 OK)**:
```json
{
  "message": "Patient successfully deleted with id: 1"
}
```

---

### 6. Exception Handling Showcase (GCE in Action)

#### A. Triggering 400 Bad Request (Validation Failure)
**Request**:
```bash
curl -X POST http://localhost:8080/api/v1/patients \
  -H "Content-Type: application/json" \
  -d '{
    "name": "",
    "email": "not-an-email"
  }'
```
**Response (400 Bad Request)**:
```json
{
  "timestamp": "2026-11-04T01:16:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Input validation failed for one or more fields",
  "path": "/api/v1/patients",
  "validationErrors": {
    "name": "Patient name is required and cannot be blank",
    "email": "Please provide a valid email format (e.g., user@example.com)",
    "gender": "Gender is required",
    "birthDate": "Birth date is required",
    "bloodGroup": "Blood group is required"
  }
}
```

#### B. Triggering 404 Not Found (Missing Resource)
**Request**:
```bash
curl http://localhost:8080/api/v1/patients/99999
```
**Response (404 Not Found)**:
```json
{
  "timestamp": "2026-11-04T01:17:00",
  "status": 404,
  "error": "Not Found",
  "message": "Patient not found with id: 99999",
  "path": "/api/v1/patients/99999"
}
```

#### C. Triggering 409 Conflict (Duplicate Email)
**Request**: Sending an already registered email.
**Response (409 Conflict)**:
```json
{
  "timestamp": "2026-11-04T01:18:00",
  "status": 409,
  "error": "Conflict",
  "message": "Patient with email 'aarav.sharma@example.com' already exists",
  "path": "/api/v1/patients"
}
```
