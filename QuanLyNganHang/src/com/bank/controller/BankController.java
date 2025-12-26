package com.bank.controller;

import com.bank.model.Transaction;
import com.bank.model.User;
import com.bank.service.AccountService;
import com.bank.service.AuthService;

import java.util.List;
import java.util.Scanner;

/**
 * Lớp Controller (Bộ điều khiển)
 * Nhận yêu cầu từ người dùng (View) và gọi Service tương ứng.
 * (Tệp này không dùng cho GUI, nhưng được sửa để không báo lỗi biên dịch)
 */
public class BankController {
    private AccountService accountService;
    private AuthService authService;
    private User currentUser; // Người dùng đang đăng nhập

    public BankController(AccountService accountService, AuthService authService) {
        this.accountService = accountService;
        this.authService = authService;
    }

    /**
     * Xử lý yêu cầu đăng nhập
     */
    public void handleLogin(Scanner scanner) {
        System.out.print("Nhập email: ");
        String email = scanner.nextLine();
        System.out.print("Nhập mật khẩu: ");
        String password = scanner.nextLine();
        
        // [ĐÃ SỬA] Bọc trong try-catch
        try {
            this.currentUser = authService.login(email, password);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            this.currentUser = null;
        }
    }
    
    public void handleLogout() {
        if (this.currentUser != null) { // Thêm kiểm tra null
            System.out.println("Tạm biệt " + this.currentUser.getFullName());
            this.currentUser = null;
        }
    }

    /**
     * Xử lý yêu cầu xem số dư
     */
    public void handleViewBalance(Scanner scanner) {
        if (!isLoggedIn()) return;
        
        // Giả sử xem tài khoản đầu tiên
        String accountId = currentUser.getAccountIds().get(0);
        
        // [ĐÃ SỬA] Bọc trong try-catch
        try {
            double balance = accountService.getBalance(accountId);
            // Đây là phần "View"
            System.out.println("---------------------------------");
            System.out.println("Tài khoản: " + accountId);
            System.out.printf("Số dư hiện tại: %.2f VND\n", balance);
            System.out.println("---------------------------------");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Xử lý yêu cầu xem lịch sử giao dịch
     */
    public void handleViewTransactionHistory(Scanner scanner) {
        if (!isLoggedIn()) return;
        
        String accountId = currentUser.getAccountIds().get(0);
        List<Transaction> history = accountService.getTransactionHistory(accountId);
        
        // Đây là phần "View"
        System.out.println("--- LỊCH SỬ GIAO DỊCH ---");
        if (history.isEmpty()) {
            System.out.println("Không có giao dịch nào.");
        } else {
            for (Transaction tx : history) {
                System.out.println(tx);
            }
        }
        System.out.println("---------------------------");
    }

    /**
     * Xử lý yêu cầu chuyển tiền
     */
    public void handleTransfer(Scanner scanner) {
        if (!isLoggedIn()) return;
        
        String fromAccountId = currentUser.getAccountIds().get(0);
        
        System.out.print("Nhập số tài khoản nhận: ");
        String toAccountId = scanner.nextLine();
        System.out.print("Nhập số tiền: ");
        double amount = 0;
        try {
            amount = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.err.println("Lỗi: Số tiền không hợp lệ.");
            return;
        }
        
        System.out.print("Nhập nội dung: ");
        String content = scanner.nextLine();
        
        // [ĐÃ SỬA] Bọc trong try-catch
        try {
            accountService.transfer(fromAccountId, toAccountId, amount, content);
            System.out.println("Chuyển tiền thành công!");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
    
    // ... Thêm các hàm handle khác cho Vay, Sổ tiết kiệm, ...

    public boolean isLoggedIn() {
        if (currentUser == null) {
            System.err.println("Bạn cần đăng nhập để thực hiện chức năng này.");
            return false;
        }
        return true;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
}