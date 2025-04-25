package com.superli.deliveries.domain;

import java.util.Objects;

/**
 * Represents a truck in the company's fleet.
 * Matches the Truck class in the Domain Layer class diagram.
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
        if (plateNum == null || plateNum.trim().isEmpty()) {
            throw new IllegalArgumentException("Plate number cannot be null or empty.");
        }
        if (model == null || model.trim().isEmpty()) {
            throw new IllegalArgumentException("Model cannot be null or empty.");
        }
        if (netWeight <= 0) {
            throw new IllegalArgumentException("Net weight must be positive.");
        }
        if (maxWeight < netWeight) {
            throw new IllegalArgumentException("Max weight must be greater than or equal to net weight.");
        }
        if (requiredLicenseType == null) {
            throw new IllegalArgumentException("Required license type cannot be null.");
        }

        this.plateNum = plateNum;
        this.model = model;
        this.netWeight = netWeight;
        this.maxWeight = maxWeight;
        this.requiredLicenseType = requiredLicenseType;
    }

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== TRUCK DETAILS ===\n");
        sb.append("License Plate: ").append(plateNum).append("\n");
        sb.append("Model: ").append(model).append("\n");
        sb.append("Weight: ").append(netWeight).append(" kg (net) / ")
                .append(maxWeight).append(" kg (max)\n");
        sb.append("Required License: ").append(requiredLicenseType).append("\n");
        sb.append("=====================");
        return sb.toString();
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