package com.bank.view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.*;

/**
 * Lớp hỗ trợ UI - Cung cấp các thành phần và hiệu ứng UI tùy chỉnh
 */
public class UIUtils {
    
    /**
     * Tạo nút với style tùy chỉnh, icon emoji và hiệu ứng hover
     */
    public static JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setForeground(fgColor);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        
        // Hover effect
        Color originalBg = bgColor;
        Color hoverBg = lightenColor(bgColor, 0.2f);
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverBg);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalBg);
            }
        });
        
        return button;
    }
    
    /**
     * Tạo label tiêu đề lớn với style
     */
    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 28));
        label.setForeground(ThemeColors.TEXT_PRIMARY);
        return label;
    }
    
    /**
     * Tạo label tiêu đề con với style
     */
    public static JLabel createSubtitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(ThemeColors.SECONDARY);
        return label;
    }
    
    /**
     * Tạo panel với header đẹp
     */
    public static JPanel createHeaderPanel(String title, Color bgColor) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Vẽ gradient background từ PRIMARY đến PRIMARY_DARK
                GradientPaint gradient = new GradientPaint(0, 0, ThemeColors.PRIMARY, 
                                                          0, getHeight(), ThemeColors.PRIMARY_DARK);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        
        panel.setLayout(new BorderLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(0, 80));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = createTitleLabel(title);
        panel.add(titleLabel, BorderLayout.WEST);
        
        return panel;
    }
    
    /**
     * Tạo rounded panel
     */
    public static JPanel createRoundedPanel(int radius) {
        return new JPanel() {
            private int cornerRadius = radius;
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
            }
            
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(getBorder() != null ? ThemeColors.BORDER : new Color(0, 0, 0, 0));
                g2d.setStroke(new BasicStroke(1f));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
            }
        };
    }
    
    /**
     * Tạo separator với style tùy chỉnh
     */
    public static JSeparator createStyledSeparator() {
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(ThemeColors.BORDER_LIGHT);
        separator.setPreferredSize(new Dimension(0, 2));
        return separator;
    }
    
    /**
     * Tạo card panel - dùng cho các khối thông tin
     */
    public static JPanel createCardPanel(String title, JComponent content) {
        JPanel cardPanel = createRoundedPanel(10);
        cardPanel.setBackground(ThemeColors.BG_LIGHT);
        cardPanel.setLayout(new BorderLayout(10, 10));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = createSubtitleLabel(title);
        cardPanel.add(titleLabel, BorderLayout.NORTH);
        cardPanel.add(content, BorderLayout.CENTER);
        
        return cardPanel;
    }
    
    /**
     * Làm nhẹt/tối màu
     */
    public static Color lightenColor(Color color, float factor) {
        int r = Math.min(255, (int) (color.getRed() + (255 - color.getRed()) * factor));
        int g = Math.min(255, (int) (color.getGreen() + (255 - color.getGreen()) * factor));
        int b = Math.min(255, (int) (color.getBlue() + (255 - color.getBlue()) * factor));
        return new Color(r, g, b);
    }
    
    /**
     * Tạo icon từ emoji
     */
    public static ImageIcon createEmojiIcon(String emoji, int size) {
        JLabel label = new JLabel(emoji);
        label.setFont(new Font("Arial", Font.PLAIN, size));
        
        BufferedImage image = new BufferedImage(size + 4, size + 4, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        label.paint(g2d);
        g2d.dispose();
        
        return new ImageIcon(image);
    }
    
    /**
     * Tạo nút với icon và hover effect
     */
    public static JButton createIconButton(String emoji, String tooltip, Color bgColor) {
        JButton button = new JButton(emoji);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setToolTipText(tooltip);
        button.setBackground(bgColor);
        button.setForeground(ThemeColors.TEXT_PRIMARY);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        
        Color hoverBg = lightenColor(bgColor, 0.2f);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverBg);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    
    
    /**
     * Import BufferedImage
     */
}