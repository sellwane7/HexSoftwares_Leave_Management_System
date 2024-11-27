# HexSoftwares_Leave_Management_System
# Employee Leave Management System
# Overview
The Employee Leave Management System is a simple desktop-based Java application designed to manage employee leave requests, approvals, and leave balances efficiently. This system provides different interfaces for employees and administrators, enabling efficient leave management and decision-making processes.

Employees can view their leave balances, submit leave requests, and check the status of their submitted requests.
Administrators can view, approve, or reject leave requests submitted by employees.
* The application is built using Java Swing for the GUI, MySQL for the database, and MySQL Connector (mysql-connector-j-9.0.0.jar) for database connectivity.

# Features
1. Employee Features
* View available leave balances.
* Submit leave requests specifying start date, end date, and reason.
* View the status of submitted leave requests (Pending, Approved, or Rejected).
2. Admin Features
* View a list of all pending leave requests from employees.
* Approve or reject leave requests, automatically updating their status.
3. Database-Driven
* Leave balances are tracked and updated actively in the database.
* Leave requests and their statuses are stored in a MySQL database.
4. Interactive Dashboard
* Admin Dashboard: Provides tools to manage and approve leave requests.
* Employee Dashboard: Displays leave balances and request history in a user-friendly format.

# Technology used
* IDE: IntelliJ IDEA
* Programming Language: Java (JDK 8 or later)
* Database: MySQL
* JDBC Driver: mysql-connector-j-9.0.0.jar
