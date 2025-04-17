/**
 * The AgreementService class manages agreements with suppliers.
 * It handles creation, lookup, product management, and deletion of agreements.
 * Each agreement includes supplier information, delivery days, and associated products.
 * This service interacts with the ProductService to maintain data consistency.
 */

package Service;

import Domain.Agreement;
import Domain.Product;

import java.util.HashMap;

public class AgreementService {
    private HashMap<Integer, Agreement> agreementHashMap; // Stores agreements using agreementID as the key
    private ProductService productService; // Reference to the product service used for creating and deleting products

    /**
     * Sets the ProductService dependency for this service.
     *
     * @param productService the product service instance to be used
     */
    public void setProductService(ProductService productService) {
        this.productService = productService; // Assign the provided ProductService to the field
    }

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

    /**
     * Finds an agreement by its ID.
     *
     * @param agreement_ID the ID of the agreement to search
     * @return the Agreement object if found, otherwise null
     */
    public Agreement findAgreementWithSupplier(int agreement_ID) {
        return agreementHashMap.get(agreement_ID); // Retrieve the agreement from the map
    }

    /**
     * Adds a new product to an existing agreement.
     * It also creates the product via the ProductService.
     *
     * @param agreementID     the ID of the agreement to which the product will be added
     * @param catalog_Number  the catalog number of the product
     * @param product_id      the unique ID of the product
     * @param price           the price of the product
     * @param unitsOfMeasure  the units in which the product is measured
     */
    public void addProductToAgreement(int agreementID, int catalog_Number, int product_id, double price, String unitsOfMeasure) {
        Agreement agreement = agreementHashMap.get(agreementID); // Find the relevant agreement
        if (agreement != null) {
            Product newProduct = productService.createProduct(catalog_Number, product_id, price, unitsOfMeasure); // Create the product
            agreement.addNewSupplierProduct(product_id, newProduct); // Add the product to the agreement
        }
    }

    /**
     * Removes a product from a specific agreement and deletes it from the ProductService.
     *
     * @param agreement_ID the ID of the agreement
     * @param product_ID   the ID of the product to remove
     */
    public void removeProductFromAgreement(int agreement_ID, int product_ID) {
        Agreement agreement = findAgreementWithSupplier(agreement_ID); // Find the relevant agreement
        if (agreement != null) {
            int catalogNumber = agreement.removeProduct(product_ID); // Remove the product and get its catalog number
            productService.delete_by_catalog(catalogNumber); // Remove the product from the system
        }
    }

    /**
     * Deletes an entire agreement and removes all its associated products from the system.
     *
     * @param agreement_ID the ID of the agreement to delete
     */
    public void deleteAgreementWithSupplier(int agreement_ID) {
        Agreement agreement = findAgreementWithSupplier(agreement_ID); // Find the agreement
        if (agreement != null) {
            int[] allCatalogNumbers = agreement.removeAllProducts(); // Remove all products from Map in agreement and collect their catalog numbers
            for (int catalogNumber : allCatalogNumbers) {
                productService.delete_by_catalog(catalogNumber); // Delete each product from the product system
            }
        }
    }
}
