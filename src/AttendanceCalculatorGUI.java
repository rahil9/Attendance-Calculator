import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

public class AttendanceCalculatorGUI extends JFrame {
    private JLabel titleLabel, nameLabel, sapIdLabel, subjectsLabel;
    private JTextField nameField, sapIdField, subjectsField;
    private JButton generateButton;

    public AttendanceCalculatorGUI() {
        setTitle("Attendance Calculator");
        setSize(400, 300);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        titleLabel = new JLabel("Attendance Calculator");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        nameLabel = new JLabel("Enter your name:");
        nameField = new JTextField(20);

        sapIdLabel = new JLabel("Enter your SAP ID:");
        sapIdField = new JTextField(20);

        subjectsLabel = new JLabel("Number of subjects?");
        subjectsField = new JTextField(10);

        generateButton = new JButton("Generate Tables");
        generateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generateTables();
            }
        });

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(nameLabel, gbc);

        gbc.gridx = 1;
        add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(sapIdLabel, gbc);

        gbc.gridx = 1;
        add(sapIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(subjectsLabel, gbc);

        gbc.gridx = 1;
        add(subjectsField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(generateButton, gbc);

        setVisible(true);
    }
    private void generateTables() {
        String name = nameField.getText();
        String sapId = sapIdField.getText();
        String numSubjectsInput = subjectsField.getText();

        // Validate name (string)
        if (!name.matches("[a-zA-Z ]+")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid name.", "Error", JOptionPane.ERROR_MESSAGE);
            nameField.setText(""); // Clear the name field
            return;
        }

        if (!sapId.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Please enter only numbers for the SAP ID.", "Error", JOptionPane.ERROR_MESSAGE);
            sapIdField.setText("");
            return;
        }

        // Validate number of subjects (only numbers)
        if (!numSubjectsInput.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for the number of subjects.", "Error", JOptionPane.ERROR_MESSAGE);
            subjectsField.setText("");
            return;
        }

        try {
            int numSubjects = Integer.parseInt(numSubjectsInput);
            if (numSubjects <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter a positive number of subjects.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            new AttendanceInputWindow(name, sapId, numSubjects);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number of subjects.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new AttendanceCalculatorGUI();
            }
        });
    }
}

class AttendanceInputWindow extends JFrame {
    private String name;
    private String sapId;
    private int numSubjects;
    private JTextField[] attendedFields;
    private JTextField[] totalFields;

    public AttendanceInputWindow(String name, String sapId, int numSubjects) {
        this.name = name;
        this.sapId = sapId;
        this.numSubjects = numSubjects;
        setTitle("Attendance Input - " + name);
        setSize(400, 350);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Subject"), gbc);

        gbc.gridx = 1;
        mainPanel.add(new JLabel("Lectures Attended"), gbc);

        gbc.gridx = 2;
        mainPanel.add(new JLabel("Total Lectures"), gbc);

        attendedFields = new JTextField[numSubjects];
        totalFields = new JTextField[numSubjects];

        for (int i = 0; i < numSubjects; i++) {
            gbc.gridx = 0;
            gbc.gridy = i + 1;
            mainPanel.add(new JLabel("Subject " + (i + 1)), gbc);

            gbc.gridx = 1;
            attendedFields[i] = new JTextField(10);
            mainPanel.add(attendedFields[i], gbc);

            gbc.gridx = 2;
            totalFields[i] = new JTextField(10);
            mainPanel.add(totalFields[i], gbc);
        }

        gbc.gridx = 0;
        gbc.gridy = numSubjects + 1;
        gbc.gridwidth = 3;
        JButton calculateButton = new JButton("Calculate");
        calculateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calculateAttendance();
            }
        });
        mainPanel.add(calculateButton, gbc);

        add(mainPanel);
        setVisible(true);
    }

    private void calculateAttendance() {
        StringBuilder warningMessage = new StringBuilder();
        StringBuilder successMessage = new StringBuilder();
        successMessage.append("Attendance Percentages for ").append(name).append(":\n\n");

        for (int i = 0; i < numSubjects; i++) {
            try {
                int attended = Integer.parseInt(attendedFields[i].getText());
                int total = Integer.parseInt(totalFields[i].getText());

                if (attended < 0 || total <= 0 || attended > total) {
                    JOptionPane.showMessageDialog(this, "Please enter valid attendance values for all subjects.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double attendancePercentage = (double) attended / total * 100;

                if (attendancePercentage < 80) {
                    warningMessage.append("Subject ").append(i + 1).append(": More lectures to be attended. Attendance Percentage: ").append(String.format("%.2f", attendancePercentage)).append("%\n");
                }

                successMessage.append("Subject ").append(i + 1).append(": ").append(String.format("%.2f", attendancePercentage)).append("%\n");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid attendance values for all subjects.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (!warningMessage.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Warning for " + name + ":\n\n" + warningMessage.toString(), "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, successMessage.toString() + "\nYour attendance is good.", "Success", JOptionPane.INFORMATION_MESSAGE);
        }

        writeAttendanceToCSV();

        dispose();
        Window[] windows = Window.getWindows();
        for (Window window : windows) {
            if (window instanceof AttendanceCalculatorGUI) {
                window.dispose();
                break;
            }
        }
    }
    private void writeAttendanceToCSV() {
        String fileName = "attendance.csv";
        File file = new File(fileName);
        boolean fileExists = file.exists() && file.length() > 0;

        try (FileWriter writer = new FileWriter(fileName, true)) {
            if (!fileExists) {
                writer.write("Name,SAP ID,Subject,Attended,Total,Percentage\n");
            }
            for (int i = 0; i < numSubjects; i++) {
                int attended = Integer.parseInt(attendedFields[i].getText());
                int total = Integer.parseInt(totalFields[i].getText());
                double attendancePercentage = (double) attended / total * 100;
                writer.write(name + "," + sapId + "," + (i + 1) + "," + attended + "," + total + "," + attendancePercentage + "\n");
            }
            writer.flush();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to write to CSV file.", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
