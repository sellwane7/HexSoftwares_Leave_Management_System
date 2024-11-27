import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LeaveRequestPage extends JFrame {
    private int employeeId;

    public LeaveRequestPage(int employeeId) {
        this.employeeId = employeeId;

        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        JLabel startDateLabel = new JLabel("Start Date (YYYY-MM-DD):");
        JTextField startDateField = new JTextField();

        JLabel endDateLabel = new JLabel("End Date (YYYY-MM-DD):");
        JTextField endDateField = new JTextField();

        JLabel reasonLabel = new JLabel("Reason:");
        JTextField reasonField = new JTextField();

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> submitRequest(startDateField.getText(), endDateField.getText(), reasonField.getText()));

        panel.add(startDateLabel);
        panel.add(startDateField);
        panel.add(endDateLabel);
        panel.add(endDateField);
        panel.add(reasonLabel);
        panel.add(reasonField);
        panel.add(new JLabel());
        panel.add(submitButton);

        add(panel);
        setVisible(true);
    }

    private void submitRequest(String startDate, String endDate, String reason) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO leave_requests (employee_id, start_date, end_date, reason) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, employeeId);
            stmt.setString(2, startDate);
            stmt.setString(3, endDate);
            stmt.setString(4, reason);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Leave request submitted!");
            dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

