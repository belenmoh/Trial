package com.gym.dao.impl;

import com.gym.dao.PaymentDAO;
import com.gym.model.Payment;
import com.gym.model.PaymentType;
import com.gym.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PaymentDAOImpl implements PaymentDAO {
    
    @Override
    public Payment save(Payment payment) {
        String sql = "INSERT INTO payments (member_id, amount, date, type) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, payment.getMemberId());
            pstmt.setDouble(2, payment.getAmount());
            pstmt.setString(3, payment.getDate().toString());
            pstmt.setString(4, payment.getType().name());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating payment failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    payment.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating payment failed, no ID obtained.");
                }
            }
            
            return payment;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving payment", e);
        }
    }
    
    @Override
    public Payment update(Payment payment) {
        String sql = "UPDATE payments SET member_id = ?, amount = ?, date = ?, type = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, payment.getMemberId());
            pstmt.setDouble(2, payment.getAmount());
            pstmt.setString(3, payment.getDate().toString());
            pstmt.setString(4, payment.getType().name());
            pstmt.setInt(5, payment.getId());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating payment failed, no rows affected.");
            }
            
            return payment;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating payment", e);
        }
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM payments WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting payment", e);
        }
    }
    
    @Override
    public Optional<Payment> findById(int id) {
        String sql = "SELECT * FROM payments WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPayment(rs));
                }
            }
            
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding payment by ID", e);
        }
    }
    
    @Override
    public List<Payment> findByMemberId(int memberId) {
        String sql = "SELECT * FROM payments WHERE member_id = ?";
        List<Payment> payments = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, memberId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    payments.add(mapResultSetToPayment(rs));
                }
            }
            
            return payments;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding payments by member ID", e);
        }
    }
    
    @Override
    public List<Payment> findByType(PaymentType type) {
        String sql = "SELECT * FROM payments WHERE type = ?";
        List<Payment> payments = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, type.name());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    payments.add(mapResultSetToPayment(rs));
                }
            }
            
            return payments;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding payments by type", e);
        }
    }
    
    @Override
    public List<Payment> findByDateRange(LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT * FROM payments WHERE date BETWEEN ? AND ?";
        List<Payment> payments = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, startDate.toString());
            pstmt.setString(2, endDate.toString());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    payments.add(mapResultSetToPayment(rs));
                }
            }
            
            return payments;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding payments by date range", e);
        }
    }
    
    @Override
    public List<Payment> findByMonth(int month, int year) {
        String sql = "SELECT * FROM payments WHERE strftime('%m', date) = ? AND strftime('%Y', date) = ?";
        List<Payment> payments = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, String.format("%02d", month));
            pstmt.setString(2, String.valueOf(year));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    payments.add(mapResultSetToPayment(rs));
                }
            }
            
            return payments;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding payments by month", e);
        }
    }
    
    @Override
    public List<Payment> findAll() {
        String sql = "SELECT * FROM payments";
        List<Payment> payments = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                payments.add(mapResultSetToPayment(rs));
            }
            
            return payments;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all payments", e);
        }
    }
    
    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setId(rs.getInt("id"));
        payment.setMemberId(rs.getInt("member_id"));
        payment.setAmount(rs.getDouble("amount"));
        payment.setDate(LocalDate.parse(rs.getString("date")));
        payment.setType(PaymentType.valueOf(rs.getString("type")));
        return payment;
    }
}
