package com.superli.deliveries.domain; // שם חבילה לדוגמה

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a transport or delivery event within the system.
 * It holds details about the journey, involved truck and driver, origin,
 * status, weight, and its constituent delivery stops.
 * Corresponds to the Transport class in the Domain Layer class diagram.
 */
public class Transport {

    // Attributes
    private final String transportId;           // Unique identifier for the transport
    private LocalDateTime departureDateTime;    // Planned or actual departure date and time
    private final Site originSite;              // The site where the transport originates
    private final Truck truck;                  // The truck assigned to this transport
    private final Driver driver;                // The driver assigned to this transport
    private Float actualDepartureWeight;  // Actual weight measured at departure (null if not yet weighed)
    private String status;                      // Current status (e.g., PLANNED, DISPATCHED, COMPLETED, CANCELLED)
    private final List<DeliveryStop> deliveryStops; // The list of stops (destinations) composing this transport

    /**
     * Constructs a new Transport object, typically in a planning phase.
     *
     * @param transportId       The unique identifier for this transport. Cannot be null or empty.
     * @param originSite        The origin site of the transport. Cannot be null.
     * @param truck             The truck assigned to the transport. Cannot be null.
     * @param driver            The driver assigned to the transport. Cannot be null.
     * @param initialStatus     The initial status (e.g., "PLANNED"). Cannot be null or empty.
     * @param departureDateTime The planned departure date and time (can be null initially).
     * @throws IllegalArgumentException if validation for ID, site, truck, driver, or status fails.
     */
    public Transport(String transportId, Site originSite, Truck truck, Driver driver, String initialStatus, LocalDateTime departureDateTime) {
        // Validation
        if (transportId == null || transportId.trim().isEmpty()) {
            throw new IllegalArgumentException("Transport ID cannot be null or empty.");
        }
        if (originSite == null) {
            throw new IllegalArgumentException("Origin site cannot be null.");
        }
        if (truck == null) {
            throw new IllegalArgumentException("Truck cannot be null.");
        }
        if (driver == null) {
            throw new IllegalArgumentException("Driver cannot be null.");
        }
         if (initialStatus == null || initialStatus.trim().isEmpty()) {
            throw new IllegalArgumentException("Initial status cannot be null or empty.");
        }
        // Note: Add license compatibility check logic here or in a service layer before calling constructor

        this.transportId = transportId;
        this.originSite = originSite;
        this.truck = truck;
        this.driver = driver;
        this.status = initialStatus;
        this.departureDateTime = departureDateTime;
        this.actualDepartureWeight = null; // Initialized as null
        this.deliveryStops = new ArrayList<>(); // Initialize empty list for stops
    }

    // --- Getters ---

    /**
     * Gets the unique identifier of the transport.
     * @return The transport ID.
     */
    public String getTransportId() {
        return transportId;
    }

    /**
     * Gets the departure date and time.
     * @return The departure LocalDateTime, or null if not set.
     */
    public LocalDateTime getDepartureDateTime() {
        return departureDateTime;
    }

    /**
     * Gets the origin site of the transport.
     * @return The origin Site object.
     */
    public Site getOriginSite() {
        return originSite;
    }

    /**
     * Gets the truck assigned to the transport.
     * @return The assigned Truck object.
     */
    public Truck getTruck() {
        return truck;
    }

    /**
     * Gets the driver assigned to the transport.
     * @return The assigned Driver object.
     */
    public Driver getDriver() {
        return driver;
    }

    /**
     * Gets the actual weight measured at departure.
     * @return The actual weight as a Float, or null if not weighed yet.
     */
    public Float getActualDepartureWeight() {
        return actualDepartureWeight;
    }

    /**
     * Gets the current status of the transport.
     * @return The status string.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Gets an unmodifiable view of the list of delivery stops for this transport.
     * Prevents external modification of the internal list.
     * Use {@link #addDeliveryStop(DeliveryStop)} or {@link #removeDeliveryStop(DeliveryStop)} to modify.
     * @return An unmodifiable List of DeliveryStop objects.
     */
    public List<DeliveryStop> getDeliveryStops() {
         return List.copyOf(deliveryStops);
    }

    // --- Setters ---

    /**
     * Sets the departure date and time for the transport.
     * @param departureDateTime The departure date and time.
     */
    public void setDepartureDateTime(LocalDateTime departureDateTime) {
        this.departureDateTime = departureDateTime;
    }

    /**
     * Sets the actual measured departure weight.
     * @param actualDepartureWeight The measured weight. Should ideally be positive.
     */
    public void setActualDepartureWeight(Float actualDepartureWeight) {
         if (actualDepartureWeight != null && actualDepartureWeight <= 0) {
             // Consider throwing IllegalArgumentException instead
             System.err.println("Warning: Setting non-positive actual departure weight for transport " + this.transportId);
         }
        this.actualDepartureWeight = actualDepartureWeight;
    }

    /**
     * Sets the current status of the transport.
     * @param status The new status string. Should not be null or empty.
     * @throws IllegalArgumentException if status is null or empty.
     */
    public void setStatus(String status) {
         if (status == null || status.trim().isEmpty()) {
             throw new IllegalArgumentException("Status cannot be null or empty.");
         }
        // Optional: Add validation against a predefined list of valid statuses
        this.status = status;
    }

    /**
     * Adds a delivery stop to this transport.
     * Handles the composition relationship.
     * @param stop The DeliveryStop to add. Cannot be null.
     * @throws IllegalArgumentException if stop is null.
     */
    public void addDeliveryStop(DeliveryStop stop) {
        if (stop == null) {
             throw new IllegalArgumentException("Cannot add a null delivery stop.");
        }
        // Optional: Check for duplicate stops (e.g., by destination site or ID) if needed
        this.deliveryStops.add(stop);
    }

    /**
     * Removes a delivery stop from this transport.
     * @param stop The DeliveryStop to remove.
     * @return true if the stop was found and removed, false otherwise.
     */
    public boolean removeDeliveryStop(DeliveryStop stop) {
        if (stop != null) {
           // Removal usually relies on the DeliveryStop's equals() method (based on its ID)
           return this.deliveryStops.remove(stop);
        }
        return false;
    }



    /**
     * Returns a string representation of the Transport object.
     * @return A string representation including key attributes.
     */
    @Override
    public String toString() {
        return "Transport{" +
               "transportId='" + transportId + '\'' +
               ", departureDateTime=" + departureDateTime +
               ", originSiteId=" + (originSite != null ? originSite.getSiteId() : "null") +
               ", truckPlate=" + (truck != null ? truck.getLicensePlateNumber() : "null") +
               ", driverId=" + (driver != null ? driver.getDriverId() : "null") +
               ", actualDepartureWeight=" + actualDepartureWeight +
               ", status='" + status + '\'' +
               ", deliveryStopsCount=" + deliveryStops.size() +
               '}';
    }

   /**
     * Checks if this Transport is equal to another object.
     * Equality is based solely on the transportId.
     * @param o The object to compare with.
     * @return true if the objects are equal (same transportId), false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transport transport = (Transport) o;
        return transportId.equals(transport.transportId);
    }

   /**
     * Returns the hash code for this Transport.
     * Based solely on the transportId.
     * @return The hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(transportId);
    }
}