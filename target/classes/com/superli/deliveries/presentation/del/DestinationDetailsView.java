package com.superli.deliveries.presentation.del;

import java.util.List;
import java.util.Objects;

import com.superli.deliveries.domain.core.TransportStatus;

/**
 * Represents a single destination document in a transport,
 * including the destination site, delivered items, and status.
 */
public class DestinationDetailsView {

    private final String destinationDocId;
    private final SiteDetailsView destinationSite;
    private final List<DeliveredItemDetailsView> deliveredItems;
    private final TransportStatus status;

    public DestinationDetailsView(String destinationDocId, SiteDetailsView destinationSite,
                                  List<DeliveredItemDetailsView> deliveredItems, TransportStatus status) {
        if (destinationSite == null) {
            throw new IllegalArgumentException("Destination site cannot be null.");
        }
        if (deliveredItems == null) {
            throw new IllegalArgumentException("Delivered items list cannot be null.");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null.");
        }

        this.destinationDocId = destinationDocId;
        this.destinationSite = destinationSite;
        this.deliveredItems = List.copyOf(deliveredItems);
        this.status = status;
    }

    public String getDestinationDocId() {
        return destinationDocId;
    }

    public SiteDetailsView getDestinationSite() {
        return destinationSite;
    }

    public List<DeliveredItemDetailsView> getDeliveredItems() {
        return deliveredItems;
    }

    public TransportStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "DestinationDetailsView{" +
                "destinationDocId=" + destinationDocId +
                ", destinationSite=" + destinationSite +
                ", deliveredItemsCount=" + deliveredItems.size() +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DestinationDetailsView)) return false;
        DestinationDetailsView that = (DestinationDetailsView) o;
        return destinationDocId.equals(that.destinationDocId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(destinationDocId);
    }
}
