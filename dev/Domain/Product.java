package Domain;


public class Product {


    private int catalog_number;

    private String product_name;

    private String category;

    private String sub_category;

    private int product_demand_level; // integer number between 1-5, 1 is the lowest and 5 is the highest

    private int supply_time; //in days

    private int total_quantity;

    private int minimum_quantity_for_alert;

    private int quantity_in_store;

    private int quantity_in_warehouse;

    private String manufacturer;

    private Discount discount;

    private double supplier_discount;

    private double cost_price_before_supplier_discount;

    private double cost_price_after_supplier_discount;

    private double store_discount;

    private double sale_price_before_store_discount;

    private double sale_price_after_store_discount;


    public int getCatalogNumber() {
        return catalog_number;
    }

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return sub_category;
    }

    public void setSubCategory(String sub_category) {
        this.sub_category = sub_category;
    }

    public int getProductDemandLevel() {
        return product_demand_level;
    }

    public void setProductDemandLevel(int product_demand_level) {
        this.product_demand_level = product_demand_level;
    }

    public int getSupplyTime() {
        return supply_time;
    }

    public void setSupplyTime(int supply_time) {
        this.supply_time = supply_time;
    }

    public int getTotalQuantity() {
        return total_quantity;
    }

    public void setTotalQuantity(int total_quantity) {
        this.total_quantity = total_quantity;
    }

    public int getMinimumQuantityForAlert() {
        return minimum_quantity_for_alert;
    }

    public void setMinimumQuantityForAlert(int minimum_quantity_for_alert) {
        this.minimum_quantity_for_alert = minimum_quantity_for_alert;
    }

    public int getQuantityInStore() {
        return quantity_in_store;
    }

    public void setQuantityInStore(int quantity_in_store) {
        this.quantity_in_store = quantity_in_store;
    }

    public int getQuantityInWarehouse() {
        return quantity_in_warehouse;
    }

    public void setQuantityInWarehouse(int quantity_in_warehouse) {
        this.quantity_in_warehouse = quantity_in_warehouse;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    public double getSupplierDiscount() {
        return supplier_discount;
    }

    //need to use here the discount class
    public void setSupplierDiscount(double supplier_discount) {
        this.supplier_discount = supplier_discount;
    }

    public double getCostPriceBeforeSupplierDiscount() {
        return cost_price_before_supplier_discount;
    }

    public void setCostPriceBeforeSupplierDiscount(double cost_price_before_supplier_discount) {
        this.cost_price_before_supplier_discount = cost_price_before_supplier_discount;
    }

    public double getCostPriceAfterSupplierDiscount() {
        return cost_price_after_supplier_discount;
    }

    public void setCostPriceAfterSupplierDiscount(double cost_price_after_supplier_discount) {
        this.cost_price_after_supplier_discount = cost_price_after_supplier_discount;
    }

    public double getStoreDiscount() {
        return store_discount;
    }

    //need to use here the discount class
    public void setStoreDiscount(double store_discount) {
        this.store_discount = store_discount;
    }

    public double getSalePriceBeforeStoreDiscount() {
        return sale_price_before_store_discount;
    }

    public void setSalePriceBeforeStoreDiscount(double sale_price_before_store_discount) {
        this.sale_price_before_store_discount = sale_price_before_store_discount;
    }

    public double getSalePriceAfterStoreDiscount() {
        return sale_price_after_store_discount;
    }

    public void setSalePriceAfterStoreDiscount(double sale_price_after_store_discount) {
        this.sale_price_after_store_discount = sale_price_after_store_discount;
    }
}
