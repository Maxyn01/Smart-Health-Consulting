package SmartConsultingApp;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;

public class DoctorDetailsPage extends JFrame implements ActionListener {
    private JTextField specializationField, experienceField;
    private JButton submitButton, backButton;
    private int s_id;

    // Theme colors to match the signup page
    private Color primaryColor = new Color(41, 128, 185); // Professional blue
    private Color lightGray = new Color(240, 240, 240);
    private Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
    public DoctorDetailsPage(int s_id) {
        this.s_id = s_id;

        setTitle("Doctor Additional Info");
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
        mainPanel.add(new JLabel("Specialization:"));
        specializationField = createTextField();
        mainPanel.add(specializationField);

        mainPanel.add(new JLabel("Experience (in years):"));
        experienceField = createTextField();
        mainPanel.add(experienceField);

        // Submit button
        submitButton = new JButton("Submit");
        submitButton.setFont(buttonFont);
        submitButton.setForeground(Color.WHITE);
        submitButton.setBackground(primaryColor);
        submitButton.setBorder(new RoundBorder(primaryColor, 25)); // Custom rounded border
        submitButton.setOpaque(true);
        submitButton.setContentAreaFilled(true);
        submitButton.setFocusPainted(false);
        submitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        submitButton.setBorderPainted(false); // Remove default rectangular border
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
        backButton.setBorder(new RoundBorder(primaryColor, 25)); // Custom rounded border
        backButton.setOpaque(true);
        backButton.setContentAreaFilled(true);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setBorderPainted(false); // Remove default rectangular border
        backButton.addActionListener(e -> {
            dispose(); // Close the current window
            new SignupPage(); // Open the signup page
        });
        backButton.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(Color.BLUE, 25), // Blue rounded border
                BorderFactory.createEmptyBorder(5, 5, 5, 5) // Optional padding
        ));
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
            String query = "INSERT INTO Doctor (S_id, Specialization, Experience) VALUES (?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(query);

            pst.setInt(1, s_id);
            pst.setString(2, specializationField.getText());
            pst.setInt(3, Integer.parseInt(experienceField.getText()));

            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Doctor Details Saved Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
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

        new DoctorDetailsPage(1); // Example usage with a sample s_id
    }
}