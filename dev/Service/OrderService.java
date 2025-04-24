package Service;
import Domain.Order;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class OrderService {
    private final HashMap<Integer, Order> orderHashMap; // Stores all orders using orderID as the key


    public OrderService() {
        orderHashMap = new HashMap<>(); // Initializes the order map
    }


    public void createOrder(int orderID, long phoneNumber, LocalDateTime orderDate, Map<Integer, Integer> productsInOrder) {
        Order order = new Order(orderID, phoneNumber, orderDate, productsInOrder); // Create a new Order object using all collected data
        this.orderHashMap.put(orderID, order); // Add the order to the internal order list, using its ID as the key
    }


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

    //    public boolean existsZeroOrders() {
//        return orderHashMap.isEmpty();
//    }

}
