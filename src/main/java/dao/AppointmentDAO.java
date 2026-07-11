package dao;

import database.DatabaseManager;
import model.Appointment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {
public List<Appointment> searchAppointments(String keyword) {

    String sql = """
        SELECT
            a.appointment_id,
            p.patient_id,
            p.name AS patient_name,
            d.doctor_id,
            d.doctor_name,
            s.slot_id,
            CAST(
                s.appointment_date + s.appointment_time
                AS timestamp
            ) AS appointment_datetime,
            a.status
        FROM Appointments a
        JOIN Patients p
            ON a.patient_id = p.patient_id
        JOIN Slots s
            ON a.slot_id = s.slot_id
        JOIN Doctors d
            ON s.doctor_id = d.doctor_id
        WHERE
            p.name ILIKE ?
            OR d.doctor_name ILIKE ?
            OR a.status ILIKE ?
        ORDER BY
            s.appointment_date,
            s.appointment_time
        """;

    List<Appointment> appointments = new ArrayList<>();

    try (Connection c = DatabaseManager.openConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        String search = "%" + (keyword == null ? "" : keyword.trim()) + "%";

        ps.setString(1, search);
        ps.setString(2, search);
        ps.setString(3, search);

        try (ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                appointments.add(new Appointment(

                        rs.getInt("appointment_id"),

                        rs.getInt("patient_id"),
                        rs.getString("patient_name"),

                        rs.getInt("doctor_id"),
                        rs.getString("doctor_name"),

                        rs.getInt("slot_id"),

                        rs.getTimestamp("appointment_datetime"),

                        rs.getString("status")

                ));

            }

        }

    } catch (SQLException e) {
        throw new RuntimeException("Unable to search appointments", e);
    }

    return appointments;
}
public Appointment getAppointmentById(int id) {

    String sql = """
        SELECT
            a.appointment_id,
            p.patient_id,
            p.name AS patient_name,
            d.doctor_id,
            d.doctor_name,
            s.slot_id,
            CAST(
                s.appointment_date + s.appointment_time
                AS timestamp
            ) AS appointment_datetime,
            a.status
        FROM Appointments a
        JOIN Patients p
            ON a.patient_id = p.patient_id
        JOIN Slots s
            ON a.slot_id = s.slot_id
        JOIN Doctors d
            ON s.doctor_id = d.doctor_id
        WHERE a.appointment_id = ?
        """;

    try (Connection c = DatabaseManager.openConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setInt(1, id);

        try (ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {

                return new Appointment(

                        rs.getInt("appointment_id"),

                        rs.getInt("patient_id"),
                        rs.getString("patient_name"),

                        rs.getInt("doctor_id"),
                        rs.getString("doctor_name"),

                        rs.getInt("slot_id"),

                        rs.getTimestamp("appointment_datetime"),

                        rs.getString("status")

                );

            }

        }

    } catch (SQLException e) {
        throw new RuntimeException("Unable to fetch appointment", e);
    }

    return null;
}
public Appointment createAppointment(
        int patientId,
        int slotId,
        Date schedulingDate,
        String status) {

    String sql = """
        INSERT INTO Appointments
        (patient_id, slot_id, scheduling_date, status)
        VALUES (?, ?, ?, ?)
        RETURNING appointment_id
        """;

    try (Connection c = DatabaseManager.openConnection()) {

        c.setAutoCommit(false);

        try (PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, patientId);
            ps.setInt(2, slotId);
            ps.setDate(3, schedulingDate);
            ps.setString(4, status);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                int appointmentId = rs.getInt(1);

                PreparedStatement updateSlot = c.prepareStatement("""
                    UPDATE Slots
                    SET is_available = FALSE
                    WHERE slot_id = ?
                """);

                updateSlot.setInt(1, slotId);
                updateSlot.executeUpdate();

                c.commit();

                return getAppointmentById(appointmentId);
            }

            c.rollback();

        } catch (SQLException e) {

            c.rollback();
            throw e;

        }

    } catch (SQLException e) {

        throw new RuntimeException("Unable to create appointment", e);

    }

    return null;
}
public boolean updateAppointment(
        int appointmentId,
        int patientId,
        int newSlotId,
        Date schedulingDate,
        String status) {

    String getOldSlotSql = """
            SELECT slot_id
            FROM Appointments
            WHERE appointment_id = ?
            """;

    String updateAppointmentSql = """
            UPDATE Appointments
            SET patient_id = ?,
                slot_id = ?,
                scheduling_date = ?,
                status = ?
            WHERE appointment_id = ?
            """;

    String freeOldSlotSql = """
            UPDATE Slots
            SET is_available = TRUE
            WHERE slot_id = ?
            """;

    String reserveNewSlotSql = """
            UPDATE Slots
            SET is_available = FALSE
            WHERE slot_id = ?
            """;

    try (Connection c = DatabaseManager.openConnection()) {

        c.setAutoCommit(false);

        try {

            int oldSlotId;

            // Get current slot
            try (PreparedStatement ps = c.prepareStatement(getOldSlotSql)) {

                ps.setInt(1, appointmentId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {
                        c.rollback();
                        return false;
                    }

                    oldSlotId = rs.getInt("slot_id");
                }
            }

            // Free old slot if changed
            if (oldSlotId != newSlotId) {

                try (PreparedStatement ps = c.prepareStatement(freeOldSlotSql)) {

                    ps.setInt(1, oldSlotId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = c.prepareStatement(reserveNewSlotSql)) {

                    ps.setInt(1, newSlotId);
                    ps.executeUpdate();
                }
            }

            // Update appointment
            try (PreparedStatement ps = c.prepareStatement(updateAppointmentSql)) {

                ps.setInt(1, patientId);
                ps.setInt(2, newSlotId);
                ps.setDate(3, schedulingDate);
                ps.setString(4, status);
                ps.setInt(5, appointmentId);

                int rows = ps.executeUpdate();

                if (rows == 0) {
                    c.rollback();
                    return false;
                }
            }

            c.commit();
            return true;

        } catch (SQLException e) {

            c.rollback();
            throw e;
        }

    } catch (SQLException e) {
        throw new RuntimeException("Unable to update appointment", e);
    }
}
public boolean deleteAppointment(int appointmentId) {

    String getSlotSql = """
            SELECT slot_id
            FROM Appointments
            WHERE appointment_id = ?
            """;

    String deleteAppointmentSql = """
            DELETE FROM Appointments
            WHERE appointment_id = ?
            """;

    String freeSlotSql = """
            UPDATE Slots
            SET is_available = TRUE
            WHERE slot_id = ?
            """;

    try (Connection c = DatabaseManager.openConnection()) {

        c.setAutoCommit(false);

        try {

            int slotId;

            // Get slot ID
            try (PreparedStatement ps = c.prepareStatement(getSlotSql)) {

                ps.setInt(1, appointmentId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {
                        c.rollback();
                        return false;
                    }

                    slotId = rs.getInt("slot_id");
                }
            }

            // Delete appointment
            try (PreparedStatement ps = c.prepareStatement(deleteAppointmentSql)) {

                ps.setInt(1, appointmentId);

                if (ps.executeUpdate() == 0) {
                    c.rollback();
                    return false;
                }
            }

            // Free slot
            try (PreparedStatement ps = c.prepareStatement(freeSlotSql)) {

                ps.setInt(1, slotId);
                ps.executeUpdate();
            }

            c.commit();
            return true;

        } catch (SQLException e) {

            c.rollback();
            throw e;
        }

    } catch (SQLException e) {

        throw new RuntimeException("Unable to delete appointment", e);

    }
}
}