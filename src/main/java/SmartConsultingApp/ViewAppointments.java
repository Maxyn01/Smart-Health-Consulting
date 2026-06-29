package SmartConsultingApp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class ViewAppointments extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private String username;
    private String role;

    // Smart Health Theme Colors
    private Color primaryColor = new Color(84, 160, 255); // Soft Blue
    private Color secondaryColor = new Color(84, 160, 255); //  Soft Blue
    private Color backgroundColor = new Color(245, 250, 255); // Light Pastel Blue
    private Color accentColor = new Color(84, 160, 255); //  Soft Blue

    public ViewAppointments(String username, String role) {
        this.username = username;
        this.role = role;

        setTitle("View Appointments");
        setSize(900, 600); // Increased size for better layout
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Custom Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Button.background", accentColor);
            UIManager.put("Button.foreground", Color.BLACK);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main Panel with Padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(backgroundColor);

        // Table Setup
        String[] columns = {"Appointment ID", "Patient Name", "Doctor Name", "Date", "Time", "Status"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(230, 240, 255)); // Alternating row colors
                }
                ((JLabel) c).setForeground(Color.BLACK); // Black font for table cells
                return c;
            }
        };
        table.getTableHeader().setBackground(primaryColor);
        table.getTableHeader().setForeground(Color.BLACK); // Black font for table headers
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(25);
        loadAppointments();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(primaryColor), "Appointments"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBackground(backgroundColor);

        JButton refreshButton = createStyledButton("↻ Refresh", accentColor, Color.BLACK);
        refreshButton.setToolTipText("Refresh the appointment list");
        refreshButton.addActionListener(e -> {
            model.setRowCount(0); // Clear existing rows
            loadAppointments(); // Reload data
        });

        JButton backButton = createStyledButton("<- Back", secondaryColor, Color.BLACK);
        backButton.setToolTipText("Go back to the previous module");
        backButton.addActionListener(e -> {
            new PatientModule(username); // Redirect to PatientModule
            dispose();
        });

        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("📅 Appointments");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        return headerPanel;
    }

    public void loadAppointments() {
        try (Connection con = DatabaseConnector.connect()) {
            String query;
            PreparedStatement pst;

            if ("Admin".equalsIgnoreCase(role)) {
                // Admin sees all appointments
                query = """
                        SELECT 
                            b.B_id,
                            s1.Name AS Patient_Name,
                            s2.Name AS Doctor_Name,
                            b.Appointment_Date,
                            b.Slot_Time,
                            b.Status
                        FROM 
                            Book_Appointment b
                        JOIN 
                            Patient p ON b.P_id = p.P_id
                        JOIN 
                            Doctor d ON b.D_id = d.D_id
                        JOIN 
                            Signup s1 ON p.S_id = s1.S_id
                        JOIN 
                            Signup s2 ON d.S_id = s2.S_id
                        ORDER BY b.Appointment_Date DESC;
                        """;
                pst = con.prepareStatement(query);
            } else if ("Doctor".equalsIgnoreCase(role)) {
                // Doctor sees only their own appointments
                query = """
                        SELECT 
                            b.B_id,
                            s1.Name AS Patient_Name,
                            s2.Name AS Doctor_Name,
                            b.Appointment_Date,
                            b.Slot_Time,
                            b.Status
                        FROM 
                            Book_Appointment b
                        JOIN 
                            Patient p ON b.P_id = p.P_id
                        JOIN 
                            Doctor d ON b.D_id = d.D_id
                        JOIN 
                            Signup s1 ON p.S_id = s1.S_id
                        JOIN 
                            Signup s2 ON d.S_id = s2.S_id
                        WHERE 
                            s2.Username = ?
                        ORDER BY b.Appointment_Date DESC;
                        """;
                pst = con.prepareStatement(query);
                pst.setString(1, username);
            } else {
                // Patient sees only their own appointments
                query = """
                        SELECT 
                            b.B_id,
                            s1.Name AS Patient_Name,
                            s2.Name AS Doctor_Name,
                            b.Appointment_Date,
                            b.Slot_Time,
                            b.Status
                        FROM 
                            Book_Appointment b
                        JOIN 
                            Patient p ON b.P_id = p.P_id
                        JOIN 
                            Doctor d ON b.D_id = d.D_id
                        JOIN 
                            Signup s1 ON p.S_id = s1.S_id
                        JOIN 
                            Signup s2 ON d.S_id = s2.S_id
                        WHERE 
                            s1.Username = ?
                        ORDER BY b.Appointment_Date DESC;
                        """;
                pst = con.prepareStatement(query);
                pst.setString(1, username);
            }

            ResultSet rs = pst.executeQuery();

            // Date/Time Formatter
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");

            while (rs.next()) {
                int id = rs.getInt("B_id");
                String patientName = rs.getString("Patient_Name");
                String doctorName = rs.getString("Doctor_Name");
                Date date = rs.getDate("Appointment_Date");
                Time time = rs.getTime("Slot_Time");
                String status = rs.getString("Status");

                // Handle NULL Date & Time
                String formattedDate = (date != null) ? dateFormat.format(date) : "N/A";
                String formattedTime = (time != null) ? timeFormat.format(time) : "N/A";

                // Add Row to Table
                model.addRow(new Object[]{id, patientName, doctorName, formattedDate, formattedTime, status});
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load appointments.");
        }
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(fgColor); // Black or white font
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

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> new ViewAppointments("ziyauddin", "patient"));
    }
}