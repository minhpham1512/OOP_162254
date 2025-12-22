package com.bank.model;

/**
 * Lớp Address - Đại diện cho địa chỉ Việt Nam
 * Gồm: Tỉnh/Thành phố, Quận/Huyện, Phường/Xã, Chi tiết địa chỉ
 */
public class Address {
    private String province;      // Tỉnh/Thành phố
    private String district;      // Quận/Huyện
    private String ward;          // Phường/Xã
    private String detail;        // Chi tiết địa chỉ (số nhà, đường)

    public Address(String province, String district, String ward, String detail) {
        this.province = province;
        this.district = district;
        this.ward = ward;
        this.detail = detail;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    /**
     * Lấy địa chỉ đầy đủ
     */
    public String getFullAddress() {
        return detail + ", " + ward + ", " + district + ", " + province;
    }

    @Override
    public String toString() {
        return getFullAddress();
    }
}
