package Tests;

import Domain.*;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Unit tests for the DiscountController class.
 */
public class DiscountControllerTest {

    private DiscountController discountController;
    private Product product;

    @Before
    public void setUp() {
        HashMap<Integer, Product> products = new HashMap<>();
        product = new Product();
        product.setCatalogNumber(101);
        product.setCategory("Snacks");
        product.setSubCategory("Chips");
        product.setCostPriceBeforeSupplierDiscount(50.0);
        products.put(101, product);

        discountController = new DiscountController(products);
    }

    @Test
    public void testApplyStoreDiscountToCatalog() {
        Discount discount = new Discount(20.0, LocalDate.now().minusDays(1), LocalDate.now().plusDays(5));
        boolean result = discountController.setStoreDiscountForCatalogNumber(101, discount);
        assertTrue(result);
        assertEquals(20.0, product.getStoreDiscount(), 0.01);
    }

    @Test
    public void testApplySupplierDiscountToCategory() {
        Discount discount = new Discount(15.0, LocalDate.now(), LocalDate.now().plusDays(3));
        boolean result = discountController.setSupplierDiscountForCategory("Snacks", discount);
        assertTrue(result);
        assertEquals(15.0, product.getSupplierDiscount(), 0.01);
    }

    @Test
    public void testInvalidDiscountDates() {
        Discount discount = new Discount(15.0, LocalDate.now().plusDays(3), LocalDate.now());
        boolean result = discountController.setStoreDiscountForCatalogNumber(101, discount);
        assertFalse(result);
    }
}
