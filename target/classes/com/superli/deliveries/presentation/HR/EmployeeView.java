package com.superli.deliveries.presentation.HR;

import com.superli.deliveries.domain.core.AvailableShifts;
import com.superli.deliveries.domain.core.Role;
import com.superli.deliveries.domain.core.Shift;
import com.superli.deliveries.domain.core.ShiftType;

import java.time.DayOfWeek;
import java.util.List;

public class EmployeeView {

    public void printShiftsHeader() {
        System.out.println("Your Shifts:");
    }

    public void printShift(Shift shift, Role role) {
        System.out.println(shift.getShiftDate() + " - " + shift.getShiftDay() + " - " + shift.getShiftType()
                + " | Role: " + role.getRoleName());
    }

    public void printNoShifts() {
        System.out.println("You have no assigned shifts.");
    }

    public void printHasConstraintsMenu() {
        System.out.println("You already have availability constraints.");
        System.out.println("Do you want to:");
        System.out.println("1. Clear and submit new constraints");
        System.out.println("2. Edit existing ones");
        System.out.println("0. Logout");
    }

    public void printClearSuccess() {
        System.out.println("All previous availability constraints cleared.");
    }

    public void printAvailabilityUpdated() {
        System.out.println("Availability updated successfully.");
    }

    public void printPromptEnterDay() {
        System.out.print("Enter day of week, or type 'done' to finish: ");
    }

    public void printInvalidDay(String input) {
        System.out.println("Invalid day: " + input + ". Please try again.");
    }

    public void printPromptEnterDayOnly() {
        System.out.print("Enter day of week: ");
    }

    public void printPromptEnterShiftType() {
        System.out.print("Enter shift type (MORNING or EVENING): ");
    }

    public void printShiftAlreadyExists() {
        System.out.println("This shift is already in your availability constraints.");
    }

    public void printEditAvailabilityHeader(List<AvailableShifts> constraints) {
        System.out.println("Your current availability:");
        for (AvailableShifts a : constraints) {
            System.out.println("- " + a.getDay() + " " + a.getShift());
        }
    }

    public void printEditMenu() {
        System.out.println("Do you want to:");
        System.out.println("1. Add a new availability");
        System.out.println("2. Remove an existing availability");
        System.out.println("0. Logout");
    }

    public void printInvalidChoice() {
        System.out.println("Invalid choice. Try again.");
    }

    public void printRemovedAvailability(DayOfWeek day, ShiftType shift) {
        System.out.println("Removed availability: " + day + " " + shift);
    }

    public void printNoSuchAvailability() {
        System.out.println("No such availability found to remove.");
    }

    public void printDBSaveError(String message) {
        System.err.println("Failed to save to DB: " + message);
    }

    public void printDBDeleteError(String message) {
        System.err.println("Failed to delete from DB: " + message);
    }

    public void printDBConnectionError(String message) {
        System.err.println("Failed to connect to database: " + message);
    }
}
