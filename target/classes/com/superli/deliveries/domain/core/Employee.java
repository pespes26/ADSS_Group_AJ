package domain.core;

import java.time.DayOfWeek;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Represents an employee in the system, which can be either a regular employee or a driver.
 * This class unifies the functionality of both Employee and Driver classes.
 */
public class Employee {
    private String id;
    private String fullName;
    private String bankAccount;
    private double salary;
    private List<Role> roleQualifications;
    private String employeeTerms;
    private Date employeeStartDate;
    private List<AvailableShifts> availabilityConstraints;
    private EmployeeType type;
    private LicenseType licenseType;  // Only for DRIVER type
    private boolean available;

    /**
     * Constructs a new Employee object.
     *
     * @param id Unique employee ID
     * @param fullName Employee's full name
     * @param bankAccount Employee's bank account
     * @param salary Employee's salary
     * @param employeeTerms Employment terms
     * @param employeeStartDate Start date of employment
     * @param roleQualifications List of roles the employee is qualified for
     * @param availabilityConstraints List of available shifts
     * @param type Type of employee (DRIVER or STAFF)
     * @param licenseType License type (only for DRIVER type)
     */
    public Employee(String id, String fullName, String bankAccount, double salary,
                   String employeeTerms, Date employeeStartDate,
                   List<Role> roleQualifications, List<AvailableShifts> availabilityConstraints,
                   EmployeeType type, LicenseType licenseType) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee ID cannot be null or empty");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee name cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Employee type cannot be null");
        }
        if (type == EmployeeType.DRIVER && licenseType == null) {
            throw new IllegalArgumentException("License type is required for drivers");
        }

        this.id = id;
        this.fullName = fullName;
        this.bankAccount = bankAccount;
        this.salary = salary;
        this.employeeTerms = employeeTerms;
        this.employeeStartDate = employeeStartDate;
        this.roleQualifications = roleQualifications;
        this.availabilityConstraints = availabilityConstraints;
        this.type = type;
        this.licenseType = licenseType;
        this.available = true;
    }

    // Getters
    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public String getBankAccount() { return bankAccount; }
    public double getSalary() { return salary; }
    public String getEmployeeTerms() { return employeeTerms; }
    public Date getEmployeeStartDate() { return employeeStartDate; }
    public List<Role> getRoleQualifications() { return roleQualifications; }
    public List<AvailableShifts> getAvailabilityConstraints() { return availabilityConstraints; }
    public EmployeeType getType() { return type; }
    public LicenseType getLicenseType() { return licenseType; }
    public boolean isAvailable() { return available; }

    // Setters
    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee name cannot be null or empty");
        }
        this.fullName = fullName;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public void setRoleQualifications(List<Role> roleQualifications) {
        this.roleQualifications = roleQualifications;
    }

    public void setLicenseType(LicenseType licenseType) {
        if (type != EmployeeType.DRIVER) {
            throw new IllegalStateException("Only drivers can have license types");
        }
        if (licenseType == null) {
            throw new IllegalArgumentException("License type cannot be null");
        }
        this.licenseType = licenseType;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    /**
     * Checks if the employee is available to work on a given day and shift type.
     */
    public boolean isAvailable(DayOfWeek day, ShiftType shiftType) {
        for (AvailableShifts option : availabilityConstraints) {
            if (option.getDay().equals(day) && option.getShift() == shiftType) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the employee is qualified for a given role.
     */
    public boolean isQualified(Role role) {
        return roleQualifications.contains(role);
    }

    /**
     * Adds an availability constraint for the employee.
     */
    public void addAvailability(AvailableShifts availability) {
        if (!availabilityConstraints.contains(availability)) {
            availabilityConstraints.add(availability);
        }
    }

    /**
     * Clears all availability constraints for the employee.
     */
    public void clearAvailability() {
        availabilityConstraints.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        Employee employee = (Employee) o;
        return id.equals(employee.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== EMPLOYEE DETAILS ===\n");
        sb.append("ID: ").append(id).append("\n");
        sb.append("Name: ").append(fullName).append("\n");
        sb.append("Type: ").append(type).append("\n");
        if (type == EmployeeType.DRIVER) {
            sb.append("License Type: ").append(licenseType).append("\n");
        }
        sb.append("Status: ").append(available ? "Available" : "Not Available").append("\n");
        sb.append("=====================");
        return sb.toString();
    }
} 