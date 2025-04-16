package com.superli.deliveries.domain;

import java.util.Objects;

/**
 * Represents a specific item (product and quantity) that was delivered
 * or planned for delivery at a specific DeliveryStop.
 * Acts as an association class between DeliveryStop and Product, holding the quantity.
 */
public class DeliveredItem {

    // Attributes
    private final Product product; // The product being delivered (final link)
    private int quantity;          // The quantity of the product (mutable)
    private String status;         // Optional: Status of this specific item line (e.g., DELIVERED, MISSING, DAMAGED)

    /**
     * Constructs a new DeliveredItem record.
     * @param product The product associated with this item line. Cannot be null.
     * @param quantity The initial quantity. Must be non-negative (consider if 0 is allowed).
     * @param initialStatus The initial status (can be null or empty if not used initially).
     * @throws IllegalArgumentException if product is null or quantity is negative.
     */
    public DeliveredItem(Product product, int quantity, String initialStatus) {
        // Validation
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null for DeliveredItem.");
        }
        if (quantity < 0) { // Allow zero quantity? E.g., if removed later?
             throw new IllegalArgumentException("Quantity cannot be negative.");
        }

        this.product = product;
        this.quantity = quantity;
        this.status = initialStatus;
    }

    // --- Getters ---

    /**
     * Gets the product associated with this delivery item line.
     * @return The Product object.
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Gets the quantity of the product for this delivery item line.
     * @return The quantity.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Gets the status of this specific item line (optional).
     * @return The status string, or null.
     */
    public String getStatus() {
        return status;
    }

    // --- Setters --- (product is final)

    /**
     * Sets the quantity for this delivery item line.
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
     * Sets the status for this specific item line.
     * @param status The new status string.
     */
    public void setStatus(String status) {
         // Optional: Validate status against known values
        this.status = status;
    }

    // --- Standard Methods ---

    /**
     * Returns a string representation of the DeliveredItem object.
     * @return A string representation including product ID, quantity, and status.
     */
    @Override
    public String toString() {
        return "DeliveredItem{" +
               "productId=" + (product != null ? product.getProductId() : "null") +
               ", quantity=" + quantity +
               ", status='" + status + '\'' +
               '}';
    }

   /**
     * Checks if this DeliveredItem is equal to another object.
     * Equality is based on the associated Product. Assumes a DeliveryStop
     * will not contain two DeliveredItem entries for the same Product.
     * @param o The object to compare with.
     * @return true if the objects refer to the same product, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeliveredItem that = (DeliveredItem) o;
        // Equality based on the referenced Product within the context of a DeliveryStop's list
        return product.equals(that.product);
    }

   /**
     * Returns the hash code for this DeliveredItem.
     * Based on the associated Product.
     * @return The hash code value.
     */
    @Override
    public int hashCode() {
        // Hash code based on the referenced Product
        return Objects.hash(product);
    }
}