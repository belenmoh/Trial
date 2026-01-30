package com.gym.controller;

import com.gym.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class AdminDashboardController {
    @FXML private Label welcomeLabel;
    @FXML private Label adminIdLabel;
    @FXML private VBox mainContent;
    
    private User admin;

    public void setUser(User user) {
        this.admin = user;
        initializeDashboard();
    }

    private void initializeDashboard() {
        if (admin != null) {
            welcomeLabel.setText("Welcome, " + admin.getName() + "!");
            adminIdLabel.setText("Admin ID: " + ((com.gym.model.Admin) admin).getAdminId());
        }
    }

    @FXML
    private void handleUserManagement() {
        // Navigate to user management screen
        System.out.println("Opening User Management...");
    }

    @FXML
    private void handleMemberManagement() {
        // Navigate to member management screen
        System.out.println("Opening Member Management...");
    }

    @FXML
    private void handleFinancialReports() {
        // Navigate to financial reports screen
        System.out.println("Opening Financial Reports...");
    }

    @FXML
    private void handleSystemSettings() {
        // Navigate to system settings screen
        System.out.println("Opening System Settings...");
    }

    @FXML
    private void handleLogout() {
        // Logout and return to login screen
        System.out.println("Logging out...");
    }
}
