package SmartConsultingApp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class MedicationTracker extends JFrame {
    private String username;
    private int patientId;
    private JTable table;
    private DefaultTableModel model;

    // Smart Health Theme Colors
    private Color primaryColor = new Color(84, 160, 255); // Soft Blue
    private Color secondaryColor = new Color(84, 160, 255);// Soft Blue
    private Color backgroundColor = new Color(245, 250, 255); // Light Pastel Blue
    private Color accentColor = new Color(84, 160, 255);  // Soft Blue

    public MedicationTracker(String username) {
        this.username = username;
        this.patientId = fetchPatientId(username);

        if (patientId == -1) {
            JOptionPane.showMessageDialog(this, "Patient ID not found for username: " + username, "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        setTitle("Medication Tracker - " + username);
        setSize(900, 600); // Increased size for better layout
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
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(backgroundColor);

        // Table Setup
        String[] columnNames = {"Medicine Name", "Dosage", "Frequency", "Start Date", "End Date", "Allergies"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(230, 240, 255)); // Alternating row colors
                }
                ((JLabel) c).setForeground(Color.black); // Black font for table cells
                return c;
            }
        };
        table.getTableHeader().setBackground(primaryColor);
        table.getTableHeader().setForeground(Color.black); // black font for table headers
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(25);
        loadMedications();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(primaryColor), "Medication Details"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBackground(backgroundColor);

        JButton btnAdd = createStyledButton("➕ Add Medication", accentColor, Color.BLACK);
        btnAdd.setToolTipText("Add a new medication");
        btnAdd.addActionListener(e -> addMedication());

        JButton btnDelete = createStyledButton("❌ Remove Medication", secondaryColor, Color.BLACK);
        btnDelete.setToolTipText("Remove the selected medication");
        btnDelete.addActionListener(e -> removeMedication());
        btnDelete.setEnabled(false); // Enable only when a row is selected
        table.getSelectionModel().addListSelectionListener(e -> btnDelete.setEnabled(table.getSelectedRow() != -1));

        JButton btnBack = createStyledButton("🔙 Back", primaryColor, Color.BLACK); // Black font for back button
        btnBack.setToolTipText("Go back to the Patient Module");
        btnBack.addActionListener(e -> {
            new PatientModule(username); // Redirect to PatientModule
            dispose();
        });

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnBack);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("💊 Medication Tracker");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE); // White font for title
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        return headerPanel;
    }

    private int fetchPatientId(String username) {
        try (Connection con = DatabaseConnector.connect()) {
            String query = "SELECT P_id FROM patient WHERE S_id = (SELECT S_id FROM signup WHERE Username=?)";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getInt("P_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // Default value if not found
    }

    private void loadMedications() {
        try (Connection con = DatabaseConnector.connect()) {
            String query = "SELECT Medicine_Name, Dosage, Frequency, Start_Date, End_Date, Allergies_Noted FROM medication_tracker WHERE P_id=?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, patientId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("Medicine_Name"),
                        rs.getString("Dosage"),
                        rs.getString("Frequency"),
                        rs.getDate("Start_Date"),
                        rs.getDate("End_Date"),
                        rs.getString("Allergies_Noted")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addMedication() {
        JTextField medNameField = new JTextField();
        JTextField dosageField = new JTextField();
        String[] frequencyOptions = {"Once Daily", "Twice Daily", "Thrice Daily", "As Needed"};
        JComboBox<String> frequencyBox = new JComboBox<>(frequencyOptions);
        JTextField startDateField = new JTextField("YYYY-MM-DD");
        JTextField endDateField = new JTextField("YYYY-MM-DD");
        JTextArea allergiesField = new JTextArea(2, 20);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(backgroundColor);

        // Labels and Fields
        JLabel medNameLabel = new JLabel("Medicine Name:");
        medNameLabel.setForeground(Color.BLACK); // Black font
        panel.add(medNameLabel);
        medNameField.setForeground(Color.BLACK); // Black font
        panel.add(medNameField);

        JLabel dosageLabel = new JLabel("Dosage:");
        dosageLabel.setForeground(Color.BLACK); // Black font
        panel.add(dosageLabel);
        dosageField.setForeground(Color.BLACK); // Black font
        panel.add(dosageField);

        JLabel frequencyLabel = new JLabel("Frequency:");
        frequencyLabel.setForeground(Color.BLACK); // Black font
        panel.add(frequencyLabel);
        ((JLabel) frequencyBox.getRenderer()).setForeground(Color.BLACK); // Black font
        panel.add(frequencyBox);

        JLabel startDateLabel = new JLabel("Start Date (YYYY-MM-DD):");
        startDateLabel.setForeground(Color.BLACK); // Black font
        panel.add(startDateLabel);
        startDateField.setForeground(Color.BLACK); // Black font
        panel.add(startDateField);

        JLabel endDateLabel = new JLabel("End Date (YYYY-MM-DD):");
        endDateLabel.setForeground(Color.BLACK); // Black font
        panel.add(endDateLabel);
        endDateField.setForeground(Color.BLACK); // Black font
        panel.add(endDateField);

        JLabel allergiesLabel = new JLabel("Allergies Noted:");
        allergiesLabel.setForeground(Color.BLACK); // Black font
        panel.add(allergiesLabel);
        allergiesField.setForeground(Color.BLACK); // Black font
        panel.add(new JScrollPane(allergiesField));

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Medication", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String medName = medNameField.getText();
            String dosage = dosageField.getText();
            String frequency = (String) frequencyBox.getSelectedItem();
            String startDate = startDateField.getText();
            String endDate = endDateField.getText();
            String allergies = allergiesField.getText();

            if (!medName.isEmpty() && !dosage.isEmpty() && isValidDate(startDate) && isValidDate(endDate)) {
                try (Connection con = DatabaseConnector.connect()) {
                    String query = "INSERT INTO medication_tracker (P_id, Medicine_Name, Dosage, Frequency, Start_Date, End_Date, Allergies_Noted) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement pst = con.prepareStatement(query);
                    pst.setInt(1, patientId);
                    pst.setString(2, medName);
                    pst.setString(3, dosage);
                    pst.setString(4, frequency);
                    pst.setDate(5, Date.valueOf(startDate));
                    pst.setDate(6, Date.valueOf(endDate));
                    pst.setString(7, allergies);
                    pst.executeUpdate();

                    model.addRow(new Object[]{medName, dosage, frequency, startDate, endDate, allergies});
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please enter valid details.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void removeMedication() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            String medName = model.getValueAt(selectedRow, 0).toString();

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete " + medName + "?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection con = DatabaseConnector.connect()) {
                    String query = "DELETE FROM medication_tracker WHERE P_id=? AND Medicine_Name=?";
                    PreparedStatement pst = con.prepareStatement(query);
                    pst.setInt(1, patientId);
                    pst.setString(2, medName);
                    pst.executeUpdate();

                    model.removeRow(selectedRow);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Select a medication to remove.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isValidDate(String date) {
        try {
            new SimpleDateFormat("yyyy-MM-dd").parse(date);
            return true;
        } catch (Exception e) {
            return false;
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
        SwingUtilities.invokeLater(() -> new MedicationTracker("ziyauddin"));
    }
}