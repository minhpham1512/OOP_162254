package com.bank.service;

import java.io.*;
import java.util.*;

/**
 * Lớp SystemSettingsService - Quản lý cài đặt hệ thống
 * Cung cấp các chức năng:
 * - Quản lý cài đặt hệ thống (tỷ lệ lãi suất, phí giao dịch, v.v.)
 * - Lưu/tải cài đặt từ file
 * - Cảnh báo hệ thống
 * - Nhật ký hoạt động
 */
public class SystemSettingsService {
    private static final String SETTINGS_DIR = System.getProperty("user.home") + File.separator + "BankSettings";
    private static final String SETTINGS_FILE = SETTINGS_DIR + File.separator + "system.properties";
    private static final String LOG_DIR = System.getProperty("user.home") + File.separator + "BankLogs";
    
    private Properties settings;
    private static final String LOG_FILE = LOG_DIR + File.separator + "system.log";

    public SystemSettingsService() {
        // Tạo các thư mục nếu chưa tồn tại
        new File(SETTINGS_DIR).mkdirs();
        new File(LOG_DIR).mkdirs();

        // Tải cài đặt từ file, nếu không có thì tạo mặc định
        loadSettings();
    }

    /**
     * Tải cài đặt từ file
     */
    private void loadSettings() {
        settings = new Properties();
        File settingsFile = new File(SETTINGS_FILE);

        if (settingsFile.exists()) {
            try (FileInputStream fis = new FileInputStream(settingsFile)) {
                settings.load(fis);
            } catch (IOException e) {
                initializeDefaultSettings();
            }
        } else {
            initializeDefaultSettings();
        }
    }

    /**
     * Khởi tạo cài đặt mặc định
     */
    private void initializeDefaultSettings() {
        settings.clear();

        // Cài đặt lãi suất
        settings.setProperty("savings.interest.rate", "3.5");      // 3.5%/năm
        settings.setProperty("loan.interest.rate", "8.5");          // 8.5%/năm
        settings.setProperty("overdraft.interest.rate", "12.0");    // 12.0%/năm

        // Cài đặt phí
        settings.setProperty("transfer.fee", "5000");               // 5,000 VND
        settings.setProperty("withdrawal.fee", "0");                // Miễn phí
        settings.setProperty("card.annual.fee", "100000");          // 100,000 VND/năm

        // Cài đặt giới hạn
        settings.setProperty("withdrawal.daily.limit", "10000000"); // 10M VND/ngày
        settings.setProperty("transfer.daily.limit", "50000000");   // 50M VND/ngày
        settings.setProperty("account.min.balance", "0");           // Số dư tối thiểu

        // Cài đặt hệ thống
        settings.setProperty("system.maintenance.mode", "false");
        settings.setProperty("system.language", "vi_VN");
        settings.setProperty("system.timezone", "Asia/Ho_Chi_Minh");
        settings.setProperty("system.max.users", "100000");

        // Cài đặt bảo mật
        settings.setProperty("security.password.min.length", "6");
        settings.setProperty("security.failed.login.attempts", "5");
        settings.setProperty("security.lockout.duration.minutes", "30");

        // Cài đặt thông báo
        settings.setProperty("notification.email.enabled", "true");
        settings.setProperty("notification.sms.enabled", "false");
        settings.setProperty("notification.transaction.enabled", "true");

        saveSettings();
    }

    /**
     * Lưu cài đặt vào file
     */
    public String saveSettings() {
        try (FileOutputStream fos = new FileOutputStream(SETTINGS_FILE)) {
            settings.store(fos, "Bank System Settings - Last updated: " + new Date());
            logActivity("SYSTEM_SETTINGS_UPDATED", "Cài đặt hệ thống được cập nhật");
            return "✓ Cài đặt đã được lưu thành công";
        } catch (IOException e) {
            return "✗ Lỗi lưu cài đặt: " + e.getMessage();
        }
    }

    /**
     * Lấy giá trị cài đặt
     */
    public String getSetting(String key) {
        return settings.getProperty(key, "");
    }

    /**
     * Thiết lập giá trị
     */
    public String setSetting(String key, String value) {
        settings.setProperty(key, value);
        logActivity("SETTING_CHANGED", String.format("%s = %s", key, value));
        return saveSettings();
    }

    /**
     * Lấy tất cả cài đặt dưới dạng chuỗi
     */
    public String getAllSettings() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("      CÀI ĐẶT HỆ THỐNG NGÂN HÀNG\n");
        sb.append("═══════════════════════════════════════\n\n");

        sb.append("CÀI ĐẶT LÃI SUẤT:\n");
        sb.append(String.format("  • Lãi suất tiết kiệm: %.2f%%/năm\n", 
            Double.parseDouble(settings.getProperty("savings.interest.rate", "3.5"))));
        sb.append(String.format("  • Lãi suất vay: %.2f%%/năm\n", 
            Double.parseDouble(settings.getProperty("loan.interest.rate", "8.5"))));
        sb.append(String.format("  • Lãi suất thấu chi: %.2f%%/năm\n\n", 
            Double.parseDouble(settings.getProperty("overdraft.interest.rate", "12.0"))));

        sb.append("💳 CÀI ĐẶT PHÍ:\n");
        sb.append(String.format("  • Phí chuyển tiền: %,.0f VND\n", 
            Double.parseDouble(settings.getProperty("transfer.fee", "5000"))));
        sb.append(String.format("  • Phí rút tiền: %,.0f VND\n", 
            Double.parseDouble(settings.getProperty("withdrawal.fee", "0"))));
        sb.append(String.format("  • Phí thẻ năm: %,.0f VND\n\n", 
            Double.parseDouble(settings.getProperty("card.annual.fee", "100000"))));

        sb.append("⚙️ GI	ỚI HẠN:\n");
        sb.append(String.format("  • Giới hạn rút tiền/ngày: %,.0f VND\n", 
            Double.parseDouble(settings.getProperty("withdrawal.daily.limit", "10000000"))));
        sb.append(String.format("  • Giới hạn chuyển tiền/ngày: %,.0f VND\n", 
            Double.parseDouble(settings.getProperty("transfer.daily.limit", "50000000"))));
        sb.append(String.format("  • Số dư tối thiểu: %,.0f VND\n\n", 
            Double.parseDouble(settings.getProperty("account.min.balance", "0"))));

        sb.append("🔧 CÀI ĐẶT HỆ THỐNG:\n");
        sb.append(String.format("  • Chế độ bảo trì: %s\n", 
            settings.getProperty("system.maintenance.mode", "false")));
        sb.append(String.format("  • Ngôn ngữ: %s\n", 
            settings.getProperty("system.language", "vi_VN")));
        sb.append(String.format("  • Múi giờ: %s\n", 
            settings.getProperty("system.timezone", "Asia/Ho_Chi_Minh")));
        sb.append(String.format("  • Tối đa người dùng: %s\n\n", 
            settings.getProperty("system.max.users", "100000")));

        sb.append("🔐 CÀI ĐẶT BẢO MẬT:\n");
        sb.append(String.format("  • Độ dài mật khẩu tối thiểu: %s ký tự\n", 
            settings.getProperty("security.password.min.length", "6")));
        sb.append(String.format("  • Lần đăng nhập sai cho phép: %s lần\n", 
            settings.getProperty("security.failed.login.attempts", "5")));
        sb.append(String.format("  • Thời gian khóa: %s phút\n\n", 
            settings.getProperty("security.lockout.duration.minutes", "30")));

        sb.append("📬 CÀI ĐẶT THÔNG BÁO:\n");
        sb.append(String.format("  • Email: %s\n", 
            settings.getProperty("notification.email.enabled", "true")));
        sb.append(String.format("  • SMS: %s\n", 
            settings.getProperty("notification.sms.enabled", "false")));
        sb.append(String.format("  • Thông báo giao dịch: %s\n", 
            settings.getProperty("notification.transaction.enabled", "true")));

        return sb.toString();
    }

    /**
     * Ghi nhật ký hoạt động
     */
    public void logActivity(String category, String message) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            String timestamp = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
            bw.write(String.format("[%s] %s: %s\n", timestamp, category, message));
        } catch (IOException e) {
            System.err.println("Lỗi ghi nhật ký: " + e.getMessage());
        }
    }

    /**
     * Lấy nhật ký hoạt động gần đây
     */
    public String getRecentLogs(int lines) {
        StringBuilder sb = new StringBuilder();
        List<String> logLines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(LOG_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                logLines.add(line);
            }
        } catch (FileNotFoundException e) {
            return "Chưa có nhật ký nào.";
        } catch (IOException e) {
            return "Lỗi đọc nhật ký: " + e.getMessage();
        }

        sb.append("═══════════════════════════════════════\n");
        sb.append("        NHẬT KÝ HOẠT ĐỘNG HỆ THỐNG\n");
        sb.append("═══════════════════════════════════════\n\n");

        int startIndex = Math.max(0, logLines.size() - lines);
        for (int i = startIndex; i < logLines.size(); i++) {
            sb.append(logLines.get(i)).append("\n");
        }

        return sb.toString();
    }

    /**
     * Kiểm tra chế độ bảo trì
     */
    public boolean isMaintenanceMode() {
        return Boolean.parseBoolean(settings.getProperty("system.maintenance.mode", "false"));
    }

    /**
     * Bật/tắt chế độ bảo trì
     */
    public String setMaintenanceMode(boolean enabled) {
        setSetting("system.maintenance.mode", String.valueOf(enabled));
        String status = enabled ? "BẬT" : "TẮT";
        logActivity("MAINTENANCE_MODE", "Chế độ bảo trì được " + status);
        return "✓ Chế độ bảo trì đã được " + status;
    }

    /**
     * Thống kê hệ thống
     */
    public String getSystemStats() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("       THỐNG KÊ HỆ THỐNG\n");
        sb.append("═══════════════════════════════════════\n\n");

        sb.append("📂 ĐỦ DUNG LƯỢNG:\n");
        sb.append(String.format("  • Cài đặt: %s\n", SETTINGS_DIR));
        sb.append(String.format("  • Nhật ký: %s\n", LOG_DIR));
        sb.append(String.format("  • Sao lưu: %s\n\n", BackupService.getBackupDirectory()));

        sb.append("⏰ TRẠNG THÁI:\n");
        sb.append(String.format("  • Chế độ bảo trì: %s\n", 
            isMaintenanceMode() ? "BẬT ⚠️" : "TẮT ✓"));
        sb.append(String.format("  • Ngôn ngữ: %s\n", 
            settings.getProperty("system.language", "vi_VN")));
        sb.append(String.format("  • Múi giờ: %s\n", 
            settings.getProperty("system.timezone", "Asia/Ho_Chi_Minh")));

        sb.append("\n📊 GIỚI HẠN:\n");
        sb.append(String.format("  • Tối đa người dùng: %s\n", 
            settings.getProperty("system.max.users", "100000")));

        return sb.toString();
    }

    /**
     * Reset cài đặt về mặc định
     */
    public String resetToDefault() {
        initializeDefaultSettings();
        logActivity("SETTINGS_RESET", "Cài đặt hệ thống được reset về mặc định");
        return "✓ Cài đặt đã được reset về mặc định";
    }

    /**
     * Xuất cài đặt ra file
     */
    public String exportSettings(String filePath) throws IOException {
        try (FileWriter fw = new FileWriter(filePath)) {
            fw.write(getAllSettings());
            logActivity("SETTINGS_EXPORTED", "Cài đặt được xuất: " + filePath);
            return "✓ Cài đặt đã được xuất thành công";
        }
    }
}
