package application.services;

import com.superli.deliveries.domain.enums.employee.*;
import com.superli.deliveries.domain.ports.employee.IEmployeeRepository;

import java.time.DayOfWeek;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service class for managing employees in the system.
 */
public class EmployeeService {
    private final IEmployeeRepository employeeRepository;

    public EmployeeService(IEmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    /**
     * Creates a new employee.
     *
     * @param id Employee ID
     * @param fullName Employee's full name
     * @param bankAccount Employee's bank account
     * @param salary Employee's salary
     * @param employeeTerms Employment terms
     * @param employeeStartDate Start date of employment
     * @param roleQualifications List of roles the employee is qualified for
     * @param availabilityConstraints List of available shifts
     * @param type Type of employee (DRIVER or STAFF)
     * @param licenseType License type (only for DRIVER type)
     * @return The created employee
     */
    public Employee createEmployee(String id, String fullName, String bankAccount, double salary,
                                 String employeeTerms, Date employeeStartDate,
                                 List<Role> roleQualifications, List<AvailableShifts> availabilityConstraints,
                                 EmployeeType type, LicenseType licenseType) {
        Employee employee = new Employee(id, fullName, bankAccount, salary, employeeTerms,
                employeeStartDate, roleQualifications, availabilityConstraints, type, licenseType);
        return employeeRepository.save(employee);
    }

    /**
     * Finds an employee by their ID.
     *
     * @param id The employee ID
     * @return Optional containing the employee if found
     */
    public Optional<Employee> findEmployeeById(String id) {
        return employeeRepository.findById(id);
    }

    /**
     * Finds all employees in the system.
     *
     * @return List of all employees
     */
    public List<Employee> findAllEmployees() {
        return employeeRepository.findAll();
    }

    /**
     * Finds all employees of a specific type.
     *
     * @param type The employee type to filter by
     * @return List of employees of the specified type
     */
    public List<Employee> findEmployeesByType(EmployeeType type) {
        return employeeRepository.findByType(type);
    }

    /**
     * Finds all employees with a specific license type.
     *
     * @param licenseType The license type to filter by
     * @return List of employees with the specified license type
     */
    public List<Employee> findEmployeesByLicenseType(LicenseType licenseType) {
        return employeeRepository.findByLicenseType(licenseType);
    }

    /**
     * Finds all employees qualified for a specific role.
     *
     * @param role The role to filter by
     * @return List of employees qualified for the specified role
     */
    public List<Employee> findEmployeesByRole(Role role) {
        return employeeRepository.findByRole(role);
    }

    /**
     * Finds all available employees.
     *
     * @return List of available employees
     */
    public List<Employee> findAvailableEmployees() {
        return employeeRepository.findAvailable();
    }

    /**
     * Updates an employee's information.
     *
     * @param employee The employee with updated information
     * @return The updated employee
     */
    public Employee updateEmployee(Employee employee) {
        return employeeRepository.update(employee);
    }

    /**
     * Deletes an employee from the system.
     *
     * @param id The ID of the employee to delete
     * @return true if the employee was deleted, false otherwise
     */
    public boolean deleteEmployee(String id) {
        return employeeRepository.delete(id);
    }

    /**
     * Checks if an employee is available for a specific day and shift.
     *
     * @param employeeId The employee ID
     * @param day The day of the week
     * @param shiftType The shift type
     * @return true if the employee is available, false otherwise
     */
    public boolean isEmployeeAvailable(String employeeId, DayOfWeek day, ShiftType shiftType) {
        return employeeRepository.findById(employeeId)
                .map(employee -> employee.isAvailable(day, shiftType))
                .orElse(false);
    }

    /**
     * Updates an employee's availability status.
     *
     * @param employeeId The employee ID
     * @param available The new availability status
     * @return The updated employee
     */
    public Employee updateAvailability(String employeeId, boolean available) {
        return employeeRepository.findById(employeeId)
                .map(employee -> {
                    employee.setAvailable(available);
                    return employeeRepository.update(employee);
                })
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
    }

    /**
     * Adds a new employee to the system.
     *
     * @param employee The employee to add
     * @throws IllegalArgumentException if the employee is invalid
     */
    public void addEmployee(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }
        employeeRepository.save(employee);
    }

    /**
     * Removes an employee from the system.
     *
     * @param employee The employee to remove
     * @throws IllegalArgumentException if the employee is null
     */
    public void removeEmployee(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }
        employeeRepository.delete(employee.getId());
    }

    /**
     * Finds a role by its name.
     *
     * @param roleName The name of the role
     * @return The role if found, null otherwise
     */
    public Role findRoleByName(String roleName) {
        return employeeRepository.findRoleByName(roleName).orElse(null);
    }

    /**
     * Adds a role to an employee.
     *
     * @param employee The employee to add the role to
     * @param role The role to add
     * @return true if the role was added, false if the employee already has the role
     * @throws IllegalArgumentException if either parameter is null
     */
    public boolean addRoleToEmployee(Employee employee, Role role) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }

        if (employee.getRoleQualifications().contains(role)) {
            return false;
        }

        employee.addRoleQualification(role);
        employeeRepository.update(employee);
        return true;
    }

    /**
     * Finds all roles in the system.
     *
     * @return List of all roles
     */
    public List<Role> findAllRoles() {
        return employeeRepository.findAllRoles();
    }

    /**
     * Finds all archived employees.
     *
     * @return List of archived employees
     */
    public List<Employee> findArchived() {
        return employeeRepository.findArchived();
    }

    /**
     * Clears all availability constraints for an employee.
     *
     * @param employee The employee to clear constraints for
     * @throws IllegalArgumentException if the employee is null
     */
    public void clearAvailability(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }
        employee.clearAvailability();
        employeeRepository.update(employee);
    }

    /**
     * Checks if an employee has a specific availability constraint.
     *
     * @param employee The employee to check
     * @param day The day to check
     * @param shift The shift type to check
     * @return true if the employee has the constraint, false otherwise
     * @throws IllegalArgumentException if any parameter is null
     */
    public boolean hasAvailabilityConstraint(Employee employee, DayOfWeek day, ShiftType shift) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }
        if (day == null) {
            throw new IllegalArgumentException("Day cannot be null");
        }
        if (shift == null) {
            throw new IllegalArgumentException("Shift cannot be null");
        }

        return employee.getAvailabilityConstraints().stream()
                .anyMatch(a -> a.getDay() == day && a.getShift() == shift);
    }

    /**
     * Adds an availability constraint to an employee.
     *
     * @param employee The employee to add the constraint to
     * @param day The day to add
     * @param shift The shift type to add
     * @throws IllegalArgumentException if any parameter is null
     */
    public void addAvailabilityConstraint(Employee employee, DayOfWeek day, ShiftType shift) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }
        if (day == null) {
            throw new IllegalArgumentException("Day cannot be null");
        }
        if (shift == null) {
            throw new IllegalArgumentException("Shift cannot be null");
        }

        employee.addAvailability(new AvailableShifts(day, shift));
        employeeRepository.update(employee);
    }

    /**
     * Removes an availability constraint from an employee.
     *
     * @param employee The employee to remove the constraint from
     * @param day The day to remove
     * @param shift The shift type to remove
     * @return true if the constraint was removed, false if it didn't exist
     * @throws IllegalArgumentException if any parameter is null
     */
    public boolean removeAvailabilityConstraint(Employee employee, DayOfWeek day, ShiftType shift) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }
        if (day == null) {
            throw new IllegalArgumentException("Day cannot be null");
        }
        if (shift == null) {
            throw new IllegalArgumentException("Shift cannot be null");
        }

        boolean removed = employee.getAvailabilityConstraints().removeIf(a -> 
            a.getDay() == day && a.getShift() == shift);
        
        if (removed) {
            employeeRepository.update(employee);
        }
        return removed;
    }
} 