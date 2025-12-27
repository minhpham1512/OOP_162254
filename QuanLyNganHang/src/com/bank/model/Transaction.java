package com.bank.model;

import java.util.Date;

/**
 * Đại diện cho Giao dịch (GiaoDich) và Transaction Log
 * Gộp 2 khái niệm để đơn giản hóa
 */
public class Transaction {
    private String transactionId; // Mã giao dịch (PK)
    private double amount; // Số tiền
    private Date timestamp; // Thời gian
    private String content; // Nội dung
    private TransactionType type; // Loại giao dịch
    private double balanceAfter; // Số dư sau giao dịch
    
    // Liên kết với các tài khoản
    private String fromAccountId; // Tài khoản nguồn (có thể null nếu là nạp tiền)
    private String toAccountId; // Tài khoản đích (có thể null nếu là rút tiền)

    public enum TransactionType {
        DEPOSIT, // Nạp tiền
        WITHDRAWAL, // Rút tiền
        TRANSFER, // Chuyển tiền (bao gồm cả Nhận tiền)
        PAYMENT, // Thanh toán (mua sắm)
        TOP_UP // Nạp tiền điện thoại
    }

    public Transaction(String transactionId, double amount, String content, 
                       TransactionType type, String fromAccountId, String toAccountId) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.timestamp = new Date(); // Tự động lấy thời gian hiện tại
        this.content = content;
        this.type = type;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.balanceAfter = 0;
    }

    // Getters
    public String getTransactionId() { return transactionId; }
    public double getAmount() { return amount; }
    public Date getTimestamp() { return timestamp; }
    public Date getTransactionDate() { return timestamp; }
    public String getContent() { return content; }
    public TransactionType getType() { return type; }
    public String getTransactionType() { 
        switch (type) {
            case DEPOSIT:
                return "Nạp tiền";
            case WITHDRAWAL:
                return "Rút tiền";
            case TRANSFER:
                return "Chuyển tiền";
            case PAYMENT:
                return "Thanh toán";
            case TOP_UP:
                return "Nạp điện thoại";
            default:
                return type.toString();
        }
    }
    public String getFromAccountId() { return fromAccountId; }
    public String getToAccountId() { return toAccountId; }
    public double getBalanceAfter() { return balanceAfter; }

    public void setBalanceAfter(double balance) {
        this.balanceAfter = balance;
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s: %s - %.2f VND. Từ: %s, Tới: %s",
            timestamp, type, content, amount, 
            fromAccountId != null ? fromAccountId : "N/A", 
            toAccountId != null ? toAccountId : "N/A");
    }
}
