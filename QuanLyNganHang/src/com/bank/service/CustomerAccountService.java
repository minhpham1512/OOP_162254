package com.bank.service;

import com.bank.model.Account;
import com.bank.model.User;
import com.bank.model.DebitCard;
import com.bank.repository.DatabaseSimulator;

import java.util.UUID;
import java.util.List;
import java.util.Date;
import java.util.Calendar;

/**
 * Lớp CustomerAccountService - Quản lý tài khoản khách hàng
 * Cung cấp các chức năng:
 * - Tạo tài khoản mới cho khách hàng
 * - Lấy danh sách tài khoản
 * - Cập nhật số dư tài khoản
 * - Xóa tài khoản
 */
public class CustomerAccountService {
    private DatabaseSimulator db;

    public CustomerAccountService(DatabaseSimulator db) {
        this.db = db;
    }

    /**
     * Tạo tài khoản mới cho khách hàng
     * @param customerId ID của khách hàng
     * @param initialBalance Số tiền ban đầu
     * @return Đối tượng Account mới được tạo
     */
    public Account createAccount(String customerId, double initialBalance) throws Exception {
        // Kiểm tra khách hàng tồn tại
        User customer = db.findUserById(customerId);
        if (customer == null) {
            throw new Exception("Khách hàng không tồn tại!");
        }

        // Kiểm tra số dư ban đầu
        if (initialBalance < 0) {
            throw new Exception("Số tiền ban đầu không được âm!");
        }

        // Tạo ID tài khoản duy nhất
        String accountId = "ACC" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Tạo đối tượng Account
        Account newAccount = new Account(accountId, customerId, initialBalance);

        // Lưu vào database
        db.saveAccount(newAccount);

        // ✅ Tự động tạo Debit Card cho tài khoản
        String cardNumber = "4111" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12).toUpperCase();
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 5); // Hết hạn sau 5 năm
        Date expiryDate = cal.getTime();
        
        DebitCard debitCard = new DebitCard(cardNumber, accountId, now, expiryDate);
        db.saveCard(debitCard);

        // Thêm ID tài khoản vào danh sách của khách hàng
        customer.addAccountId(accountId);
        db.saveUser(customer);

        return newAccount;
    }

    /**
     * Lấy danh sách tài khoản của khách hàng
     */
    public List<Account> getCustomerAccounts(String customerId) throws Exception {
        User customer = db.findUserById(customerId);
        if (customer == null) {
            throw new Exception("Khách hàng không tồn tại!");
        }

        return db.findAccountsByCustomerId(customerId);
    }

    /**
     * Lấy tài khoản theo ID
     */
    public Account getAccount(String accountId) throws Exception {
        Account account = db.findAccountById(accountId);
        if (account == null) {
            throw new Exception("Tài khoản không tồn tại!");
        }
        return account;
    }

    /**
     * Cập nhật số dư tài khoản (thêm tiền)
     */
    public void depositFunds(String accountId, double amount) throws Exception {
        Account account = db.findAccountById(accountId);
        if (account == null) {
            throw new Exception("Tài khoản không tồn tại!");
        }

        if (amount <= 0) {
            throw new Exception("Số tiền gửi phải lớn hơn 0!");
        }

        account.deposit(amount);
        db.saveAccount(account);
    }

    /**
     * Rút tiền từ tài khoản
     */
    public void withdrawFunds(String accountId, double amount) throws Exception {
        Account account = db.findAccountById(accountId);
        if (account == null) {
            throw new Exception("Tài khoản không tồn tại!");
        }

        if (amount <= 0) {
            throw new Exception("Số tiền rút phải lớn hơn 0!");
        }

        if (account.getBalance() < amount) {
            throw new Exception("Số dư không đủ để rút tiền!");
        }

        account.withdraw(amount);
        db.saveAccount(account);
    }

    /**
     * Kiểm tra số dư tài khoản
     */
    public double checkBalance(String accountId) throws Exception {
        Account account = db.findAccountById(accountId);
        if (account == null) {
            throw new Exception("Tài khoản không tồn tại!");
        }
        return account.getBalance();
    }

    /**
     * Xóa tài khoản (đánh dấu là closed)
     */
    public void closeAccount(String accountId) throws Exception {
        Account account = db.findAccountById(accountId);
        if (account == null) {
            throw new Exception("Tài khoản không tồn tại!");
        }

        if (account.getBalance() != 0) {
            throw new Exception("Phải rút hết tiền trước khi đóng tài khoản!");
        }

        // Trong thực tế, chúng ta sẽ đánh dấu là đóng thay vì xóa
        // account.setStatus("CLOSED");
        // db.saveAccount(account);
    }

    /**
     * Lấy số lượng tài khoản của khách hàng
     */
    public int getAccountCount(String customerId) throws Exception {
        User customer = db.findUserById(customerId);
        if (customer == null) {
            throw new Exception("Khách hàng không tồn tại!");
        }

        return customer.getAccountIds().size();
    }

    /**
     * Tạo tài khoản tiết kiệm
     */
    public Account createSavingsAccount(String customerId, double initialBalance, double interestRate) throws Exception {
        // Tạo tài khoản bình thường trước
        Account account = createAccount(customerId, initialBalance);
        
        // Có thể thêm logic riêng cho tài khoản tiết kiệm nếu cần
        // Ví dụ: Lưu lãi suất vào database hoặc model

        return account;
    }
}
