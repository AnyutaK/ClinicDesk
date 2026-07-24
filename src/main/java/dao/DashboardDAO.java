package dao;

import database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardDAO {


    public int getTodayAppointments() {

        String sql = """
            SELECT COUNT(*)
            FROM Appointments
            WHERE scheduling_date = (
                SELECT MAX(scheduling_date)
                FROM Appointments
            );
        """;

        return executeCount(sql);
    }


    public int getWaitingPatients() {

        String sql = """
            SELECT COUNT(*)
            FROM Appointments
            WHERE status = 'Scheduled'
        """;

        return executeCount(sql);
    }


    public int getDoctorsOnDuty() {

        String sql = """
            SELECT COUNT(*)
            FROM Doctors;
        """;

        return executeCount(sql);
    }


    public int getUtilization() {

    String sql = """
        SELECT
            ROUND(
                (
                    COUNT(*) FILTER (WHERE is_available = FALSE) * 100.0
                    / COUNT(*)
                )
            )
        FROM Slots
        """;

    return executeCount(sql);
}
    private int executeCount(String sql) {

        try (
            Connection c = DatabaseManager.openConnection();
            PreparedStatement ps = c.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            if(rs.next()) {
                return rs.getInt(1);
            }

        } catch(SQLException e) {
            throw new RuntimeException("Dashboard query failed", e);
        }

        return 0;
    }
}