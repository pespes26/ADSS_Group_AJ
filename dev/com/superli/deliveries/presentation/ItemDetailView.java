package com.superli.deliveries.presentation;

/**
 * Holds details for one item line within a delivery stop view.
 * Contains the product name and quantity for display.
 * Belongs to the Presentation Layer (acts as a DTO/ViewModel).
 */
public class ItemDetailView {

    // Attributes for display
    private final String productName;
    private final int quantity;

    /**
     * Constructs an ItemDetailView object.
     * Usually populated based on a DeliveredItem domain object (quantity)
     * and its associated Product domain object (name).
     *
     * @param productName The name of the product to be displayed.
     * @param quantity    The quantity delivered/planned for display.
     */
    public ItemDetailView(String productName, int quantity) {
        // Assuming data validity is handled before creating this view object
        // (e.g., productName is not null/empty, quantity is non-negative)
        this.productName = productName;
        this.quantity = quantity;
    }

    // --- Getters ---

    /**
     * Gets the product name for display.
     * @return The product name string.
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Gets the quantity for display.
     * @return The quantity.
     */
    public int getQuantity() {
        return quantity;
    }

    // --- Standard Methods ---

    /**
     * Returns a string representation of the ItemDetailView object.
     * @return A string representation of the view data.
     */
     @Override
    public String toString() {
        return "ItemDetailView{" +
               "productName='" + productName + '\'' +
               ", quantity=" + quantity +
               '}';
    }

    // equals() and hashCode() are typically not required for display-only DTOs
}