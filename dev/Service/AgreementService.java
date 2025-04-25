
package Service;

import Domain.Agreement;

import java.util.HashMap;

public class AgreementService {
    private final HashMap<Integer, Agreement> agreementHashMap; // Stores agreements using agreementID as the key


    public AgreementService() {
        this.agreementHashMap = new HashMap<>(); // Initialize the agreement map
    }


    public Agreement createAgreementWithSupplier(int agreement_ID, int supplier_ID, String[] deliveryDays, boolean selfPickup) {
        Agreement agreement = new Agreement(agreement_ID, supplier_ID, deliveryDays, selfPickup); // Create a new agreement object
        this.agreementHashMap.put(agreement_ID, agreement); // Store it in the internal map
        return agreement;
    }


    public Agreement getAgreementByID(int agreement_ID) {
        return agreementHashMap.get(agreement_ID); // Retrieve the agreement from the map
    }


    public boolean thereIsAgreement(int agreement_ID) {
        return agreementHashMap.containsKey(agreement_ID);
    }


    public int[] deleteAgreementWithSupplier(int agreement_ID) {
        Agreement agreement = getAgreementByID(agreement_ID);
        if (agreement == null) {
            return new int[0];
        }

        int[] catalogNumbers = new int[0];

        if (agreement.hasProducts()) {
            catalogNumbers = agreement.removeAllProductsFromAgreement();
        }

        agreementHashMap.remove(agreement_ID); // הסכם תמיד נמחק
        return catalogNumbers;
    }

}
