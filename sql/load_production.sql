BEGIN;
-- Patients
INSERT INTO Patients
(patient_id, name, sex, dob, insurance)
SELECT
patient_id,
name,
sex,
dob,
insurance
FROM stg_patients
ON CONFLICT (patient_id) DO NOTHING;

-- Slots
INSERT INTO Slots
(slot_id,
doctor_id,
appointment_date,
appointment_time,
is_available)
SELECT
slot_id,
((slot_id - 1) % 20) + 1,
appointment_date,
appointment_time,
is_available
FROM stg_slots
ON CONFLICT (slot_id) DO NOTHING;

-- Appointments
INSERT INTO Appointments
(
appointment_id,
patient_id,
slot_id,
scheduling_date,
status,
check_in_time,
start_time,
end_time,
appointment_duration
)
SELECT
appointment_id,
patient_id,
slot_id,
scheduling_date,
CASE
    WHEN status='attended' THEN 'attended'
    WHEN status='did not attend' THEN 'did not attend'
    WHEN status='cancelled' THEN 'cancelled'
    ELSE 'available'
END,
check_in_time,
start_time,
end_time,
appointment_duration
FROM stg_appointments;

COMMIT;