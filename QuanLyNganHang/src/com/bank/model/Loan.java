package com.bank.model;

import java.util.Date;

/**
 * Đại diện cho Khoản vay (KhoanVay)
 */
public class Loan {
    private String loanId; // Mã khoản vay (PK)
    private String customerId; // Mã khách hàng (FK)
    private String loanType; // Loại khoản vay
    private double amount; // Số tiền
    private Date startDate; // Thời gian vay (ngày bắt đầu)
    private double interestRate; // Lãi suất (%/năm)
    private int termMonths; // Kỳ hạn (tháng)
    private LoanStatus status; // Tình trạng
    private Date maturityDate; // Ngày thanh toán cuối
    
    public enum LoanStatus {
        PENDING, // Đang chờ duyệt
        ACTIVE, // Đang vay
        PAID_OFF, // Đã tất toán
        OVERDUE // Quá hạn
    }

    public Loan(String loanId, String customerId, String loanType, double amount, 
                double interestRate, int termMonths) {
        this.loanId = loanId;
        this.customerId = customerId;
        this.loanType = loanType;
        this.amount = amount;
        this.interestRate = interestRate;
        this.termMonths = termMonths;
        this.startDate = new Date();
        this.status = LoanStatus.PENDING; // Mặc định là chờ duyệt
        // Tính ngày đáo hạn (ví dụ đơn giản)
        this.maturityDate = new Date(startDate.getTime() + (long)termMonths * 30 * 24 * 60 * 60 * 1000L);
    }
    
    // Getters and Setters
    public String getLoanId() { return loanId; }
    public String getCustomerId() { return customerId; }
    public String getLoanType() { return loanType; }
    public double getAmount() { return amount; }
    public double getLoanAmount() { return amount; }
    public Date getStartDate() { return startDate; }
    public Date getLoanDate() { return startDate; }
    public double getInterestRate() { return interestRate; }
    public int getTermMonths() { return termMonths; }
    public int getLoanTerm() { return termMonths; }
    public LoanStatus getStatus() { return status; }
    public double getOutstandingAmount() { return amount; }
    public Date getMaturityDate() { return maturityDate; }
    
    public void setStatus(LoanStatus status) {
        this.status = status;
    }
    
    public void setLoanType(String loanType) { this.loanType = loanType; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }
    public void setTermMonths(int termMonths) { this.termMonths = termMonths; }
}
