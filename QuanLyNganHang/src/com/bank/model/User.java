package com.bank.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Đại diện cho thực thể Khách hàng (KhachHang)
 * Bao gồm cả thông tin cho Admin (thông qua vai trò)
 */
public class User {
    private String customerId;
    private String fullName;
    private Date dateOfBirth;
    private String cccd; // Unique
    private String phoneNumber; // Unique
    private String email; // Unique
    private String address;
    private UserRole role; // Phân biệt Khách Hàng và Admin
    private String passwordHash; // Cần thiết để đăng nhập

    // Danh sách các ID liên kết để dễ dàng truy vấn
    private List<String> accountIds;
    private List<String> loanIds;
    private List<String> savingsAccountIds;

    // Enum cho vai trò
    public enum UserRole {
        CUSTOMER,
        ADMIN
    }

    // Constructor
    public User(String customerId, String fullName, Date dateOfBirth, String cccd, 
                String phoneNumber, String email, String address, UserRole role, String password) {
        this.customerId = customerId;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.cccd = cccd;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.role = role;
        // Trong thực tế, bạn sẽ băm mật khẩu này
        this.passwordHash = password; 
        this.accountIds = new ArrayList<>();
        this.loanIds = new ArrayList<>();
        this.savingsAccountIds = new ArrayList<>();
    }

    // Getters and Setters
    public String getCustomerId() { return customerId; }
    public String getFullName() { return fullName; }
    public Date getDateOfBirth() { return dateOfBirth; }
    public String getCccd() { return cccd; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public UserRole getRole() { return role; }
    public List<String> getAccountIds() { return accountIds; }
    public List<String> getLoanIds() { return loanIds; }
    public List<String> getSavingsAccountIds() { return savingsAccountIds; }
    public String getPassword() { return passwordHash; }

    // Setters
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setEmail(String email) { this.email = email; }
    public void setAddress(String address) { this.address = address; }
    public void setPassword(String password) { this.passwordHash = password; }
    public void setActive(boolean active) { 
        // Có thể thêm trường isActive nếu cần đánh dấu khách hàng bị xóa
    }

    // Phương thức kiểm tra mật khẩu (đơn giản hóa)
    public boolean checkPassword(String password) {
        return this.passwordHash.equals(password);
    }
    
    // Thêm các liên kết
    public void addAccountId(String accountId) {
        this.accountIds.add(accountId);
    }
    
    public void addLoanId(String loanId) {
        this.loanIds.add(loanId);
    }

    public void addSavingsAccountId(String savingsId) {
        this.savingsAccountIds.add(savingsId);
    }
}