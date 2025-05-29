package Inventory.DTO;

public class OrderDTO {
    private int order_id;
    private int product_Catalog_Number;
    private int quantity;

    private String order_date;

    public OrderDTO(){}
    public OrderDTO(int order_id, int product_Catalog_Number, int quantity, String order_date) {
        this.order_id = order_id;
        this.product_Catalog_Number = product_Catalog_Number;
        this.quantity = quantity;
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

    public String getOrderDate() {
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

    public void setOrderDate(String order_date) {
        this.order_date = order_date;
    }
}
