package com.bank.view;

import com.bank.model.Loan;
import com.bank.model.User;
import com.bank.repository.DatabaseSimulator;
import com.bank.service.AccountService;
import java.awt.*;
import java.util.List;
import java.util.UUID;
import javax.swing.*;

/**
 * Lớp giao diện quản lý chức năng VAY TIỀN (Loan Management)
 * Cho phép người dùng:
 * - Xem danh sách khoản vay
 * - Tạo đơn vay mới
 * - Thanh toán khoản vay
 * - Xem chi tiết khoản vay
 */
public class BankLoan extends JPanel {

    private DatabaseSimulator db;
    private User currentUser;
    private AccountService accountService;

    // Các thành phần UI
    private JTextArea loanListArea;
    private JComboBox<String> loanTypeCombo;
    private JTextField amountField;
    private JTextField interestRateField;
    private JTextField termMonthsField;
    private JList<String> activeLoansList;
    private DefaultListModel<String> loansListModel;

    public BankLoan(DatabaseSimulator db, User currentUser, AccountService accountService) {
        this.db = db;
        this.currentUser = currentUser;
        this.accountService = accountService;

        // Thiết lập layout chính
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Thêm các thành phần
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);

        // Cập nhật danh sách vay
        refreshLoanList();
    }

    /**
     * Tạo panel tiêu đề
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(ThemeColors.PRIMARY);
        
        JLabel titleLabel = new JLabel("QUẢN LÝ KHOẢN VAY TIỀN");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        panel.add(titleLabel);
        
        return panel;
    }

    /**
     * Tạo nội dung chính (gồm 2 phần: danh sách vay và form tạo vay)
     */
    private JPanel createMainContent() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        panel.setBackground(ThemeColors.BG_DARK);

        // Phần trái: Danh sách khoản vay hiện tại
        panel.add(createLoanListPanel());

        // Phần phải: Form tạo khoản vay mới
        panel.add(createNewLoanPanel());

        return panel;
    }

    /**
     * Tạo panel danh sách khoản vay
     */
    private JPanel createLoanListPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(ThemeColors.BG_LIGHT);
        panel.setBorder(BorderFactory.createTitledBorder("DANH SÁCH KHOẢN VAY"));

        loansListModel = new DefaultListModel<>();
        activeLoansList = new JList<>(loansListModel);
        activeLoansList.setBackground(ThemeColors.BG_DARKER);
        activeLoansList.setForeground(ThemeColors.TEXT_PRIMARY);
        JScrollPane scrollPane = new JScrollPane(activeLoansList);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JButton detailButton = new JButton("Chi tiết");
        JButton payButton = new JButton("Thanh toán");
        JButton refreshButton = new JButton("Làm mới");

        buttonPanel.add(detailButton);
        buttonPanel.add(payButton);
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Xử lý sự kiện nút Chi tiết
        detailButton.addActionListener(e -> showLoanDetail());

        // Xử lý sự kiện nút Thanh toán
        payButton.addActionListener(e -> payLoan());

        // Xử lý sự kiện nút Làm mới
        refreshButton.addActionListener(e -> refreshLoanList());

        return panel;
    }

    /**
     * Tạo panel form tạo khoản vay mới
     */
    private JPanel createNewLoanPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ThemeColors.BG_LIGHT);
        panel.setBorder(BorderFactory.createTitledBorder("TẠO ĐƠN VAY MỚI"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Loại vay
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Loại vay:"), gbc);
        gbc.gridx = 1;
        loanTypeCombo = new JComboBox<>(new String[]{
            "Vay tín chấp",
            "Vay mua nhà",
            "Vay mua xe",
            "Vay kinh doanh",
            "Vay tiêu dùng"
        });
        panel.add(loanTypeCombo, gbc);

        // Số tiền vay
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Số tiền vay (VND):"), gbc);
        gbc.gridx = 1;
        amountField = new JTextField(20);
        panel.add(amountField, gbc);

        // Lãi suất
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Lãi suất (%/năm):"), gbc);
        gbc.gridx = 1;
        interestRateField = new JTextField(20);
        interestRateField.setText("5.5");
        interestRateField.setEditable(false);
        panel.add(interestRateField, gbc);

        // Kỳ hạn
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Kỳ hạn (tháng):"), gbc);
        gbc.gridx = 1;
        termMonthsField = new JTextField(20);
        termMonthsField.setText("12");
        panel.add(termMonthsField, gbc);

        // Nút tạo vay
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton createLoanButton = new JButton("Tạo đơn vay");
        createLoanButton.setBackground(ThemeColors.SUCCESS);
        createLoanButton.setForeground(ThemeColors.TEXT_PRIMARY);
        createLoanButton.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(createLoanButton, gbc);

        // Xử lý sự kiện nút Tạo đơn vay
        createLoanButton.addActionListener(e -> createNewLoan());

        return panel;
    }

    /**
     * Tạo panel chân trang (thông tin hữu ích)
     */
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        loanListArea = new JTextArea(3, 50);
        loanListArea.setEditable(false);
        loanListArea.setLineWrap(true);
        loanListArea.setWrapStyleWord(true);
        loanListArea.setText("Thông tin: Công ty sẽ phê duyệt đơn vay của bạn trong vòng 24-48 giờ.\n" +
                             "Lãi suất có thể thay đổi tùy theo loại vay và điều kiện khách hàng.\n" +
                             "Liên hệ với bộ phận hỗ trợ nếu bạn có bất kỳ câu hỏi nào.");
        loanListArea.setBackground(ThemeColors.BG_LIGHT);
        loanListArea.setForeground(ThemeColors.TEXT_PRIMARY);
        JScrollPane scrollPane = new JScrollPane(loanListArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Tạo khoản vay mới
     */
    private void createNewLoan() {
        try {
            // Kiểm tra dữ liệu đầu vào
            if (amountField.getText().isEmpty() || interestRateField.getText().isEmpty() || 
                termMonthsField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", 
                                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double amount = Double.parseDouble(amountField.getText());
            double interestRate = Double.parseDouble(interestRateField.getText());
            int termMonths = Integer.parseInt(termMonthsField.getText());
            String loanType = (String) loanTypeCombo.getSelectedItem();

            // Kiểm tra giá trị hợp lệ
            if (amount <= 0 || interestRate < 0 || termMonths <= 0) {
                JOptionPane.showMessageDialog(this, "Giá trị không hợp lệ!\n" +
                                            "- Số tiền vay phải > 0\n" +
                                            "- Lãi suất phải >= 0\n" +
                                            "- Kỳ hạn phải > 0",
                                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Tạo ID vay duy nhất
            String loanId = "LOAN" + UUID.randomUUID().toString().substring(0, 8);

            // Tạo đối tượng Loan
            Loan newLoan = new Loan(loanId, currentUser.getCustomerId(), loanType, 
                                   amount, interestRate, termMonths);
            
            // Lưu vào "database"
            db.saveLoan(newLoan);

            // Hiển thị thông báo thành công
            JOptionPane.showMessageDialog(this, 
                                        String.format("Đơn vay đã được tạo thành công!\n" +
                                                     "Mã đơn vay: %s\n" +
                                                     "Số tiền: %.2f VND\n" +
                                                     "Kỳ hạn: %d tháng\n" +
                                                     "Trạng thái: %s",
                                                     loanId, amount, termMonths, "ĐANG CHỜ DUYỆT"),
                                        "Thành công", JOptionPane.INFORMATION_MESSAGE);

            // Xóa form
            amountField.setText("");
            interestRateField.setText("5.5");
            termMonthsField.setText("12");

            // Cập nhật danh sách vay
            refreshLoanList();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: Vui lòng nhập số hợp lệ!", 
                                        "Lỗi Dữ liệu", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), 
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Làm mới danh sách khoản vay
     */
    private void refreshLoanList() {
        loansListModel.clear();
        
        try {
            // Lấy tất cả khoản vay của người dùng hiện tại
            List<Loan> loans = db.findLoansByCustomerId(currentUser.getCustomerId());
            
            if (loans.isEmpty()) {
                loansListModel.addElement("Chưa có khoản vay nào");
                return;
            }

            for (Loan loan : loans) {
                String displayText = String.format("ID: %s | Loại: %s | Số tiền: %.2f VND | Trạng thái: %s",
                                                  loan.getLoanId(), 
                                                  loan.getLoanType(), 
                                                  loan.getAmount(),
                                                  loan.getStatus().toString());
                loansListModel.addElement(displayText);
            }
        } catch (Exception ex) {
            loansListModel.addElement("Lỗi: " + ex.getMessage());
        }
    }

    /**
     * Hiển thị chi tiết khoản vay được chọn
     */
    private void showLoanDetail() {
        int selectedIndex = activeLoansList.getSelectedIndex();
        if (selectedIndex < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một khoản vay!", 
                                        "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            List<Loan> loans = db.findLoansByCustomerId(currentUser.getCustomerId());
            if (loans.isEmpty() || selectedIndex >= loans.size()) {
                return;
            }

            Loan selectedLoan = loans.get(selectedIndex);

            // Tính lãi phải trả
            double totalInterest = selectedLoan.getAmount() * selectedLoan.getInterestRate() / 100 * 
                                 (selectedLoan.getTermMonths() / 12.0);
            double monthlyPayment = (selectedLoan.getAmount() + totalInterest) / selectedLoan.getTermMonths();

            String detail = String.format(
                "=== CHI TIẾT KHOẢN VAY ===\n" +
                "Mã vay: %s\n" +
                "Loại vay: %s\n" +
                "Số tiền vay: %.2f VND\n" +
                "Lãi suất: %.2f %%/năm\n" +
                "Kỳ hạn: %d tháng\n" +
                "Tổng lãi phải trả: %.2f VND\n" +
                "Thanh toán hàng tháng: %.2f VND\n" +
                "Tổng tiền phải trả: %.2f VND\n" +
                "Ngày bắt đầu: %s\n" +
                "Ngày đáo hạn: %s\n" +
                "Trạng thái: %s",
                selectedLoan.getLoanId(),
                selectedLoan.getLoanType(),
                selectedLoan.getAmount(),
                selectedLoan.getInterestRate(),
                selectedLoan.getTermMonths(),
                totalInterest,
                monthlyPayment,
                selectedLoan.getAmount() + totalInterest,
                selectedLoan.getStartDate(),
                selectedLoan.getMaturityDate(),
                selectedLoan.getStatus().toString()
            );

            JOptionPane.showMessageDialog(this, detail, "Chi tiết khoản vay", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Thanh toán khoản vay được chọn
     */
    private void payLoan() {
        int selectedIndex = activeLoansList.getSelectedIndex();
        if (selectedIndex < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một khoản vay để thanh toán!", 
                                        "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            List<Loan> loans = db.findLoansByCustomerId(currentUser.getCustomerId());
            if (loans.isEmpty() || selectedIndex >= loans.size()) {
                return;
            }

            Loan selectedLoan = loans.get(selectedIndex);

            // Kiểm tra trạng thái khoản vay
            if (selectedLoan.getStatus() == Loan.LoanStatus.PAID_OFF) {
                JOptionPane.showMessageDialog(this, "Khoản vay này đã được thanh toán hết!",
                                            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Tính số tiền còn phải trả
            double totalInterest = selectedLoan.getAmount() * selectedLoan.getInterestRate() / 100 * 
                                 (selectedLoan.getTermMonths() / 12.0);
            double totalDebt = selectedLoan.getAmount() + totalInterest;

            // Hiển thị dialog thanh toán
            String[] options = {"Thanh toán toàn bộ", "Thanh toán một phần", "Hủy"};
            int choice = JOptionPane.showOptionDialog(this,
                                                     "Chọn phương thức thanh toán:\n" +
                                                     "Số tiền còn phải trả: " + totalDebt + " VND",
                                                     "Thanh toán khoản vay",
                                                     JOptionPane.YES_NO_CANCEL_OPTION,
                                                     JOptionPane.QUESTION_MESSAGE,
                                                     null, options, options[0]);

            if (choice == 0) {
                // Thanh toán toàn bộ
                try {
                    double userBalance = accountService.getBalance(currentUser.getAccountIds().get(0));
                    if (userBalance < totalDebt) {
                        JOptionPane.showMessageDialog(this, "Số dư không đủ! Số dư hiện tại: " + userBalance + " VND",
                                                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Cập nhật trạng thái khoản vay
                    selectedLoan.setStatus(Loan.LoanStatus.PAID_OFF);
                    db.saveLoan(selectedLoan);

                    JOptionPane.showMessageDialog(this, "Thanh toán thành công!\n" +
                                                "Số tiền thanh toán: " + totalDebt + " VND",
                                                "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    
                    refreshLoanList();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(),
                                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                }

            } else if (choice == 1) {
                // Thanh toán một phần
                String amountStr = JOptionPane.showInputDialog(this, "Nhập số tiền muốn thanh toán:", "");
                if (amountStr != null && !amountStr.isEmpty()) {
                    try {
                        double paymentAmount = Double.parseDouble(amountStr);
                        if (paymentAmount <= 0) {
                            JOptionPane.showMessageDialog(this, "Số tiền phải lớn hơn 0!",
                                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        JOptionPane.showMessageDialog(this, "Thanh toán " + paymentAmount + " VND thành công!\n" +
                                                    "Số tiền còn lại: " + (totalDebt - paymentAmount) + " VND",
                                                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        
                        refreshLoanList();
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ!",
                                                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
