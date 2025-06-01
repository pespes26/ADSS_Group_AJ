package Inventory.DTO;

/**
 * Data Transfer Object representing a periodic order in the inventory system.
 */
public class PeriodicOrderDTO {

    private int orderId;
    private int productCatalogNumber;
    private int quantity;
    private int supplierId;
    private String daysInTheWeek;
    private String orderDate;
    private double supplierDiscount;
    private int agreementId;

    /**
     * Empty constructor for frameworks and manual mapping.
     */
    public PeriodicOrderDTO() {}

    /**
     * Full constructor to create a periodic order DTO with all relevant fields.
     *
     * @param orderId              the ID of the periodic order
     * @param productCatalogNumber the catalog number of the product
     * @param quantity             the quantity of the product in the order
     * @param orderDate            the date the order was placed
     * @param supplierDiscount     the supplier discount for this order
     * @param supplierId           the supplier ID
     * @param daysInTheWeek        the days of the week this order recurs on
     * @param agreementId          the ID of the agreement for this order
     */
    public PeriodicOrderDTO(int orderId, int productCatalogNumber, int quantity, String orderDate,
                            double supplierDiscount, int supplierId, String daysInTheWeek, int agreementId) {
        this.orderId = orderId;
        this.productCatalogNumber = productCatalogNumber;
        this.quantity = quantity;
        this.orderDate = orderDate;
        this.supplierDiscount = supplierDiscount;
        this.supplierId = supplierId;
        this.daysInTheWeek = daysInTheWeek;
        this.agreementId = agreementId;
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

    public String getDaysInTheWeek() {
        return daysInTheWeek;
    }

    public int getAgreementId() {
        return agreementId;
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

    public void setDaysInTheWeek(String daysInTheWeek) {
        this.daysInTheWeek = daysInTheWeek;
    }

    public void setAgreementId(int agreementId) {
        this.agreementId = agreementId;
    }
}