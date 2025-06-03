package Integration_And_Unit_Tests;

import Inventory.DTO.*;
import Inventory.Repository.*;
import InventorySupplier.SystemService.PeriodicOrderService;
import Suppliers.Init.SupplierRepositoryInitializer;
import Suppliers.Repository.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * System test class for periodic order functionality.
 * Tests the complete flow of creating, processing, and managing periodic orders
 * across multiple system components.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PeriodicOrderSystemTest {
    private static PeriodicOrderService periodicOrderService;
    private static IPeriodicOrderRepository periodicOrderRepo;
    private static IOrderOnTheWayRepository onTheWayRepo;
    private static IItemRepository itemRepo;
    private static final int testBranchId = 1;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Sets up the test environment.
     * Initializes services, repositories, and required components for periodic order testing.
     */
    @BeforeAll
    public static void setUp() {
        // Initialize repositories
        SupplierRepositoryInitializer supplierInit = new SupplierRepositoryInitializer();
        IInventoryOrderRepository supplierRepo = supplierInit.getSupplierOrderRepository();
        periodicOrderRepo = new PeriodicOrderRepositoryImpl();
        onTheWayRepo = new OrderOnTheWayRepositoryImpl();
        itemRepo = new ItemRepositoryImpl();

        periodicOrderService = new PeriodicOrderService(
            supplierRepo, periodicOrderRepo, onTheWayRepo, itemRepo);
    }

    /**
     * Tests the creation of a periodic order.
     * Verifies that orders can be created with proper supplier and product details.
     */
    @Test
    @Order(1)
    void testCreatePeriodicOrder() throws SQLException {
        // Test creating a basic periodic order
        PeriodicOrderDTO order = new PeriodicOrderDTO(
            0, 1001, 5, "2025-12-31", 0.8,
            1, "Test Supplier", "MONDAY,WEDNESDAY",
            1, testBranchId, "MONDAY,WEDNESDAY", null, null, 0
        );
        periodicOrderRepo.insertPeriodicOrder(order);
        
        List<PeriodicOrderDTO> orders = periodicOrderRepo.getAllPeriodicOrders();
        assertFalse(orders.isEmpty(), "Order list should not be empty after insertion");
        assertTrue(orders.stream().anyMatch(o -> o.getProductCatalogNumber() == 1001), 
            "Should find the inserted product");
    }

    /**
     * Tests the periodic order processing flow.
     * Verifies that orders are correctly processed and moved to in-transit state.
     */
    @Test
    @Order(2)
    void testProcessPeriodicOrders() throws SQLException {
        boolean success = periodicOrderService.start(testBranchId);
        assertTrue(success, "Order processing should succeed");
        
        List<OrderOnTheWayDTO> inTransit = onTheWayRepo.getOrdersInTransit();
        assertFalse(inTransit.isEmpty(), "Should have orders in transit after processing");
        
        // Verify order details
        if (!inTransit.isEmpty()) {
            OrderOnTheWayDTO firstOrder = inTransit.get(0);
            assertEquals(testBranchId, firstOrder.getBranchId(), 
                "Order should be for the test branch");
        }
    }

    /**
     * Tests that items from periodic orders are added to inventory.
     * Verifies correct warehouse placement and quantity updates.
     */
    @Test
    @Order(3)
    void testItemsAddedToInventory() throws SQLException {
        // Test inventory update after order processing
        List<ItemDTO> items = itemRepo.getAllItems();
        assertFalse(items.isEmpty(), "Should have items after processing orders");
        
        // Verify warehouse items
        for (ItemDTO item : items) {
            if (item.getBranchId() == testBranchId) {
                assertEquals("Warehouse", item.getStorageLocation(),
                    "New items should be in warehouse location");
            }
        }
    }

    /**
     * Tests handling of orders from multiple suppliers.
     * Verifies that the system can process orders from different suppliers correctly.
     */
    @Test
    @Order(4)
    void testMultipleSupplierOrders() throws SQLException {
        // Test ordering from multiple suppliers
        PeriodicOrderDTO order1 = new PeriodicOrderDTO(
            0, 2001, 3, "2025-12-31", 0.7,
            2, "Supplier 2", "TUESDAY,THURSDAY",
            2, testBranchId, "TUESDAY,THURSDAY", null, null, 0
        );
        
        PeriodicOrderDTO order2 = new PeriodicOrderDTO(
            0, 3001, 4, "2025-12-31", 0.6,
            3, "Supplier 3", "WEDNESDAY,FRIDAY",
            3, testBranchId, "WEDNESDAY,FRIDAY", null, null, 0
        );
        
        periodicOrderRepo.insertPeriodicOrder(order1);
        periodicOrderRepo.insertPeriodicOrder(order2);
        
        List<PeriodicOrderDTO> orders = periodicOrderRepo.getAllPeriodicOrders();
        long distinctSuppliers = orders.stream()
            .filter(o -> o.getBranchId() == testBranchId)
            .map(PeriodicOrderDTO::getSupplierId)
            .distinct()
            .count();
        
        assertTrue(distinctSuppliers >= 2, 
            "Should have orders from at least 2 different suppliers");
    }

    /**
     * Tests ordering triggered by shortage conditions.
     * Verifies that orders are created when inventory levels are low.
     */
    @Test
    @Order(5)
    void testShortageTriggeredOrder() throws SQLException {
        // Test that orders are created when inventory is low
        int catalogNumber = 1001;
        
        // Create a low-stock item
        ItemDTO item = new ItemDTO(
            catalogNumber, 
            testBranchId,
            "Warehouse",
            null, // section_in_store
            false, // is_defect
            null // expiring_date
        );
        
        itemRepo.addItem(item);
        
        // Process orders
        boolean orderProcessed = periodicOrderService.start(testBranchId);
        assertTrue(orderProcessed, "Should create order for low inventory");
        
        // Verify order was created
        List<OrderOnTheWayDTO> pendingOrders = onTheWayRepo.getOrdersInTransit();
        assertTrue(
            pendingOrders.stream()
                .anyMatch(o -> o.getProductCatalogNumber() == catalogNumber),
            "Should find pending order for low inventory product"
        );
    }

    /**
     * Tests recording of order delivery process.
     * Verifies that deliveries are correctly tracked and processed.
     */
    @Test
    @Order(6)
    void testOrderDeliveryRecording() throws SQLException {
        // Test order delivery process
        int catalogNumber = 4001;
        int quantity = 5;
        String deliveryDate = LocalDate.now().plusDays(1).format(DATE_FORMATTER);
        String orderDate = LocalDate.now().format(DATE_FORMATTER);
        
        // Create a test delivery
        OrderOnTheWayDTO delivery = new OrderOnTheWayDTO(
            0, // orderId (will be auto-generated)
            catalogNumber,
            quantity,
            1, // supplierId
            testBranchId,
            deliveryDate,
            orderDate,
            1, // agreementId
            true // isPeriodic
        );
        onTheWayRepo.insert(delivery);
        
        // Process the delivery
        List<OrderOnTheWayDTO> deliveries = onTheWayRepo.getOrdersInTransit();
        assertFalse(deliveries.isEmpty(), "Should have pending deliveries");
        
        // Check if the delivery can be found
        assertTrue(
            deliveries.stream()
                .anyMatch(d -> d.getProductCatalogNumber() == catalogNumber 
                    && d.getQuantity() == quantity),
            "Should find the test delivery"
        );
    }

    /**
     * Cleans up test data after all tests complete.
     * Removes test orders and related data from the system.
     */
    @AfterAll
    public static void tearDown() throws SQLException {
        // Clean up test data
        List<PeriodicOrderDTO> orders = periodicOrderRepo.getAllPeriodicOrders();
        for (PeriodicOrderDTO order : orders) {
            if (order.getBranchId() == testBranchId) {
                periodicOrderRepo.deletePeriodicOrderById(order.getOrderId());
            }
        }
        
        // Clean up orders in transit
        List<OrderOnTheWayDTO> inTransitOrders = onTheWayRepo.getOrdersInTransit();
        for (OrderOnTheWayDTO order : inTransitOrders) {
            if (order.getBranchId() == testBranchId) {
                onTheWayRepo.deleteById(order.getOrderId());
            }
        }
    }
}
