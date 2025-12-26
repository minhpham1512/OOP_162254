package com.bank;

import com.bank.view.BankGUI;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Lớp chính - Khởi chạy ứng dụng Ngân Hàng
 * Sử dụng hệ thống Look and Feel mặc định
 */
public class Main {

    public static void main(String[] args) {
        // --- Khởi chạy ứng dụng GUI với built-in theme ---
        SwingUtilities.invokeLater(() -> {
            try {
                // Thiết lập hệ thống Look and Feel mặc định
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("Không thể tải FlatLaf theme: " + ex.getMessage());
                // Tiếp tục chạy với Look and Feel mặc định nếu có lỗi
            }
            
            // Khởi tạo và hiển thị ứng dụng
            BankGUI app = new BankGUI();
            app.setVisible(true);
        });
    }
    
    // --- TOÀN BỘ LOGIC CLI CŨ ĐÃ BỊ XÓA ---
    // (Bao gồm showLoginMenu, showMainMenu, handleWithdrawDemo)
    // (setupSampleData đã được chuyển vào BankGUI.java)
}