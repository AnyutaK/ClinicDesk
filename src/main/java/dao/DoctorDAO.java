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
        String sql = " SELECT\n" + //
                        "doctor_id,\n" + //
                        "doctor_name,\n" + //
                        "department,\n" + //
                        "specialization,\n" + //
                        "phone,\n" + //
                        "email\n" + //
                        "FROM search_doctors(?)";
        List<Doctor> results = new ArrayList<>();

        try (Connection connection = DatabaseManager.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, keyword == null ? "" : keyword.trim());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int doctorId = resultSet.getInt("doctor_id");
                    String doctorName = resultSet.getString("doctor_name");
                    String department = resultSet.getString("department");
                    String specialization = resultSet.getString("specialization");
                    String phone = resultSet.getString("phone");
                    String email = resultSet.getString("email");
                    results.add(new Doctor(doctorId, doctorName, department, specialization, phone, email));
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to search doctors", exception);
        }

        return results;
    }

public Doctor getDoctorById(int id) {

    String sql = """
            SELECT
                doctor_id,
                doctor_name,
                department,
                specialization,
                phone,
                email
            FROM Doctors
            WHERE doctor_id = ?
            """;

    try (Connection c = DatabaseManager.openConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setInt(1, id);

        try (ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {

                return new Doctor(
                        rs.getInt("doctor_id"),
                        rs.getString("doctor_name"),
                        rs.getString("department"),
                        rs.getString("specialization"),
                        rs.getString("phone"),
                        rs.getString("email")
                );

            }

        }

    } catch (SQLException e) {
        throw new RuntimeException("Unable to fetch doctor", e);
    }

    return null;
}
public Doctor createDoctor(String doctorName,
                           String department,
                           String specialization,
                           String phone,
                           String email) {

    String sql = """
            INSERT INTO Doctors
            (doctor_name, department, specialization, phone, email)
            VALUES (?, ?, ?, ?, ?)
            RETURNING doctor_id
            """;

    try (Connection c = DatabaseManager.openConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setString(1, doctorName);
        ps.setString(2, department);
        ps.setString(3, specialization);
        ps.setString(4, phone);
        ps.setString(5, email);

        try (ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {

                return new Doctor(
                        rs.getInt(1),
                        doctorName,
                        department,
                        specialization,
                        phone,
                        email
                );

            }

        }

    } catch (SQLException e) {
        throw new RuntimeException("Unable to create doctor", e);
    }

    return null;
}
public boolean updateDoctor(int id,
                            String doctorName,
                            String department,
                            String specialization,
                            String phone,
                            String email) {

    String sql = """
            UPDATE Doctors
            SET doctor_name = ?,
                department = ?,
                specialization = ?,
                phone = ?,
                email = ?
            WHERE doctor_id = ?
            """;

    try (Connection c = DatabaseManager.openConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setString(1, doctorName);
        ps.setString(2, department);
        ps.setString(3, specialization);
        ps.setString(4, phone);
        ps.setString(5, email);
        ps.setInt(6, id);

        return ps.executeUpdate() > 0;

    } catch (SQLException e) {
        throw new RuntimeException("Unable to update doctor", e);
    }
}
public boolean deleteDoctor(int id) {

    String sql = "DELETE FROM Doctors WHERE doctor_id = ?";

    try (Connection c = DatabaseManager.openConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setInt(1, id);

        return ps.executeUpdate() > 0;

    } catch (SQLException e) {
        throw new RuntimeException("Unable to delete doctor", e);
    }
}
}
