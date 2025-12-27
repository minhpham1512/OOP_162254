package com.bank.model;

import java.util.Date;

/**
 * Đại diện cho Thẻ tín dụng (Credit Card)
 */
public class CreditCard extends Card {
    private double creditLimit; // Hạn mức tín dụng
    private double currentDebt; // Dư nợ hiện tại

    public CreditCard(String cardNumber, String accountNumber, Date issueDate, Date expiryDate, double creditLimit) {
        super(cardNumber, accountNumber, issueDate, expiryDate);
        this.creditLimit = creditLimit;
        this.currentDebt = 0.0;
    }

    // Getters and Setters for credit info
    public double getCreditLimit() { return creditLimit; }
    public double getCurrentDebt() { return currentDebt; }
    public double getAvailableCredit() { return creditLimit - currentDebt; }
    
    public void setCurrentDebt(double currentDebt) {
        this.currentDebt = currentDebt;
    }

    @Override
    public boolean isTransactionValid(double amount, double accountBalance) {
        // Thẻ tín dụng kiểm tra hạn mức còn lại
        // Số dư tài khoản (accountBalance) không liên quan trực tiếp đến chi tiêu thẻ
        return getStatus() == CardStatus.ACTIVE && getAvailableCredit() >= amount;
    }
    
    @Override
    public boolean canPerformTransactionType(Transaction.TransactionType transactionType) {
        // Chỉ sử dụng cho hình thức giao dịch thanh toán dịch vụ (theo yêu cầu)
        if (getStatus() != CardStatus.ACTIVE) {
            return false;
        }
        
        switch (transactionType) {
            case PAYMENT: // Thanh toán mua sắm, nạp thẻ, v.v.
            case TOP_UP:
                return true;
            case WITHDRAWAL: // Không cho rút tiền mặt (ví dụ)
            case TRANSFER: // Không cho chuyển khoản
            default:
                return false;
        }
    }
}