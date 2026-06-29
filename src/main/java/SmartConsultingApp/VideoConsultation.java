package SmartConsultingApp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.sql.*;
import java.util.ArrayList;

public class VideoConsultation extends JFrame {
    JTable videoConsultTable;
    JButton btnJoinMeeting, btnClose; // Added Close Button
    DefaultTableModel tableModel;
    String googleMeetLink = "No Active Link";

    // Colors for a modern and professional look
    private final Color PRIMARY_COLOR = new Color(41, 128, 185); // Soft Blue
    private final Color ACCENT_COLOR = new Color(52, 152, 219); // Light Blue
    private final Color TEXT_COLOR = new Color(44, 62, 80); // Dark Gray
    private final Color PANEL_COLOR = new Color(236, 240, 241); // Light Background

    public VideoConsultation(String username) {
        setTitle("Video Consultation");
        setSize(700, 400); // Increased size for better readability
        setLayout(new BorderLayout());
        getContentPane().setBackground(PANEL_COLOR);

        // ✅ Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // ✅ Table to display doctor and meeting link
        tableModel = new DefaultTableModel(new String[]{"Doctor Name", "Meeting Link"}, 0);
        videoConsultTable = new JTable(tableModel);
        videoConsultTable.setRowHeight(30); // Increase row height for readability
        videoConsultTable.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Use a modern font
        videoConsultTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        videoConsultTable.getTableHeader().setBackground(ACCENT_COLOR);
        videoConsultTable.getTableHeader().setForeground(Color.WHITE);
        videoConsultTable.setSelectionBackground(new Color(232, 241, 252));
        videoConsultTable.setSelectionForeground(TEXT_COLOR);
        videoConsultTable.setShowGrid(false);
        videoConsultTable.setIntercellSpacing(new Dimension(0, 0));

        fetchDoctorDetails(username);

        JScrollPane scrollPane = new JScrollPane(videoConsultTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
        add(scrollPane, BorderLayout.CENTER);

        // ✅ Footer Panel with Join Meeting and Close Buttons
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    // ✅ Header Panel
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Video Consultation");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.white);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        return headerPanel;
    }

    // ✅ Footer Panel with Styled Buttons
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(PANEL_COLOR);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Join Meeting Button
        btnJoinMeeting = new JButton("Join Meeting");
        btnJoinMeeting.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnJoinMeeting.setForeground(Color.WHITE);
        btnJoinMeeting.setBackground(ACCENT_COLOR);
        btnJoinMeeting.setFocusPainted(false);
        btnJoinMeeting.setBorderPainted(false);
        btnJoinMeeting.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnJoinMeeting.addActionListener(e -> openMeetLink());

        // Close Button
        btnClose = new JButton("Close");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnClose.setForeground(Color.WHITE);
        btnClose.setBackground(PRIMARY_COLOR);
        btnClose.setFocusPainted(false);
        btnClose.setBorderPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> {
            new PatientModule("ziyauddin"); // Replace with actual username as needed
            dispose(); // Close the current window
        });

        footerPanel.add(btnJoinMeeting);
        footerPanel.add(btnClose); // Add Close Button
        return footerPanel;
    }

    // ✅ FETCH DOCTOR NAME & MEET LINK
    private void fetchDoctorDetails(String username) {
        ArrayList<Object[]> dataList = new ArrayList<>();
        try (Connection con = DatabaseConnector.connect()) {
            String query = """
                SELECT s.Name AS DoctorName, b.Meet_Link
                FROM Book_Appointment b
                JOIN Patient p ON b.P_id = p.P_id
                JOIN Signup s ON b.D_id = (SELECT D_id FROM Doctor WHERE S_id = s.S_id)
                WHERE p.S_id = (SELECT S_id FROM Signup WHERE Username = ?) 
                  AND b.Meet_Link IS NOT NULL 
                  AND b.Status = 'Pending'
                ORDER BY b.Appointment_Date DESC
            """;
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String doctorName = rs.getString("DoctorName");
                googleMeetLink = rs.getString("Meet_Link");
                dataList.add(new Object[]{doctorName, googleMeetLink});
            }

            // Populate Table
            for (Object[] row : dataList) {
                tableModel.addRow(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ OPEN GOOGLE MEET LINK
    private void openMeetLink() {
        int selectedRow = videoConsultTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a meeting!", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String selectedLink = (String) videoConsultTable.getValueAt(selectedRow, 1);
        if (!selectedLink.equals("No Active Link")) {
            try {
                Desktop.getDesktop().browse(new URI(selectedLink));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "No active meeting link available!", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }
}