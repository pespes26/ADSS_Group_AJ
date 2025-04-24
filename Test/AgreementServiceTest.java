package Service;

import Domain.Agreement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AgreementServiceTest {

    private AgreementService agreementService;
    @BeforeEach
    public void setUp() {
        agreementService = new AgreementService();
    }

    @Test
    public void givenNewAgreementDetails_whenCreateAgreement_thenAgreementIsCreatedSuccessfully() {
        Agreement agreement = agreementService.createAgreementWithSupplier(1, 100, new String[]{"Mon", "Wed"}, true);
        assertNotNull(agreement);
        assertEquals(1, agreement.getAgreementID());
        assertEquals(100, agreement.getSupplier_ID());
        assertTrue(agreementService.thereIsAgreement(1));
    }

    @Test
    public void givenExistingAgreement_whenGetAgreementByID_thenReturnsCorrectAgreement() {
        agreementService.createAgreementWithSupplier(2, 200, new String[]{"Tue"}, false);
        Agreement found = agreementService.getAgreementByID(2);
        assertNotNull(found);
        assertEquals(200, found.getSupplier_ID());
    }

    @Test
    public void givenAgreementExists_whenCheckIfExists_thenReturnsTrue() {
        agreementService.createAgreementWithSupplier(3, 300, new String[]{"Thu"}, true);
        assertTrue(agreementService.thereIsAgreement(3));
    }

    @Test
    public void givenNoAgreement_whenCheckIfExists_thenReturnsFalse() {
        assertFalse(agreementService.thereIsAgreement(999));
    }

    @Test
    public void givenExistingAgreement_whenDeleteAgreement_thenAgreementIsRemoved() {
        agreementService.createAgreementWithSupplier(4, 400, new String[]{"Fri"}, true);
        int[] result = agreementService.deleteAgreementWithSupplier(4);
        assertNotNull(result);
        assertEquals(0, result.length); // No products in agreement
        assertFalse(agreementService.thereIsAgreement(4));
    }

}
