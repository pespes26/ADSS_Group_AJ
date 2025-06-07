package com.superli.deliveries.presentation;

/**
 * Represents a single delivered item in a destination document,
 * used for presentation purposes.
 * Matches DeliveredItemDetailsView from the Presentation Layer spec.
 */
public class DeliveredItemDetailsView {

    private final String productId;
    private final int quantity;

    /**
     * Constructs a DeliveredItemDetailsView object.
     *
     * @param productId ID of the delivered product.
     * @param quantity  Quantity of the product delivered.
     */
    public DeliveredItemDetailsView(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    /**
     * @return Product ID (identifier of the product).
     */
    public String getProductId() {
        return productId;
    }

    /**
     * @return Quantity of the delivered product.
     */
    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "DeliveredItemDetailsView{" +
                "productId='" + productId + '\'' +
                ", quantity=" + quantity +
                '}';
    }

    // equals() and hashCode() can be added if needed for collection operations
}
