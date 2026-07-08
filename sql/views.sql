CREATE OR REPLACE VIEW vw_doctor_schedule AS
SELECT
d.doctor_id,
d.doctor_name,
d.department,
s.slot_id,
s.appointment_date,
s.appointment_time,
p.patient_id,
p.name AS patient_name,
a.status
FROM Slots s
LEFT JOIN Doctors d
ON s.doctor_id = d.doctor_id
LEFT JOIN Appointments a
ON s.slot_id = a.slot_id
LEFT JOIN Patients p
ON a.patient_id = p.patient_id;

CREATE OR REPLACE VIEW vw_available_slots AS
SELECT
s.slot_id,
d.doctor_name,
d.department,
s.appointment_date,
s.appointment_time
FROM Slots s
JOIN Doctors d
ON s.doctor_id = d.doctor_id
WHERE s.is_available = TRUE;

CREATE OR REPLACE VIEW vw_patient_history AS
SELECT
p.patient_id,
p.name,
d.doctor_name,
d.department,
s.appointment_date,
s.appointment_time,
a.status,
a.appointment_duration
FROM Appointments a
JOIN Patients p
ON a.patient_id = p.patient_id
JOIN Slots s
ON a.slot_id = s.slot_id
JOIN Doctors d
ON s.doctor_id = d.doctor_id;

CREATE OR REPLACE VIEW vw_doctor_statistics AS
SELECT
d.doctor_id,
d.doctor_name,
d.department,
COUNT(a.appointment_id) AS total_appointments,
COUNT(*) FILTER (WHERE a.status='attended') AS attended,
COUNT(*) FILTER (WHERE a.status='cancelled') AS cancelled,
COUNT(*) FILTER (WHERE a.status='did not attend') AS no_show,
ROUND(AVG(a.appointment_duration),2) AS avg_duration
FROM Doctors d
LEFT JOIN Slots s
ON d.doctor_id = s.doctor_id
LEFT JOIN Appointments a
ON s.slot_id = a.slot_id
GROUP BY
d.doctor_id,
d.doctor_name,
d.department;

CREATE OR REPLACE VIEW vw_insurance_statistics AS
SELECT
insurance,
COUNT(*) AS total_patients
FROM Patients
GROUP BY insurance
ORDER BY total_patients DESC;