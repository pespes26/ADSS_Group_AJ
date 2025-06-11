package com.superli.deliveries.dto;

import java.util.List;

public class TransportDTO {
    private String transportId;
    private String departureDateTime;
    private String truckId;
    private String driverId;
    private String originSiteId;
    private float departureWeight;
    private String status;
    private List<String> destinationDocIds;

    public TransportDTO() {}

    public TransportDTO(String transportId, String departureDateTime, String truckId, String driverId,
                        String originSiteId, float departureWeight, String status, List<String> destinationDocIds) {
        this.transportId = transportId;
        this.departureDateTime = departureDateTime;
        this.truckId = truckId;
        this.driverId = driverId;
        this.originSiteId = originSiteId;
        this.departureWeight = departureWeight;
        this.status = status;
        this.destinationDocIds = destinationDocIds;
    }

    public String getTransportId() { return transportId; }
    public void setTransportId(String transportId) { this.transportId = transportId; }

    public String getDepartureDateTime() { return departureDateTime; }
    public void setDepartureDateTime(String departureDateTime) { this.departureDateTime = departureDateTime; }

    public String getTruckId() { return truckId; }
    public void setTruckId(String truckId) { this.truckId = truckId; }

    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }

    public String getOriginSiteId() { return originSiteId; }
    public void setOriginSiteId(String originSiteId) { this.originSiteId = originSiteId; }

    public float getDepartureWeight() { return departureWeight; }
    public void setDepartureWeight(float departureWeight) { this.departureWeight = departureWeight; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<String> getDestinationDocIds() { return destinationDocIds; }
    public void setDestinationDocIds(List<String> destinationDocIds) { this.destinationDocIds = destinationDocIds; }
}
