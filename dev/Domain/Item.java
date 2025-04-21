package Domain;

public class Item {
    // Product Catalog Number identifies the type of product this item instance belongs to.
    // It links the item to its corresponding Product entity in the system.
    private int catalogNumber;
    private int item_id;
    private String item_expiring_date;
    private int item_size; //1 - small, 2 - medium, 3 - large
    private String storage_location; //warehouse or Interior store
    private String section_in_store; //for example: E7
    private boolean isDefect;


    public int getCatalogNumber() {
        return catalogNumber;
    }

    public void setCatalogNumber(int catalogNumber) {
        this.catalogNumber = catalogNumber;
    }

    /**
     * Gets the product ID.
     *
     * @return the product ID
     */
    public int getItemId() {
        return item_id;
    }

    /**
     * Sets the product ID.
     *
     * @param product_id the ID to set
     */
    public void setItemId(int product_id) {
        this.item_id = product_id;
    }

    /**
     * Gets the product's expiration date.
     *
     * @return the expiration date
     */
    public String getItemExpiringDate() {
        return item_expiring_date;
    }

    /**
     * Sets the product's expiration date.
     *
     * @param item_expiring_date the date to set
     */
    public void setItemExpiringDate(String item_expiring_date) {
        this.item_expiring_date = item_expiring_date;
    }

    public int getItemSize() {
        return item_size;
    }

    /**
     * Sets the product's size.
     *
     * @param item_size the size to set (1 = small, 2 = medium, 3 = large)
     */
    public void setItemSize(int item_size) {
        this.item_size = item_size;
    }

    /**
     * Gets the section where the product is located.
     *
     * @return the section
     */
    public String getSectionInStore() {
        return section_in_store;
    }

    /**
     * Sets the section of the product.
     *
     * @param section the section to set
     */
    public void setSectionInStore(String section) {
        this.section_in_store = section;
    }

    /**
     * Gets the storage location of the product.
     *
     * @return the storage location ("interior" or "warehouse")
     */
    public String getStorageLocation() {
        return storage_location;
    }

    /**
     * Sets the storage location of the product.
     *
     * @param storage_location the storage location to set ("interior" or "warehouse")
     */
    public void setStorageLocation(String storage_location) {
        this.storage_location = storage_location;
    }

    /**
     * Checks whether the product is defective.
     *
     * @return true if the product is defective, false otherwise
     */
    public boolean isDefect() {
        return isDefect;
    }

    /**
     * Marks the product as defective or not.
     *
     * @param defect true if defective, false otherwise
     */
    public void setDefect(boolean defect) {
        isDefect = defect;
    }

}
