package com.superli.deliveries.presentation;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents the data needed to display a single row
 * in a summary list of transports.
 * Belongs to the Presentation Layer (acts as a DTO/ViewModel).
 */
public class TransportListViewItem {

    private final String transportId;
    private final LocalDateTime departureDateTime;
    private final String driverName;
    private final String truckLicensePlate;
    private final String originSiteName; // שם האתר, לא האובייקט המלא

    /**
     * Constructs a new TransportListViewItem.
     * Typically created based on data retrieved from Domain objects.
     *
     * @param transportId         The ID of the transport.
     * @param departureDateTime   The departure time.
     * @param driverName          The driver's name.
     * @param truckLicensePlate   The truck's license plate.
     * @param originSiteName      The name of the origin site.
     */
    public TransportListViewItem(String transportId, LocalDateTime departureDateTime, String driverName, String truckLicensePlate, String originSiteName) {
        // Basic assignment, assuming data is valid coming from another layer
        this.transportId = transportId;
        this.departureDateTime = departureDateTime;
        this.driverName = driverName;
        this.truckLicensePlate = truckLicensePlate;
        this.originSiteName = originSiteName;
    }

    // --- Getters ---

    public String getTransportId() {
        return transportId;
    }

    public LocalDateTime getDepartureDateTime() {
        return departureDateTime;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getTruckLicensePlate() {
        return truckLicensePlate;
    }

    public String getOriginSiteName() {
        return originSiteName;
    }

    // --- Standard Methods ---

     @Override
    public String toString() {
        // Example format, adjust as needed
        return String.format("ID: %s, Departs: %s, Driver: %s, Truck: %s, Origin: %s",
                             transportId, departureDateTime, driverName, truckLicensePlate, originSiteName);
    }

    // equals() and hashCode() might not be strictly necessary for a simple DTO
    // but can be added if needed (e.g., based on transportId).
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransportListViewItem that = (TransportListViewItem) o;
        return Objects.equals(transportId, that.transportId); // Example: equality based on ID
    }

    @Override
    public int hashCode() {
        return Objects.hash(transportId); // Example: hash based on ID
    }
}