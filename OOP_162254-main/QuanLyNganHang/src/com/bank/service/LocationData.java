package com.bank.service;

import java.util.*;

/**
 * Lớp LocationData - Quản lý dữ liệu địa chỉ Việt Nam
 * Cung cấp danh sách Tỉnh/Thành, Quận/Huyện, Phường/Xã
 */
public class LocationData {
    private static final Map<String, List<String>> PROVINCES_DISTRICTS = new HashMap<>();
    private static final Map<String, List<String>> DISTRICTS_WARDS = new HashMap<>();

    static {
        initializeProvinces();
    }

    /**
     * Khởi tạo dữ liệu địa chỉ Việt Nam
     */
    private static void initializeProvinces() {
        // Hà Nội
        PROVINCES_DISTRICTS.put("Hà Nội", Arrays.asList(
            "Ba Đình", "Hoàn Kiếm", "Tây Hồ", "Cầu Giấy", "Đống Đa", 
            "Hai Bà Trưng", "Hoàng Mai", "Long Biên", "Thanh Xuân", "Sóc Sơn"
        ));
        DISTRICTS_WARDS.put("Hà Nội-Ba Đình", Arrays.asList(
            "Phường Trúc Bạch", "Phường Cống Vị", "Phường Nguyễn Trường Tộ",
            "Phường Phúc Tân", "Phường Quán Thánh", "Phường Đội Cấn"
        ));
        DISTRICTS_WARDS.put("Hà Nội-Hoàn Kiếm", Arrays.asList(
            "Phường Hàng Bạc", "Phường Hàng Gai", "Phường Hàng Mắm",
            "Phường Chương Dương", "Phường Cửa Đông", "Phường Cửa Nam"
        ));
        DISTRICTS_WARDS.put("Hà Nội-Tây Hồ", Arrays.asList(
            "Phường Quảng An", "Phường Tây Hồ", "Phường Hà Nội",
            "Phường Nhật Tân", "Phường Kim Mã", "Phường Thục Lâm"
        ));
        DISTRICTS_WARDS.put("Hà Nội-Cầu Giấy", Arrays.asList(
            "Phường Dịch Vọng", "Phường Trung Hoà", "Phường Yên Hoà",
            "Phường Quang Trung", "Phường Láng Thượng", "Phường Nghĩa Đô"
        ));
        DISTRICTS_WARDS.put("Hà Nội-Đống Đa", Arrays.asList(
            "Phường Quốc Tử Giám", "Phường Thanh Nhan", "Phường Kim Liên",
            "Phường Liễu Giai", "Phường Láng Hạ", "Phường Thợ Nhuộm"
        ));
        DISTRICTS_WARDS.put("Hà Nội-Hai Bà Trưng", Arrays.asList(
            "Phường Bạch Đằng", "Phường Định Công", "Phường Hàng Bột",
            "Phường Chợ DComuna", "Phường Hoàng Liệt", "Phường Máy Chiếc"
        ));
        DISTRICTS_WARDS.put("Hà Nội-Hoàng Mai", Arrays.asList(
            "Phường Hoàng Liệt", "Phường Bồ Đề", "Phường Giáp Bát",
            "Phường Thanh Trì", "Phường Vĩnh Hưng", "Phường Đại Kim"
        ));
        DISTRICTS_WARDS.put("Hà Nội-Long Biên", Arrays.asList(
            "Phường Việt Hùng", "Phường Sài Đồng", "Phường Gia Thụy",
            "Phường Đông Hương", "Phường Thạch Bàn", "Phường Ngũ Hiệp"
        ));
        DISTRICTS_WARDS.put("Hà Nội-Thanh Xuân", Arrays.asList(
            "Phường Thanh Xuân Bắc", "Phường Thanh Xuân Trung", "Phường Thanh Xuân Nam",
            "Phường Khương Đình", "Phường Khương Thượng", "Phường Hạ Đình"
        ));
        DISTRICTS_WARDS.put("Hà Nội-Sóc Sơn", Arrays.asList(
            "Phường Sóc Sơn", "Phường Hoa Lư", "Phường Tân Lập",
            "Phường Hồng Hà", "Phường Thạch Thất", "Phường Minh Phú"
        ));

        // TP.HCM
        PROVINCES_DISTRICTS.put("TP. Hồ Chí Minh", Arrays.asList(
            "Quận 1", "Quận 2", "Quận 3", "Quận 4", "Quận 5", "Quận 6", "Quận 7",
            "Quận 8", "Quận 9", "Quận 10", "Quận 11", "Quận 12", "Thủ Đức", "Bình Thạnh"
        ));
        DISTRICTS_WARDS.put("TP. Hồ Chí Minh-Quận 1", Arrays.asList(
            "Phường Bến Nghé", "Phường Nguyễn Huệ", "Phường Tân Định",
            "Phường Đa Kao", "Phường Cầu Ông Lãnh"
        ));
        DISTRICTS_WARDS.put("TP. Hồ Chí Minh-Quận 2", Arrays.asList(
            "Phường An Phú", "Phường Bình An", "Phường Cát Lái",
            "Phường Thảo Điền", "Phường An Khánh"
        ));
        DISTRICTS_WARDS.put("TP. Hồ Chí Minh-Quận 3", Arrays.asList(
            "Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 5",
            "Phường 6", "Phường 7", "Phường 8", "Phường 9", "Phường 10", "Phường 11", "Phường 12", "Phường 13"
        ));
        DISTRICTS_WARDS.put("TP. Hồ Chí Minh-Quận 4", Arrays.asList(
            "Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 5",
            "Phường 6", "Phường 8", "Phường 9", "Phường 10", "Phường 13", "Phường 14", "Phường 15"
        ));
        DISTRICTS_WARDS.put("TP. Hồ Chí Minh-Quận 5", Arrays.asList(
            "Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 5",
            "Phường 6", "Phường 7", "Phường 8", "Phường 9", "Phường 10", "Phường 11", "Phường 12", "Phường 13", "Phường 14"
        ));
        DISTRICTS_WARDS.put("TP. Hồ Chí Minh-Quận 6", Arrays.asList(
            "Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 5",
            "Phường 6", "Phường 7", "Phường 8", "Phường 9", "Phường 10", "Phường 11", "Phường 12"
        ));
        DISTRICTS_WARDS.put("TP. Hồ Chí Minh-Quận 7", Arrays.asList(
            "Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 5",
            "Phường 6", "Phường 7", "Phường 8", "Phường 9", "Phường 10", "Phường 11", "Phường 12", "Phường 13"
        ));
        DISTRICTS_WARDS.put("TP. Hồ Chí Minh-Quận 8", Arrays.asList(
            "Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 5",
            "Phường 6", "Phường 7", "Phường 8", "Phường 9", "Phường 10", "Phường 11", "Phường 12", "Phường 13", "Phường 14"
        ));
        DISTRICTS_WARDS.put("TP. Hồ Chí Minh-Quận 9", Arrays.asList(
            "Phường An Phú", "Phường Phước Bình", "Phường Phước Long B",
            "Phường Trường Thạnh", "Phường Tăng Nhơn Phú A", "Phường Tăng Nhơn Phú B"
        ));
        DISTRICTS_WARDS.put("TP. Hồ Chí Minh-Quận 10", Arrays.asList(
            "Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 5",
            "Phường 6", "Phường 7", "Phường 8", "Phường 9", "Phường 10", "Phường 11", "Phường 12", "Phường 13", "Phường 14", "Phường 15"
        ));
        DISTRICTS_WARDS.put("TP. Hồ Chí Minh-Quận 11", Arrays.asList(
            "Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 5",
            "Phường 6", "Phường 7", "Phường 8", "Phường 9", "Phường 10", "Phường 11", "Phường 12", "Phường 13", "Phường 14"
        ));
        DISTRICTS_WARDS.put("TP. Hồ Chí Minh-Quận 12", Arrays.asList(
            "Phường An Phú Đông", "Phường Hiệp Thành", "Phường Thạnh Lộc",
            "Phường Tân Hưng Thuận", "Phường Tân Thới Nhất", "Phường Tân Thới Hiệp"
        ));
        DISTRICTS_WARDS.put("TP. Hồ Chí Minh-Thủ Đức", Arrays.asList(
            "Phường Linh Chiểu", "Phường Linh Đông", "Phường Linh Xuân",
            "Phường Tam Phú", "Phường Thạnh Mỹ Lợi", "Phường Thảo Điền"
        ));
        DISTRICTS_WARDS.put("TP. Hồ Chí Minh-Bình Thạnh", Arrays.asList(
            "Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 5",
            "Phường 6", "Phường 7", "Phường 8", "Phường 9", "Phường 10", "Phường 11", "Phường 12", "Phường 13", "Phường 14"
        ));

        // Đà Nẵng
        PROVINCES_DISTRICTS.put("Đà Nẵng", Arrays.asList(
            "Quận Hải Châu", "Quận Thanh Khê", "Quận Sơn Trà", 
            "Quận Ngũ Hành Sơn", "Quận Liên Chiểu"
        ));
        DISTRICTS_WARDS.put("Đà Nẵng-Quận Hải Châu", Arrays.asList(
            "Phường Hải Châu 1", "Phường Hải Châu 2", "Phường Thanh Bình",
            "Phường Thạch Thang", "Phường Hòa Cường Bắc", "Phường Hòa Cường Nam"
        ));
        DISTRICTS_WARDS.put("Đà Nẵng-Quận Thanh Khê", Arrays.asList(
            "Phường Thanh Khê Tây", "Phường Thanh Khê Đông",
            "Phường Chính Gián", "Phường Tân Chính"
        ));
        DISTRICTS_WARDS.put("Đà Nẵng-Quận Sơn Trà", Arrays.asList(
            "Phường An Hải Bắc", "Phường An Hải Tây",
            "Phường Mỹ An", "Phường Nại Hiên Đông"
        ));
        DISTRICTS_WARDS.put("Đà Nẵng-Quận Ngũ Hành Sơn", Arrays.asList(
            "Phường Mỹ Khê", "Phường Khuê Mỹ"
        ));
        DISTRICTS_WARDS.put("Đà Nẵng-Quận Liên Chiểu", Arrays.asList(
            "Phường Liên Chiểu", "Phường Hòa Minh"
        ));

        // Hải Phòng
        PROVINCES_DISTRICTS.put("Hải Phòng", Arrays.asList(
            "Quận Hồng Bàng", "Quận Ngô Quyền", "Quận Lê Chân",
            "Quận Kiến An", "Quận Đồ Sơn", "Quận Cát Hải"
        ));
        DISTRICTS_WARDS.put("Hải Phòng-Quận Hồng Bàng", Arrays.asList(
            "Phường Máy Chai", "Phường Máy Tơ", "Phường Quán Toan",
            "Phường Phạm Văn Đồng", "Phường Phan Bội Châu"
        ));
        DISTRICTS_WARDS.put("Hải Phòng-Quận Ngô Quyền", Arrays.asList(
            "Phường Hòa Nghĩa", "Phường Minh Khai",
            "Phường Hàng Kênh", "Phường Vân Đồn"
        ));
        DISTRICTS_WARDS.put("Hải Phòng-Quận Lê Chân", Arrays.asList(
            "Phường Cát Dài", "Phường Cát Bi", "Phường Thái Phiên"
        ));
        DISTRICTS_WARDS.put("Hải Phòng-Quận Kiến An", Arrays.asList(
            "Phường Yên Phong", "Phường Kiến An", "Phường Tràng Cát"
        ));
        DISTRICTS_WARDS.put("Hải Phòng-Quận Đồ Sơn", Arrays.asList(
            "Phường Đồ Sơn"
        ));
        DISTRICTS_WARDS.put("Hải Phòng-Quận Cát Hải", Arrays.asList(
            "Phường Cát Hải"
        ));
    }

    /**
     * Lấy danh sách tỉnh/thành phố
     */
    public static List<String> getProvinces() {
        return new ArrayList<>(PROVINCES_DISTRICTS.keySet());
    }

    /**
     * Lấy danh sách quận/huyện theo tỉnh
     */
    public static List<String> getDistricts(String province) {
        return PROVINCES_DISTRICTS.getOrDefault(province, new ArrayList<>());
    }

    /**
     * Lấy danh sách phường/xã theo quận/huyện
     */
    public static List<String> getWards(String province, String district) {
        String key = province + "-" + district;
        return DISTRICTS_WARDS.getOrDefault(key, new ArrayList<>());
    }

    /**
     * Validate địa chỉ đầy đủ
     */
    public static boolean isValidLocation(String province, String district, String ward) {
        if (!PROVINCES_DISTRICTS.containsKey(province)) {
            return false;
        }
        
        List<String> districts = PROVINCES_DISTRICTS.get(province);
        if (!districts.contains(district)) {
            return false;
        }
        
        String key = province + "-" + district;
        List<String> wards = DISTRICTS_WARDS.get(key);
        return wards != null && wards.contains(ward);
    }
}
