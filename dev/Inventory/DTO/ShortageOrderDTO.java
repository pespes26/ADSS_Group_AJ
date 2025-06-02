package Inventory.DTO;

import java.sql.SQLException;
import java.util.List;

public class ShortageOrderDTO {
    private int orderId;
    private int productCatalogNumber;
    private int quantity;
    private double costPriceBeforeSupplierDiscount;
    private double supplierDiscount;
    private String orderDate;

    public ShortageOrderDTO(int orderId, int productCatalogNumber, int quantity,
                            double costPriceBeforeSupplierDiscount, double supplierDiscount, String orderDate) {
        this.orderId = orderId;
        this.productCatalogNumber = productCatalogNumber;
        this.quantity = quantity;
        this.costPriceBeforeSupplierDiscount = costPriceBeforeSupplierDiscount;
        this.supplierDiscount = supplierDiscount;
        this.orderDate = orderDate;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getProductCatalogNumber() {
        return productCatalogNumber;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getCostPriceBeforeSupplierDiscount() {
        return costPriceBeforeSupplierDiscount;
    }

    public double getSupplierDiscount() {
        return supplierDiscount;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setProductCatalogNumber(int productCatalogNumber) {
        this.productCatalogNumber = productCatalogNumber;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setCostPriceBeforeSupplierDiscount(double costPriceBeforeSupplierDiscount) {
        this.costPriceBeforeSupplierDiscount = costPriceBeforeSupplierDiscount;
    }

    public void setSupplierDiscount(double supplierDiscount) {
        this.supplierDiscount = supplierDiscount;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }


}