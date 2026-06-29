package SmartConsultingApp;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;
import javax.swing.plaf.basic.BasicButtonUI;

public class SignupPage extends JFrame implements ActionListener {
    private JTextField nameField, usernameField, weightField, heightField, emailField;
    private JPasswordField passwordField;
    private JComboBox<String> genderBox, roleBox;
    private JButton signupButton, backButton;
    private JProgressBar progressBar;
    private JLabel statusLabel;

    // Theme colors to match the login page
    private Color primaryColor = new Color(41, 128, 185); // Professional blue
    private Color accentColor = new Color(52, 152, 219); // Lighter blue
    private Color lightGray = new Color(240, 240, 240);
    private Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font headerFont = new Font("Segoe UI", Font.BOLD, 26);
    private Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);

    public SignupPage() {
        setTitle("Healthcare Management System - Sign Up");
        setSize(900, 600);
        setLocationRelativeTo(null); // Center on screen
        setResizable(false);

        // Main panel with two sections
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1, 2));

        // Left panel for image/banner
        JPanel bannerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // Create gradient background
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(41, 128, 185, 220),
                        getWidth(), getHeight(), new Color(52, 152, 219, 180)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Enable anti-aliasing
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw healthcare icons/symbols
                drawHealthcareSymbols(g2d);
            }

            private void drawHealthcareSymbols(Graphics2D g2d) {
                int width = getWidth();
                int height = getHeight();

                // Draw large medical cross in center
                g2d.setColor(new Color(255, 255, 255, 80));
                int crossSize = Math.min(width, height) / 2;
                int crossThickness = crossSize / 5;

                int centerX = width / 2;
                int centerY = height / 2;

                // Horizontal bar
                g2d.fillRoundRect(
                        centerX - crossSize/2,
                        centerY - crossThickness/2,
                        crossSize,
                        crossThickness,
                        10, 10
                );

                // Vertical bar
                g2d.fillRoundRect(
                        centerX - crossThickness/2,
                        centerY - crossSize/2,
                        crossThickness,
                        crossSize,
                        10, 10
                );
            }
        };
        bannerPanel.setLayout(new BorderLayout());

        // Title inside banner panel
        JLabel bannerTitle = new JLabel("Create Your Account", JLabel.CENTER);
        bannerTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        bannerTitle.setForeground(Color.WHITE);
        bannerTitle.setBorder(BorderFactory.createEmptyBorder(40, 20, 0, 20));

        JLabel bannerSubtitle = new JLabel("Join our healthcare platform", JLabel.CENTER);
        bannerSubtitle.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        bannerSubtitle.setForeground(new Color(255, 255, 255, 220));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(bannerTitle, BorderLayout.NORTH);
        titlePanel.add(bannerSubtitle, BorderLayout.CENTER);

        JLabel bannerInfo = new JLabel("<html><div style='text-align: center;'>"
                + "Complete your profile<br>"
                + "Connect with healthcare providers<br>"
                + "Manage your medical records<br>"
                + "Schedule appointments</div></html>", JLabel.CENTER);
        bannerInfo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        bannerInfo.setForeground(Color.WHITE);
        bannerInfo.setBorder(BorderFactory.createEmptyBorder(30, 40, 40, 40));

        titlePanel.add(bannerInfo, BorderLayout.SOUTH);
        bannerPanel.add(titlePanel, BorderLayout.CENTER);

        // Right panel for signup form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BorderLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        formPanel.setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel headerLabel = new JLabel("Sign Up", JLabel.LEFT);
        headerLabel.setFont(headerFont);
        headerLabel.setForeground(new Color(70, 70, 70));

        JLabel subHeaderLabel = new JLabel("Please fill in your details", JLabel.LEFT);
        subHeaderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subHeaderLabel.setForeground(Color.GRAY);
        subHeaderLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        headerPanel.add(headerLabel, BorderLayout.NORTH);
        headerPanel.add(subHeaderLabel, BorderLayout.SOUTH);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Create the form fields panel
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new GridLayout(8, 1, 0, 12));
        fieldsPanel.setBackground(Color.WHITE);

        // Add form fields
        fieldsPanel.add(createFieldPanel("Full Name", nameField = createTextField()));
        fieldsPanel.add(createFieldPanel("Username", usernameField = createTextField()));

        // Gender combo box
        genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderBox.setFont(labelFont);
        genderBox.setBackground(Color.WHITE);
        genderBox.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(lightGray, 8),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)));
        fieldsPanel.add(createFieldPanel("Gender", genderBox));

        // Weight and height in one row
        JPanel measurementsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        measurementsPanel.setBackground(Color.WHITE);

        measurementsPanel.add(createFieldPanel("Weight (kg)", weightField = createTextField()));
        measurementsPanel.add(createFieldPanel("Height (cm)", heightField = createTextField()));
        fieldsPanel.add(measurementsPanel);

        fieldsPanel.add(createFieldPanel("Email", emailField = createTextField()));

        passwordField = new JPasswordField();
        passwordField.setFont(labelFont);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(lightGray, 8),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)));
        fieldsPanel.add(createFieldPanel("Password", passwordField));

        // Role combo box
        roleBox = new JComboBox<>(new String[]{"Patient", "Doctor"});
        roleBox.setFont(labelFont);
        roleBox.setBackground(Color.WHITE);
        roleBox.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(lightGray, 8),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)));
        fieldsPanel.add(createFieldPanel("Role", roleBox));

        // Status and progress panel
        JPanel statusPanel = new JPanel(new BorderLayout(5, 5));
        statusPanel.setBackground(Color.WHITE);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(false);
        progressBar.setVisible(false);
        progressBar.setForeground(primaryColor);
        progressBar.setBackground(Color.WHITE);
        progressBar.setBorder(null);

        statusLabel = new JLabel("");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setForeground(Color.GRAY);

        statusPanel.add(progressBar, BorderLayout.CENTER);
        statusPanel.add(statusLabel, BorderLayout.SOUTH);

        fieldsPanel.add(statusPanel);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Back button
        backButton = new JButton("Back to Login");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backButton.setForeground(primaryColor);
        backButton.setBackground(Color.WHITE);
        backButton.setBorder(new RoundBorder(primaryColor, 25));
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Add ActionListener to the back button
        backButton.addActionListener(e -> {
            // Dispose of the current SignupPage
            dispose();
            // Show the LoginPage
            new LoginPage(); // Assuming you have a LoginPage class
        });
        // Add hover effect
        backButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                backButton.setForeground(accentColor);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                backButton.setForeground(primaryColor);
            }
        });

        // Signup button
        signupButton = new JButton("Create Account");
        signupButton.setFont(buttonFont);
        signupButton.setForeground(Color.WHITE);
        signupButton.setBackground(primaryColor);
        signupButton.setBorder(new RoundBorder(new Color(25, 90, 140), 25));
        signupButton.setOpaque(true);
        signupButton.setContentAreaFilled(true);
        signupButton.setFocusPainted(false);
        signupButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signupButton.addActionListener(this);

        // Custom UI to ensure proper button painting
        signupButton.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                AbstractButton button = (AbstractButton) c;
                ButtonModel model = button.getModel();

                // Paint background
                if (model.isPressed()) {
                    g2d.setColor(accentColor.darker());
                } else if (model.isRollover()) {
                    g2d.setColor(accentColor);
                } else {
                    g2d.setColor(primaryColor);
                }

                g2d.fill(new RoundRectangle2D.Double(0, 0, c.getWidth(), c.getHeight(), 25, 25));

                // Paint border
                g2d.setColor(new Color(25, 90, 140));
                g2d.draw(new RoundRectangle2D.Double(0, 0, c.getWidth()-1, c.getHeight()-1, 25, 25));

                g2d.dispose();
                super.paint(g, c);
            }
        });

        // Add hover effect
        signupButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                signupButton.setBackground(accentColor);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                signupButton.setBackground(primaryColor);
            }
        });

        buttonPanel.add(backButton);
        buttonPanel.add(signupButton);

        // Add all panels to form panel
        formPanel.add(headerPanel, BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(fieldsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove border
        formPanel.add(scrollPane, BorderLayout.CENTER);
        formPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add the two main panels to the main panel
        mainPanel.add(bannerPanel);
        mainPanel.add(formPanel);

        // Add the main panel to the frame
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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

    private JPanel createFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel(labelText);
        label.setFont(labelFont);
        label.setForeground(new Color(80, 80, 80));

        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == signupButton) {
            // Validate fields
            if (nameField.getText().isEmpty() || usernameField.getText().isEmpty()
                    || emailField.getText().isEmpty() || passwordField.getPassword().length == 0) {
                statusLabel.setText("Please fill in all required fields");
                statusLabel.setForeground(new Color(231, 76, 60));
                return;
            }

            // Show animation before signup
            progressBar.setVisible(true);
            statusLabel.setText("Creating your account...");
            progressBar.setValue(0);

            // Simulate signup process with animation
            Timer signupTimer = new Timer(20, new ActionListener() {
                int progress = 0;

                @Override
                public void actionPerformed(ActionEvent evt) {
                    progress += 5;
                    progressBar.setValue(progress);

                    if (progress >= 100) {
                        ((Timer)evt.getSource()).stop();
                        createAccount();
                    }
                }
            });

            signupTimer.start();
        }
    }

    private void createAccount() {
        try (Connection con = DatabaseConnector.connect()) {
            String query = "INSERT INTO Signup (Name, Username, Gender, Weight, Height, Email, Password, Role) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            pst.setString(1, nameField.getText());
            pst.setString(2, usernameField.getText());
            pst.setString(3, (String) genderBox.getSelectedItem());
            pst.setString(4, weightField.getText());
            pst.setString(5, heightField.getText());
            pst.setString(6, emailField.getText());
            pst.setString(7, new String(passwordField.getPassword()));
            String role = (String) roleBox.getSelectedItem();
            pst.setString(8, role);

            int rows = pst.executeUpdate();
            if (rows > 0) {
                statusLabel.setText("Account created successfully!");
                statusLabel.setForeground(new Color(46, 204, 113));

                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    int s_id = rs.getInt(1);

                    // Delayed transition
                    Timer transitionTimer = new Timer(800, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent evt) {
                            // Redirect based on role
                            if (role.equals("Patient")) {
                                new PatientDetailsPage(s_id);
                            } else if (role.equals("Doctor")) {
                                new DoctorDetailsPage(s_id);
                            } else {
                                JOptionPane.showMessageDialog(SignupPage.this,
                                        "Welcome Admin! Your account has been created successfully.",
                                        "Account Created",
                                        JOptionPane.INFORMATION_MESSAGE);
                            }
                            dispose();
                        }
                    });
                    transitionTimer.setRepeats(false);
                    transitionTimer.start();
                }
            }
        } catch (Exception ex) {
            progressBar.setVisible(false);
            statusLabel.setText("Error: " + ex.getMessage());
            statusLabel.setForeground(new Color(231, 76, 60));
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

        new SignupPage();
    }
}