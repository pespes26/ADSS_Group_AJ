package com.superli.deliveries.domain;

import java.util.Objects;

/**
 * Represents a Driver involved in transports.
 * Contains basic identity and availability information.
 * Matches the Driver class in the Domain Layer diagram.
 */
public class Driver {

    /** Unique identifier for the driver */
    private final String driverId;

    /** Driver's full name */
    private String name;

    /** License type (e.g., B, C, C1, C2, E) */
    private LicenseType licenseType;

    /** Availability status for new transports */
    private boolean available;

    /**
     * Constructs a new Driver object.
     *
     * @param driverId    Unique driver ID. Cannot be null or blank.
     * @param name        Driver's name. Cannot be null or blank.
     * @param licenseType Type of license. Cannot be null.
     * @param available   Whether the driver is available for assignment.
     */
    public Driver(String driverId, String name, LicenseType licenseType, boolean available) {
        if (driverId == null || driverId.trim().isEmpty()) {
            throw new IllegalArgumentException("Driver ID cannot be null or empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Driver name cannot be null or empty.");
        }
        if (licenseType == null) {
            throw new IllegalArgumentException("License type cannot be null.");
        }

        this.driverId = driverId;
        this.name = name;
        this.licenseType = licenseType;
        this.available = available;
    }

    public String getDriverId() {
        return driverId;
    }

    public String getName() {
        return name;
    }

    public LicenseType getLicenseType() {
        return licenseType;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Driver name cannot be null or empty.");
        }
        this.name = name;
    }

    public void setLicenseType(LicenseType licenseType) {
        if (licenseType == null) {
            throw new IllegalArgumentException("License type cannot be null.");
        }
        this.licenseType = licenseType;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== DRIVER DETAILS ===\n");
        sb.append("ID: ").append(driverId).append("\n");
        sb.append("Name: ").append(name).append("\n");
        sb.append("License Type: ").append(licenseType).append("\n");
        sb.append("Status: ").append(available ? "Available" : "Not Available").append("\n");
        sb.append("=====================");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Driver)) return false;
        Driver driver = (Driver) o;
        return driverId.equals(driver.driverId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(driverId);
    }
}