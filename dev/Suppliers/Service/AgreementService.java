package Suppliers.Service;

import Suppliers.Domain.Agreement;

import java.util.HashMap;

/**
 * The AgreementService class manages agreements with suppliers.
 * It handles creation, lookup, and deletion of agreements.
 * Each agreement includes supplier information, delivery days, and associated products.
 */
public class AgreementService {
    private final HashMap<Integer, Agreement> agreementHashMap; // Stores agreements using agreementID as the key

    /**
     * Constructs a new AgreementService with an empty agreement map.
     */
    public AgreementService() {
        this.agreementHashMap = new HashMap<>(); // Initialize the agreement map
    }

    /**
     * Creates a new agreement with the given supplier and stores it.
     *
     * @param agreement_ID the unique ID of the agreement
     * @param supplier_ID the supplier's ID associated with the agreement
     * @param deliveryDays the days of the week when deliveries can occur
     * @param selfPickup true if the supermarket will handle pickup, false otherwise
     * @return the created Agreement object
     */
    public Agreement createAgreementWithSupplier(int agreement_ID, int supplier_ID, String[] deliveryDays, boolean selfPickup) {
        Agreement agreement = new Agreement(agreement_ID, supplier_ID, deliveryDays, selfPickup); // Create a new agreement object
        this.agreementHashMap.put(agreement_ID, agreement); // Store it in the internal map
        return agreement;
    }

    /**
     * Retrieves an agreement by its unique ID.
     *
     * @param agreement_ID the ID of the agreement to retrieve
     * @return the Agreement object if found, otherwise null
     */
    public Agreement getAgreementByID(int agreement_ID) {
        return agreementHashMap.get(agreement_ID); // Retrieve the agreement from the map
    }

    /**
     * Checks if an agreement exists in the system.
     *
     * @param agreement_ID the ID of the agreement to check
     * @return true if the agreement exists, false otherwise
     */
    public boolean thereIsAgreement(int agreement_ID) {
        return agreementHashMap.containsKey(agreement_ID);
    }

    /**
     * Deletes an agreement and removes all its associated products.
     *
     * @param agreement_ID the ID of the agreement to delete
     * @return an array of catalog numbers for products that were removed from the agreement
     */
    public int[] deleteAgreementWithSupplier(int agreement_ID) {
        Agreement agreement = getAgreementByID(agreement_ID);
        if (agreement == null) {
            return new int[0];
        }

        int[] catalogNumbers = new int[0];

        if (agreement.hasProducts()) {
            catalogNumbers = agreement.removeAllProductsFromAgreement(); // Remove associated products if any
        }

        agreementHashMap.remove(agreement_ID); // Agreement is always removed
        return catalogNumbers;
    }
}
