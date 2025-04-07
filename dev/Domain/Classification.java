package Domain;

public class Classification {
    private int catalog_num;
    private String category;
    private String sub_category;
    private int product_size; //3 - big, 2 - medium, 1 - small
    private double cost_price;
    private int product_demand; //rated between 1 and 10
    private int supply_time; //in days
    private int min_amount_for_alert; //derived from supplyTime and demand
    private String manufacturer;
    private int current_product_amount;
    private int supplier_discount; // the default is 0
    private int store_discount; //the default is 0
    private double sale_price; //the default is -1

    public Classification() {
        this.current_product_amount = 0;
        this.store_discount = 0;
        this.supplier_discount = 0;
        this.sale_price = -1;
    }

    public int getCatalogNumber() {
        return catalog_num;
    }

    public void setCatalogNum(int catalog_num) {
        this.catalog_num = catalog_num;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubcategory() {
        return sub_category;
    }

    public void setSubcategory(String subcategory) {
        this.sub_category = subcategory;
    }

    public int getProductSize() {
        return product_size;
    }

    public void setProductSize(int size) {
        this.product_size = size;
    }

    public double getCostPrice() {
        return cost_price;
    }

    public void setCostPrice(double cost_price){
        this.cost_price = cost_price;
    }

    public int getProductDemand() {
        return product_demand;
    }

    public void setProductDemand(int demand) {
        this.product_demand = demand;
    }

    public int getSupplyTime() {
        return supply_time;
    }

    public void setSupplyTime(int supply_time) {
        this.supply_time = supply_time;
    }
    
    public int getMinAmountForAlert() {
        return min_amount_for_alert;
    }
    
    public void setMinAmountForAlert(int min_amount_for_alert) {
        this.min_amount_for_alert = min_amount_for_alert;
    }
    
    public String getManufacturer() {
        return manufacturer;
    }
    
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
    
    public int getCurrentProductAmount() {
        return current_product_amount;
    }
    
    public void setCurrentProductAmount(int current_amount) {
        this.current_product_amount = current_amount;
    }

    public int getSupplierDiscount() {
        return supplier_discount;
    }

    public void setSupplierDiscount(int supplier_discount) {
        this.supplier_discount = supplier_discount;
    }

    public int getStoreDiscount() {
        return store_discount;
    }

    public void setStoreDiscount(int store_discount) {
        this.store_discount = store_discount;
    }

    public double getSalePrice() {
        return sale_price;
    }

    public void setSalePrice(double sale_price) {
        this.sale_price = sale_price;
    }



}
