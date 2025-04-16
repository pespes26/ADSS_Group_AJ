package com.superli.deliveries.domain; // שם חבילה לדוגמה

import java.util.Objects;

/**
 * Represents a physical site (e.g., store, supplier, warehouse)
 * relevant to the deliveries module.
 * Corresponds to the Site class in the Domain Layer class diagram.
 */
public class Site {

    // Attributes
    private final String siteId; // Unique identifier for the site
    private String address;
    private String phoneNumber;
    private String contactPersonName;
    private ShipmentArea shipmentArea; // The shipment area this site belongs to

    // Optional attribute:
    // private String siteType; // E.g., "STORE", "SUPPLIER", "WAREHOUSE"

    /**
     * Constructs a new Site object.
     *
     * @param siteId            The unique identifier for the site. Cannot be null or empty.
     * @param address           The site's address. Cannot be null or empty.
     * @param phoneNumber       The site's phone number (can be null or empty).
     * @param contactPersonName The name of the contact person at the site (can be null or empty).
     * @param shipmentArea      The shipment area the site belongs to. Cannot be null.
     * @throws IllegalArgumentException if siteId, address, or shipmentArea is null or empty/null respectively.
     */
    public Site(String siteId, String address, String phoneNumber, String contactPersonName, ShipmentArea shipmentArea) {
        // Basic validation
        if (siteId == null || siteId.trim().isEmpty()) {
            throw new IllegalArgumentException("Site ID cannot be null or empty.");
        }
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be null or empty.");
        }
         if (shipmentArea == null) {
            throw new IllegalArgumentException("Shipment Area cannot be null.");
        }

        this.siteId = siteId;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.contactPersonName = contactPersonName;
        this.shipmentArea = shipmentArea;
        // this.siteType = siteType; // If using siteType
    }

    // --- Getters ---

    /**
     * Gets the unique identifier of the site.
     * @return The site ID.
     */
    public String getSiteId() {
        return siteId;
    }

    /**
     * Gets the address of the site.
     * @return The site address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Gets the phone number for the site.
     * @return The phone number.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Gets the name of the contact person at the site.
     * @return The contact person's name.
     */
    public String getContactPersonName() {
        return contactPersonName;
    }

    /**
     * Gets the shipment area this site belongs to.
     * @return The associated ShipmentArea object.
     */
    public ShipmentArea getShipmentArea() {
        return shipmentArea;
    }

    /* Optional getter if using siteType
    public String getSiteType() {
        return siteType;
    }
    */

    // --- Setters --- (siteId is final)

    /**
     * Sets the address for the site.
     * @param address The new address. Cannot be null or empty.
     * @throws IllegalArgumentException if address is null or empty.
     */
    public void setAddress(String address) {
         if (address == null || address.trim().isEmpty()) {
             throw new IllegalArgumentException("Address cannot be null or empty.");
         }
        this.address = address;
    }

    /**
     * Sets the phone number for the site.
     * @param phoneNumber The new phone number.
     */
    public void setPhoneNumber(String phoneNumber) {
        // Consider adding format validation if needed
        this.phoneNumber = phoneNumber;
    }

    /**
     * Sets the contact person's name for the site.
     * @param contactPersonName The new contact person's name.
     */
    public void setContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName;
    }

    /**
     * Sets or updates the shipment area this site belongs to.
     * @param shipmentArea The new shipment area. Cannot be null.
     * @throws IllegalArgumentException if shipmentArea is null.
     */
    public void setShipmentArea(ShipmentArea shipmentArea) {
        if (shipmentArea == null) {
             throw new IllegalArgumentException("Shipment Area cannot be null.");
        }
        this.shipmentArea = shipmentArea;
    }

    /* Optional setter if using siteType
    public void setSiteType(String siteType) {
        // Consider validation (e.g., must be "STORE", "SUPPLIER", etc.)
        this.siteType = siteType;
    }
    */


    // --- Standard Methods ---

    /**
     * Returns a string representation of the Site object.
     * @return A string representation including key attributes.
     */
    @Override
    public String toString() {
        return "Site{" +
               "siteId='" + siteId + '\'' +
               ", address='" + address + '\'' +
               ", phoneNumber='" + phoneNumber + '\'' +
               ", contactPersonName='" + contactPersonName + '\'' +
               ", shipmentAreaId=" + (shipmentArea != null ? shipmentArea.getAreaId() : "null") +
               // ", siteType='" + siteType + '\'' + // If using siteType
               '}';
    }

   /**
     * Checks if this Site is equal to another object.
     * Equality is based solely on the siteId.
     * @param o The object to compare with.
     * @return true if the objects are equal (same siteId), false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Site site = (Site) o;
        return siteId.equals(site.siteId);
    }

   /**
     * Returns the hash code for this Site.
     * Based solely on the siteId.
     * @return The hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(siteId);
    }
}