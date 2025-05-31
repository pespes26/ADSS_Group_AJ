package Suppliers.Domain;

import Suppliers.DTO.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderByShortageController {
    private final IOrderByShortageRepository orderRepository;

    public OrderByShortageController(IOrderByShortageRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public ShortageOrderProductDetails getShortageOrderProductDetails(HashMap<Integer, Integer> products, long phoneNumber) throws SQLException {
        ShortageOrderProductDetails shortageOrderProductDetails = orderRepository.getShortageOrderProductDetails(products);
        saveOrder(shortageOrderProductDetails,products,phoneNumber);
        return shortageOrderProductDetails;
    }

    public void saveOrder(ShortageOrderProductDetails productsDetails, HashMap<Integer, Integer> requestedProducts, long phoneNumber) throws SQLException {
        List<OrderItemDTO> orderItems = new ArrayList<>();

        for (DiscountDTO discount : productsDetails.getDiscounts()) {
            int productId = discount.getProductID();
            int supplierId = discount.getSupplierID();
            int quantity = requestedProducts.getOrDefault(productId, 0);
            if (quantity > 0)
                orderItems.add(new OrderItemDTO(productId, supplierId, quantity));
        }

        for (ProductSupplierDTO product : productsDetails.getCheapestProductsWithoutDiscount()) {
            int productId = product.getProduct_id();
            int supplierId = product.getSupplierID();
            int quantity = requestedProducts.getOrDefault(productId, 0);
            if (quantity > 0)
                orderItems.add(new OrderItemDTO(productId, supplierId, quantity));
        }

        OrderDTO order = new OrderDTO(phoneNumber, LocalDateTime.now(), orderItems);
        orderRepository.createOrder(order);
    }
}

