package Inventory.DTO;

public class ProductDTO {
    private int catalogNumber;
    private String productName;
    private String category;
    private String subCategory;
    private String subSubCategory;
    private String manufacturer;
    private int size;
    private int productDemandLevel;
    private int supplyTime;

    private double costPriceBeforeSupplierDiscount;
    private double supplierDiscount;
    private double costPriceAfterSupplierDiscount;
    private double storeDiscount;
    private double salePriceBeforeStoreDiscount;
    private double salePriceAfterStoreDiscount;

    private int quantityInWarehouse;
    private int quantityInStore;
    private int minimumQuantityForAlert;

    // Constructor with parameters
    public ProductDTO(int catalogNumber, String productName, String category, String subCategory,
                      String manufacturer, int size, double costPriceBeforeSupplierDiscount,
                      double supplierDiscount) {
        this.catalogNumber = catalogNumber;
        this.productName = productName;
        this.category = category;
        this.subCategory = subCategory;
        this.manufacturer = manufacturer;
        this.size = size;
        this.costPriceBeforeSupplierDiscount = costPriceBeforeSupplierDiscount;
        this.supplierDiscount = supplierDiscount;
        this.costPriceAfterSupplierDiscount = costPriceBeforeSupplierDiscount * (1 - supplierDiscount / 100);
    }

    // Default constructor
    public ProductDTO() {}

    // Getters and Setters
    public int getCatalogNumber() {
        return catalogNumber;
    }

    public void setCatalogNumber(int catalogNumber) {
        this.catalogNumber = catalogNumber;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getSubSubCategory() {
        return subSubCategory;
    }

    public void setSubSubCategory(String subSubCategory) {
        this.subSubCategory = subSubCategory;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getProductDemandLevel() {
        return productDemandLevel;
    }

    public void setProductDemandLevel(int productDemandLevel) {
        this.productDemandLevel = productDemandLevel;
    }

    public int getSupplyTime() {
        return supplyTime;
    }

    public void setSupplyTime(int supplyTime) {
        this.supplyTime = supplyTime;
    }

    public double getCostPriceBeforeSupplierDiscount() {
        return costPriceBeforeSupplierDiscount;
    }

    public void setCostPriceBeforeSupplierDiscount(double costPriceBeforeSupplierDiscount) {
        this.costPriceBeforeSupplierDiscount = costPriceBeforeSupplierDiscount;
    }

    public double getSupplierDiscount() {
        return supplierDiscount;
    }

    public void setSupplierDiscount(double supplierDiscount) {
        this.supplierDiscount = supplierDiscount;
    }

    public double getCostPriceAfterSupplierDiscount() {
        return costPriceAfterSupplierDiscount;
    }

    public void setCostPriceAfterSupplierDiscount(double costPriceAfterSupplierDiscount) {
        this.costPriceAfterSupplierDiscount = costPriceAfterSupplierDiscount;
    }

    public double getStoreDiscount() {
        return storeDiscount;
    }

    public void setStoreDiscount(double storeDiscount) {
        this.storeDiscount = storeDiscount;
    }

    public double getSalePriceBeforeStoreDiscount() {
        return salePriceBeforeStoreDiscount;
    }

    public void setSalePriceBeforeStoreDiscount(double salePriceBeforeStoreDiscount) {
        this.salePriceBeforeStoreDiscount = salePriceBeforeStoreDiscount;
    }

    public double getSalePriceAfterStoreDiscount() {
        return salePriceAfterStoreDiscount;
    }

    public void setSalePriceAfterStoreDiscount(double salePriceAfterStoreDiscount) {
        this.salePriceAfterStoreDiscount = salePriceAfterStoreDiscount;
    }

    public int getQuantityInWarehouse() {
        return quantityInWarehouse;
    }

    public void setQuantityInWarehouse(int quantityInWarehouse) {
        this.quantityInWarehouse = quantityInWarehouse;
    }

    public int getQuantityInStore() {
        return quantityInStore;
    }

    public void setQuantityInStore(int quantityInStore) {
        this.quantityInStore = quantityInStore;
    }

    public int getMinimumQuantityForAlert() {
        return minimumQuantityForAlert;
    }

    public void setMinimumQuantityForAlert(int minimumQuantityForAlert) {
        this.minimumQuantityForAlert = minimumQuantityForAlert;
    }
}
