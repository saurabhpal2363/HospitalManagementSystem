package src;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    public boolean bookAppointment(Appointment a) {
        String sql = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, appointment_time, status, notes) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, a.getPatientId());
            ps.setInt(2, a.getDoctorId());
            ps.setString(3, a.getAppointmentDate());
            ps.setString(4, a.getAppointmentTime());
            ps.setString(5, a.getStatus());
            ps.setString(6, a.getNotes());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error booking appointment: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE appointments SET status=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating status: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteAppointment(int id) {
        String sql = "DELETE FROM appointments WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting appointment: " + e.getMessage());
            return false;
        }
    }

    public List<Appointment> getAllAppointments() {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.id, a.appointment_date, a.appointment_time, a.status, a.notes, " +
                     "p.name AS patient_name, d.name AS doctor_name, a.patient_id, a.doctor_id " +
                     "FROM appointments a " +
                     "JOIN patients p ON a.patient_id = p.id " +
                     "JOIN doctors d ON a.doctor_id = d.id " +
                     "ORDER BY a.appointment_date DESC";
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Appointment a = new Appointment();
                a.setId(rs.getInt("id"));
                a.setPatientId(rs.getInt("patient_id"));
                a.setDoctorId(rs.getInt("doctor_id"));
                a.setPatientName(rs.getString("patient_name"));
                a.setDoctorName(rs.getString("doctor_name"));
                a.setAppointmentDate(rs.getString("appointment_date"));
                a.setAppointmentTime(rs.getString("appointment_time"));
                a.setStatus(rs.getString("status"));
                a.setNotes(rs.getString("notes"));
                list.add(a);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching appointments: " + e.getMessage());
        }
        return list;
    }
}