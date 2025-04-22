/**
 * The Agreement class represents an agreement between the supermarket and a supplier.
 * It includes delivery information and a collection of products provided by the supplier.
 * Each product is stored by its ID and includes details such as price and catalog number.
 */
package Domain;

import java.util.HashMap;
import java.util.Map;

public class Agreement {
    private int agreement_ID; // Unique ID of the agreement
    private int supplier_ID; // Unique ID of the supplier
    private String[] deliveryDays; // Days on which the supplier can deliver
    private boolean selfPickup; // Whether delivery is self-handled by the supermarket
    private HashMap<Integer, Product> supplierProducts; // Maps productID → Product

    /**
     * Constructs a new Agreement.
     *
     * @param agreement_ID   unique ID for the agreement
     * @param supplier_ID    ID of the supplier
     * @param deliveryDays   array of delivery days
     * @param selfPickup     whether the delivery is self pickup or not
     */
    public Agreement(int agreement_ID, int supplier_ID, String[] deliveryDays, boolean selfPickup) {
        this.agreement_ID = agreement_ID; // Set agreement ID
        this.supplier_ID = supplier_ID; // Set supplier ID
        this.deliveryDays = deliveryDays; // Set delivery days
        this.selfPickup = selfPickup; // Set self-pickup flag
        this.supplierProducts = new HashMap<>(); // Initialize product map
    }

    /**
     * Adds a new product to the agreement.
     *
     * @param productID   the product ID as seen by the supermarket
     * @param newProduct  the product object to add
     */
    public void addNewSupplierProduct(int productID, Product newProduct) {
        this.supplierProducts.put(productID, newProduct); // Add the product to the map
    }

    /**
     * Gets the supplier's ID.
     *
     * @return the supplier ID
     */
    public int getSupplier_ID() {
        return supplier_ID;
    }

    /**
     * Gets the agreement's ID.
     *
     * @return the agreement ID
     */
    public int getAgreementID() {
        return agreement_ID;
    }


    /**
     * Updates the price of a product in the agreement.
     *
     * @param productID the ID of the product to update
     * @param priceNew  the new price
     */
    public void updateProductPrice(int productID, int priceNew) {
        Product product = supplierProducts.get(productID); // Retrieve the product
        if (product != null) {
            product.setPrice(priceNew); // Update the price if product exists
        }
    }

    /**
     * Updates the discount details of a product in the agreement.
     *
     * @param productID the product ID
     * @param discount  the discount percentage
     * @param amount    the minimum amount for the discount
     */
    public void updateProductDiscount(int productID, int discount, int amount) {
        Product product = supplierProducts.get(productID); // Retrieve the product
        if (product != null) {
            product.setDiscount(discount, amount); // Update discount info if product exists
        }
    }

    /**
     * Updates the delivery days for this agreement.
     *
     * @param newDeliveryDays an array of updated delivery days
     */
    public void updateDeliveryDays(String[] newDeliveryDays) {
        this.deliveryDays = newDeliveryDays; // Update the delivery days
    }

    /**
     * Toggles the self-pickup option.
     */
    public void updateSelfDeliveryOption() {
        this.selfPickup = !this.selfPickup; // Flip the boolean flag
    }

    /**
     * Removes a specific product from the agreement.
     *
     * @param productID the ID of the product to remove
     * @return the catalog number of the removed product
     */
//    public int removeProduct(int productID) {
//        Product product = supplierProducts.get(productID); // Retrieve the product
//        int catalogNumber = product.getCatalog_Number(); // Get its catalog number
//        supplierProducts.remove(productID); // Remove from the map
//        return catalogNumber; // Return the catalog number
//    }

    public int removeProduct(int productID) {
        Product product = supplierProducts.get(productID); // Retrieve the product
        if (product == null) {
            throw new IllegalArgumentException("Product not found in agreement.");
        }

        int catalogNumber = product.getCatalog_Number(); // Get its catalog number
        supplierProducts.remove(productID); // Remove from the map
        return catalogNumber; // Return the catalog number
    }


    /**
     * Removes all products from the agreement.
     *
     * @return an array of catalog numbers of all removed products
     */
    public int[] removeAllProducts() {
        int size = this.supplierProducts.size(); // Get how many products there are
        int[] productCatalogNumbers = new int[size]; // Prepare array to hold catalog numbers

        int index = 0; // Start index
        for (Map.Entry<Integer, Product> entry : new HashMap<>(this.supplierProducts).entrySet()) {
            Product product = entry.getValue(); // Get the product
            int catalogNumber = product.getCatalog_Number(); // Get its catalog number
            productCatalogNumbers[index++] = catalogNumber; // Store it in the array
            supplierProducts.remove(entry.getKey()); // Remove from the original map
        }

        return productCatalogNumbers; // Return all catalog numbers
    }

    public boolean hasProducts() {
        return supplierProducts != null && !supplierProducts.isEmpty();  // אם ה-HashMap לא ריק, יש לפחות מוצר אחד
    }

    public boolean hasProductWithCatalogNumber(int catalogNumber) {
        for (Product product : supplierProducts.values()) {
            if (product.getCatalog_Number() == catalogNumber) {
                return true;
            }
        }
        return false;
    }


}
