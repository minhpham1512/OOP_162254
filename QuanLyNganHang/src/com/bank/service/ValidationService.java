package com.bank.service;

import java.util.Date;
import java.util.Calendar;
import java.util.regex.Pattern;

/**
 * Lớp ValidationService - Xác thực dữ liệu đầu vào
 * Cung cấp các phương thức validate cho:
 * - Số điện thoại (phải 10 chữ số)
 * - CCCD (phải 12 chữ số)
 * - Tuổi (phải từ 18 tuổi trở lên)
 * - Email (định dạng chuẩn)
 * - Họ tên (không chứa số)
 */
public class ValidationService {

    /**
     * Validate số điện thoại
     * Yêu cầu: 10 chữ số
     * @param phoneNumber Số điện thoại cần kiểm tra
     * @return true nếu hợp lệ, false nếu không
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        
        // Loại bỏ khoảng trắng
        phoneNumber = phoneNumber.trim().replaceAll("\\s", "");
        
        // Kiểm tra chỉ chứa chữ số và có đúng 10 chữ số
        return phoneNumber.matches("^\\d{10}$");
    }

    /**
     * Validate CCCD/CMND
     * Yêu cầu: 12 chữ số (cho CCCD hiện tại)
     * @param idNumber CCCD cần kiểm tra
     * @return true nếu hợp lệ, false nếu không
     */
    public static boolean isValidIdNumber(String idNumber) {
        if (idNumber == null || idNumber.trim().isEmpty()) {
            return false;
        }
        
        // Loại bỏ khoảng trắng
        idNumber = idNumber.trim().replaceAll("\\s", "");
        
        // Kiểm tra chỉ chứa chữ số và có đúng 12 chữ số (hoặc 9-12 để tương thích với CMND cũ)
        return idNumber.matches("^\\d{12}$");
    }

    /**
     * Validate tuổi (phải từ 18 tuổi trở lên)
     * @param dateOfBirth Ngày sinh
     * @return true nếu từ 18 tuổi trở lên, false nếu chưa đủ 18 tuổi
     */
    public static boolean isValidAge(Date dateOfBirth) {
        if (dateOfBirth == null) {
            return false;
        }
        
        Calendar today = Calendar.getInstance();
        Calendar birthDate = Calendar.getInstance();
        birthDate.setTime(dateOfBirth);
        
        int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
        
        // Kiểm tra nếu chưa qua sinh nhật trong năm nay
        if (today.get(Calendar.MONTH) < birthDate.get(Calendar.MONTH) ||
            (today.get(Calendar.MONTH) == birthDate.get(Calendar.MONTH) && 
             today.get(Calendar.DAY_OF_MONTH) < birthDate.get(Calendar.DAY_OF_MONTH))) {
            age--;
        }
        
        return age >= 18;
    }

    /**
     * Lấy tuổi từ ngày sinh
     * @param dateOfBirth Ngày sinh
     * @return Tuổi hiện tại
     */
    public static int getAge(Date dateOfBirth) {
        if (dateOfBirth == null) {
            return -1;
        }
        
        Calendar today = Calendar.getInstance();
        Calendar birthDate = Calendar.getInstance();
        birthDate.setTime(dateOfBirth);
        
        int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
        
        if (today.get(Calendar.MONTH) < birthDate.get(Calendar.MONTH) ||
            (today.get(Calendar.MONTH) == birthDate.get(Calendar.MONTH) && 
             today.get(Calendar.DAY_OF_MONTH) < birthDate.get(Calendar.DAY_OF_MONTH))) {
            age--;
        }
        
        return age;
    }

    /**
     * Validate email
     * @param email Email cần kiểm tra
     * @return true nếu định dạng email hợp lệ, false nếu không
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.matches(emailPattern, email.trim());
    }

    /**
     * Validate họ tên (không chứa số)
     * @param fullName Họ tên cần kiểm tra
     * @return true nếu hợp lệ, false nếu không
     */
    public static boolean isValidFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return false;
        }
        
        fullName = fullName.trim();
        
        // Kiểm tra độ dài
        if (fullName.length() < 3 || fullName.length() > 100) {
            return false;
        }
        
        // Kiểm tra không chứa chữ số
        return !fullName.matches(".*\\d.*");
    }

    /**
     * Validate mật khẩu
     * @param password Mật khẩu cần kiểm tra
     * @return true nếu mật khẩu hợp lệ, false nếu không
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        
        // Mật khẩu phải có ít nhất 6 ký tự
        return password.length() >= 6;
    }

    /**
     * Validate ngày sinh (không được trong tương lai)
     * @param dateOfBirth Ngày sinh
     * @return true nếu ngày sinh hợp lệ, false nếu trong tương lai
     */
    public static boolean isValidDateOfBirth(Date dateOfBirth) {
        if (dateOfBirth == null) {
            return false;
        }
        
        Date today = new Date();
        return !dateOfBirth.after(today);
    }

    /**
     * Validate địa chỉ
     * @param address Địa chỉ cần kiểm tra
     * @return true nếu địa chỉ hợp lệ, false nếu không
     */
    public static boolean isValidAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            return false;
        }
        
        address = address.trim();
        return address.length() >= 5 && address.length() <= 200;
    }

    /**
     * Validate toàn bộ thông tin khách hàng đăng ký
     * @return Chuỗi mô tả lỗi (rỗng nếu không có lỗi)
     */
    public static String validateRegistrationData(String fullName, Date dateOfBirth, 
                                                   String idNumber, String phoneNumber, 
                                                   String email, String address, 
                                                   String password, String confirmPassword) {
        StringBuilder errors = new StringBuilder();
        
        // Kiểm tra họ tên
        if (!isValidFullName(fullName)) {
            errors.append("• Họ tên không hợp lệ (3-100 ký tự, không chứa số)\n");
        }
        
        // Kiểm tra ngày sinh
        if (!isValidDateOfBirth(dateOfBirth)) {
            errors.append("• Ngày sinh không được trong tương lai\n");
        }
        
        // Kiểm tra tuổi
        if (!isValidAge(dateOfBirth)) {
            int age = getAge(dateOfBirth);
            errors.append("• Bạn phải từ 18 tuổi trở lên (hiện tại: ").append(age).append(" tuổi)\n");
        }
        
        // Kiểm tra CCCD
        if (!isValidIdNumber(idNumber)) {
            errors.append("• CCCD/CMND phải có đúng 12 chữ số\n");
        }
        
        // Kiểm tra số điện thoại
        if (!isValidPhoneNumber(phoneNumber)) {
            errors.append("• Số điện thoại phải có đúng 10 chữ số\n");
        }
        
        // Kiểm tra email
        if (!isValidEmail(email)) {
            errors.append("• Email không hợp lệ\n");
        }
        
        // Kiểm tra địa chỉ
        if (!isValidAddress(address)) {
            errors.append("• Địa chỉ không hợp lệ (5-200 ký tự)\n");
        }
        
        // Kiểm tra mật khẩu
        if (!isValidPassword(password)) {
            errors.append("• Mật khẩu phải có ít nhất 6 ký tự\n");
        }
        
        // Kiểm tra xác nhận mật khẩu
        if (!password.equals(confirmPassword)) {
            errors.append("• Mật khẩu xác nhận không khớp\n");
        }
        
        return errors.toString();
    }
}
