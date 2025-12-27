package com.bank.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Đại diện cho thực thể Tài khoản (TaiKhoan)
 */
public class Account {
    private String accountNumber; // Số tài khoản (PK)
    private double balance; // Số dư
    private String customerId; // Mã khách hàng (FK)
    private List<String> cardIds; // Danh sách các thẻ liên kết
    private String accountType; // Loại tài khoản (Tiết kiệm, Thanh toán, etc.)
    private Date dateCreated; // Ngày tạo
    private boolean active; // Trạng thái hoạt động
    private List<Transaction> transactionHistory; // Lịch sử giao dịch

    public Account(String accountNumber, String customerId, double initialBalance) {
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.balance = initialBalance;
        this.cardIds = new ArrayList<>();
        this.accountType = "Thanh toán"; // Mặc định
        this.dateCreated = new Date();
        this.active = true;
        this.transactionHistory = new ArrayList<>();
    }

    // Getters
    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }
    public String getCustomerId() { return customerId; }
    public List<String> getCardIds() { return cardIds; }
    public String getAccountType() { return accountType; }
    public Date getDateCreated() { return dateCreated; }
    public boolean isActive() { return active; }
    public List<Transaction> getTransactionHistory() { return transactionHistory; }

    // Phương thức nghiệp vụ cơ bản
    // Logic kiểm tra sẽ nằm trong Service
    public void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
        }
    }

    public boolean withdraw(double amount) {
        if (amount > 0 && this.balance >= amount) {
            this.balance -= amount;
            return true;
        }
        return false; // Không đủ tiền
    }
    
    public void addCardId(String cardId) {
        this.cardIds.add(cardId);
    }

    public void addTransaction(Transaction transaction) {
        this.transactionHistory.add(transaction);
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}