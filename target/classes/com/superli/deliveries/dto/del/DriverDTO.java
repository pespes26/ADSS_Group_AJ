package com.superli.deliveries.dto.del;

public class DriverDTO {
    private String id;
    private String fullName;
    private String licenseType;
    private boolean available;

    public DriverDTO() {}

    public DriverDTO(String id, String fullName, String licenseType) {
        this.id = id;
        this.fullName = fullName;
        this.licenseType = licenseType;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getLicenseType() { return licenseType; }
    public void setLicenseType(String licenseType) { this.licenseType = licenseType; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}
