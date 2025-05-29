package Inventory.DTO;

public class ProductDTO {
    private int catalogNumber;
    private String productName;
    private String category;
    private String subCategory;
    private String subSubCategory;
    private String manufacturer;
    private double price;
    private double supplierDiscount;
    private double storeDiscount;

    // Default constructor
    public ProductDTO() {}

    // Constructor with all fields
    public ProductDTO(int catalogNumber, String productName, String category,
                      String subCategory, String subSubCategory,
                      String manufacturer, double price,
                      double supplierDiscount, double storeDiscount) {
        this.catalogNumber = catalogNumber;
        this.productName = productName;
        this.category = category;
        this.subCategory = subCategory;
        this.subSubCategory = subSubCategory;
        this.manufacturer = manufacturer;
        this.price = price;
        this.supplierDiscount = supplierDiscount;
        this.storeDiscount = storeDiscount;
    }

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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getSupplierDiscount() {
        return supplierDiscount;
    }

    public void setSupplierDiscount(double supplierDiscount) {
        this.supplierDiscount = supplierDiscount;
    }

    public double getStoreDiscount() {
        return storeDiscount;
    }

    public void setStoreDiscount(double storeDiscount) {
        this.storeDiscount = storeDiscount;
    }
}
