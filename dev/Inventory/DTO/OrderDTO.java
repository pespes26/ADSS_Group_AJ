package Inventory.DTO;

/**
 * Data Transfer Object representing an order in the inventory system.
 */
public class OrderDTO {

    private int orderId;
    private int productCatalogNumber;
    private int quantity;
    private String orderDate;
    private double supplierDiscount;
    private int supplyTime;

    /**
     * Empty constructor for frameworks and manual mapping.
     */
    public OrderDTO() {}

    /**
     * Full constructor to create an order DTO with all relevant fields.
     *
     * @param orderId              the ID of the order
     * @param productCatalogNumber the catalog number of the product
     * @param quantity             the quantity of the product in the order
     * @param orderDate            the date the order was placed
     * @param supplierDiscount     the supplier discount for this order
     * @param supplyTime           the supply time in days
     */
    public OrderDTO(int orderId, int productCatalogNumber, int quantity, String orderDate, double supplierDiscount, int supplyTime) {
        this.orderId = orderId;
        this.productCatalogNumber = productCatalogNumber;
        this.quantity = quantity;
        this.orderDate = orderDate;
        this.supplierDiscount = supplierDiscount;
        this.supplyTime = supplyTime;
    }

    // Getters

    /**
     * Gets the ID of the order.
     * @return the order ID
     */
    public int getOrderId() {
        return orderId;
    }

    /**
     * Gets the catalog number of the ordered product.
     * @return the product's catalog number
     */
    public int getProductCatalogNumber() {
        return productCatalogNumber;
    }

    /**
     * Gets the quantity of the product ordered.
     * @return the quantity ordered
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Gets the date the order was placed.
     * @return the order date as a String
     */
    public String getOrderDate() {
        return orderDate;
    }

    /**
     * Gets the discount percentage offered by the supplier.
     * @return the supplier discount
     */
    public double getSupplierDiscount() {
        return supplierDiscount;
    }

    /**
     * Gets the supply time in days.
     * @return the supply time
     */
    public int getSupplyTime() {
        return supplyTime;
    }

    // Setters

    /**
     * Sets the ID of the order.
     * @param orderId the order ID to set
     */
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    /**
     * Sets the catalog number of the product.
     * @param productCatalogNumber the catalog number to set
     */
    public void setProductCatalogNumber(int productCatalogNumber) {
        this.productCatalogNumber = productCatalogNumber;
    }

    /**
     * Sets the quantity of the product ordered.
     * @param quantity the quantity to set
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Sets the date the order was placed.
     * @param orderDate the order date to set
     */
    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    /**
     * Sets the supplier discount for the order.
     * @param supplierDiscount the discount to set
     */
    public void setSupplierDiscount(double supplierDiscount) {
        this.supplierDiscount = supplierDiscount;
    }

    /**
     * Sets the supply time in days.
     * @param supplyTime the supply time to set
     */
    public void setSupplyTime(int supplyTime) {
        this.supplyTime = supplyTime;
    }
}