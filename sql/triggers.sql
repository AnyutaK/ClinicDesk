CREATE OR REPLACE FUNCTION appointment_trigger()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        UPDATE Slots
        SET is_available = FALSE
        WHERE slot_id = NEW.slot_id;
        INSERT INTO Audit_Log(appointment_id, action)
        VALUES (NEW.appointment_id, 'Appointment Created');
        RETURN NEW;
    END IF;

    IF TG_OP = 'UPDATE' THEN
        IF OLD.slot_id <> NEW.slot_id THEN
            UPDATE Slots
            SET is_available = TRUE
            WHERE slot_id = OLD.slot_id;
            UPDATE Slots
            SET is_available = FALSE
            WHERE slot_id = NEW.slot_id;
            INSERT INTO Audit_Log
            (appointment_id, action)
            VALUES
            (NEW.appointment_id,'Appointment Rescheduled');
        END IF;
        IF OLD.status <> 'cancelled'
           AND NEW.status='cancelled' THEN
            UPDATE Slots
            SET is_available=TRUE
            WHERE slot_id=NEW.slot_id;
            INSERT INTO Audit_Log
            VALUES
            (
                DEFAULT,
                NEW.appointment_id,
                'Appointment Cancelled',
                DEFAULT
            );
        END IF;
        IF OLD.status='cancelled'
           AND NEW.status<>'cancelled' THEN
            UPDATE Slots
            SET is_available=FALSE
            WHERE slot_id=NEW.slot_id;
            INSERT INTO Audit_Log
            VALUES
            (
                DEFAULT,
                NEW.appointment_id,
                'Appointment Reactivated',
                DEFAULT
            );
        END IF;
        RETURN NEW;
    END IF;
    RETURN NULL;
END;
$$;

CREATE TRIGGER trg_appointment
AFTER INSERT OR UPDATE
ON Appointments
FOR EACH ROW
EXECUTE FUNCTION appointment_trigger();