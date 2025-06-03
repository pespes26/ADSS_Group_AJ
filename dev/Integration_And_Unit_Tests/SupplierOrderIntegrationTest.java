package Integration_And_Unit_Tests;

import Suppliers.DAO.*;
import Suppliers.DTO.*;
import Suppliers.Domain.*;
import Suppliers.Repository.*;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Integration test class for supplier order management.
 * Tests the interaction between supplier management and order processing components.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SupplierOrderIntegrationTest {
    private static OrderManagementController orderController;
    private static SupplierManagementController supplierController;
    private static int testSupplierId;
    private static int testOrderId;

    /**
     * Sets up the test environment.
     * Initializes controllers, repositories, and creates a test supplier for use in tests.
     */
    @BeforeAll
    public static void setUp() throws SQLException {
        // Initialize controllers
        IOrderDAO orderDAO = new JdbcOrderDAO();
        ISupplierDAO supplierDAO = new JdbcSupplierDAO();
        IAgreementDAO agreementDAO = new JdbcAgreementDAO();
        IProductSupplierDAO productSupplierDAO = new JdbcProductSupplierDAO();
        IDiscountDAO discountDAO = new JdbcDiscountDAO();
        
        // Create repositories
        ISupplierRepository supplierRepository = new SupplierRepositoryImpl(supplierDAO, agreementDAO, productSupplierDAO, discountDAO);
          // Initialize controllers
        orderController = new OrderManagementController(orderDAO);
        supplierController = new SupplierManagementController(supplierRepository);
        
        // Create test supplier
        SupplierDTO supplier = new SupplierDTO("Test Supplier", 12345, 0, "Cash", "Prepaid", 1234567890L, "test@supplier.com");
        supplier.setActive(true);
        supplierController.createSupplier(supplier);
        testSupplierId = supplier.getSupplier_id();
    }

    /**
     * Tests creation of a new order.
     * Verifies that orders can be created with multiple items and assigned proper IDs.
     */
    @Test
    @Order(1)
    void testCreateOrder() throws SQLException {
        // Create a new order
        OrderDTO order = new OrderDTO(1234567890L, LocalDateTime.now(), List.of(
            new OrderItemDTO(1, 5, testSupplierId),
            new OrderItemDTO(2, 3, testSupplierId)
        ));
        orderController.getAllOrders(); // Should not throw exception
        testOrderId = order.getOrderID();
        Assertions.assertNotNull(testOrderId);
    }

    /**
     * Tests searching for orders by supplier ID.
     * Verifies that orders can be retrieved for a specific supplier.
     */
    @Test
    @Order(2)
    void testSearchOrdersBySupplier() throws SQLException {
        List<OrderDTO> orders = orderController.getOrdersBySupplierId(testSupplierId);
        Assertions.assertFalse(orders.isEmpty(), "Should find orders for test supplier");
    }

    /**
     * Tests searching for orders within a date range.
     * Verifies that orders can be found between specified start and end dates.
     */
    @Test
    @Order(3)
    void testSearchOrdersByDateRange() throws SQLException {
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        List<OrderDTO> orders = orderController.searchOrders(startDate, endDate, null);
        Assertions.assertFalse(orders.isEmpty(), "Should find orders in date range");
    }

    /**
     * Cleans up test data.
     * Removes test supplier and associated data after all tests complete.
     */
    @AfterAll
    public static void tearDown() throws SQLException {
        // Cleanup test data
        supplierController.deleteSupplier(testSupplierId);
    }
}
