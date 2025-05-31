package domain.core;

import java.util.ArrayList;
import java.util.List;
import domain.core.Employee;

/**
 * Manages human resources in the system, including employees, shifts, and roles.
 */
public class HRManager {
    private final List<Employee> employees;
    private final List<Shift> shifts;
    private final List<Role> allRoles;
    private final ArchivedEmployee archivedEmployee;
    private final ArchivedShifts archivedShifts;

    /**
     * Constructs a new HRManager with empty lists.
     */
    public HRManager() {
        this.employees = new ArrayList<>();
        this.shifts = new ArrayList<>();
        this.allRoles = new ArrayList<>();
        this.archivedEmployee = new ArchivedEmployee();
        this.archivedShifts = new ArchivedShifts();
    }

    /**
     * Gets all roles in the system.
     *
     * @return A copy of the list of all roles.
     */
    public List<Role> getAllRoles() {
        return List.copyOf(allRoles);
    }

    /**
     * Gets all shifts in the system.
     *
     * @return A copy of the list of all shifts.
     */
    public List<Shift> getAllShifts() {
        return List.copyOf(shifts);
    }

    /**
     * Gets all employees in the system.
     *
     * @return A copy of the list of all employees.
     */
    public List<Employee> getEmployees() {
        return List.copyOf(employees);
    }

    /**
     * Gets the archived employee manager.
     *
     * @return The archived employee manager.
     */
    public ArchivedEmployee getArchivedEmployee() {
        return archivedEmployee;
    }

    /**
     * Gets the archived shifts manager.
     *
     * @return The archived shifts manager.
     */
    public ArchivedShifts getArchivedShifts() {
        return archivedShifts;
    }

    // ----------------------------------- Employee Management -----------------------------------
    /**
     * Adds a new employee to the system.
     *
     * @param employee The employee to add.
     * @return true if the employee was added successfully, false if an employee with the same ID already exists.
     */
    public boolean addEmployee(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null.");
        }
        if (findEmployeeByID(employee.getId()) != null) {
            System.out.println("Employee with this ID already exists. Cannot add duplicate.");
            return false;
        }
        employees.add(employee);
        return true;
    }

    /**
     * Removes an employee from the system.
     *
     * @param employee The employee to remove.
     * @return true if the employee was removed, false if they weren't in the system.
     */
    public boolean removeEmployee(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null.");
        }
        return employees.remove(employee);
    }

    /**
     * Finds an employee by their ID.
     *
     * @param id The ID to search for.
     * @return The employee with the given ID, or null if not found.
     */
    public Employee findEmployeeByID(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null or empty.");
        }
        for (Employee e : employees) {
            if (e.getId().equals(id)) {
                return e;
            }
        }
        return null;
    }

    /**
     * Removes an employee from the active list and archives them.
     *
     * @param employee The employee to archive.
     * @return true if the employee was archived successfully, false if they weren't in the system.
     */
    public boolean removeAndArchiveEmployee(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null.");
        }

        Employee toRemove = findEmployeeByID(employee.getId());
        if (toRemove == null) {
            System.out.println("Employee with ID " + employee.getId() + " not found.");
            return false;
        }

        employees.remove(toRemove);
        archivedEmployee.addArchivedEmployee(toRemove);
        System.out.println("Employee with ID " + employee.getId() + " has been removed and archived.");
        return true;
    }

    // ----------------------------------- Role and Shift Management -----------------------------------
    /**
     * Adds a new role to the system.
     *
     * @param role The role to add.
     * @return true if the role was added, false if it already exists.
     */
    public boolean addRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null.");
        }
        if (!allRoles.contains(role)) {
            allRoles.add(role);
            return true;
        }
        return false;
    }

    /**
     * Adds a new shift to the system.
     *
     * @param shift The shift to add.
     * @return true if the shift was added, false if it already exists.
     */
    public boolean addShift(Shift shift) {
        if (shift == null) {
            throw new IllegalArgumentException("Shift cannot be null.");
        }
        if (!shifts.contains(shift)) {
            shifts.add(shift);
            return true;
        }
        return false;
    }

    /**
     * Sets the required roles for a shift.
     *
     * @param shift The shift to update.
     * @param requiredRoles The list of required roles.
     */
    public void defineRequiredRolesForShift(Shift shift, List<Role> requiredRoles) {
        if (shift == null) {
            throw new IllegalArgumentException("Shift cannot be null.");
        }
        if (requiredRoles == null) {
            throw new IllegalArgumentException("Required roles cannot be null.");
        }
        shift.getShiftRequiredRoles().clear();
        shift.getShiftRequiredRoles().addAll(requiredRoles);
    }

    /**
     * Adds a required role to a shift.
     *
     * @param shift The shift to update.
     * @param role The role to add.
     * @return true if the role was added, false if it was already required.
     */
    public boolean addRequiredRoleForShift(Shift shift, Role role) {
        if (shift == null) {
            throw new IllegalArgumentException("Shift cannot be null.");
        }
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null.");
        }
        if (!shift.getShiftRequiredRoles().contains(role)) {
            shift.getShiftRequiredRoles().add(role);
            return true;
        }
        return false;
    }

    // ----------------------------------- Shift Assignment -----------------------------------
    /**
     * Assigns an employee to a shift.
     *
     * @param shift The shift to assign to.
     * @param employee The employee to assign.
     * @return true if the employee was assigned successfully, false otherwise.
     */
    public boolean addEmployeeToShift(Shift shift, Employee employee) {
        if (shift == null) {
            throw new IllegalArgumentException("Shift cannot be null.");
        }
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null.");
        }

        for (Role role : shift.getShiftRequiredRoles()) {
            if (employee.isQualified(role) && employee.isAvailable(shift.getShiftDay(), shift.getShiftType())) {
                return shift.addEmployeeToShift(employee, role);
            }
        }
        return false;
    }

    /**
     * Removes an employee from a shift.
     *
     * @param shift The shift to remove from.
     * @param employee The employee to remove.
     * @return true if the employee was removed successfully, false if they weren't assigned to the shift.
     */
    public boolean removeEmployeeFromShift(Shift shift, Employee employee) {
        if (shift == null) {
            throw new IllegalArgumentException("Shift cannot be null.");
        }
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null.");
        }
        return shift.removeEmployeeFromShift(employee);
    }

    /**
     * Prints information about all employees to the console.
     */
    public void printEmployees() {
        for (Employee e : employees) {
            System.out.println("Employee full name:" + e.getFullName() + ", Employee ID: " + e.getId());
        }
    }

    /**
     * Prints information about all roles to the console.
     */
    public void printRoles() {
        System.out.print("Roles: [");
        for (int i = 0; i < allRoles.size(); i++) {
            if (i < allRoles.size() - 1) {
                System.out.print(allRoles.get(i).getRoleName() + ", ");
            } else {
                System.out.print(allRoles.get(i).getRoleName());
            }
        }
        System.out.println("]");
    }

    @Override
    public String toString() {
        return "HRManager{" +
                "employees=" + employees +
                ", shifts=" + shifts +
                ", allRoles=" + allRoles +
                ", archivedEmployee=" + archivedEmployee +
                ", archivedShifts=" + archivedShifts +
                '}';
    }
}
