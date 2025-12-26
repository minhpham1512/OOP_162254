package com.bank.view;

import javax.swing.UIManager;
import java.awt.Color;

/**
 * Lớp hỗ trợ theme colors cho FlatLaf
 * Cung cấp các màu nhất quán với FlatDarkLaf theme
 */
public class ThemeColors {
    
    // Màu chính từ FlatDarkLaf
    public static final Color PRIMARY = new Color(95, 158, 160);      // Teal
    public static final Color PRIMARY_DARK = new Color(70, 130, 180); // Steel Blue
    public static final Color PRIMARY_LIGHT = new Color(176, 224, 230); // Powder Blue
    
    // Màu thứ cấp
    public static final Color SECONDARY = new Color(100, 149, 237);   // Corn Flower Blue
    public static final Color SUCCESS = new Color(76, 175, 80);       // Green
    public static final Color WARNING = new Color(255, 152, 0);       // Orange
    public static final Color DANGER = new Color(244, 67, 54);        // Red
    public static final Color INFO = new Color(33, 150, 243);         // Blue
    
    // Màu nền
    public static final Color BG_DARK = new Color(45, 45, 48);        // Dark Gray
    public static final Color BG_DARKER = new Color(37, 37, 38);      // Darker Gray
    public static final Color BG_LIGHT = new Color(60, 60, 67);       // Light Gray
    
    // Màu text
    public static final Color TEXT_PRIMARY = new Color(229, 229, 229);   // Light Gray Text
    public static final Color TEXT_SECONDARY = new Color(169, 169, 169); // Medium Gray Text
    public static final Color TEXT_DISABLED = new Color(119, 119, 119);  // Dark Gray Text
    
    // Màu border
    public static final Color BORDER = new Color(62, 62, 66);         // Medium Gray Border
    public static final Color BORDER_LIGHT = new Color(76, 76, 81);   // Light Gray Border
    
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
