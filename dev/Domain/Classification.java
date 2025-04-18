package Domain;
/**
 * Represents the classification details of a product, including size, category,
 * demand, supply time, pricing, and discounts.
 * This class belongs to the Domain layer and encapsulates core business data for products.
 */
public class Classification {

    /** The catalog number of the product */
    private int catalog_num;

    /** The main category of the product */
    private String category;

    /** The sub-category of the product */
    private String sub_category;

    /** The size of the product: 1 = small, 2 = medium, 3 = big */
    private int product_size;

    /** The cost price of the product */
    private double cost_price;

    /** The product's demand level (1 to 5) */
    private int product_demand;

    /** The supply time in days */
    private int supply_time;

    /** The minimum amount before an alert should be raised */
    private int min_amount_for_alert;

    /** The manufacturer of the product */
    private String manufacturer;

    /** The current amount of this product in stock */
    private int current_product_amount;

    /** The discount provided by the supplier (in %) */
    private int supplier_discount;

    /** The discount offered in the store (in %) */
    private int store_discount;

    /** The sale price of the product (default is -1 if not sold) */
    private double sale_price;

    /**
     * Constructs a new Classification with default values:
     * current amount = 0, store & supplier discounts = 0, sale price = -1.
     */
    public Classification() {
        this.current_product_amount = 0;
        this.store_discount = 0;
        this.supplier_discount = 0;
        this.sale_price = -1;
    }

    /**
     * Gets the catalog number.
     *
     * @return the catalog number
     */
    public int getCatalogNumber() {
        return catalog_num;
    }

    /**
     * Sets the catalog number.
     *
     * @param catalog_num the catalog number to set
     */
    public void setCatalogNum(int catalog_num) {
        this.catalog_num = catalog_num;
    }

    /**
     * Gets the product's main category.
     *
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the product's main category.
     *
     * @param category the category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Gets the product's sub-category.
     *
     * @return the sub-category
     */
    public String getSubcategory() {
        return sub_category;
    }

    /**
     * Sets the product's sub-category.
     *
     * @param subcategory the sub-category to set
     */
    public void setSubcategory(String subcategory) {
        this.sub_category = subcategory;
    }

    /**
     * Gets the size of the product.
     *
     * @return the product size (1 = small, 2 = medium, 3 = big)
     */
    public int getProductSize() {
        return product_size;
    }

    /**
     * Sets the size of the product.
     *
     * @param size the size to set (1 = small, 2 = medium, 3 = big)
     */
    public void setProductSize(int size) {
        this.product_size = size;
    }

    /**
     * Gets the cost price of the product.
     *
     * @return the cost price
     */
    public double getCostPrice() {
        return cost_price;
    }

    /**
     * Sets the cost price of the product.
     *
     * @param cost_price the price to set
     */
    public void setCostPrice(double cost_price){
        this.cost_price = cost_price;
    }

    /**
     * Gets the demand level of the product.
     *
     * @return the product demand (1-10)
     */
    public int getProductDemand() {
        return product_demand;
    }

    /**
     * Sets the demand level of the product.
     *
     * @param demand the demand to set (1-10)
     */
    public void setProductDemand(int demand) {
        this.product_demand = demand;
    }

    /**
     * Gets the supply time in days.
     *
     * @return the supply time
     */
    public int getSupplyTime() {
        return supply_time;
    }

    /**
     * Sets the supply time in days.
     *
     * @param supply_time the supply time to set
     */
    public void setSupplyTime(int supply_time) {
        this.supply_time = supply_time;
    }

    /**
     * Gets the minimum amount for alert.
     *
     * @return the minimum amount that triggers an alert
     */
    public int getMinAmountForAlert() {
        return min_amount_for_alert;
    }

    /**
     * Sets the minimum amount for alert.
     *
     * @param min_amount_for_alert the alert threshold
     */
    public void setMinAmountForAlert(int min_amount_for_alert) {
        this.min_amount_for_alert = min_amount_for_alert;
    }

    /**
     * Gets the manufacturer name.
     *
     * @return the manufacturer
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * Sets the manufacturer name.
     *
     * @param manufacturer the name to set
     */
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    /**
     * Gets the current amount of this product in stock.
     *
     * @return the current amount
     */
    public int getCurrentProductAmount() {
        return current_product_amount;
    }

    /**
     * Sets the current amount of this product in stock.
     *
     * @param current_amount the amount to set
     */
    public void setCurrentProductAmount(int current_amount) {
        this.current_product_amount = current_amount;
    }

    /**
     * Gets the supplier discount percentage.
     *
     * @return the supplier discount
     */
    public int getSupplierDiscount() {
        return supplier_discount;
    }

    /**
     * Sets the supplier discount percentage.
     *
     * @param supplier_discount the discount to set
     */
    public void setSupplierDiscount(int supplier_discount) {
        this.supplier_discount = supplier_discount;
    }

    /**
     * Gets the store discount percentage.
     *
     * @return the store discount
     */
    public int getStoreDiscount() {
        return store_discount;
    }

    /**
     * Sets the store discount percentage.
     *
     * @param store_discount the discount to set
     */
    public void setStoreDiscount(int store_discount) {
        this.store_discount = store_discount;
    }

    /**
     * Gets the sale price of the product.
     *
     * @return the sale price
     */
    public double getSalePrice() {
        return sale_price;
    }

    /**
     * Sets the sale price of the product.
     *
     * @param sale_price the sale price to set
     */
    public void setSalePrice(double sale_price) {
        this.sale_price = sale_price;
    }
}
