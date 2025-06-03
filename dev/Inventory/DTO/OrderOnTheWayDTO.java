package Inventory.DTO;

public class OrderOnTheWayDTO {
    private int orderId;
    private int productCatalogNumber;
    private int quantity;
    private int supplierId;
    private int branchId;
    private String expectedDeliveryDate;
    private String orderCreationDate;
    private int agreementId;
    private boolean isPeriodic;
    private String status;

    public OrderOnTheWayDTO(int orderId, int productCatalogNumber, int quantity, int supplierId, int branchId,
                            String expectedDeliveryDate, String orderCreationDate, int agreementId, boolean isPeriodic) {
        this.orderId = orderId;
        this.productCatalogNumber = productCatalogNumber;
        this.quantity = quantity;
        this.supplierId = supplierId;
        this.branchId = branchId;
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.orderCreationDate = orderCreationDate;
        this.agreementId = agreementId;
        this.isPeriodic = isPeriodic;
        this.status = "IN_TRANSIT"; // Default status for new orders
    }

    public int getOrderId() { return orderId; }
    public int getProductCatalogNumber() { return productCatalogNumber; }
    public int getQuantity() { return quantity; }
    public int getSupplierId() { return supplierId; }
    public int getBranchId() { return branchId; }
    public String getExpectedDeliveryDate() { return expectedDeliveryDate; }
    public String getOrderCreationDate() { return orderCreationDate; }
    public int getAgreementId() { return agreementId; }
    public boolean isPeriodic() { return isPeriodic; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}