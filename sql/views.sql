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

CREATE OR REPLACE VIEW vw_daily_clinic_summary AS
SELECT
appointment_date,
COUNT(*) AS total_appointments,
COUNT(*) FILTER (WHERE status='attended') AS attended,
COUNT(*) FILTER (WHERE status='cancelled') AS cancelled,
COUNT(*) FILTER (WHERE status='did not attend') AS no_show,
ROUND(AVG(appointment_duration),2) AS avg_duration
FROM Appointments a
JOIN Slots s
ON a.slot_id=s.slot_id
GROUP BY appointment_date
ORDER BY appointment_date;

CREATE OR REPLACE VIEW vw_doctor_utilization AS
SELECT
d.doctor_name,
COUNT(s.slot_id) AS total_slots,
COUNT(a.appointment_id) AS booked_slots,
ROUND(
100.0*COUNT(a.appointment_id)/COUNT(s.slot_id),
2
) AS utilization_percentage
FROM Doctors d
LEFT JOIN Slots s
ON d.doctor_id=s.doctor_id
LEFT JOIN Appointments a
ON s.slot_id=a.slot_id
GROUP BY d.doctor_name;

CREATE OR REPLACE VIEW vw_upcoming_appointments AS
SELECT
p.name,
d.doctor_name,
s.appointment_date,
s.appointment_time
FROM Appointments a
JOIN Patients p
ON a.patient_id=p.patient_id
JOIN Slots s
ON a.slot_id=s.slot_id
JOIN Doctors d
ON s.doctor_id=d.doctor_id
WHERE s.appointment_date>=CURRENT_DATE
ORDER BY s.appointment_date,s.appointment_time;

CREATE OR REPLACE VIEW vw_frequent_patients AS
SELECT
p.patient_id,
p.name,
COUNT(*) AS visits
FROM Patients p
JOIN Appointments a
ON p.patient_id=a.patient_id
GROUP BY p.patient_id,p.name
ORDER BY visits DESC;

CREATE OR REPLACE VIEW vw_monthly_appointments AS
SELECT
DATE_TRUNC('month',s.appointment_date) AS month,
COUNT(*) AS appointments
FROM Appointments a
JOIN Slots s
ON a.slot_id=s.slot_id
GROUP BY month
ORDER BY month;
