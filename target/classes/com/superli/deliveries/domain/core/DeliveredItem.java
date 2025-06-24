package com.superli.deliveries.domain.core;

import java.util.Objects;

/**
 * Represents a specific item that is part of a delivery document.
 * A DeliveredItem links a product (via its ID) to the quantity being delivered.
 *
 * This class adheres to the UML structure which includes only a productId and quantity.
 * No direct reference to the Product object is kept to maintain clear traceability.
 *
 * Equality is based solely on the productId, assuming each document
 * will contain at most one DeliveredItem per product.
 */
public class DeliveredItem {

    /** The unique ID of this delivered item. */
    private final String itemId;

    /** The ID of the destination document this item belongs to. */
    private final String destinationDocId;

    /** The unique ID of the product being delivered. */
    private final String productId;

    /** The quantity of the product delivered. Must be non-negative. */
    private int quantity;

    /**
     * Constructs a new DeliveredItem instance.
     *
     * @param itemId The unique identifier of this delivered item.
     * @param destinationDocId The ID of the destination document this item belongs to.
     * @param productId The unique identifier of the product. Cannot be null or blank.
     * @param quantity The quantity of the product delivered. Must be zero or greater.
     * @throws IllegalArgumentException if productId is null/blank or quantity is negative.
     */
    public DeliveredItem(String itemId, String destinationDocId, String productId, int quantity) {
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("Product ID cannot be null or blank.");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }

        this.itemId = itemId;
        this.destinationDocId = destinationDocId;
        this.productId = productId;
        this.quantity = quantity;
    }

    /**
     * Returns the ID of this delivered item.
     *
     * @return The item's unique identifier.
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * Returns the ID of the destination document this item belongs to.
     *
     * @return The destination document's unique identifier.
     */
    public String getDestinationDocId() {
        return destinationDocId;
    }

    /**
     * Returns the product ID associated with this item.
     *
     * @return The product's unique identifier.
     */
    public String getProductId() {
        return productId;
    }

    /**
     * Returns the quantity of product delivered.
     *
     * @return The quantity of the product.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets a new quantity for the product.
     *
     * @param quantity The new quantity. Must be non-negative.
     * @throws IllegalArgumentException if quantity is negative.
     */
    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        this.quantity = quantity;
    }

    /**
     * Returns a string representation of this DeliveredItem.
     *
     * @return A string containing itemId, destinationDocId, productId and quantity.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== DELIVERED ITEM ===\n");
        sb.append("Item ID: ").append(itemId).append("\n");
        sb.append("Destination Document ID: ").append(destinationDocId).append("\n");
        sb.append("Product ID: ").append(productId).append("\n");
        sb.append("Quantity: ").append(quantity).append("\n");
        sb.append("=====================");
        return sb.toString();
    }

    /**
     * Compares this DeliveredItem with another object.
     * Two DeliveredItems are considered equal if they refer to the same product ID.
     *
     * @param o The object to compare.
     * @return true if the product IDs match; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeliveredItem)) return false;
        DeliveredItem that = (DeliveredItem) o;
        return Objects.equals(productId, that.productId);
    }

    /**
     * Returns the hash code of this DeliveredItem, based on product ID.
     *
     * @return The hash code value.
     */
    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }
}
