package Controller;

import DAO.EmployeDAOImp;
import DAO.HolidayDAOImpl;
import Model.EmployeModel;
import Model.HolidayModel;  // Assuming you have a model for Holiday
import View.EmployeView;
import View.HolidayView;
import View.LoginView;
import View.PanelsView;

import javax.swing.*;
import java.sql.*;

public class MainController {
    private Connection connection;
    private LoginView loginView;
    private EmployeView employeView;
    private HolidayView holidayView;
    private EmployeModel employeLogique;  // Use EmployeLogique instead of DAO
    private HolidayModel holidayModel;  // Use HolidayModel

    // Constructor: Initialize necessary components
    public MainController(Connection connection) {
        this.connection = connection;
        this.loginView = new LoginView();  // Login view is created separately

        // Initialize the logic and DAO objects
        this.employeView = new EmployeView();
        this.holidayView = new HolidayView();
        this.employeLogique = new EmployeModel(new EmployeDAOImp());  // Pass the DAO to EmployeLogique
        this.holidayModel = new HolidayModel(new HolidayDAOImpl());  // Assuming HolidayModel takes HolidayDAOImpl

        // Add login functionality
        loginView.getLoginButton().addActionListener(e -> handleLogin());
    }

    // Method to handle login
    private void handleLogin() {
        String username = loginView.getUsername();
        String password = loginView.getPassword();

        try {
            // SQL query to check login credentials
            String query = "SELECT login.id, employee.role FROM login " +
                    "JOIN employee ON login.id = employee.id " +
                    "WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("id");
                String role = rs.getString("role");

                // Success: Show message and initialize views for the role
                JOptionPane.showMessageDialog(loginView, "Login successful!");
                loginView.dispose();  // Close login window
                initializeViews(userId, role);
            } else {
                loginView.displayMessage("Invalid username or password.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            loginView.displayMessage("Error connecting to database.");
        }
    }

    // Method to initialize the views after login
    private void initializeViews(int userId, String role) {
        if (role.equalsIgnoreCase("Admin")) {
            // Initialize admin-specific views and controllers
            new EmployeController(employeView, employeLogique);  // Pass EmployeLogique to EmployeController
            new HolidayController(holidayView, holidayModel);  // Pass HolidayModel to HolidayController

            // Create the main panel view containing both the employee and holiday views
            PanelsView panelsView = new PanelsView(holidayView, employeView);
        } else if (role.equalsIgnoreCase("Employee")) {
            EmployeController employeController = new EmployeController(employeView, employeLogique);
            employeView.setVisible(true);
            PanelsView panelsView = new PanelsView(holidayView, employeView);
        } else {
            JOptionPane.showMessageDialog(null, "Access Denied: Invalid role.");
        }
    }

    // Start the application and show the login view
    public void startApplication() {
        if (loginView != null) {
            loginView.setVisible(true);  // Show the login window
        } else {
            System.out.println("Error: LoginView is not initialized.");
        }
    }
}
