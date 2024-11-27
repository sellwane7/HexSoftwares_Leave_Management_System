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

        // Dashboard heading
        JLabel heading = new JLabel("Employee Dashboard", JLabel.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(heading, BorderLayout.NORTH);

        // Leave balance section
        JTextArea leaveBalanceArea = new JTextArea();
        leaveBalanceArea.setEditable(false);
        leaveBalanceArea.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(new JScrollPane(leaveBalanceArea), BorderLayout.WEST);

        // Leave requests section
        JTextArea leaveRequestsArea = new JTextArea();
        leaveRequestsArea.setEditable(false);
        leaveRequestsArea.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(new JScrollPane(leaveRequestsArea), BorderLayout.CENTER);

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        JButton requestLeaveButton = new JButton("Request Leave");
        JButton backButton = new JButton("Back");

        // Add actions for buttons
        requestLeaveButton.addActionListener(e -> {
            new LeaveRequestPage(employeeId);  // Open the Leave Request Page
            updateLeaveBalance(leaveBalanceArea); // Update the balance after a request
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

    // Method to load leave balance and display it in the UI
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

    // Method to load leave requests for the employee and display them in the UI
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

    // Method to update the leave balance after a leave request is made
    private void updateLeaveBalance(JTextArea leaveBalanceArea) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get the current balance of the employee
            String query = "SELECT balance_days FROM leave_balances WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int currentBalance = rs.getInt("balance_days");

                // Assuming you deduct the requested number of days, e.g. 2 days (adjust this logic as needed)
                int requestedLeaveDays = 2; // Replace with dynamic leave days requested
                if (currentBalance >= requestedLeaveDays) {
                    int newBalance = currentBalance - requestedLeaveDays;

                    // Update the balance in the database
                    String updateQuery = "UPDATE leave_balances SET balance_days = ? WHERE user_id = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                    updateStmt.setInt(1, newBalance);
                    updateStmt.setInt(2, employeeId);
                    updateStmt.executeUpdate();

                    // Refresh the leave balance display
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
        // Test EmployeeDashboard with a dummy user ID (replace with actual user ID in production)
        new EmployeeDashboard(1);
    }
}
