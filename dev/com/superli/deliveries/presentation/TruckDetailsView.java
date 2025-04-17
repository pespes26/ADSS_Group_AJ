package com.superli.deliveries.presentation;

/**
 * Holds truck details formatted specifically for presentation purposes.
 * Used as a data carrier (DTO/ViewModel) in the Presentation Layer.
 */
public class TruckDetailsView {

    // Attributes relevant for display in this context
    private final String licensePlateNumber;
    private final String model;
    private final float maxWeight; // Max allowed weight (useful context for actual weight)

    /**
     * Constructs a TruckDetailsView object.
     * Usually populated with data originating from a Truck domain object.
     *
     * @param licensePlateNumber The truck's license plate to be displayed.
     * @param model              The truck's model to be displayed.
     * @param maxWeight          The truck's maximum allowed weight to be displayed.
     */
    public TruckDetailsView(String licensePlateNumber, String model, float maxWeight) {
        // Assuming data validity is handled before creating this view object
        this.licensePlateNumber = licensePlateNumber;
        this.model = model;
        this.maxWeight = maxWeight;
    }

    // --- Getters ---

    /**
     * Gets the truck's license plate number for display.
     * @return The license plate number string.
     */
    public String getLicensePlateNumber() {
        return licensePlateNumber;
    }

    /**
     * Gets the truck's model for display.
     * @return The model string.
     */
    public String getModel() {
        return model;
    }

    /**
     * Gets the truck's maximum allowed weight for display.
     * @return The maximum weight.
     */
    public float getMaxWeight() {
        return maxWeight;
    }

    // --- Standard Methods ---

    /**
     * Returns a string representation of the TruckDetailsView object.
     * @return A string representation of the view data.
     */
     @Override
    public String toString() {
        return "TruckDetailsView{" +
               "licensePlateNumber='" + licensePlateNumber + '\'' +
               ", model='" + model + '\'' +
               ", maxWeight=" + maxWeight +
               '}';
    }

    // equals() and hashCode() are typically not required for display-only DTOs
}