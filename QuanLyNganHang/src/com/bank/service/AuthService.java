package com.bank.service;

import com.bank.model.User;
import com.bank.repository.DatabaseSimulator;
import java.util.Date;
import java.util.UUID;

/**
 * Service quản lý Xác thực (Đăng nhập, Đăng ký)
 */
public class AuthService {
    private DatabaseSimulator db;

    public AuthService(DatabaseSimulator db) {
        this.db = db;
    }

    public User login(String email, String password) throws Exception {
        User user = db.findUserByEmail(email);
        if (user != null && user.checkPassword(password)) {
            System.out.println("Đăng nhập thành công! Xin chào " + user.getFullName());
            return user;
        }
        // Thay đổi: Ném lỗi để GUI bắt
        throw new Exception("Lỗi: Email hoặc mật khẩu không đúng.");
    }

    public User register(String fullName, Date dob, String cccd, String phone, String email, String address, String password) throws Exception {
        // Kiểm tra unique (đơn giản hóa)
        if (db.findUserByEmail(email) != null) {
            // Thay đổi: Ném lỗi để GUI bắt
            throw new Exception("Lỗi: Email đã tồn tại.");
        }
        
        String customerId = "CUS" + UUID.randomUUID().toString().substring(0, 8);
        User newUser = new User(customerId, fullName, dob, cccd, phone, email, address, User.UserRole.CUSTOMER, password);
        
        db.saveUser(newUser);
        System.out.println("Đăng ký thành công! Mã khách hàng: " + customerId);
        return newUser;
    }
}