-- Ensure schema columns exist if the table was created by older migrations
ALTER TABLE IF EXISTS patient ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;

-- Populate created_at and blood_group for any legacy rows that have null
UPDATE patient SET created_at = CURRENT_TIMESTAMP WHERE created_at IS NULL;
UPDATE patient SET blood_group = 'O_POSITIVE' WHERE blood_group IS NULL;

-- Initial seed data for learning and testing Spring Boot JPA
-- Safe idempotent inserts using standard SQL: only inserts if email does not already exist

INSERT INTO patient (name, gender, birth_date, email, blood_group, created_at)
SELECT 'Aarav Sharma', 'MALE', '1990-05-10', 'aarav.sharma@example.com', 'O_POSITIVE', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM patient WHERE email = 'aarav.sharma@example.com');

INSERT INTO patient (name, gender, birth_date, email, blood_group, created_at)
SELECT 'Diya Patel', 'FEMALE', '1995-08-20', 'diya.patel@example.com', 'A_POSITIVE', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM patient WHERE email = 'diya.patel@example.com');

INSERT INTO patient (name, gender, birth_date, email, blood_group, created_at)
SELECT 'Dishant Verma', 'MALE', '1988-03-15', 'dishant.verma@example.com', 'A_POSITIVE', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM patient WHERE email = 'dishant.verma@example.com');

INSERT INTO patient (name, gender, birth_date, email, blood_group, created_at)
SELECT 'Neha Iyer', 'FEMALE', '1992-12-01', 'neha.iyer@example.com', 'AB_POSITIVE', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM patient WHERE email = 'neha.iyer@example.com');

INSERT INTO patient (name, gender, birth_date, email, blood_group, created_at)
SELECT 'Kabir Singh', 'MALE', '1993-07-11', 'kabir.singh@example.com', 'O_POSITIVE', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM patient WHERE email = 'kabir.singh@example.com');