package dao;

import database.DatabaseManager;
import model.Patient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    public List<Patient> searchPatients(String keyword) {
        String sql = "SELECT patient_id, name, insurance FROM search_patients(?)";
        List<Patient> results = new ArrayList<>();

        try (Connection connection = DatabaseManager.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, keyword == null ? "" : keyword.trim());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int patientId = resultSet.getInt("patient_id");
                    String name = resultSet.getString("name");
                    String insurance = resultSet.getString("insurance");
                    results.add(new Patient(patientId, name, insurance));
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to search patients", exception);
        }

        return results;
    }

    public Patient getPatientById(int id) {
        String sql = "SELECT patient_id, name, insurance FROM Patients WHERE patient_id = ?";
        try (Connection c = DatabaseManager.openConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Patient(rs.getInt("patient_id"), rs.getString("name"), rs.getString("insurance"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to fetch patient", e);
        }
        return null;
    }

    public Patient createPatient(String name, java.sql.Date dob, String insurance) {
        String sql = "INSERT INTO Patients (name, dob, insurance) VALUES (?, ?, ?) RETURNING patient_id";
        try (Connection c = DatabaseManager.openConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setDate(2, dob);
            ps.setString(3, insurance);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return new Patient(id, name, insurance);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to create patient", e);
        }
        return null;
    }

    public boolean deletePatient(int id) {
        String sql = "DELETE FROM Patients WHERE patient_id = ?";
        try (Connection c = DatabaseManager.openConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            int updated = ps.executeUpdate();
            return updated > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Unable to delete patient", e);
        }
    }
}
