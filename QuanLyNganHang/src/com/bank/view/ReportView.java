package com.bank.view;

import com.bank.model.User;
import com.bank.repository.DatabaseSimulator;
import com.bank.service.ReportService;
import java.awt.*;
import java.io.File;
import javax.swing.*;

/**
 * Lớp ReportView - Giao diện xuất báo cáo
 * Cho phép người dùng xuất các loại báo cáo khác nhau
 */
public class ReportView extends JPanel {
    private ReportService reportService;
    private User currentUser;
    
    private JTextArea reportArea;
    private JButton exportAccountReportBtn;
    private JButton exportLoanReportBtn;
    private JButton exportCustomerReportBtn;
    private JButton exportSystemReportBtn;
    private JLabel statusLabel;

    public ReportView(DatabaseSimulator db, User currentUser) {
        this.currentUser = currentUser;
        this.reportService = new ReportService(db);

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createButtonPanel(), BorderLayout.WEST);
        add(createContentPanel(), BorderLayout.CENTER);
    }

    /**
     * Tạo panel tiêu đề
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(70, 130, 180));

        JLabel titleLabel = new JLabel("XUẤT BÁO CÁO");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel);

        return panel;
    }

    /**
     * Tạo panel chứa các nút
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 5, 5));
        panel.setPreferredSize(new Dimension(200, 0));
        panel.setBorder(BorderFactory.createTitledBorder("Loại báo cáo"));

        // Nút Báo cáo tài khoản
        exportAccountReportBtn = new JButton("Báo cáo tài khoản");
        exportAccountReportBtn.setBackground(new Color(34, 139, 34));
        exportAccountReportBtn.setForeground(Color.WHITE);
        exportAccountReportBtn.setFont(new Font("Arial", Font.BOLD, 11));
        exportAccountReportBtn.addActionListener(e -> exportAccountReport());
        panel.add(exportAccountReportBtn);

        // Nút Báo cáo khoản vay
        exportLoanReportBtn = new JButton("💳 Báo cáo khoản vay");
        exportLoanReportBtn.setBackground(new Color(70, 130, 180));
        exportLoanReportBtn.setForeground(Color.WHITE);
        exportLoanReportBtn.setFont(new Font("Arial", Font.BOLD, 11));
        exportLoanReportBtn.addActionListener(e -> exportLoanReport());
        panel.add(exportLoanReportBtn);

        // Nút Báo cáo khách hàng
        exportCustomerReportBtn = new JButton("👤 Báo cáo khách hàng");
        exportCustomerReportBtn.setBackground(new Color(255, 140, 0));
        exportCustomerReportBtn.setForeground(Color.WHITE);
        exportCustomerReportBtn.setFont(new Font("Arial", Font.BOLD, 11));
        exportCustomerReportBtn.addActionListener(e -> exportCustomerReport());
        panel.add(exportCustomerReportBtn);

        // Nút Báo cáo tổng quát (Admin only)
        exportSystemReportBtn = new JButton("Báo cáo tổng quát");
        exportSystemReportBtn.setBackground(new Color(200, 16, 46));
        exportSystemReportBtn.setForeground(Color.WHITE);
        exportSystemReportBtn.setFont(new Font("Arial", Font.BOLD, 11));
        exportSystemReportBtn.addActionListener(e -> exportSystemReport());
        
        // Chỉ admin mới có thể xuất báo cáo tổng quát
        if (currentUser.getRole() != User.UserRole.ADMIN) {
            exportSystemReportBtn.setEnabled(false);
        }
        panel.add(exportSystemReportBtn);

        JPanel spacer = new JPanel();
        panel.add(spacer);

        return panel;
    }

    /**
     * Tạo panel nội dung (hiển thị thông tin và trạng thái)
     */
    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Khu vực hiển thị thông tin
        reportArea = new JTextArea(15, 50);
        reportArea.setEditable(false);
        reportArea.setLineWrap(true);
        reportArea.setWrapStyleWord(true);
        reportArea.setBackground(new Color(240, 240, 240));
        reportArea.setFont(new Font("Courier New", Font.PLAIN, 11));
        JScrollPane scrollPane = new JScrollPane(reportArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Khu vực trạng thái
        statusLabel = new JLabel("Chọn loại báo cáo để xuất");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statusLabel.setForeground(new Color(100, 100, 100));
        panel.add(statusLabel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Xuất báo cáo tài khoản
     */
    private void exportAccountReport() {
        try {
            if (currentUser.getAccountIds().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Bạn không có tài khoản nào!",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Chọn tài khoản
            String[] accountIds = currentUser.getAccountIds().toArray(new String[0]);
            String selectedAccount = (String) JOptionPane.showInputDialog(this,
                "Chọn tài khoản:", "Báo cáo tài khoản", JOptionPane.QUESTION_MESSAGE,
                null, accountIds, accountIds[0]);

            if (selectedAccount == null) return;

            // Tạo đường dẫn file
            String reportDir = ReportService.getDefaultReportPath();
            String fileName = ReportService.generateReportFileName("TaiKhoan");
            String filePath = reportDir + File.separator + fileName;

            // Xuất báo cáo
            String result = reportService.exportAccountReport(selectedAccount, filePath);

            reportArea.setText("Báo cáo tài khoản đã được xuất!\n\n");
            reportArea.append(result + "\n");
            statusLabel.setText(result);

            // Hiển thị thông báo
            JOptionPane.showMessageDialog(this, result + "\n\n" +
                "Bạn có muốn mở thư mục chứa báo cáo?",
                "Thành công", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            reportArea.append("✗ Lỗi: " + ex.getMessage() + "\n");
            statusLabel.setText("Lỗi: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(),
                "Lỗi xuất báo cáo", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Xuất báo cáo khoản vay
     */
    private void exportLoanReport() {
        try {
            if (currentUser.getLoanIds().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Bạn không có khoản vay nào!",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Tạo đường dẫn file
            String reportDir = ReportService.getDefaultReportPath();
            String fileName = ReportService.generateReportFileName("KhoanVay");
            String filePath = reportDir + File.separator + fileName;

            // Xuất báo cáo
            String result = reportService.exportLoanReport(currentUser.getCustomerId(), filePath);

            reportArea.setText("Báo cáo khoản vay đã được xuất!\n\n");
            reportArea.append(result + "\n");
            statusLabel.setText(result);

            // Hiển thị thông báo
            JOptionPane.showMessageDialog(this, result,
                "Thành công", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            reportArea.append("✗ Lỗi: " + ex.getMessage() + "\n");
            statusLabel.setText("Lỗi: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(),
                "Lỗi xuất báo cáo", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Xuất báo cáo khách hàng
     */
    private void exportCustomerReport() {
        try {
            // Tạo đường dẫn file
            String reportDir = ReportService.getDefaultReportPath();
            String fileName = ReportService.generateReportFileName("KhachHang");
            String filePath = reportDir + File.separator + fileName;

            // Xuất báo cáo
            String result = reportService.exportCustomerReport(currentUser.getCustomerId(), filePath);

            reportArea.setText("Báo cáo khách hàng đã được xuất!\n\n");
            reportArea.append(result + "\n");
            statusLabel.setText(result);

            // Hiển thị thông báo
            JOptionPane.showMessageDialog(this, result,
                "Thành công", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            reportArea.append("✗ Lỗi: " + ex.getMessage() + "\n");
            statusLabel.setText("Lỗi: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(),
                "Lỗi xuất báo cáo", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Xuất báo cáo tổng quát (Admin only)
     */
    private void exportSystemReport() {
        try {
            if (currentUser.getRole() != User.UserRole.ADMIN) {
                JOptionPane.showMessageDialog(this, "Chỉ admin mới có quyền xuất báo cáo tổng quát!",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Tạo đường dẫn file
            String reportDir = ReportService.getDefaultReportPath();
            String fileName = ReportService.generateReportFileName("HeThong");
            String filePath = reportDir + File.separator + fileName;

            // Xuất báo cáo
            String result = reportService.exportSystemReport(filePath);

            reportArea.setText("Báo cáo tổng quát hệ thống đã được xuất!\n\n");
            reportArea.append(result + "\n");
            statusLabel.setText(result);

            // Hiển thị thông báo
            JOptionPane.showMessageDialog(this, result,
                "Thành công", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            reportArea.append("✗ Lỗi: " + ex.getMessage() + "\n");
            statusLabel.setText("Lỗi: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(),
                "Lỗi xuất báo cáo", JOptionPane.ERROR_MESSAGE);
        }
    }
}
