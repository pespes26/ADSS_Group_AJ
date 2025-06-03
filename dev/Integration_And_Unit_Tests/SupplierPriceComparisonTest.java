package Integration_And_Unit_Tests;

import Suppliers.DAO.*;
import Suppliers.DTO.*;
import Suppliers.Domain.*;
import Suppliers.Repository.*;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 * Integration test class for verifying supplier price comparison functionality.
 * Tests scenarios where multiple suppliers offer the same product at different prices
 * with various discount structures.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SupplierPriceComparisonTest {
    private static OrderByShortageController shortageController;
    private static ISupplierDAO supplierDAO;
    private static IAgreementDAO agreementDAO;
    private static IProductSupplierDAO productSupplierDAO;
    private static IDiscountDAO discountDAO;
    private static IOrderDAO orderDAO;

    @BeforeAll
    public static void setUp() throws SQLException {
        // Initialize DAOs
        supplierDAO = new JdbcSupplierDAO();
        agreementDAO = new JdbcAgreementDAO();
        productSupplierDAO = new JdbcProductSupplierDAO();
        discountDAO = new JdbcDiscountDAO();
        orderDAO = new JdbcOrderDAO();

        // Clear any existing data
        clearDatabase();

        // Initialize repository and controller
        IInventoryOrderRepository orderRepository = new InventoryOrderRepositoryImpl(
            productSupplierDAO, discountDAO, orderDAO, supplierDAO, agreementDAO);
        shortageController = new OrderByShortageController(orderRepository);
    }

    private static void clearDatabase() throws SQLException {
        orderDAO.clearTable();
        discountDAO.clearTable();
        productSupplierDAO.clearTable();
        agreementDAO.clearTable();
        supplierDAO.clearTable();
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    @DisplayName("Compare prices among multiple suppliers for the same product")
    void testMultipleSupplierPriceComparison() throws SQLException {
        // Create test suppliers with different pricing strategies
        int supplier1Id = supplierDAO.insertAndGetID(
            new SupplierDTO("HighPrice Corp", 1001, 1, "Cash", "Prepaid", 1111111, "high@test.com"));
        int supplier2Id = supplierDAO.insertAndGetID(
            new SupplierDTO("MidPrice Ltd", 1002, 2, "Bank", "Net30", 2222222, "mid@test.com"));
        int supplier3Id = supplierDAO.insertAndGetID(
            new SupplierDTO("LowPrice Inc", 1003, 3, "Credit", "Net60", 3333333, "low@test.com"));
        
        // Create supplier agreements with different delivery schedules
        int agreement1Id = agreementDAO.insertAndGetID(
            new AgreementDTO(supplier1Id, new String[]{"Mon", "Wed"}, false));
        int agreement2Id = agreementDAO.insertAndGetID(
            new AgreementDTO(supplier2Id, new String[]{"Tue", "Thu"}, false));
        int agreement3Id = agreementDAO.insertAndGetID(
            new AgreementDTO(supplier3Id, new String[]{"Wed", "Fri"}, false));

        // Same product (ID: 1204) with different base prices
        productSupplierDAO.insert(new ProductSupplierDTO(1204, 1, supplier1Id, agreement1Id, 100.0, "Unit")); // Most expensive
        productSupplierDAO.insert(new ProductSupplierDTO(1204, 2, supplier2Id, agreement2Id, 80.0, "Unit")); // Mid-range
        productSupplierDAO.insert(new ProductSupplierDTO(1204, 3, supplier3Id, agreement3Id, 70.0, "Unit")); // Cheapest

        // Different discount structures
        discountDAO.insert(new DiscountDTO(1204, supplier1Id, agreement1Id, 10, 20.0)); // 20% off for 10+ units
        discountDAO.insert(new DiscountDTO(1204, supplier2Id, agreement2Id, 5, 10.0));  // 10% off for 5+ units
        discountDAO.insert(new DiscountDTO(1204, supplier3Id, agreement3Id, 20, 5.0));  // 5% off for 20+ units

        // Test Case 1: Small order (below all discount thresholds)
        HashMap<Integer, Integer> smallOrder = new HashMap<>();
        smallOrder.put(1204, 3);
        List<OrderProductDetailsDTO> smallOrderResult = shortageController.getShortageOrderProductDetails(smallOrder, 123456789);
        
        Assertions.assertEquals(1, smallOrderResult.size(), "Should get exactly one supplier option");
        Assertions.assertEquals(supplier3Id, smallOrderResult.get(0).getSupplierId(), "Should choose the cheapest base price for small orders");
        Assertions.assertEquals(70.0, smallOrderResult.get(0).getPrice(), "Should get the base price with no discount");

        // Test Case 2: Medium order (triggers mid-tier supplier's discount)
        HashMap<Integer, Integer> mediumOrder = new HashMap<>();
        mediumOrder.put(1204, 8);
        List<OrderProductDetailsDTO> mediumOrderResult = shortageController.getShortageOrderProductDetails(mediumOrder, 123456789);
        
        Assertions.assertEquals(1, mediumOrderResult.size(), "Should get exactly one supplier option");
        Assertions.assertEquals(supplier2Id, mediumOrderResult.get(0).getSupplierId(), "Should choose supplier with best effective price after discount");
        Assertions.assertEquals(80.0, mediumOrderResult.get(0).getPrice(), "Should get the correct base price");
        Assertions.assertEquals(10.0, mediumOrderResult.get(0).getDiscount(), "Should get the correct discount percentage");

        // Test Case 3: Large order (triggers all discounts)
        HashMap<Integer, Integer> largeOrder = new HashMap<>();
        largeOrder.put(1204, 25);
        List<OrderProductDetailsDTO> largeOrderResult = shortageController.getShortageOrderProductDetails(largeOrder, 123456789);
        
        Assertions.assertEquals(1, largeOrderResult.size(), "Should get exactly one supplier option");
        double effectivePrice1 = 100.0 * (1 - 0.20); // Supplier 1 with 20% discount
        double effectivePrice2 = 80.0 * (1 - 0.10);  // Supplier 2 with 10% discount
        double effectivePrice3 = 70.0 * (1 - 0.05);  // Supplier 3 with 5% discount
        
        // The supplier with the lowest effective price should be chosen
        double lowestEffectivePrice = Math.min(Math.min(effectivePrice1, effectivePrice2), effectivePrice3);
        Assertions.assertEquals(lowestEffectivePrice, 
            largeOrderResult.get(0).getPrice() * (1 - largeOrderResult.get(0).getDiscount()/100.0),
            "Should choose the supplier with lowest effective price after all discounts");
    }

    @AfterAll
    public static void tearDown() throws SQLException {
        clearDatabase();
    }
}
