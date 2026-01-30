package com.gym.dao.impl;

import com.gym.dao.BookingDAO;
import com.gym.model.Booking;
import com.gym.model.BookingStatus;
import com.gym.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookingDAOImpl implements BookingDAO {
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public Booking save(Booking booking) {
        String sql = "INSERT INTO bookings (member_id, class_name, booking_time, class_time, status) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, booking.getMemberId());
            pstmt.setString(2, booking.getClassName());
            pstmt.setString(3, booking.getBookingTime().format(DATE_TIME_FORMATTER));
            pstmt.setString(4, booking.getClassTime().format(DATE_TIME_FORMATTER));
            pstmt.setString(5, booking.getStatus().name());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating booking failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    booking.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating booking failed, no ID obtained.");
                }
            }
            
            return booking;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving booking", e);
        }
    }
    
    @Override
    public Booking update(Booking booking) {
        String sql = "UPDATE bookings SET member_id = ?, class_name = ?, booking_time = ?, class_time = ?, status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, booking.getMemberId());
            pstmt.setString(2, booking.getClassName());
            pstmt.setString(3, booking.getBookingTime().format(DATE_TIME_FORMATTER));
            pstmt.setString(4, booking.getClassTime().format(DATE_TIME_FORMATTER));
            pstmt.setString(5, booking.getStatus().name());
            pstmt.setInt(6, booking.getId());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating booking failed, no rows affected.");
            }
            
            return booking;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating booking", e);
        }
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM bookings WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting booking", e);
        }
    }
    
    @Override
    public Optional<Booking> findById(int id) {
        String sql = "SELECT * FROM bookings WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToBooking(rs));
                }
            }
            
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding booking by ID", e);
        }
    }
    
    @Override
    public List<Booking> findByMemberId(int memberId) {
        String sql = "SELECT * FROM bookings WHERE member_id = ?";
        List<Booking> bookings = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, memberId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapResultSetToBooking(rs));
                }
            }
            
            return bookings;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding bookings by member ID", e);
        }
    }
    
    @Override
    public List<Booking> findByClassName(String className) {
        String sql = "SELECT * FROM bookings WHERE class_name = ?";
        List<Booking> bookings = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, className);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapResultSetToBooking(rs));
                }
            }
            
            return bookings;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding bookings by class name", e);
        }
    }
    
    @Override
    public List<Booking> findByStatus(BookingStatus status) {
        String sql = "SELECT * FROM bookings WHERE status = ?";
        List<Booking> bookings = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status.name());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapResultSetToBooking(rs));
                }
            }
            
            return bookings;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding bookings by status", e);
        }
    }
    
    @Override
    public List<Booking> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT * FROM bookings WHERE class_time BETWEEN ? AND ?";
        List<Booking> bookings = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, startDate.format(DATE_TIME_FORMATTER));
            pstmt.setString(2, endDate.format(DATE_TIME_FORMATTER));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapResultSetToBooking(rs));
                }
            }
            
            return bookings;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding bookings by date range", e);
        }
    }
    
    @Override
    public List<Booking> findAll() {
        String sql = "SELECT * FROM bookings";
        List<Booking> bookings = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
            
            return bookings;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all bookings", e);
        }
    }
    
    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setId(rs.getInt("id"));
        booking.setMemberId(rs.getInt("member_id"));
        booking.setClassName(rs.getString("class_name"));
        booking.setBookingTime(LocalDateTime.parse(rs.getString("booking_time"), DATE_TIME_FORMATTER));
        booking.setClassTime(LocalDateTime.parse(rs.getString("class_time"), DATE_TIME_FORMATTER));
        booking.setStatus(BookingStatus.valueOf(rs.getString("status")));
        return booking;
    }
}
