package com.bank.model;

import java.util.Date;

/**
 * Đại diện cho Thẻ ghi nợ (Debit Card)
 * (Đây là phiên bản chính xác, có 'extends Card' và constructor)
 */
public class DebitCard extends Card {

    public DebitCard(String cardNumber, String accountNumber, Date issueDate, Date expiryDate) {
        super(cardNumber, accountNumber, issueDate, expiryDate);
    }

    @Override
    public boolean isTransactionValid(double amount, double accountBalance) {
        // Thẻ ghi nợ yêu cầu số dư phải đủ
        return getStatus() == CardStatus.ACTIVE && accountBalance >= amount;
    }
    
    @Override
    public boolean canPerformTransactionType(Transaction.TransactionType transactionType) {
        // Thẻ ghi nợ có thể sử dụng cho mọi hình thức giao dịch
        return getStatus() == CardStatus.ACTIVE;
    }
}