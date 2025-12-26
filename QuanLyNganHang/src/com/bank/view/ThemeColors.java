package com.bank.view;

import javax.swing.UIManager;
import java.awt.Color;

/**
 * Lớp hỗ trợ theme colors cho FlatLaf
 * Cung cấp các màu nhất quán với FlatDarkLaf theme
 */
public class ThemeColors {
    
    // --- MÀU CHỦ ĐỀ XANH DƯƠNG (BLUE PROFESSIONAL) ---
    // Màu chính - Xanh dương đậm
    public static final Color PRIMARY = new Color(25, 103, 159);       // Navy Blue
    public static final Color PRIMARY_DARK = new Color(15, 75, 120);   // Dark Navy
    public static final Color PRIMARY_LIGHT = new Color(66, 133, 244); // Light Blue
    
    // Màu thứ cấp - Xanh da trời
    public static final Color SECONDARY = new Color(100, 181, 246);    // Sky Blue
    public static final Color SUCCESS = new Color(76, 175, 80);        // Green
    public static final Color WARNING = new Color(255, 152, 0);        // Orange
    public static final Color DANGER = new Color(244, 67, 54);         // Red
    public static final Color INFO = new Color(100, 181, 246);         // Info Blue
    
    // Màu nền - xám tối nhất quán
    public static final Color BG_DARK = new Color(33, 33, 37);         // Very Dark Gray
    public static final Color BG_DARKER = new Color(25, 25, 28);       // Even Darker Gray
    public static final Color BG_LIGHT = new Color(50, 50, 55);        // Light Gray
    
    // Màu text - Trắng sáng để độ contrast cao
    public static final Color TEXT_PRIMARY = new Color(240, 240, 240);    // Off-White
    public static final Color TEXT_SECONDARY = new Color(190, 190, 190);  // Light Gray Text
    public static final Color TEXT_DISABLED = new Color(130, 130, 130);   // Medium Gray Text
    
    // Màu border - xanh dương nhạt
    public static final Color BORDER = new Color(66, 133, 244);        // Border Blue
    public static final Color BORDER_LIGHT = new Color(100, 149, 237); // Light Border Blue
    
    /**
     * Lấy màu từ UIManager (tuân theo FlatLaf theme hiện tại)
     */
    public static Color getUIColor(String key, Color defaultColor) {
        try {
            Color color = UIManager.getColor(key);
            return color != null ? color : defaultColor;
        } catch (Exception e) {
            return defaultColor;
        }
    }
    
    /**
     * Tạo màu semi-transparent
     */
    public static Color withAlpha(Color color, float alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), 
                        Math.round(alpha * 255));
    }
}
