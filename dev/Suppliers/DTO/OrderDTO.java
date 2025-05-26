package Suppliers.DTO;

import java.time.LocalDateTime;
import java.util.Map;

public class OrderDTO {
    private int orderID; // Unique identifier of the order
    private long phoneNumber; // Customer's phone number
    private LocalDateTime orderDate; // The date and time when the order was placed
    private Map<Integer, Integer> productsInOrder; // Maps productID -> quantity

    public OrderDTO(int orderID, long phoneNumber, LocalDateTime orderDate, Map<Integer, Integer> productsInOrder) {
        this.orderID = orderID;
        this.phoneNumber = phoneNumber;
        this.orderDate = orderDate;
        this.productsInOrder = productsInOrder;
    }

    public int getOrderID() {
        return orderID;
    }

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public Map<Integer, Integer> getProductsInOrder() {
        return productsInOrder;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public void setProductsInOrder(Map<Integer, Integer> productsInOrder) {
        this.productsInOrder = productsInOrder;
    }
}
