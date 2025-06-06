package com.superli.deliveries.application.services;

import com.superli.deliveries.domain.core.Shift;
import com.superli.deliveries.domain.core.Employee;
import com.superli.deliveries.domain.core.Role;
import com.superli.deliveries.domain.core.ShiftType;
import com.superli.deliveries.domain.core.*;

import java.time.DayOfWeek;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ShiftService {

    /**
     * Creates a new shift with required roles and manager.
     */
    public Shift createShift(Date date, ShiftType type, DayOfWeek day,
                             List<Role> requiredRoles, Map<Employee, Role> assignedEmployees, Employee manager) {
        int shiftId = UUID.randomUUID().hashCode();
        return new Shift(shiftId, date, type, day, requiredRoles, assignedEmployees, manager);
    }

    /**
     * Assigns an employee to a shift, if valid.
     */
    public boolean assignEmployee(Shift shift, Employee employee, Role role) {
        if (shift.isEmployeeAssigned(employee)) return false;
        if (!shift.getShiftRequiredRoles().contains(role)) return false;

        shift.addEmployeeToShift(employee, role);
        return true;
    }

    /**
     * Removes an employee from a shift.
     */
    public boolean removeEmployee(Shift shift, Employee employee) {
        if (!shift.isEmployeeAssigned(employee)) return false;

        shift.removeEmployeeFromShift(employee);
        return true;
    }

    /**
     * Checks if a shift is fully staffed.
     */
    public boolean isFullyAssigned(Shift shift) {
        return shift.isShiftFullyAssigned();
    }

    /**
     * Checks if a shift already ended.
     */
    public boolean isShiftInPast(Shift shift) {
        return shift.isPastShift();
    }
}