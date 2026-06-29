package SmartConsultingApp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class AdminModule extends JFrame {
    JTabbedPane tabbedPane;
    JTable patientTable, doctorTable, invoiceTable;
    JLabel totalRevenueLabel;

    public AdminModule() {
        setTitle("Admin Module");
        setSize(1000, 600); // Increased width for better visibility
        setLayout(new BorderLayout());

        // Add the header panel to the top (NORTH) of the layout
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Create and add the tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Patient Management", createUserManagementPanel());
        tabbedPane.addTab("Doctor Management", createDoctorManagementPanel());
        tabbedPane.addTab("Invoice & Payment Management", createInvoiceManagementPanel());
        add(tabbedPane, BorderLayout.CENTER);

        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    // Panel 1: User Management (View All Patients)
    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table for User Details
        patientTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(patientTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Buttons for Update and Delete
        JButton btnUpdatePatient = new JButton("Update Patient");
        JButton btnDeletePatient = new JButton("Delete Patient");
        btnUpdatePatient.addActionListener(this::updatePatient);
        btnDeletePatient.addActionListener(this::deletePatient);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnUpdatePatient);
        buttonPanel.add(btnDeletePatient);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Load Patient Data from Database
        loadPatientData();
        return panel;
    }

    // Panel 2: Doctor Management (View All Doctors)
    private JPanel createDoctorManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table for Doctor Details
        doctorTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(doctorTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Buttons for Update and Delete
        JButton btnUpdateDoctor = new JButton("Update Doctor");
        JButton btnDeleteDoctor = new JButton("Delete Doctor");
        btnUpdateDoctor.addActionListener(this::updateDoctor);
        btnDeleteDoctor.addActionListener(this::deleteDoctor);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnUpdateDoctor);
        buttonPanel.add(btnDeleteDoctor);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Load Doctor Data from Database
        loadDoctorData();
        return panel;
    }

    // Panel 3: Invoice and Payment Management (View All Payments)
    private JPanel createInvoiceManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table for Invoices
        invoiceTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(invoiceTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Revenue Section
        totalRevenueLabel = new JLabel("Total Revenue: ₹0");
        totalRevenueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(totalRevenueLabel, BorderLayout.SOUTH);

        // Load Invoice Data and Calculate Revenue
        loadInvoiceData();
        return panel;
    }

    // Load ALL Patient Data (Without Duplicate S_id)
    private void loadPatientData() {
        try (Connection con = DatabaseConnector.connect()) {
            String query = "SELECT P.P_id, S.Name, S.Email, S.Gender, P.Medical_History, P.Allergies " +
                    "FROM Patient P " +
                    "INNER JOIN Signup S ON P.S_id = S.S_id WHERE S.Role = 'Patient'";
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            // Table Model
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Patient ID");
            model.addColumn("Name");
            model.addColumn("Email");
            model.addColumn("Gender");
            model.addColumn("Medical History");
            model.addColumn("Allergies");

            // Add Data Rows
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("P_id"),
                        rs.getString("Name"),
                        rs.getString("Email"),
                        rs.getString("Gender"),
                        rs.getString("Medical_History"),
                        rs.getString("Allergies")
                });
            }
            patientTable.setModel(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Load ALL Doctor Data (Without Duplicate S_id)
    private void loadDoctorData() {
        try (Connection con = DatabaseConnector.connect()) {
            String query = "SELECT D.D_id, S.Name, S.Email, S.Gender, D.Specialization, D.Experience " +
                    "FROM Doctor D " +
                    "INNER JOIN Signup S ON D.S_id = S.S_id WHERE S.Role = 'Doctor'";
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            // Table Model
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Doctor ID");
            model.addColumn("Name");
            model.addColumn("Email");
            model.addColumn("Gender");
            model.addColumn("Specialization");
            model.addColumn("Experience (Years)");

            // Add Data Rows
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("D_id"),
                        rs.getString("Name"),
                        rs.getString("Email"),
                        rs.getString("Gender"),
                        rs.getString("Specialization"),
                        rs.getInt("Experience")
                });
            }
            doctorTable.setModel(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Load ALL Payment Data and Calculate Total Revenue
    private void loadInvoiceData() {
        try (Connection con = DatabaseConnector.connect()) {
            String query = "SELECT Payment_id, P_id, Invoice_No, Amount, Payment_Date FROM payment";
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            // Table Model
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Payment ID");
            model.addColumn("Patient ID");
            model.addColumn("Invoice No.");
            model.addColumn("Amount (₹)");
            model.addColumn("Payment Date");

            double totalRevenue = 0;

            // Add Data Rows
            while (rs.next()) {
                double amount = rs.getDouble("Amount");
                totalRevenue += amount; // Accumulate total revenue
                model.addRow(new Object[]{
                        rs.getInt("Payment_id"),
                        rs.getInt("P_id"),
                        rs.getString("Invoice_No"),
                        "₹" + amount,
                        rs.getTimestamp("Payment_Date")
                });
            }
            invoiceTable.setModel(model);

            // Update Revenue Label
            totalRevenueLabel.setText("Total Revenue: ₹" + totalRevenue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Update Patient
    private void updatePatient(ActionEvent e) {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient to update!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int patientId = (int) patientTable.getValueAt(selectedRow, 0);
        String name = (String) patientTable.getValueAt(selectedRow, 1);
        String email = (String) patientTable.getValueAt(selectedRow, 2);
        String gender = (String) patientTable.getValueAt(selectedRow, 3);
        String medicalHistory = (String) patientTable.getValueAt(selectedRow, 4);
        String allergies = (String) patientTable.getValueAt(selectedRow, 5);

        // Input Dialogs for Updating Fields
        String newName = JOptionPane.showInputDialog(this, "Enter New Name:", name);
        String newEmail = JOptionPane.showInputDialog(this, "Enter New Email:", email);
        String newGender = JOptionPane.showInputDialog(this, "Enter New Gender (Male/Female/Other):", gender);
        String newMedicalHistory = JOptionPane.showInputDialog(this, "Enter New Medical History:", medicalHistory);
        String newAllergies = JOptionPane.showInputDialog(this, "Enter New Allergies:", allergies);

        try (Connection con = DatabaseConnector.connect()) {
            // Update Signup Table
            String updateSignupQuery = "UPDATE Signup SET Name = ?, Email = ?, Gender = ? WHERE S_id = (SELECT S_id FROM Patient WHERE P_id = ?)";
            PreparedStatement signupPst = con.prepareStatement(updateSignupQuery);
            signupPst.setString(1, newName);
            signupPst.setString(2, newEmail);
            signupPst.setString(3, newGender);
            signupPst.setInt(4, patientId);
            signupPst.executeUpdate();

            // Update Patient Table
            String updatePatientQuery = "UPDATE Patient SET Medical_History = ?, Allergies = ? WHERE P_id = ?";
            PreparedStatement patientPst = con.prepareStatement(updatePatientQuery);
            patientPst.setString(1, newMedicalHistory);
            patientPst.setString(2, newAllergies);
            patientPst.setInt(3, patientId);
            patientPst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Patient Updated Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadPatientData(); // Refresh the table
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating patient!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Delete Patient
    private void deletePatient(ActionEvent e) {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient to delete!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int patientId = (int) patientTable.getValueAt(selectedRow, 0);

        try (Connection con = DatabaseConnector.connect()) {
            // Delete from Patient Table
            String deletePatientQuery = "DELETE FROM Patient WHERE P_id = ?";
            PreparedStatement patientPst = con.prepareStatement(deletePatientQuery);
            patientPst.setInt(1, patientId);
            patientPst.executeUpdate();

            // Delete from Signup Table
            String deleteSignupQuery = "DELETE FROM Signup WHERE S_id = (SELECT S_id FROM Patient WHERE P_id = ?)";
            PreparedStatement signupPst = con.prepareStatement(deleteSignupQuery);
            signupPst.setInt(1, patientId);
            signupPst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Patient Deleted Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadPatientData(); // Refresh the table
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting patient!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Update Doctor
    private void updateDoctor(ActionEvent e) {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a doctor to update!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int doctorId = (int) doctorTable.getValueAt(selectedRow, 0);
        String name = (String) doctorTable.getValueAt(selectedRow, 1);
        String email = (String) doctorTable.getValueAt(selectedRow, 2);
        String gender = (String) doctorTable.getValueAt(selectedRow, 3);
        String specialization = (String) doctorTable.getValueAt(selectedRow, 4);
        int experience = (int) doctorTable.getValueAt(selectedRow, 5);

        // Input Dialogs for Updating Fields
        String newName = JOptionPane.showInputDialog(this, "Enter New Name:", name);
        String newEmail = JOptionPane.showInputDialog(this, "Enter New Email:", email);
        String newGender = JOptionPane.showInputDialog(this, "Enter New Gender (Male/Female/Other):", gender);
        String newSpecialization = JOptionPane.showInputDialog(this, "Enter New Specialization:", specialization);
        String newExperience = JOptionPane.showInputDialog(this, "Enter New Experience (in Years):", experience);

        try (Connection con = DatabaseConnector.connect()) {
            // Update Signup Table
            String updateSignupQuery = "UPDATE Signup SET Name = ?, Email = ?, Gender = ? WHERE S_id = (SELECT S_id FROM Doctor WHERE D_id = ?)";
            PreparedStatement signupPst = con.prepareStatement(updateSignupQuery);
            signupPst.setString(1, newName);
            signupPst.setString(2, newEmail);
            signupPst.setString(3, newGender);
            signupPst.setInt(4, doctorId);
            signupPst.executeUpdate();

            // Update Doctor Table
            String updateDoctorQuery = "UPDATE Doctor SET Specialization = ?, Experience = ? WHERE D_id = ?";
            PreparedStatement doctorPst = con.prepareStatement(updateDoctorQuery);
            doctorPst.setString(1, newSpecialization);
            doctorPst.setInt(2, Integer.parseInt(newExperience));
            doctorPst.setInt(3, doctorId);
            doctorPst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Doctor Updated Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadDoctorData(); // Refresh the table
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating doctor!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Delete Doctor
    private void deleteDoctor(ActionEvent e) {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a doctor to delete!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int doctorId = (int) doctorTable.getValueAt(selectedRow, 0);

        try (Connection con = DatabaseConnector.connect()) {
            // Delete from Doctor Table
            String deleteDoctorQuery = "DELETE FROM Doctor WHERE D_id = ?";
            PreparedStatement doctorPst = con.prepareStatement(deleteDoctorQuery);
            doctorPst.setInt(1, doctorId);
            doctorPst.executeUpdate();

            // Delete from Signup Table
            String deleteSignupQuery = "DELETE FROM Signup WHERE S_id = (SELECT S_id FROM Doctor WHERE D_id = ?)";
            PreparedStatement signupPst = con.prepareStatement(deleteSignupQuery);
            signupPst.setInt(1, doctorId);
            signupPst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Doctor Deleted Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadDoctorData(); // Refresh the table
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting doctor!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Header Panel for Admin Module
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185)); // Primary blue color
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding

        // Title Label
        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Bold font
        titleLabel.setForeground(Color.WHITE); // White text
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Profile and Logout Buttons
        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        profilePanel.setOpaque(false); // Transparent background

        // Profile Button
        JButton profileButton = new JButton("Profile");
        profileButton.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Set font
        profileButton.setForeground(Color.WHITE); // White text
        profileButton.setBackground(new Color(41, 128, 185)); // Same as header background
        profileButton.setFocusPainted(false); // Remove focus border
        profileButton.setBorderPainted(false); // Remove border
        profileButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Hand cursor on hover
        profileButton.addActionListener(e -> showAdminInfo()); // Show admin info
        profilePanel.add(profileButton);

        // Logout Button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Set font
        logoutButton.setForeground(Color.WHITE); // White text
        logoutButton.setBackground(new Color(41, 128, 185)); // Same as header background
        logoutButton.setFocusPainted(false); // Remove focus border
        logoutButton.setBorderPainted(false); // Remove border
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Hand cursor on hover
        logoutButton.addActionListener(e -> confirmLogout()); // Confirm logout
        profilePanel.add(logoutButton);

        headerPanel.add(profilePanel, BorderLayout.EAST);
        return headerPanel;
    }

    // Show Admin Information
    private void showAdminInfo() {
        String[] adminData = fetchAdminInfo();
        StringBuilder infoMessage = new StringBuilder();
        infoMessage.append("<html><body style='font-family: Segoe UI;'>");
        infoMessage.append("<h3>Admin Information</h3>");
        infoMessage.append("<p><b>Name:</b> ").append(adminData[0]).append("</p>");
        infoMessage.append("<p><b>Username:</b> ").append(adminData[1]).append("</p>");
        infoMessage.append("<p><b>Email:</b> ").append(adminData[2]).append("</p>");
        infoMessage.append("</body></html>");
        JOptionPane.showMessageDialog(this, infoMessage.toString(), "Admin Profile",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // Fetch Admin Info from Database
    private String[] fetchAdminInfo() {
        String[] data = {"Not Found", "Not Found", "Not Found"};
        try (Connection con = DatabaseConnector.connect()) {
            String query = """
                SELECT Name, Username, Email
                FROM Signup
                WHERE Role = 'Admin'
            """;
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                data[0] = rs.getString("Name"); // Admin's Name
                data[1] = rs.getString("Username"); // Admin's Username
                data[2] = rs.getString("Email") != null ? rs.getString("Email") : "Not Provided";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    // Confirm Logout
    private void confirmLogout() {
        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?",
                "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            new LoginPage(); // Assuming LoginPage is your login screen class
            dispose(); // Close the current window
        }
    }

    public static void main(String[] args) {
        new AdminModule();
    }
}