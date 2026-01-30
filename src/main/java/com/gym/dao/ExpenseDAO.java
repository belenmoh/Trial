package com.gym.dao;

import com.gym.model.Expense;
import com.gym.model.ExpenseCategory;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseDAO {
    Expense save(Expense expense);
    Expense update(Expense expense);
    boolean delete(int id);
    java.util.Optional<Expense> findById(int id);
    List<Expense> findByCategory(ExpenseCategory category);
    List<Expense> findByDateRange(LocalDate startDate, LocalDate endDate);
    List<Expense> findByMonth(int month, int year);
    List<Expense> findAll();
}
