CREATE OR REPLACE FUNCTION get_patient_visit_count(pid INTEGER)
RETURNS INTEGER
LANGUAGE plpgsql
AS $$
DECLARE
    visit_count INTEGER;
BEGIN
    SELECT COUNT(*)
    INTO visit_count
    FROM Appointments
    WHERE patient_id = pid;
    RETURN visit_count;
END;
$$;

CREATE OR REPLACE FUNCTION get_doctor_appointments(doc INTEGER)
RETURNS INTEGER
LANGUAGE plpgsql
AS $$
DECLARE
    total INTEGER;
BEGIN
    SELECT COUNT(*)
    INTO total
    FROM Appointments a
    JOIN Slots s
    ON a.slot_id=s.slot_id
    WHERE s.doctor_id=doc;
    RETURN total;
END;
$$;

CREATE OR REPLACE FUNCTION average_waiting_time()
RETURNS NUMERIC
LANGUAGE plpgsql
AS $$
DECLARE
    avg_wait NUMERIC;
BEGIN
    SELECT ROUND(AVG(waiting_time),2)
    INTO avg_wait
    FROM stg_appointments;
    RETURN avg_wait;
END;
$$;

CREATE OR REPLACE FUNCTION count_available_slots(doc INTEGER)
RETURNS INTEGER
LANGUAGE plpgsql
AS $$
DECLARE
    total INTEGER;
BEGIN
    SELECT COUNT(*)
    INTO total
    FROM Slots
    WHERE doctor_id=doc
    AND is_available=TRUE;
    RETURN total;
END;
$$;

CREATE OR REPLACE FUNCTION estimate_revenue(rate NUMERIC)
RETURNS NUMERIC
LANGUAGE plpgsql
AS $$
DECLARE
    revenue NUMERIC;
BEGIN
    SELECT COUNT(*) * rate
    INTO revenue
    FROM Appointments
    WHERE status='attended';
    RETURN revenue;
END;
$$;

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
    d.doctor_id,
    d.doctor_name,
    d.department
FROM Doctors d
WHERE d.doctor_name ILIKE '%'||keyword||'%'
OR d.department ILIKE '%'||keyword||'%'
OR d.specialization ILIKE '%'||keyword||'%'
ORDER BY d.doctor_name;
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
RETURNS TABLE
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
