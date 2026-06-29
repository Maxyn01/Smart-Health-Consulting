package SmartConsultingApp;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;

public class LoginPage extends JFrame implements ActionListener {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, signupButton;
    private Color primaryColor = new Color(41, 128, 185); // Professional blue
    private Color accentColor = new Color(52, 152, 219); // Lighter blue
    private Color lightGray = new Color(240, 240, 240);
    private Color successColor = new Color(46, 204, 113); // Green
    private Color errorColor = new Color(231, 76, 60); // Red
    private Font labelFont = new Font("Poppins", Font.PLAIN, 14);
    private Font buttonFont = new Font("Poppins", Font.BOLD, 16);

    // Animation elements
    private JPanel animationPanel;
    private ArrayList<FloatingImage> floatingImages = new ArrayList<>();
    private Timer animationTimer;
    private JProgressBar progressBar;
    private JLabel statusLabel;

    public LoginPage() {
        setTitle("Healthcare Management System - Login");
        setSize(900, 600);
        setLocationRelativeTo(null); // Center on screen
        setResizable(false);

        // Main panel with two sections
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1, 2));

        // Left panel for animation
        animationPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(41, 128, 185),
                        getWidth(), getHeight(), new Color(52, 152, 219)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                for (FloatingImage img : floatingImages) {
                    img.draw(g2d);
                }
            }
        };
        animationPanel.setLayout(new BorderLayout());

        // Title inside animation panel
        JLabel animTitle = new JLabel("Healthcare Management", JLabel.CENTER);
        animTitle.setFont(new Font("Poppins", Font.BOLD, 28));
        animTitle.setForeground(Color.WHITE);
        animTitle.setBorder(BorderFactory.createEmptyBorder(40, 20, 0, 20));

        JLabel animSubtitle = new JLabel("Professional Care at Your Fingertips", JLabel.CENTER);
        animSubtitle.setFont(new Font("Poppins", Font.ITALIC, 16));
        animSubtitle.setForeground(new Color(255, 255, 255, 220));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(animTitle, BorderLayout.NORTH);
        titlePanel.add(animSubtitle, BorderLayout.CENTER);
        animationPanel.add(titlePanel, BorderLayout.NORTH);

        // Right panel for login
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BorderLayout(10, 10));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(60, 40, 60, 40));
        loginPanel.setBackground(Color.WHITE);

        // Header panel with logo/title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("Sign In", JLabel.LEFT);
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 26));
        titleLabel.setForeground(new Color(70, 70, 70));

        JLabel subtitleLabel = new JLabel("Please enter your credentials", JLabel.LEFT);
        subtitleLabel.setFont(new Font("Poppins", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.CENTER);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(3, 1, 10, 0));
        formPanel.setBackground(Color.WHITE);

        // Username panel with placeholder
        JPanel usernamePanel = new JPanel(new BorderLayout(5, 5));
        usernamePanel.setBackground(Color.WHITE);
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(labelFont);

        usernameField = new PlaceholderTextField("Enter your username");
        usernameField.setFont(labelFont);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(lightGray, 8),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        usernameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                usernameField.setBorder(BorderFactory.createCompoundBorder(
                        new RoundBorder(primaryColor, 8),
                        BorderFactory.createEmptyBorder(8, 15, 8, 15)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                usernameField.setBorder(BorderFactory.createCompoundBorder(
                        new RoundBorder(lightGray, 8),
                        BorderFactory.createEmptyBorder(8, 15, 8, 15)
                ));
            }
        });

        usernamePanel.add(usernameLabel, BorderLayout.NORTH);
        usernamePanel.add(usernameField, BorderLayout.CENTER);

        // Password panel with placeholder and toggle
        JPanel passwordPanel = new JPanel(new BorderLayout(5, 5));
        passwordPanel.setBackground(Color.WHITE);
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(labelFont);

        passwordField = new PlaceholderPasswordField("Enter your password");
        passwordField.setFont(labelFont);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(lightGray, 8),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                        new RoundBorder(primaryColor, 8),
                        BorderFactory.createEmptyBorder(8, 15, 8, 15)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                        new RoundBorder(lightGray, 8),
                        BorderFactory.createEmptyBorder(8, 15, 8, 15)
                ));
            }
        });

        JButton togglePasswordButton = new JButton("\u2795"); // Eye icon
        togglePasswordButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        togglePasswordButton.setForeground(accentColor);
        togglePasswordButton.setBorder(null);
        togglePasswordButton.setContentAreaFilled(false);
        togglePasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        togglePasswordButton.setFocusPainted(false);
        togglePasswordButton.addActionListener(e -> {
            if (passwordField.getEchoChar() == '\u2022') { // Bullet character
                passwordField.setEchoChar((char) 0); // Show password
                togglePasswordButton.setText("\u2796"); // Open eye icon
            } else {
                passwordField.setEchoChar('\u2022'); // Hide password
                togglePasswordButton.setText("\u2795"); // Closed eye icon
            }
        });

        passwordPanel.add(passwordLabel, BorderLayout.NORTH);
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        passwordPanel.add(togglePasswordButton, BorderLayout.EAST);

        // Status panel
        JPanel statusPanel = new JPanel(new BorderLayout(5, 5));
        statusPanel.setBackground(Color.WHITE);

// Add extra padding to move the progress bar down
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(15, 0, 0, 0), // Top and bottom padding increased
                BorderFactory.createEmptyBorder()
        ));

        progressBar = new JProgressBar(0, 100) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int width = getWidth();
                int height = getHeight();
                GradientPaint gradient = new GradientPaint(0, 0, primaryColor, width, 0, accentColor);
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, (int) (width * getPercentComplete()), height, 10, 10);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        progressBar.setStringPainted(false);
        progressBar.setVisible(false);
        progressBar.setForeground(primaryColor);
        progressBar.setBackground(Color.WHITE);
        progressBar.setBorder(null);

        statusLabel = new JLabel("");
        statusLabel.setFont(new Font("Poppins", Font.ITALIC, 12));
        statusLabel.setForeground(Color.GRAY);

        statusPanel.add(progressBar, BorderLayout.CENTER);
        statusPanel.add(statusLabel, BorderLayout.SOUTH);

        formPanel.add(usernamePanel);
        formPanel.add(passwordPanel);
        formPanel.add(statusPanel);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 1, 0, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 5, 0));

        // Styled login button
        loginButton = new JButton("Login");
        loginButton.setFont(buttonFont);
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(primaryColor);
        loginButton.setBorder(new RoundBorder(accentColor, 25));
        loginButton.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                AbstractButton button = (AbstractButton) c;
                ButtonModel model = button.getModel();

                if (model.isPressed()) {
                    g2d.setColor(accentColor.darker());
                } else if (model.isRollover()) {
                    g2d.setColor(accentColor.brighter());
                } else {
                    g2d.setColor(primaryColor);
                }

                g2d.fill(new RoundRectangle2D.Double(0, 0, c.getWidth(), c.getHeight(), 25, 25));
                g2d.setColor(new Color(25, 90, 140));
                g2d.draw(new RoundRectangle2D.Double(0, 0, c.getWidth() - 1, c.getHeight() - 1, 25, 25));
                g2d.dispose();
                super.paint(g, c);
            }
        });
        loginButton.addActionListener(this);

        // Styled sign-up button
        signupButton = new JButton("New User? Sign Up");
        signupButton.setFont(new Font("Poppins", Font.PLAIN, 14));
        signupButton.setForeground(primaryColor);
        signupButton.setBackground(Color.WHITE);
        signupButton.setBorder(null);
        signupButton.setFocusPainted(false);
        signupButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signupButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                signupButton.setText("<html><u>New User? Sign Up</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                signupButton.setText("New User? Sign Up");
            }
        });
        signupButton.addActionListener(this);

        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);

        // Add components to login panel
        loginPanel.add(headerPanel, BorderLayout.NORTH);
        loginPanel.add(formPanel, BorderLayout.CENTER);
        loginPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add both panels to main panel
        mainPanel.add(animationPanel);
        mainPanel.add(loginPanel);

        // Add main panel to frame
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialize floating images
        initFloatingImages();

        // Start animation timer
        animationTimer = new Timer(50, e -> {
            updateFloatingImages();
            animationPanel.repaint();
        });
        animationTimer.start();

        setVisible(true);
    }

    private void initFloatingImages() {
        String[] imageNames = {"doctor", "patient", "hospital", "heartbeat", "pills", "stethoscope"};
        Random rand = new Random();
        int panelWidth = getWidth() / 2;
        int panelHeight = getHeight();

        for (int i = 0; i < 12; i++) {
            String imageName = imageNames[rand.nextInt(imageNames.length)];
            int size = 30 + rand.nextInt(40);
            int x = rand.nextInt(panelWidth - size);
            int y = rand.nextInt(panelHeight - size);
            double speedX = -0.5 + rand.nextDouble();
            double speedY = -0.5 + rand.nextDouble();
            floatingImages.add(new FloatingImage(imageName, x, y, size, speedX, speedY));
        }
    }

    private void updateFloatingImages() {
        int panelWidth = animationPanel.getWidth();
        int panelHeight = animationPanel.getHeight();

        for (FloatingImage img : floatingImages) {
            img.move(panelWidth, panelHeight);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            progressBar.setVisible(true);
            statusLabel.setText("Authenticating...");
            progressBar.setValue(0);

            Timer loginTimer = new Timer(30, evt -> {
                int progress = progressBar.getValue() + 3;
                progressBar.setValue(progress);

                if (progress >= 100) {
                    ((Timer) evt.getSource()).stop();
                    loginUser(username, password);
                }
            });
            loginTimer.start();
        } else if (e.getSource() == signupButton) {
            new SignupPage();
        }
    }

    public void loginUser(String username, String password) {
        try (Connection con = DatabaseConnector.connect()) {
            String query = "SELECT * FROM Signup WHERE Username=? AND Password=?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String role = rs.getString("Role");
                statusLabel.setText("Login successful!");
                statusLabel.setForeground(successColor);

                Timer transitionTimer = new Timer(800, evt -> {
                    JOptionPane.showMessageDialog(LoginPage.this,
                            "Welcome back! You are logged in as " + role,
                            "Login Successful",
                            JOptionPane.INFORMATION_MESSAGE);

                    switch (role) {
                        case "Patient":
                            new PatientModule(username);
                            break;
                        case "Doctor":
                            new DoctorModule(username);
                            break;
                        case "Admin":
                            new AdminModule();
                            break;
                    }

                    animationTimer.stop();
                    dispose();
                });
                transitionTimer.setRepeats(false);
                transitionTimer.start();
            } else {
                progressBar.setVisible(false);
                statusLabel.setText("Invalid username or password");
                statusLabel.setForeground(errorColor);

                final int[] directions = {-1, 1, -1, 1, -1, 1, -2, 2, -2, 2};
                final Point originalLocation = getLocation();

                Timer shakeTimer = new Timer(30, evt -> {
                    int count = ((Timer) evt.getSource()).getDelay();
                    if (count < directions.length) {
                        Point p = getLocation();
                        setLocation(p.x + directions[count], p.y);
                        count++;
                    } else {
                        ((Timer) evt.getSource()).stop();
                        setLocation(originalLocation);
                    }
                });
                shakeTimer.start();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            progressBar.setVisible(false);
            statusLabel.setText("Connection error");
            statusLabel.setForeground(errorColor);
        }
    }

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

    private class PlaceholderTextField extends JTextField {
        private String placeholder;

        public PlaceholderTextField(String placeholder) {
            this.placeholder = placeholder;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty() && !isFocusOwner()) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(150, 150, 150)); // Gray color for placeholder
                g2d.setFont(getFont().deriveFont(Font.ITALIC));
                Insets insets = getInsets();
                int padding = 10; // Adjust padding as needed
                g2d.drawString(placeholder, insets.left + padding, g.getFontMetrics().getMaxAscent() + insets.top);
                g2d.dispose();
            }
        }
    }

    private class PlaceholderPasswordField extends JPasswordField {
        private String placeholder;

        public PlaceholderPasswordField(String placeholder) {
            this.placeholder = placeholder;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getPassword().length == 0 && !isFocusOwner()) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(150, 150, 150)); // Gray color for placeholder
                g2d.setFont(getFont().deriveFont(Font.ITALIC));
                Insets insets = getInsets();
                int padding = 10; // Adjust padding as needed
                g2d.drawString(placeholder, insets.left + padding, g.getFontMetrics().getMaxAscent() + insets.top);
                g2d.dispose();
            }
        }
    }

    private class FloatingImage {
        private String type;
        private double x, y;
        private int size;
        private double speedX, speedY;
        private float opacity;

        public FloatingImage(String type, double x, double y, int size, double speedX, double speedY) {
            this.type = type;
            this.x = x;
            this.y = y;
            this.size = size;
            this.speedX = speedX;
            this.speedY = speedY;
            this.opacity = 0.2f + new Random().nextFloat() * 0.4f;
        }

        public void move(int maxWidth, int maxHeight) {
            x += speedX;
            y += speedY;

            if (x <= 0 || x + size >= maxWidth) {
                speedX = -speedX;
            }
            if (y <= 0 || y + size >= maxHeight) {
                speedY = -speedY;
            }
        }

        public void draw(Graphics2D g2d) {
            Composite originalComposite = g2d.getComposite();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            g2d.setColor(Color.WHITE);

            switch (type) {
                case "doctor":
                    drawDoctorIcon(g2d);
                    break;
                case "patient":
                    drawPatientIcon(g2d);
                    break;
                default:
                    g2d.fillOval((int) x, (int) y, size, size);
            }

            g2d.setComposite(originalComposite);
        }

        private void drawDoctorIcon(Graphics2D g2d) {
            int x = (int) this.x;
            int y = (int) this.y;
            g2d.fillOval(x + size / 4, y, size / 2, size / 2); // Head
            g2d.fillRect(x + size / 3, y + size / 2, size / 3, size / 2); // Body
        }

        private void drawPatientIcon(Graphics2D g2d) {
            int x = (int) this.x;
            int y = (int) this.y;
            g2d.fillOval(x + size / 4, y, size / 2, size / 2); // Head
            g2d.drawLine(x + size / 2, y + size / 2, x + size / 2, y + size * 3 / 4); // Body
            g2d.drawLine(x + size / 4, y + size * 2 / 3, x + size * 3 / 4, y + size * 2 / 3); // Arms
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        new LoginPage();
    }
}