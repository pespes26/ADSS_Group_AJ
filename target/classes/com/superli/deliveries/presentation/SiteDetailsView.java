package com.superli.deliveries.presentation;

/**
 * Holds site details formatted specifically for presentation purposes.
 * Used as a data carrier (DTO/ViewModel) in the Presentation Layer.
 */
public class SiteDetailsView {

    // Attributes needed for display
    private final String address;
    private final String phoneNumber;
    private final String contactPersonName;

    /**
     * Constructs a SiteDetailsView object.
     * Usually populated with data originating from a Site domain object.
     *
     * @param address           The site's address to be displayed.
     * @param phoneNumber       The site's phone number to be displayed.
     * @param contactPersonName The contact person's name to be displayed.
     */
    public SiteDetailsView(String address, String phoneNumber, String contactPersonName) {
        // Assuming data validity is handled before creating this view object
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.contactPersonName = contactPersonName;
    }

    // --- Getters ---

    /**
     * Gets the site's address for display.
     * @return The address string.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Gets the site's phone number for display.
     * @return The phone number string.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Gets the site's contact person name for display.
     * @return The contact person's name string.
     */
    public String getContactPersonName() {
        return contactPersonName;
    }

    // --- Standard Methods ---

    /**
     * Returns a string representation of the SiteDetailsView object.
     * @return A string representation of the view data.
     */
     @Override
    public String toString() {
        return "SiteDetailsView{" +
               "address='" + address + '\'' +
               ", phoneNumber='" + phoneNumber + '\'' +
               ", contactPersonName='" + contactPersonName + '\'' +
               '}';
    }

    // equals() and hashCode() are typically not required for display-only DTOs
    // unless needed for specific UI logic (e.g., comparing view states).
}