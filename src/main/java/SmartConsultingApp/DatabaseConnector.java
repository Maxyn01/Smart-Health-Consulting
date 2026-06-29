package SmartConsultingApp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConnector {

    // Method to connect to the database
    public static Connection connect() {
        try {
            Properties props = new Properties();
            try (InputStream input = DatabaseConnector.class.getClassLoader().getResourceAsStream("config.properties")) {
                if (input == null) {
                    System.out.println("Sorry, unable to find config.properties");
                    return null;
                }
                props.load(input);
            }

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    props.getProperty("db.url"),
                    props.getProperty("db.user"),
                    props.getProperty("db.password")
            );
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ============================================
    // ✅ INSERT INTO: USERS TABLE (SIGNUP)
    // ============================================
    public static boolean insertSignup(String username, String password, String email, String role) {
        Connection con = connect();
        if (con == null) return false;
        try {
            String query = "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);
            stmt.setString(4, role);

            int rows = stmt.executeUpdate();
            con.close();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ============================================
    // ✅ INSERT INTO: APPOINTMENTS TABLE
    // ============================================
    public static boolean insertAppointment(int doctor_id, int patient_id, String date, String time) {
        Connection con = connect();
        if (con == null) return false;
        try {
            String query = "INSERT INTO appointments (doctor_id, patient_id, appointment_date, appointment_time) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, doctor_id);
            stmt.setInt(2, patient_id);
            stmt.setString(3, date);
            stmt.setString(4, time);

            int rows = stmt.executeUpdate();
            con.close();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ============================================
    // ✅ INSERT INTO: PRESCRIPTIONS TABLE
    // ============================================
    public static boolean insertPrescription(int appointment_id, String medicine, String dosage, String duration) {
        Connection con = connect();
        if (con == null) return false;
        try {
            String query = "INSERT INTO prescriptions (appointment_id, medicine, dosage, duration) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, appointment_id);
            stmt.setString(2, medicine);
            stmt.setString(3, dosage);
            stmt.setString(4, duration);

            int rows = stmt.executeUpdate();
            con.close();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ============================================
    // ✅ INSERT INTO: INVOICES TABLE
    // ============================================
    public static boolean insertInvoice(int appointment_id, double amount, String payment_status) {
        Connection con = connect();
        if (con == null) return false;
        try {
            String query = "INSERT INTO invoices (appointment_id, amount, payment_status) VALUES (?, ?, ?)";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, appointment_id);
            stmt.setDouble(2, amount);
            stmt.setString(3, payment_status);

            int rows = stmt.executeUpdate();
            con.close();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ============================================
    // ✅ INSERT INTO: MEDICAL HISTORY TABLE
    // ============================================
    public static boolean insertMedicalHistory(int patient_id, String condition, String treatment, String doctor_notes) {
        Connection con = connect();
        if (con == null) return false;
        try {
            String query = "INSERT INTO medical_history (patient_id, condition, treatment, doctor_notes) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, patient_id);
            stmt.setString(2, condition);
            stmt.setString(3, treatment);
            stmt.setString(4, doctor_notes);

            int rows = stmt.executeUpdate();
            con.close();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
