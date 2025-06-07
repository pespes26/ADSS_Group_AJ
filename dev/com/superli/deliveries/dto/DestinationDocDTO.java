package com.superli.deliveries.dto;

public class DestinationDocDTO {
    private int id;
    private int transportId;
    private int siteId;
    private String status;

    public DestinationDocDTO() {}

    public DestinationDocDTO(int id, int transportId, int siteId, String status) {
        this.id = id;
        this.transportId = transportId;
        this.siteId = siteId;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTransportId() { return transportId; }
    public void setTransportId(int transportId) { this.transportId = transportId; }

    public int getSiteId() { return siteId; }
    public void setSiteId(int siteId) { this.siteId = siteId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}