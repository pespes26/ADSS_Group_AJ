package com.superli.deliveries.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a specific stop or destination within a Transport.
 * It holds details about the destination site, the associated document number,
 * and the list of items delivered or planned for delivery at this stop.
 * Corresponds to the DeliveryStop class in the Domain Layer class diagram.
 */
public class DeliveryStop {

    private final String deliveryStopId;        // Unique identifier for this specific stop
    private final Site destinationSite;         // The destination site for this stop
    private final String documentNumber;        // The numbered document associated with this destination/stop
    private int sequence;                       // The sequence number of this stop within the transport route
    private String status;                      // The status of this stop (e.g., PLANNED, ARRIVED, COMPLETED, FAILED)
    private final List<DeliveredItem> deliveredItems; // The list of items and quantities for this stop

    /**
     * Constructs a new DeliveryStop.
     *
     * @param deliveryStopId The unique identifier for the stop. Cannot be null or empty.
     * @param destinationSite The destination site. Cannot be null.
     * @param documentNumber The document number for this stop. Cannot be null or empty.
     * @param sequence The sequence number of this stop in the route. Should be positive.
     * @param initialStatus The initial status (e.g., "PLANNED"). Cannot be null or empty.
     * @throws IllegalArgumentException if validation for ID, site, document number, or status fails.
     */
    public DeliveryStop(String deliveryStopId, Site destinationSite, String documentNumber, int sequence, String initialStatus) {
        // Validation
        if (deliveryStopId == null || deliveryStopId.trim().isEmpty()) {
            throw new IllegalArgumentException("Delivery Stop ID cannot be null or empty.");
        }
        if (destinationSite == null) {
            throw new IllegalArgumentException("Destination Site cannot be null.");
        }
         if (documentNumber == null || documentNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Document number cannot be null or empty.");
        }
        if (sequence <= 0) {
            // Consider throwing exception instead
            System.err.println("Warning: Initializing DeliveryStop with non-positive sequence: " + sequence);
             throw new IllegalArgumentException("Sequence must be positive.");
        }
        if (initialStatus == null || initialStatus.trim().isEmpty()) {
            throw new IllegalArgumentException("Initial status cannot be null or empty.");
        }

        this.deliveryStopId = deliveryStopId;
        this.destinationSite = destinationSite;
        this.documentNumber = documentNumber;
        this.sequence = sequence;
        this.status = initialStatus;
        this.deliveredItems = new ArrayList<>(); // Initialize empty list
    }

    // --- Getters ---

    /**
     * Gets the unique identifier of this delivery stop.
     * @return The delivery stop ID.
     */
    public String getDeliveryStopId() {
        return deliveryStopId;
    }

    /**
     * Gets the destination site for this stop.
     * @return The destination Site object.
     */
    public Site getDestinationSite() {
        return destinationSite;
    }

    /**
     * Gets the numbered document identifier associated with this stop.
     * @return The document number.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Gets the sequence number of this stop in the transport route.
     * @return The sequence number.
     */
    public int getSequence() {
        return sequence;
    }

    /**
     * Gets the current status of this delivery stop.
     * @return The status string.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Gets an unmodifiable view of the list of items delivered/planned for this stop.
     * @return An unmodifiable List of DeliveredItem objects.
     */
    public List<DeliveredItem> getDeliveredItems() {
        return List.copyOf(deliveredItems);
    }

    // --- Setters ---

    /**
     * Sets the sequence number for this stop.
     * @param sequence The sequence number (should be positive).
     * @throws IllegalArgumentException if sequence is not positive.
     */
    public void setSequence(int sequence) {
         if (sequence <= 0) {
             throw new IllegalArgumentException("Sequence must be positive.");
         }
         this.sequence = sequence;
    }

    /**
     * Sets the status for this delivery stop.
     * @param status The new status. Cannot be null or empty.
     * @throws IllegalArgumentException if status is null or empty.
     */
    public void setStatus(String status) {
         if (status == null || status.trim().isEmpty()) {
             throw new IllegalArgumentException("Status cannot be null or empty.");
         }
        // Optional: Validate against a list of known statuses
        this.status = status;
    }

    // --- Methods for managing composition ---
    // (Included for code usability, not required in the 'attributes only' diagram)

    /**
     * Adds an item/quantity record to this delivery stop.
     * Prevents adding null or duplicate products (based on Product.equals).
     * @param item The DeliveredItem to add. Cannot be null.
     * @throws IllegalArgumentException if item is null or product already exists in this stop.
     */
    public void addDeliveredItem(DeliveredItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Cannot add a null item to stop " + this.deliveryStopId);
        }
        // Prevent adding the same product twice
        for (DeliveredItem existingItem : this.deliveredItems) {
            if (existingItem.getProduct().equals(item.getProduct())) {
                throw new IllegalArgumentException("Product " + item.getProduct().getProductId() +
                                                   " already exists in stop " + this.deliveryStopId +
                                                   ". Update quantity instead?");
            }
        }
        this.deliveredItems.add(item);
    }

     /**
      * Removes a specific item record from this delivery stop.
      * Relies on DeliveredItem's equals() method.
      * @param item The DeliveredItem record to remove.
      * @return true if the item was found and removed, false otherwise.
      */
     public boolean removeDeliveredItem(DeliveredItem item) {
        if (item != null) {
           return this.deliveredItems.remove(item);
        }
        return false;
     }

    // --- Standard Methods ---

    /**
     * Returns a string representation of the DeliveryStop object.
     * @return A string representation including key attributes.
     */
    @Override
    public String toString() {
        return "DeliveryStop{" +
               "deliveryStopId='" + deliveryStopId + '\'' +
               ", destinationSiteId=" + (destinationSite != null ? destinationSite.getSiteId() : "null") +
               ", documentNumber='" + documentNumber + '\'' +
               ", sequence=" + sequence +
               ", status='" + status + '\'' +
               ", itemsCount=" + deliveredItems.size() +
               '}';
    }

   /**
     * Checks if this DeliveryStop is equal to another object.
     * Equality is based solely on the deliveryStopId.
     * @param o The object to compare with.
     * @return true if the objects are equal (same deliveryStopId), false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeliveryStop that = (DeliveryStop) o;
        return deliveryStopId.equals(that.deliveryStopId);
    }

   /**
     * Returns the hash code for this DeliveryStop.
     * Based solely on the deliveryStopId.
     * @return The hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(deliveryStopId);
    }
}