package com.superli.deliveries.application.services;

import com.superli.deliveries.domain.core.Employee;
import com.superli.deliveries.domain.core.Role;
import com.superli.deliveries.domain.core.Shift;
import com.superli.deliveries.domain.core.ShiftType;
import com.superli.deliveries.domain.ports.IShiftRepository;

import java.time.DayOfWeek;
import java.util.*;

/**
 * Service class that provides business logic for managing shifts.
 */
public class ShiftService {
    private final IShiftRepository shiftRepository;

    public ShiftService(IShiftRepository shiftRepository) {
        this.shiftRepository = shiftRepository;
    }

    /**
     * Creates a new shift.
     *
     * @param shiftDate The date of the shift
     * @param shiftType The type of shift
     * @param shiftDay The day of the week
     * @param requiredRoles The roles required for this shift
     * @param manager The manager assigned to this shift
     * @return The created shift
     * @throws IllegalArgumentException if any parameter is null
     */
    public Shift createShift(Date shiftDate, ShiftType shiftType, DayOfWeek shiftDay,
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

        Shift shift = new Shift(shiftDate, shiftType, shiftDay, requiredRoles, manager);
        return shiftRepository.save(shift);
    }

    /**
     * Finds a shift by its ID.
     *
     * @param id The shift ID
     * @return The shift if found, null otherwise
     */
    public Shift findById(String id) {
        return shiftRepository.findById(id).orElse(null);
    }

    /**
     * Finds all shifts in the system.
     *
     * @return List of all shifts
     */
    public List<Shift> findAll() {
        return shiftRepository.findAll();
    }

    /**
     * Finds all shifts of a specific type.
     *
     * @param type The shift type to filter by
     * @return List of shifts of the specified type
     */
    public List<Shift> findByType(ShiftType type) {
        return shiftRepository.findByType(type);
    }

    /**
     * Finds all shifts on a specific day.
     *
     * @param day The day to filter by
     * @return List of shifts on the specified day
     */
    public List<Shift> findByDay(DayOfWeek day) {
        return shiftRepository.findByDay(day);
    }

    /**
     * Finds all shifts between two dates.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return List of shifts between the specified dates
     */
    public List<Shift> findByDateRange(Date startDate, Date endDate) {
        return shiftRepository.findByDateRange(startDate, endDate);
    }

    /**
     * Finds all shifts that require a specific role.
     *
     * @param role The role to filter by
     * @return List of shifts requiring the specified role
     */
    public List<Shift> findByRequiredRole(Role role) {
        return shiftRepository.findByRequiredRole(role);
    }

    /**
     * Finds all shifts where an employee is assigned.
     *
     * @param employee The employee to filter by
     * @return List of shifts where the employee is assigned
     */
    public List<Shift> findEmployeeShifts(Employee employee) {
        return shiftRepository.findByAssignedEmployee(employee);
    }

    /**
     * Finds all shifts managed by a specific employee.
     *
     * @param manager The manager to filter by
     * @return List of shifts managed by the specified employee
     */
    public List<Shift> findManagerShifts(Employee manager) {
        return shiftRepository.findByManager(manager);
    }

    /**
     * Finds all archived shifts.
     *
     * @return List of archived shifts
     */
    public List<Shift> findArchived() {
        return shiftRepository.findArchived();
    }

    /**
     * Finds all active (non-archived) shifts.
     *
     * @return List of active shifts
     */
    public List<Shift> findActive() {
        return shiftRepository.findActive();
    }

    /**
     * Assigns an employee to a shift.
     *
     * @param shiftId The ID of the shift
     * @param employee The employee to assign
     * @return true if the employee was assigned successfully, false otherwise
     * @throws IllegalArgumentException if the shift is not found or the employee cannot be assigned
     */
    public boolean assignEmployee(String shiftId, Employee employee) {
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new IllegalArgumentException("Shift not found"));

        if (shift.isArchived()) {
            throw new IllegalArgumentException("Cannot assign employee to archived shift");
        }

        if (shift.isShiftFullyAssigned()) {
            throw new IllegalArgumentException("Shift is already fully assigned");
        }

        if (!shift.canAssignEmployee(employee)) {
            throw new IllegalArgumentException("Employee cannot be assigned to this shift");
        }

        Role role = shift.getShiftRequiredRoles().iterator().next();
        shift.addEmployeeToShift(employee, role);
        shiftRepository.update(shift);
        return true;
    }

    /**
     * Removes an employee from a shift.
     *
     * @param shiftId The ID of the shift
     * @param employee The employee to remove
     * @return true if the employee was removed successfully, false otherwise
     * @throws IllegalArgumentException if the shift is not found
     */
    public boolean removeEmployee(String shiftId, Employee employee) {
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new IllegalArgumentException("Shift not found"));

        if (shift.isArchived()) {
            throw new IllegalArgumentException("Cannot remove employee from archived shift");
        }

        if (!shift.isEmployeeAssigned(employee)) {
            return false;
        }

        shift.removeEmployeeFromShift(employee);
        shiftRepository.update(shift);
        return true;
    }

    /**
     * Updates the required roles for a shift.
     *
     * @param shift The shift to update
     * @param requiredRoles The new required roles
     * @throws IllegalArgumentException if either parameter is null
     */
    public void updateRequiredRoles(Shift shift, Set<Role> requiredRoles) {
        if (shift == null) {
            throw new IllegalArgumentException("Shift cannot be null");
        }
        if (requiredRoles == null) {
            throw new IllegalArgumentException("Required roles cannot be null");
        }

        // Create a new shift with the updated roles
        Shift updatedShift = new Shift(
            shift.getShiftDate(),
            shift.getShiftType(),
            shift.getShiftDay(),
            requiredRoles,
            shift.getShiftManager()
        );
        shiftRepository.update(updatedShift);
    }

    /**
     * Archives a shift.
     *
     * @param shiftId The ID of the shift to archive
     * @return The archived shift
     * @throws IllegalArgumentException if the shift is not found
     */
    public Shift archiveShift(String shiftId) {
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new IllegalArgumentException("Shift not found"));

        shift.archive();
        return shiftRepository.update(shift);
    }
} 