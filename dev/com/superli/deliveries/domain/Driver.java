package com.superli.deliveries.domain;

import java.util.Objects;

/**
 * Represents a Driver involved in transports.
 * Corresponds to the Driver class in the Domain Layer diagram.
 */
public class Driver {

    private final String driverId; // Unique ID (e.g., Teudat Zehut), cannot be changed after creation
    private String name;           // Driver's name
    private String licenseType;    // Driver's license type (e.g., "C1", "C", etc.)

    /**
     * Constructs a new Driver object.
     *
     * @param driverId    The unique identifier for the driver (e.g., Teudat Zehut). Cannot be null or empty.
     * @param name        The name of the driver. Cannot be null or empty.
     * @param licenseType The type of driving license the driver holds. Can be null or empty initially if not known.
     * @throws IllegalArgumentException if driverId or name is null or empty.
     */
    public Driver(String driverId, String name, String licenseType) {
        if (driverId == null || driverId.trim().isEmpty()) {
            throw new IllegalArgumentException("Driver ID cannot be null or empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Driver name cannot be null or empty.");
        }
        // licenseType can potentially be null or updated later
        this.driverId = driverId;
        this.name = name;
        this.licenseType = licenseType;
    }

    // --- Getters ---

    /**
     * Gets the unique identifier of the driver.
     * @return The driver ID (e.g., Teudat Zehut).
     */
    public String getDriverId() {
        return driverId;
    }

    /**
     * Gets the name of the driver.
     * @return The driver's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the driver's license type.
     * @return The license type as a String.
     */
    public String getLicenseType() {
        return licenseType;
    }

    // --- Setters --- (driverId is final)

    /**
     * Sets the name of the driver.
     * @param name The new name for the driver. Cannot be null or empty.
     * @throws IllegalArgumentException if name is null or empty.
     */
    public void setName(String name) {
         if (name == null || name.trim().isEmpty()) {
             throw new IllegalArgumentException("Driver name cannot be null or empty.");
         }
         this.name = name;
    }

    /**
     * Sets the driver's license type.
     * @param licenseType The new license type for the driver.
     */
    public void setLicenseType(String licenseType) {
        // Optional: Add validation for known license types if applicable
        this.licenseType = licenseType;
    }

    // --- Standard Methods ---

    /**
     * Returns a string representation of the Driver object.
     * @return A string representation including driverId, name, and licenseType.
     */
    @Override
    public String toString() {
        return "Driver{" +
               "driverId='" + driverId + '\'' +
               ", name='" + name + '\'' +
               ", licenseType='" + licenseType + '\'' +
               '}';
    }

   /**
     * Checks if this Driver is equal to another object.
     * Equality is based solely on the driverId.
     * @param o The object to compare with.
     * @return true if the objects are equal (same driverId), false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Driver driver = (Driver) o;
        return driverId.equals(driver.driverId);
    }

   /**
     * Returns the hash code for this Driver.
     * Based solely on the driverId.
     * @return The hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(driverId); // Using Objects.hash is slightly safer than direct call
    }
}