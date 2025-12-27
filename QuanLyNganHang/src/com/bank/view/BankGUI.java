package com.bank.view;

import com.bank.model.*;
import com.bank.repository.DatabaseSimulator;
import com.bank.service.AccountService;
import com.bank.service.AuthService;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.swing.*;

/**
 * Lớp Giao diện Người dùng (View) chính
 * Sử dụng Java Swing.
 */
public class BankGUI extends JFrame {

    // Các thành phần Service và Repository
    private static DatabaseSimulator db;
    private static AuthService authService;
    private static AccountService accountService;

    // Trạng thái người dùng
    private User currentUser;

    // Các thành phần UI chính
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JPanel loginPanel;
    private JPanel dashboardPanel;

    // Thành phần trên Dashboard
    private JLabel welcomeLabel;
    private JTabbedPane tabbedPane;
    private BankLoan bankLoanPanel;
    private BankNap bankNapPanel;

    public BankGUI() {
        // 1. Khởi tạo services
        db = new DatabaseSimulator();
        authService = new AuthService(db);
        accountService = new AccountService(db);

        // 2. Nạp dữ liệu mẫu
        setupSampleData();

        // 3. Cài đặt cửa sổ chính (JFrame)
        setTitle("🏦 Ngân Hàng OOP - Hệ thống Quản lý Ngân Hàng");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Giữa màn hình
        setResizable(true);
        setExtendedState(JFrame.NORMAL); // Đảm bảo không fullscreen

        // 4. Sử dụng CardLayout để chuyển đổi giữa các màn hình
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // 5. Tạo các màn hình (panel)
        createLoginPanel();
        createDashboardPanel();

        // 6. Thêm các panel vào panel chính
        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(dashboardPanel, "DASHBOARD");

        // 7. Thêm panel chính vào JFrame và hiển thị
        add(mainPanel);
        cardLayout.show(mainPanel, "LOGIN"); // Hiển thị màn hình login đầu tiên
    }

    /**
     * Tạo màn hình đăng nhập
     */
    private void createLoginPanel() {
        // Panel chính với gradient background
        loginPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Vẽ gradient từ xanh dương đậm đến xanh dương nhạt (nhất quán)
                GradientPaint gradient = new GradientPaint(
                    0, 0, ThemeColors.PRIMARY_DARK,           // Xanh dương đậm phía trên
                    getWidth(), getHeight(), ThemeColors.PRIMARY  // Xanh dương phía dưới
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        loginPanel.setLayout(new BorderLayout());
        
        // Panel content ở giữa
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Avatar tròn (import hình Maneki-neko từ URL)
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JPanel logoPanel = new JPanel() {
            private ImageIcon logoIcon;
            private boolean logoLoaded = false;
            
            {
                // Tải hình từ URL
                String imageUrl = "https://files.catbox.moe/v9l1kk.png";
                
                try {
                    java.net.URL url = new java.net.URL(imageUrl);
                    java.awt.image.BufferedImage img = javax.imageio.ImageIO.read(url);
                    logoIcon = new ImageIcon(img);
                    logoLoaded = true;
                    System.out.println("Đã tải logo từ URL: " + imageUrl);
                } catch (Exception e) {
                    System.err.println("Lỗi khi tải logo từ URL: " + e.getMessage());
                    
                    // Fallback: tải từ file local
                    String[] possiblePaths = {
                        "resources/maneki-neko.png",
                        "resources/maneki-neko.jpg",
                        "./resources/maneki-neko.png"
                    };
                    
                    for (String path : possiblePaths) {
                        java.io.File file = new java.io.File(path);
                        if (file.exists()) {
                            try {
                                logoIcon = new ImageIcon(path);
                                logoLoaded = true;
                                System.out.println("Đã tải logo từ file: " + path);
                                break;
                            } catch (Exception ex) {
                                System.err.println("Lỗi khi tải: " + path);
                            }
                        }
                    }
                }
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int x = (getWidth() - 80) / 2;
                int y = 10;
                
                if (logoLoaded && logoIcon != null) {
                    // Vẽ hình thực
                    Image img = logoIcon.getImage();
                    g2d.drawImage(img, x, y, 80, 80, this);
                } else {
                    // Fallback: vẽ logo đơn giản
                    g2d.setColor(new Color(255, 215, 0));
                    g2d.fillOval(x, y, 80, 80);
                    
                    g2d.setColor(new Color(0, 0, 0));
                    g2d.setStroke(new BasicStroke(3));
                    g2d.drawOval(x, y, 80, 80);
                    
                    g2d.setColor(new Color(255, 255, 255));
                    g2d.setFont(new Font("Arial", Font.BOLD, 14));
                    g2d.drawString("lucky", x + 15, y + 50);
                }
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(200, 100);
            }
        };
        logoPanel.setOpaque(false);
        centerPanel.add(logoPanel, gbc);
        
        // Tên ngân hàng
        gbc.gridy = 1;
        JLabel bankNameLabel = new JLabel("NGÂN HÀNG OOP");
        bankNameLabel.setFont(new Font("Arial", Font.BOLD, 24));
        bankNameLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        bankNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(bankNameLabel, gbc);
        
        // Mô tả
        gbc.gridy = 2;
        JLabel descLabel = new JLabel("Hệ thống quản lý ngân hàng");
        descLabel.setFont(new Font("Arial", Font.ITALIC, 13));
        descLabel.setForeground(ThemeColors.PRIMARY_LIGHT);
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(descLabel, gbc);
        
        // Khoảng trống
        gbc.gridy = 3;
        gbc.weighty = 0.2;
        gbc.gridwidth = 2;
        centerPanel.add(Box.createVerticalStrut(30), gbc);
        
        // Panel chứa form (để tạo khối riêng)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.insets = new Insets(8, 0, 8, 0);
        formGbc.fill = GridBagConstraints.HORIZONTAL;
        formGbc.gridwidth = 1;
        
        // Email
        formGbc.gridy = 0;
        JTextField emailField = new JTextField(20) {
            private String placeholder = "Nhập email của bạn";
            private boolean showingPlaceholder = true;
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (showingPlaceholder && getText().isEmpty()) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(new Color(150, 150, 150));
                    g2d.setFont(new Font("Arial", Font.ITALIC, 12));
                    g2d.drawString(placeholder, 12, 24);
                }
            }
        };
        emailField.setBackground(new Color(255, 255, 255));
        emailField.setForeground(ThemeColors.BG_DARK);
        emailField.setFont(new Font("Arial", Font.PLAIN, 13));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeColors.PRIMARY_LIGHT, 2),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        emailField.setPreferredSize(new Dimension(280, 36));
        
        // Xử lý focus event
        emailField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                ((JTextField)e.getComponent()).repaint();
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                ((JTextField)e.getComponent()).repaint();
            }
        });
        
        formPanel.add(emailField, formGbc);
        
        // Mật khẩu
        formGbc.gridy = 1;
        JPasswordField passwordField = new JPasswordField(20) {
            private String placeholder = "Nhập mật khẩu";
            private boolean showingPlaceholder = true;
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (showingPlaceholder && getPassword().length == 0) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(new Color(150, 150, 150));
                    g2d.setFont(new Font("Arial", Font.ITALIC, 12));
                    g2d.drawString(placeholder, 12, 24);
                }
            }
        };
        passwordField.setBackground(new Color(255, 255, 255));
        passwordField.setForeground(ThemeColors.BG_DARK);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 13));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeColors.PRIMARY_LIGHT, 2),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        passwordField.setPreferredSize(new Dimension(280, 36));
        
        // Xử lý focus event
        passwordField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                ((JPasswordField)e.getComponent()).repaint();
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                ((JPasswordField)e.getComponent()).repaint();
            }
        });
        
        formPanel.add(passwordField, formGbc);
        
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        centerPanel.add(formPanel, gbc);
        
        // Khoảng trống
        gbc.gridy = 5;
        gbc.weighty = 0.15;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        centerPanel.add(Box.createVerticalStrut(15), gbc);
        
        // Nút Đăng nhập (rộng hơn, kiểu modern)
        gbc.gridy = 6;
        gbc.weighty = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 40, 8, 40);
        JButton loginButton = new JButton("Đăng nhập");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setForeground(new Color(30, 30, 30));
        loginButton.setBackground(new Color(76, 175, 80));
        loginButton.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        loginButton.setOpaque(true);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        centerPanel.add(loginButton, gbc);
        
        // Nút Đăng ký
        gbc.gridy = 7;
        JButton registerButton = new JButton("Đăng ký");
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setForeground(new Color(30, 30, 30));
        registerButton.setBackground(new Color(255, 152, 0));
        registerButton.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        registerButton.setOpaque(true);
        registerButton.setFocusPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        centerPanel.add(registerButton, gbc);
        
        // Link "Đăng nhập tài khoản khác"
        gbc.gridy = 8;
        gbc.weighty = 0.3;
        gbc.insets = new Insets(10, 20, 10, 20);
        JLabel otherAccountLabel = new JLabel("Đăng nhập tài khoản khác?");
        otherAccountLabel.setForeground(new Color(100, 200, 255));
        otherAccountLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        otherAccountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        otherAccountLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        centerPanel.add(otherAccountLabel, gbc);

        loginPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Xử lý sự kiện nút Đăng nhập
        loginButton.addActionListener((ActionEvent e) -> {
            try {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                
                // Gọi AuthService
                currentUser = authService.login(email, password);
                
                // Nếu thành công
                updateDashboardInfo(); // Cập nhật thông tin
                cardLayout.show(mainPanel, "DASHBOARD"); // Chuyển màn hình
                
            } catch (Exception ex) {
                // Nếu thất bại, hiển thị lỗi
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi Đăng nhập", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Xử lý sự kiện nút Đăng ký
        registerButton.addActionListener((ActionEvent e) -> {
            // Hiển thị dialog với form đăng ký
            JDialog registerDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
                                                "Đăng ký khách hàng mới", true);
            registerDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            registerDialog.setSize(600, 600);
            registerDialog.setLocationRelativeTo(this);
            
            CustomerRegistrationView registrationView = new CustomerRegistrationView(db);
            registerDialog.add(registrationView);
            registerDialog.setVisible(true);
        });
    }

    /**
     * Tạo màn hình chính (Dashboard) sau khi đăng nhập
     */
    private void createDashboardPanel() {
        dashboardPanel = new JPanel(new BorderLayout(10, 10)); // Layout chính
        dashboardPanel.setBackground(ThemeColors.BG_DARK);
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ThemeColors.BG_LIGHT);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        welcomeLabel = UIUtils.createTitleLabel("Chào mừng!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        
        dashboardPanel.add(headerPanel, BorderLayout.NORTH);

        // Tạo các tab chức năng (sẽ được điền sau khi đăng nhập)
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(new Color(35, 35, 40));
        tabbedPane.setForeground(new Color(230, 230, 235));
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 13));
        
        // Điều chỉnh UI cho tab để tăng độ tương phản
        javax.swing.UIManager.put("TabbedPane.selected", new Color(70, 130, 180));
        javax.swing.UIManager.put("TabbedPane.selectedForeground", new Color(255, 255, 255));
        
        dashboardPanel.add(tabbedPane, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(ThemeColors.BG_LIGHT);
        
        JButton logoutButton = UIUtils.createStyledButton("Đăng xuất", ThemeColors.DANGER, ThemeColors.TEXT_PRIMARY);
        footerPanel.add(logoutButton);

        dashboardPanel.add(footerPanel, BorderLayout.SOUTH);

        // Xử lý sự kiện nút Đăng xuất
        logoutButton.addActionListener((e) -> {
            currentUser = null;
            cardLayout.show(mainPanel, "LOGIN");
        });
    }

    /**
     * Tạo panel "Lịch sử giao dịch"
     */
    private JPanel createHistoryPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(ThemeColors.BG_DARK);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Tiêu đề
        JLabel titleLabel = UIUtils.createSubtitleLabel("Lịch sử giao dịch");
        titleLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        JButton refreshButton = UIUtils.createStyledButton("Làm mới", ThemeColors.INFO, ThemeColors.TEXT_PRIMARY);

        JTextArea historyArea = new JTextArea(15, 50);
        historyArea.setEditable(false);
        historyArea.setBackground(ThemeColors.BG_LIGHT);
        historyArea.setForeground(ThemeColors.TEXT_PRIMARY);
        historyArea.setFont(new Font("Courier New", Font.PLAIN, 11));
        historyArea.setLineWrap(true);
        historyArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(historyArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeColors.BORDER, 1));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(ThemeColors.BG_DARK);
        topPanel.add(refreshButton, BorderLayout.WEST);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Xử lý sự kiện nút Làm mới
        refreshButton.addActionListener((e) -> {
            try {
                String accountId = currentUser.getAccountIds().get(0);
                List<Transaction> history = accountService.getTransactionHistory(accountId);
                historyArea.setText("");
                if (history.isEmpty()) {
                    historyArea.append("📭 Không có lịch sử giao dịch.");
                } else {
                    historyArea.append("═════════════════════════════════════════════\n");
                    historyArea.append("          LỊCH SỬ GIAO DỊCH\n");
                    historyArea.append("═════════════════════════════════════════════\n\n");
                    for (Transaction tx : history) {
                        historyArea.append(tx.toString() + "\n");
                        historyArea.append("─────────────────────────────────────────────\n");
                    }
                }
            } catch (Exception ex) {
                historyArea.setText("❌ Lỗi: " + ex.getMessage());
            }
        });

        // Load dữ liệu lần đầu
        refreshButton.doClick();

        return mainPanel;
    }

    /**
     * Tạo panel "Chuyển tiền" cho User
     */
    private JPanel createTransferPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(ThemeColors.BG_DARK);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Tiêu đề
        JLabel titleLabel = UIUtils.createSubtitleLabel("Chuyển tiền");
        titleLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Panel nội dung
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(ThemeColors.BG_DARK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel toAccLabel = new JLabel("Tài khoản nhận:");
        toAccLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        contentPanel.add(toAccLabel, gbc);
        gbc.gridx = 1;
        JTextField toAccountField = new JTextField(20);
        toAccountField.setText("ACC002");
        toAccountField.setBackground(ThemeColors.BG_LIGHT);
        toAccountField.setForeground(ThemeColors.TEXT_PRIMARY);
        contentPanel.add(toAccountField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel amountLabel = new JLabel("Số tiền:");
        amountLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        contentPanel.add(amountLabel, gbc);
        gbc.gridx = 1;
        JTextField amountField = new JTextField(20);
        amountField.setBackground(ThemeColors.BG_LIGHT);
        amountField.setForeground(ThemeColors.TEXT_PRIMARY);
        contentPanel.add(amountField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel contentLabel = new JLabel("Nội dung:");
        contentLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        contentPanel.add(contentLabel, gbc);
        gbc.gridx = 1;
        JTextField contentField = new JTextField(20);
        contentField.setText("Chuyen tien");
        contentField.setBackground(ThemeColors.BG_LIGHT);
        contentField.setForeground(ThemeColors.TEXT_PRIMARY);
        contentPanel.add(contentField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton transferButton = UIUtils.createStyledButton("✓ Thực hiện chuyển tiền", ThemeColors.SUCCESS, ThemeColors.TEXT_PRIMARY);
        contentPanel.add(transferButton, gbc);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Xử lý sự kiện nút Chuyển tiền
        transferButton.addActionListener((e) -> {
            try {
                String fromAccountId = currentUser.getAccountIds().get(0);
                String toAccountId = toAccountField.getText();
                double amount = Double.parseDouble(amountField.getText());
                String content = contentField.getText();

                accountService.transfer(fromAccountId, toAccountId, amount, content);
                
                JOptionPane.showMessageDialog(this, "✅ Chuyển tiền thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                amountField.setText("");
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "❌ Lỗi: Số tiền không hợp lệ.", "Lỗi Dữ liệu", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "❌ Lỗi Giao dịch", JOptionPane.ERROR_MESSAGE);
            }
        });

        return mainPanel;
    }

    /**
     * Cập nhật thông tin trên Dashboard (số dư, lịch sử)
     */
    private void updateDashboardInfo() {
        if (currentUser == null) return;

        try {
            // Cập nhật tên chào mừng
            welcomeLabel.setText("Chào mừng, " + currentUser.getFullName() + 
                               (currentUser.getRole() == User.UserRole.ADMIN ? " (Quản trị viên)" : " (Khách hàng)"));

            // Clear all existing tabs
            tabbedPane.removeAll();

            // Nếu là ADMIN
            if (currentUser.getRole() == User.UserRole.ADMIN) {
                AdminDashboard adminDashboard = new AdminDashboard(db, currentUser, accountService);
                tabbedPane.addTab("Bảng điều khiển", adminDashboard);
                
                ReportView reportView = new ReportView(db, currentUser);
                tabbedPane.addTab("Xuất báo cáo", reportView);
                
                BackupView backupView = new BackupView(db);
                tabbedPane.addTab("Sao lưu", backupView);
                
                SystemSettingsView settingsView = new SystemSettingsView();
                tabbedPane.addTab("Cài đặt", settingsView);
            } else {
                // Nếu là CUSTOMER
                bankNapPanel = new BankNap(db, currentUser, accountService);
                tabbedPane.addTab("Nạp tiền", bankNapPanel);
                
                tabbedPane.addTab("Chuyển tiền", createTransferPanel());
                
                bankLoanPanel = new BankLoan(db, currentUser, accountService);
                tabbedPane.addTab("Vay tiền", bankLoanPanel);
                
                CreditCardManagement creditCardPanel = new CreditCardManagement(db, currentUser, accountService);
                tabbedPane.addTab("Thẻ tín dụng", creditCardPanel);
                
                // Thêm tab lịch sử giao dịch
                tabbedPane.addTab("Lịch sử giao dịch", createHistoryPanel());
                
                // Thêm tab xuất báo cáo cho khách hàng
                ReportView reportView = new ReportView(db, currentUser);
                tabbedPane.addTab("Báo cáo", reportView);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không thể tải dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Nạp dữ liệu mẫu (Giống trong file Main.java cũ)
     */
    private static void setupSampleData() {
        // Tạo User 1 (Alice)
        User alice = new User("CUS001", "Nguyen Van A", new Date(), "001234567890", 
                            "0912345678", "alice@bank.com", "123 Duong ABC", 
                            User.UserRole.CUSTOMER, "123");
        db.saveUser(alice);

        // Tạo User 2 (Bob)
        User bob = new User("CUS002", "Tran Thi B", new Date(), "001234567891", 
                          "0912345679", "bob@bank.com", "456 Duong XYZ", 
                          User.UserRole.CUSTOMER, "123");
        db.saveUser(bob);
        
        // Tạo User 3 (Admin)
         User admin = new User("ADM001", "Quan Tri Vien", new Date(), "000000000000", 
                          "0900000000", "admin@gmail.com", "Ngan Hang", 
                          User.UserRole.ADMIN, "admin");
        db.saveUser(admin);

        // Tạo Tài khoản cho Alice
        Account accAlice = new Account("ACC001", alice.getCustomerId(), 50000000);
        alice.addAccountId(accAlice.getAccountNumber());
        db.saveAccount(accAlice);

        // Tạo Tài khoản cho Bob
        Account accBob = new Account("ACC002", bob.getCustomerId(), 10000000);
        bob.addAccountId(accBob.getAccountNumber());
        db.saveAccount(accBob);
        
        // Tạo Thẻ cho Alice
        // Thẻ Debit
        Date expiry = new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(365 * 3)); // Hết hạn sau 3 năm
        Card debitAlice = new DebitCard("CARD001", accAlice.getAccountNumber(), new Date(), expiry);
        accAlice.addCardId(debitAlice.getCardNumber());
        db.saveCard(debitAlice);
        
        // Thẻ Credit
        Card creditAlice = new CreditCard("CARD002", accAlice.getAccountNumber(), new Date(), expiry, 20000000); // Hạn mức 20tr
        accAlice.addCardId(creditAlice.getCardNumber());
        db.saveCard(creditAlice);
        
        System.out.println("Dữ liệu mẫu đã được nạp.");
        System.out.println("\n========== HƯỚNG DẪN ĐĂNG NHẬP ==========");
        System.out.println("👤 Tài khoản ADMIN:");
        System.out.println("   Email: admin@bank.com");
        System.out.println("   Mật khẩu: admin");
        System.out.println("\n👤 Tài khoản KHÁCH HÀNG:");
        System.out.println("   Email: alice@bank.com / Mật khẩu: 123");
        System.out.println("   Email: bob@bank.com / Mật khẩu: 123");
        System.out.println("========================================\n");
    }

    /**
     * Phương thức main để khởi chạy ứng dụng GUI
     */
    public static void main(String[] args) {
        // Chạy ứng dụng Swing trên Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            BankGUI app = new BankGUI();
            app.setVisible(true);
        });
    }
}
