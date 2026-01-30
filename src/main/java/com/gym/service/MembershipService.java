package com.gym.service;

import com.gym.dao.MemberDAO;
import com.gym.dao.UserDAO;
import com.gym.model.Member;
import com.gym.model.Membership;
import com.gym.model.MonthlyMembership;
import com.gym.model.AnnualMembership;
import com.gym.model.VIPMembership;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class MembershipService {
    private final MemberDAO memberDAO;
    private final UserDAO userDAO;

    public MembershipService(MemberDAO memberDAO, UserDAO userDAO) {
        this.memberDAO = memberDAO;
        this.userDAO = userDAO;
    }

    public Member registerMember(Member member) {
        if (userDAO.existsByUsername(member.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + member.getUsername());
        }
        
        // Save user first
        userDAO.save(member);
        
        // Then save member with membership details
        if (member.getMembership() == null) {
            member.setMembership(new MonthlyMembership());
        }
        
        if (member.getStartDate() == null) {
            member.setStartDate(LocalDate.now());
        }
        
        if (member.getEndDate() == null) {
            member.setEndDate(member.getStartDate().plusMonths(member.getMembership().getDurationMonths()));
        }
        
        return memberDAO.save(member);
    }

    public Optional<Member> findMemberById(int memberId) {
        return memberDAO.findById(memberId);
    }

    public List<Member> findAllMembers() {
        return memberDAO.findAll();
    }

    public List<Member> findActiveMembers() {
        return memberDAO.findActiveMembers();
    }

    public Member updateMembership(int memberId, Membership newMembership) {
        Optional<Member> memberOpt = memberDAO.findById(memberId);
        if (memberOpt.isEmpty()) {
            throw new IllegalArgumentException("Member not found with ID: " + memberId);
        }

        Member member = memberOpt.get();
        member.setMembership(newMembership);
        member.setEndDate(LocalDate.now().plusMonths(newMembership.getDurationMonths()));
        
        return memberDAO.update(member);
    }

    public boolean renewMembership(int memberId, Membership newMembership) {
        try {
            Optional<Member> memberOpt = memberDAO.findById(memberId);
            if (memberOpt.isEmpty()) {
                return false;
            }

            Member member = memberOpt.get();
            member.setMembership(newMembership);
            member.setStartDate(LocalDate.now());
            member.setEndDate(LocalDate.now().plusMonths(newMembership.getDurationMonths()));
            
            memberDAO.update(member);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean cancelMembership(int memberId) {
        try {
            Optional<Member> memberOpt = memberDAO.findById(memberId);
            if (memberOpt.isEmpty()) {
                return false;
            }

            Member member = memberOpt.get();
            member.setEndDate(LocalDate.now().minusDays(1)); // End membership immediately
            
            memberDAO.update(member);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public double calculateMembershipPrice(Membership membership) {
        double basePrice = membership.getPrice();
        double discount = membership.calculateDiscount();
        return basePrice * (1 - discount);
    }

    public List<Member> findMembersExpiringSoon(int daysThreshold) {
        LocalDate thresholdDate = LocalDate.now().plusDays(daysThreshold);
        return memberDAO.findAll().stream()
                .filter(member -> member.getEndDate() != null)
                .filter(member -> member.getEndDate().isBefore(thresholdDate) && member.getEndDate().isAfter(LocalDate.now()))
                .toList();
    }

    public Membership createMembership(String type, double price) {
        return switch (type.toLowerCase()) {
            case "monthly" -> new MonthlyMembership(0, "Monthly Membership", price);
            case "annual" -> new AnnualMembership(0, "Annual Membership", price);
            case "vip" -> new VIPMembership(0, "VIP Membership", price);
            default -> throw new IllegalArgumentException("Invalid membership type: " + type);
        };
    }

    public boolean isMembershipActive(int memberId) {
        Optional<Member> memberOpt = memberDAO.findById(memberId);
        return memberOpt.map(Member::isMembershipActive).orElse(false);
    }

    public long getTotalActiveMembers() {
        return memberDAO.findActiveMembers().size();
    }

    public double getMembershipRevenueByType(String membershipType) {
        return memberDAO.findAll().stream()
                .filter(member -> member.getMembership().getClass().getSimpleName().equalsIgnoreCase(membershipType))
                .mapToDouble(member -> calculateMembershipPrice(member.getMembership()))
                .sum();
    }
}
