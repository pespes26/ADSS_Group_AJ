package Inventory.Tests;

import Inventory.Domain.Item;
import Inventory.Domain.Product;
import Inventory.Domain.ProductController;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Unit tests for the ProductController class.
 * These tests verify price updates, supply information management,
 * and category/sub-category checks.
 */
public class ProductControllerTest {

    private ProductController productController;
    private Product product;

    /**
     * Initializes a product and the ProductController before each test.
     */
    @Before
    public void setUp() {
        HashMap<Integer, Product> products = new HashMap<>();
        HashMap<Integer, Item> purchasedItems = new HashMap<>();

        product = new Product();
        product.setCatalogNumber(101);
        product.setCategory("Dairy");
        product.setSubCategory("Cheese");
        product.setProductDemandLevel(3);
        product.setSupplyTime(5);
        product.setCostPriceBeforeSupplierDiscount(100.0);
        product.setSupplierDiscount(10.0);
        product.setStoreDiscount(20.0);
        products.put(101, product);

        productController = new ProductController(products, purchasedItems);
    }

    /**
     * Verifies that updating the cost price for a product also updates its
     * derived prices based on supplier and store discounts.
     */
    @Test
    public void testUpdateCostPrice() {
        boolean result = productController.updateCostPriceByCatalogNumber(101, 200.0);

        assertTrue("Expected update to succeed", result);
        assertEquals("Cost price before discount should be updated", 200.0, product.getCostPriceBeforeSupplierDiscount(), 0.01);
        assertEquals("Cost price after supplier discount should be 200 * 0.9", 180.0, product.getCostPriceAfterSupplierDiscount(), 0.01);
        assertEquals("Sale price before store discount should be 180 * 2", 360.0, product.getSalePriceBeforeStoreDiscount(), 0.01);
        assertEquals("Sale price after store discount should be 360 * 0.8", 288.0, product.getSalePriceAfterStoreDiscount(), 0.01);
    }

    /**
     * Verifies that supply time and product demand level can be updated for a product.
     */
    @Test
    public void testUpdateProductSupplyDetails() {
        boolean result = productController.updateProductSupplyDetails(101, 7, 4);

        assertTrue("Expected supply update to succeed", result);
        assertEquals("Supply time should be updated to 7", 7, product.getSupplyTime());
        assertEquals("Demand level should be updated to 4", 4, product.getProductDemandLevel());
    }

    /**
     * Verifies whether a given catalog number is known or unknown in the system.
     */
    @Test
    public void testIsUnknownCatalogNumber() {
        assertTrue("Catalog number 999 should be unknown", productController.isUnknownCatalogNumber(999));
        assertFalse("Catalog number 101 should be known", productController.isUnknownCatalogNumber(101));
    }

    /**
     * Verifies that the controller correctly detects known categories and sub-categories.
     */
    @Test
    public void testHasCategoryAndSubCategory() {
        assertTrue("Category 'Dairy' should be recognized", productController.hasCategory("Dairy"));
        assertTrue("Sub-category 'Cheese' should be recognized", productController.hasSubCategory("Cheese"));
    }
}
