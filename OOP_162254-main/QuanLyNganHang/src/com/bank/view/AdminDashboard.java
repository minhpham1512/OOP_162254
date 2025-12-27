package com.bank.view;

import com.bank.model.Account;
import com.bank.model.User;
import com.bank.repository.DatabaseSimulator;
import com.bank.service.AccountService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Lớp AdminDashboard - Giao diện quản trị viên (Admin)
 * Chỉ Admin mới có thể xem được:
 * - Danh sách tất cả khách hàng
 * - Danh sách tất cả tài khoản trong hệ thống
 * - Danh sách giao dịch
 * - Thống kê hệ thống
 * - Quản lý người dùng
 */
public class AdminDashboard extends JPanel {

    private DatabaseSimulator db;

    // Các thành phần UI
    private JTable customerTable;
    private JTable accountTable;
    private JTable transactionTable;
    private DefaultTableModel customerTableModel;
    private DefaultTableModel accountTableModel;
    private DefaultTableModel transactionTableModel;
    private JLabel totalCustomersLabel;
    private JLabel totalAccountsLabel;
    private JLabel totalBalanceLabel;

    public AdminDashboard(DatabaseSimulator db, User currentAdmin, AccountService accountService) {
        this.db = db;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);

        refreshAllData();
    }

    /**
     * Tạo panel tiêu đề
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeColors.DANGER);

        JLabel titleLabel = new JLabel("🔐 BẢNG ĐIỀU KHIỂN QUẢN TRỊ VIÊN");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        panel.add(titleLabel, BorderLayout.WEST);

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 5));
        statsPanel.setBackground(ThemeColors.DANGER);

        totalCustomersLabel = new JLabel("Khách hàng: 0");
        totalCustomersLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        totalCustomersLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statsPanel.add(totalCustomersLabel);

        totalAccountsLabel = new JLabel("Tài khoản: 0");
        totalAccountsLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        totalAccountsLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statsPanel.add(totalAccountsLabel);

        totalBalanceLabel = new JLabel("Tổng số dư: 0.00 VND");
        totalBalanceLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        totalBalanceLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statsPanel.add(totalBalanceLabel);

        panel.add(statsPanel, BorderLayout.EAST);
        return panel;
    }

    /**
     * Tạo nội dung chính với các tab
     */
    private JPanel createMainContent() {
        JPanel panel = new JPanel(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Khách hàng", createCustomerTablePanel());
        tabbedPane.addTab("Tài khoản", createAccountTablePanel());
        tabbedPane.addTab("Giao dịch", createTransactionTablePanel());

        panel.add(tabbedPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Tạo panel bảng khách hàng
     */
    private JPanel createCustomerTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("DANH SÁCH KHÁCH HÀNG"));

        String[] columnNames = {"Mã KH", "Tên", "Email", "SĐT", "Số TK", "Tổng số dư", "Vai trò"};
        customerTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        customerTable = new JTable(customerTableModel);
        customerTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(customerTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JButton refreshButton = new JButton("Làm mới");
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> refreshCustomerTable());

        return panel;
    }

    /**
     * Tạo panel bảng tài khoản
     */
    private JPanel createAccountTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("DANH SÁCH TÀI KHOẢN"));

        String[] columnNames = {"Số TK", "Chủ tài khoản", "Số dư", "Số thẻ", "Ngày tạo"};
        accountTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        accountTable = new JTable(accountTableModel);
        accountTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(accountTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JButton refreshButton = new JButton("Làm mới");
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> refreshAccountTable());

        return panel;
    }

    /**
     * Tạo panel bảng giao dịch
     */
    private JPanel createTransactionTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("DANH SÁCH GIAO DỊCH"));

        String[] columnNames = {"Mã GD", "Từ TK", "Đến TK", "Loại", "Số tiền", "Nội dung"};
        transactionTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        transactionTable = new JTable(transactionTableModel);
        transactionTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(transactionTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JButton refreshButton = new JButton("Làm mới");
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> refreshTransactionTable());

        return panel;
    }

    /**
     * Làm mới tất cả dữ liệu
     */
    private void refreshAllData() {
        refreshCustomerTable();
        refreshAccountTable();
        refreshTransactionTable();
    }

    /**
     * Làm mới bảng khách hàng
     */
    private void refreshCustomerTable() {
        customerTableModel.setRowCount(0);
        double totalSystemBalance = 0;
        int totalCustomers = 0;
        int totalAccounts = 0;

        try {
            List<User> allUsers = db.findAllUsers();

            for (User user : allUsers) {
                if (user.getRole() == User.UserRole.CUSTOMER) {
                    totalCustomers++;

                    // Tính tổng số dư của khách hàng
                    double customerBalance = 0;
                    List<String> accountIds = user.getAccountIds();
                    totalAccounts += accountIds.size();

                    for (String accountId : accountIds) {
                        Account account = db.findAccountById(accountId);
                        if (account != null) {
                            customerBalance += account.getBalance();
                        }
                    }

                    totalSystemBalance += customerBalance;

                    customerTableModel.addRow(new Object[]{
                        user.getCustomerId(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getPhoneNumber(),
                        accountIds.size(),
                        String.format("%.2f", customerBalance),
                        user.getRole().toString()
                    });
                }
            }

            totalCustomersLabel.setText("Khách hàng: " + totalCustomers);
            totalAccountsLabel.setText("Tài khoản: " + totalAccounts);
            totalBalanceLabel.setText(String.format("Tổng số dư: %.2f VND", totalSystemBalance));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(),
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Làm mới bảng tài khoản
     */
    private void refreshAccountTable() {
        accountTableModel.setRowCount(0);

        try {
            List<User> allUsers = db.findAllUsers();

            for (User user : allUsers) {
                if (user.getRole() == User.UserRole.CUSTOMER) {
                    List<String> accountIds = user.getAccountIds();

                    for (String accountId : accountIds) {
                        Account account = db.findAccountById(accountId);
                        if (account != null) {
                            accountTableModel.addRow(new Object[]{
                                account.getAccountNumber(),
                                user.getFullName(),
                                String.format("%.2f", account.getBalance()),
                                account.getCardIds().size(),
                                "2025-01-01" // Có thể thêm ngày tạo vào model nếu cần
                            });
                        }
                    }
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(),
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Làm mới bảng giao dịch
     */
    private void refreshTransactionTable() {
        transactionTableModel.setRowCount(0);

        try {
            // Lấy tất cả giao dịch từ hệ thống
            List<User> allUsers = db.findAllUsers();

            for (User user : allUsers) {
                if (user.getRole() == User.UserRole.CUSTOMER) {
                    List<String> accountIds = user.getAccountIds();

                    for (String accountId : accountIds) {
                        var transactions = db.findTransactionsByAccountId(accountId);

                        for (var tx : transactions) {
                            transactionTableModel.addRow(new Object[]{
                                tx.getTransactionId(),
                                tx.getFromAccountId() != null ? tx.getFromAccountId() : "N/A",
                                tx.getToAccountId() != null ? tx.getToAccountId() : "N/A",
                                tx.getType().toString(),
                                String.format("%.2f", tx.getAmount()),
                                tx.getContent()
                            });
                        }
                    }
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(),
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
