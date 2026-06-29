package SmartConsultingApp;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.event.*;
import java.sql.*;

public class PatientDetailsPage extends JFrame implements ActionListener {
    private JTextArea medicalHistoryArea;
    private JTextField allergyField;
    private JButton submitButton, backButton;
    private int s_id;

    // Theme colors to match the signup page
    private Color primaryColor = new Color(41, 128, 185); // Professional blue
    private Color lightGray = new Color(240, 240, 240);
    private Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font headerFont = new Font("Segoe UI", Font.BOLD, 18);

    public PatientDetailsPage(int s_id) {
        this.s_id = s_id;

        setTitle("Patient Additional Info");
        setSize(400, 300);
        setLocationRelativeTo(null); // Center on screen
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(4, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Add components
        mainPanel.add(new JLabel("Medical History:"));
        medicalHistoryArea = new JTextArea();
        medicalHistoryArea.setFont(labelFont);
        medicalHistoryArea.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(lightGray, 8),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)));
        mainPanel.add(new JScrollPane(medicalHistoryArea)); // Add scroll pane for text area

        mainPanel.add(new JLabel("Allergies:"));
        allergyField = createTextField();
        mainPanel.add(allergyField);

        // Submit button
        submitButton = new JButton("Submit");
        submitButton.setFont(buttonFont);
        submitButton.setForeground(Color.WHITE); // Set text color to white for better visibility
        submitButton.setBackground(primaryColor);
        submitButton.setBorder(new RoundBorder(primaryColor, 25)); // Set custom rounded border
        submitButton.setOpaque(true);
        submitButton.setContentAreaFilled(true);
        submitButton.setFocusPainted(false);
        submitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        submitButton.setBorderPainted(false); // Remove the default rectangular border
        submitButton.addActionListener(this);

// Add hover effect
        submitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                submitButton.setBackground(primaryColor.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                submitButton.setBackground(primaryColor);
            }
        });

        mainPanel.add(submitButton);

        // Back button
        backButton = new JButton("Back");
        backButton.setFont(buttonFont);
        backButton.setForeground(primaryColor);
        backButton.setBackground(Color.WHITE);
        backButton.setBorder(new RoundBorder(primaryColor, 25));
        backButton.setOpaque(true);
        backButton.setContentAreaFilled(true);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> {
            dispose(); // Close the current window
            new SignupPage(); // Open the signup page
        });

        // Add hover effect for back button
        backButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                backButton.setForeground(primaryColor.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                backButton.setForeground(primaryColor);
            }
        });

        mainPanel.add(backButton);

        // Add main panel to frame
        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(labelFont);
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(lightGray, 8),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)));
        return field;
    }

    public void actionPerformed(ActionEvent e) {
        try (Connection con = DatabaseConnector.connect()) {
            String query = "INSERT INTO Patient (S_id, Medical_History, Allergies) VALUES (?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(query);

            pst.setInt(1, s_id);
            pst.setString(2, medicalHistoryArea.getText());
            pst.setString(3, allergyField.getText());

            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Patient Details Saved Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // Custom round border class
    private class RoundBorder extends AbstractBorder {
        private Color color;
        private int radius;

        RoundBorder(Color color, int radius) {
            this.color = color;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.draw(new RoundRectangle2D.Double(x, y, width - 1, height - 1, radius, radius));
            g2d.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius / 2, this.radius / 2, this.radius / 2, this.radius / 2);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }

    public static void main(String[] args) {
        // Set look and feel to system style
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        new PatientDetailsPage(1); // Example usage with a sample s_id
    }
}