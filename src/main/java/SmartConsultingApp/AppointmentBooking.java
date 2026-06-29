package SmartConsultingApp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppointmentBooking extends JFrame implements ActionListener {
    private JTextField patientField, doctorField;
    private JSpinner timeSpinner;
    private JButton bookButton, backButton; // Added backButton
    private JComboBox<String> appointmentType;
    private JTextArea notesArea;
    private JLabel statusLabel;

    // Smart Health Theme Colors
    private Color primaryColor = new Color(84, 160, 255); // Soft Blue
    private Color secondaryColor = new Color(84, 160, 255); // Soft Blue
    private Color backgroundColor = new Color(245, 250, 255); // Light Pastel Blue
    private Color accentColor = new Color(84, 160, 255); // Soft Blue

    public AppointmentBooking(String username) {
        setTitle("Book Appointment or Consultation");
        setSize(800, 600); // Increased size for better layout
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Custom Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Button.background", accentColor);
            UIManager.put("Button.foreground", Color.BLACK); // Black font for buttons
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main Panel with Padding
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30)); // Increased padding
        mainPanel.setBackground(backgroundColor);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15); // Increased spacing between components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Patient Name Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        JLabel patientLabel = new JLabel("Patient Name:");
        patientLabel.setForeground(Color.BLACK); // Black font
        mainPanel.add(patientLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        patientField = new JTextField(20);
        patientField.setToolTipText("Enter the name of the patient");
        patientField.setForeground(Color.BLACK); // Black font
        patientField.setFont(new Font("Arial", Font.PLAIN, 14));
        patientField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primaryColor, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        mainPanel.add(patientField, gbc);

        // Doctor Name Field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        JLabel doctorLabel = new JLabel("Doctor Name:");
        doctorLabel.setForeground(Color.BLACK); // Black font
        mainPanel.add(doctorLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        doctorField = new JTextField(20);
        doctorField.setToolTipText("Enter the name of the doctor");
        doctorField.setForeground(Color.BLACK); // Black font
        doctorField.setFont(new Font("Arial", Font.PLAIN, 14));
        doctorField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primaryColor, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        mainPanel.add(doctorField, gbc);

        // Slot Time Spinner
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        JLabel timeLabel = new JLabel("Slot Time:");
        timeLabel.setForeground(Color.BLACK); // Black font
        mainPanel.add(timeLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        timeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "MMM dd, yyyy hh:mm a");
        timeSpinner.setEditor(timeEditor);
        timeSpinner.setToolTipText("Select the appointment time");
        ((JSpinner.DefaultEditor) timeSpinner.getEditor()).getTextField().setForeground(Color.BLACK); // Black font
        timeSpinner.setFont(new Font("Arial", Font.PLAIN, 14));
        timeSpinner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primaryColor, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        mainPanel.add(timeSpinner, gbc);

        // Appointment Type ComboBox
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.LINE_END;
        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setForeground(Color.BLACK); // Black font
        mainPanel.add(typeLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.LINE_START;
        appointmentType = new JComboBox<>(new String[]{"Appointment", "Consultation"});
        appointmentType.addActionListener(e -> toggleNotesVisibility());
        appointmentType.setToolTipText("Select the type of booking");
        ((JLabel) appointmentType.getRenderer()).setForeground(Color.BLACK); // Black font
        appointmentType.setFont(new Font("Arial", Font.PLAIN, 14));
        appointmentType.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primaryColor, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        mainPanel.add(appointmentType, gbc);

        // Notes Area
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        JLabel notesLabel = new JLabel("Notes (For Consultation):");
        notesLabel.setForeground(Color.BLACK); // Black font
        mainPanel.add(notesLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        notesArea = new JTextArea(4, 30);
        notesArea.setEnabled(false);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setToolTipText("Add additional notes for consultation");
        notesArea.setForeground(Color.BLACK); // Black font
        notesArea.setFont(new Font("Arial", Font.PLAIN, 14));
        notesArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primaryColor, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        JScrollPane notesScrollPane = new JScrollPane(notesArea);
        mainPanel.add(notesScrollPane, gbc);

        // Book and Back Buttons
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        bookButton = createStyledButton("Book", accentColor, Color.BLACK);
        bookButton.addActionListener(this);
        mainPanel.add(bookButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        backButton = createStyledButton("Back", secondaryColor, Color.BLACK);
        backButton.setToolTipText("Go back to the Patient Module");
        backButton.addActionListener(e -> {
            new PatientModule(username); // Redirect to PatientModule
            dispose();
        });
        mainPanel.add(backButton, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // Status Bar
        JPanel statusBar = createStatusBar();
        add(statusBar, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("Appointment Booking System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        return headerPanel;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(new EmptyBorder(10, 20, 10, 20));
        statusBar.setBackground(new Color(230, 240, 255)); // Light blue-gray

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statusLabel.setForeground(Color.BLACK); // Black font
        statusBar.add(statusLabel, BorderLayout.WEST);

        return statusBar;
    }

    private void toggleNotesVisibility() {
        boolean isConsultation = appointmentType.getSelectedItem().equals("Consultation");
        notesArea.setEnabled(isConsultation);
        if (!isConsultation) {
            notesArea.setText("");
        }
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(fgColor); // Black font for buttons
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Hover effects
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try (Connection con = DatabaseConnector.connect()) {
            String patientName = patientField.getText().trim();
            String doctorName = doctorField.getText().trim();
            Date date = (Date) timeSpinner.getValue();
            String selectedType = (String) appointmentType.getSelectedItem();
            String notes = notesArea.getText().trim();

            if (patientName.isEmpty() || doctorName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = sdf.format(date);

            int P_id = fetchUserId(con, "Patient", patientName);
            int D_id = fetchUserId(con, "Doctor", doctorName);

            if (P_id == -1) {
                JOptionPane.showMessageDialog(this, "Patient not found! Please register the patient first.");
                return;
            }
            if (D_id == -1) {
                JOptionPane.showMessageDialog(this, "Doctor not found! Please register the doctor first.");
                return;
            }

            if ("Appointment".equals(selectedType)) {
                bookAppointment(con, P_id, D_id, formattedDateTime);
            } else {
                bookConsultation(con, P_id, D_id, formattedDateTime, notes);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int fetchUserId(Connection con, String userType, String name) throws SQLException {
        String query = "SELECT " + userType.substring(0, 1) + "_id FROM " + userType + " WHERE S_id IN (SELECT S_id FROM Signup WHERE Name = ?)";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, name);
            ResultSet rs = pst.executeQuery();
            return rs.next() ? rs.getInt(1) : -1;
        }
    }

    private void bookAppointment(Connection con, int P_id, int D_id, String formattedDateTime) throws SQLException {
        String query = "INSERT INTO book_appointment (P_id, D_id, Appointment_Date, Slot_Time, Status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, P_id);
            pst.setInt(2, D_id);
            pst.setString(3, formattedDateTime);
            pst.setString(4, formattedDateTime.split(" ")[1]); // Extract only time
            pst.setString(5, "Pending");

            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Appointment Booked Successfully!");
                dispose();
            }
        }
    }

    private void bookConsultation(Connection con, int P_id, int D_id, String formattedDateTime, String notes) throws SQLException {
        String query = "INSERT INTO consultation (P_id, D_id, Consultation_Date, Notes) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, P_id);
            pst.setInt(2, D_id);
            pst.setString(3, formattedDateTime);
            pst.setString(4, notes);

            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Consultation Booked Successfully!");
                dispose();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AppointmentBooking("ziyauddin"));
    }
}