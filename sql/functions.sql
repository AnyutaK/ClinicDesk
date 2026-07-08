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

CREATE OR REPLACE FUNCTION available_slots(doc INTEGER)
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

