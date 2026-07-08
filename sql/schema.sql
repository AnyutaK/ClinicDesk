CREATE TABLE Patients (
    patient_id SERIAL PRIMARY KEY,
    name VARCHAR(60) NOT NULL,
    sex VARCHAR(20) NOT NULL,
    dob DATE NOT NULL,
    insurance VARCHAR(50),
    phone VARCHAR(15),
    email VARCHAR(100) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Doctors (
    doctor_id SERIAL PRIMARY KEY,
    doctor_name VARCHAR(100) NOT NULL,
    department VARCHAR(50) NOT NULL,
    specialization VARCHAR(100),
    phone VARCHAR(15),
    email VARCHAR(100) UNIQUE
);

CREATE TABLE Slots (
    slot_id SERIAL PRIMARY KEY,
    doctor_id INTEGER NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    is_available BOOLEAN DEFAULT TRUE,

    FOREIGN KEY (doctor_id)
        REFERENCES Doctors(doctor_id)
);

CREATE TABLE Appointments (
    appointment_id SERIAL PRIMARY KEY,
    patient_id INTEGER NOT NULL,
    slot_id INTEGER NOT NULL,
    scheduling_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    check_in_time TIME,
    start_time TIME,
    end_time TIME,
    appointment_duration NUMERIC(5,2),
    FOREIGN KEY (patient_id)
        REFERENCES Patients(patient_id),
    FOREIGN KEY (slot_id)
        REFERENCES Slots(slot_id)
);

CREATE TABLE Audit_Log (
    log_id SERIAL PRIMARY KEY,
    appointment_id INTEGER,
    action VARCHAR(100),
    performed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
