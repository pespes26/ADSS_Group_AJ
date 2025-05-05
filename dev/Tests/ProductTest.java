package Tests;

import Domain.Product;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for the Product class.
 * These tests verify property setters/getters and price calculations.
 */
public class ProductTest {

    /**
     * Verifies setting and retrieving basic product fields such as name, category, and size.
     */
    @Test
    public void testSetAndGetBasicAttributes() {
        Product p = new Product();
        p.setCatalogNumber(1001);
        p.setProductName("Milk");
        p.setCategory("Dairy");
        p.setSubCategory("Low Fat");
        p.setSize(2);
        p.setManufacturer("Tnuva");

        assertEquals("Catalog number should be 1001", 1001, p.getCatalogNumber());
        assertEquals("Product name should be 'Milk'", "Milk", p.getProductName());
        assertEquals("Category should be 'Dairy'", "Dairy", p.getCategory());
        assertEquals("Sub-category should be 'Low Fat'", "Low Fat", p.getSubCategory());
        assertEquals("Size should be 2", 2, p.getSize());
        assertEquals("Manufacturer should be 'Tnuva'", "Tnuva", p.getManufacturer());
    }

    /**
     * Verifies that supply and demand-related fields can be updated and retrieved correctly.
     */
    @Test
    public void testDemandAndSupplyFields() {
        Product p = new Product();
        p.setProductDemandLevel(4);
        p.setSupplyTime(3);
        p.setMinimumQuantityForAlert(5);

        assertEquals("Demand level should be 4", 4, p.getProductDemandLevel());
        assertEquals("Supply time should be 3", 3, p.getSupplyTime());
        assertEquals("Minimum quantity for alert should be 5", 5, p.getMinimumQuantityForAlert());
    }

    /**
     * Verifies quantity tracking in store and warehouse, and total quantity calculation.
     */
    @Test
    public void testQuantitiesAndTotal() {
        Product p = new Product();
        p.setQuantityInStore(30);
        p.setQuantityInWarehouse(70);

        assertEquals("Quantity in store should be 30", 30, p.getQuantityInStore());
        assertEquals("Quantity in warehouse should be 70", 70, p.getQuantityInWarehouse());
        assertEquals("Total quantity should be 100", 100, p.getTotalQuantity());
    }

    /**
     * Verifies price calculations when setting cost and applying supplier/store discounts.
     */
    @Test
    public void testRecalculatePricesWithDiscounts() {
        Product p = new Product();
        p.setCostPriceBeforeSupplierDiscount(100.0); // base cost
        p.setSupplierDiscount(10.0);                 // 10% discount
        assertEquals("Cost price after 10% supplier discount", 90.0, p.getCostPriceAfterSupplierDiscount(), 0.01);

        assertEquals("Sale price before store discount (90 * 2)", 180.0, p.getSalePriceBeforeStoreDiscount(), 0.01);

        p.setStoreDiscount(20.0); // 20% discount
        assertEquals("Sale price after 20% store discount", 144.0, p.getSalePriceAfterStoreDiscount(), 0.01);
    }

    /**
     * Verifies that prices can be manually overridden instead of calculated.
     */
    @Test
    public void testManualPriceOverride() {
        Product p = new Product();
        p.setCostPriceAfterSupplierDiscount(50.0);
        p.setSalePriceBeforeStoreDiscount(80.0);
        p.setSalePriceAfterStoreDiscount(70.0);

        assertEquals("Manually set cost price", 50.0, p.getCostPriceAfterSupplierDiscount(), 0.01);
        assertEquals("Manually set sale price before discount", 80.0, p.getSalePriceBeforeStoreDiscount(), 0.01);
        assertEquals("Manually set sale price after discount", 70.0, p.getSalePriceAfterStoreDiscount(), 0.01);
    }
}
