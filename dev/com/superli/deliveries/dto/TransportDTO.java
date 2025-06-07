package com.superli.deliveries.dto;

public class TransportDTO {
    private int id;
    private String departureDateTime;
    private int truckId;
    private int driverId;
    private int originSiteId;
    private float departureWeight;
    private String status;

    public TransportDTO() {}

    public TransportDTO(int id, String departureDateTime, int truckId, int driverId,
                        int originSiteId, float departureWeight, String status) {
        this.id = id;
        this.departureDateTime = departureDateTime;
        this.truckId = truckId;
        this.driverId = driverId;
        this.originSiteId = originSiteId;
        this.departureWeight = departureWeight;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDepartureDateTime() { return departureDateTime; }
    public void setDepartureDateTime(String departureDateTime) { this.departureDateTime = departureDateTime; }

    public int getTruckId() { return truckId; }
    public void setTruckId(int truckId) { this.truckId = truckId; }

    public int getDriverId() { return driverId; }
    public void setDriverId(int driverId) { this.driverId = driverId; }

    public int getOriginSiteId() { return originSiteId; }
    public void setOriginSiteId(int originSiteId) { this.originSiteId = originSiteId; }

    public float getDepartureWeight() { return departureWeight; }
    public void setDepartureWeight(float departureWeight) { this.departureWeight = departureWeight; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
