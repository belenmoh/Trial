package com.gym.controller;

import com.gym.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ReceptionistDashboardController {
    @FXML private Label welcomeLabel;
    @FXML private Label employeeIdLabel;
    @FXML private VBox mainContent;
    
    private User receptionist;

    public void setUser(User user) {
        this.receptionist = user;
        initializeDashboard();
    }

    private void initializeDashboard() {
        if (receptionist != null) {
            welcomeLabel.setText("Welcome, " + receptionist.getName() + "!");
            employeeIdLabel.setText("Employee ID: " + 
                ((com.gym.model.Receptionist) receptionist).getEmployeeId());
        }
    }

    @FXML
    private void handleMemberRegistration() {
        // Open member registration form
        System.out.println("Opening Member Registration...");
    }

    @FXML
    private void handleClassBooking() {
        // Open class booking interface
        System.out.println("Opening Class Booking...");
    }

    @FXML
    private void handlePaymentProcessing() {
        // Open payment processing interface
        System.out.println("Opening Payment Processing...");
    }

    @FXML
    private void handleMemberSearch() {
        // Open member search interface
        System.out.println("Opening Member Search...");
    }

    @FXML
    private void handleTodaySchedule() {
        // View today's class schedule
        System.out.println("Viewing Today's Schedule...");
    }

    @FXML
    private void handleLogout() {
        // Logout and return to login screen
        System.out.println("Logging out...");
    }
}
