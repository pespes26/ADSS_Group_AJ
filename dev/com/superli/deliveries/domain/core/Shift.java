package com.superli.deliveries.domain.core;

import java.time.DayOfWeek;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a shift in the delivery system.
 */
public class Shift {
    private final String id;
    private final Date shiftDate;
    private final ShiftType shiftType;
    private final DayOfWeek shiftDay;
    private final Set<Role> shiftRequiredRoles;
    private final Map<Employee, Role> shiftEmployees;
    private final Employee shiftManager;
    private boolean isArchived;

    /**
     * Creates a new shift.
     *
     * @param shiftDate The date of the shift
     * @param shiftType The type of shift
     * @param shiftDay The day of the week
     * @param requiredRoles The roles required for this shift
     * @param manager The manager assigned to this shift
     * @throws IllegalArgumentException if any parameter is null
     */
    public Shift(Date shiftDate, ShiftType shiftType, DayOfWeek shiftDay,
                Set<Role> requiredRoles, Employee manager) {
        if (shiftDate == null) {
            throw new IllegalArgumentException("Shift date cannot be null");
        }
        if (shiftType == null) {
            throw new IllegalArgumentException("Shift type cannot be null");
        }
        if (shiftDay == null) {
            throw new IllegalArgumentException("Shift day cannot be null");
        }
        if (requiredRoles == null) {
            throw new IllegalArgumentException("Required roles cannot be null");
        }
        if (manager == null) {
            throw new IllegalArgumentException("Manager cannot be null");
        }

        this.id = UUID.randomUUID().toString();
        this.shiftDate = shiftDate;
        this.shiftType = shiftType;
        this.shiftDay = shiftDay;
        this.shiftRequiredRoles = new HashSet<>(requiredRoles);
        this.shiftEmployees = new HashMap<>();
        this.shiftManager = manager;
        this.isArchived = false;
    }

    /**
     * Gets the unique identifier of this shift.
     *
     * @return The shift ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the date of this shift.
     *
     * @return The shift date
     */
    public Date getShiftDate() {
        return shiftDate;
    }

    /**
     * Gets the type of this shift.
     *
     * @return The shift type
     */
    public ShiftType getShiftType() {
        return shiftType;
    }

    /**
     * Gets the day of the week for this shift.
     *
     * @return The shift day
     */
    public DayOfWeek getShiftDay() {
        return shiftDay;
    }

    /**
     * Gets the roles required for this shift.
     *
     * @return Set of required roles
     */
    public Set<Role> getShiftRequiredRoles() {
        return Collections.unmodifiableSet(shiftRequiredRoles);
    }

    /**
     * Gets the employees assigned to this shift and their roles.
     *
     * @return Map of employees to their assigned roles
     */
    public Map<Employee, Role> getShiftEmployees() {
        return Collections.unmodifiableMap(shiftEmployees);
    }

    /**
     * Gets the manager assigned to this shift.
     *
     * @return The shift manager
     */
    public Employee getShiftManager() {
        return shiftManager;
    }

    /**
     * Checks if this shift is archived.
     *
     * @return true if the shift is archived, false otherwise
     */
    public boolean isArchived() {
        return isArchived;
    }

    /**
     * Archives this shift.
     */
    public void archive() {
        this.isArchived = true;
    }

    /**
     * Adds an employee to this shift with the specified role.
     *
     * @param employee The employee to add
     * @param role The role to assign to the employee
     * @throws IllegalArgumentException if the employee or role is null, or if the role is not required
     */
    public void addEmployeeToShift(Employee employee, Role role) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        if (!shiftRequiredRoles.contains(role)) {
            throw new IllegalArgumentException("Role is not required for this shift");
        }

        shiftEmployees.put(employee, role);
        shiftRequiredRoles.remove(role);
    }

    /**
     * Removes an employee from this shift and restores their role to the required roles.
     *
     * @param employee The employee to remove
     * @throws IllegalArgumentException if the employee is null or not assigned to this shift
     */
    public void removeEmployeeFromShift(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }
        if (!shiftEmployees.containsKey(employee)) {
            throw new IllegalArgumentException("Employee is not assigned to this shift");
        }

        Role role = shiftEmployees.remove(employee);
        shiftRequiredRoles.add(role);
    }

    /**
     * Checks if an employee is assigned to this shift.
     *
     * @param employee The employee to check
     * @return true if the employee is assigned to this shift, false otherwise
     * @throws IllegalArgumentException if the employee is null
     */
    public boolean isEmployeeAssigned(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }
        return shiftEmployees.containsKey(employee);
    }

    /**
     * Checks if this shift is fully assigned (all required roles are filled).
     *
     * @return true if all required roles are filled, false otherwise
     */
    public boolean isShiftFullyAssigned() {
        return shiftRequiredRoles.isEmpty();
    }

    /**
     * Checks if this shift is in the past.
     *
     * @return true if the shift date is before the current date, false otherwise
     */
    public boolean isPastShift() {
        return shiftDate.before(new Date());
    }

    /**
     * Checks if an employee can be assigned to this shift.
     *
     * @param employee The employee to check
     * @return true if the employee can be assigned, false otherwise
     */
    public boolean canAssignEmployee(Employee employee) {
        if (isArchived || isShiftFullyAssigned() || isEmployeeAssigned(employee)) {
            return false;
        }

        return shiftRequiredRoles.stream()
                .anyMatch(role -> employee.isQualified(role) && employee.isAvailable(shiftDay, shiftType));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Shift)) return false;
        Shift shift = (Shift) o;
        return Objects.equals(id, shift.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Shift{id='%s', date=%s, type=%s, day=%s, requiredRoles=%s, employees=%s, manager=%s}",
                id, shiftDate, shiftType, shiftDay, shiftRequiredRoles, shiftEmployees, shiftManager);
    }
} 