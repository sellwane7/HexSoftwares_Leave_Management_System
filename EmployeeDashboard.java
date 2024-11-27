import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EmployeeDashboard extends JFrame {
    private int employeeId;

    // Constructor that initializes the employee dashboard
    public EmployeeDashboard(int employeeId) {
        this.employeeId = employeeId;

        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());

        JLabel heading = new JLabel("Employee Dashboard", JLabel.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(heading, BorderLayout.NORTH);

        JTextArea leaveBalanceArea = new JTextArea();
        leaveBalanceArea.setEditable(false);
        leaveBalanceArea.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(new JScrollPane(leaveBalanceArea), BorderLayout.WEST);

        JTextArea leaveRequestsArea = new JTextArea();
        leaveRequestsArea.setEditable(false);
        leaveRequestsArea.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(new JScrollPane(leaveRequestsArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton requestLeaveButton = new JButton("Request Leave");
        JButton backButton = new JButton("Back");

        requestLeaveButton.addActionListener(e -> {
            new LeaveRequestPage(employeeId);  
            updateLeaveBalance(leaveBalanceArea);
        });

        backButton.addActionListener(e -> {
            dispose(); // Close current dashboard
            new LoginPage(); // Open the login page
        });

        buttonPanel.add(requestLeaveButton);
        buttonPanel.add(backButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);

        // Load data
        loadLeaveBalance(leaveBalanceArea);
        loadLeaveRequests(leaveRequestsArea);

        setVisible(true);
    }

    private void loadLeaveBalance(JTextArea leaveBalanceArea) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT balance_days FROM leave_balances WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                leaveBalanceArea.setText("Leave Balance: " + rs.getInt("balance_days") + " days");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadLeaveRequests(JTextArea leaveRequestsArea) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT start_date, end_date, reason, status FROM leave_requests WHERE employee_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();

            leaveRequestsArea.setText("Your Leave Requests:\n\n");
            while (rs.next()) {
                leaveRequestsArea.append("Start Date: " + rs.getDate("start_date") + "\n");
                leaveRequestsArea.append("End Date: " + rs.getDate("end_date") + "\n");
                leaveRequestsArea.append("Reason: " + rs.getString("reason") + "\n");
                leaveRequestsArea.append("Status: " + rs.getString("status") + "\n\n");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateLeaveBalance(JTextArea leaveBalanceArea) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT balance_days FROM leave_balances WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int currentBalance = rs.getInt("balance_days");

                int requestedLeaveDays = 2; 
                if (currentBalance >= requestedLeaveDays) {
                    int newBalance = currentBalance - requestedLeaveDays;

                    String updateQuery = "UPDATE leave_balances SET balance_days = ? WHERE user_id = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                    updateStmt.setInt(1, newBalance);
                    updateStmt.setInt(2, employeeId);
                    updateStmt.executeUpdate();

                    leaveBalanceArea.setText("Leave Balance: " + newBalance + " days");
                    JOptionPane.showMessageDialog(this, "Leave request submitted. Updated balance: " + newBalance + " days");
                } else {
                    JOptionPane.showMessageDialog(this, "Insufficient leave balance.");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new EmployeeDashboard(1);
    }
}
