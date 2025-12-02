package com.bank.repository;

import com.bank.model.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Khung lớp giả lập cơ sở dữ liệu (DatabaseSimulator Skeleton)
 */
public class DatabaseSimulator {
    // --- KHAI BÁO CẤU TRÚC LƯU TRỮ (DATA STORE) ---
    private static final Map<String, User> users = new HashMap<>();
    private static final Map<String, Account> accounts = new HashMap<>();
    private static final Map<String, Card> cards = new HashMap<>();
    private static final Map<String, Loan> loans = new HashMap<>();
    private static final Map<String, SavingsAccount> savingsAccounts = new HashMap<>();
    private static final List<Transaction> transactions = new ArrayList<>();

    // --- User Methods ---
    public User findUserById(String customerId) {
        // TODO: Viết logic tìm user theo ID
        return null; 
    }

    public User findUserByEmail(String email) {
        // TODO: Viết logic tìm user theo Email (dùng stream filter)
        return null;
    }

    public void saveUser(User user) {
        // TODO: Viết logic lưu user vào map
    }

    public List<User> findAllUsers() {
        // TODO: Viết logic trả về danh sách tất cả user
        return new ArrayList<>();
    }

    // --- Account Methods ---
    public Account findAccountById(String accountNumber) {
        // TODO: Viết logic tìm tài khoản theo số tài khoản
        return null;
    }

    public List<Account> findAccountsByCustomerId(String customerId) {
        // TODO: Viết logic tìm các tài khoản thuộc về một khách hàng
        return new ArrayList<>();
    }

    public void saveAccount(Account account) {
        // TODO: Viết logic lưu tài khoản
    }

    // --- Card Methods ---
    public Card findCardById(String cardNumber) {
        // TODO: Viết logic tìm thẻ
        return null;
    }

    public List<Card> findCardsByAccountId(String accountId) {
        // TODO: Viết logic tìm các thẻ liên kết với tài khoản
        return new ArrayList<>();
    }

    public void saveCard(Card card) {
        // TODO: Viết logic lưu thẻ
    }
    
    // --- Transaction Methods ---
    public void saveTransaction(Transaction tx) {
        // TODO: Viết logic lưu giao dịch
    }

    public List<Transaction> findTransactionsByAccountId(String accountId) {
        // TODO: Viết logic tìm giao dịch liên quan đến tài khoản (gửi hoặc nhận)
        return new ArrayList<>();
    }
    
    // --- Loan Methods ---
    public void saveLoan(Loan loan) {
        // TODO: Viết logic lưu khoản vay
    }

    public List<Loan> findLoansByCustomerId(String customerId) {
        // TODO: Viết logic tìm khoản vay của khách hàng
        return new ArrayList<>();
    }
    
    // --- Savings Methods ---
    public void saveSavingsAccount(SavingsAccount sa) {
        // TODO: Viết logic lưu sổ tiết kiệm
    }

    public List<SavingsAccount> findSavingsByCustomerId(String customerId) {
        // TODO: Viết logic tìm sổ tiết kiệm của khách hàng
        return new ArrayList<>();
    }
}