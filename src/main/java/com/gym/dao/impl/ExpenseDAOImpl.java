package com.gym.dao.impl;

import com.gym.dao.ExpenseDAO;
import com.gym.model.Expense;
import com.gym.model.ExpenseCategory;
import com.gym.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExpenseDAOImpl implements ExpenseDAO {
    
    @Override
    public Expense save(Expense expense) {
        String sql = "INSERT INTO expenses (description, amount, date, category) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, expense.getDescription());
            pstmt.setDouble(2, expense.getAmount());
            pstmt.setString(3, expense.getDate().toString());
            pstmt.setString(4, expense.getCategory().name());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating expense failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    expense.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating expense failed, no ID obtained.");
                }
            }
            
            return expense;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving expense", e);
        }
    }
    
    @Override
    public Expense update(Expense expense) {
        String sql = "UPDATE expenses SET description = ?, amount = ?, date = ?, category = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, expense.getDescription());
            pstmt.setDouble(2, expense.getAmount());
            pstmt.setString(3, expense.getDate().toString());
            pstmt.setString(4, expense.getCategory().name());
            pstmt.setInt(5, expense.getId());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating expense failed, no rows affected.");
            }
            
            return expense;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating expense", e);
        }
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM expenses WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting expense", e);
        }
    }
    
    @Override
    public Optional<Expense> findById(int id) {
        String sql = "SELECT * FROM expenses WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToExpense(rs));
                }
            }
            
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding expense by ID", e);
        }
    }
    
    @Override
    public List<Expense> findByCategory(ExpenseCategory category) {
        String sql = "SELECT * FROM expenses WHERE category = ?";
        List<Expense> expenses = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, category.name());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    expenses.add(mapResultSetToExpense(rs));
                }
            }
            
            return expenses;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding expenses by category", e);
        }
    }
    
    @Override
    public List<Expense> findByDateRange(LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT * FROM expenses WHERE date BETWEEN ? AND ?";
        List<Expense> expenses = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, startDate.toString());
            pstmt.setString(2, endDate.toString());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    expenses.add(mapResultSetToExpense(rs));
                }
            }
            
            return expenses;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding expenses by date range", e);
        }
    }
    
    @Override
    public List<Expense> findByMonth(int month, int year) {
        String sql = "SELECT * FROM expenses WHERE strftime('%m', date) = ? AND strftime('%Y', date) = ?";
        List<Expense> expenses = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, String.format("%02d", month));
            pstmt.setString(2, String.valueOf(year));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    expenses.add(mapResultSetToExpense(rs));
                }
            }
            
            return expenses;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding expenses by month", e);
        }
    }
    
    @Override
    public List<Expense> findAll() {
        String sql = "SELECT * FROM expenses";
        List<Expense> expenses = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                expenses.add(mapResultSetToExpense(rs));
            }
            
            return expenses;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all expenses", e);
        }
    }
    
    private Expense mapResultSetToExpense(ResultSet rs) throws SQLException {
        Expense expense = new Expense();
        expense.setId(rs.getInt("id"));
        expense.setDescription(rs.getString("description"));
        expense.setAmount(rs.getDouble("amount"));
        expense.setDate(LocalDate.parse(rs.getString("date")));
        expense.setCategory(ExpenseCategory.valueOf(rs.getString("category")));
        return expense;
    }
}
