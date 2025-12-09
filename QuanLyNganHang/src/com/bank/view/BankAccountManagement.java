package com.bank.view;

import com.bank.model.Account;
import com.bank.model.Card;
import com.bank.model.User;
import com.bank.repository.DatabaseSimulator;
import com.bank.service.AccountService;
import com.bank.service.CustomerAccountService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.UUID;

/**
 * Lớp BankAccountManagement - Giao diện quản lý tài khoản khách hàng
 * Cho phép người dùng:
 * - Xem danh sách tất cả tài khoản
 * - Xem chi tiết từng tài khoản
 * - Tạo tài khoản mới
 * - Cập nhật thông tin tài khoản
 * - Xóa/Đóng tài khoản
 * - Xem danh sách thẻ liên kết
 */
public class BankAccountManagement extends JPanel {

    private DatabaseSimulator db;
    private User currentUser;
    private CustomerAccountService customerAccountService;
    private AccountService accountService;

    // Các thành phần UI
    private JTable accountTable;
    private DefaultTableModel tableModel;
    private JLabel totalBalanceLabel;
    private JLabel accountCountLabel;
    private JTextArea accountDetailArea;
    private JComboBox<String> accountTypeCombo;
    private JTextField initialBalanceField;

    public BankAccountManagement(DatabaseSimulator db, User currentUser, 
                                 AccountService accountService) {
        this.db = db;
        this.currentUser = currentUser;
        this.accountService = accountService;
        this.customerAccountService = new CustomerAccountService(db);

        // Kiểm tra quyền - chỉ CUSTOMER mới được xem
        if (currentUser.getRole() != User.UserRole.CUSTOMER) {
            setLayout(new BorderLayout());
            JLabel errorLabel = new JLabel("❌ Bạn không có quyền truy cập chức năng này!");
            errorLabel.setFont(new Font("Arial", Font.BOLD, 16));
            errorLabel.setForeground(new Color(220, 20, 60));
            add(errorLabel, BorderLayout.CENTER);
            return;
        }

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
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(70, 130, 180));

        JLabel titleLabel = new JLabel("QUẢN LÝ TÀI KHOẢN NGÂN HÀNG");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, BorderLayout.WEST);

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 5));
        statsPanel.setBackground(new Color(70, 130, 180));

        accountCountLabel = new JLabel("Số tài khoản: 0");
        accountCountLabel.setForeground(Color.WHITE);
        accountCountLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statsPanel.add(accountCountLabel);

        totalBalanceLabel = new JLabel("Tổng số dư: 0.00 VND");
        totalBalanceLabel.setForeground(Color.WHITE);
        totalBalanceLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statsPanel.add(totalBalanceLabel);

        panel.add(statsPanel, BorderLayout.EAST);
        return panel;
    }

    /**
     * Tạo nội dung chính
     */
    private JPanel createMainContent() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));

        // Phần trên: Bảng danh sách tài khoản
        panel.add(createAccountTablePanel());

        // Phần dưới: Chi tiết và form tạo tài khoản mới
        panel.add(createDetailAndFormPanel());

        return panel;
    }

    /**
     * Tạo panel bảng danh sách tài khoản
     */
    private JPanel createAccountTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("DANH SÁCH TÀI KHOẢN"));

        // Tạo bảng
        String[] columnNames = {"STT", "Số tài khoản", "Số dư", "Loại tài khoản", "Thẻ liên kết"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép edit trực tiếp
            }
        };
        accountTable = new JTable(tableModel);
        accountTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        accountTable.setRowHeight(25);
        accountTable.getSelectionModel().addListSelectionListener(e -> showAccountDetails());

        JScrollPane scrollPane = new JScrollPane(accountTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Panel nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JButton deleteButton = new JButton("Đóng tài khoản");
        JButton refreshButton = new JButton("Làm mới");
        deleteButton.setBackground(new Color(220, 20, 60));
        deleteButton.setForeground(Color.WHITE);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Xử lý sự kiện
        deleteButton.addActionListener(e -> closeAccount());
        refreshButton.addActionListener(e -> refreshAccountList());

        return panel;
    }

    /**
     * Tạo panel chi tiết tài khoản và form tạo mới
     */
    private JPanel createDetailAndFormPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));

        // Phần trái: Chi tiết tài khoản
        panel.add(createAccountDetailPanel());

        // Phần phải: Form tạo tài khoản mới
        panel.add(createNewAccountFormPanel());

        return panel;
    }

    /**
     * Tạo panel chi tiết tài khoản
     */
    private JPanel createAccountDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("CHI TIẾT TÀI KHOẢN"));

        accountDetailArea = new JTextArea(8, 30);
        accountDetailArea.setEditable(false);
        accountDetailArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        accountDetailArea.setText("Chọn một tài khoản để xem chi tiết...");

        JScrollPane scrollPane = new JScrollPane(accountDetailArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Tạo panel form tạo tài khoản mới
     */
    private JPanel createNewAccountFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("TẠO TÀI KHOẢN MỚI"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Loại tài khoản
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Loại tài khoản:"), gbc);
        gbc.gridx = 1;
        accountTypeCombo = new JComboBox<>(new String[]{
            "Tài khoản thanh toán",
            "Tài khoản tiết kiệm",
            "Tài khoản tương lai"
        });
        panel.add(accountTypeCombo, gbc);

        // Số tiền ban đầu
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Số tiền ban đầu (VND):"), gbc);
        gbc.gridx = 1;
        initialBalanceField = new JTextField(20);
        initialBalanceField.setText("0");
        panel.add(initialBalanceField, gbc);

        // Thông tin hỗ trợ
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JLabel infoLabel = new JLabel("Lãi suất tiết kiệm: 3.5% - 6.5% tùy kỳ hạn");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        infoLabel.setForeground(new Color(100, 100, 100));
        panel.add(infoLabel, gbc);

        // Nút tạo
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton createButton = new JButton("Tạo tài khoản mới");
        createButton.setBackground(new Color(34, 139, 34));
        createButton.setForeground(Color.WHITE);
        createButton.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(createButton, gbc);

        createButton.addActionListener(e -> createNewAccount());

        return panel;
    }

    /**
     * Tạo panel chân trang
     */
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea infoArea = new JTextArea(2, 50);
        infoArea.setEditable(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        infoArea.setText("Thông tin: Bạn có thể tạo nhiều tài khoản với các loại khác nhau. " +
                        "Hãy chắc chắn rút hết tiền trước khi đóng tài khoản.");
        infoArea.setBackground(new Color(240, 240, 240));
        JScrollPane scrollPane = new JScrollPane(infoArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Làm mới danh sách tài khoản
     */
    private void refreshAccountList() {
        tableModel.setRowCount(0);
        double totalBalance = 0;

        try {
            List<String> accountIds = currentUser.getAccountIds();

            if (accountIds.isEmpty()) {
                accountCountLabel.setText("Số tài khoản: 0");
                totalBalanceLabel.setText("Tổng số dư: 0.00 VND");
                accountDetailArea.setText("Chưa có tài khoản nào. Hãy tạo tài khoản mới!");
                return;
            }

            int count = 0;
            for (String accountId : accountIds) {
                Account account = db.findAccountById(accountId);
                if (account != null) {
                    count++;
                    double balance = account.getBalance();
                    totalBalance += balance;

                    List<String> cardIds = account.getCardIds();
                    String cardInfo = cardIds.isEmpty() ? "Không" : cardIds.size() + " thẻ";

                    tableModel.addRow(new Object[]{
                        count,
                        account.getAccountNumber(),
                        String.format("%.2f", balance),
                        "Thanh toán",
                        cardInfo
                    });
                }
            }

            accountCountLabel.setText("Số tài khoản: " + count);
            totalBalanceLabel.setText(String.format("Tổng số dư: %.2f VND", totalBalance));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(),
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Hiển thị chi tiết tài khoản được chọn
     */
    private void showAccountDetails() {
        int selectedRow = accountTable.getSelectedRow();
        if (selectedRow < 0) {
            accountDetailArea.setText("Chọn một tài khoản để xem chi tiết...");
            return;
        }

        try {
            String accountNumber = (String) tableModel.getValueAt(selectedRow, 1);
            Account account = db.findAccountById(accountNumber);

            if (account != null) {
                StringBuilder details = new StringBuilder();
                details.append("=== CHI TIẾT TÀI KHOẢN ===\n\n");
                details.append("Số tài khoản: ").append(account.getAccountNumber()).append("\n");
                details.append("Chủ tài khoản: ").append(currentUser.getFullName()).append("\n");
                details.append("Số dư hiện tại: ").append(String.format("%.2f", account.getBalance())).append(" VND\n\n");

                details.append("=== THÔNG TIN THẺ ===\n");
                List<String> cardIds = account.getCardIds();
                if (cardIds.isEmpty()) {
                    details.append("Chưa có thẻ liên kết\n");
                } else {
                    for (int i = 0; i < cardIds.size(); i++) {
                        Card card = db.findCardById(cardIds.get(i));
                        if (card != null) {
                            details.append((i + 1)).append(". ").append(cardIds.get(i)).append("\n");
                        }
                    }
                }

                details.append("\n=== THAO TÁC ===\n");
                details.append("- Gửi tiền: Tab 'Nạp tiền'\n");
                details.append("- Rút tiền: Tab 'Chuyển tiền'\n");
                details.append("- Vay tiền: Tab 'Vay tiền'\n");

                accountDetailArea.setText(details.toString());
            }

        } catch (Exception ex) {
            accountDetailArea.setText("Lỗi: " + ex.getMessage());
        }
    }

    /**
     * Tạo tài khoản mới
     */
    private void createNewAccount() {
        try {
            double initialBalance = Double.parseDouble(initialBalanceField.getText());

            if (initialBalance < 0) {
                JOptionPane.showMessageDialog(this, "Số tiền ban đầu không được âm!",
                                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Tạo tài khoản mới
            Account newAccount = customerAccountService.createAccount(currentUser.getCustomerId(), initialBalance);

            JOptionPane.showMessageDialog(this,
                                        String.format("Tạo tài khoản thành công!\n" +
                                                    "Số tài khoản: %s\n" +
                                                    "Số dư: %.2f VND",
                                                    newAccount.getAccountNumber(), initialBalance),
                                        "Thành công", JOptionPane.INFORMATION_MESSAGE);

            // Xóa form
            initialBalanceField.setText("0");

            // Cập nhật danh sách
            refreshAccountList();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số tiền không hợp lệ!",
                                        "Lỗi Dữ liệu", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(),
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Đóng tài khoản
     */
    private void closeAccount() {
        int selectedRow = accountTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một tài khoản để đóng!",
                                        "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String accountNumber = (String) tableModel.getValueAt(selectedRow, 1);
            Account account = db.findAccountById(accountNumber);

            if (account == null) {
                throw new Exception("Tài khoản không tồn tại!");
            }

            // Kiểm tra số dư
            if (account.getBalance() != 0) {
                int option = JOptionPane.showConfirmDialog(this,
                                                          "Tài khoản còn " + account.getBalance() + " VND.\n" +
                                                          "Bạn có muốn rút toàn bộ tiền trước khi đóng?",
                                                          "Xác nhận", JOptionPane.YES_NO_CANCEL_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    account.withdraw(account.getBalance());
                    db.saveAccount(account);
                } else if (option == JOptionPane.CANCEL_OPTION) {
                    return;
                }
            }

            // Kiểm tra lần nữa
            if (account.getBalance() != 0) {
                JOptionPane.showMessageDialog(this, "Phải rút hết tiền trước khi đóng tài khoản!",
                                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Xóa ID tài khoản khỏi user
            currentUser.getAccountIds().remove(accountNumber);
            db.saveUser(currentUser);

            JOptionPane.showMessageDialog(this, "Đóng tài khoản thành công!",
                                        "Thành công", JOptionPane.INFORMATION_MESSAGE);

            refreshAccountList();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(),
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
