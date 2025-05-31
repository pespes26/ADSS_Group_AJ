package domain.core.employee;

import java.util.Date;
import java.util.List;

/**
 * Represents a driver in the company.
 */
public class Driver extends Employee {
    private final LicenseType licenseType;

    /**
     * Constructs a new Driver.
     *
     * @param id Unique identifier of the driver.
     * @param fullName Full name of the driver.
     * @param bankAccount Bank account number.
     * @param salary Driver's salary.
     * @param employeeTerms Employment terms.
     * @param employeeStartDate Start date of employment.
     * @param roleQualifications List of role qualifications.
     * @param availabilityConstraints List of available shifts.
     * @param loginRole Login role.
     * @param licenseType Type of driver's license.
     */
    public Driver(String id, String fullName, String bankAccount, double salary,
                 String employeeTerms, Date employeeStartDate,
                 List<Role> roleQualifications, List<AvailableShifts> availabilityConstraints,
                 Role loginRole, LicenseType licenseType) {
        super(id, fullName, bankAccount, salary, employeeTerms, employeeStartDate,
              roleQualifications, availabilityConstraints, loginRole);
        if (licenseType == null) {
            throw new IllegalArgumentException("License type cannot be null.");
        }
        this.licenseType = licenseType;
    }

    public LicenseType getLicenseType() {
        return licenseType;
    }

    @Override
    public String toString() {
        return "Driver{" +
                "id=" + getId() +
                ", fullName='" + getFullName() + '\'' +
                ", licenseType=" + licenseType +
                '}';
    }
} 