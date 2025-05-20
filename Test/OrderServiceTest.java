package Service;

import Domain.Order;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class OrderServiceTest {

    private OrderService orderService;

    @Before
    public void setUp() {
        orderService = new OrderService();
    }

    @Test
    public void givenOrderDoesNotExist_whenCreateOrder_thenOrderIsAdded() {
        Map<Integer, Integer> productsInOrder = new HashMap<>();
        productsInOrder.put(123, 100);
        productsInOrder.put(242, 200);
        productsInOrder.put(33, 300);

        long phoneNumber = 503337121;
        orderService.createOrder(1, phoneNumber, LocalDateTime.now(), productsInOrder);

        Order order = orderService.searchOrderById(1);
        assertNotNull(order);
        assertEquals(1, order.getOrderID());
        assertEquals(phoneNumber, order.getPhoneNumber());
        assertEquals(productsInOrder, order.getProductsInOrder());
    }



    @Test
    public void givenExistingOrder_whenSearchById_thenReturnOrder() {
        Map<Integer, Integer> products = new HashMap<>();
        products.put(202, 5);
        orderService.createOrder(2, 987654321, LocalDateTime.now(), products);

        Order order = orderService.searchOrderById(2);
        assertNotNull(order);
        assertEquals(987654321, order.getPhoneNumber());
    }

    @Test
    public void givenOrder_whenGetProductsInOrder_thenReturnCorrectProducts() {
        Map<Integer, Integer> products = new HashMap<>();
        products.put(303, 7);
        products.put(404, 3);
        orderService.createOrder(3, 111222333, LocalDateTime.now(), products);

        Map<Integer, Integer> retrievedProducts = orderService.getProductsInOrder(3);
        assertNotNull(retrievedProducts);
        assertEquals(2, retrievedProducts.size());
        assertEquals((Integer) 7, retrievedProducts.get(303));
    }

    @Test
    public void givenUnknownOrderId_whenSearchOrderById_thenReturnsNull() {
        Order order = orderService.searchOrderById(999);
        assertNull(order);
    }

    @Test
    public void givenNoOrderWithId_whenThereIsOrder_thenReturnsFalse() {
        assertFalse(orderService.thereIsOrder(1000));
    }
}
