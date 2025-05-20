package Domain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ControllerCoreFunctionsTest {

    private Controller controller;

    @BeforeEach
    public void setup() {
        controller = new Controller();
    }

    @Test
    public void givenSupplierWithAgreementsAndProducts_whenDeleteSupplier_thenAllDataIsRemoved() {
        controller.createSupplier("Test Supplier", 1, 123, 456, "Cash", 1234567890L, "test@supplier.com", "Prepaid");
        controller.createAgreement(100, 1, new String[]{"Mon"}, false);
        Product product = controller.createProduct(10, 20, 50.0, "kg", 1);
        controller.addProductToAgreement(20, product, 100);

        controller.deleteSupplier(1);

        assertFalse(controller.thereIsSupplier(1));
        assertFalse(controller.thereIsProductWithSameProductIDInAgreement(20, 1));
        assertFalse(controller.thereIsProductWithSameCatalogNumberInAgreement(10, 1));
        assertFalse(controller.existsProductWithCatalogAndSupplierId(10, 1));
    }

    @Test
    public void givenSupplierWithAgreements_whenDeleteAllAgreements_thenCatalogNumbersReturned() {
        controller.createSupplier("Test", 2, 111, 222, "Cash", 1234567891L, "test2@supplier.com", "Prepaid");
        controller.createAgreement(201, 2, new String[]{"Tue"}, false);
        Product p = controller.createProduct(11, 21, 30.0, "pcs", 2);
        controller.addProductToAgreement(21, p, 201);

        List<Integer> removed = controller.deleteAllAgreementFromSupplier(2);
        assertTrue(removed.contains(11));
    }

    @Test
    public void givenAgreementWithProduct_whenDeleteAgreement_thenProductIsRemoved() {
        controller.createSupplier("Supp", 3, 123, 456, "Cash", 1234567892L, "supp@x.com", "Prepaid");
        controller.createAgreement(301, 3, new String[]{"Wed"}, false);
        Product p = controller.createProduct(12, 22, 45.0, "L", 3);
        controller.addProductToAgreement(22, p, 301);

        controller.deleteAgreement(301);

        assertFalse(controller.thereIsProductWithSameProductIDInAgreement(22, 3));
        assertFalse(controller.existsProductWithCatalogAndSupplierId(22, 3));//בדיקה במערך שבProductService

    }

    @Test
    public void givenValidAgreement_whenAddProductToAgreement_thenProductIsLinkedToAgreement() {
        controller.createSupplier("ABC", 4, 789, 321, "Cash", 1234567893L, "abc@x.com", "Prepaid");
        controller.createAgreement(401, 4, new String[]{"Thu"}, false);
        Product product = controller.createProduct(13, 23, 15.0, "pack", 4);

        controller.addProductToAgreement(23, product, 401);
        assertTrue(controller.thereIsProductWithSameProductIDInAgreement(23, 4));
        assertTrue(controller.existsProductWithCatalogAndSupplierId(13, 4));//בדיקה במערך שבProductService
    }

    @Test
    public void givenProductInAgreement_whenDeleteByCatalogAndSupplier_thenProductIsRemovedFromSystem() {
        controller.createSupplier("XYZ", 5, 654, 987, "Cash", 1234567894L, "xyz@x.com", "Prepaid");
        controller.createAgreement(501, 5, new String[]{"Fri"}, false);
        Product product = controller.createProduct(14, 24, 99.0, "bottle", 5);
        controller.addProductToAgreement(24, product, 501);

        controller.delete_by_catalogAndSupplierId(14, 5, 501);

        assertFalse(controller.thereIsProductWithSameCatalogNumberInAgreement(14, 5));
    }

    @Test
    public void givenProductInAgreement_whenUpdateProductUnit_thenUnitIsUpdatedSuccessfully() {
        controller.createSupplier("UPD", 6, 321, 654, "Cash", 1234567895L, "upd@x.com", "Prepaid");
        controller.createAgreement(601, 6, new String[]{"Sat"}, false);
        Product product = controller.createProduct(15, 25, 77.0, "box", 6);
        controller.addProductToAgreement(25, product, 601);

        controller.updateProductUnit(15, "crate", 601);


        assertTrue(controller.thereIsProductWithSameCatalogNumberInAgreement(15, 6)); // אינדיקציה שהמוצר קיים
    }

    @Test
    public void givenProductWithDiscount_whenOrderWithBestPrice_thenReturnsLowestPrice() {
        controller.createSupplier("PriceCo", 7, 333, 444, "Cash", 1234567896L, "price@co.com", "Prepaid");
        Product p = controller.createProduct(16, 26, 100.0, "kg", 7);
        p.updateOrAddDiscountRule(10, 10);

        Map<Integer, Integer> productsInOrder = new HashMap<>();
        productsInOrder.put(26, 12);

        Map<Integer, Map.Entry<Integer, Double>> result = controller.orderWithBestPrice(productsInOrder);

        assertEquals(1, result.size());
        assertEquals(12, result.get(26).getKey());
        assertEquals(1080.0, result.get(26).getValue()); // 12 * 90.0
    }

    @Test
    public void givenSupplierWithCatalogNumber_whenCheckForDuplicate_thenReturnTrue() {
        controller.createSupplier("Dup", 8, 999, 888, "Cash", 1234567897L, "dup@x.com", "Prepaid");
        controller.createAgreement(801, 8, new String[]{"Sun"}, false);
        Product p = controller.createProduct(17, 27, 22.0, "unit", 8);
        controller.addProductToAgreement(27, p, 801);

        assertTrue(controller.thereIsProductWithSameCatalogNumberInAgreement(17, 8));
    }

    @Test
    public void givenSupplierWithProductID_whenCheckForDuplicate_thenReturnTrue() {
        controller.createSupplier("Dup2", 9, 666, 777, "Cash", 1234567898L, "dup2@x.com", "Prepaid");
        controller.createAgreement(901, 9, new String[]{"Sun"}, false);
        Product p = controller.createProduct(18, 28, 22.0, "unit", 9);
        controller.addProductToAgreement(28, p, 901);

        assertTrue(controller.thereIsProductWithSameProductIDInAgreement(28, 9));
    }

}
