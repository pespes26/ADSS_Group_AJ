package com.superli.deliveries.presentation;

/**
 * Holds driver details formatted specifically for presentation purposes.
 * Used as a data carrier (DTO/ViewModel) in the Presentation Layer.
 */
public class DriverDetailsView {

    // Attributes relevant for display
    private final String name;
    private final String licenseType;

    /**
     * Constructs a DriverDetailsView object.
     * Usually populated with data originating from a Driver domain object.
     *
     * @param name        The driver's name to be displayed.
     * @param licenseType The driver's license type to be displayed.
     */
    public DriverDetailsView(String name, String licenseType) {
        // Assuming data validity is handled before creating this view object
        this.name = name;
        this.licenseType = licenseType;
    }

    // --- Getters ---

    /**
     * Gets the driver's name for display.
     * @return The name string.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the driver's license type for display.
     * @return The license type string.
     */
    public String getLicenseType() {
        return licenseType;
    }

    // --- Standard Methods ---

    /**
     * Returns a string representation of the DriverDetailsView object.
     * @return A string representation of the view data.
     */
     @Override
    public String toString() {
        return "DriverDetailsView{" +
               "name='" + name + '\'' +
               ", licenseType='" + licenseType + '\'' +
               '}';
    }

    // equals() and hashCode() are typically not required for display-only DTOs
}