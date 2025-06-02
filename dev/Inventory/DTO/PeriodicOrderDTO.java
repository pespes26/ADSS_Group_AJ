package Inventory.DTO;

/**
 * Data Transfer Object representing a periodic order in the inventory system.
 */
public class PeriodicOrderDTO {

    private int orderId;
    private int productCatalogNumber;
    private int quantity;
    private int supplierId;
    private String supplierName; // ✅ חדש
    private String daysInTheWeek;
    private String orderDate;
    private double supplierDiscount;
    private int agreementId;
    private int branchId;

    /**
     * Empty constructor for frameworks and manual mapping.
     */
    public PeriodicOrderDTO() {}

    /**
     * Full constructor
     */
    public PeriodicOrderDTO(int orderId, int productCatalogNumber, int quantity, String orderDate,
                            double supplierDiscount, int supplierId, String supplierName,
                            String daysInTheWeek, int agreementId, int branchId) {
        this.orderId = orderId;
        this.productCatalogNumber = productCatalogNumber;
        this.quantity = quantity;
        this.orderDate = orderDate;
        this.supplierDiscount = supplierDiscount;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.daysInTheWeek = daysInTheWeek;
        this.agreementId = agreementId;
        this.branchId = branchId;
    }

    // Getters
    public int getOrderId() {
        return orderId;
    }

    public int getProductCatalogNumber() {
        return productCatalogNumber;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public double getSupplierDiscount() {
        return supplierDiscount;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public String getDaysInTheWeek() {
        return daysInTheWeek;
    }

    public int getAgreementId() {
        return agreementId;
    }

    public int getBranchId() {
        return branchId;
    }

    // Setters
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setProductCatalogNumber(int productCatalogNumber) {
        this.productCatalogNumber = productCatalogNumber;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public void setSupplierDiscount(double supplierDiscount) {
        this.supplierDiscount = supplierDiscount;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public void setDaysInTheWeek(String daysInTheWeek) {
        this.daysInTheWeek = daysInTheWeek;
    }

    public void setAgreementId(int agreementId) {
        this.agreementId = agreementId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }
}
