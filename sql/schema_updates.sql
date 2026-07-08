ALTER TABLE Patients
ADD CONSTRAINT chk_patient_sex
CHECK (sex IN ('Male', 'Female', 'Non-binary'));

ALTER TABLE Appointments
ADD CONSTRAINT chk_appointment_status
CHECK (
    status IN (
        'scheduled',
        'completed',
        'cancelled',
        'no-show',
        'available'
    )
);

ALTER TABLE Appointments
ADD CONSTRAINT chk_duration
CHECK (
    appointment_duration IS NULL
    OR appointment_duration > 0
);
CREATE INDEX idx_patient_name
ON Patients(name);

CREATE INDEX idx_slot_date
ON Slots(appointment_date);

CREATE INDEX idx_slot_doctor
ON Slots(doctor_id);

CREATE INDEX idx_appointment_patient
ON Appointments(patient_id);

CREATE INDEX idx_appointment_slot
ON Appointments(slot_id);

ALTER TABLE Doctors
ADD COLUMN created_at TIMESTAMP
DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE Appointments
ADD COLUMN created_at TIMESTAMP
DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE Audit_Log
ADD CONSTRAINT fk_audit_appointment
FOREIGN KEY (appointment_id)
REFERENCES Appointments(appointment_id);

