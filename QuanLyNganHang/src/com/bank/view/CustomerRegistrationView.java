package com.bank.view;

import com.bank.model.Account;
import com.bank.model.User;
import com.bank.repository.DatabaseSimulator;
import com.bank.service.CustomerAccountService;
import com.bank.service.LocationData;
import com.bank.service.UserService;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Lớp CustomerRegistrationView - Giao diện đăng ký khách hàng mới
 * Cho phép người dùng:
 * - Nhập thông tin cá nhân
 * - Chọn địa chỉ (Tỉnh → Quận → Phường)
 * - Tạo tài khoản mới và tài khoản ngân hàng
 */
public class CustomerRegistrationView extends JPanel {
    private UserService userService;
    private CustomerAccountService customerAccountService;

    // Các thành phần UI
    private JTextField fullNameField;
    private JTextField idNumberField;
    private JTextField phoneField;
    private JTextField emailField;
    private JComboBox<String> provinceCombo;
    private JComboBox<String> districtCombo;
    private JComboBox<String> wardCombo;
    private JTextField detailAddressField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField dateOfBirthField;
    private JTextArea resultArea;

    public CustomerRegistrationView(DatabaseSimulator db) {
        this.userService = new UserService(db);
        this.customerAccountService = new CustomerAccountService(db);

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createFormPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    /**
     * Tạo panel tiêu đề
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(70, 130, 180));

        JLabel titleLabel = new JLabel("ĐĂNG KÝ KHÁCH HÀNG MỚI");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel);

        return panel;
    }

    /**
     * Tạo panel form đăng ký
     */
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Họ tên
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Họ tên:"), gbc);
        gbc.gridx = 1;
        fullNameField = new JTextField(25);
        panel.add(fullNameField, gbc);

        // Ngày sinh
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Ngày sinh (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1;
        dateOfBirthField = new JTextField(25);
        dateOfBirthField.setText("01/01/2000");
        panel.add(dateOfBirthField, gbc);

        // CMND/CCCD
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("CMND/CCCD:"), gbc);
        gbc.gridx = 1;
        idNumberField = new JTextField(25);
        panel.add(idNumberField, gbc);

        // Số điện thoại
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Số điện thoại:"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(25);
        panel.add(phoneField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(25);
        panel.add(emailField, gbc);

        // Tỉnh/Thành phố
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Tỉnh/Thành phố:"), gbc);
        gbc.gridx = 1;
        provinceCombo = new JComboBox<>(LocationData.getProvinces().toArray(new String[0]));
        provinceCombo.addActionListener(e -> updateDistrictCombo());
        panel.add(provinceCombo, gbc);

        // Quận/Huyện
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("Quận/Huyện:"), gbc);
        gbc.gridx = 1;
        districtCombo = new JComboBox<>();
        districtCombo.addActionListener(e -> updateWardCombo());
        panel.add(districtCombo, gbc);

        // Phường/Xã
        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(new JLabel("Phường/Xã:"), gbc);
        gbc.gridx = 1;
        wardCombo = new JComboBox<>();
        panel.add(wardCombo, gbc);

        // Chi tiết địa chỉ
        gbc.gridx = 0;
        gbc.gridy = 8;
        panel.add(new JLabel("Chi tiết (số nhà, đường):"), gbc);
        gbc.gridx = 1;
        detailAddressField = new JTextField(25);
        panel.add(detailAddressField, gbc);

        // Mật khẩu
        gbc.gridx = 0;
        gbc.gridy = 9;
        panel.add(new JLabel("Mật khẩu:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(25);
        panel.add(passwordField, gbc);

        // Xác nhận mật khẩu
        gbc.gridx = 0;
        gbc.gridy = 10;
        panel.add(new JLabel("Xác nhận mật khẩu:"), gbc);
        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(25);
        panel.add(confirmPasswordField, gbc);

        // Nút Đăng ký
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton registerButton = new JButton("Đăng ký");
        registerButton.setBackground(new Color(34, 139, 34));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Arial", Font.BOLD, 12));
        registerButton.addActionListener(e -> registerNewCustomer());

        panel.add(registerButton, gbc);

        // Initialize district combo
        updateDistrictCombo();

        return panel;
    }

    /**
     * Cập nhật danh sách quận/huyện khi chọn tỉnh
     */
    private void updateDistrictCombo() {
        String selectedProvince = (String) provinceCombo.getSelectedItem();
        districtCombo.removeAllItems();
        if (selectedProvince != null) {
            for (String district : LocationData.getDistricts(selectedProvince)) {
                districtCombo.addItem(district);
            }
        }
        updateWardCombo();
    }

    /**
     * Cập nhật danh sách phường/xã khi chọn quận
     */
    private void updateWardCombo() {
        String selectedProvince = (String) provinceCombo.getSelectedItem();
        String selectedDistrict = (String) districtCombo.getSelectedItem();
        wardCombo.removeAllItems();
        if (selectedProvince != null && selectedDistrict != null) {
            for (String ward : LocationData.getWards(selectedProvince, selectedDistrict)) {
                wardCombo.addItem(ward);
            }
        }
    }

    /**
     * Tạo panel chân trang (kết quả)
     */
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        resultArea = new JTextArea(4, 50);
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setBackground(new Color(240, 240, 240));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Xử lý đăng ký khách hàng mới
     */
    private void registerNewCustomer() {
        try {
            // Kiểm tra và lấy dữ liệu
            String fullName = fullNameField.getText().trim();
            String idNumber = idNumberField.getText().trim().replaceAll("\\D", ""); // Chỉ lấy chữ số
            String phone = phoneField.getText().trim().replaceAll("\\D", ""); // Chỉ lấy chữ số
            String email = emailField.getText().trim();
            
            // Lấy địa chỉ từ các combobox
            String province = (String) provinceCombo.getSelectedItem();
            String district = (String) districtCombo.getSelectedItem();
            String ward = (String) wardCombo.getSelectedItem();
            String detailAddress = detailAddressField.getText().trim();
            String fullAddress = detailAddress + ", " + ward + ", " + district + ", " + province;
            
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            
            // Parse ngày sinh từ JTextField
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date dateOfBirth;
            try {
                dateOfBirth = sdf.parse(dateOfBirthField.getText().trim());
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this, 
                    "Ngày sinh không hợp lệ. Vui lòng nhập theo format dd/MM/yyyy",
                    "Lỗi Format", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validate tất cả dữ liệu
            String validationErrors = com.bank.service.ValidationService.validateRegistrationData(
                fullName, dateOfBirth, idNumber, phone, email, fullAddress, password, confirmPassword
            );

            if (!validationErrors.isEmpty()) {
                resultArea.append("✗ Lỗi xác thực:\n" + validationErrors + "\n");
                JOptionPane.showMessageDialog(this, 
                    "Vui lòng kiểm tra lại thông tin:\n\n" + validationErrors,
                    "Lỗi Xác thực", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Tạo khách hàng mới
            User newCustomer = userService.createCustomer(fullName, dateOfBirth, idNumber,
                                                          phone, email, fullAddress, password);

            resultArea.append("✓ Khách hàng được tạo thành công!\n");
            resultArea.append("  Mã khách hàng: " + newCustomer.getCustomerId() + "\n");
            resultArea.append("  Họ tên: " + newCustomer.getFullName() + "\n");
            resultArea.append("  Email: " + newCustomer.getEmail() + "\n");
            resultArea.append("  Địa chỉ: " + fullAddress + "\n\n");

            // Tạo tài khoản ngân hàng đầu tiên
            Account account = customerAccountService.createAccount(newCustomer.getCustomerId(), 0);
            
            resultArea.append("✓ Tài khoản ngân hàng được tạo thành công!\n");
            resultArea.append("  Số tài khoản: " + account.getAccountNumber() + "\n");
            resultArea.append("  Số dư: " + account.getBalance() + " VND\n\n");

            // Hiển thị thông báo thành công
            JOptionPane.showMessageDialog(this,
                                        "Đăng ký thành công!\n" +
                                        "Mã khách hàng: " + newCustomer.getCustomerId() + "\n" +
                                        "Số tài khoản: " + account.getAccountNumber() + "\n\n" +
                                        "Bạn có thể đăng nhập bằng email: " + email,
                                        "Thành công", JOptionPane.INFORMATION_MESSAGE);

            // Xóa form
            clearForm();

        } catch (Exception ex) {
            resultArea.append("✗ Lỗi: " + ex.getMessage() + "\n");
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(),
                                        "Lỗi Đăng ký", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Xóa dữ liệu form
     */
    private void clearForm() {
        fullNameField.setText("");
        idNumberField.setText("");
        phoneField.setText("");
        emailField.setText("");
        detailAddressField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        dateOfBirthField.setText("01/01/2000");
        provinceCombo.setSelectedIndex(0);
        updateDistrictCombo();
    }
}
