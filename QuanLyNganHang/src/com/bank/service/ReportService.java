package com.bank.service;

import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.model.User;
import com.bank.model.Loan;
import com.bank.repository.DatabaseSimulator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Lớp ReportService - Xuất báo cáo
 * Cung cấp các chức năng:
 * - Xuất báo cáo tài khoản (thông tin tài khoản, giao dịch)
 * - Xuất báo cáo khoản vay
 * - Xuất báo cáo khách hàng
 * - Xuất báo cáo tổng quát hệ thống
 */
public class ReportService {
    private DatabaseSimulator db;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private SimpleDateFormat dateFormatShort = new SimpleDateFormat("dd/MM/yyyy");

    public ReportService(DatabaseSimulator db) {
        this.db = db;
    }

    /**
     * Xuất báo cáo tài khoản chi tiết (bao gồm giao dịch)
     * @param accountId ID của tài khoản
     * @param filePath Đường dẫn file xuất
     * @return Thông báo kết quả
     */
    public String exportAccountReport(String accountId, String filePath) throws Exception {
        Account account = db.findAccountById(accountId);
        if (account == null) {
            throw new Exception("Tài khoản không tồn tại!");
        }

        User user = db.findUserById(account.getCustomerId());
        
        StringBuilder report = new StringBuilder();
        report.append("========================================\n");
        report.append("       BÁO CÁO CHI TIẾT TÀI KHOẢN\n");
        report.append("========================================\n\n");
        
        report.append("Thông tin tài khoản:\n");
        report.append("---------\n");
        report.append("Số tài khoản: ").append(account.getAccountNumber()).append("\n");
        report.append("Chủ tài khoản: ").append(user.getFullName()).append("\n");
        report.append("Loại tài khoản: ").append(account.getAccountType()).append("\n");
        report.append("Số dư hiện tại: ").append(formatCurrency(account.getBalance())).append(" VND\n");
        report.append("Ngày tạo: ").append(dateFormatShort.format(account.getDateCreated())).append("\n");
        report.append("Trạng thái: ").append(account.isActive() ? "Hoạt động" : "Bị khóa").append("\n\n");
        
        // Danh sách giao dịch
        report.append("Lịch sử giao dịch (10 giao dịch gần nhất):\n");
        report.append("---------\n");
        
        List<Transaction> transactions = account.getTransactionHistory();
        if (transactions.isEmpty()) {
            report.append("Chưa có giao dịch nào\n\n");
        } else {
            // Hiển thị 10 giao dịch gần nhất (theo thứ tự đảo ngược)
            int count = 0;
            for (int i = transactions.size() - 1; i >= 0 && count < 10; i--, count++) {
                Transaction t = transactions.get(i);
                report.append(String.format("[%s] %s: %s %s VND (Số dư: %s VND)\n",
                    dateFormat.format(t.getTransactionDate()),
                    t.getTransactionType(),
                    t.getTransactionType().equals("Rút tiền") ? "-" : "+",
                    formatCurrency(t.getAmount()),
                    formatCurrency(t.getBalanceAfter())
                ));
            }
            report.append("\n");
        }
        
        return saveReportToFile(report.toString(), filePath);
    }

    /**
     * Xuất báo cáo khoản vay
     * @param customerId ID của khách hàng
     * @param filePath Đường dẫn file xuất
     * @return Thông báo kết quả
     */
    public String exportLoanReport(String customerId, String filePath) throws Exception {
        User customer = db.findUserById(customerId);
        if (customer == null) {
            throw new Exception("Khách hàng không tồn tại!");
        }

        StringBuilder report = new StringBuilder();
        report.append("========================================\n");
        report.append("       BÁO CÁO KHOẢN VAY\n");
        report.append("========================================\n\n");
        
        report.append("Thông tin khách hàng:\n");
        report.append("---------\n");
        report.append("Mã khách hàng: ").append(customer.getCustomerId()).append("\n");
        report.append("Họ tên: ").append(customer.getFullName()).append("\n");
        report.append("Email: ").append(customer.getEmail()).append("\n");
        report.append("Số điện thoại: ").append(customer.getPhoneNumber()).append("\n\n");
        
        List<String> loanIds = customer.getLoanIds();
        
        if (loanIds.isEmpty()) {
            report.append("Khách hàng không có khoản vay nào.\n\n");
        } else {
            report.append("Danh sách các khoản vay:\n");
            report.append("---------\n");
            
            double totalAmount = 0;
            double totalOutstanding = 0;
            
            for (String loanId : loanIds) {
                Loan loan = db.findLoanById(loanId);
                if (loan != null) {
                    report.append(String.format("\nKhoản vay ID: %s\n", loan.getLoanId()));
                    report.append(String.format("  Số tiền vay: %s VND\n", formatCurrency(loan.getLoanAmount())));
                    report.append(String.format("  Lãi suất: %.2f%%\n", loan.getInterestRate()));
                    report.append(String.format("  Kỳ hạn: %d tháng\n", loan.getLoanTerm()));
                    report.append(String.format("  Ngày vay: %s\n", dateFormatShort.format(loan.getLoanDate())));
                    report.append(String.format("  Trạng thái: %s\n", loan.getStatus()));
                    report.append(String.format("  Số tiền còn nợ: %s VND\n", formatCurrency(loan.getOutstandingAmount())));
                    
                    totalAmount += loan.getLoanAmount();
                    totalOutstanding += loan.getOutstandingAmount();
                }
            }
            
            report.append("\n---------\n");
            report.append(String.format("Tổng cộng vay: %s VND\n", formatCurrency(totalAmount)));
            report.append(String.format("Tổng còn nợ: %s VND\n", formatCurrency(totalOutstanding)));
            report.append("\n");
        }
        
        return saveReportToFile(report.toString(), filePath);
    }

    /**
     * Xuất báo cáo khách hàng (thông tin cá nhân)
     * @param customerId ID của khách hàng
     * @param filePath Đường dẫn file xuất
     * @return Thông báo kết quả
     */
    public String exportCustomerReport(String customerId, String filePath) throws Exception {
        User customer = db.findUserById(customerId);
        if (customer == null) {
            throw new Exception("Khách hàng không tồn tại!");
        }

        StringBuilder report = new StringBuilder();
        report.append("========================================\n");
        report.append("       BÁO CÁO THÔNG TIN KHÁCH HÀNG\n");
        report.append("========================================\n\n");
        
        report.append("Thông tin cá nhân:\n");
        report.append("---------\n");
        report.append("Mã khách hàng: ").append(customer.getCustomerId()).append("\n");
        report.append("Họ tên: ").append(customer.getFullName()).append("\n");
        report.append("Ngày sinh: ").append(dateFormatShort.format(customer.getDateOfBirth())).append("\n");
        report.append("CCCD/CMND: ").append(customer.getCccd()).append("\n");
        report.append("Số điện thoại: ").append(customer.getPhoneNumber()).append("\n");
        report.append("Email: ").append(customer.getEmail()).append("\n");
        report.append("Địa chỉ: ").append(customer.getAddress()).append("\n");
        report.append("Loại tài khoản: ").append(customer.getRole()).append("\n\n");
        
        report.append("Tài khoản ngân hàng:\n");
        report.append("---------\n");
        List<String> accountIds = customer.getAccountIds();
        if (accountIds.isEmpty()) {
            report.append("Không có tài khoản nào\n\n");
        } else {
            double totalBalance = 0;
            for (String accountId : accountIds) {
                Account account = db.findAccountById(accountId);
                if (account != null) {
                    report.append(String.format("  • %s - Loại: %s - Số dư: %s VND\n",
                        account.getAccountNumber(),
                        account.getAccountType(),
                        formatCurrency(account.getBalance())
                    ));
                    totalBalance += account.getBalance();
                }
            }
            report.append(String.format("Tổng số dư: %s VND\n\n", formatCurrency(totalBalance)));
        }
        
        return saveReportToFile(report.toString(), filePath);
    }

    /**
     * Xuất báo cáo tổng quát hệ thống
     * @param filePath Đường dẫn file xuất
     * @return Thông báo kết quả
     */
    public String exportSystemReport(String filePath) throws Exception {
        List<User> allUsers = db.findAllUsers();
        
        StringBuilder report = new StringBuilder();
        report.append("========================================\n");
        report.append("    BÁO CÁO TỔNG QUÁT HỆ THỐNG NGÂN HÀNG\n");
        report.append("========================================\n\n");
        
        report.append("Ngày xuất báo cáo: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())).append("\n\n");
        
        int customerCount = 0;
        int adminCount = 0;
        double totalBalance = 0;
        int totalAccounts = 0;
        int totalLoans = 0;
        double totalLoanAmount = 0;
        
        for (User user : allUsers) {
            if (user.getRole() == User.UserRole.CUSTOMER) {
                customerCount++;
                totalAccounts += user.getAccountIds().size();
                totalLoans += user.getLoanIds().size();
                
                for (String accountId : user.getAccountIds()) {
                    Account account = db.findAccountById(accountId);
                    if (account != null) {
                        totalBalance += account.getBalance();
                    }
                }
                
                for (String loanId : user.getLoanIds()) {
                    Loan loan = db.findLoanById(loanId);
                    if (loan != null) {
                        totalLoanAmount += loan.getLoanAmount();
                    }
                }
            } else if (user.getRole() == User.UserRole.ADMIN) {
                adminCount++;
            }
        }
        
        report.append("Thống kê chung:\n");
        report.append("---------\n");
        report.append("Tổng số khách hàng: ").append(customerCount).append("\n");
        report.append("Tổng số quản trị viên: ").append(adminCount).append("\n");
        report.append("Tổng số tài khoản: ").append(totalAccounts).append("\n");
        report.append("Tổng số khoản vay: ").append(totalLoans).append("\n\n");
        
        report.append("Thống kê tài chính:\n");
        report.append("---------\n");
        report.append("Tổng số dư tài khoản: ").append(formatCurrency(totalBalance)).append(" VND\n");
        report.append("Tổng giá trị các khoản vay: ").append(formatCurrency(totalLoanAmount)).append(" VND\n\n");
        
        return saveReportToFile(report.toString(), filePath);
    }

    /**
     * Lưu báo cáo vào file
     * @param content Nội dung báo cáo
     * @param filePath Đường dẫn file
     * @return Thông báo kết quả
     */
    private String saveReportToFile(String content, String filePath) throws IOException {
        try {
            // Tạo thư mục nếu chưa tồn tại
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            
            // Ghi nội dung vào file với UTF-8 encoding
            try (FileWriter writer = new FileWriter(file, java.nio.charset.StandardCharsets.UTF_8)) {
                writer.write(content);
            }
            
            return "✓ Báo cáo đã được xuất thành công tới: " + filePath;
        } catch (IOException e) {
            throw new IOException("Lỗi khi xuất báo cáo: " + e.getMessage());
        }
    }

    /**
     * Định dạng tiền tệ (thêm dấu phẩy)
     * @param amount Số tiền
     * @return Chuỗi tiền tệ định dạng
     */
    private String formatCurrency(double amount) {
        return String.format("%,.0f", amount);
    }

    /**
     * Lấy đường dẫn mặc định để lưu báo cáo
     * @return Đường dẫn thư mục
     */
    public static String getDefaultReportPath() {
        return System.getProperty("user.home") + File.separator + "BankReports";
    }

    /**
     * Tạo tên file báo cáo với timestamp
     * @param reportType Loại báo cáo (Account, Loan, Customer, System)
     * @return Tên file
     */
    public static String generateReportFileName(String reportType) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return reportType + "_" + formatter.format(new Date()) + ".txt";
    }
}
