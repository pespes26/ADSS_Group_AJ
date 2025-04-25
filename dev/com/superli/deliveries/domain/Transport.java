package com.superli.deliveries.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a transport event containing truck, driver, origin, delivery list, and transport status.
 */
public class Transport {

    private final int transportId;
    private LocalDateTime departureDateTime;
    private final Truck truck;
    private final Driver driver;
    private final Site originSite;
    private final List<DestinationDoc> destinationList;
    private float departureWeight;
    private TransportStatus status;

    /**
     * Constructs a new Transport.
     *
     * @param transportId Unique identifier of the transport.
     * @param departureDateTime Planned departure date and time.
     * @param truck Assigned truck.
     * @param driver Assigned driver.
     * @param originSite Origin site of the transport.
     * @param status Initial status of the transport.
     */
    public Transport(int transportId, LocalDateTime departureDateTime, Truck truck,
                     Driver driver, Site originSite, TransportStatus status) {
        if (truck == null || driver == null || originSite == null || status == null) {
            throw new IllegalArgumentException("Truck, driver, origin site, and status cannot be null.");
        }

        this.transportId = transportId;
        this.departureDateTime = departureDateTime;
        this.truck = truck;
        this.driver = driver;
        this.originSite = originSite;
        this.status = status;
        this.destinationList = new ArrayList<>();
        this.departureWeight = 0f;
    }

    // --- Getters ---

    public int getTransportId() {
        return transportId;
    }

    public LocalDateTime getDepartureDateTime() {
        return departureDateTime;
    }

    public Truck getTruck() {
        return truck;
    }

    public Driver getDriver() {
        return driver;
    }

    public Site getOriginSite() {
        return originSite;
    }

    public List<DestinationDoc> getDestinationList() {
        return List.copyOf(destinationList);
    }

    public float getDepartureWeight() {
        return departureWeight;
    }

    public TransportStatus getStatus() {
        return status;
    }

    // --- Setters ---

    public void setDepartureDateTime(LocalDateTime departureDateTime) {
        this.departureDateTime = departureDateTime;
    }

    public void setDepartureWeight(float departureWeight) {
        if (departureWeight < 0) {
            throw new IllegalArgumentException("Departure weight cannot be negative.");
        }
        this.departureWeight = departureWeight;
    }

    public void setStatus(TransportStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null.");
        }
        this.status = status;
    }

    public void setActualDepartureWeight(float actualDepartureWeight) {
        if (actualDepartureWeight < 0) {
            throw new IllegalArgumentException("Actual departure weight cannot be negative.");
        }
        this.departureWeight = actualDepartureWeight;
    }

    // --- Composition ---

    public void addDestination(DestinationDoc destination) {
        if (destination == null) {
            throw new IllegalArgumentException("Destination cannot be null.");
        }
        destinationList.add(destination);
    }

    public boolean removeDestination(DestinationDoc destination) {
        return destination != null && destinationList.remove(destination);
    }

    // --- Standard Methods ---

    @Override
    public String toString() {
        return "Transport{" +
                "transportId=" + transportId +
                ", departureDateTime=" + departureDateTime +
                ", truck=" + truck.getPlateNum() +
                ", driver=" + driver.getDriverId() +
                ", originSite=" + originSite.getSiteId() +
                ", departureWeight=" + departureWeight +
                ", status=" + status +
                ", destinationCount=" + destinationList.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transport)) return false;
        Transport that = (Transport) o;
        return transportId == that.transportId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(transportId);
    }
}