package com.superli.deliveries.dto;

public class DriverDTO {
    private int id;
    private String fullName;
    private String licenseType;

    public DriverDTO() {}

    public DriverDTO(int id, String fullName, String licenseType) {
        this.id = id;
        this.fullName = fullName;
        this.licenseType = licenseType;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getLicenseType() { return licenseType; }
    public void setLicenseType(String licenseType) { this.licenseType = licenseType; }
}
