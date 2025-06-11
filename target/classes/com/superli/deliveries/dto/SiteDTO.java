package com.superli.deliveries.dto;

public class SiteDTO {
    private String siteId;
    private String address;
    private String phoneNumber;
    private String contactPersonName;
    private String zoneId;

    public SiteDTO() {}

    public SiteDTO(String siteId, String address, String phoneNumber, String contactPersonName, String zoneId) {
        this.siteId = siteId;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.contactPersonName = contactPersonName;
        this.zoneId = zoneId;
    }

    public String getSiteId() { return siteId; }
    public void setSiteId(String siteId) { this.siteId = siteId; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getContactPersonName() { return contactPersonName; }
    public void setContactPersonName(String contactPersonName) { this.contactPersonName = contactPersonName; }

    public String getZoneId() { return zoneId; }
    public void setZoneId(String zoneId) { this.zoneId = zoneId; }

    @Override
    public String toString() {
        return "SiteDTO{" +
                "siteId='" + siteId + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", contactPersonName='" + contactPersonName + '\'' +
                ", zoneId='" + zoneId + '\'' +
                '}';
    }
}
