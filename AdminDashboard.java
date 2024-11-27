import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminDashboard extends JFrame {
    private JTable table; 
    private JButton approveButton, rejectButton, backButton;

    public AdminDashboard() {
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel heading = new JLabel("Admin Dashboard", JLabel.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 20));
        add(heading, BorderLayout.NORTH);

        table = new JTable();
        loadTableData(); 

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        approveButton = new JButton("Approve");
        rejectButton = new JButton("Reject");
        backButton = new JButton("Back");

        approveButton.addActionListener(e -> updateRequest("Approved"));
        rejectButton.addActionListener(e -> updateRequest("Rejected"));
        backButton.addActionListener(e -> {
            dispose(); // Close the Admin Dashboard
            new LoginPage(); //go back to the login page
        });

        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadTableData() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT lr.id, u.username, lr.start_date, lr.end_date, lr.reason, lr.status " +
                    "FROM leave_requests lr JOIN users u ON lr.employee_id = u.id " +
                    "WHERE lr.status = 'Pending'";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            DefaultTableModel model = new DefaultTableModel(
                    new Object[]{"Request ID", "Employee", "Start Date", "End Date", "Reason", "Status"}, 0);
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getDate("start_date"),
                        rs.getDate("end_date"),
                        rs.getString("reason"),
                        rs.getString("status")
                });
            }
            table.setModel(model);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateRequest(String status) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a request.");
            return;
        }

        int requestId = (int) table.getValueAt(selectedRow, 0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE leave_requests SET status = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, status);
            stmt.setInt(2, requestId);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Request " + status + "!");
            loadTableData(); 
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new AdminDashboard(); 
    }
}
