package com.superli.deliveries.domain.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.superli.deliveries.domain.core.DeliveredItem;
import com.superli.deliveries.domain.core.Site;

/**
 * Represents a delivery document that is part of a specific transport and is linked to a destination site.
 * Includes document ID, destination site, list of delivered items, and delivery status.
 */
public class DestinationDoc {

    /** Unique document identifier */
    private final String destinationDocId;

    /** ID of the transport this document belongs to */
    private final String transportId;

    /** Destination site for this document */
    private final Site destinationId;

    /** Status of the delivery (e.g., PLANNED, COMPLETED) */
    private String status;

    /** List of items to be delivered to this site */
    private final List<DeliveredItem> deliveryItems;

    /**
     * Constructs a new delivery document.
     * @param destinationDocId Unique document ID
     * @param transportId ID of the associated transport
     * @param destinationId Site to which items are delivered
     * @param status Initial status of the document
     */
    public DestinationDoc(String destinationDocId, String transportId, Site destinationId, String status) {
        if (destinationId == null) {
            throw new IllegalArgumentException("Destination site cannot be null.");
        }
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Status cannot be null or blank.");
        }

        this.destinationDocId = destinationDocId;
        this.transportId = transportId;
        this.destinationId = destinationId;
        this.status = status;
        this.deliveryItems = new ArrayList<>();
    }

    public String getDestinationDocId() {
        return destinationDocId;
    }

    public String getTransportId() {
        return transportId;
    }

    public Site getDestinationId() {
        return destinationId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Status cannot be null or blank.");
        }
        this.status = status;
    }

    public List<DeliveredItem> getDeliveryItems() {
        return List.copyOf(deliveryItems);
    }

    public void addDeliveredItem(DeliveredItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Cannot add null item.");
        }
        for (DeliveredItem existing : deliveryItems) {
            if (existing.getProductId().equals(item.getProductId())) {
                throw new IllegalArgumentException("Duplicate product: " + item.getProductId());
            }
        }
        deliveryItems.add(item);
    }

    public boolean removeDeliveredItem(DeliveredItem item) {
        return item != null && deliveryItems.remove(item);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== DESTINATION DOCUMENT ===\n");
        sb.append("Document ID: ").append(destinationDocId).append("\n");
        sb.append("Transport ID: ").append(transportId).append("\n");
        sb.append("Status: ").append(status).append("\n\n");

        sb.append("Destination Site: ").append(destinationId.getSiteId())
                .append(" (").append(destinationId.getAddress()).append(")\n\n");

        sb.append("Items: ").append(deliveryItems.size()).append("\n");
        if (!deliveryItems.isEmpty()) {
            int i = 1;
            for (DeliveredItem item : deliveryItems) {
                sb.append("  ").append(i++).append(". Product: ")
                        .append(item.getProductId())
                        .append(", Quantity: ").append(item.getQuantity()).append("\n");
            }
        }

        sb.append("===========================");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DestinationDoc)) return false;
        DestinationDoc that = (DestinationDoc) o;
        return destinationDocId.equals(that.destinationDocId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(destinationDocId);
    }
}
