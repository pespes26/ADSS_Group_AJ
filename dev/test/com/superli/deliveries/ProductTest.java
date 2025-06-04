package test.com.superli.deliveries;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.superli.deliveries.domain.core.Product;

/**
 * Unit tests for the Product class.
 */
class ProductTest {

    @Test
    void constructor_ValidData_CreatesProduct() {
        // Arrange
        String id = "PROD-123";
        String name = "Milk 3%";
        float weight = 1.5f;

        // Act
        Product product = new Product(id, name, weight);

        // Assert
        assertNotNull(product, "Product object should not be null");
        assertEquals(id, product.getProductId());
        assertEquals(name, product.getName());
        assertEquals(weight, product.getWeight());
    }

    @Test
    void constructor_NullProductId_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Product(null, "Test Product", 1.0f);
        }, "Constructor should throw for null productId");
    }

    @Test
    void constructor_BlankProductId_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Product("  ", "Test Product", 1.0f);
        }, "Constructor should throw for blank productId");
    }

    @Test
    void constructor_NegativeWeight_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Product("PROD-123", "Test Product", -1.0f);
        }, "Constructor should throw for negative weight");
    }

    @Test
    void constructor_NullName_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Product("PROD-ID", null, 1.0f);
        }, "Constructor should throw for null name");
    }

    @Test
    void equals_SameId_ShouldBeEqual() {
        // Arrange
        String id = "PROD-EQ";
        Product product1 = new Product(id, "Product Name One", 1.0f);
        Product product2 = new Product(id, "Product Name Two", 2.0f); // Same ID, different name and weight

        // Act & Assert
        assertEquals(product1, product2, "Products with the same ID should be equal");
        assertEquals(product1.hashCode(), product2.hashCode(), "HashCode should be equal for equal objects");
    }

    @Test
    void equals_DifferentId_ShouldNotBeEqual() {
        // Arrange
        Product product1 = new Product("PROD-1", "Name", 1.0f);
        Product product2 = new Product("PROD-2", "Name", 1.0f); // Different ID

        // Act & Assert
        assertNotEquals(product1, product2, "Products with different IDs should not be equal");
    }
}