package domain.core.employee;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a delivery destination document containing information about a delivery destination.
 */
public class DestinationDoc {
    private final Site destinationId;
    private final List<DeliveryItem> deliveryItems;
    private DestinationStatus status;

    /**
     * Constructs a new DestinationDoc.
     *
     * @param destinationId The destination site.
     * @param status Initial status of the destination.
     */
    public DestinationDoc(Site destinationId, DestinationStatus status) {
        if (destinationId == null) {
            throw new IllegalArgumentException("Destination ID cannot be null.");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null.");
        }

        this.destinationId = destinationId;
        this.status = status;
        this.deliveryItems = new ArrayList<>();
    }

    public Site getDestinationId() {
        return destinationId;
    }

    public List<DeliveryItem> getDeliveryItems() {
        return List.copyOf(deliveryItems);
    }

    public DestinationStatus getStatus() {
        return status;
    }

    public void setStatus(DestinationStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null.");
        }
        this.status = status;
    }

    public void addDeliveryItem(DeliveryItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Delivery item cannot be null.");
        }
        deliveryItems.add(item);
    }

    public boolean removeDeliveryItem(DeliveryItem item) {
        return item != null && deliveryItems.remove(item);
    }

    @Override
    public String toString() {
        return "DestinationDoc{" +
                "destinationId=" + destinationId +
                ", deliveryItems=" + deliveryItems +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DestinationDoc)) return false;
        DestinationDoc that = (DestinationDoc) o;
        return destinationId.equals(that.destinationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(destinationId);
    }
} 