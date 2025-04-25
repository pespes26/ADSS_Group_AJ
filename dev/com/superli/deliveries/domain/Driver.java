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

    /** License type (e.g., "C", "C1") */
    private String licenseType;

    /** Availability status for new transports */
    private boolean available;

    /**
     * Constructs a new Driver object.
     *
     * @param driverId    Unique driver ID. Cannot be null or blank.
     * @param name        Driver's name. Cannot be null or blank.
     * @param licenseType Type of license. Can be null/empty.
     * @param available   Whether the driver is available for assignment.
     */
    public Driver(String driverId, String name, String licenseType, boolean available) {
        if (driverId == null || driverId.trim().isEmpty()) {
            throw new IllegalArgumentException("Driver ID cannot be null or empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Driver name cannot be null or empty.");
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

    public String getLicenseType() {
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

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "Driver{" +
                "driverId='" + driverId + '\'' +
                ", name='" + name + '\'' +
                ", licenseType='" + licenseType + '\'' +
                ", available=" + available +
                '}';
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
