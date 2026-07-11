package dao;

import database.DatabaseManager;
import model.Slot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SlotDAO {

    public List<Slot> getAvailableSlots() {

        String sql = """
                SELECT
                    slot_id,
                    doctor_id,
                    appointment_date,
                    appointment_time
                FROM Slots
                WHERE is_available = TRUE
                ORDER BY appointment_date,
                        appointment_time
                """;

        List<Slot> slots = new ArrayList<>();

        try (Connection c = DatabaseManager.openConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                slots.add(new Slot(
                        rs.getInt("slot_id"),
                        rs.getInt("doctor_id"),
                        rs.getDate("appointment_date").toLocalDate(),
                        rs.getTime("appointment_time").toLocalTime()
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Unable to load available slots", e);
        }

        return slots;
    }

    
    public Slot getSlotById(int slotId) {

        String sql = """
                SELECT
                    slot_id,
                    doctor_id,
                    appointment_date,
                    appointment_time
                FROM Slots
                WHERE slot_id = ?
                """;

        try (Connection c = DatabaseManager.openConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, slotId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return new Slot(
                            rs.getInt("slot_id"),
                            rs.getInt("doctor_id"),
                            rs.getDate("appointment_date").toLocalDate(),
                            rs.getTime("appointment_time").toLocalTime()
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Unable to fetch slot", e);
        }

        return null;
    }
    public List<Slot> getAvailableSlotsByDoctor(int doctorId) {

    String sql = """
            SELECT
                slot_id,
                doctor_id,
                appointment_date,
                appointment_time
            FROM Slots
            WHERE doctor_id = ?
              AND is_available = TRUE
            ORDER BY appointment_date,
                     appointment_time
            """;

    List<Slot> slots = new ArrayList<>();

    try (Connection c = DatabaseManager.openConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setInt(1, doctorId);

        try (ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                slots.add(new Slot(
                        rs.getInt("slot_id"),
                        rs.getInt("doctor_id"),
                        rs.getDate("appointment_date").toLocalDate(),
                        rs.getTime("appointment_time").toLocalTime()
                ));

            }

        }

    } catch (SQLException e) {
        throw new RuntimeException("Unable to load doctor's slots", e);
    }

    return slots;
}
}