/**
 * The AgreementService class manages agreements with suppliers.
 * It handles creation, lookup, product management, and deletion of agreements.
 * Each agreement includes supplier information, delivery days, and associated products.
 * This service interacts with the ProductService to maintain data consistency.
 */

package Service;

import Domain.Agreement;

import java.util.HashMap;

public class AgreementService {
    private HashMap<Integer, Agreement> agreementHashMap; // Stores agreements using agreementID as the key

    /**
     * Constructs a new AgreementService with an empty agreement map.
     */
    public AgreementService() {
        this.agreementHashMap = new HashMap<>(); // Initialize the agreement map
    }

    /**
     * Creates a new agreement with the given supplier.
     *
     * @param agreement_ID the unique ID of the agreement
     * @param supplier_ID  the ID of the supplier
     * @param deliveryDays the days of the week when delivery is possible
     * @param selfPickup   whether the supplier uses self-pickup or not
     */
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


    public Integer removeProductFromAgreement(int agreement_ID, int product_ID) {
        Agreement agreement = getAgreementByID(agreement_ID); // Find the relevant agreement
        if (agreement != null) {
            Integer catalogNumber = agreement.removeProductByProductID(product_ID); // Remove the product and get its catalog number
            return catalogNumber;
        }
        return null;
    }


    public int[] deleteAgreementWithSupplier(int agreement_ID) {
        Agreement agreement = getAgreementByID(agreement_ID);
        if (agreement != null) {
            return agreement.removeAllProductsFromAgreement();
        }
        return new int[0]; // במקום null
    }

    // מתודה בודקת אם בהסכם יש מוצרים
    public boolean hasProducts(Agreement agreement) {
        return agreement.hasProducts();  // בודק אם ההסכם מכיל מוצרים
    }

}
