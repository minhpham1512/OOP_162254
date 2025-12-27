package com.bank.view;

import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.model.User;
import com.bank.repository.DatabaseSimulator;
import com.bank.service.AccountService;
import java.awt.*;
import java.util.List;
import java.util.UUID;
import javax.swing.*;

public class BankNap extends JPanel {

    private DatabaseSimulator db;
    private User currentUser;

    // --- CÁC THÀNH PHẦN UI ĐÃ SỬA ---
    private JLabel accountLabel;        // Thay cho JComboBox
    private JLabel currentBalanceLabel;
    private JTextField amountField;
    private JTextField transactionRefField;
    private JTextArea depositHistoryArea;
    
    // Biến lưu ID tài khoản đang chọn (vì không còn ComboBox để getSelectedItem)
    private String currentAccountId = null; 

    public BankNap(DatabaseSimulator db, User currentUser, AccountService accountService) {
        this.db = db;
        this.currentUser = currentUser;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);

        // Khởi tạo dữ liệu
        loadAccountData();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(ThemeColors.PRIMARY);
        JLabel titleLabel = new JLabel("NẠP TIỀN VÀO TÀI KHOẢN");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        panel.add(titleLabel);
        return panel;
    }

    private JPanel createMainContent() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        panel.add(createDepositFormPanel());
        panel.add(createDepositHistoryPanel());
        return panel;
    }

    private JPanel createDepositFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ThemeColors.BG_LIGHT);
        panel.setBorder(BorderFactory.createTitledBorder("THÔNG TIN NẠP TIỀN"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5); // Tăng khoảng cách cho thoáng
        gbc.anchor = GridBagConstraints.WEST;

        // 1. Số dư hiện tại
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Số dư hiện tại:"), gbc);
        
        gbc.gridx = 1;
        currentBalanceLabel = new JLabel("0.00 VND");
        currentBalanceLabel.setFont(new Font("Arial", Font.BOLD, 14)); // Chữ to hơn chút
        currentBalanceLabel.setForeground(ThemeColors.SUCCESS);
        panel.add(currentBalanceLabel, gbc);

        // 2. Tài khoản (SỬA: Chuyển từ List sang Label tĩnh)
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Tài khoản:"), gbc);
        
        gbc.gridx = 1;
        accountLabel = new JLabel("Đang tải...");
        accountLabel.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(accountLabel, gbc);

        // 3. Phương thức (SỬA: Chuyển từ List sang Label tĩnh)
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Phương thức:"), gbc);
        
        gbc.gridx = 1;
        JLabel methodLabel = new JLabel("Nạp tiền vào tài khoản"); // Giá trị cố định
        methodLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(methodLabel, gbc);

        // 4. Số tiền nạp
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Số tiền nạp (VND):"), gbc);
        
        gbc.gridx = 1;
        amountField = new JTextField(20);
        panel.add(amountField, gbc);

        // --- ĐÃ XÓA PHẦN TÊN NGÂN HÀNG Ở ĐÂY ---

        // 5. Mã tham chiếu
        gbc.gridx = 0; gbc.gridy = 4; // Index y giảm xuống do xóa bankName
        panel.add(new JLabel("Mã tham chiếu (nếu có):"), gbc);
        
        gbc.gridx = 1;
        transactionRefField = new JTextField(20);
        panel.add(transactionRefField, gbc);

        // 6. Nút nạp tiền
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton depositButton = new JButton("Nạp tiền");
        depositButton.setBackground(ThemeColors.SUCCESS);
        depositButton.setForeground(ThemeColors.TEXT_PRIMARY);
        depositButton.setFont(new Font("Arial", Font.BOLD, 14));
        // Tăng kích thước nút
        depositButton.setPreferredSize(new Dimension(150, 35)); 
        panel.add(depositButton, gbc);

        depositButton.addActionListener(e -> processDeposit());

        return panel;
    }

    private JPanel createDepositHistoryPanel() {
        // ... (Giữ nguyên như code cũ) ...
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(ThemeColors.BG_LIGHT);
        panel.setBorder(BorderFactory.createTitledBorder("LỊCH SỬ NẠP TIỀN"));

        depositHistoryArea = new JTextArea(10, 30);
        depositHistoryArea.setEditable(false);
        depositHistoryArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        panel.add(new JScrollPane(depositHistoryArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton refreshButton = new JButton("Làm mới");
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> refreshDepositHistory());
        return panel;
    }

    private JPanel createFooterPanel() {
        // ... (Giữ nguyên hoặc rút gọn text) ...
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel infoLabel = new JLabel("Lưu ý: Tiền sẽ được cộng ngay lập tức vào tài khoản sau khi xác nhận.");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        panel.add(infoLabel);
        return panel;
    }

    /**
     * Logic mới: Tự động lấy tài khoản đầu tiên và hiển thị lên Label
     */
    private void loadAccountData() {
        try {
            List<String> accountIds = currentUser.getAccountIds();
            if (accountIds.isEmpty()) {
                accountLabel.setText("Chưa có tài khoản");
                currentBalanceLabel.setText("0.00 VND");
                currentAccountId = null;
                return;
            }

            // Lấy tài khoản đầu tiên mặc định
            currentAccountId = accountIds.get(0); 
            
            updateAccountDisplay();
            refreshDepositHistory();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage());
        }
    }

    private void updateAccountDisplay() {
        if (currentAccountId == null) return;
        
        try {
            Account account = db.findAccountById(currentAccountId);
            if (account != null) {
                // Hiển thị dạng: ACC001 (Chủ TK: Nguyen Van A)
                accountLabel.setText(String.format("%s (Chủ TK: %s)", 
                        currentAccountId, currentUser.getFullName()));
                currentBalanceLabel.setText(String.format("%.2f VND", account.getBalance()));
            }
        } catch (Exception e) {
            accountLabel.setText("Lỗi hiển thị");
        }
    }

    private void processDeposit() {
        if (currentAccountId == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy tài khoản để nạp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (amountField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số tiền!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double amount = Double.parseDouble(amountField.getText());
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Số tiền phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Logic nạp tiền
            Account account = db.findAccountById(currentAccountId);
            if (account != null) {
                double oldBalance = account.getBalance();
                account.deposit(amount);
                db.saveAccount(account);

                // Lưu lịch sử
                String txId = "DEP" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                String ref = transactionRefField.getText();
                String content = "Nạp tiền vào tài khoản" + (ref.isEmpty() ? "" : " | Ref: " + ref);
                
                Transaction tx = new Transaction(txId, amount, content, 
                        Transaction.TransactionType.DEPOSIT, currentAccountId, null);
                db.saveTransaction(tx);

                // Thông báo
                JOptionPane.showMessageDialog(this, 
                    String.format("Nạp thành công!\nSố dư mới: %.2f VND", account.getBalance()), 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);

                // Reset form
                amountField.setText("");
                transactionRefField.setText("");
                updateAccountDisplay(); // Cập nhật lại số dư trên màn hình
                refreshDepositHistory(); // Cập nhật log
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số tiền không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống: " + ex.getMessage());
        }
    }

    private void refreshDepositHistory() {
        // ... (Logic tương tự code cũ, dùng currentAccountId để query) ...
        depositHistoryArea.setText("");
        if (currentAccountId == null) return;

        try {
            var transactions = db.findTransactionsByAccountId(currentAccountId);
            depositHistoryArea.append("Mã GD   | Số tiền      | Nội dung\n");
            depositHistoryArea.append("--------+--------------+-------------------\n");
            
            for (Transaction tx : transactions) {
                if (tx.getType() == Transaction.TransactionType.DEPOSIT) {
                    depositHistoryArea.append(String.format("%-7s | %10.0f   | %s\n", 
                        tx.getTransactionId(), tx.getAmount(), tx.getContent()));
                }
            }
        } catch (Exception e) {
            depositHistoryArea.setText("Lỗi tải lịch sử.");
        }
    }
}