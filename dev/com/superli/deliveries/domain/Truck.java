package com.superli.deliveries.domain;

import java.util.Objects;

/**
 * Represents a truck in the company's fleet.
 * Designed as an immutable class as truck characteristics rarely change.
 * Corresponds to the Truck class in the Domain Layer class diagram.
 */
public class Truck {

    // Attributes - All final for immutability
    private final String licensePlateNumber; // Unique identifier (license plate)
    private final String model;              // Truck model
    private final float netWeight;           // Net weight of the truck (e.g., in kg or tons)
    private final float maxWeight;           // Maximum allowed payload weight (e.g., in kg or tons)
    private final String requiredLicenseType; // License type required to operate this truck

    /**
     * Constructs a new Truck object.
     *
     * @param licensePlateNumber The unique license plate number. Cannot be null or empty.
     * @param model              The truck's model. Cannot be null or empty.
     * @param netWeight          The net weight of the truck. Must be positive.
     * @param maxWeight          The maximum allowed weight the truck can carry. Must be >= netWeight.
     * @param requiredLicenseType The license type required for this truck. Cannot be null or empty (assuming).
     * @throws IllegalArgumentException if any validation fails.
     */
    public Truck(String licensePlateNumber, String model, float netWeight, float maxWeight, String requiredLicenseType) {
        // Input validation
        if (licensePlateNumber == null || licensePlateNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("License plate number cannot be null or empty.");
        }
        if (model == null || model.trim().isEmpty()) {
            throw new IllegalArgumentException("Model cannot be null or empty.");
        }
        if (netWeight <= 0) {
            throw new IllegalArgumentException("Net weight must be positive.");
        }
        if (maxWeight < netWeight) {
            throw new IllegalArgumentException("Maximum weight cannot be less than net weight.");
        }
        if (requiredLicenseType == null || requiredLicenseType.trim().isEmpty()) {
             throw new IllegalArgumentException("Required license type cannot be null or empty.");
        }


        this.licensePlateNumber = licensePlateNumber;
        this.model = model;
        this.netWeight = netWeight;
        this.maxWeight = maxWeight;
        this.requiredLicenseType = requiredLicenseType;
    }

    // --- Getters --- (No setters as fields are final)

    /**
     * Gets the truck's unique license plate number.
     * @return The license plate number.
     */
    public String getLicensePlateNumber() {
        return licensePlateNumber;
    }

    /**
     * Gets the truck's model.
     * @return The model name/identifier.
     */
    public String getModel() {
        return model;
    }

    /**
     * Gets the net weight of the truck (without cargo).
     * @return The net weight.
     */
    public float getNetWeight() {
        return netWeight;
    }

    /**
     * Gets the maximum allowed weight the truck can carry (including cargo).
     * Note: This is the gross vehicle weight rating (GVWR) conceptually.
     * The maximum *payload* would be maxWeight - netWeight.
     * @return The maximum allowed weight.
     */
    public float getMaxWeight() {
        return maxWeight;
    }

    /**
     * Gets the type of driver's license required to operate this truck.
     * @return The required license type as a String.
     */
    public String getRequiredLicenseType() {
        return requiredLicenseType;
    }

    // --- Standard Methods ---

    /**
     * Returns a string representation of the Truck object.
     * @return A string representation including key attributes.
     */
    @Override
    public String toString() {
        return "Truck{" +
               "licensePlateNumber='" + licensePlateNumber + '\'' +
               ", model='" + model + '\'' +
               ", netWeight=" + netWeight +
               ", maxWeight=" + maxWeight +
               ", requiredLicenseType='" + requiredLicenseType + '\'' +
               '}';
    }

   /**
     * Checks if this Truck is equal to another object.
     * Equality is based solely on the licensePlateNumber.
     * @param o The object to compare with.
     * @return true if the objects are equal (same licensePlateNumber), false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Truck truck = (Truck) o;
        return licensePlateNumber.equals(truck.licensePlateNumber);
    }

   /**
     * Returns the hash code for this Truck.
     * Based solely on the licensePlateNumber.
     * @return The hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(licensePlateNumber);
    }
}