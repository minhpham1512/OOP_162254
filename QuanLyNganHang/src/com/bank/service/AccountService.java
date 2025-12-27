package com.bank.service;

import com.bank.model.Account;
import com.bank.model.Card;
import com.bank.model.Transaction;
import com.bank.repository.DatabaseSimulator;

import java.util.List;
import java.util.UUID;

/**
 * Lớp Service (Dịch vụ)
 * Chứa logic nghiệp vụ chính cho các hoạt động liên quan đến tài khoản.
 * (Phiên bản này đã sửa tất cả các lỗi)
 */
public class AccountService {
    private DatabaseSimulator db;

    public AccountService(DatabaseSimulator db) {
        this.db = db;
    }

    /**
     * Nghiệp vụ chuyển tiền
     */
    public void transfer(String fromAccountId, String toAccountId, double amount, String content) throws Exception {
        Account fromAccount = db.findAccountById(fromAccountId);
        Account toAccount = db.findAccountById(toAccountId);

        if (fromAccount == null || toAccount == null) {
            // Thay đổi: Ném lỗi
            throw new Exception("Lỗi: Tài khoản nguồn hoặc đích không tồn tại.");
        }

        // Kiểm tra số dư (logic thẻ ghi nợ mặc định)
        if (fromAccount.getBalance() < amount) {
            // Thay đổi: Ném lỗi
            throw new Exception("Lỗi: Số dư không đủ.");
        }
        
        // Giả sử có 1 thẻ Debit mặc định được liên kết để kiểm tra
        // Trong thực tế, bạn sẽ truyền ID thẻ vào
        Card defaultCard = db.findCardsByAccountId(fromAccountId).stream()
                            .filter(c -> c instanceof com.bank.model.DebitCard)
                            .findFirst().orElse(null);
                            
        // (Lỗi của bạn có thể ở dòng "if" bên dưới, nhưng phiên bản này là đúng)
        if (defaultCard == null || !defaultCard.isTransactionValid(amount, fromAccount.getBalance())) {
             // Thay đổi: Ném lỗi
             throw new Exception("Lỗi: Thẻ không hợp lệ hoặc số dư không đủ.");
        }

        // Thực hiện giao dịch
        fromAccount.withdraw(amount);
        toAccount.deposit(amount);

        // Cập nhật lại vào "database"
        db.saveAccount(fromAccount);
        db.saveAccount(toAccount);

        // Ghi lại log giao dịch
        String txId = "TX" + UUID.randomUUID().toString().substring(0, 8);
        Transaction tx = new Transaction(txId, amount, content, 
                        Transaction.TransactionType.TRANSFER, fromAccountId, toAccountId);
        db.saveTransaction(tx);
        
        System.out.println("Chuyển tiền thành công!");
    }

    /**
     * Nghiệp vụ xem số dư
     * (Đây là phiên bản ĐÚNG, ném ra Exception)
     */
    public double getBalance(String accountId) throws Exception {
        Account account = db.findAccountById(accountId);
        if (account != null) {
            return account.getBalance();
        }
        // Thay đổi: Ném lỗi
        throw new Exception("Lỗi: Tài khoản không tồn tại.");
    }

    /**
     * Nghiệp vụ xem lịch sử giao dịch
     */
    public List<Transaction> getTransactionHistory(String accountId) {
        return db.findTransactionsByAccountId(accountId);
    }
    
    /**
     * Nghiệp vụ rút tiền
     */
    public void withdraw(String accountId, double amount, String cardId) throws Exception {
        Account account = db.findAccountById(accountId);
        Card card = db.findCardById(cardId);

        if (account == null || card == null || !card.getAccountNumber().equals(accountId)) {
            // Thay đổi: Ném lỗi
            throw new Exception("Lỗi: Tài khoản hoặc thẻ không hợp lệ.");
        }
        
        // Kiểm tra loại giao dịch thẻ có cho phép không
        if (!card.canPerformTransactionType(Transaction.TransactionType.WITHDRAWAL)) {
            // Thay đổi: Ném lỗi
            throw new Exception("Lỗi: Thẻ này không hỗ trợ rút tiền.");
        }
        
        // (Lỗi của bạn có thể ở dòng "if" bên dưới, nhưng phiên bản này là đúng)
        // Kiểm tra thẻ có hợp lệ cho số tiền này không (dựa trên loại thẻ)
        if (!card.isTransactionValid(amount, account.getBalance())) {
            // Thay đổi: Ném lỗi
            throw new Exception("Lỗi: Giao dịch không hợp lệ (Không đủ số dư/hạn mức).");
        }
        
        // Xử lý rút tiền
        boolean success = false;
        if (card instanceof com.bank.model.DebitCard) {
            success = account.withdraw(amount);
        } else if (card instanceof com.bank.model.CreditCard) {
            // Rút tiền từ thẻ tín dụng (thường mất phí và tính vào dư nợ)
            com.bank.model.CreditCard cc = (com.bank.model.CreditCard) card;
            cc.setCurrentDebt(cc.getCurrentDebt() + amount);
            db.saveCard(cc); // Cập nhật dư nợ thẻ
            success = true; // Tiền được rút ra, không ảnh hưởng tài khoản chính
        }

        if (success) {
            db.saveAccount(account); // Cập nhật tài khoản nếu là thẻ debit
            // Ghi log
            String txId = "TX" + UUID.randomUUID().toString().substring(0, 8);
            Transaction tx = new Transaction(txId, amount, "Rut tien", 
                            Transaction.TransactionType.WITHDRAWAL, accountId, null);
            db.saveTransaction(tx);
            System.out.println("Rút tiền thành công!");
        } else {
            // Thay đổi: Ném lỗi
            throw new Exception("Lỗi: Rút tiền thất bại.");
        }
    }
}
