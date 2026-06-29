package SmartConsultingApp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.*;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

public class HealthTracker extends JFrame {
    private String username;
    private int patientId = -1;  // Default invalid value
    private JTextField txtSystolic, txtDiastolic;
    private JTextField txtSugarLevel, txtOxygenLevel, txtTemperature;
    private JTextField txtSleepTime, txtWakeTime; // New fields for sleep tracking
    private JPanel graphPanel;
    private JButton btnSaveAll, btnGraphs, backButton; // Buttons for saving data and showing graphs
    private JLabel patientInfoLabel, statusLabel;
    private JTabbedPane tabbedPane;
    private Color themeColor = new Color(41, 128, 185);
    private Color secondaryColor = new Color(84, 160, 255); //  Soft Blue
    private Color accentColor = new Color(84, 160, 255); //  Soft Blue

    public HealthTracker(String username) {
        this.username = username;
        setTitle("Health Tracker - Smart Consulting");
        setSize(1000, 700); // Increased size for better layout
        setLayout(new BorderLayout());

        // Custom Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Button.background", accentColor);
            UIManager.put("Button.foreground", Color.WHITE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Main Panel with Padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Increased padding
        mainPanel.setBackground(new Color(245, 245, 245)); // Light gray background

        // Fetch Patient ID using username
        fetchPatientId();
        if (patientId == -1) {
            JOptionPane.showMessageDialog(this,
                    "Patient ID not found! Unable to track health.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Tabbed Pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));
        tabbedPane.setBackground(new Color(240, 240, 240));

        // Input Panel
        JPanel inputPanelContainer = createInputPanel();
        tabbedPane.addTab("Data Entry", new ImageIcon(), inputPanelContainer, "Enter your health data");

        // Dashboard Panel
        graphPanel = new JPanel(new GridLayout(1, 3, 15, 15));
        graphPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        graphPanel.setBackground(new Color(240, 240, 240));
        tabbedPane.addTab("Graph", new ImageIcon(), graphPanel, "View your health metrics");

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        // Status Bar
        JPanel statusBar = createStatusBar();
        add(statusBar, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        // Show initial graphs
        refreshGraphs();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(themeColor);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20)); // Increased padding

        JLabel titleLabel = new JLabel("Health Tracker Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        patientInfoLabel = new JLabel("Patient: " + username + " (ID: " + patientId + ")");
        patientInfoLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        patientInfoLabel.setForeground(Color.WHITE);
        headerPanel.add(patientInfoLabel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createInputPanel() {
        JPanel inputPanelContainer = new JPanel(new BorderLayout());
        inputPanelContainer.setBorder(new EmptyBorder(20, 20, 20, 20));
        inputPanelContainer.setBackground(new Color(240, 240, 240));

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

        // Blood Pressure Panel
        JPanel bpPanel = createStyledInputPanel("Blood Pressure",
                "Systolic (mmHg):", "Diastolic (mmHg):",
                txtSystolic = new JTextField(10), txtDiastolic = new JTextField(10));

        // Health Metrics Panel
        JPanel metricsPanel = createStyledInputPanel("Health Metrics",
                "Sugar Level (mg/dL):", "Oxygen Level (%):", "Temperature (°C):",
                txtSugarLevel = new JTextField(10),
                txtOxygenLevel = new JTextField(10),
                txtTemperature = new JTextField(10));

        // Sleep Tracking Panel
        JPanel sleepPanel = createStyledInputPanel("Sleep Tracking",
                "Sleep Time (HH:mm):", "Wake Up Time (HH:mm):",
                txtSleepTime = new JTextField(10), txtWakeTime = new JTextField(10));

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(new Color(240, 240, 240));

        // Save All Data Button
        btnSaveAll = createStyledButton("Save All Data", new Color(52, 152, 219), Color.black); // Green background
        btnSaveAll.addActionListener(e -> {
            saveBloodPressure();
            saveHealthMetrics();
            saveSleepData();
            statusLabel.setText("All data saved at " + java.time.LocalDateTime.now().toString());
            refreshGraphs(); // Refresh graphs after saving data
        });

// Graphs Button
        btnGraphs = createStyledButton("Graphs", new Color(52, 152, 219), Color.black); // Blue background
        btnGraphs.addActionListener(e -> showGraphsInNewWindow());

        // Back Button (Newly Added)
        JButton backButton = createStyledButton("🔙 Back", secondaryColor, Color.BLACK); // Red background
        backButton.setToolTipText("Go back to the previous module");
        backButton.addActionListener(e -> {
            new PatientModule(username); // Redirect to PatientModule
            dispose();
        });
// Add buttons to the panel
        buttonPanel.add(btnSaveAll);
        buttonPanel.add(btnGraphs);
        buttonPanel.add(backButton); // Added "Back" button
        // Add Panels to Input Panel
        inputPanel.add(bpPanel);
        inputPanel.add(Box.createVerticalStrut(20));
        inputPanel.add(metricsPanel);
        inputPanel.add(Box.createVerticalStrut(20));
        inputPanel.add(sleepPanel);
        inputPanel.add(Box.createVerticalStrut(20));
        inputPanel.add(buttonPanel);

        inputPanelContainer.add(new JScrollPane(inputPanel), BorderLayout.CENTER);
        return inputPanelContainer;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(new EmptyBorder(10, 20, 10, 20));
        statusBar.setBackground(new Color(220, 220, 220));

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statusBar.add(statusLabel, BorderLayout.WEST);

        return statusBar;
    }

    private JPanel createStyledInputPanel(String title, String label1, String label2, JTextField field1, JTextField field2) {
        return createStyledInputPanel(title, label1, label2, null, field1, field2, null);
    }

    private JPanel createStyledInputPanel(String title, String label1, String label2, String label3,
                                          JTextField field1, JTextField field2, JTextField field3) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(themeColor, 2),
                title, TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 16)));
        panel.setBackground(new Color(240, 240, 240));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lbl1 = new JLabel(label1);
        lbl1.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(lbl1, gbc);
        gbc.gridx = 1;
        panel.add(field1, gbc);

        JLabel lbl2 = new JLabel(label2);
        lbl2.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(lbl2, gbc);
        gbc.gridx = 1;
        panel.add(field2, gbc);

        if (label3 != null && field3 != null) {
            JLabel lbl3 = new JLabel(label3);
            lbl3.setFont(new Font("Arial", Font.PLAIN, 14));
            gbc.gridx = 0;
            gbc.gridy = 2;
            panel.add(lbl3, gbc);
            gbc.gridx = 1;
            panel.add(field3, gbc);
        }

        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor); // Custom background color
        button.setForeground(fgColor); // Custom foreground color
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Add hover effects
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter()); // Lighten the color on hover
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor); // Restore original color
            }
        });

        return button;
    }

    private void fetchPatientId() {
        try (Connection con = DatabaseConnector.connect()) {
            String query = "SELECT P_id FROM Patient WHERE S_id = (SELECT S_id FROM Signup WHERE Username=?)";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                patientId = rs.getInt("P_id");
                System.out.println("Patient ID found: " + patientId); // Debug print
            } else {
                System.out.println("No patient ID found for username: " + username); // Debug print
                patientId = -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Database error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "An unexpected error occurred: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveBloodPressure() {
        if (patientId == -1) {
            JOptionPane.showMessageDialog(this,
                    "Cannot save data without valid Patient ID.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String systolic = txtSystolic.getText().trim();
        String diastolic = txtDiastolic.getText().trim();

        if (systolic.isEmpty() || diastolic.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both systolic and diastolic values.",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int sysValue = Integer.parseInt(systolic);
            int diaValue = Integer.parseInt(diastolic);

            if (sysValue < 50 || sysValue > 250 || diaValue < 30 || diaValue > 150) {
                int option = JOptionPane.showConfirmDialog(this,
                        "The blood pressure values seem unusual. Are you sure they are correct?",
                        "Confirm Values", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (option != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            try (Connection con = DatabaseConnector.connect()) {
                String query = "INSERT INTO health_tracker (P_id, BP, Record_Date) VALUES (?, ?, CURDATE())";
                PreparedStatement pst = con.prepareStatement(query);
                pst.setInt(1, patientId);
                pst.setString(2, sysValue + "/" + diaValue);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this,
                        "Blood pressure data saved successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                txtSystolic.setText("");
                txtDiastolic.setText("");
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error saving blood pressure data: " + e.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid numbers for blood pressure.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveHealthMetrics() {
        if (patientId == -1) {
            JOptionPane.showMessageDialog(this,
                    "Cannot save data without valid Patient ID.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sugarLevel = txtSugarLevel.getText().trim();
        String oxygenLevel = txtOxygenLevel.getText().trim();
        String temperature = txtTemperature.getText().trim();

        if (sugarLevel.isEmpty() || oxygenLevel.isEmpty() || temperature.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter all health metrics.",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int sugarValue = Integer.parseInt(sugarLevel);
            int oxygenValue = Integer.parseInt(oxygenLevel);
            double tempValue = Double.parseDouble(temperature);

            if (sugarValue < 0 || sugarValue > 500 || oxygenValue < 0 || oxygenValue > 100 || tempValue < 30 || tempValue > 45) {
                int option = JOptionPane.showConfirmDialog(this,
                        "The health metrics values seem unusual. Are you sure they are correct?",
                        "Confirm Values", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (option != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            try (Connection con = DatabaseConnector.connect()) {
                String query = "UPDATE health_tracker SET Sugar_Level = ?, Oxygen_Level = ?, Temperature = ?, Record_Date = CURDATE() WHERE P_id = ?";
                PreparedStatement pst = con.prepareStatement(query);
                pst.setString(1, sugarLevel);
                pst.setString(2, oxygenLevel);
                pst.setString(3, temperature);
                pst.setInt(4, patientId);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this,
                        "Health metrics data saved successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                txtSugarLevel.setText("");
                txtOxygenLevel.setText("");
                txtTemperature.setText("");
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error saving health metrics data: " + e.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid numbers for health metrics.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveSleepData() {
        if (patientId == -1) {
            JOptionPane.showMessageDialog(this,
                    "Cannot save data without valid Patient ID.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sleepTime = txtSleepTime.getText().trim();
        String wakeTime = txtWakeTime.getText().trim();

        if (sleepTime.isEmpty() || wakeTime.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both sleep time and wake-up time.",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            LocalTime sleep = LocalTime.parse(sleepTime, DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime wake = LocalTime.parse(wakeTime, DateTimeFormatter.ofPattern("HH:mm"));

            long sleepHours = Duration.between(sleep, wake).toHours();
            if (sleepHours < 0) {
                sleepHours += 24; // Adjust for overnight sleep
            }

            try (Connection con = DatabaseConnector.connect()) {
                String query = "INSERT INTO sleeptracker (P_id, SleepTime, WakeTime, DurationHours, Record_Date) VALUES (?, ?, ?, ?, CURDATE())";
                PreparedStatement pst = con.prepareStatement(query);
                pst.setInt(1, patientId);
                pst.setString(2, sleepTime);
                pst.setString(3, wakeTime);
                pst.setLong(4, sleepHours);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this,
                        "Sleep data saved successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                txtSleepTime.setText("");
                txtWakeTime.setText("");
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error saving sleep data: " + e.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid time in HH:mm format (e.g., 22:30).",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshGraphs() {
        graphPanel.removeAll();

        // Create the blood pressure graph
        JPanel bpGraphPanel = createBloodPressureGraph();
        graphPanel.add(bpGraphPanel);

        // Create the health metrics graph
        JPanel metricsGraphPanel = createHealthMetricsGraph();
        graphPanel.add(metricsGraphPanel);

        // Create the sleep graph
        JPanel sleepGraphPanel = createSleepGraph();
        graphPanel.add(sleepGraphPanel);

        graphPanel.revalidate();
        graphPanel.repaint();
    }

    private void showGraphsInNewWindow() {
        JFrame graphFrame = new JFrame("Graphs - Health Tracker");
        graphFrame.setSize(1000, 600);
        graphFrame.setLayout(new GridLayout(1, 3, 15, 15)); // Adjusted layout for better spacing
        graphFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        graphFrame.setLocationRelativeTo(null);

        // Create the blood pressure graph
        JPanel bpGraphPanel = createBloodPressureGraph();
        graphFrame.add(bpGraphPanel);

        // Create the health metrics graph
        JPanel metricsGraphPanel = createHealthMetricsGraph();
        graphFrame.add(metricsGraphPanel);

        // Create the sleep graph
        JPanel sleepGraphPanel = createSleepGraph();
        graphFrame.add(sleepGraphPanel);

        graphFrame.setVisible(true);
    }

    private JPanel createBloodPressureGraph() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try (Connection con = DatabaseConnector.connect()) {
            String query = "SELECT BP, Record_Date FROM health_tracker WHERE P_id = ? ORDER BY Record_Date ASC LIMIT 10";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, patientId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String[] bpValues = rs.getString("BP").split("/");
                String date = rs.getString("Record_Date");
                int systolic = Integer.parseInt(bpValues[0]);
                int diastolic = Integer.parseInt(bpValues[1]);
                dataset.addValue(systolic, "Systolic", date);
                dataset.addValue(diastolic, "Diastolic", date);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error retrieving blood pressure data: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return createChartPanel(dataset, "Blood Pressure History", "Date", "Blood Pressure (mmHg)");
    }

    private JPanel createHealthMetricsGraph() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try (Connection con = DatabaseConnector.connect()) {
            String query = "SELECT Sugar_Level, Oxygen_Level, Temperature, Record_Date FROM health_tracker WHERE P_id = ? ORDER BY Record_Date ASC LIMIT 10";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, patientId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String date = rs.getString("Record_Date");
                dataset.addValue(rs.getInt("Sugar_Level"), "Sugar Level", date);
                dataset.addValue(rs.getInt("Oxygen_Level"), "Oxygen Level", date);
                dataset.addValue(rs.getInt("Temperature"), "Temperature", date);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error retrieving health metrics data: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return createChartPanel(dataset, "Health Metrics History", "Date", "Values");
    }

    private JPanel createSleepGraph() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try (Connection con = DatabaseConnector.connect()) {
            String query = "SELECT DurationHours, Record_Date FROM SleepTracker WHERE P_id = ? ORDER BY Record_Date ASC LIMIT 10";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, patientId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String date = rs.getString("Record_Date");
                dataset.addValue(rs.getLong("DurationHours"), "Sleep Duration (Hours)", date);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error retrieving sleep data: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return createChartPanel(dataset, "Sleep Duration History", "Date", "Hours");
    }

    private JPanel createChartPanel(DefaultCategoryDataset dataset, String title, String xAxisLabel, String yAxisLabel) {
        JFreeChart chart = ChartFactory.createLineChart(
                title,
                xAxisLabel,
                yAxisLabel,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = new BarRenderer();

        // Customize colors for each series
        renderer.setSeriesPaint(0, new Color(231, 76, 60)); // Red for first series
        if (dataset.getRowCount() > 1) {
            renderer.setSeriesPaint(1, new Color(52, 152, 219)); // Blue for second series
        }
        if (dataset.getRowCount() > 2) {
            renderer.setSeriesPaint(2, new Color(142, 68, 173)); // Purple for third series
        }

        // Enable tooltips
        plot.setRenderer(renderer);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(themeColor, 2),
                        title,
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 16)
                ),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        return chartPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HealthTracker("ziyauddin"));
    }
}