
CREATE OR REPLACE FUNCTION book_appointment(
    p_patient_id INTEGER,
    p_slot_id INTEGER,
    p_status VARCHAR DEFAULT 'scheduled'
)
RETURNS TEXT
LANGUAGE plpgsql
AS $$
DECLARE
    slot_available BOOLEAN;
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM Patients
        WHERE patient_id = p_patient_id
    ) THEN
        RETURN 'Booking failed: Patient does not exist.';
    END IF;
    SELECT is_available
    INTO slot_available
    FROM Slots
    WHERE slot_id = p_slot_id;
    IF NOT FOUND THEN
        RETURN 'Booking failed: Slot does not exist.';
    END IF;
    IF slot_available = FALSE THEN
        RETURN 'Booking failed: Slot is already booked.';
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
    RETURN 'Appointment booked successfully.';
EXCEPTION
    WHEN foreign_key_violation THEN
        RETURN 'Booking failed: Invalid patient or slot.';
    WHEN unique_violation THEN
        RETURN 'Booking failed: Duplicate appointment.';
    WHEN OTHERS THEN
        RETURN 'Booking failed: ' || SQLERRM;
END
$$;

CREATE OR REPLACE FUNCTION cancel_appointment(
    p_appointment_id INTEGER
)
RETURNS TEXT
LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE Appointments
    SET status = 'cancelled'
    WHERE appointment_id = p_appointment_id;
    IF NOT FOUND THEN
        RETURN 'Cancellation failed: Appointment not found.';
    END IF;
    RETURN 'Appointment cancelled successfully.';
EXCEPTION
    WHEN OTHERS THEN
        RETURN 'Cancellation failed: ' || SQLERRM;
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
    slot_available BOOLEAN;
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM Appointments
        WHERE appointment_id = p_appointment_id
    ) THEN
        RETURN 'Reschedule failed: Appointment not found.';
    END IF;
    SELECT is_available
    INTO slot_available
    FROM Slots
    WHERE slot_id = p_new_slot;
    IF NOT FOUND THEN
        RETURN 'Reschedule failed: Slot does not exist.';
    END IF;
    IF slot_available = FALSE THEN
        RETURN 'Reschedule failed: Selected slot is unavailable.';
    END IF;
    UPDATE Appointments
    SET slot_id = p_new_slot,
        scheduling_date = CURRENT_DATE
    WHERE appointment_id = p_appointment_id;
    RETURN 'Appointment rescheduled successfully.';
EXCEPTION
    WHEN foreign_key_violation THEN
        RETURN 'Reschedule failed: Invalid slot.';

    WHEN OTHERS THEN
        RETURN 'Reschedule failed: ' || SQLERRM;
END;
$$;