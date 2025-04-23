package Service;

import Domain.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SupplierServiceTest {

    private SupplierService supplierService;

    @BeforeEach
    public void setUp() {
        supplierService = new SupplierService();
    }

    @Test
    public void testCreateSupplierAddsSupplier() {
        supplierService.createSupplier("Test Supplier", 1, 123, 456, "Cash", 5551234, "test@mail.com", "Prepaid");
        assertTrue(supplierService.thereIsSupplier(1));
    }

    @Test
    public void testGetSupplierByIdReturnsCorrectSupplier() {
        supplierService.createSupplier("Test Supplier", 2, 123, 456, "Check", 5551235, "email@mail.com", "Postpaid");
        Supplier s = supplierService.getSupplierById(2);
        assertNotNull(s);
        assertEquals(2, s.getSupplier_id());
    }

    @Test
    public void testThereIsSupplierReturnsFalseWhenNoneExists() {
        assertFalse(supplierService.thereIsSupplier(999));
    }

    @Test
    public void testDeleteSupplierRemovesSupplier() {
        supplierService.createSupplier("Delete Me", 3, 123, 456, "Bank Transfer", 5551236, "delete@mail.com", "Standing Order");
        supplierService.deleteSupplier(3);
        assertFalse(supplierService.thereIsSupplier(3));
    }

    @Test
    public void testHasSuppliers() {
        assertFalse(supplierService.hasSuppliers());
        supplierService.createSupplier("Exists", 4, 123, 456, "Card", 5551237, "exists@mail.com", "Prepaid");
        assertTrue(supplierService.hasSuppliers());
    }
}
