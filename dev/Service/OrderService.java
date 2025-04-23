/**
 * The OrderService class is responsible for managing customer orders.
 * It handles the creation and storage of orders, as well as searching orders by their ID.
 *
 * Each order includes product information (quantity and price), which is dynamically calculated
 * using the ProductService to determine the best available price.
 */
package Service;

import Domain.Order;
import Domain.Product;

import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OrderService {
    private HashMap<Integer, Order> orderHashMap; // Stores all orders using orderID as the key

    /**
     * Constructs a new OrderService with an empty order map.
     */
    public OrderService() {
        orderHashMap = new HashMap<>(); // Initializes the order map
    }

//    public boolean existsZeroOrders() {
//        return orderHashMap.isEmpty();
//    }


    public void createOrder(int orderID, int phoneNumber, LocalDateTime orderDate, Map<Integer, Integer> productsInOrder) {
        Order order = new Order(orderID, phoneNumber, orderDate, productsInOrder); // Create a new Order object using all collected data
        this.orderHashMap.put(orderID, order); // Add the order to the internal order list, using its ID as the key
    }

    /**
     * Searches for an order by its ID.
     *
     * @param orderID the ID of the order to search for
     * @return the matching Order object, or null if not found
     */
    public Order searchOrderById(int orderID) {
        return orderHashMap.get(orderID); // Returns the order from the map using the given ID, or null if not found
    }

    public Map<Integer, Integer> getProductsInOrder(int orderID) {
        Order order = orderHashMap.get(orderID);
        if (order != null) {
            return order.getProductsInOrder();
        }
        return null;
    }

    public boolean thereIsOrder(int order_ID) {
        return orderHashMap.containsKey(order_ID);
    }

}
