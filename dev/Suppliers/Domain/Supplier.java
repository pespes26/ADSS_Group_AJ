package Inventory.Presentation.Domain;

import Suppliers.Domain.Agreement;

import java.util.HashMap;

/**
 * Represents a supplier in the system.
 * A supplier has basic contact and financial details and can be associated with multiple agreements.
 */
public class Supplier {
    private final HashMap<Integer, Agreement> agreements; // Maps agreement ID → Agreement
    String supplierName;
    int supplier_id;
    int company_id;
    int bankAccount;
    String paymentMethod;
    String paymentCondition;
    long phoneNumber;
    String email;

    /**
     * Constructs a new Supplier with all required details.
     *
     * @param supplierName      the name of the supplier
     * @param supplier_id       unique supplier ID
     * @param company_id        the ID of the associated company
     * @param bankAccount       supplier's bank account number
     * @param paymentMethod     payment method (e.g., Bank Transfer, Check)
     * @param phoneNumber       supplier contact phone number
     * @param email             supplier's email address
     * @param paymentCondition  payment condition (e.g., Prepaid, Pay at delivery)
     */
    public Supplier(String supplierName, int supplier_id, int company_id, int bankAccount, String paymentMethod, long phoneNumber, String email, String paymentCondition){
        this.supplierName = supplierName;
        this.supplier_id = supplier_id;
        this.company_id = company_id;
        this.bankAccount = bankAccount;
        this.paymentMethod = paymentMethod;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.paymentCondition = paymentCondition;
        this.agreements = new HashMap<>();
    }

    /**
     * Adds a new agreement to the supplier.
     *
     * @param agreement the agreement to be added
     */
    public void addNewAgreement(Agreement agreement) { // Not critical, but used to associate agreement with supplier
        if (agreement != null) {
            agreements.put(agreement.getAgreementID(), agreement);
        }
    }

    /**
     * Returns the supplier's unique ID.
     *
     * @return the supplier ID
     */
    public int getSupplier_id() {
        return supplier_id;
    }

    /**
     * Removes all agreements associated with the supplier and returns their IDs.
     *
     * @return array of removed agreement IDs
     */
    public int[] removeAllAgreementFromSupplier() {
        int size = agreements.size();
        int[] agreementIDs = new int[size];
        int i = 0;

        for (Agreement agreement : new HashMap<>(agreements).values()) {
            int agreement_ID = agreement.getAgreementID();
            agreements.remove(agreement_ID);
            agreementIDs[i++] = agreement_ID;
        }
        return agreementIDs;
    }

    /**
     * Retrieves all agreement IDs associated with this supplier.
     *
     * @return array of agreement IDs
     */
    public int[] getAllAgreementIDs() {
        int[] agreementIDs = new int[agreements.size()];
        int i = 0;

        for (Agreement agreement : agreements.values()) {
            agreementIDs[i++] = agreement.getAgreementID();
        }

        return agreementIDs;
    }
}
