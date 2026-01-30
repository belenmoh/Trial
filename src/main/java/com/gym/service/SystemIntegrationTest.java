package com.gym.service;

import com.gym.dao.impl.UserDAOImpl;
import com.gym.dao.impl.MemberDAOImpl;
import com.gym.dao.impl.PaymentDAOImpl;
import com.gym.dao.impl.BookingDAOImpl;
import com.gym.dao.impl.ExpenseDAOImpl;
import com.gym.model.User;
import com.gym.model.Member;
import com.gym.model.Payment;
import com.gym.model.Booking;
import com.gym.model.BookingStatus;
import com.gym.model.PaymentType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Integration test class to verify the complete system functionality
 * This demonstrates how all components work together
 */
public class SystemIntegrationTest {
    
    private final MembershipService membershipService;
    private final BookingService bookingService;
    private final BillingService billingService;
    private final FinancialService financialService;
    
    public SystemIntegrationTest() {
        // Initialize all services with their dependencies
        this.membershipService = new MembershipService(new MemberDAOImpl(), new UserDAOImpl());
        this.bookingService = new BookingService(new BookingDAOImpl(), new MemberDAOImpl());
        this.billingService = new BillingService(new PaymentDAOImpl(), membershipService);
        this.financialService = new FinancialService(new PaymentDAOImpl(), new ExpenseDAOImpl());
    }
    
    public void runCompleteWorkflow() {
        System.out.println("=== Gym Management System Integration Test ===");
        
        // Test 1: Member Registration
        testMemberRegistration();
        
        // Test 2: Class Booking
        testClassBooking();
        
        // Test 3: Payment Processing
        testPaymentProcessing();
        
        // Test 4: Financial Reporting
        testFinancialReporting();
        
        System.out.println("=== Integration Test Completed Successfully ===");
    }
    
    private void testMemberRegistration() {
        System.out.println("\n1. Testing Member Registration...");
        
        try {
            // Create a new member
            Member member = new Member();
            member.setName("John Doe");
            member.setUsername("johndoe");
            member.setPassword("password123");
            
            // Register the member
            Member registeredMember = membershipService.registerMember(member);
            System.out.println("✓ Member registered successfully: " + registeredMember.getName());
            
            // Verify member exists
            Optional<Member> foundMember = membershipService.findMemberById(registeredMember.getMemberId());
            if (foundMember.isPresent()) {
                System.out.println("✓ Member verification successful");
            } else {
                System.out.println("✗ Member verification failed");
            }
            
        } catch (Exception e) {
            System.out.println("✗ Member registration failed: " + e.getMessage());
        }
    }
    
    private void testClassBooking() {
        System.out.println("\n2. Testing Class Booking...");
        
        try {
            // Find a member for booking
            List<Member> members = membershipService.findAllMembers();
            if (!members.isEmpty()) {
                Member member = members.get(0);
                
                // Book a class
                Booking booking = bookingService.bookClass(
                    member.getMemberId(), 
                    "Yoga Class", 
                    LocalDateTime.now().plusDays(1)
                );
                
                System.out.println("✓ Class booked successfully: " + booking.getClassName());
                
                // Verify booking exists
                Optional<Booking> foundBooking = bookingService.findBookingById(booking.getId());
                if (foundBooking.isPresent()) {
                    System.out.println("✓ Booking verification successful");
                } else {
                    System.out.println("✗ Booking verification failed");
                }
                
            } else {
                System.out.println("✗ No members found for booking test");
            }
            
        } catch (Exception e) {
            System.out.println("✗ Class booking failed: " + e.getMessage());
        }
    }
    
    private void testPaymentProcessing() {
        System.out.println("\n3. Testing Payment Processing...");
        
        try {
            // Find a member for payment
            List<Member> members = membershipService.findAllMembers();
            if (!members.isEmpty()) {
                Member member = members.get(0);
                
                // Process a membership payment
                Payment payment = billingService.recordMembershipPayment(
                    member.getMemberId(), 
                    50.00
                );
                
                System.out.println("✓ Payment processed successfully: $" + payment.getAmount());
                
                // Verify payment exists
                Optional<Payment> foundPayment = billingService.findPaymentById(payment.getId());
                if (foundPayment.isPresent()) {
                    System.out.println("✓ Payment verification successful");
                } else {
                    System.out.println("✗ Payment verification failed");
                }
                
            } else {
                System.out.println("✗ No members found for payment test");
            }
            
        } catch (Exception e) {
            System.out.println("✗ Payment processing failed: " + e.getMessage());
        }
    }
    
    private void testFinancialReporting() {
        System.out.println("\n4. Testing Financial Reporting...");
        
        try {
            // Generate monthly report
            var report = financialService.generateMonthlyReport(
                java.time.LocalDate.now().getMonthValue(), 
                java.time.LocalDate.now().getYear()
            );
            
            System.out.println("✓ Financial report generated successfully");
            System.out.println("  - Total Revenue: $" + report.getTotalRevenue());
            System.out.println("  - Total Expenses: $" + report.getTotalExpenses());
            System.out.println("  - Net Profit: $" + report.getNetProfit());
            
            // Test profit margin calculation
            double profitMargin = financialService.calculateProfitMargin(
                java.time.LocalDate.now().minusMonths(1), 
                java.time.LocalDate.now()
            );
            System.out.println("✓ Profit margin calculated: " + profitMargin + "%");
            
        } catch (Exception e) {
            System.out.println("✗ Financial reporting failed: " + e.getMessage());
        }
    }
    
    /**
     * Main method to run the integration test
     */
    public static void main(String[] args) {
        SystemIntegrationTest test = new SystemIntegrationTest();
        test.runCompleteWorkflow();
    }
}
