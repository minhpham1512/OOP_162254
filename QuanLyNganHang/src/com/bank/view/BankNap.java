package com.bank.view;

import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.model.User;
import com.bank.repository.DatabaseSimulator;
import com.bank.service.AccountService;
import com.bank.service.CustomerAccountService;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.UUID;

/**
 * Lớp BankNap - Giao diện nạp tiền vào tài khoản
 * Cho phép người dùng:
 * - Xem danh sách tài khoản
 * - Chọn phương thức nạp tiền
 * - Nạp tiền vào tài khoản
 * - Xem lịch sử nạp tiền
 */
public class BankNap extends JPanel {

    private DatabaseSimulator db;
    private User currentUser;
    private AccountService accountService;
    private CustomerAccountService customerAccountService;

    // Các thành phần UI
    private JComboBox<String> accountCombo;
    private JTextField amountField;
    private JComboBox<String> paymentMethodCombo;
    private JTextField bankNameField;
    private JTextField transactionRefField;
    private JTextArea depositHistoryArea;
    private JLabel currentBalanceLabel;
    private DefaultComboBoxModel<String> accountModel;

    public BankNap(DatabaseSimulator db, User currentUser, AccountService accountService) {
        this.db = db;
        this.currentUser = currentUser;
        this.accountService = accountService;
        this.customerAccountService = new CustomerAccountService(db);

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);

        refreshAccountList();
    }

    /**
     * Tạo panel tiêu đề
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(70, 130, 180));

        JLabel titleLabel = new JLabel("NẠP TIỀN VÀO TÀI KHOẢN");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel);

        return panel;
    }

    /**
     * Tạo nội dung chính (gồm 2 phần: form nạp tiền và lịch sử)
     */
    private JPanel createMainContent() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));

        // Phần trái: Form nạp tiền
        panel.add(createDepositFormPanel());

        // Phần phải: Lịch sử nạp tiền
        panel.add(createDepositHistoryPanel());

        return panel;
    }

    /**
     * Tạo panel form nạp tiền
     */
    private JPanel createDepositFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("THÔNG TIN NẠP TIỀN"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Số dư hiện tại
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Số dư hiện tại:"), gbc);
        gbc.gridx = 1;
        currentBalanceLabel = new JLabel("0.00 VND");
        currentBalanceLabel.setFont(new Font("Arial", Font.BOLD, 12));
        currentBalanceLabel.setForeground(new Color(34, 139, 34));
        panel.add(currentBalanceLabel, gbc);

        // Chọn tài khoản
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Tài khoản:"), gbc);
        gbc.gridx = 1;
        accountModel = new DefaultComboBoxModel<>();
        accountCombo = new JComboBox<>(accountModel);
        accountCombo.addActionListener(e -> updateCurrentBalance());
        panel.add(accountCombo, gbc);

        // Phương thức nạp tiền
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Phương thức:"), gbc);
        gbc.gridx = 1;
        paymentMethodCombo = new JComboBox<>(new String[]{
            "Chuyển khoản ngân hàng",
            "Thẻ tín dụng",
            "Tiền mặt tại quầy",
            "Ví điện tử",
            "Chuyển tiền qua điện thoại"
        });
        paymentMethodCombo.addActionListener(e -> updateFormFields());
        panel.add(paymentMethodCombo, gbc);

        // Số tiền nạp
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Số tiền nạp (VND):"), gbc);
        gbc.gridx = 1;
        amountField = new JTextField(20);
        panel.add(amountField, gbc);

        // Tên ngân hàng (hiển thị tùy theo phương thức)
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Tên ngân hàng:"), gbc);
        gbc.gridx = 1;
        bankNameField = new JTextField(20);
        bankNameField.setVisible(false);
        panel.add(bankNameField, gbc);

        // Mã tham chiếu giao dịch
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Mã tham chiếu (nếu có):"), gbc);
        gbc.gridx = 1;
        transactionRefField = new JTextField(20);
        panel.add(transactionRefField, gbc);

        // Nút nạp tiền
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton depositButton = new JButton("Nạp tiền");
        depositButton.setBackground(new Color(34, 139, 34));
        depositButton.setForeground(Color.WHITE);
        depositButton.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(depositButton, gbc);

        // Xử lý sự kiện nút nạp tiền
        depositButton.addActionListener(e -> processDeposit());

        return panel;
    }

    /**
     * Tạo panel lịch sử nạp tiền
     */
    private JPanel createDepositHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("LỊCH SỬ NẠP TIỀN"));

        depositHistoryArea = new JTextArea(10, 30);
        depositHistoryArea.setEditable(false);
        depositHistoryArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
        JScrollPane scrollPane = new JScrollPane(depositHistoryArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Panel nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JButton refreshButton = new JButton("Làm mới");
        JButton exportButton = new JButton("Xuất báo cáo");
        buttonPanel.add(refreshButton);
        buttonPanel.add(exportButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Xử lý sự kiện
        refreshButton.addActionListener(e -> refreshDepositHistory());
        exportButton.addActionListener(e -> JOptionPane.showMessageDialog(this, 
            "Chức năng xuất báo cáo sẽ được cập nhật sớm!",
            "Thông báo", JOptionPane.INFORMATION_MESSAGE));

        return panel;
    }

    /**
     * Tạo panel chân trang
     */
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea infoArea = new JTextArea(3, 50);
        infoArea.setEditable(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        infoArea.setText("Thông tin: Gửi tiền nhanh chóng và an toàn. " +
                        "Tiền sẽ được cộng vào tài khoản trong vòng 2-24 giờ tùy phương thức. " +
                        "Phí nạp tiền (nếu có) sẽ được thông báo trong quá trình xử lý.");
        infoArea.setBackground(new Color(240, 240, 240));
        JScrollPane scrollPane = new JScrollPane(infoArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Cập nhật danh sách tài khoản
     */
    private void refreshAccountList() {
        accountModel.removeAllElements();
        try {
            List<String> accountIds = currentUser.getAccountIds();
            if (accountIds.isEmpty()) {
                accountModel.addElement("Chưa có tài khoản");
                currentBalanceLabel.setText("0.00 VND");
                return;
            }

            for (String accountId : accountIds) {
                try {
                    Account account = db.findAccountById(accountId);
                    if (account != null) {
                        String displayText = String.format("%s (%.2f VND)", accountId, account.getBalance());
                        accountModel.addElement(displayText);
                    }
                } catch (Exception ex) {
                    // Bỏ qua tài khoản lỗi
                }
            }

            if (accountModel.getSize() > 0) {
                accountCombo.setSelectedIndex(0);
                updateCurrentBalance();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(),
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Cập nhật số dư hiện tại
     */
    private void updateCurrentBalance() {
        try {
            String selectedText = (String) accountCombo.getSelectedItem();
            if (selectedText == null || selectedText.contains("Chưa có")) {
                currentBalanceLabel.setText("0.00 VND");
                return;
            }

            String accountId = selectedText.split(" \\(")[0];
            Account account = db.findAccountById(accountId);
            if (account != null) {
                currentBalanceLabel.setText(String.format("%.2f VND", account.getBalance()));
            }
        } catch (Exception ex) {
            currentBalanceLabel.setText("Lỗi");
        }
    }

    /**
     * Cập nhật trạng thái các trường nhập liệu tùy theo phương thức
     */
    private void updateFormFields() {
        String method = (String) paymentMethodCombo.getSelectedItem();
        bankNameField.setVisible(method.equals("Chuyển khoản ngân hàng"));
    }

    /**
     * Xử lý nạp tiền
     */
    private void processDeposit() {
        try {
            // Kiểm tra dữ liệu đầu vào
            if (amountField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số tiền!",
                                            "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double amount = Double.parseDouble(amountField.getText());
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Số tiền phải lớn hơn 0!",
                                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Kiểm tra giới hạn nạp tiền
            if (amount > 1_000_000_000) {
                JOptionPane.showMessageDialog(this, "Số tiền nạp vượt quá giới hạn cho phép!",
                                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String selectedText = (String) accountCombo.getSelectedItem();
            String accountId = selectedText.split(" \\(")[0];
            String paymentMethod = (String) paymentMethodCombo.getSelectedItem();
            String bankName = bankNameField.getText();
            String transactionRef = transactionRefField.getText();

            // Nạp tiền vào tài khoản
            Account account = db.findAccountById(accountId);
            if (account == null) {
                throw new Exception("Tài khoản không tồn tại!");
            }

            double oldBalance = account.getBalance();
            account.deposit(amount);
            db.saveAccount(account);

            // Tạo ghi chép giao dịch
            String txId = "DEP" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            String depositNote = String.format("Nạp tiền qua %s | Tham chiếu: %s | Ngân hàng: %s",
                                              paymentMethod, 
                                              transactionRef.isEmpty() ? "N/A" : transactionRef,
                                              bankName.isEmpty() ? "N/A" : bankName);
            Transaction tx = new Transaction(txId, amount, depositNote,
                                           Transaction.TransactionType.DEPOSIT, accountId, null);
            db.saveTransaction(tx);

            // Hiển thị thông báo thành công
            JOptionPane.showMessageDialog(this,
                                        String.format("Nạp tiền thành công!\n" +
                                                    "Số tiền: %.2f VND\n" +
                                                    "Số dư cũ: %.2f VND\n" +
                                                    "Số dư mới: %.2f VND\n" +
                                                    "Mã giao dịch: %s",
                                                    amount, oldBalance, account.getBalance(), txId),
                                        "Thành công", JOptionPane.INFORMATION_MESSAGE);

            // Xóa form và cập nhật
            amountField.setText("");
            bankNameField.setText("");
            transactionRefField.setText("");
            updateCurrentBalance();
            refreshDepositHistory();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số tiền không hợp lệ!",
                                        "Lỗi Dữ liệu", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(),
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Làm mới lịch sử nạp tiền
     */
    private void refreshDepositHistory() {
        depositHistoryArea.setText("");
        try {
            String selectedText = (String) accountCombo.getSelectedItem();
            if (selectedText == null || selectedText.contains("Chưa có")) {
                depositHistoryArea.append("Chưa có giao dịch nạp tiền");
                return;
            }

            String accountId = selectedText.split(" \\(")[0];
            List<Transaction> allTransactions = db.findTransactionsByAccountId(accountId);

            if (allTransactions.isEmpty()) {
                depositHistoryArea.append("Chưa có giao dịch nạp tiền");
                return;
            }

            depositHistoryArea.append("STT | Mã GD | Loại | Số tiền | Nội dung\n");
            depositHistoryArea.append("----+-------+------+----------+----------------------------\n");

            int count = 0;
            for (Transaction tx : allTransactions) {
                if (tx.getType() == Transaction.TransactionType.DEPOSIT) {
                    count++;
                    depositHistoryArea.append(String.format("%2d | %s | %s | %9.2f | %s\n",
                                                           count,
                                                           tx.getTransactionId(),
                                                           tx.getType().toString(),
                                                           tx.getAmount(),
                                                           tx.getContent()));
                }
            }

            if (count == 0) {
                depositHistoryArea.setText("Chưa có giao dịch nạp tiền nào");
            }

        } catch (Exception ex) {
            depositHistoryArea.append("Lỗi: " + ex.getMessage());
        }
    }
}
