package com.bank.service;

import com.bank.model.User;
import com.bank.repository.DatabaseSimulator;

import java.util.UUID;
import java.util.Date;
import java.util.List;

/**
 * Lớp UserService - Quản lý người dùng (khách hàng)
 * Cung cấp các chức năng:
 * - Tạo khách hàng mới
 * - Tìm kiếm khách hàng
 * - Cập nhật thông tin khách hàng
 * - Xóa khách hàng
 */
public class UserService {
    private DatabaseSimulator db;

    public UserService(DatabaseSimulator db) {
        this.db = db;
    }

    /**
     * Tạo khách hàng mới
     * @param fullName Họ tên khách hàng
     * @param dateOfBirth Ngày sinh
     * @param idNumber Số CMND/CCCD
     * @param phoneNumber Số điện thoại
     * @param email Email
     * @param address Địa chỉ
     * @param password Mật khẩu
     * @return Đối tượng User mới được tạo
     */
    public User createCustomer(String fullName, Date dateOfBirth, String idNumber, 
                               String phoneNumber, String email, String address, String password) throws Exception {
        // Validate toàn bộ dữ liệu đầu vào
        String validationErrors = ValidationService.validateRegistrationData(
            fullName, dateOfBirth, idNumber, phoneNumber, email, address, password, password
        );
        
        if (!validationErrors.isEmpty()) {
            throw new Exception("Lỗi xác thực dữ liệu:\n" + validationErrors);
        }

        // Kiểm tra email đã tồn tại chưa
        if (db.findUserByEmail(email) != null) {
            throw new Exception("Email này đã được đăng ký!");
        }

        // Kiểm tra CCCD đã tồn tại chưa
        if (db.findUserByCCCD(idNumber) != null) {
            throw new Exception("CCCD/CMND này đã được đăng ký!");
        }

        // Kiểm tra số điện thoại đã tồn tại chưa
        if (db.findUserByPhoneNumber(phoneNumber) != null) {
            throw new Exception("Số điện thoại này đã được đăng ký!");
        }

        // Tạo ID khách hàng duy nhất
        String customerId = "CUS" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Tạo đối tượng User
        User newUser = new User(customerId, fullName, dateOfBirth, idNumber, 
                               phoneNumber, email, address, User.UserRole.CUSTOMER, password);

        // Lưu vào database
        db.saveUser(newUser);

        return newUser;
    }

    /**
     * Tìm khách hàng theo ID
     */
    public User findCustomerById(String customerId) throws Exception {
        User user = db.findUserById(customerId);
        if (user == null) {
            throw new Exception("Khách hàng không tồn tại!");
        }
        return user;
    }

    /**
     * Tìm khách hàng theo email
     */
    public User findCustomerByEmail(String email) throws Exception {
        User user = db.findUserByEmail(email);
        if (user == null) {
            throw new Exception("Khách hàng với email này không tồn tại!");
        }
        return user;
    }

    /**
     * Cập nhật thông tin khách hàng
     */
    public void updateCustomer(String customerId, String fullName, String phoneNumber, 
                              String email, String address) throws Exception {
        User user = db.findUserById(customerId);
        if (user == null) {
            throw new Exception("Khách hàng không tồn tại!");
        }

        // Kiểm tra email mới có bị trùng không
        if (!user.getEmail().equals(email)) {
            if (db.findUserByEmail(email) != null) {
                throw new Exception("Email này đã được sử dụng!");
            }
        }

        // Cập nhật thông tin
        user.setFullName(fullName);
        user.setPhoneNumber(phoneNumber);
        user.setEmail(email);
        user.setAddress(address);

        db.saveUser(user);
    }

    /**
     * Kiểm tra mật khẩu
     */
    public boolean verifyPassword(String customerId, String password) throws Exception {
        User user = db.findUserById(customerId);
        if (user == null) {
            throw new Exception("Khách hàng không tồn tại!");
        }
        return user.getPassword().equals(password);
    }

    /**
     * Thay đổi mật khẩu
     */
    public void changePassword(String customerId, String oldPassword, String newPassword) throws Exception {
        User user = db.findUserById(customerId);
        if (user == null) {
            throw new Exception("Khách hàng không tồn tại!");
        }

        if (!user.getPassword().equals(oldPassword)) {
            throw new Exception("Mật khẩu cũ không chính xác!");
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new Exception("Mật khẩu mới không được để trống!");
        }

        if (newPassword.length() < 3) {
            throw new Exception("Mật khẩu mới phải có ít nhất 3 ký tự!");
        }

        user.setPassword(newPassword);
        db.saveUser(user);
    }

    /**
     * Lấy danh sách tất cả khách hàng
     */
    public List<User> getAllCustomers() {
        // Lưu ý: Cách này chỉ để test, thực tế cần cài đặt getAllUsers trong DatabaseSimulator
        return db.findAllUsers();
    }

    /**
     * Xóa khách hàng (đánh dấu là inactive)
     */
    public void deleteCustomer(String customerId) throws Exception {
        User user = db.findUserById(customerId);
        if (user == null) {
            throw new Exception("Khách hàng không tồn tại!");
        }
        
        // Thay vì xóa thực sự, chúng ta chỉ đánh dấu là không hoạt động
        user.setActive(false);
        db.saveUser(user);
    }
}
