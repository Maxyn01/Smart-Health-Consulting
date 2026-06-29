package SmartConsultingApp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;
import java.awt.Desktop;
import java.net.URI;

public class DoctorModule extends JFrame {
    String username;
    JTable appointmentTable, consultationTable, patientTable;
    JButton btnStartConsultation, btnRefresh, btnUpdateStatus;
    JButton btnViewConsultationDetails, btnViewMedicalHistory, btnViewAllergies;
    JComboBox<String> statusComboBox;
    JTabbedPane tabbedPane;
    JTextArea prescriptionTextArea; // Text area for prescription
    JButton btnSavePrescription; // Button to save prescription

    public DoctorModule(String username) {
        this.username = username;
        setTitle("Doctor Module");
        setSize(800, 500);
        setLayout(new BorderLayout());

        add(createHeaderPanel(), BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();

        createAppointmentsTab();
        createConsultationTab();
        createPatientDetailsTab();

        add(tabbedPane, BorderLayout.CENTER);
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void createAppointmentsTab() {
        JPanel appointmentPanel = new JPanel(new BorderLayout());
        appointmentTable = new JTable(fetchAppointments(), new String[]{"Appointment ID", "Patient Name", "Date", "Slot Time", "Status"});
        JScrollPane scrollPane1 = new JScrollPane(appointmentTable);
        appointmentPanel.add(scrollPane1, BorderLayout.CENTER);

        btnStartConsultation = new JButton("Start Consultation");
        btnRefresh = new JButton("Refresh");

        btnStartConsultation.addActionListener(e -> startConsultation());
        btnRefresh.addActionListener(e -> refreshAllData());

        JPanel btnPanel1 = new JPanel();
        btnPanel1.add(btnStartConsultation);
        btnPanel1.add(btnRefresh);
        appointmentPanel.add(btnPanel1, BorderLayout.SOUTH);

        tabbedPane.add("View Appointments", appointmentPanel);
    }

    private void startConsultation() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment!");
            return;
        }

        int appointmentId = (int) appointmentTable.getValueAt(selectedRow, 0);
        String link = generateGoogleMeetLink();

        try (Connection con = DatabaseConnector.connect()) {
            String query = "UPDATE Book_Appointment SET Meet_Link = ? WHERE B_id = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, link);
            pst.setInt(2, appointmentId);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Generated Google Meet Link: " + link);
            Desktop.getDesktop().browse(new URI(link));

            refreshAllData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String generateGoogleMeetLink() {
        String uniqueID = UUID.randomUUID().toString().substring(0, 8);
        return "https://meet.google.com/" + uniqueID;
    }

    private void createConsultationTab() {
        JPanel consultationPanel = new JPanel(new BorderLayout());
        consultationTable = new JTable(fetchConsultations(), new String[]{"Consultation ID", "Patient Name", "Date", "Notes", "Status"});

        JScrollPane scrollPane2 = new JScrollPane(consultationTable);
        consultationPanel.add(scrollPane2, BorderLayout.CENTER);

        statusComboBox = new JComboBox<>(new String[]{"Pending", "Completed", "Cancelled"});
        btnUpdateStatus = new JButton("Update Status");

        btnUpdateStatus.addActionListener(e -> updateConsultationStatus());

        btnViewConsultationDetails = new JButton("View Details");
        btnViewConsultationDetails.addActionListener(e -> viewConsultationDetails());

        // Prescription area
        prescriptionTextArea = new JTextArea(5, 20);
        btnSavePrescription = new JButton("Save Prescription");
        btnSavePrescription.addActionListener(e -> savePrescription());

        JPanel btnPanel2 = new JPanel();
        btnPanel2.add(new JLabel("Status:"));
        btnPanel2.add(statusComboBox);
        btnPanel2.add(btnUpdateStatus);
        btnPanel2.add(btnViewConsultationDetails);
        btnPanel2.add(new JLabel("Prescription:"));
        btnPanel2.add(new JScrollPane(prescriptionTextArea));
        btnPanel2.add(btnSavePrescription);

        consultationPanel.add(btnPanel2, BorderLayout.SOUTH);
        tabbedPane.add("Consultations", consultationPanel);
    }

    private void savePrescription() {
        int selectedRow = consultationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a consultation!");
            return;
        }

        int consultationId = (int) consultationTable.getValueAt(selectedRow, 0);
        String prescription = prescriptionTextArea.getText();

        try (Connection con = DatabaseConnector.connect()) {
            String query = "UPDATE Consultation SET Prescription = ? WHERE C_id = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, prescription);
            pst.setInt(2, consultationId);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Prescription saved!");
            PDFGenerator.generatePrescription("Patient Name", "Doctor Name", prescription, "Dosage Info"); // Adjust as needed
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateConsultationStatus() {
        int selectedRow = consultationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a consultation!");
            return;
        }

        int consultationId = (int) consultationTable.getValueAt(selectedRow, 0);
        String newStatus = (String) statusComboBox.getSelectedItem();

        try (Connection con = DatabaseConnector.connect()) {
            String query = "UPDATE Consultation SET Status = ? WHERE C_id = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, newStatus);
            pst.setInt(2, consultationId);
            pst.executeUpdate();

            String prescription = prescriptionTextArea.getText();
            String dosageInfo = "";

            if (newStatus.equals("Completed")) {
                dosageInfo = JOptionPane.showInputDialog(this, "Enter Dosage Info:", "Dosage Information", JOptionPane.PLAIN_MESSAGE);
                if (dosageInfo == null || dosageInfo.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Dosage Info is required!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Fetch patient & correct doctor name
                String fetchQuery = """
                SELECT s.Name AS Patient_Name, d_s.Name AS Doctor_Name, c.Consultation_Date
                FROM Consultation c
                JOIN Patient p ON c.P_id = p.P_id
                JOIN Signup s ON p.S_id = s.S_id
                JOIN Doctor d ON c.D_id = d.D_id
                JOIN Signup d_s ON d.S_id = d_s.S_id
                WHERE c.C_id = ?
            """;
                PreparedStatement fetchStmt = con.prepareStatement(fetchQuery);
                fetchStmt.setInt(1, consultationId);
                ResultSet rs = fetchStmt.executeQuery();

                if (rs.next()) {
                    String patientName = rs.getString("Patient_Name");
                    String doctorName = rs.getString("Doctor_Name");
                    String appointmentDate = rs.getTimestamp("Consultation_Date").toString();

                    // Generate a random amount between 500 and 2000
                    int amount = 500 + (int) (Math.random() * 1500);

                    // Generate PDFs
                    PDFGenerator.generateInvoice(patientName, doctorName, appointmentDate, String.valueOf(amount));
                    PDFGenerator.generatePrescription(patientName, doctorName, prescription, dosageInfo);

                    JOptionPane.showMessageDialog(this, "Consultation marked as completed. Invoice & Prescription PDF generated!");
                }
            }

            refreshAllData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void viewConsultationDetails() {
        int selectedRow = consultationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a consultation!");
            return;
        }

        String details = "Consultation ID: " + consultationTable.getValueAt(selectedRow, 0) +
                "\nPatient Name: " + consultationTable.getValueAt(selectedRow, 1) +
                "\nDate: " + consultationTable.getValueAt(selectedRow, 2) +
                "\nNotes: " + consultationTable.getValueAt(selectedRow, 3) +
                "\nStatus: " + consultationTable.getValueAt(selectedRow, 4);

        JOptionPane.showMessageDialog(this, details, "Consultation Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void createPatientDetailsTab() {
        JPanel patientPanel = new JPanel(new BorderLayout());
        patientTable = new JTable(fetchPatientDetails(), new String[]{"Patient ID", "Patient Name", "Medical History", "Allergies"});
        JScrollPane scrollPane3 = new JScrollPane(patientTable);
        patientPanel.add(scrollPane3, BorderLayout.CENTER);

        btnViewMedicalHistory = new JButton("View Medical History");
        btnViewAllergies = new JButton("View Allergies");

        btnViewMedicalHistory.addActionListener(e -> viewMedicalHistory());
        btnViewAllergies.addActionListener(e -> viewAllergies());

        JPanel btnPanel3 = new JPanel();
        btnPanel3.add(btnViewMedicalHistory);
        btnPanel3.add(btnViewAllergies);

        patientPanel.add(btnPanel3, BorderLayout.SOUTH);
        tabbedPane.add("Patient Details", patientPanel);
    }

    private void viewMedicalHistory() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient!");
            return;
        }

        String medicalHistory = (String) patientTable.getValueAt(selectedRow, 2);
        JOptionPane.showMessageDialog(this, "Medical History:\n" + medicalHistory, "Medical History", JOptionPane.INFORMATION_MESSAGE);
    }

    private void viewAllergies() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient!");
            return;
        }

        String allergies = (String) patientTable.getValueAt(selectedRow, 3);
        JOptionPane.showMessageDialog(this, "Allergies:\n" + allergies, "Allergies", JOptionPane.INFORMATION_MESSAGE);
    }

    private void refreshAllData() {
        appointmentTable.setModel(new DefaultTableModel(fetchAppointments(),
                new String[]{"Appointment ID", "Patient Name", "Date", "Slot Time", "Status"}));

        consultationTable.setModel(new DefaultTableModel(fetchConsultations(),
                new String[]{"Consultation ID", "Patient Name", "Date", "Notes", "Status"}));

        patientTable.setModel(new DefaultTableModel(fetchPatientDetails(),
                new String[]{"Patient ID", "Patient Name", "Medical History", "Allergies"}));
    }

    private Object[][] fetchAppointments() {
        ArrayList<Object[]> dataList = new ArrayList<>();
        try (Connection con = DatabaseConnector.connect()) {
            String query = """
                SELECT b.B_id, s.Name AS Patient_Name, b.Appointment_Date, b.Slot_Time, b.Status
                FROM Book_Appointment b
                JOIN Patient p ON b.P_id = p.P_id
                JOIN Signup s ON p.S_id = s.S_id
                WHERE b.D_id = (SELECT D_id FROM Doctor WHERE S_id = (SELECT S_id FROM Signup WHERE Username = ?))
            """;
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                dataList.add(new Object[]{rs.getInt(1), rs.getString(2), rs.getDate(3), rs.getTime(4), rs.getString(5)});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList.toArray(new Object[0][]);
    }

    private Object[][] fetchPatientDetails() {
        ArrayList<Object[]> dataList = new ArrayList<>();
        try (Connection con = DatabaseConnector.connect()) {
            String query = """
                SELECT p.P_id, s.Name, p.Medical_History, p.Allergies
                FROM Patient p
                JOIN Signup s ON p.S_id = s.S_id
            """;
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                dataList.add(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4)});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList.toArray(new Object[0][]);
    }

    private Object[][] fetchConsultations() {
        ArrayList<Object[]> dataList = new ArrayList<>();
        try (Connection con = DatabaseConnector.connect()) {
            String query = """
            SELECT c.C_id, s.Name AS Patient_Name, c.Consultation_Date, c.Notes, c.Status
            FROM Consultation c
            JOIN Patient p ON c.P_id = p.P_id
            JOIN Signup s ON p.S_id = s.S_id
            WHERE c.D_id = (SELECT D_id FROM Doctor WHERE S_id = (SELECT S_id FROM Signup WHERE Username = ?))
        """;
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                dataList.add(new Object[]{
                        rs.getInt("C_id"),
                        rs.getString("Patient_Name"),
                        rs.getTimestamp("Consultation_Date"),
                        rs.getString("Notes"),
                        rs.getString("Status")  // ✅ Fetching status from database
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList.toArray(new Object[0][]);
    }

    private void showDoctorInfo() {
        String[] doctorData = fetchDoctorInfo();
        StringBuilder infoMessage = new StringBuilder();
        infoMessage.append("<html><body style='font-family: Segoe UI;'>");
        infoMessage.append("<h3>Doctor Information</h3>");
        infoMessage.append("<p><b>Name:</b> ").append(doctorData[0]).append("</p>");
        infoMessage.append("<p><b>Username:</b> ").append(doctorData[1]).append("</p>");
        infoMessage.append("<p><b>Specialization:</b> ").append(doctorData[2]).append("</p>");
        infoMessage.append("<p><b>Email:</b> ").append(doctorData[3]).append("</p>");
        infoMessage.append("</body></html>");

        JOptionPane.showMessageDialog(this, infoMessage.toString(), "Doctor Profile",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // ✅ FETCH DOCTOR INFO FROM DATABASE
    private String[] fetchDoctorInfo() {
        String[] data = {"Not Found", "Not Found", "Not Found", "Not Found"};
        try (Connection con = DatabaseConnector.connect()) {
            String query = """
                SELECT s.Name, s.Username, d.Specialization, s.Email
                FROM signup s
                JOIN doctor d ON s.S_id = d.S_id
                WHERE s.Username = ?
            """;
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, username); // Use the logged-in doctor's username
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                data[0] = rs.getString("Name"); // Doctor's Name
                data[1] = rs.getString("Username"); // Doctor's Username
                data[2] = rs.getString("Specialization") != null ? rs.getString("Specialization") : "Not Provided";
                data[3] = rs.getString("Email") != null ? rs.getString("Email") : "Not Provided";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185)); // Primary blue color
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding

        // Title Label
        JLabel titleLabel = new JLabel("Doctor Dashboard");
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
        profileButton.addActionListener(e -> showDoctorInfo()); // Show doctor info
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
    public void confirmLogout() {
        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?",
                "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            new LoginPage(); // Assuming LoginPage is your login screen class
            dispose();
        }
    }

    public static void main(String[] args) {
        new DoctorModule("samiya");
    }
}