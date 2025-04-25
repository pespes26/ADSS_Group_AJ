package com.superli.deliveries.domain;

/**
 * Represents a product within the context of the deliveries module.
 * Includes basic information such as ID, name, and weight.
 * Matches the Product class in the Domain Layer class diagram.
 */
public class Product {

    /** Unique identifier for the product (links to Inventory module) */
    private final String productId;

    /** Name of the product (for display and identification) */
    private final String name;

    /** Weight of a single unit of the product */
    private final float weight;

    /**
     * Constructs a new Product object.
     *
     * @param productId The unique identifier of the product. Cannot be null or empty.
     * @param name The name of the product. Cannot be null or empty.
     * @param weight The weight of the product. Must be non-negative.
     * @throws IllegalArgumentException if productId or name is null/empty, or weight is negative.
     */
    public Product(String productId, String name, float weight) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty.");
        }
        if (weight < 0) {
            throw new IllegalArgumentException("Product weight cannot be negative.");
        }

        this.productId = productId;
        this.name = name;
        this.weight = weight;
    }

    // --- Getters ---

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public float getWeight() {
        return weight;
    }

    // --- Standard Methods ---

    @Override
    public String toString() {
        return "Product{" +
                "productId='" + productId + '\'' +
                ", name='" + name + '\'' +
                ", weight=" + weight +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return productId.equals(product.productId);
    }

    @Override
    public int hashCode() {
        return productId.hashCode();
    }
}
