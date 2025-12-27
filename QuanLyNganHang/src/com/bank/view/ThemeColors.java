package com.bank.view;

import java.awt.Color;
import javax.swing.UIManager;

/**
 * Lớp hỗ trợ theme colors cho FlatLaf
 * Cung cấp các màu nhất quán với FlatDarkLaf theme
 */
public class ThemeColors {
    
    // --- CHỦ ĐỀ SIMPLISTIC: NỀN TRẮNG-XÁM, CHỮ ĐEN ---
    // Tất cả dùng xám/trắng để đơn giản
    public static final Color PRIMARY = new Color(100, 100, 100);       // Dark Gray
    public static final Color PRIMARY_DARK = new Color(70, 70, 70);    // Medium Dark Gray
    public static final Color PRIMARY_LIGHT = new Color(150, 150, 150); // Light Gray
    
    // Màu thứ cấp - tất cả dùng gray scale
    public static final Color SECONDARY = new Color(130, 130, 130);     // Medium Gray
    public static final Color SUCCESS = new Color(100, 100, 100);       // Dark Gray
    public static final Color WARNING = new Color(120, 120, 120);       // Gray
    public static final Color DANGER = new Color(100, 100, 100);        // Dark Gray
    public static final Color INFO = new Color(130, 130, 130);          // Medium Gray
    
    // Màu nền - TRẮNG-XÁM
    public static final Color BG_DARK = new Color(245, 245, 245);      // Very Light Gray
    public static final Color BG_DARKER = new Color(235, 235, 235);    // Light Gray
    public static final Color BG_LIGHT = new Color(250, 250, 250);     // Almost White
    
    // Màu text - ĐEN
    public static final Color TEXT_PRIMARY = new Color(20, 20, 20);       // Almost Black
    public static final Color TEXT_SECONDARY = new Color(80, 80, 80);     // Dark Gray
    public static final Color TEXT_DISABLED = new Color(150, 150, 150);   // Medium Gray
    
    // Màu border - Xám
    public static final Color BORDER = new Color(150, 150, 150);       // Gray Border
    public static final Color BORDER_LIGHT = new Color(200, 200, 200); // Light Gray Border
    
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
