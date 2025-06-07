package com.superli.deliveries.dto;

public class SiteDTO {
    private int id;
    private String address;
    private String contact;

    public SiteDTO() {}

    public SiteDTO(int id, String address, String contact) {
        this.id = id;
        this.address = address;
        this.contact = contact;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
}
