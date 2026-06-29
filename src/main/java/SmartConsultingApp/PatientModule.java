package SmartConsultingApp;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

public class PatientModule extends JFrame {
    private String username;
    private String role;
    private JLabel lblProfileIcon;
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color ACCENT_COLOR = new Color(52, 152, 219);
    private final Color TEXT_COLOR = new Color(44, 62, 80);
    private final Color PANEL_COLOR = new Color(236, 240, 241);
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private final Font BUTTON_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    public PatientModule(String username) {
        this.username = username;
        this.role = fetchUserRole(username);
        setTitle("Patient Dashboard");
        setSize(850, 650);
        setLayout(new BorderLayout());
        getContentPane().setBackground(PANEL_COLOR);

        // Create main scroll pane
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(PANEL_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // ✅ HEADER PANEL
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // ✅ CONTENT PANEL
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setBackground(PANEL_COLOR);

        // ✅ User greeting and stats panel
        JPanel userStatsPanel = createUserStatsPanel();
        contentPanel.add(userStatsPanel, BorderLayout.NORTH);

        // ✅ MODULES PANEL
        JPanel modulesPanel = createModulesPanel();
        contentPanel.add(modulesPanel, BorderLayout.CENTER);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // ✅ FOOTER PANEL
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(PANEL_COLOR);
        java.net.URL logoutUrl = getClass().getResource("/logout_icon.png");
        ImageIcon logoutIcon = logoutUrl != null ? new ImageIcon(logoutUrl) : new ImageIcon();
        JButton logoutBtn = createStyledButton("Logout", logoutIcon);
        logoutBtn.addActionListener(e -> confirmLogout());
        footerPanel.add(logoutBtn);

        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        // ✅ Frame settings
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel("Patient Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        profilePanel.setOpaque(false);

        // ✅ Profile Icon (Increased size to 80x80)
        java.net.URL g1Url = getClass().getResource("/g1.jpg");
        ImageIcon icon = g1Url != null ? new ImageIcon(g1Url) : new ImageIcon();
        Image img = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        lblProfileIcon = new JLabel(new ImageIcon(img));
        lblProfileIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblProfileIcon.setToolTipText("View Profile");
        lblProfileIcon.setBorder(BorderFactory.createLineBorder(Color.WHITE, 10));
        lblProfileIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showPatientInfo();
            }
        });

        JLabel userLabel = new JLabel(username);
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setForeground(Color.WHITE);

        profilePanel.add(userLabel);
        profilePanel.add(Box.createHorizontalStrut(10));
        profilePanel.add(lblProfileIcon);
        headerPanel.add(profilePanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createUserStatsPanel() {
        String[] patientData = fetchPatientInfo();

        JPanel userStatsPanel = new JPanel(new BorderLayout(10, 10));
        userStatsPanel.setBackground(Color.WHITE);
        userStatsPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(ACCENT_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel welcomeLabel = new JLabel("Welcome, " + patientData[0]);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        welcomeLabel.setForeground(TEXT_COLOR);
        userStatsPanel.add(welcomeLabel, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel(new GridLayout(1, 3, 20, 5));
        infoPanel.setOpaque(false);

        JPanel appointmentPanel = createInfoCard("Upcoming Appointments", "2");
        JPanel medicationPanel = createInfoCard("Medications", "3 Active");
        JPanel consultationPanel = createInfoCard("Last Consultation", "3 days ago");

        infoPanel.add(appointmentPanel);
        infoPanel.add(medicationPanel);
        infoPanel.add(consultationPanel);

        userStatsPanel.add(infoPanel, BorderLayout.CENTER);

        return userStatsPanel;
    }

    private JPanel createInfoCard(String title, String value) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(new Color(100, 100, 100));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(TEXT_COLOR);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createModulesPanel() {
        JPanel modulesPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        modulesPanel.setBackground(PANEL_COLOR);

        // Add module cards
        modulesPanel.add(createModuleCard("Book Appointment", "Schedule a new appointment with a doctor",
                e -> { new AppointmentBooking(username); dispose(); }));
        modulesPanel.add(createModuleCard("View Appointments", "Check your upcoming and past appointments",
                e -> { new ViewAppointments(username, role); dispose(); }));
        modulesPanel.add(createModuleCard("Video Consultation", "Connect with doctors through video call",
                e -> { new VideoConsultation(username); dispose(); }));
        modulesPanel.add(createModuleCard("Payment Module", "Make payments and view payment history",
                e -> { new PaymentModule(username); dispose(); }));
        modulesPanel.add(createModuleCard("Health Tracker", "Track and monitor your health metrics",
                e -> { new HealthTracker(username); dispose(); }));
        modulesPanel.add(createModuleCard("Medication Tracker", "Manage your medications and reminders",
                e -> { new MedicationTracker(username); dispose(); }));

        JPanel viewConsultationsCard = createModuleCard("View Consultations", "Check your consultation history",
                e -> showConsultationTable());
        modulesPanel.add(viewConsultationsCard);

        return modulesPanel;
    }

    private JPanel createModuleCard(String title, String description, java.awt.event.ActionListener listener) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);

        JLabel descLabel = new JLabel("<html><body width='150px'>" + description + "</body></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(new Color(100, 100, 100));

        JButton actionButton = new JButton("Open");
        actionButton.setFont(BUTTON_FONT);
        actionButton.setForeground(Color.WHITE);
        actionButton.setBackground(ACCENT_COLOR);
        actionButton.setFocusPainted(false);
        actionButton.setBorderPainted(false);
        actionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        actionButton.addActionListener(listener);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setOpaque(false);
        northPanel.add(titleLabel, BorderLayout.CENTER);

        JPanel contentPanel = new JPanel(new BorderLayout(5, 10));
        contentPanel.setOpaque(false);
        contentPanel.add(descLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(actionButton);

        card.add(northPanel, BorderLayout.NORTH);
        card.add(contentPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);

        return card;
    }

    private JButton createStyledButton(String text, ImageIcon icon) {
        JButton button = new JButton(text);
        if (icon != null) {
            // Resize the icon to 20x20 pixels
            Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(img));
        }
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 35)); // Set a fixed size for the button
        return button;
    }

    // ✅ FETCH USER ROLE FROM DATABASE
    private String fetchUserRole(String username) {
        String userRole = "Patient";
        try (Connection con = DatabaseConnector.connect()) {
            String query = "SELECT Role FROM signup WHERE Username = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                userRole = rs.getString("Role");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userRole;
    }

    // ✅ FETCH CONSULTATION RECORDS
    private Vector<Vector<String>> fetchConsultations() {
        Vector<Vector<String>> consultations = new Vector<>();
        try (Connection con = DatabaseConnector.connect()) {
            String query = """
                SELECT c.Consultation_Date, 
                       TIME(c.Consultation_Date) AS Consultation_Time, 
                       c.Notes, 
                       ds.Name AS Doctor_Name
                FROM consultation c
                JOIN patient p ON p.P_id = c.P_id
                JOIN signup s ON s.S_id = p.S_id
                JOIN doctor d ON d.D_id = c.D_id
                JOIN signup ds ON ds.S_id = d.S_id
                WHERE s.Username = ?;
            """;
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("Doctor_Name"));
                row.add(rs.getString("Consultation_Date"));
                row.add(rs.getString("Consultation_Time"));
                row.add(rs.getString("Notes"));
                consultations.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return consultations;
    }

    // ✅ DISPLAY CONSULTATION TABLE
    private void showConsultationTable() {
        JDialog dialog = new JDialog(this, "Consultation History", true);
        dialog.setSize(600, 400);
        dialog.setLayout(new BorderLayout());

        String[] columnNames = {"Doctor", "Date", "Time", "Notes"};
        Vector<Vector<String>> data = fetchConsultations();

        DefaultTableModel model = new DefaultTableModel(data, new Vector<>(java.util.List.of(columnNames)));
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dialog.dispose());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(btnClose);

        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    // ✅ DISPLAY PATIENT INFO
    private void showPatientInfo() {
        String[] patientData = fetchPatientInfo();
        StringBuilder infoMessage = new StringBuilder();
        infoMessage.append("<html><body style='font-family: Segoe UI;'>");
        infoMessage.append("<h3>Patient Information</h3>");
        infoMessage.append("<p><b>Name:</b> ").append(patientData[0]).append("</p>");
        infoMessage.append("<p><b>Username:</b> ").append(patientData[1]).append("</p>");
        infoMessage.append("<p><b>Weight:</b> ").append(patientData[2]).append("</p>");
        infoMessage.append("<p><b>Email:</b> ").append(patientData[3]).append("</p>");
        infoMessage.append("<p><b>Medical History:</b> ").append(patientData[4]).append("</p>");
        infoMessage.append("<p><b>Allergies:</b> ").append(patientData[5]).append("</p>");
        infoMessage.append("</body></html>");
        JOptionPane.showMessageDialog(this, infoMessage.toString(), "Patient Profile",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // ✅ FETCH PATIENT INFO FROM DATABASE
    private String[] fetchPatientInfo() {
        String[] data = {"Not Found", "Not Found", "Not Found", "Not Found", "Not Found", "Not Found"};
        try (Connection con = DatabaseConnector.connect()) {
            String query = """
                    SELECT s.Name, s.Username, s.Weight, s.Email, p.Medical_History, p.Allergies
                    FROM signup s
                    JOIN patient p ON s.S_id = p.S_id
                    WHERE s.Username = ?
                """;
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                data[0] = rs.getString("Name");
                data[1] = rs.getString("Username");
                data[2] = rs.getString("Weight") != null ? rs.getString("Weight") : "Not Provided";
                data[3] = rs.getString("Email");
                data[4] = rs.getString("Medical_History") != null ? rs.getString("Medical_History") : "No History";
                data[5] = rs.getString("Allergies") != null ? rs.getString("Allergies") : "No Allergies";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    // ✅ CONFIRM LOGOUT
    public void confirmLogout() {
        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?",
                "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            new LoginPage(); // Assuming LoginPage is your login screen class
            dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PatientModule("ziyauddin")); // Replace with actual username as needed
    }
}