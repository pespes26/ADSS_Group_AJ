package Tests;

import Domain.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for the InventoryController class.
 * This class verifies initialization of controllers and basic method stability.
 */
public class InventoryControllerTest {

    private InventoryController inventory;

    /**
     * Initializes a new InventoryController before each test.
     */
    @Before
    public void setUp() {
        inventory = new InventoryController();
    }

    /**
     * Verifies that all internal controllers (Item, Product, Discount, Report) are properly initialized.
     */
    @Test
    public void testAllControllersAreInitialized() {
        assertNotNull("ItemController should not be null", inventory.getItemController());
        assertNotNull("ProductController should not be null", inventory.getProductController());
        assertNotNull("DiscountController should not be null", inventory.getDiscountController());
        assertNotNull("ReportController should not be null", inventory.getReportController());
    }

    /**
     * Verifies that calling importData on a non-existing file does not crash the program.
     * This is a smoke test to ensure graceful failure handling.
     */
    @Test
    public void testImportDataHandlesMissingFileGracefully() {
        try {
            inventory.importData("non_existing_file.csv");
        } catch (Exception e) {
            fail("importData should not throw exception on missing file: " + e.getMessage());
        }
    }
}
