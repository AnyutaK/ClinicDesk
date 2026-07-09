package dao;

import database.DatabaseManager;
import model.Doctor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {

    public List<Doctor> searchDoctors(String keyword) {
        String sql = "SELECT doctor_id, doctor_name, department FROM search_doctors(?)";
        List<Doctor> results = new ArrayList<>();

        try (Connection connection = DatabaseManager.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, keyword == null ? "" : keyword.trim());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int doctorId = resultSet.getInt("doctor_id");
                    String doctorName = resultSet.getString("doctor_name");
                    String department = resultSet.getString("department");
                    results.add(new Doctor(doctorId, doctorName, department));
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to search doctors", exception);
        }

        return results;
    }
}
