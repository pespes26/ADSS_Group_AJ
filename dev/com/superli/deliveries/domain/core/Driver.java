package com.superli.deliveries.domain.core;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Represents a Driver involved in transports.
 * Contains basic identity and availability information.
 * Inherits from Employee and adds driver-specific properties.
 */
public class Driver extends Employee {

    /** License type (e.g., B, C, C1, C2, E) */
    private LicenseType licenseType;

    /** Availability status for new transports */
    private boolean available;

    /**
     * Constructs a new Driver object.
     */
    public Driver(String id, String fullName, String bankAccount, double salary, int siteId, String employeeTerms,
                  Date employeeStartDate, List<Role> roleQualifications, List<AvailableShifts> availabilityConstraints, Role loginRole, LicenseType licenseType) {
        super(id, fullName, bankAccount, salary, -1, employeeTerms, employeeStartDate, roleQualifications, availabilityConstraints, loginRole);
        Role r = new Role("Driver");
        this.addRoleQualification(r);
        if (getRoleQualifications() == null) {
            setRoleQualifications(new ArrayList<>());
        }
        if (licenseType == null) {
            throw new IllegalArgumentException("License type cannot be null.");
        }
        this.licenseType = licenseType;
        this.available = true; // Initialize available to true by default
    }

    /**
     * Gets the driver ID (inherited from Employee as employeeId).
     * @return The driver's unique identifier.
     */
    public String getDriverId() {
        return getId();
    }

    /**
     * Gets the license type.
     * @return The driver's license type.
     */
    public LicenseType getLicenseType() {
        return licenseType;
    }

    /**
     * Checks if driver is available.
     * @return true if available, false otherwise.
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * Sets the license type.
     * @param licenseType The new license type. Cannot be null.
     */
    public void setLicenseType(LicenseType licenseType) {
        if (licenseType == null) {
            throw new IllegalArgumentException("License type cannot be null.");
        }
        this.licenseType = licenseType;
    }

    /**
     * Sets availability status.
     * @param available The new availability status.
     */
    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== DRIVER DETAILS ===\n");
        sb.append("ID: ").append(getId()).append("\n");
        sb.append("Name: ").append(getFullName()).append("\n");
        sb.append("License Type: ").append(licenseType).append("\n");
        sb.append("Status: ").append(available ? "Available" : "Not Available").append("\n");
        sb.append("=====================");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Driver)) return false;
        if (!super.equals(o)) return false;
        Driver driver = (Driver) o;
        return getId().equals(driver.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}


//package com.superli.deliveries.domain.core;
//
//import com.superli.deliveries.domain.core.Employee;
//import com.superli.deliveries.domain.core.Role;
//
//import java.util.Date;
//import java.util.List;
//import java.util.Objects;
//
///**
// * Represents a Driver involved in transports.
// * Contains basic identity and availability information.
// * Inherits from Employee and adds driver-specific properties.
// */
//public class Driver extends Employee {
//
//    /** License type (e.g., B, C, C1, C2, E) */
//    private LicenseType licenseType;
//
//    /** Availability status for new transports */
//    private boolean available;
//
//    /**
//     * Constructs a new Driver object.
//     */
//    public Driver(String id, String fullName, String bankAccount, double salary, String employeeTerms,
//                  Date employeeStartDate, List<Role> roleQualifications, List<AvailableShifts> availabilityConstraints, Role loginRole) {
//        super(id, fullName, bankAccount, salary, employeeTerms, employeeStartDate, roleQualifications, availabilityConstraints, loginRole); // Call parent constructor
//        this.addRoleQualification((Role)"Driver");
//        if (licenseType == null) {
//            throw new IllegalArgumentException("License type cannot be null.");
//        }
//        this.licenseType = licenseType;
//    }
//
//    public Driver(String id, String name, LicenseType license, boolean available) {
//        super();
//    }
//
//    /**
//     * Gets the driver ID (inherited from Employee as employeeId).
//     * @return The driver's unique identifier.
//     */
//    public String getDriverId() {
//        return getId(); // Use inherited method
//    }
//
//    /**
//     * Gets the license type.
//     * @return The driver's license type.
//     */
//    public LicenseType getLicenseType() {
//        return licenseType;
//    }
//
//    /**
//     * Checks if driver is available.
//     * @return true if available, false otherwise.
//     */
//    public boolean isAvailable() {
//        return available;
//    }
//
//    /**
//     * Sets the license type.
//     * @param licenseType The new license type. Cannot be null.
//     */
//    public void setLicenseType(LicenseType licenseType) {
//        if (licenseType == null) {
//            throw new IllegalArgumentException("License type cannot be null.");
//        }
//        this.licenseType = licenseType;
//    }
//
//    /**
//     * Sets availability status.
//     * @param available The new availability status.
//     */
//    public void setAvailable(boolean available) {
//        this.available = available;
//    }
//
//    @Override
//    public String toString() {
//        StringBuilder sb = new StringBuilder();
//        sb.append("=== DRIVER DETAILS ===\n");
//        sb.append("ID: ").append(getId()).append("\n");
//        sb.append("Name: ").append(getFullName()).append("\n");
//        sb.append("License Type: ").append(licenseType).append("\n");
//        sb.append("Status: ").append(available ? "Available" : "Not Available").append("\n");
//        sb.append("=====================");
//        return sb.toString();
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof Driver)) return false;
//        if (!super.equals(o)) return false; // Call parent equals
//        Driver driver = (Driver) o;
//        return getId().equals(driver.getId());
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(getId());
//    }
//}