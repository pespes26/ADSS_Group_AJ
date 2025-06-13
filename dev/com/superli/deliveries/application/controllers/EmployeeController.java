package com.superli.deliveries.application.controllers;

import com.superli.deliveries.domain.core.Employee;
import com.superli.deliveries.domain.core.HRManager;
import com.superli.deliveries.domain.core.Shift;
import com.superli.deliveries.domain.core.Role;
import com.superli.deliveries.domain.core.AvailableShifts;
//import com.superli.deliveries.domain.core.DayOfWeek;
import com.superli.deliveries.domain.core.ShiftType;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Scanner;

public class EmployeeController {
    private static HRManager hrManager = new HRManager();

    public static void viewMyShifts(Employee employee, HRManager hr) {
        List<Shift> shifts = hr.getAllShifts();
        boolean hasShifts = false;

        System.out.println("Your Shifts:");

        for (Shift shift : shifts) {
            if (shift.getShiftEmployees().containsKey(employee)) {
                hasShifts = true;
                Role assignedRole = shift.getShiftEmployees().get(employee);
                System.out.println(shift.getShiftDate() + " - " + shift.getShiftDay() + " - " + shift.getShiftType()
                        + " | Role: " + assignedRole.getRoleName());
            }
        }

        if (!hasShifts) {
            System.out.println("You have no assigned shifts.");
        }
    }
    public static void updateAvailability(Employee employee, Scanner sc) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK);

       /* if (dayOfWeek != java.util.Calendar.THURSDAY) {
            System.out.println("Sorry, availability can only be updated on Thursdays.");
            return;
        }*/

        if (!employee.getAvailabilityConstraints().isEmpty()) {
            System.out.println("You already have availability constraints.");
            System.out.println("Do you want to:");
            System.out.println("1. Clear and submit new constraints");
            System.out.println("2. Edit existing ones");
            System.out.println("0. Logout");
            String choice = sc.nextLine().trim();

            if (choice.equals("1")) {
                employee.clearAvailability();
                System.out.println("All previous availability constraints cleared.");
            } else if (choice.equals("2")) {
                editAvailability(employee, sc);
                System.out.println("Availability updated successfully.");
                return;
            } else if (choice.equals("0")) {
                return;
            }else {
                System.out.println("Invalid choice. No changes made.");
                return;
            }
        }

        while (true) {
            System.out.print("Enter day of week, or type 'done' to finish: ");
            String dayInput = sc.nextLine().trim().toUpperCase();

            if (dayInput.equalsIgnoreCase("DONE")) {
                break;
            }

            DayOfWeek day = parseDay(dayInput);
            if (day == null) {
                System.out.println("Invalid day: " + dayInput + ". Please try again.");
                continue;
            }

            ShiftType shift = readShift(sc);

            if (shiftExists(employee, day, shift)) {
                System.out.println("This shift is already in your availability constraints.");
            } else {
                addNewAvailability(employee, day, shift);
            }
        }

        System.out.println("Availability updated successfully.");
    }

    private static void editAvailability(Employee employee, Scanner sc) {
        while (true) {
            System.out.println("Your current availability:");
            for (AvailableShifts a : employee.getAvailabilityConstraints()) {
                System.out.println("- " + a.getDay() + " " + a.getShift());
            }

            System.out.println("Do you want to:");
            System.out.println("1. Add a new availability");
            System.out.println("2. Remove an existing availability");
            System.out.println("0. Logout");
            String choice = sc.nextLine().trim().toLowerCase();

            if (choice.equals("done")) {
                break;
            } else if (choice.equals("1")) {
                Object[] dayShift = readDayAndShift(sc);
                DayOfWeek day = (DayOfWeek) dayShift[0];
                ShiftType shift = (ShiftType) dayShift[1];

                if (shiftExists(employee, day, shift)) {
                    System.out.println("This shift is already in your availability constraints.");
                } else {
                    addNewAvailability(employee, day, shift);
                }

            } else if (choice.equals("2")) {
                Object[] dayShift = readDayAndShift(sc);
                DayOfWeek day = (DayOfWeek) dayShift[0];
                ShiftType shift = (ShiftType) dayShift[1];

                removeAvailability(employee, day, shift);

            } else if (choice.equals("0")){
                return;
            }else {
                System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static Object[] readDayAndShift(Scanner sc) {
        DayOfWeek day = null;
        while (day == null) {
            System.out.print("Enter day of week: ");
            String dayInput = sc.nextLine().trim().toUpperCase();
            day = parseDay(dayInput);
            if (day == null) {
                System.out.println("Invalid day. Try again.");
            }
        }

        ShiftType shift = readShift(sc);

        return new Object[]{day, shift};
    }

    private static DayOfWeek parseDay(String input) {
        for (DayOfWeek d : DayOfWeek.values()) {
            if (d.name().equals(input)) {
                return d;
            }
        }
        return null;
    }

    private static ShiftType readShift(Scanner sc) {
        ShiftType shift = null;
        while (shift == null) {
            System.out.print("Enter shift type (MORNING or EVENING): ");
            String shiftInput = sc.nextLine().trim().toUpperCase();
            for (ShiftType s : ShiftType.values()) {
                if (s.name().equals(shiftInput)) {
                    shift = s;
                    break;
                }
            }
            if (shift == null) {
                System.out.println("Invalid shift type. Please try again.");
            }
        }
        return shift;
    }

    private static boolean shiftExists(Employee employee, DayOfWeek day, ShiftType shift) {
        for (AvailableShifts a : employee.getAvailabilityConstraints()) {
            if (a.getDay() == day && a.getShift() == shift) {
                return true;
            }
        }
        return false;
    }

    private static void addNewAvailability(Employee employee, DayOfWeek day, ShiftType shift) {
        employee.addAvailability(new AvailableShifts(day, shift));
        System.out.println("Added availability: Day - " + day + ", Shift - " + shift);
    }

    private static void removeAvailability(Employee employee, DayOfWeek day, ShiftType shift) {
        AvailableShifts toRemove = null;
        for (AvailableShifts a : employee.getAvailabilityConstraints()) {
            if (a.getDay() == day && a.getShift() == shift) {
                toRemove = a;
                break;
            }
        }

        if (toRemove != null) {
            employee.getAvailabilityConstraints().remove(toRemove);
            System.out.println("Removed availability: " + day + " " + shift);
        } else {
            System.out.println("No such availability found to remove.");
        }
    }
}