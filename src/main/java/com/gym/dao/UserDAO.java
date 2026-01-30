package com.gym.dao;

import com.gym.model.User;
import java.util.Optional;

public interface UserDAO {
    User save(User user);
    Optional<User> findById(int id);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    User update(User user);
    boolean delete(int id);
    java.util.List<User> findAll();
}
