package com.gym.service;

import com.gym.dao.PaymentDAO;
import com.gym.dao.ExpenseDAO;
import com.gym.model.FinancialReport;
import com.gym.model.Payment;
import com.gym.model.Expense;
import com.gym.model.PaymentType;
import com.gym.model.ExpenseCategory;

import java.time.LocalDate;
import java.util.List;

public class FinancialService {
    private final PaymentDAO paymentDAO;
    private final ExpenseDAO expenseDAO;

    public FinancialService(PaymentDAO paymentDAO, ExpenseDAO expenseDAO) {
        this.paymentDAO = paymentDAO;
        this.expenseDAO = expenseDAO;
    }

    public FinancialReport generateMonthlyReport(int month, int year) {
        LocalDate reportDate = LocalDate.of(year, month, 1);
        
        List<Payment> monthlyPayments = paymentDAO.findByMonth(month, year);
        List<Expense> monthlyExpenses = expenseDAO.findByMonth(month, year);
        
        double totalRevenue = monthlyPayments.stream()
                .mapToDouble(Payment::getAmount)
                .sum();
        
        double totalExpenses = monthlyExpenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();
        
        return new FinancialReport(reportDate, totalRevenue, totalExpenses, monthlyPayments, monthlyExpenses);
    }

    public FinancialReport generateDateRangeReport(LocalDate startDate, LocalDate endDate) {
        List<Payment> payments = paymentDAO.findByDateRange(startDate, endDate);
        List<Expense> expenses = expenseDAO.findByDateRange(startDate, endDate);
        
        double totalRevenue = payments.stream()
                .mapToDouble(Payment::getAmount)
                .sum();
        
        double totalExpenses = expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();
        
        return new FinancialReport(LocalDate.now(), totalRevenue, totalExpenses, payments, expenses);
    }

    public FinancialReport generateAnnualReport(int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        return generateDateRangeReport(startDate, endDate);
    }

    public double calculateProfitMargin(LocalDate startDate, LocalDate endDate) {
        FinancialReport report = generateDateRangeReport(startDate, endDate);
        
        if (report.getTotalRevenue() == 0) {
            return 0.0;
        }
        
        return (report.getNetProfit() / report.getTotalRevenue()) * 100;
    }

    public double calculateYearOverYearGrowth(int currentYear, int previousYear) {
        FinancialReport currentReport = generateAnnualReport(currentYear);
        FinancialReport previousReport = generateAnnualReport(previousYear);
        
        if (previousReport.getTotalRevenue() == 0) {
            return 0.0;
        }
        
        double growth = ((currentReport.getTotalRevenue() - previousReport.getTotalRevenue()) 
                        / previousReport.getTotalRevenue()) * 100;
        
        return growth;
    }

    public double getRevenueByPaymentType(PaymentType type, LocalDate startDate, LocalDate endDate) {
        return paymentDAO.findByDateRange(startDate, endDate).stream()
                .filter(payment -> payment.getType() == type)
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    public double getExpensesByCategory(ExpenseCategory category, LocalDate startDate, LocalDate endDate) {
        return expenseDAO.findByDateRange(startDate, endDate).stream()
                .filter(expense -> expense.getCategory() == category)
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    public List<Payment> getTopPaymentsByAmount(int limit) {
        return paymentDAO.findAll().stream()
                .sorted((p1, p2) -> Double.compare(p2.getAmount(), p1.getAmount()))
                .limit(limit)
                .toList();
    }

    public List<Expense> getTopExpensesByAmount(int limit) {
        return expenseDAO.findAll().stream()
                .sorted((e1, e2) -> Double.compare(e2.getAmount(), e1.getAmount()))
                .limit(limit)
                .toList();
    }

    public double getAverageMonthlyRevenue(int year) {
        double totalAnnualRevenue = 0.0;
        
        for (int month = 1; month <= 12; month++) {
            totalAnnualRevenue += paymentDAO.findByMonth(month, year).stream()
                    .mapToDouble(Payment::getAmount)
                    .sum();
        }
        
        return totalAnnualRevenue / 12;
    }

    public double getAverageMonthlyExpenses(int year) {
        double totalAnnualExpenses = 0.0;
        
        for (int month = 1; month <= 12; month++) {
            totalAnnualExpenses += expenseDAO.findByMonth(month, year).stream()
                    .mapToDouble(Expense::getAmount)
                    .sum();
        }
        
        return totalAnnualExpenses / 12;
    }

    public boolean isProfitable(LocalDate startDate, LocalDate endDate) {
        FinancialReport report = generateDateRangeReport(startDate, endDate);
        return report.getNetProfit() > 0;
    }

    public double getRevenueGrowthRate(int months) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(months);
        
        LocalDate midDate = startDate.plusMonths(months / 2);
        
        double firstHalfRevenue = getRevenueByDateRange(startDate, midDate);
        double secondHalfRevenue = getRevenueByDateRange(midDate, endDate);
        
        if (firstHalfRevenue == 0) {
            return 0.0;
        }
        
        return ((secondHalfRevenue - firstHalfRevenue) / firstHalfRevenue) * 100;
    }

    private double getRevenueByDateRange(LocalDate startDate, LocalDate endDate) {
        return paymentDAO.findByDateRange(startDate, endDate).stream()
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    public List<LocalDate> getProfitableMonths(int year) {
        return paymentDAO.findAll().stream()
                .map(payment -> payment.getDate())
                .filter(date -> date.getYear() == year)
                .distinct()
                .sorted()
                .filter(date -> {
                    int month = date.getMonthValue();
                    FinancialReport monthlyReport = generateMonthlyReport(month, year);
                    return monthlyReport.getNetProfit() > 0;
                })
                .toList();
    }

    public double getExpenseToRevenueRatio(LocalDate startDate, LocalDate endDate) {
        FinancialReport report = generateDateRangeReport(startDate, endDate);
        
        if (report.getTotalRevenue() == 0) {
            return Double.MAX_VALUE;
        }
        
        return report.getTotalExpenses() / report.getTotalRevenue();
    }
}
