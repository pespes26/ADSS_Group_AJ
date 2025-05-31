package com.superli.deliveries.domain;

import java.util.Objects;

/**
 * Represents a truck in the company's fleet.
 * Immutable class with fixed core attributes and mutable availability.
 */
public class Truck {

    /** Unique identifier for the truck (license plate number) */
    private final String plateNum;

    /** Model name or code of the truck */
    private final String model;

    /** Net weight of the truck (without cargo) */
    private final float netWeight;

    /** Maximum allowed weight (gross vehicle weight) */
    private final float maxWeight;

    /** Required license type to operate this truck */
    private final LicenseType requiredLicenseType;

    /** Availability status for new transports - mutable */
    private boolean available;

    /**
     * Constructs a new Truck object.
     *
     * @param plateNum Unique license plate. Cannot be null or empty.
     * @param model Truck model. Cannot be null or empty.
     * @param netWeight Truck's net weight. Must be > 0.
     * @param maxWeight Truck's max weight. Must be >= netWeight.
     * @param requiredLicenseType Required license type. Cannot be null.
     */
    public Truck(String plateNum, String model, float netWeight, float maxWeight, LicenseType requiredLicenseType) {
        this(plateNum, model, netWeight, maxWeight, requiredLicenseType, true);
    }

    /**
     * Constructs a new Truck object with specified availability.
     *
     * @param plateNum Unique license plate. Cannot be null or empty.
     * @param model Truck model. Cannot be null or empty.
     * @param netWeight Truck's net weight. Must be > 0.
     * @param maxWeight Truck's max weight. Must be >= netWeight.
     * @param requiredLicenseType Required license type. Cannot be null.
     * @param available Initial availability status of the truck.
     */
    public Truck(String plateNum, String model, float netWeight, float maxWeight,
                 LicenseType requiredLicenseType, boolean available) {
        // Validate plate number
        if (plateNum == null || plateNum.trim().isEmpty()) {
            throw new IllegalArgumentException("Plate number cannot be null or empty.");
        }

        // Validate model
        if (model == null || model.trim().isEmpty()) {
            throw new IllegalArgumentException("Model cannot be null or empty.");
        }

        // Validate net weight
        if (netWeight <= 0) {
            throw new IllegalArgumentException("Net weight must be positive.");
        }

        // Validate max weight
        if (maxWeight < netWeight) {
            throw new IllegalArgumentException("Max weight must be greater than or equal to net weight.");
        }

        // Validate license type
        if (requiredLicenseType == null) {
            throw new IllegalArgumentException("Required license type cannot be null.");
        }

        this.plateNum = plateNum;
        this.model = model;
        this.netWeight = netWeight;
        this.maxWeight = maxWeight;
        this.requiredLicenseType = requiredLicenseType;
        this.available = available;
    }

    // Getters for core attributes
    public String getPlateNum() {
        return plateNum;
    }

    public String getModel() {
        return model;
    }

    public float getNetWeight() {
        return netWeight;
    }

    public float getMaxWeight() {
        return maxWeight;
    }

    public LicenseType getRequiredLicenseType() {
        return requiredLicenseType;
    }

    /**
     * Checks if the truck is available for assignments.
     *
     * @return true if the truck is available, false otherwise.
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * Sets the availability status of the truck.
     *
     * @param available true to mark the truck as available, false otherwise.
     */
    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "Truck{" +
                "plateNum='" + plateNum + '\'' +
                ", model='" + model + '\'' +
                ", netWeight=" + netWeight +
                ", maxWeight=" + maxWeight +
                ", requiredLicenseType=" + requiredLicenseType +
                ", available=" + available +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Truck)) return false;
        Truck truck = (Truck) o;
        return plateNum.equals(truck.plateNum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plateNum);
    }
}