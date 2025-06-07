package com.superli.deliveries.dto;

public class DeliveredItemDTO {
    private int id;
    private int destinationDocId;
    private int productId;
    private int quantity;

    public DeliveredItemDTO() {}

    public DeliveredItemDTO(int id, int destinationDocId, int productId, int quantity) {
        this.id = id;
        this.destinationDocId = destinationDocId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDestinationDocId() { return destinationDocId; }
    public void setDestinationDocId(int destinationDocId) { this.destinationDocId = destinationDocId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}