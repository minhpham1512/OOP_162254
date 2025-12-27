package com.bank.view;

import com.bank.service.SystemSettingsService;
import java.awt.*;
import javax.swing.*;

/**
 * Lớp SystemSettingsView - Giao diện cài đặt hệ thống
 * Cho phép:
 * - Xem tất cả cài đặt
 * - Sửa các cài đặt
 * - Xem nhật ký hoạt động
 * - Xem thống kê hệ thống
 * - Bật/tắt chế độ bảo trì
 * - Reset về mặc định
 */
public class SystemSettingsView extends JPanel {
    private SystemSettingsService settingsService;
    
    private JTabbedPane tabbedPane;
    private JTextArea settingsArea;
    private JTextArea logsArea;
    private JTextArea statsArea;
    private JButton viewSettingsBtn;
    private JButton editSettingsBtn;
    private JButton maintenanceBtn;
    private JButton resetBtn;
    private JButton viewLogsBtn;
    private JButton viewStatsBtn;
    private JLabel statusLabel;

    public SystemSettingsView() {
        this.settingsService = new SystemSettingsService();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    /**
     * Tạo panel tiêu đề
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(ThemeColors.PRIMARY);

        JLabel titleLabel = new JLabel("CÀI ĐẶT HỆ THỐNG");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        panel.add(titleLabel);

        return panel;
    }

    /**
     * Tạo panel chính với tabbed pane
     */
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        tabbedPane = new JTabbedPane();

        // Tab 1: Cài đặt
        tabbedPane.addTab("📋 Cài Đặt", createSettingsTab());

        // Tab 2: Nhật ký
        tabbedPane.addTab("Nhật Ký", createLogsTab());

        // Tab 3: Thống kê
        tabbedPane.addTab("Thống Kê", createStatsTab());

        panel.add(tabbedPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Tạo tab Cài đặt
     */
    private JPanel createSettingsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        viewSettingsBtn = new JButton("👁️ Xem cài đặt");
        viewSettingsBtn.setBackground(ThemeColors.PRIMARY);
        viewSettingsBtn.setForeground(ThemeColors.TEXT_PRIMARY);
        viewSettingsBtn.addActionListener(e -> viewAllSettings());
        buttonPanel.add(viewSettingsBtn);

        maintenanceBtn = new JButton("⚠️ Chế độ bảo trì");
        maintenanceBtn.setBackground(ThemeColors.WARNING);
        maintenanceBtn.setForeground(ThemeColors.TEXT_PRIMARY);
        maintenanceBtn.addActionListener(e -> toggleMaintenance());
        buttonPanel.add(maintenanceBtn);

        editSettingsBtn = new JButton("✏️ Sửa cài đặt");
        editSettingsBtn.setBackground(ThemeColors.SUCCESS);
        editSettingsBtn.setForeground(ThemeColors.TEXT_PRIMARY);
        editSettingsBtn.addActionListener(e -> editSetting());
        buttonPanel.add(editSettingsBtn);

        resetBtn = new JButton("Reset về mặc định");
        resetBtn.setBackground(ThemeColors.DANGER);
        resetBtn.setForeground(ThemeColors.TEXT_PRIMARY);
        resetBtn.addActionListener(e -> resetSettings());
        buttonPanel.add(resetBtn);

        panel.add(buttonPanel, BorderLayout.NORTH);

        // Khu vực hiển thị
        settingsArea = new JTextArea(20, 60);
        settingsArea.setEditable(false);
        settingsArea.setLineWrap(true);
        settingsArea.setWrapStyleWord(true);
        settingsArea.setBackground(ThemeColors.BG_LIGHT);
        settingsArea.setForeground(ThemeColors.TEXT_PRIMARY);
        settingsArea.setFont(new Font("Courier New", Font.PLAIN, 11));
        settingsArea.setText("Nhấn 'Xem cài đặt' để xem chi tiết...");
        JScrollPane scrollPane = new JScrollPane(settingsArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Tạo tab Nhật ký
     */
    private JPanel createLogsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        viewLogsBtn = new JButton("📖 Xem nhật ký (50 dòng)");
        viewLogsBtn.setBackground(ThemeColors.PRIMARY);
        viewLogsBtn.setForeground(ThemeColors.TEXT_PRIMARY);
        viewLogsBtn.addActionListener(e -> viewLogs());
        buttonPanel.add(viewLogsBtn);

        panel.add(buttonPanel, BorderLayout.NORTH);

        // Khu vực hiển thị
        logsArea = new JTextArea(20, 60);
        logsArea.setEditable(false);
        logsArea.setLineWrap(true);
        logsArea.setWrapStyleWord(true);
        logsArea.setBackground(ThemeColors.BG_LIGHT);
        logsArea.setForeground(ThemeColors.TEXT_PRIMARY);
        logsArea.setFont(new Font("Courier New", Font.PLAIN, 10));
        logsArea.setText("Nhấn 'Xem nhật ký' để xem hoạt động gần đây...");
        JScrollPane scrollPane = new JScrollPane(logsArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Tạo tab Thống kê
     */
    private JPanel createStatsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        viewStatsBtn = new JButton("📈 Xem thống kê hệ thống");
        viewStatsBtn.setBackground(ThemeColors.SUCCESS);
        viewStatsBtn.setForeground(ThemeColors.TEXT_PRIMARY);
        viewStatsBtn.addActionListener(e -> viewStats());
        buttonPanel.add(viewStatsBtn);

        panel.add(buttonPanel, BorderLayout.NORTH);

        // Khu vực hiển thị
        statsArea = new JTextArea(20, 60);
        statsArea.setEditable(false);
        statsArea.setLineWrap(true);
        statsArea.setWrapStyleWord(true);
        statsArea.setBackground(ThemeColors.BG_LIGHT);
        statsArea.setForeground(ThemeColors.TEXT_PRIMARY);
        statsArea.setFont(new Font("Courier New", Font.PLAIN, 11));
        statsArea.setText("Nhấn 'Xem thống kê' để xem tình trạng hệ thống...");
        JScrollPane scrollPane = new JScrollPane(statsArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Tạo panel chân trang
     */
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        statusLabel = new JLabel("Sẵn sàng");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statusLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        panel.add(statusLabel, BorderLayout.WEST);

        return panel;
    }

    /**
     * Xem tất cả cài đặt
     */
    private void viewAllSettings() {
        String settings = settingsService.getAllSettings();
        settingsArea.setText(settings);
        statusLabel.setText("Cài đặt được tải");
    }

    /**
     * Sửa cài đặt
     */
    private void editSetting() {
        String key = JOptionPane.showInputDialog(this,
            "Nhập tên cài đặt (ví dụ: savings.interest.rate):");
        
        if (key == null || key.trim().isEmpty()) {
            return;
        }

        String currentValue = settingsService.getSetting(key);
        String newValue = JOptionPane.showInputDialog(this,
            "Giá trị hiện tại: " + currentValue + "\nNhập giá trị mới:",
            currentValue);

        if (newValue == null) {
            return;
        }

        String result = settingsService.setSetting(key, newValue);
        
        settingsArea.setText("✓ " + result);
        statusLabel.setText("Cài đặt được cập nhật");
        
        JOptionPane.showMessageDialog(this, result,
            "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Bật/tắt chế độ bảo trì
     */
    private void toggleMaintenance() {
        boolean isCurrentlyOn = settingsService.isMaintenanceMode();
        
        int result = JOptionPane.showConfirmDialog(this,
            "Chế độ bảo trì hiện tại: " + (isCurrentlyOn ? "BẬT" : "TẮT") + "\n\n" +
            "Bạn muốn " + (isCurrentlyOn ? "TẮT" : "BẬT") + " chế độ bảo trì?",
            "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            String resultMsg = settingsService.setMaintenanceMode(!isCurrentlyOn);
            settingsArea.setText("✓ " + resultMsg);
            statusLabel.setText(resultMsg);
            
            JOptionPane.showMessageDialog(this, resultMsg,
                "Thành công", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Reset về mặc định
     */
    private void resetSettings() {
        int result = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc muốn reset tất cả cài đặt về mặc định?\n" +
            "Hành động này không thể hoàn tác!",
            "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            String resultMsg = settingsService.resetToDefault();
            settingsArea.setText("✓ " + resultMsg);
            statusLabel.setText("Cài đặt được reset");
            
            JOptionPane.showMessageDialog(this, resultMsg,
                "Thành công", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Xem nhật ký
     */
    private void viewLogs() {
        String logs = settingsService.getRecentLogs(50);
        logsArea.setText(logs);
        statusLabel.setText("Nhật ký được tải");
    }

    /**
     * Xem thống kê hệ thống
     */
    private void viewStats() {
        String stats = settingsService.getSystemStats();
        statsArea.setText(stats);
        statusLabel.setText("Thống kê được tải");
    }
}
