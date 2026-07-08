CREATE OR REPLACE FUNCTION book_appointment(
    p_patient_id INTEGER,
    p_slot_id INTEGER,
    p_status VARCHAR DEFAULT 'scheduled'
)
RETURNS TEXT
LANGUAGE plpgsql
AS $$
DECLARE
    available BOOLEAN;
BEGIN
    SELECT is_available
    INTO available
    FROM Slots
    WHERE slot_id = p_slot_id;
    IF available IS DISTINCT FROM TRUE THEN
        RETURN 'Slot not available';
    END IF;
    INSERT INTO Appointments
    (
        patient_id,
        slot_id,
        scheduling_date,
        status
    )
    VALUES
    (
        p_patient_id,
        p_slot_id,
        CURRENT_DATE,
        p_status
    );
    RETURN 'Appointment booked';
END;
$$;

CREATE OR REPLACE FUNCTION cancel_appointment(
    p_appointment_id INTEGER

)
RETURNS TEXT
LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE Appointments
    SET status='cancelled'
    WHERE appointment_id=p_appointment_id;
    RETURN 'Appointment cancelled';
END;
$$;

CREATE OR REPLACE FUNCTION reschedule_appointment(
    p_appointment_id INTEGER,
    p_new_slot INTEGER
)
RETURNS TEXT
LANGUAGE plpgsql
AS $$
DECLARE
    available BOOLEAN;
BEGIN
    SELECT is_available
    INTO available
    FROM Slots
    WHERE slot_id=p_new_slot;
    IF available IS DISTINCT FROM TRUE THEN
        RETURN 'New slot unavailable';
    END IF;
    UPDATE Slots
    SET is_available=TRUE
    WHERE slot_id=(
        SELECT slot_id
        FROM Appointments
        WHERE appointment_id=p_appointment_id
    );
    UPDATE Appointments
    SET slot_id=p_new_slot,
        scheduling_date=CURRENT_DATE
    WHERE appointment_id=p_appointment_id;
    UPDATE Slots
    SET is_available=FALSE
    WHERE slot_id=p_new_slot;
    INSERT INTO Audit_Log
    (
        appointment_id,
        action
    )
    VALUES
    (
        p_appointment_id,
        'Appointment Rescheduled'
    );
    RETURN 'Appointment rescheduled';
END;
$$;