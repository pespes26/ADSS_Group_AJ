package Tests;

import Domain.*;
import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.HashMap;

/**
 * Unit tests for core domain logic including Product, Discount, Item, and controllers.
 * These tests verify basic functionality, edge cases, and logic enforcement.
 */
public class DomainUnitTests {

    /** Verifies price calculations based on cost and discounts. */
    @Test
    public void testSetCostPriceUpdatesSalePricesCorrectly() {
        Product product = new Product();
        product.setSupplierDiscount(20);
        product.setStoreDiscount(25);
        product.setCostPriceBeforeSupplierDiscount(100);

        assertEquals(80.0, product.getCostPriceAfterSupplierDiscount(), 0.01);
        assertEquals(160.0, product.getSalePriceBeforeStoreDiscount(), 0.01);
        assertEquals(120.0, product.getSalePriceAfterStoreDiscount(), 0.01);
    }

    /** Verifies that discount rate over 100 is rejected or adjusted. */
    @Test
    public void testInvalidDiscountRateTooHigh() {
        Discount discount = new Discount(150, LocalDate.now(), LocalDate.now().plusDays(10));
        assertTrue(discount.getDiscountRate() <= 100);
    }

    /** Checks that active discounts are valid for current date. */
    @Test
    public void testDiscountIsActive() {
        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = LocalDate.now().plusDays(5);
        Discount discount = new Discount(10, start, end);
        assertTrue(discount.isActive());
    }

    /** Ensures item attributes are stored correctly. */
    @Test
    public void testItemInitialization() {
        Item item = new Item();
        item.setItemId(1);
        item.setItemExpiringDate("01/01/2026");
        item.setStorageLocation("Warehouse");
        item.setItemSize(2);

        assertEquals(1, item.getItemId());
        assertEquals("Warehouse", item.getStorageLocation());
        assertEquals(2, item.getItemSize());
    }

    /** Confirms that defect status can be updated. */
    @Test
    public void testMarkItemAsDefective() {
        Item item = new Item();
        item.setDefect(false);
        item.setDefect(true);

        assertTrue(item.isDefect());
    }



    @Test
    public void testGetItemNameForNonexistentItem() {
        HashMap<Integer, Branch> branches = new HashMap<>();
        HashMap<Integer, Product> products = new HashMap<>();
        HashMap<Integer, Item> purchased_items = new HashMap<>();
        ItemController controller = new ItemController(branches, products, purchased_items);
        assertEquals("", controller.getItemName(1, 999));
    }

    /** Verifies location update on an existing item. */
    @Test
    public void testUpdateItemLocation() {
        Item item = new Item();
        item.setItemId(1);

        Branch branch = new Branch(2);
        branch.getItems().put(1, item);

        HashMap<Integer, Branch> branches = new HashMap<>();
        branches.put(2, branch);

        HashMap<Integer, Product> products = new HashMap<>();
        HashMap<Integer, Item> purchased = new HashMap<>();

        ItemController controller = new ItemController(branches, products, purchased);

        boolean updated = controller.updateItemLocation(1, 2, "Warehouse", "B2");


        assertTrue(updated);
    }

    /** Applies store discount to a full category. */
    @Test
    public void testApplyStoreDiscountToCategory() {
        HashMap<Integer, Product> products = new HashMap<>();
        Product p = new Product();
        p.setCatalogNumber(1);
        p.setCategory("Dairy");
        products.put(1, p);

        DiscountController controller = new DiscountController(products);
        Discount discount = new Discount(10, LocalDate.now(), LocalDate.now().plusDays(7));
        assertTrue(controller.setStoreDiscountForCategory("Dairy", discount));
    }

    /** Verifies invalid discount dates are rejected. */
    @Test
    public void testInvalidDiscountDates() {
        Discount discount = new Discount(10, LocalDate.now().plusDays(10), LocalDate.now());
        HashMap<Integer, Product> products = new HashMap<>();
        DiscountController controller = new DiscountController(products);
        assertFalse(controller.setStoreDiscountForCategory("Dairy", discount));
    }

    /** Ensures discount rate can't be negative. */
    @Test
    public void testNegativeDiscountRateFails() {
        Discount discount = new Discount(-5, LocalDate.now(), LocalDate.now().plusDays(5));
        assertTrue(discount.getDiscountRate() >= 0);
    }

    /** Fails to update location if item doesn't exist. */
    @Test
    public void testUpdateLocationFailsForMissingItem() {
        ItemController controller = new ItemController(new HashMap<>(), new HashMap<>(), new HashMap<>());
        assertFalse(controller.updateItemLocation(1234, 1, "Warehouse", "A1"));
        //                               itemId  branchId  location   section
    }

    /** Ensures discounts with null dates are not accepted. */
    @Test
    public void testDiscountMissingDatesFails() {
        Discount discount = new Discount(15, null, LocalDate.now().plusDays(2));
        DiscountController controller = new DiscountController(new HashMap<>());
        assertFalse(controller.setStoreDiscountForCategory("Dairy", discount));
    }

    /** When item is added, existing product should not be overwritten. */
    @Test
    public void testItemAddedButProductNotOverwritten() {
        HashMap<Integer, Branch> branches = new HashMap<>();
        HashMap<Integer, Product> products = new HashMap<>();
        HashMap<Integer, Item> purchased = new HashMap<>();

        Branch branch = new Branch(1);
        branches.put(0, branch);

        Product p = new Product();
        p.setCatalogNumber(10);
        p.setProductName("Old");
        products.put(10, p);

        String csv = "1,0,New,01/01/2026,Warehouse,A1,10,Dairy,Milk,2,100.0,3,2,Brand,0,0";
        ItemController controller = new ItemController(branches, products, purchased);
        controller.addItem(csv);

        assertEquals("Old", products.get(10).getProductName());
    }

    /** Verifies that expired items are reported correctly. */
    @Test
    public void testExpiredItemDate() {
        Item item = new Item();
        item.setItemId(1);
        item.setCatalog_number(5);
        item.setItemExpiringDate("01/01/2000");
        item.setStorageLocation("Warehouse");
        item.setItemSize(2);
        item.setSectionInStore("E1");

        Product product = new Product();
        product.setCatalogNumber(5);
        product.setProductName("Milk");

        Branch branch = new Branch(1);
        branch.getItems().put(1, item);

        HashMap<Integer, Branch> branches = new HashMap<>();
        branches.put(1, branch);

        HashMap<Integer, Product> products = new HashMap<>();
        products.put(5, product);

        ReportController reportController = new ReportController(branches, products);
        String report = reportController.defectAndExpiredReport(1);

        assertTrue(report.contains("Expired Items"));
        assertTrue(report.contains("01/01/2000"));
    }


}
