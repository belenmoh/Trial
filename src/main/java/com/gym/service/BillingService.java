package com.gym.service;

import com.gym.dao.PaymentDAO;
import com.gym.model.Member;
import com.gym.model.Membership;
import com.gym.model.Payment;
import com.gym.model.PaymentType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class BillingService {
    private final PaymentDAO paymentDAO;
    private final MembershipService membershipService;

    public BillingService(PaymentDAO paymentDAO, MembershipService membershipService) {
        this.paymentDAO = paymentDAO;
        this.membershipService = membershipService;
    }

    public Payment recordMembershipPayment(int memberId, double amount) {
        Optional<Member> memberOpt = membershipService.findMemberById(memberId);
        if (memberOpt.isEmpty()) {
            throw new IllegalArgumentException("Member not found");
        }

        Payment payment = new Payment();
        payment.setMemberId(memberId);
        payment.setAmount(amount);
        payment.setDate(LocalDate.now());
        payment.setType(PaymentType.MEMBERSHIP);

        return paymentDAO.save(payment);
    }

    public Payment recordClassPayment(int memberId, double amount) {
        Optional<Member> memberOpt = membershipService.findMemberById(memberId);
        if (memberOpt.isEmpty()) {
            throw new IllegalArgumentException("Member not found");
        }

        Payment payment = new Payment();
        payment.setMemberId(memberId);
        payment.setAmount(amount);
        payment.setDate(LocalDate.now());
        payment.setType(PaymentType.CLASS);

        return paymentDAO.save(payment);
    }

    public Payment recordOtherPayment(int memberId, double amount) {
        Optional<Member> memberOpt = membershipService.findMemberById(memberId);
        if (memberOpt.isEmpty()) {
            throw new IllegalArgumentException("Member not found");
        }

        Payment payment = new Payment();
        payment.setMemberId(memberId);
        payment.setAmount(amount);
        payment.setDate(LocalDate.now());
        payment.setType(PaymentType.OTHER);

        return paymentDAO.save(payment);
    }

    public Optional<Payment> findPaymentById(int id) {
        return paymentDAO.findById(id);
    }

    public List<Payment> getPaymentsByMember(int memberId) {
        return paymentDAO.findByMemberId(memberId);
    }

    public List<Payment> getPaymentsByType(PaymentType type) {
        return paymentDAO.findByType(type);
    }

    public List<Payment> getPaymentsByDateRange(LocalDate startDate, LocalDate endDate) {
        return paymentDAO.findByDateRange(startDate, endDate);
    }

    public List<Payment> getPaymentsByMonth(int month, int year) {
        return paymentDAO.findByMonth(month, year);
    }

    public double getTotalPaymentsByMember(int memberId) {
        return paymentDAO.findByMemberId(memberId).stream()
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    public double getTotalPaymentsByMemberAndType(int memberId, PaymentType type) {
        return paymentDAO.findByMemberId(memberId).stream()
                .filter(payment -> payment.getType() == type)
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    public double calculateMembershipFee(Membership membership) {
        return membershipService.calculateMembershipPrice(membership);
    }

    public boolean processMembershipRenewal(int memberId, Membership newMembership) {
        try {
            double fee = calculateMembershipFee(newMembership);
            recordMembershipPayment(memberId, fee);
            membershipService.renewMembership(memberId, newMembership);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Payment> getAllPayments() {
        return paymentDAO.findAll();
    }

    public boolean updatePayment(Payment payment) {
        try {
            paymentDAO.update(payment);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deletePayment(int paymentId) {
        try {
            return paymentDAO.delete(paymentId);
        } catch (Exception e) {
            return false;
        }
    }

    public double getTotalRevenue() {
        return paymentDAO.findAll().stream()
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    public double getRevenueByType(PaymentType type) {
        return paymentDAO.findByType(type).stream()
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    public double getRevenueByDateRange(LocalDate startDate, LocalDate endDate) {
        return paymentDAO.findByDateRange(startDate, endDate).stream()
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    public double getMonthlyRevenue(int month, int year) {
        return paymentDAO.findByMonth(month, year).stream()
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    public List<Payment> getOverduePayments() {
        // This would require adding due dates to the Payment model
        // For now, return empty list
        return List.of();
    }

    public double getAveragePaymentAmount() {
        List<Payment> allPayments = paymentDAO.findAll();
        if (allPayments.isEmpty()) {
            return 0.0;
        }
        
        double total = allPayments.stream()
                .mapToDouble(Payment::getAmount)
                .sum();
        
        return total / allPayments.size();
    }

    public long getPaymentCount() {
        return paymentDAO.findAll().size();
    }

    public List<Payment> getRecentPayments(int days) {
        LocalDate cutoffDate = LocalDate.now().minusDays(days);
        return paymentDAO.findAll().stream()
                .filter(payment -> payment.getDate().isAfter(cutoffDate))
                .sorted((p1, p2) -> p2.getDate().compareTo(p1.getDate()))
                .toList();
    }
}
