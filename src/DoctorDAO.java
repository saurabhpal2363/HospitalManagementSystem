package src;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {

    public boolean addDoctor(Doctor d) {
        String sql = "INSERT INTO doctors (name, specialization, phone, available_days, available_time) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, d.getName());
            ps.setString(2, d.getSpecialization());
            ps.setString(3, d.getPhone());
            ps.setString(4, d.getAvailableDays());
            ps.setString(5, d.getAvailableTime());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding doctor: " + e.getMessage());
            return false;
        }
    }

    public boolean updateDoctor(Doctor d) {
        String sql = "UPDATE doctors SET name=?, specialization=?, phone=?, available_days=?, available_time=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, d.getName());
            ps.setString(2, d.getSpecialization());
            ps.setString(3, d.getPhone());
            ps.setString(4, d.getAvailableDays());
            ps.setString(5, d.getAvailableTime());
            ps.setInt(6, d.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating doctor: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteDoctor(int id) {
        String sql = "DELETE FROM doctors WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting doctor: " + e.getMessage());
            return false;
        }
    }

    public List<Doctor> getAllDoctors() {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT * FROM doctors ORDER BY name";
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Doctor d = new Doctor();
                d.setId(rs.getInt("id"));
                d.setName(rs.getString("name"));
                d.setSpecialization(rs.getString("specialization"));
                d.setPhone(rs.getString("phone"));
                d.setAvailableDays(rs.getString("available_days"));
                d.setAvailableTime(rs.getString("available_time"));
                list.add(d);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching doctors: " + e.getMessage());
        }
        return list;
    }
}