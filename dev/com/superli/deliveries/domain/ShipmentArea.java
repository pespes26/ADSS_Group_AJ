package com.superli.deliveries.domain;

/**
 * Represents a geographic shipment area used for delivery planning.
 * Corresponds to the ShipmentArea class in the Domain Layer class diagram.
 */
public class ShipmentArea {

    // Attributes - Defined as final assuming area ID and name don't change once defined
    private final String areaId; // The unique identifier for the shipment area
    private final String name;   // The name of the shipment area

    /**
     * Constructs a new ShipmentArea object.
     *
     * @param areaId The unique identifier for the area. Cannot be null or empty.
     * @param name   The name of the area. Cannot be null or empty.
     * @throws IllegalArgumentException if areaId or name is null or empty.
     */
    public ShipmentArea(String areaId, String name) {
        // Basic validation
        if (areaId == null || areaId.trim().isEmpty()) {
            throw new IllegalArgumentException("Area ID cannot be null or empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Area name cannot be null or empty.");
        }

        this.areaId = areaId;
        this.name = name;
    }

    // --- Getters --- (No setters as fields are final)

    /**
     * Gets the unique identifier of the shipment area.
     * @return The area ID.
     */
    public String getAreaId() {
        return areaId;
    }

    /**
     * Gets the name of the shipment area.
     * @return The area name.
     */
    public String getName() {
        return name;
    }

    // --- Standard Methods ---

    /**
     * Returns a string representation of the ShipmentArea object.
     * @return A string representation including areaId and name.
     */
    @Override
    public String toString() {
        return "ShipmentArea{" +
               "areaId='" + areaId + '\'' +
               ", name='" + name + '\'' +
               '}';
    }

    /**
     * Checks if this ShipmentArea is equal to another object.
     * Equality is based solely on the areaId.
     * @param o The object to compare with.
     * @return true if the objects are equal (same areaId), false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShipmentArea that = (ShipmentArea) o;
        return areaId.equals(that.areaId);
    }

    /**
     * Returns the hash code for this ShipmentArea.
     * Based solely on the areaId.
     * @return The hash code value for this object.
     */
    @Override
    public int hashCode() {
        return areaId.hashCode();
    }
}