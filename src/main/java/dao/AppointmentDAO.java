package dao;

import database.DatabaseManager;
import model.Slot;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    public List<Slot> getAvailableSlots(LocalDate date) {
        String sql = "SELECT slot_id, appointment_time FROM Slots WHERE appointment_date = ? AND is_available = TRUE ORDER BY appointment_time";
        List<Slot> slots = new ArrayList<>();
        try (Connection c = DatabaseManager.openConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("slot_id");
                    Time t = rs.getTime("appointment_time");
                    slots.add(new Slot(id, t.toLocalTime()));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to fetch slots", e);
        }
        return slots;
    }

    public List<String> getBookingsForDay(LocalDate date) {
        String sql = "SELECT s.appointment_time, p.name FROM Appointments a JOIN Slots s ON a.slot_id = s.slot_id JOIN Patients p ON a.patient_id = p.patient_id WHERE s.appointment_date = ? ORDER BY s.appointment_time";
        List<String> res = new ArrayList<>();
        try (Connection c = DatabaseManager.openConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Time t = rs.getTime(1);
                    String name = rs.getString(2);
                    res.add(t.toLocalTime().toString() + " — " + name);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to fetch bookings", e);
        }
        return res;
    }

    public String callBookAppointment(int patientId, int slotId) {
        String sql = "SELECT book_appointment(?, ?)";
        try (Connection c = DatabaseManager.openConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ps.setInt(2, slotId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Booking failed", e);
        }
        return "Booking failed: unknown error";
    }

    public java.util.Map<java.time.LocalDate, Integer> getAppointmentsCountByDay(int daysBack) {
        String sql = "SELECT s.appointment_date, COUNT(a.appointment_id) AS cnt " +
                "FROM Appointments a JOIN Slots s ON a.slot_id = s.slot_id " +
                "WHERE s.appointment_date >= CURRENT_DATE - (? - 1) " +
                "GROUP BY s.appointment_date ORDER BY s.appointment_date";
        java.util.Map<java.time.LocalDate, Integer> map = new java.util.LinkedHashMap<>();
        try (Connection c = DatabaseManager.openConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, daysBack);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getDate(1).toLocalDate(), rs.getInt("cnt"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to fetch appointment counts by day", e);
        }
        return map;
    }

    public java.util.Map<String, Integer> getAppointmentsByDepartment() {
        String sql = "SELECT d.department, COUNT(a.appointment_id) AS cnt " +
                "FROM Appointments a " +
                "JOIN Slots s ON a.slot_id = s.slot_id " +
                "JOIN Doctors d ON s.doctor_id = d.doctor_id " +
                "GROUP BY d.department ORDER BY cnt DESC";
        java.util.Map<String, Integer> map = new java.util.LinkedHashMap<>();
        try (Connection c = DatabaseManager.openConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getString(1), rs.getInt("cnt"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to fetch appointments by department", e);
        }
        return map;
    }
}
