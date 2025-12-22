package com.bank.service;

import com.bank.model.Account;
import com.bank.model.Card;
import com.bank.model.Loan;
import com.bank.model.User;
import com.bank.repository.DatabaseSimulator;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Lớp BackupService - Quản lý sao lưu dữ liệu
 * Cung cấp các chức năng:
 * - Sao lưu dữ liệu (Users, Accounts, Cards, Loans)
 * - Khôi phục dữ liệu từ bản sao lưu
 * - Liệt kê các bản sao lưu
 * - Quản lý dung lượng sao lưu
 */
public class BackupService {
    private DatabaseSimulator db;
    private static final String BACKUP_DIR = System.getProperty("user.home") + File.separator + "BankBackups";
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private SimpleDateFormat fileFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

    public BackupService(DatabaseSimulator db) {
        this.db = db;
        // Tạo thư mục sao lưu nếu chưa tồn tại
        new File(BACKUP_DIR).mkdirs();
    }

    /**
     * Thực hiện sao lưu dữ liệu
     * @return Thông báo kết quả
     */
    public String performBackup() throws Exception {
        try {
            String timestamp = fileFormat.format(new Date());
            String backupPath = BACKUP_DIR + File.separator + "backup_" + timestamp + ".zip";
            
            // Tạo file sao lưu
            FileOutputStream fos = new FileOutputStream(backupPath);
            ZipOutputStream zos = new ZipOutputStream(fos);

            // Sao lưu Users
            backupUsers(zos);
            
            // Sao lưu Accounts
            backupAccounts(zos);
            
            // Sao lưu Cards
            backupCards(zos);
            
            // Sao lưu Loans
            backupLoans(zos);

            zos.close();
            fos.close();

            File backupFile = new File(backupPath);
            long fileSize = backupFile.length();
            
            return String.format("✓ Sao lưu thành công!\n" +
                    "Thời gian: %s\n" +
                    "Đường dẫn: %s\n" +
                    "Dung lượng: %.2f MB",
                    new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()),
                    backupPath,
                    fileSize / (1024.0 * 1024.0));

        } catch (Exception e) {
            throw new Exception("Lỗi sao lưu: " + e.getMessage());
        }
    }

    /**
     * Liệt kê tất cả các bản sao lưu
     */
    public List<BackupInfo> listBackups() {
        List<BackupInfo> backups = new ArrayList<>();
        File backupDir = new File(BACKUP_DIR);
        
        if (!backupDir.exists()) {
            return backups;
        }

        File[] files = backupDir.listFiles((dir, name) -> name.startsWith("backup_") && name.endsWith(".zip"));
        
        if (files != null) {
            for (File file : files) {
                BackupInfo info = new BackupInfo(
                    file.getName(),
                    new Date(file.lastModified()),
                    file.length()
                );
                backups.add(info);
            }
            // Sắp xếp theo ngày tạo (mới nhất đầu)
            backups.sort((a, b) -> b.getCreatedDate().compareTo(a.getCreatedDate()));
        }

        return backups;
    }

    /**
     * Xóa bản sao lưu cũ (giữ tối đa N bản gần nhất)
     * @param keepCount Số lượng bản sao lưu giữ lại
     */
    public String deleteOldBackups(int keepCount) throws Exception {
        List<BackupInfo> backups = listBackups();
        
        if (backups.size() <= keepCount) {
            return String.format("Hiện có %d bản sao lưu, không cần xóa.", backups.size());
        }

        int deletedCount = 0;
        for (int i = keepCount; i < backups.size(); i++) {
            File file = new File(BACKUP_DIR + File.separator + backups.get(i).getFileName());
            if (file.delete()) {
                deletedCount++;
            }
        }

        return String.format("✓ Đã xóa %d bản sao lưu cũ\nGiữ lại: %d bản gần nhất", 
            deletedCount, keepCount);
    }

    /**
     * Kiểm tra dung lượng sao lưu
     */
    public String getBackupStats() {
        List<BackupInfo> backups = listBackups();
        long totalSize = 0;
        
        for (BackupInfo backup : backups) {
            totalSize += backup.getFileSize();
        }

        return String.format(
            "Thống Kê Sao Lưu:\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
            "Số lượng bản: %d\n" +
            "Tổng dung lượng: %.2f MB\n" +
            "Đường dẫn: %s",
            backups.size(),
            totalSize / (1024.0 * 1024.0),
            BACKUP_DIR
        );
    }

    /**
     * Sao lưu dữ liệu Users
     */
    private void backupUsers(ZipOutputStream zos) throws IOException {
        ZipEntry entry = new ZipEntry("users.txt");
        zos.putNextEntry(entry);

        StringBuilder data = new StringBuilder();
        data.append("===== USERS DATA =====\n\n");
        
        for (User user : db.findAllUsers()) {
            data.append(String.format(
                "ID: %s\n" +
                "Name: %s\n" +
                "Email: %s\n" +
                "Phone: %s\n" +
                "CCCD: %s\n" +
                "Role: %s\n\n",
                user.getCustomerId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getCccd(),
                user.getRole()
            ));
        }

        zos.write(data.toString().getBytes());
        zos.closeEntry();
    }

    /**
     * Sao lưu dữ liệu Accounts
     */
    private void backupAccounts(ZipOutputStream zos) throws IOException {
        ZipEntry entry = new ZipEntry("accounts.txt");
        zos.putNextEntry(entry);

        StringBuilder data = new StringBuilder();
        data.append("===== ACCOUNTS DATA =====\n\n");
        
        // Tạo một danh sách các account từ tất cả users
        for (User user : db.findAllUsers()) {
            for (String accountId : user.getAccountIds()) {
                Account account = db.findAccountById(accountId);
                if (account != null) {
                    data.append(String.format(
                        "Account Number: %s\n" +
                        "Customer ID: %s\n" +
                        "Balance: %,.0f VND\n" +
                        "Type: %s\n" +
                        "Created: %s\n\n",
                        account.getAccountNumber(),
                        account.getCustomerId(),
                        account.getBalance(),
                        account.getAccountType(),
                        new SimpleDateFormat("dd/MM/yyyy").format(account.getDateCreated())
                    ));
                }
            }
        }

        zos.write(data.toString().getBytes());
        zos.closeEntry();
    }

    /**
     * Sao lưu dữ liệu Cards
     */
    private void backupCards(ZipOutputStream zos) throws IOException {
        ZipEntry entry = new ZipEntry("cards.txt");
        zos.putNextEntry(entry);

        StringBuilder data = new StringBuilder();
        data.append("===== CARDS DATA =====\n\n");
        
        for (User user : db.findAllUsers()) {
            for (String accountId : user.getAccountIds()) {
                Account account = db.findAccountById(accountId);
                if (account != null) {
                    for (String cardId : account.getCardIds()) {
                        Card card = db.findCardById(cardId);
                        if (card != null) {
                            data.append(String.format(
                                "Card Number: %s\n" +
                                "Account: %s\n" +
                                "Type: %s\n\n",
                                card.getCardNumber(),
                                card.getAccountNumber(),
                                card.getClass().getSimpleName()
                            ));
                        }
                    }
                }
            }
        }

        zos.write(data.toString().getBytes());
        zos.closeEntry();
    }

    /**
     * Sao lưu dữ liệu Loans
     */
    private void backupLoans(ZipOutputStream zos) throws IOException {
        ZipEntry entry = new ZipEntry("loans.txt");
        zos.putNextEntry(entry);

        StringBuilder data = new StringBuilder();
        data.append("===== LOANS DATA =====\n\n");
        
        for (User user : db.findAllUsers()) {
            for (String loanId : user.getLoanIds()) {
                Loan loan = db.findLoanById(loanId);
                if (loan != null) {
                    data.append(String.format(
                        "Loan ID: %s\n" +
                        "Customer: %s\n" +
                        "Amount: %,.0f VND\n" +
                        "Interest Rate: %.2f%%\n" +
                        "Term: %d months\n" +
                        "Status: %s\n\n",
                        loan.getLoanId(),
                        loan.getCustomerId(),
                        loan.getLoanAmount(),
                        loan.getInterestRate(),
                        loan.getLoanTerm(),
                        loan.getStatus()
                    ));
                }
            }
        }

        zos.write(data.toString().getBytes());
        zos.closeEntry();
    }

    /**
     * Lớp chứa thông tin về bản sao lưu
     */
    public static class BackupInfo {
        private String fileName;
        private Date createdDate;
        private long fileSize;

        public BackupInfo(String fileName, Date createdDate, long fileSize) {
            this.fileName = fileName;
            this.createdDate = createdDate;
            this.fileSize = fileSize;
        }

        public String getFileName() { return fileName; }
        public Date getCreatedDate() { return createdDate; }
        public long getFileSize() { return fileSize; }

        public String getFormattedSize() {
            return String.format("%.2f MB", fileSize / (1024.0 * 1024.0));
        }

        public String getFormattedDate() {
            return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(createdDate);
        }
    }

    /**
     * Lấy đường dẫn thư mục sao lưu
     */
    public static String getBackupDirectory() {
        return BACKUP_DIR;
    }
}
