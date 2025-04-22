/**
 * The Order class represents a customer's order.
 * It includes the order ID, customer phone number, order date,
 * and a map of products included in the order.
 *
 * Each entry in the map links a product ID to its quantity.
 */
package Domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class Order {
    private int orderID; // Unique identifier of the order
    private int phoneNumber; // Customer's phone number
    private LocalDateTime orderDate; // The date and time when the order was placed
    private Map<Integer, Integer> productsInOrder; // Maps productID -> quantity

    /**
     * Constructs a new Order object with the provided details.
     *
     * @param orderID          the unique ID of the order
     * @param phoneNumber      the customer's phone number
     * @param orderDate        the date and time the order was placed
     * @param productsInOrder  a map of product IDs to their quantities
     */
    public Order(int orderID, int phoneNumber, LocalDateTime orderDate, Map<Integer, Integer> productsInOrder) {
        this.orderID = orderID;
        this.phoneNumber = phoneNumber;
        this.orderDate = orderDate;
        this.productsInOrder = productsInOrder;
    }

    public int getOrderID() {
        return orderID;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    /**
     * Returns a formatted version of the order date as a string.
     * @return date in format "dd/MM/yyyy HH:mm"
     */
    public String getFormattedOrderDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return orderDate.format(formatter);
    }

    public Map<Integer, Integer> getProductsInOrder() {
        return productsInOrder;
    }

}
