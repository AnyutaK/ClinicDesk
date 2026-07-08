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

CREATE OR REPLACE FUNCTION get_doctor_schedule(
    p_doctor_id INTEGER,
    p_date DATE
)
RETURNS TABLE
(
    appointment_time TIME,
    patient_name VARCHAR,
    status VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT
    s.appointment_time,
    p.name,
    a.status
    FROM Slots s
    LEFT JOIN Appointments a
    ON s.slot_id = a.slot_id
    LEFT JOIN Patients p
    ON a.patient_id = p.patient_id
    WHERE s.doctor_id = p_doctor_id
    AND s.appointment_date = p_date
    ORDER BY s.appointment_time;
END;
$$;

CREATE OR REPLACE FUNCTION get_patient_history(
    p_patient_id INTEGER
)
RETURNS TABLE
(
    doctor_name VARCHAR,
    department VARCHAR,
    appointment_date DATE,
    appointment_time TIME,
    status VARCHAR,
    duration NUMERIC
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT
d.doctor_name,
d.department,
s.appointment_date,
s.appointment_time,
a.status,
a.appointment_duration
FROM Appointments a
JOIN Slots s
ON a.slot_id=s.slot_id
JOIN Doctors d
ON s.doctor_id=d.doctor_id
WHERE a.patient_id=p_patient_id
ORDER BY s.appointment_date DESC;
END;
$$;

CREATE OR REPLACE FUNCTION get_clinic_summary(
    start_date DATE,
    end_date DATE
)
RETURNS TABLE
(
    total_appointments BIGINT,
    attended BIGINT,
    cancelled BIGINT,
    no_show BIGINT
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT
COUNT(*),
COUNT(*) FILTER (WHERE status='attended'),
COUNT(*) FILTER (WHERE status='cancelled'),
COUNT(*) FILTER (WHERE status='did not attend')
FROM Appointments a
JOIN Slots s
ON a.slot_id=s.slot_id
WHERE s.appointment_date
BETWEEN start_date
AND end_date;
END;
$$;

CREATE OR REPLACE FUNCTION get_doctor_utilization(
    p_doctor_id INTEGER
)
RETURNS TABLE
(
    total_slots BIGINT,
    booked_slots BIGINT,
    available_slots BIGINT,
    utilization NUMERIC
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT
COUNT(s.slot_id),
COUNT(a.appointment_id),
COUNT(s.slot_id)-COUNT(a.appointment_id),
ROUND(
100.0*COUNT(a.appointment_id)/NULLIF(COUNT(s.slot_id),0),
2
)
FROM Slots s
LEFT JOIN Appointments a
ON s.slot_id=a.slot_id
WHERE s.doctor_id=p_doctor_id;
END;
$$;

CREATE OR REPLACE FUNCTION get_available_slots(
    p_doctor_id INTEGER,
    p_date DATE
)
RETURNS TABLE
(
    slot_id INTEGER,
    appointment_time TIME
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT
slot_id,
appointment_time
FROM Slots
WHERE doctor_id=p_doctor_id
AND appointment_date=p_date
AND is_available=TRUE
ORDER BY appointment_time;
END;
$$;

CREATE OR REPLACE FUNCTION search_patients(
    keyword TEXT
)
RETURNS TABLE
(
    patient_id INTEGER,
    name VARCHAR,
    insurance VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT
p.patient_id,
p.name,
p.insurance
FROM Patients p
WHERE p.name ILIKE '%'||keyword||'%'
ORDER BY p.name;
END;
$$;

CREATE OR REPLACE FUNCTION search_doctors(
    keyword TEXT
)
RETURNS TABLE
(
    doctor_id INTEGER,
    doctor_name VARCHAR,
    department VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT
doctor_id,
doctor_name,
department
FROM Doctors
WHERE doctor_name ILIKE '%'||keyword||'%'
OR department ILIKE '%'||keyword||'%'
OR specialization ILIKE '%'||keyword||'%'
ORDER BY doctor_name;
END;
$$;

CREATE OR REPLACE FUNCTION doctor_dashboard(
    p_doctor_id INTEGER
)
RETURNS TABLE
(
    total BIGINT,
    attended BIGINT,
    cancelled BIGINT,
    no_show BIGINT,
    avg_duration NUMERIC
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT
COUNT(a.appointment_id),
COUNT(*) FILTER (WHERE a.status='attended'),
COUNT(*) FILTER (WHERE a.status='cancelled'),
COUNT(*) FILTER (WHERE a.status='did not attend'),
ROUND(AVG(a.appointment_duration),2)
FROM Slots s
LEFT JOIN Appointments a
ON s.slot_id=a.slot_id
WHERE s.doctor_id=p_doctor_id;
END;
$$;

CREATE OR REPLACE FUNCTION check_slot_availability(
    p_doctor_id INTEGER,
    p_date DATE,
    p_time TIME
)
RETURNS BOOLEAN
LANGUAGE plpgsql
AS $$
DECLARE
    available BOOLEAN;
BEGIN
    SELECT is_available
    INTO available
    FROM Slots
    WHERE doctor_id = p_doctor_id
      AND appointment_date = p_date
      AND appointment_time = p_time
    LIMIT 1;
    RETURN COALESCE(available, FALSE);
END;
$$;

CREATE OR REPLACE FUNCTION get_next_available_slot(
    p_doctor_id INTEGER,
    p_from_date DATE
)
NS TABLE
(
    slot_id INTEGER,
    appointment_date DATE,
    appointment_time TIME
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT
s.slot_id,
s.appointment_date,
s.appointment_time
FROM Slots s
WHERE s.doctor_id = p_doctor_id
AND s.is_available = TRUE
AND s.appointment_date >= p_from_date
ORDER BY
s.appointment_date,
s.appointment_time
LIMIT 1;
END;
$$;

CREATE OR REPLACE FUNCTION get_average_wait_time_by_doctor(
    p_doctor_id INTEGER
)
RETURNS NUMERIC
LANGUAGE plpgsql
AS $$
DECLARE
    avg_wait NUMERIC;
BEGIN
    SELECT ROUND(AVG(sa.waiting_time),2)
    INTO avg_wait
    FROM Appointments a
    JOIN Slots s
        ON a.slot_id=s.slot_id
    JOIN stg_appointments sa
        ON sa.appointment_id=a.appointment_id
    WHERE s.doctor_id=p_doctor_id;
    RETURN avg_wait;
END;
$$;

CREATE OR REPLACE FUNCTION get_busiest_doctor()
RETURNS TABLE
(
    doctor_name VARCHAR,
    appointments BIGINT
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT
d.doctor_name,
COUNT(a.appointment_id)
FROM Doctors d
JOIN Slots s
ON d.doctor_id=s.doctor_id
JOIN Appointments a
ON s.slot_id=a.slot_id
GROUP BY d.doctor_name
ORDER BY COUNT(*) DESC
LIMIT 1;
END;
$$;

CREATE OR REPLACE FUNCTION insurance_distribution()
RETURNS TABLE
(
    insurance VARCHAR,
    patients BIGINT
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT
p.insurance,
COUNT(*)
FROM Patients p
GROUP BY p.insurance
ORDER BY COUNT(*) DESC;
END;
$$;

CREATE OR REPLACE FUNCTION patient_age_distribution()
RETURNS TABLE
(
    age_group VARCHAR,
    patients BIGINT
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT
sa.age_group,
COUNT(*)
FROM stg_appointments sa
GROUP BY sa.age_group
ORDER BY sa.age_group;
END;
$$;

CREATE OR REPLACE FUNCTION today_summary()
RETURNS TABLE
(
    appointments BIGINT,
    attended BIGINT,
    cancelled BIGINT,
    no_show BIGINT
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT
COUNT(*),
COUNT(*) FILTER(WHERE status='attended'),
COUNT(*) FILTER(WHERE status='cancelled'),
COUNT(*) FILTER(WHERE status='did not attend')
FROM Appointments a
JOIN Slots s
ON a.slot_id=s.slot_id
WHERE s.appointment_date=CURRENT_DATE;
END;
$$;

CREATE OR REPLACE FUNCTION estimate_daily_revenue(
    consultation_fee NUMERIC
)
RETURNS NUMERIC
LANGUAGE plpgsql
AS $$
DECLARE
    revenue NUMERIC;

BEGIN
SELECT
COUNT(*)*consultation_fee
INTO revenue
FROM Appointments a
WHERE status='attended';
RETURN revenue;
END;
$$;