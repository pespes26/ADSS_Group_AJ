package com.superli.deliveries.presentation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
// Import List if using List.of() explicitly, though often automatically available
// import java.util.ArrayList; // Only needed if using new ArrayList<>() for defensive copy

/**
 * Holds the comprehensive details of a single transport, formatted for presentation.
 * This object aggregates details about the transport itself, origin site, truck, driver,
 * and a list of stops (each with their own details and items).
 * Acts as a primary DTO/ViewModel for the transport detail screen in the Presentation Layer.
 */
public class TransportDetailView {

    // Core transport attributes for display
    private final String transportId;
    private final LocalDateTime departureDateTime;
    private final Float actualDepartureWeight; // Use Float wrapper type to allow null
    private final String status;

    // Aggregated details from helper view objects
    private final SiteDetailsView originSite;
    private final TruckDetailsView truck;
    private final DriverDetailsView driver;
    private final List<StopDetailView> stops; // List of stops associated with this transport view

    /**
     * Constructs a TransportDetailView object, typically created from domain data.
     *
     * @param transportId           The transport's unique identifier.
     * @param departureDateTime     The transport's departure date and time (can be null).
     * @param actualDepartureWeight The transport's actual measured weight at departure (can be null).
     * @param status                The current status of the transport.
     * @param originSite            A {@link SiteDetailsView} object for the origin site. Cannot be null.
     * @param truck                 A {@link TruckDetailsView} object for the assigned truck. Cannot be null.
     * @param driver                A {@link DriverDetailsView} object for the assigned driver. Cannot be null.
     * @param stops                 A list of {@link StopDetailView} objects representing the stops. Cannot be null (can be empty).
     * @throws IllegalArgumentException if any of the non-nullable object parameters (originSite, truck, driver, stops) are null.
     */
    public TransportDetailView(String transportId, LocalDateTime departureDateTime, Float actualDepartureWeight, String status,
                               SiteDetailsView originSite, TruckDetailsView truck, DriverDetailsView driver, List<StopDetailView> stops) {

        // Validation for required objects
        if (originSite == null) {
            throw new IllegalArgumentException("Origin Site view details cannot be null.");
        }
        if (truck == null) {
            throw new IllegalArgumentException("Truck view details cannot be null.");
        }
        if (driver == null) {
            throw new IllegalArgumentException("Driver view details cannot be null.");
        }
        if (stops == null) {
            throw new IllegalArgumentException("Stops list cannot be null (can be empty).");
        }
        // Assume primitive/String types like transportId and status are validated elsewhere or acceptable as passed

        this.transportId = transportId;
        this.departureDateTime = departureDateTime;
        this.actualDepartureWeight = actualDepartureWeight;
        this.status = status;
        this.originSite = originSite;
        this.truck = truck;
        this.driver = driver;
        // Store an immutable copy of the stops list
        this.stops = List.copyOf(stops);
    }

    // --- Getters ---

    public String getTransportId() {
        return transportId;
    }

    public LocalDateTime getDepartureDateTime() {
        return departureDateTime;
    }

    public Float getActualDepartureWeight() {
        return actualDepartureWeight;
    }

    public String getStatus() {
        return status;
    }

    public SiteDetailsView getOriginSite() {
        return originSite;
    }

    public TruckDetailsView getTruck() {
        return truck;
    }

    public DriverDetailsView getDriver() {
        return driver;
    }

    /**
     * Gets an unmodifiable view of the list of stop details for this transport.
     * @return An unmodifiable List of StopDetailView objects.
     */
    public List<StopDetailView> getStops() {
        // The list stored is already an unmodifiable copy from List.copyOf()
        return stops;
    }

    // --- Standard Methods ---

    /**
     * Returns a string representation of the TransportDetailView object.
     * @return A string representation summarizing the view data.
     */
     @Override
    public String toString() {
        // Provides a summary; detailed stop info is within the list items
        return "TransportDetailView{" +
               "transportId='" + transportId + '\'' +
               ", departureDateTime=" + departureDateTime +
               ", actualDepartureWeight=" + actualDepartureWeight +
               ", status='" + status + '\'' +
               ", originSite=" + originSite + // Relies on SiteDetailsView.toString()
               ", truck=" + truck +           // Relies on TruckDetailsView.toString()
               ", driver=" + driver +         // Relies on DriverDetailsView.toString()
               ", stopsCount=" + stops.size() +
               '}';
    }

    // equals() and hashCode() are typically not strictly required for top-level view DTOs,
    // unless used in collections or for specific comparison logic. Can be based on transportId if needed.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransportDetailView that = (TransportDetailView) o;
        return Objects.equals(transportId, that.transportId); // Example based on ID
    }

    @Override
    public int hashCode() {
        return Objects.hash(transportId); // Example based on ID
    }
}