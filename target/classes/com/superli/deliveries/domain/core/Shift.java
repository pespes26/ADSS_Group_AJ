package com.superli.deliveries.domain.core;

import java.time.DayOfWeek;
import java.util.*;

public class Shift {
    private int shiftId;
    private Date shiftDate;
    private ShiftType shiftType;
    private DayOfWeek shiftDay;
    private List<Role> shiftRequiredRoles;
    private Map<Employee, Role> shiftEmployees;
    private Employee shiftManager;

    public Shift(int shiftId, Date shiftDate, ShiftType shiftType, DayOfWeek shiftDay, List<Role> shiftRequiredRoles, Map<Employee, Role> shiftEmployees, Employee shiftManager) {
        this.shiftId = shiftId;
        this.shiftDate = shiftDate;
        this.shiftType = shiftType;
        this.shiftDay = shiftDay;
        this.shiftRequiredRoles = shiftRequiredRoles;
        this.shiftEmployees = shiftEmployees;
        this.shiftManager = shiftManager;
    }

    // ----------------------------------- Getters -----------------------------------
    public List<Role> getShiftRequiredRoles() {
        return shiftRequiredRoles;
    }

    public ShiftType getShiftType() {
        return shiftType;
    }

    public Date getShiftDate() {
        return shiftDate;
    }

    public Map<Employee, Role> getShiftEmployees() {
        return shiftEmployees;
    }

    public Employee getShiftManager() {
        return shiftManager;
    }

    public DayOfWeek getShiftDay() {
        return shiftDay;
    }

    public int getShiftId() {
        return shiftId;
    }

    // ----------------------------------- End Getters -----------------------------------

    // ----------------------------------- Assignment Management -----------------------------------
    /**
     * Assigns an employee to the given role in this shift.
     * Assigns the employee with the given role only if:
     * 1. The employee hasn't already been assigned to the shift.
     * 2. The role is still required
     * If the assignment is successful, we remove the role from the required roles list of the shift.
     *
     * @param employee - the employee to assign to the shift.
     * @param role - the role to assign the employee to.
     */
    public void addEmployeeToShift(Employee employee, Role role){
        // if the role is not in the required roles list - return
        if (!shiftRequiredRoles.contains(role)) {
            return;
        }

        // if the employee was assigned to the shift - return
        if (shiftEmployees.containsKey(employee)) {
            return;
        }

        shiftEmployees.putIfAbsent(employee,role);
        shiftRequiredRoles.remove(role);
    }

    /**
     * Removes the given employee from the shift and returns their role to the required roles list.
     *
     * @param employee - the employee to remove from the shift.
     */
    public void removeEmployeeFromShift(Employee employee){
        if (shiftEmployees.containsKey(employee)) {
            shiftRequiredRoles.add(shiftEmployees.get(employee));
            shiftEmployees.remove(employee);

        }
    }

    // ----------------------------------- End Assignment Management -----------------------------------

    // ----------------------------------- Status Checks -----------------------------------
    /**
     * Checks whether the employee is assigned to any role in this shift.
     *
     * @param employee - the employee to check.
     * @return the boolean value true - if the employee is assigned to the shift.
     *         the boolean value false - otherwise.
     */
    public boolean isEmployeeAssigned(Employee employee) {
        return shiftEmployees.containsKey(employee);
    }

    /**
     * Checks if all required roles in the shift have been assigned.
     *
     * @return the boolean value true - if no roles remain unassigned
     *         the boolean value false - otherwise.
     */
    public boolean isShiftFullyAssigned() {
        return shiftRequiredRoles.isEmpty();
    }

    /**
     * Checks if the shift has already ended based on the current date and time.
     *
     * @return the boolean value true - if the shift date is before the current moment(shift ended)
     *         the boolean value false - otherwise.
     */
    public boolean isPastShift() {
        Date now = new Date();
        return shiftDate.before(now);
    }

    // ----------------------------------- End Status Checks -----------------------------------
}