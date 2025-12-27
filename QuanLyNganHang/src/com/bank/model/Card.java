package com.bank.model;

import java.util.Date;

/**
 * Lớp cơ sở trừu tượng cho Thẻ (The)
 * (Đây là phiên bản chính xác)
 */
public abstract class Card {
    private String cardNumber; // Số thẻ (PK)
    private String accountNumber; // Liên kết với tài khoản (FK)
    private Date expiryDate; // Ngày hết hạn
    private CardStatus status; // Trạng thái thẻ
    
    public enum CardStatus {
        ACTIVE,
        LOCKED,
        EXPIRED
    }

    public Card(String cardNumber, String accountNumber, Date issueDate, Date expiryDate) {
        this.cardNumber = cardNumber;
        this.accountNumber = accountNumber;
        this.expiryDate = expiryDate;
        this.status = CardStatus.ACTIVE;
    }

    // Getters
    public String getCardNumber() { return cardNumber; }
    public String getAccountNumber() { return accountNumber; }
    public Date getExpiryDate() { return expiryDate; }
    public CardStatus getStatus() { return status; }
    
    public void setStatus(CardStatus status) {
        this.status = status;
    }

    /**
     * Phương thức trừu tượng để kiểm tra xem một giao dịch có hợp lệ
     * dựa trên loại thẻ hay không.
     * @param amount Số tiền giao dịch
     * @param accountBalance Số dư tài khoản liên kết
     * @return true nếu hợp lệ, false nếu không
     */
    public abstract boolean isTransactionValid(double amount, double accountBalance);
    
    /**
     * Kiểm tra xem thẻ có thể dùng cho loại giao dịch này không
     * @param transactionType Loại giao dịch (ví dụ: PAYMENT, WITHDRAWAL)
     * @return true nếu được phép
     */
    public abstract boolean canPerformTransactionType(Transaction.TransactionType transactionType);
}