package com.superli.deliveries.domain;


/**
 * Represents a product within the context of the deliveries module.
 * This might be a minimal representation; full product details may reside elsewhere.
 * Corresponds to the Product class in the Domain Layer class diagram.
 */
public class Product {

    private final String productId; // Unique identifier for the product (links to Inventory module)
    private final String name;      // Name of the product (for display and identification)

    /**
     * Constructs a new Product object (for use within delivery context).
     *
     * @param productId The unique identifier of the product. Cannot be null or empty.
     * @param name      The name of the product. Cannot be null or empty.
     * @throws IllegalArgumentException if productId or name is null or empty.
     */
    public Product(String productId, String name) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty.");
        }
        
        this.productId = productId;
        this.name = name;
    }

    // --- Getters ---

    /**
     * Gets the unique identifier of the product.
     * @return The product ID.
     */
    public String getProductId() {
        return productId;
    }

    /**
     * Gets the name of the product.
     * @return The product name.
     */
    public String getName() {
        return name;
    }

    // --- Standard Methods ---

    /**
     * Returns a string representation of the Product object.
     * @return A string representation including productId and name.
     */
    @Override
    public String toString() {
        return "Product{" +
               "productId='" + productId + '\'' +
               ", name='" + name + '\'' +
               '}';
    }

    /**
     * Checks if this Product is equal to another object.
     * Equality is based solely on the productId.
     * @param o The object to compare with.
     * @return true if the objects are equal (same productId), false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return productId.equals(product.productId);
    }

    /**
     * Returns the hash code for this Product.
     * Based solely on the productId.
     * @return The hash code value for this object.
     */
    @Override
    public int hashCode() {
        return productId.hashCode();
    }
}