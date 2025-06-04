package Integration_And_Unit_Tests;

import Inventory.DAO.JdbcShortageOrderDAO;
import Inventory.DTO.ShortageOrderDTO;
import Inventory.Repository.IShortageOrderRepository;
import Inventory.Repository.ShortageOrderRepositoryImpl;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Test class to verify duplicate order prevention functionality
 */
public class DuplicateOrderPreventionTest {
    
    public static void main(String[] args) {
        System.out.println("🧪 Testing Duplicate Order Prevention...");
        
        try {
            testDuplicateOrderPrevention();
            System.out.println("✅ All tests passed!");
        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testDuplicateOrderPrevention() throws SQLException {
        IShortageOrderRepository repository = new ShortageOrderRepositoryImpl();
        
        // Test data
        int testProductId = 12345;
        int testBranchId = 1;
        String orderDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        System.out.println("1. Testing initial state - should have no pending orders");
        boolean hasPendingInitial = repository.hasPendingOrderForProduct(testProductId, testBranchId);
        System.out.println("   Has pending order initially: " + hasPendingInitial);
        assert !hasPendingInitial : "Should not have pending order initially";
        
        System.out.println("2. Creating first order");
        ShortageOrderDTO firstOrder = new ShortageOrderDTO(
            0, // Auto-incremented
            testProductId,
            10, // quantity
            15.50, // cost price
            0.10, // discount
            orderDate,
            testBranchId,
            "Monday, Tuesday", // days
            101, // supplier id
            "Test Supplier",
            10, // quantity needed
            0, // current stock
            "PENDING"
        );
        
        repository.insert(firstOrder);
        System.out.println("   First order created successfully");
        
        System.out.println("3. Checking if pending order exists now");
        boolean hasPendingAfterInsert = repository.hasPendingOrderForProduct(testProductId, testBranchId);
        System.out.println("   Has pending order after insert: " + hasPendingAfterInsert);
        assert hasPendingAfterInsert : "Should have pending order after insert";
        
        System.out.println("4. Testing with different product - should not find pending order");
        boolean hasPendingDifferentProduct = repository.hasPendingOrderForProduct(99999, testBranchId);
        System.out.println("   Has pending order for different product: " + hasPendingDifferentProduct);
        assert !hasPendingDifferentProduct : "Should not have pending order for different product";
        
        System.out.println("5. Testing with different branch - should not find pending order");
        boolean hasPendingDifferentBranch = repository.hasPendingOrderForProduct(testProductId, 999);
        System.out.println("   Has pending order for different branch: " + hasPendingDifferentBranch);
        assert !hasPendingDifferentBranch : "Should not have pending order for different branch";
        
        // Clean up: remove the test order
        System.out.println("6. Cleaning up test data...");
        // Note: In a real test, we'd clean up the test data
        // For now, we'll just mark it as delivered to avoid affecting other tests
        repository.markProcessedForToday(testBranchId);
        System.out.println("   Test data cleaned up");
    }
}
