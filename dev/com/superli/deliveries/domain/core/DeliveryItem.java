package com.superli.deliveries.domain.core;

import java.util.Objects;

/**
 * Represents an item to be delivered.
 */
public class DeliveryItem {
    private final String itemId;
    private final String description;
    private final float weight;
    private final float volume;
    private boolean delivered;

    /**
     * Constructs a new DeliveryItem.
     *
     * @param itemId Unique identifier of the item.
     * @param description Description of the item.
     * @param weight Weight of the item in kilograms.
     * @param volume Volume of the item in cubic meters.
     */
    public DeliveryItem(String itemId, String description, float weight, float volume) {
        if (itemId == null || itemId.trim().isEmpty()) {
            throw new IllegalArgumentException("Item ID cannot be null or empty.");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty.");
        }
        if (weight <= 0) {
            throw new IllegalArgumentException("Weight must be positive.");
        }
        if (volume <= 0) {
            throw new IllegalArgumentException("Volume must be positive.");
        }

        this.itemId = itemId;
        this.description = description;
        this.weight = weight;
        this.volume = volume;
        this.delivered = false;
    }

    public String getItemId() {
        return itemId;
    }

    public String getDescription() {
        return description;
    }

    public float getWeight() {
        return weight;
    }

    public float getVolume() {
        return volume;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    @Override
    public String toString() {
        return "DeliveryItem{" +
                "itemId='" + itemId + '\'' +
                ", description='" + description + '\'' +
                ", weight=" + weight +
                ", volume=" + volume +
                ", delivered=" + delivered +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeliveryItem)) return false;
        DeliveryItem that = (DeliveryItem) o;
        return itemId.equals(that.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId);
    }
} 