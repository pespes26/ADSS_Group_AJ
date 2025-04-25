package com.superli.deliveries.presentation;

/**
 * Holds driver details formatted specifically for presentation purposes.
 * Used as a data carrier (DTO/ViewModel) in the Presentation Layer.
 */
public class DriverDetailsView {

    private final String driverId;
    private final String name;
    private final String licenseType;

    /**
     * Constructs a DriverDetailsView object.
     *
     * @param driverId    Unique identifier of the driver.
     * @param name        Driver's name.
     * @param licenseType License type (e.g., C, C1).
     */
    public DriverDetailsView(String driverId, String name, String licenseType) {
        this.driverId = driverId;
        this.name = name;
        this.licenseType = licenseType;
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

    @Override
    public String toString() {
        return "DriverDetailsView{" +
                "driverId='" + driverId + '\'' +
                ", name='" + name + '\'' +
                ", licenseType='" + licenseType + '\'' +
                '}';
    }
}
