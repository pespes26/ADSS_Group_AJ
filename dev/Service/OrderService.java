/**
 * The OrderService class is responsible for managing customer orders.
 * It handles the creation and storage of orders, as well as searching orders by their ID.
 *
 * Each order includes product information (quantity and price), which is dynamically calculated
 * using the ProductService to determine the best available price.
 */
package Service;

import Domain.Order;

import java.util.AbstractMap;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OrderService {
    private HashMap<Integer, Order> orderHashMap; // Stores all orders using orderID as the key
    public ProductService productService; // Reference to the ProductService, used to get best prices

    /**
     * Constructs a new OrderService with an empty order map.
     */
    public OrderService() {
        orderHashMap = new HashMap<>(); // Initializes the order map
    }

    /**
     * Creates a new order and adds it to the order list.
     * For each product in the order, the best price is retrieved from the ProductService.
     *
     * @param orderID          the unique ID of the order
     * @param phoneNumber      the customer's phone number
     * @param orderDate        the date the order was placed
     * @param productsInOrder  a map of product IDs to their requested quantities
     */
    public void createOrder(int orderID, int phoneNumber, Date orderDate, Map<Integer, Integer> productsInOrder) {
        Map<Integer, Map.Entry<Integer, Double>> orderedProducts = new HashMap<>(); // Map to store productID -> (quantity, bestPrice)

        for (Integer productID : productsInOrder.keySet()) { // Loop through each product in the input map
            int amount = productsInOrder.get(productID); // Get the quantity requested for the product
            double bestOffer = productService.best_price(productID, amount); // Find the best price for the product using ProductService
            orderedProducts.put(productID, new AbstractMap.SimpleEntry<>(amount, bestOffer)); // Store the product info in the map
        }

        Order order = new Order(orderID, phoneNumber, orderDate, orderedProducts); // Create a new Order object using all collected data
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
}
