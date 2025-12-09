package com.bank;

import com.bank.view.BankGUI;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import com.formdev.flatlaf.FlatDarkLaf;

/**
 * Lớp chính - Khởi chạy ứng dụng Ngân Hàng
 * Sử dụng FlatLaf theme để có giao diện hiện đại, phẳng
 */
public class Main {

    public static void main(String[] args) {
        // --- Khởi chạy ứng dụng GUI với FlatLaf theme ---
        SwingUtilities.invokeLater(() -> {
            try {
                // Thiết lập FlatDarkLaf theme (hiện đại, phẳng)
                UIManager.setLookAndFeel(new FlatDarkLaf());
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
