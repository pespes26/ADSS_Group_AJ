package com.superli.deliveries.presentation;

import java.util.List;
// Import needed if List.of() or List.copyOf() is used, already implicitly imported often.
// import java.util.ArrayList; // Needed only if defensive copy uses new ArrayList<>()

/**
 * Holds details for one delivery stop, formatted specifically for presentation.
 * Contains nested details for the destination site and a list of delivered items for this stop.
 * Belongs to the Presentation Layer (acts as a DTO/ViewModel).
 */
public class StopDetailView {

    // Attributes for display
    private final int sequence;                     // The sequence number of this stop in the route
    private final String documentNumber;            // The document number associated with this stop
    private final SiteDetailsView destinationSite;  // Details of the destination site (using helper class)
    private final List<ItemDetailView> deliveredItems; // List of item details for this stop (using helper class)
    // Status was removed based on user feedback

    /**
     * Constructs a StopDetailView object.
     * Usually populated based on a DeliveryStop domain object and its related data.
     *
     * @param sequence        The sequence number of this stop within the transport.
     * @param documentNumber  The document number specific to this stop/destination.
     * @param destinationSite A {@link SiteDetailsView} object containing display details for the destination site. Cannot be null.
     * @param deliveredItems  A list of {@link ItemDetailView} objects representing items for this stop. Cannot be null (can be empty).
     * @throws IllegalArgumentException if destinationSite or deliveredItems is null.
     */
    public StopDetailView(int sequence, String documentNumber, SiteDetailsView destinationSite, List<ItemDetailView> deliveredItems) {
        // Validation
         if (destinationSite == null) {
             throw new IllegalArgumentException("Destination Site view cannot be null.");
         }
         if (deliveredItems == null) {
             throw new IllegalArgumentException("Delivered items list cannot be null (can be empty).");
         }
         // Maybe check sequence > 0? Assume valid for now.

        this.sequence = sequence;
        this.documentNumber = documentNumber;
        this.destinationSite = destinationSite;
        // Store an immutable copy of the list to prevent external modification
        this.deliveredItems = List.copyOf(deliveredItems);
    }

    // --- Getters ---

    /**
     * Gets the sequence number of this stop.
     * @return The sequence number.
     */
    public int getSequence() {
        return sequence;
    }

    /**
     * Gets the document number associated with this stop.
     * @return The document number string.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Gets the details of the destination site for this stop.
     * @return The SiteDetailsView object.
     */
    public SiteDetailsView getDestinationSite() {
        return destinationSite;
    }

    /**
     * Gets an unmodifiable view of the list of item details for this stop.
     * @return An unmodifiable List of ItemDetailView objects.
     */
    public List<ItemDetailView> getDeliveredItems() {
        // The list stored is already an unmodifiable copy from List.copyOf()
        return deliveredItems;
    }

    // --- Standard Methods ---

    /**
     * Returns a string representation of the StopDetailView object.
     * @return A string representation of the view data.
     */
     @Override
    public String toString() {
        return "StopDetailView{" +
               "sequence=" + sequence +
               ", documentNumber='" + documentNumber + '\'' +
               ", destinationSite=" + destinationSite +
               ", deliveredItemsCount=" + deliveredItems.size() +
               '}';
    }

    // equals() and hashCode() might be useful if comparing stops in the UI,
    // could be based on sequence or documentNumber within the context of a transport.
    // Omitting for simplicity for now.
}