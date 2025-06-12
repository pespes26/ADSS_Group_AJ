package com.superli.deliveries.dto.del;

public class DeliveredItemDTO {
    private String id;
    private String destinationDocId;
    private String productId;
    private int quantity;

    public DeliveredItemDTO() {}

    public DeliveredItemDTO(String id, String destinationDocId, String productId, int quantity) {
        this.id = id;
        this.destinationDocId = destinationDocId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDestinationDocId() { return destinationDocId; }
    public void setDestinationDocId(String destinationDocId) { this.destinationDocId = destinationDocId; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}