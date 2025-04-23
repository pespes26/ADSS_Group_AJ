package Domain;

/**
 * Represents a product in the inventory system.
 * Each product is uniquely identified by a catalog number and contains information
 * about category, sub-category, demand, pricing, quantity, discounts, and manufacturer.
 */
public class Product {

    private int catalog_number; // Unique identifier for the product
    private String product_name; // Name of the product
    private String category; // Main category the product belongs to
    private String sub_category; // Sub-category under the main category
    private int product_demand_level; // Demand level (1 - low, 5 - high)
    private int supply_time; // Number of days required to supply the product
    private int total_quantity; // Total number of this product in stock (store + warehouse)
    private int minimum_quantity_for_alert; // Minimum required stock before triggering a reorder alert
    private int quantity_in_store; // Quantity available in the interior store
    private int quantity_in_warehouse; // Quantity available in the warehouse
    private String manufacturer; // Name of the product's manufacturer
    private Discount discount; // Discount object containing value and active dates (store or supplier)
    private double supplier_discount; // Discount percentage received from supplier
    private double cost_price_before_supplier_discount; // Base cost before supplier discount is applied
    private double cost_price_after_supplier_discount; // Cost after applying the supplier discount
    private double store_discount; // Discount percentage given by the store to customers
    private double sale_price_before_store_discount; // Sale price before applying store discount
    private double sale_price_after_store_discount; // Final price after applying store discount

    /**
     * @return The catalog number that uniquely identifies the product.
     */
    public int getCatalogNumber() {
        return catalog_number;
    }

    /**
     * @param catalog_number The catalog number to set.
     */
    public void setCatalogNumber(int catalog_number) {
        this.catalog_number = catalog_number;
    }

    /**
     * Gets the product name.
     *
     * @return the product name
     */
    public String getProductName() {
        return product_name;
    }

    /**
     * Sets the product name.
     *
     * @param name the name to set
     */
    public void setProductName(String name) {
        this.product_name = name;
    }

    /**
     * @return The product's category.
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category The category to assign to the product.
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * @return The product's sub-category.
     */
    public String getSubCategory() {
        return sub_category;
    }

    /**
     * @param sub_category The sub-category to assign to the product.
     */
    public void setSubCategory(String sub_category) {
        this.sub_category = sub_category;
    }

    /**
     * @return The product's demand level (1–5).
     */
    public int getProductDemandLevel() {
        return product_demand_level;
    }

    /**
     * @param product_demand_level The demand level to set (1–5).
     */
    public void setProductDemandLevel(int product_demand_level) {
        this.product_demand_level = product_demand_level;
    }

    /**
     * @return The number of days required to restock the product.
     */
    public int getSupplyTime() {
        return supply_time;
    }

    /**
     * @param supply_time The number of days for restocking.
     */
    public void setSupplyTime(int supply_time) {
        this.supply_time = supply_time;
    }

    /**
     * @return The total quantity of the product (store + warehouse).
     */
    public int getTotalQuantity() {
        return total_quantity;
    }

    /**
     * @param total_quantity The total quantity to set for the product.
     */
    public void setTotalQuantity(int total_quantity) {
        this.total_quantity = total_quantity;
    }

    /**
     * @return The minimum required quantity before a reorder alert is triggered.
     */
    public int getMinimumQuantityForAlert() {
        return minimum_quantity_for_alert;
    }

    /**
     * @param minimum_quantity_for_alert The threshold below which a reorder alert should be issued.
     */
    public void setMinimumQuantityForAlert(int minimum_quantity_for_alert) {
        this.minimum_quantity_for_alert = minimum_quantity_for_alert;
    }

    /**
     * @return The quantity of the product currently available in the store.
     */
    public int getQuantityInStore() {
        return quantity_in_store;
    }

    /**
     * @param quantity_in_store The quantity to set as available in the store.
     */
    public void setQuantityInStore(int quantity_in_store) {
        this.quantity_in_store = quantity_in_store;
    }

    /**
     * @return The quantity of the product currently available in the warehouse.
     */
    public int getQuantityInWarehouse() {
        return quantity_in_warehouse;
    }

    /**
     * @param quantity_in_warehouse The quantity to set as available in the warehouse.
     */
    public void setQuantityInWarehouse(int quantity_in_warehouse) {
        this.quantity_in_warehouse = quantity_in_warehouse;
    }

    /**
     * @return The name of the manufacturer of the product.
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * @param manufacturer The manufacturer name to assign to the product.
     */
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    /**
     * @return The discount object currently associated with the product (store or supplier discount).
     */
    public Discount getDiscount() {
        return discount;
    }

    /**
     * @param discount The discount object to apply to the product.
     */
    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    /**
     * @return The discount percentage given by the supplier.
     */
    public double getSupplierDiscount() {
        return supplier_discount;
    }

    /**
     * @param supplier_discount Discount value from supplier (percentage).
     */
    public void setSupplierDiscount(double supplier_discount) {
        this.supplier_discount = supplier_discount;
    }

    /**
     * @return Cost price before applying supplier discount.
     */
    public double getCostPriceBeforeSupplierDiscount() {
        return cost_price_before_supplier_discount;
    }

    /**
     * @param cost_price_before_supplier_discount The base cost price before any discount.
     */
    public void setCostPriceBeforeSupplierDiscount(double cost_price_before_supplier_discount) {
        this.cost_price_before_supplier_discount = cost_price_before_supplier_discount;
    }

    /**
     * @return Cost price after applying supplier discount.
     */
    public double getCostPriceAfterSupplierDiscount() {
        return cost_price_after_supplier_discount;
    }

    /**
     * @param cost_price_after_supplier_discount The cost price after discount from supplier.
     */
    public void setCostPriceAfterSupplierDiscount(double cost_price_after_supplier_discount) {
        this.cost_price_after_supplier_discount = cost_price_after_supplier_discount;
    }

    /**
     * @return Discount percentage applied by the store.
     */
    public double getStoreDiscount() {
        return store_discount;
    }

    /**
     * @param store_discount Discount value applied by store (percentage).
     */
    public void setStoreDiscount(double store_discount) {
        this.store_discount = store_discount;
    }

    /**
     * @return Sale price before applying store discount.
     */
    public double getSalePriceBeforeStoreDiscount() {
        return sale_price_before_store_discount;
    }

    /**
     * @param sale_price_before_store_discount The initial markup price before store discount.
     */
    public void setSalePriceBeforeStoreDiscount(double sale_price_before_store_discount) {
        this.sale_price_before_store_discount = sale_price_before_store_discount;
    }

    /**
     * @return Sale price after applying store discount.
     */
    public double getSalePriceAfterStoreDiscount() {
        return sale_price_after_store_discount;
    }

    /**
     * @param sale_price_after_store_discount The final price after applying store discount.
     */
    public void setSalePriceAfterStoreDiscount(double sale_price_after_store_discount) {
        this.sale_price_after_store_discount = sale_price_after_store_discount;
    }
}
