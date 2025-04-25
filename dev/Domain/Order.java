
package Domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class Order {
    private int orderID; // Unique identifier of the order
    private long phoneNumber; // Customer's phone number
    private LocalDateTime orderDate; // The date and time when the order was placed
    private Map<Integer, Integer> productsInOrder; // Maps productID -> quantity


    public Order(int orderID, long phoneNumber, LocalDateTime orderDate, Map<Integer, Integer> productsInOrder) {
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


    public String getFormattedOrderDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return orderDate.format(formatter);
    }

    public Map<Integer, Integer> getProductsInOrder() {
        return productsInOrder;
    }

}
