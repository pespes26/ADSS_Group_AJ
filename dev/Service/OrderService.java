package Service;

import Domain.Order;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * The OrderService class is responsible for managing all customer orders.
 * It supports creating orders, checking their existence, retrieving orders, and accessing their product contents.
 */
public class OrderService {
    private final HashMap<Integer, Order> orderHashMap; // Stores all orders using orderID as the key

    /**
     * Constructs a new OrderService with an empty order map.
     */
    public OrderService() {
        orderHashMap = new HashMap<>(); // Initializes the order map
    }

    /**
     * Creates a new order and adds it to the order map.
     *
     * @param orderID         the unique identifier for the order
     * @param phoneNumber     the customer's phone number
     * @param orderDate       the date and time when the order was created
     * @param productsInOrder a map of product IDs to their quantities in the order
     */
    public void createOrder(int orderID, long phoneNumber, LocalDateTime orderDate, Map<Integer, Integer> productsInOrder) {
        Order order = new Order(orderID, phoneNumber, orderDate, productsInOrder); // Create a new Order object using all collected data
        this.orderHashMap.put(orderID, order); // Add the order to the internal order list, using its ID as the key
    }

    /**
     * Searches for and retrieves an order by its ID.
     *
     * @param orderID the ID of the order to search for
     * @return the corresponding Order object, or null if not found
     */
    public Order searchOrderById(int orderID) {
        return orderHashMap.get(orderID); // Returns the order from the map using the given ID, or null if not found
    }

    /**
     * Returns the map of product IDs to quantities for a given order.
     *
     * @param orderID the ID of the order
     * @return the map of products in the order, or null if the order is not found
     */
    public Map<Integer, Integer> getProductsInOrder(int orderID) {
        Order order = orderHashMap.get(orderID);
        if (order != null) {
            return order.getProductsInOrder();
        }
        return null;
    }

    /**
     * Checks if an order with the given ID exists in the system.
     *
     * @param order_ID the ID of the order to check
     * @return true if the order exists, false otherwise
     */
    public boolean thereIsOrder(int order_ID) {
        return orderHashMap.containsKey(order_ID);
    }
}
