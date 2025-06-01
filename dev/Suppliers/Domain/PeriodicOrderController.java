package Suppliers.Domain;

import Suppliers.DTO.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PeriodicOrderController {
    private final IInventoryOrderRepository orderRepository;

    public PeriodicOrderController(IInventoryOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<OrderProductDetails> getPeriodicOrderProductDetails(List<InventoryProductPeriodic> requestedProducts, long phoneNumber) throws SQLException {
        List<OrderProductDetails> productDetails = new ArrayList<>();
        for (InventoryProductPeriodic requestedProduct : requestedProducts) {
            int supplierID = requestedProduct.getSupplierId();
            int agreementID = requestedProduct.getAgreementID();
            int productID = requestedProduct.getCatalogNumber();
            int quantity = requestedProduct.getQuantity();

            SupplierDTO supplierDTO = orderRepository.getSupplierByID(supplierID);
            AgreementDTO agreementDTO = orderRepository.getAgreementByID(agreementID);
            ProductSupplierDTO productSupplierDTO = orderRepository.getProductByProductIDSupplierID(productID, supplierID);

            if (supplierDTO == null || agreementDTO == null || productSupplierDTO == null) {
                System.out.printf(" Skipped productID=%d: missing supplier/agreement/product data%n", productID);
                continue;
            }

            //הנחה
            double discount = 0.0;
            DiscountDTO discountDTO = orderRepository.getBestMatchingDiscount(supplierID, agreementID, productID, quantity);
            if (discountDTO != null) {
                discount = discountDTO.getDiscountPercentage();
            }

            String supplierName = supplierDTO.getSupplierName();

            String[] deliveryDays = agreementDTO.getDeliveryDays();

            double price = productSupplierDTO.getPrice();

             productDetails.add( new OrderProductDetails(supplierID, supplierName, deliveryDays, agreementID, productID, price, discount, quantity)
            );
        }
        return productDetails;
    }

    public void saveOrder(List<OrderProductDetails>  productsDetails, long phoneNumber) throws SQLException {
        if (productsDetails == null || productsDetails.isEmpty()) {
            throw new IllegalArgumentException("Cannot save order: no products provided.");
        }

        List<OrderItemDTO> orderItems = new ArrayList<>();

        for (OrderProductDetails details : productsDetails) {
            int productID = details.getProductId();
            int supplierID = details.getSupplierId();
            int quantity = details.getQuantity();

            if (quantity > 0) {
                orderItems.add(new OrderItemDTO(productID, supplierID, quantity));
            }
        }

        OrderDTO order = new OrderDTO(phoneNumber, LocalDateTime.now(), orderItems);
        orderRepository.createOrder(order);
    }

}
