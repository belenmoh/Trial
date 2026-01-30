package com.gym.dao;

import com.gym.model.Payment;
import com.gym.model.PaymentType;
import java.time.LocalDate;
import java.util.List;

public interface PaymentDAO {
    Payment save(Payment payment);
    Payment update(Payment payment);
    boolean delete(int id);
    java.util.Optional<Payment> findById(int id);
    List<Payment> findByMemberId(int memberId);
    List<Payment> findByType(PaymentType type);
    List<Payment> findByDateRange(LocalDate startDate, LocalDate endDate);
    List<Payment> findByMonth(int month, int year);
    List<Payment> findAll();
}
