package SmartConsultingApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.border.EmptyBorder;

public class PaymentModule extends JFrame {
    private final String username; // Store username

    // Theme Colors
    private final Color PRIMARY_COLOR = new Color(41, 128, 185); // Professional blue
    private final Color ACCENT_COLOR = new Color(52, 152, 219); // Light blue
    private final Color TEXT_COLOR = new Color(44, 62, 80); // Dark text color
    private final Color PANEL_COLOR = new Color(236, 240, 241); // Light background

    // Fonts
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private final Font BUTTON_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    public PaymentModule(String username) {
        this.username = username; // Set username
        setTitle("Payment Module");
        setSize(500, 400); // Increased size for better readability
        setLayout(new BorderLayout());
        getContentPane().setBackground(PANEL_COLOR);

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main Content Panel
        JPanel mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);

        // Footer Panel
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Ensure only this window is closed
        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Payment Module");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        return headerPanel;
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(PANEL_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Center Message Label
        JLabel messageLabel = new JLabel("Payment Successful for: " + username, SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        messageLabel.setForeground(TEXT_COLOR);
        mainPanel.add(messageLabel, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(PANEL_COLOR);
        footerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Generate Invoice Button
        JButton btnGenerateInvoice = createStyledButton("Generate Invoice", ACCENT_COLOR);
        btnGenerateInvoice.addActionListener(this::generateInvoice);

        // Close Button
        JButton btnClose = createStyledButton("Close", PRIMARY_COLOR);
        btnClose.addActionListener(e -> {
            // Open PatientModule and close this window
            new PatientModule(username); // Reopen the PatientModule
            dispose(); // Close the PaymentModule window
        });

        footerPanel.add(btnGenerateInvoice);
        footerPanel.add(btnClose);

        return footerPanel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(140, 35));
        return button;
    }

    // ✅ Updated method to fetch real data from the database AND store payment info
    private void generateInvoice(ActionEvent e) {
        try (Connection con = DatabaseConnector.connect()) {
            // 🔹 Query to fetch patient details, correct doctor name, and appointment date
            String fetchQuery = """
                SELECT 
                    s.Name AS Patient_Name, 
                    d_s.Name AS Doctor_Name, 
                    b.Appointment_Date,
                    p.P_id
                FROM Book_Appointment b
                JOIN Patient p ON b.P_id = p.P_id
                JOIN Signup s ON p.S_id = s.S_id
                JOIN Doctor d ON b.D_id = d.D_id
                JOIN Signup d_s ON d.S_id = d_s.S_id
                WHERE s.Username = ?
                ORDER BY b.Appointment_Date DESC LIMIT 1
            """;

            PreparedStatement pst = con.prepareStatement(fetchQuery);
            pst.setString(1, username); // ✅ Use `username` directly
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String patientName = rs.getString("Patient_Name");
                String doctorName = rs.getString("Doctor_Name");
                String appointmentDate = rs.getString("Appointment_Date");
                int patientId = rs.getInt("P_id");

                // 🔹 Generate a random bill amount (500-2000)
                double amount = 500 + (Math.random() * 1500);

                // 📝 Generate the invoice PDF
                PDFGenerator.generateInvoice(patientName, doctorName, appointmentDate, String.valueOf(amount));

                // ✅ Insert payment details into the `payment` table
                String insertPaymentQuery = """
                    INSERT INTO payment (P_id, Invoice_No, Amount, Payment_Date)
                    VALUES (?, ?, ?, NOW())
                """;
                PreparedStatement insertPst = con.prepareStatement(insertPaymentQuery);
                insertPst.setInt(1, patientId);  // Patient ID
                insertPst.setString(2, "INV-" + System.currentTimeMillis());  // Unique Invoice No.
                insertPst.setDouble(3, amount);  // Generated amount
                insertPst.executeUpdate();

                JOptionPane.showMessageDialog(this, "✅ Invoice Generated Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "❌ No appointment found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "⚠ Error generating invoice!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PaymentModule("ziyauddin"));
    }
}