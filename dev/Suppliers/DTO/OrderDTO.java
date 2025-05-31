package Suppliers.DTO;

import java.time.LocalDateTime;
import java.util.List;

public class OrderDTO {
    private int orderID; // מזהה ייחודי להזמנה
    private long phoneNumber; // טלפון של הלקוח
    private LocalDateTime orderDate; // תאריך ביצוע ההזמנה
    private List<OrderItemDTO> items; // רשימת מוצרים בהזמנה

    // בנאי חדש

    public OrderDTO(long phoneNumber, LocalDateTime orderDate, List<OrderItemDTO> items) {
        orderID = 0;
        this.phoneNumber = phoneNumber;
        this.orderDate = orderDate;
        this.items = items;
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

    public List<OrderItemDTO> getItems() {
        return items;
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

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }
}
