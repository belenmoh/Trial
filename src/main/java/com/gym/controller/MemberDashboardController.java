package com.gym.controller;

import com.gym.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class MemberDashboardController {
    @FXML private Label welcomeLabel;
    @FXML private Label membershipStatusLabel;
    @FXML private Label membershipTypeLabel;
    @FXML private VBox mainContent;
    
    private User member;

    public void setUser(User user) {
        this.member = user;
        initializeDashboard();
    }

    private void initializeDashboard() {
        if (member != null) {
            welcomeLabel.setText("Welcome, " + member.getName() + "!");
            
            com.gym.model.Member m = (com.gym.model.Member) member;
            membershipTypeLabel.setText("Membership: " + 
                (m.getMembership() != null ? m.getMembership().getName() : "None"));
            
            boolean isActive = m.isMembershipActive();
            membershipStatusLabel.setText("Status: " + (isActive ? "Active" : "Inactive"));
            membershipStatusLabel.setStyle(isActive ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
        }
    }

    @FXML
    private void handleBookClass() {
        // Open class booking interface
        System.out.println("Opening Class Booking...");
    }

    @FXML
    private void handleViewSchedule() {
        // View member's class schedule
        System.out.println("Viewing Class Schedule...");
    }

    @FXML
    private void handleMembershipDetails() {
        // View membership details and renewal options
        System.out.println("Viewing Membership Details...");
    }

    @FXML
    private void handlePaymentHistory() {
        // View payment history
        System.out.println("Viewing Payment History...");
    }

    @FXML
    private void handleProfileSettings() {
        // Update profile settings
        System.out.println("Opening Profile Settings...");
    }

    @FXML
    private void handleLogout() {
        // Logout and return to login screen
        System.out.println("Logging out...");
    }
}
