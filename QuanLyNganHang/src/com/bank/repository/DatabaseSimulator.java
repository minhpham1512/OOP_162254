package com.bank.repository;

import com.bank.model.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Lớp giả lập cơ sở dữ liệu (Database)
 * Sử dụng HashMap để lưu trữ dữ liệu trong bộ nhớ.
 * Đây là nơi lớp Service sẽ tương tác.
 */
public class DatabaseSimulator {
    // Sử dụng static để mô phỏng một database duy nhất
    private static final Map<String, User> users = new HashMap<>();
    private static final Map<String, Account> accounts = new HashMap<>();
    private static final Map<String, Card> cards = new HashMap<>();
    private static final Map<String, Loan> loans = new HashMap<>();
    private static final Map<String, SavingsAccount> savingsAccounts = new HashMap<>();
    private static final List<Transaction> transactions = new ArrayList<>();
    //... (Bạn có thể thêm Services, ServiceLogs...)

    // --- User Methods ---
    public User findUserById(String customerId) {
        return users.get(customerId);
    }
    public User findUserByEmail(String email) {
        return users.values().stream()
            .filter(u -> u.getEmail().equals(email))
            .findFirst().orElse(null);
    }
    public User findUserByCCCD(String cccd) {
        return users.values().stream()
            .filter(u -> u.getCccd().equals(cccd))
            .findFirst().orElse(null);
    }
    public User findUserByPhoneNumber(String phoneNumber) {
        return users.values().stream()
            .filter(u -> u.getPhoneNumber().equals(phoneNumber))
            .findFirst().orElse(null);
    }
    public void saveUser(User user) {
        users.put(user.getCustomerId(), user);
    }
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    // --- Account Methods ---
    public Account findAccountById(String accountNumber) {
        return accounts.get(accountNumber);
    }
    public List<Account> findAccountsByCustomerId(String customerId) {
        return accounts.values().stream()
            .filter(a -> a.getCustomerId().equals(customerId))
            .collect(Collectors.toList());
    }
    public void saveAccount(Account account) {
        accounts.put(account.getAccountNumber(), account);
    }

    // --- Card Methods ---
    public Card findCardById(String cardNumber) {
        return cards.get(cardNumber);
    }
    public List<Card> findCardsByAccountId(String accountId) {
        return cards.values().stream()
            .filter(c -> c.getAccountNumber().equals(accountId))
            .collect(Collectors.toList());
    }
    public void saveCard(Card card) {
        cards.put(card.getCardNumber(), card);
    }
    
    // --- Transaction Methods ---
    public void saveTransaction(Transaction tx) {
        transactions.add(tx);
    }
    public List<Transaction> findTransactionsByAccountId(String accountId) {
        return transactions.stream()
            .filter(tx -> (accountId.equals(tx.getFromAccountId()) || accountId.equals(tx.getToAccountId())))
            .collect(Collectors.toList());
    }
    
    // --- Loan Methods ---
    public void saveLoan(Loan loan) {
        loans.put(loan.getLoanId(), loan);
    }
    public Loan findLoanById(String loanId) {
        return loans.get(loanId);
    }
    public List<Loan> findLoansByCustomerId(String customerId) {
        return loans.values().stream()
            .filter(l -> l.getCustomerId().equals(customerId))
            .collect(Collectors.toList());
    }
    
    // --- Savings Methods ---
    public void saveSavingsAccount(SavingsAccount sa) {
        savingsAccounts.put(sa.getSavingsId(), sa);
    }
    public List<SavingsAccount> findSavingsByCustomerId(String customerId) {
        return savingsAccounts.values().stream()
            .filter(sa -> sa.getCustomerId().equals(customerId))
            .collect(Collectors.toList());
    }
}
