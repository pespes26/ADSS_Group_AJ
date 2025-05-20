package Inventory.Presentation.Domain;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a commercial agreement between a supplier and the supermarket.
 * Each agreement includes a supplier ID, delivery days, a self-pickup flag, and
 * a list of products provided by the supplier under this agreement.
 */
public class Agreement {
    private final int agreement_ID; // Unique ID of the agreement
    private final int supplier_ID; // Unique ID of the supplier
    private String[] deliveryDays; // Days on which the supplier can deliver
    private boolean selfPickup; // Whether delivery is self-handled by the supermarket
    private final HashMap<Integer, Product> supplierProducts; // Maps productID → Product

    /**
     * Constructs an Agreement object with specified attributes.
     *
     * @param agreement_ID  the unique ID of the agreement
     * @param supplier_ID   the unique ID of the supplier
     * @param deliveryDays  array of fixed delivery days (e.g., Mon, Wed)
     * @param selfPickup    true if delivery is self-handled by the supermarket
     */
    public Agreement(int agreement_ID, int supplier_ID, String[] deliveryDays, boolean selfPickup) {
        this.agreement_ID = agreement_ID;
        this.supplier_ID = supplier_ID;
        this.deliveryDays = deliveryDays;
        this.selfPickup = selfPickup;
        this.supplierProducts = new HashMap<>();
    }

    /**
     * Adds a new product to the agreement's product list.
     *
     * @param productID   the unique ID of the product
     * @param newProduct  the Product object to add
     */
    public void addNewSupplierProduct(int productID, Product newProduct) {
        this.supplierProducts.put(productID, newProduct);
    }

    /**
     * Removes a product from the agreement based on its catalog number.
     *
     * @param catalogNumber the catalog number of the product to remove
     */
    public void removeProductByProductCatalogNumber(int catalogNumber) {
        Integer keyToRemove = null;
        for (Map.Entry<Integer, Product> entry : supplierProducts.entrySet()) {
            if (entry.getValue().getCatalog_Number() == catalogNumber) {
                keyToRemove = entry.getKey();
                break;
            }
        }
        if (keyToRemove != null) {
            supplierProducts.remove(keyToRemove);
        }
    }

    /**
     * @return the supplier ID associated with this agreement
     */
    public int getSupplier_ID() {
        return supplier_ID;
    }

    /**
     * @return the agreement ID
     */
    public int getAgreementID() {
        return agreement_ID;
    }

    /**
     * Updates the delivery days for this agreement.
     *
     * @param newDeliveryDays new array of delivery days
     */
    public void updateDeliveryDays(String[] newDeliveryDays) {
        this.deliveryDays = newDeliveryDays;
    }

    /**
     * Toggles the self-pickup status for the agreement.
     *
     * @return the updated self-pickup status
     */
    public boolean updateSelfDeliveryOption() {
        this.selfPickup = !this.selfPickup;
        return this.selfPickup;
    }

    /**
     * Removes all products from the agreement and returns their catalog numbers.
     *
     * @return an array of catalog numbers of removed products
     */
    public int[] removeAllProductsFromAgreement() {
        int size = this.supplierProducts.size();
        int[] productCatalogNumbers = new int[size];
        int index = 0;
        for (Map.Entry<Integer, Product> entry : new HashMap<>(this.supplierProducts).entrySet()) {
            Product product = entry.getValue();
            int catalogNumber = product.getCatalog_Number();
            productCatalogNumbers[index++] = catalogNumber;
            supplierProducts.remove(entry.getKey());
        }
        return productCatalogNumbers;
    }

    /**
     * Retrieves a product by its catalog number.
     *
     * @param catalogNumber the catalog number to search for
     * @return the Product if found, otherwise null
     */
    public Product getProductByCatalog(int catalogNumber){
        for (Product product : supplierProducts.values()) {
            if (product.getCatalog_Number() == catalogNumber) {
                return product;
            }
        }
        return null;
    }

    /**
     * Checks if a product exists with the given catalog number.
     *
     * @param catalogNumber the catalog number to check
     * @return true if a product with this catalog number exists in the agreement
     */
    public boolean hasProductWithCatalogNumber(int catalogNumber) {
        for (Product product : supplierProducts.values()) {
            if (product.getCatalog_Number() == catalogNumber) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a product exists with the given product ID.
     *
     * @param productID the product ID to check
     * @return true if a product with this product ID exists in the agreement
     */
    public boolean hasProductWithProductID(int productID) {
        return supplierProducts.containsKey(productID);
    }

    /**
     * Checks if the agreement contains at least one product.
     *
     * @return true if there are products in the agreement
     */
    public boolean hasProducts() {
        return supplierProducts != null && !supplierProducts.isEmpty();
    }
}
