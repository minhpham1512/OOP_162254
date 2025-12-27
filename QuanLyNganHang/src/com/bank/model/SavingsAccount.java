package com.bank.model;

import java.util.Date;

/**
 * Đại diện cho Sổ tiết kiệm (SoTietKiem)
 */
public class SavingsAccount {
    private String savingsId; // Mã sổ (PK)
    private String customerId; // Mã khách hàng (FK)
    private double amount; // Số tiền
    private int termMonths; // Kỳ hạn (tháng)
    private double interestRate; // Lãi suất (%/năm)
    private Date startDate; // Ngày gửi
    private Date maturityDate; // Ngày đáo hạn

    public SavingsAccount(String savingsId, String customerId, double amount, int termMonths, double interestRate) {
        this.savingsId = savingsId;
        this.customerId = customerId;
        this.amount = amount;
        this.termMonths = termMonths;
        this.interestRate = interestRate;
        this.startDate = new Date();
        // Tính ngày đáo hạn (ví dụ đơn giản)
        this.maturityDate = new Date(startDate.getTime() + (long)termMonths * 30 * 24 * 60 * 60 * 1000L);
    }
    
    // Getters
    public String getSavingsId() { return savingsId; }
    public String getCustomerId() { return customerId; }
    public double getAmount() { return amount; }
    public double getInterestRate() { return interestRate; }
    public Date getMaturityDate() { return maturityDate; }

    /**
     * Tính lãi suất hàng tháng (ví dụ đơn giản, lãi đơn)
     */
    public double calculateMonthlyInterest() {
        // Lãi suất năm / 12
        double monthlyRate = interestRate / 12.0;
        return amount * monthlyRate;
    }
    
    /**
     * Tính tổng số tiền nhận được khi đáo hạn (lãi đơn)
     */
    public double calculateMaturityAmount() {
        return amount + (calculateMonthlyInterest() * termMonths);
    }
}