package com.bank.view;

import com.bank.model.SavingsAccount;
import com.bank.model.Account;
import com.bank.model.User;
import com.bank.repository.DatabaseSimulator;
import com.bank.service.SystemSettingsService;
import java.awt.*;
import java.util.List;
import java.util.UUID;
import javax.swing.*;

/**
 * Giao diện Quản lý Sổ Tiết Kiệm (khách hàng)
 */
public class BankSavings extends JPanel {

    private DatabaseSimulator db;
    private User currentUser;
    private SystemSettingsService settingsService;

    private DefaultListModel<String> listModel;
    private JList<String> savingsList;
    private JComboBox<String> sourceAccountCombo;
    private JTextField amountField;
    private JTextField termField;
    private JTextField rateField;

    public BankSavings(DatabaseSimulator db, User currentUser) {
        this.db = db;
        this.currentUser = currentUser;
        this.settingsService = new SystemSettingsService();

        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        add(createHeader(), BorderLayout.NORTH);
        add(createMain(), BorderLayout.CENTER);

        refreshSavingsList();
    }

    private JPanel createHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(ThemeColors.PRIMARY);
        JLabel title = new JLabel("Sổ tiết kiệm của bạn");
        title.setForeground(ThemeColors.TEXT_PRIMARY);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        p.add(title, BorderLayout.WEST);
        return p;
    }

    private JPanel createMain() {
        JPanel p = new JPanel(new GridLayout(1,2,10,10));

        // Left: list
        JPanel left = new JPanel(new BorderLayout(5,5));
        left.setBorder(BorderFactory.createTitledBorder("Danh sách sổ tiết kiệm"));
        listModel = new DefaultListModel<>();
        savingsList = new JList<>(listModel);
        left.add(new JScrollPane(savingsList), BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Làm mới");
        refreshBtn.addActionListener(e -> refreshSavingsList());
        left.add(refreshBtn, BorderLayout.SOUTH);

        // Right: form tạo sổ
        JPanel right = new JPanel(new GridBagLayout());
        right.setBorder(BorderFactory.createTitledBorder("Mở sổ mới"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        right.add(new JLabel("Tài khoản nguồn:"), gbc);
        gbc.gridx = 1;
        sourceAccountCombo = new JComboBox<>();
        right.add(sourceAccountCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        right.add(new JLabel("Số tiền (VND):"), gbc);
        gbc.gridx = 1;
        amountField = new JTextField();
        right.add(amountField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        right.add(new JLabel("Kỳ hạn (tháng):"), gbc);
        gbc.gridx = 1;
        termField = new JTextField("12");
        right.add(termField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        right.add(new JLabel("Lãi suất (%/năm):"), gbc);
        gbc.gridx = 1;
        rateField = new JTextField(settingsService.getSetting("savings.interest.rate"));
        right.add(rateField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton openBtn = new JButton("Mở sổ");
        openBtn.addActionListener(e -> openSavings());
        right.add(openBtn, gbc);

        p.add(left);
        p.add(right);

        loadSourceAccounts();

        return p;
    }

    private void loadSourceAccounts() {
        sourceAccountCombo.removeAllItems();
        List<String> accIds = currentUser.getAccountIds();
        for (String id : accIds) {
            Account a = db.findAccountById(id);
            if (a != null) sourceAccountCombo.addItem(id + " (" + String.format("%.2f", a.getBalance()) + ")");
        }
    }

    private void refreshSavingsList() {
        listModel.clear();
        List<com.bank.model.SavingsAccount> list = db.findSavingsByCustomerId(currentUser.getCustomerId());
        if (list == null || list.isEmpty()) {
            listModel.addElement("Chưa có sổ tiết kiệm.");
            return;
        }
        for (SavingsAccount sa : list) {
            // Simpler display
            listModel.addElement(sa.getSavingsId() + " — " + String.format("%.2f", sa.getAmount()) + " VND — " + sa.getTermMonths() + " tháng — " + sa.getInterestRate() + "%");
        }
    }

    private void openSavings() {
        try {
            if (sourceAccountCombo.getItemCount() == 0) {
                JOptionPane.showMessageDialog(this, "Không có tài khoản nguồn để mở sổ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String selected = (String) sourceAccountCombo.getSelectedItem();
            if (selected == null) return;
            String srcId = selected.split(" ")[0];

            double amount = Double.parseDouble(amountField.getText());
            int term = Integer.parseInt(termField.getText());
            double rate = Double.parseDouble(rateField.getText());

            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Số tiền phải lớn hơn 0", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Account src = db.findAccountById(srcId);
            if (src == null) throw new Exception("Tài khoản nguồn không tồn tại");
            if (src.getBalance() < amount) throw new Exception("Số dư không đủ để chuyển vào sổ tiết kiệm");

            // Trừ tiền từ tài khoản nguồn
            src.withdraw(amount);
            db.saveAccount(src);

            // Tạo sổ tiết kiệm
            String sid = "SAV" + UUID.randomUUID().toString().substring(0,8).toUpperCase();
            SavingsAccount sa = new SavingsAccount(sid, currentUser.getCustomerId(), amount, term, rate);
            db.saveSavingsAccount(sa);

            // Liên kết sổ với user
            currentUser.addSavingsAccountId(sid);
            db.saveUser(currentUser);

            JOptionPane.showMessageDialog(this, "✓ Đã mở sổ tiết kiệm: " + sid + " — Số dư: " + String.format("%.2f", amount) + " VND", "Thành công", JOptionPane.INFORMATION_MESSAGE);

            loadSourceAccounts();
            refreshSavingsList();

            // Nếu BankGUI bật, yêu cầu cập nhật dashboard
            try {
                java.awt.Window win = javax.swing.SwingUtilities.getWindowAncestor(this);
                if (win instanceof com.bank.view.BankGUI) {
                    ((com.bank.view.BankGUI) win).updateDashboardInfo();
                }
            } catch (Exception ex) {
                System.err.println("Không thể làm mới dashboard: " + ex.getMessage());
            }

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Số tiền/kỳ hạn/lãi không hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
