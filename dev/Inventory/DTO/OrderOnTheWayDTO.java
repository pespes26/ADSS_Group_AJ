package Inventory.DTO;

public class OrderOnTheWayDTO {
    private int order_id;
    private int product_Catalog_Number;
    private int quantity;
    private double price;
    private double discount;
    private String order_date;

    public OrderOnTheWayDTO() {}

    public OrderOnTheWayDTO(int order_id, int product_Catalog_Number, int quantity, double price, double discount, String order_date) {
        this.order_id = order_id;
        this.product_Catalog_Number = product_Catalog_Number;
        this.quantity = quantity;
        this.price = price;
        this.discount = discount;
        this.order_date = order_date;
    }

    public int getOrderId() {
        return order_id;
    }

    public int getProductCatalogNumber() {
        return product_Catalog_Number;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public double getDiscount() {
        return discount;
    }

    public String getOrderdate() {
        return order_date;
    }

    public void setOrderId(int order_id) {
        this.order_id = order_id;
    }

    public void setProductCatalogNumber(int product_Catalog_Number) {
        this.product_Catalog_Number = product_Catalog_Number;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public void setOrderDate(String order_date) {
        this.order_date = order_date;
    }
}

