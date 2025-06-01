//package Suppliers.Test;
//
//
//import Suppliers.Service.SupplierService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class SupplierServiceTest {
//
//    private SupplierService supplierService;
//
//    @BeforeEach
//    public void setUp() {
//        supplierService = new SupplierService();
//    }
//
//    @Test
//    public void givenNewSupplier_whenCreateSupplier_thenSupplierIsAdded() {
//        supplierService.createSupplier("Test Supplier", 1, 123, 456, "Cash", 5551234, "test@mail.com", "Prepaid");
//        assertTrue(supplierService.thereIsSupplier(1));
//    }
//
//
//    @Test
//    public void givenNoSuppliers_whenCheckIfExists_thenReturnsFalse() {
//        assertFalse(supplierService.thereIsSupplier(999));
//    }
//
//    @Test
//    public void givenExistingSupplier_whenDeleteSupplier_thenSupplierIsRemoved() {
//        supplierService.createSupplier("Delete Me", 3, 123, 456, "Bank Transfer", 5551236, "delete@mail.com", "Standing Order");
//        supplierService.deleteSupplier(3);
//        assertFalse(supplierService.thereIsSupplier(3));
//    }
//
//    @Test
//    public void givenNoSuppliersInitially_whenCreateSupplier_thenHasSuppliersReturnsTrue() {
//        assertFalse(supplierService.hasSuppliers());
//        supplierService.createSupplier("Exists", 4, 123, 456, "Card", 5551237, "exists@mail.com", "Prepaid");
//        assertTrue(supplierService.hasSuppliers());
//    }
//
//}
