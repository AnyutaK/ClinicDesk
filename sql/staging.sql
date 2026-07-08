-- STAGING TABLES
-- Raw import tables for Kaggle dataset

DROP TABLE IF EXISTS stg_patients;
DROP TABLE IF EXISTS stg_slots;
DROP TABLE IF EXISTS stg_appointments;

-- Patients
CREATE TABLE stg_patients (
    patient_id INTEGER,
    name VARCHAR(60),
    sex VARCHAR(20),
    dob DATE,
    insurance VARCHAR(50)
);

-- Slots
CREATE TABLE stg_slots (
    slot_id INTEGER,
    appointment_date DATE,
    appointment_time TIME,
    is_available BOOLEAN
);

-- Appointments
CREATE TABLE stg_appointments (
    appointment_id INTEGER,
    slot_id INTEGER,
    scheduling_date DATE,
    appointment_date DATE,
    appointment_time TIME,
    scheduling_interval INTEGER,
    status VARCHAR(50),
    check_in_time TIME,
    appointment_duration NUMERIC,
    start_time TIME,
    end_time TIME,
    waiting_time NUMERIC,
    patient_id INTEGER,
    sex VARCHAR(20),
    age INTEGER,
    age_group VARCHAR(20)

);