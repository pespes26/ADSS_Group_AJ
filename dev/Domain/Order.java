/**
 * The Order class represents a customer's order.
 * It includes the order ID, customer phone number, order date,
 * and a map of products included in the order.
 *
 * Each entry in the map links a product ID to a pair of (quantity, price).
 */
package Domain;

import java.util.Date;
import java.util.Map;

public class Order {
    private int orderID; // Unique identifier of the order
    public int phoneNumber; // Customer's phone number
    private Date orderDate; // The date when the order was placed
    private Map<Integer, Map.Entry<Integer, Double>> productsInOrder; // Maps productID -> (quantity, price)

    /**
     * Constructs a new Order object with the provided details.
     *
     * @param orderID          the unique ID of the order
     * @param phoneNumber      the customer's phone number
     * @param orderDate        the date the order was placed
     * @param productsInOrder  a map of product IDs to pairs of (quantity, price)
     */
    public Order(int orderID, int phoneNumber, Date orderDate, Map<Integer, Map.Entry<Integer, Double>> productsInOrder) {
        this.orderID = orderID; // Set the order ID
        this.phoneNumber = phoneNumber; // Set the customer's phone number
        this.orderDate = orderDate; // Set the order date
        this.productsInOrder = productsInOrder; // Set the products in the order
    }

    /**
     * Gets the ID of the order.
     *
     * @return the order ID
     */
    public int getOrderID() {
        return orderID; // Return the order ID
    }

    /**
     * Gets the customer's phone number.
     *
     * @return the phone number
     */
    public int getPhoneNumber() {
        return phoneNumber; // Return the customer's phone number
    }

    /**
     * Updates the customer's phone number.
     *
     * @param phoneNumber the new phone number
     */
    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber; // Set the new phone number
    }

    /**
     * Gets the date of the order.
     *
     * @return the order date
     */
    public Date getOrderDate() {
        return orderDate; // Return the order date
    }

    /**
     * Gets the map of products in the order.
     * Each entry maps a product ID to a pair (quantity, price).
     *
     * @return the products in the order
     */
    public Map<Integer, Map.Entry<Integer, Double>> getProductsInOrder() {
        return productsInOrder; // Return the products and their quantities and prices
    }
}
