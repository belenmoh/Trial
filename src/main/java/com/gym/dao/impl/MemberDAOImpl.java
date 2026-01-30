package com.gym.dao.impl;

import com.gym.dao.MemberDAO;
import com.gym.model.Member;
import com.gym.model.Membership;
import com.gym.model.MonthlyMembership;
import com.gym.model.AnnualMembership;
import com.gym.model.VIPMembership;
import com.gym.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MemberDAOImpl implements MemberDAO {
    
    @Override
    public Member save(Member member) {
        String sql = "INSERT INTO members (user_id, membership_type, start_date, end_date) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, member.getId());
            pstmt.setString(2, member.getMembership().getClass().getSimpleName());
            pstmt.setString(3, member.getStartDate() != null ? member.getStartDate().toString() : null);
            pstmt.setString(4, member.getEndDate() != null ? member.getEndDate().toString() : null);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating member failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    member.setMemberId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating member failed, no ID obtained.");
                }
            }
            
            return member;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving member", e);
        }
    }
    
    @Override
    public Optional<Member> findById(int id) {
        String sql = """
            SELECT m.*, u.name, u.username, u.password, u.role 
            FROM members m 
            JOIN users u ON m.user_id = u.id 
            WHERE m.id = ?
        """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMember(rs));
                }
            }
            
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding member by ID", e);
        }
    }
    
    @Override
    public List<Member> findByUserId(int userId) {
        String sql = """
            SELECT m.*, u.name, u.username, u.password, u.role 
            FROM members m 
            JOIN users u ON m.user_id = u.id 
            WHERE m.user_id = ?
        """;
        List<Member> members = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    members.add(mapResultSetToMember(rs));
                }
            }
            
            return members;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding members by user ID", e);
        }
    }
    
    @Override
    public List<Member> findAll() {
        String sql = """
            SELECT m.*, u.name, u.username, u.password, u.role 
            FROM members m 
            JOIN users u ON m.user_id = u.id
        """;
        List<Member> members = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                members.add(mapResultSetToMember(rs));
            }
            
            return members;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all members", e);
        }
    }
    
    @Override
    public Member update(Member member) {
        String sql = "UPDATE members SET membership_type = ?, start_date = ?, end_date = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, member.getMembership().getClass().getSimpleName());
            pstmt.setString(2, member.getStartDate() != null ? member.getStartDate().toString() : null);
            pstmt.setString(3, member.getEndDate() != null ? member.getEndDate().toString() : null);
            pstmt.setInt(4, member.getMemberId());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating member failed, no rows affected.");
            }
            
            return member;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating member", e);
        }
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM members WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting member", e);
        }
    }
    
    @Override
    public List<Member> findActiveMembers() {
        String sql = """
            SELECT m.*, u.name, u.username, u.password, u.role 
            FROM members m 
            JOIN users u ON m.user_id = u.id 
            WHERE m.end_date > date('now')
        """;
        List<Member> members = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                members.add(mapResultSetToMember(rs));
            }
            
            return members;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding active members", e);
        }
    }
    
    private Member mapResultSetToMember(ResultSet rs) throws SQLException {
        Member member = new Member();
        member.setMemberId(rs.getInt("id"));
        member.setId(rs.getInt("user_id"));
        member.setName(rs.getString("name"));
        member.setUsername(rs.getString("username"));
        member.setPassword(rs.getString("password"));
        
        // Map membership type
        String membershipType = rs.getString("membership_type");
        Membership membership = switch (membershipType) {
            case "MonthlyMembership" -> new MonthlyMembership();
            case "AnnualMembership" -> new AnnualMembership();
            case "VIPMembership" -> new VIPMembership();
            default -> new MonthlyMembership(); // Default
        };
        member.setMembership(membership);
        
        // Map dates
        String startDateStr = rs.getString("start_date");
        if (startDateStr != null && !startDateStr.isEmpty()) {
            member.setStartDate(LocalDate.parse(startDateStr));
        }
        
        String endDateStr = rs.getString("end_date");
        if (endDateStr != null && !endDateStr.isEmpty()) {
            member.setEndDate(LocalDate.parse(endDateStr));
        }
        
        return member;
    }
}
