package com.bank.view;

import com.bank.repository.DatabaseSimulator;
import com.bank.service.BackupService;
import com.bank.service.BackupService.BackupInfo;
import java.awt.*;
import java.io.File;
import java.util.List;
import javax.swing.*;

/**
 * Lớp BackupView - Giao diện quản lý sao lưu dữ liệu
 * Cho phép:
 * - Thực hiện sao lưu dữ liệu
 * - Xem danh sách các bản sao lưu
 * - Xóa bản sao lưu cũ
 * - Kiểm tra dung lượng
 */
public class BackupView extends JPanel {
    private BackupService backupService;
    
    private JTextArea infoArea;
    private JButton performBackupBtn;
    private JButton listBackupsBtn;
    private JButton deleteOldBtn;
    private JButton openFolderBtn;
    private JButton statsBtn;
    private JLabel statusLabel;

    public BackupView(DatabaseSimulator db) {
        this.backupService = new BackupService(db);

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

        JLabel titleLabel = new JLabel("SAO LƯU DỮ LIỆU");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel);

        return panel;
    }

    /**
     * Tạo panel chứa các nút chức năng
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 5, 5));
        panel.setPreferredSize(new Dimension(200, 0));
        panel.setBorder(BorderFactory.createTitledBorder("Chức năng"));

        // Nút sao lưu
        performBackupBtn = new JButton("Sao lưu ngay");
        performBackupBtn.setBackground(new Color(34, 139, 34));
        performBackupBtn.setForeground(Color.WHITE);
        performBackupBtn.setFont(new Font("Arial", Font.BOLD, 11));
        performBackupBtn.addActionListener(e -> performBackup());
        panel.add(performBackupBtn);

        // Nút liệt kê
        listBackupsBtn = new JButton("📋 Danh sách");
        listBackupsBtn.setBackground(new Color(70, 130, 180));
        listBackupsBtn.setForeground(Color.WHITE);
        listBackupsBtn.setFont(new Font("Arial", Font.BOLD, 11));
        listBackupsBtn.addActionListener(e -> listBackups());
        panel.add(listBackupsBtn);

        // Nút xóa cũ
        deleteOldBtn = new JButton("🗑️ Xóa bản cũ");
        deleteOldBtn.setBackground(new Color(200, 16, 46));
        deleteOldBtn.setForeground(Color.WHITE);
        deleteOldBtn.setFont(new Font("Arial", Font.BOLD, 11));
        deleteOldBtn.addActionListener(e -> deleteOldBackups());
        panel.add(deleteOldBtn);

        // Nút thống kê
        statsBtn = new JButton("Thống kê");
        statsBtn.setBackground(new Color(255, 140, 0));
        statsBtn.setForeground(Color.WHITE);
        statsBtn.setFont(new Font("Arial", Font.BOLD, 11));
        statsBtn.addActionListener(e -> showStats());
        panel.add(statsBtn);

        // Nút mở thư mục
        openFolderBtn = new JButton("📁 Mở thư mục");
        openFolderBtn.setBackground(new Color(128, 128, 128));
        openFolderBtn.setForeground(Color.WHITE);
        openFolderBtn.setFont(new Font("Arial", Font.BOLD, 11));
        openFolderBtn.addActionListener(e -> openBackupFolder());
        panel.add(openFolderBtn);

        return panel;
    }

    /**
     * Tạo panel nội dung
     */
    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Khu vực thông tin
        infoArea = new JTextArea(15, 50);
        infoArea.setEditable(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        infoArea.setBackground(new Color(240, 240, 240));
        infoArea.setFont(new Font("Courier New", Font.PLAIN, 11));
        infoArea.setText("Chọn chức năng để bắt đầu sao lưu dữ liệu...");
        JScrollPane scrollPane = new JScrollPane(infoArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Khu vực trạng thái
        statusLabel = new JLabel("Sẵn sàng");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statusLabel.setForeground(new Color(100, 100, 100));
        panel.add(statusLabel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Thực hiện sao lưu
     */
    private void performBackup() {
        try {
            performBackupBtn.setEnabled(false);
            statusLabel.setText("Đang sao lưu dữ liệu...");
            
            String result = backupService.performBackup();
            
            infoArea.setText("✓ SAO LƯU THÀNH CÔNG\n\n");
            infoArea.append(result);
            
            statusLabel.setText(result);
            
            JOptionPane.showMessageDialog(this, result,
                "Sao lưu thành công", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            infoArea.setText("✗ Lỗi sao lưu: " + ex.getMessage());
            statusLabel.setText("Lỗi sao lưu");
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(),
                "Lỗi sao lưu", JOptionPane.ERROR_MESSAGE);
        } finally {
            performBackupBtn.setEnabled(true);
        }
    }

    /**
     * Liệt kê danh sách sao lưu
     */
    private void listBackups() {
        try {
            List<BackupInfo> backups = backupService.listBackups();
            
            StringBuilder sb = new StringBuilder();
            sb.append("📋 DANH SÁCH CÁC BẢN SAO LƯU\n");
            sb.append("═════════════════════════════════════════════\n\n");

            if (backups.isEmpty()) {
                sb.append("Chưa có bản sao lưu nào.\n");
            } else {
                for (int i = 0; i < backups.size(); i++) {
                    BackupInfo backup = backups.get(i);
                    sb.append(String.format("%d. %s\n", i + 1, backup.getFileName()));
                    sb.append(String.format("   Ngày tạo: %s\n", backup.getFormattedDate()));
                    sb.append(String.format("   Dung lượng: %s\n\n", backup.getFormattedSize()));
                }
            }

            infoArea.setText(sb.toString());
            statusLabel.setText("Danh sách được cập nhật (" + backups.size() + " bản)");

        } catch (Exception ex) {
            infoArea.setText("✗ Lỗi: " + ex.getMessage());
            statusLabel.setText("Lỗi");
        }
    }

    /**
     * Xóa bản sao lưu cũ
     */
    private void deleteOldBackups() {
        try {
            String input = JOptionPane.showInputDialog(this,
                "Nhập số lượng bản sao lưu giữ lại (ví dụ: 5):",
                "5");

            if (input == null) return;

            int keepCount = Integer.parseInt(input);
            if (keepCount < 1) {
                JOptionPane.showMessageDialog(this, "Số lượng phải >= 1",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String result = backupService.deleteOldBackups(keepCount);
            
            infoArea.setText(result);
            statusLabel.setText(result);
            
            JOptionPane.showMessageDialog(this, result,
                "Xóa thành công", JOptionPane.INFORMATION_MESSAGE);

            listBackups();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            infoArea.setText("✗ Lỗi: " + ex.getMessage());
            statusLabel.setText("Lỗi");
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(),
                "Lỗi xóa", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Hiển thị thống kê sao lưu
     */
    private void showStats() {
        try {
            String stats = backupService.getBackupStats();
            infoArea.setText(stats);
            statusLabel.setText("Thống kê cập nhật");
        } catch (Exception ex) {
            infoArea.setText("✗ Lỗi: " + ex.getMessage());
            statusLabel.setText("Lỗi");
        }
    }

    /**
     * Mở thư mục sao lưu
     */
    private void openBackupFolder() {
        try {
            String folderPath = BackupService.getBackupDirectory();
            File folder = new File(folderPath);
            
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // Mở thư mục (Windows)
            Runtime.getRuntime().exec("explorer.exe " + folderPath);
            
            statusLabel.setText("Đang mở thư mục: " + folderPath);

        } catch (Exception ex) {
            infoArea.setText("✗ Lỗi mở thư mục: " + ex.getMessage());
            statusLabel.setText("Lỗi");
        }
    }
}
