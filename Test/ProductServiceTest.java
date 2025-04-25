package Service;


import Domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class ProductServiceTest {
    private ProductService productService;

    @BeforeEach
    public void setUp() {
        productService = new ProductService();
    }

    @Test
    public void givenProductDoesNotExist_whenCreateProduct_thenProductIsAdded() {
        Product product = productService.createProduct(1, 2, 55.5, "K", 1432);
        assertNotNull(product);
        assertEquals(1432, product.getSupplierID());
        assertEquals(1, product.getCatalog_Number());
        assertEquals(2, product.getProduct_id());
        assertEquals(55.5, product.getPrice());
        assertTrue(productService.productExistsByCatalog(product.getCatalog_Number()));
        assertTrue(productService.productExistsProductWithID(product.getProduct_id()));
    }


    @Test
    public void givenProductExists_whenDeleteByCatalogAndSupplier_thenProductIsRemoved() {
        productService.createProduct(202, 2, 15.0, "box", 777);
        productService.delete_by_catalogAndSupplierId(202, 777);
        assertFalse(productService.productExistsProductWithID(2));
        assertFalse(productService.productExistsByCatalog(202));
    }

    @Test
    public void givenNoProducts_whenExistsZeroProducts_thenReturnsTrue() {
        assertTrue(productService.existsZeroProducts());
    }

    @Test
    public void givenProductWithOneDiscount_whenBestPrice_thenReturnsDiscountedPrice() {
        Product product = productService.createProduct(303, 3, 100.0, "unit", 999);
        product.updateOrAddDiscountRule(10, 10); // 10% discount from 10 units
        double price = productService.best_price(3, 15); // 15 units => discount applies
        assertEquals(1350.0, price);
//        assertEquals(90.0, price);
    }

    @Test
    public void givenProductWithDiscounts_whenBestPrice_thenReturnsDiscountedTotalPrice() {
        Product product = productService.createProduct(303, 3, 100.0, "unit", 999);

        product.updateOrAddDiscountRule(10, 10); // 10% discount from 10 units
        product.updateOrAddDiscountRule(15, 20); // 15% discount from 20 units

        double price = productService.best_price(3, 15); // Should trigger only the 10% discount
        assertEquals(1350.0, price); // 90 * 15

        product.updateOrAddDiscountRule(20, 10); // 15% discount from 20 units
        double price1 = productService.best_price(3, 15); // Should trigger only the 10% discount
        assertEquals(1200.0, price1); // 90 * 15
    }

    @Test
    public void givenProductWithHigherTierDiscount_whenBestPrice_thenAppliesBestDiscount() {
        Product product = productService.createProduct(303, 3, 100.0, "unit", 999);

        product.updateOrAddDiscountRule(10, 10); // 10% discount from 10 units
        product.updateOrAddDiscountRule(15, 20); // 15% discount from 20 units

        double price = productService.best_price(3, 25); // Should trigger 15% discount
        assertEquals(2125.0, price); // 85 * 25
    }

    @Test
    public void givenProductWithDiscounts_whenUpdatingOneDiscount_thenBestPriceReturnsUpdatedDiscountedTotalPrice() {
        Product product = productService.createProduct(303, 3, 100.0, "unit", 999);

        product.updateOrAddDiscountRule(10, 10); // 10% discount from 10 units
        product.updateOrAddDiscountRule(20, 15); // 15% discount from 20 units

        double price = productService.best_price(3, 15); // Should trigger 15% discount
        assertEquals(1200.0, price); // 90 * 15

        product.updateOrAddDiscountRule(10, 30); // Update: 30% discount from 10 units
        double price2 = productService.best_price(3, 12);
        assertEquals(1080.0, price2); // 70 * 12
    }


    @Test
    public void givenProductNotExists_whenBestPrice_thenReturnsMinusOne() {
        double price = productService.best_price(999, 5); // product id 999 does not exist
        assertEquals(-1, price);
    }

}
