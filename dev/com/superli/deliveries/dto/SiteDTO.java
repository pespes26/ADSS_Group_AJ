package com.superli.deliveries.dto;

public class SiteDTO {
    private int SiteId;
    private String address;
    private String contact;
    private String zoneId;

    public SiteDTO() {}

    public SiteDTO(int SiteId, String address, String contact, String zoneId) {
        this.SiteId = SiteId;
        this.address = address;
        this.contact = contact;
        this.zoneId = zoneId;
    }

    public int getSiteId() { return SiteId; }
    public void setId(int SiteId) { this.SiteId = SiteId; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getZoneId() { return zoneId; }
    public void setZoneId(String zoneId) { this.zoneId = zoneId; }
}
