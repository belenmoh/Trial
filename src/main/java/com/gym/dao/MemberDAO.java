package com.gym.dao;

import com.gym.model.Member;
import java.util.Optional;

public interface MemberDAO {
    Member save(Member member);
    Optional<Member> findById(int id);
    java.util.List<Member> findByUserId(int userId);
    java.util.List<Member> findAll();
    Member update(Member member);
    boolean delete(int id);
    java.util.List<Member> findActiveMembers();
}
