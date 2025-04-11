package Domain;

/**
 * Represents a product in the store.
 * Contains identification, storage information, expiration, classification details,
 * and defect status.
 * This class belongs to the Domain layer and models the core product entity.
 */
public class Product {
    /** The unique ID of the product */
    private int product_id;

    /** The name of the product */
    private String product_name;

    /** The product's expiration date as a string */
    private String expiring_date;

    /** The section in which the product is located */
    private String section;

    /** The storage location (either "interior" or "warehouse") */
    private String stored;

    /** Indicates whether the product is defective */
    private boolean isDefect;

    /** The classification details of the product */
    private Classification classification;

    /**
     * Constructs a new Product instance.
     * By default, the product is not marked as defective.
     */
    public Product() {
        isDefect = false;
    }

    /**
     * Gets the product ID.
     *
     * @return the product ID
     */
    public int getProductId() {
        return product_id;
    }

    /**
     * Sets the product ID.
     *
     * @param product_id the ID to set
     */
    public void setProductId(int product_id) {
        this.product_id = product_id;
    }

    /**
     * Gets the product name.
     *
     * @return the product name
     */
    public String getProductName() {
        return product_name;
    }

    /**
     * Sets the product name.
     *
     * @param name the name to set
     */
    public void setProductName(String name) {
        this.product_name = name;
    }

    /**
     * Gets the product's expiration date.
     *
     * @return the expiration date
     */
    public String getExpiringDate() {
        return expiring_date;
    }

    /**
     * Sets the product's expiration date.
     *
     * @param expiring_date the date to set
     */
    public void setExpiringDate(String expiring_date) {
        this.expiring_date = expiring_date;
    }

    /**
     * Gets the section where the product is located.
     *
     * @return the section
     */
    public String getSection() {
        return section;
    }

    /**
     * Sets the section of the product.
     *
     * @param section the section to set
     */
    public void setSection(String section) {
        this.section = section;
    }

    /**
     * Gets the storage location of the product.
     *
     * @return the storage location ("interior" or "warehouse")
     */
    public String getStored() {
        return stored;
    }

    /**
     * Sets the storage location of the product.
     *
     * @param stored the storage location to set ("interior" or "warehouse")
     */
    public void setStored(String stored) {
        this.stored = stored;
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

    /**
     * Gets the classification details of the product.
     *
     * @return the classification object
     */
    public Classification getClassification() {
        return classification;
    }

    /**
     * Sets the classification details of the product.
     *
     * @param classification the classification object to set
     */
    public void setClassification(Classification classification) {
        this.classification = classification;
    }
}
