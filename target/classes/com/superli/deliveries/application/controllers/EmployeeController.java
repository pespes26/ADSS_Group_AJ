package com.superli.deliveries.application.controllers;

import com.superli.deliveries.dataaccess.dao.HR.*;
import com.superli.deliveries.dto.HR.*;
import com.superli.deliveries.domain.core.*;
import com.superli.deliveries.presentation.HR.EmployeeView;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EmployeeController {
    private static final HRManager hrManager = new HRManager();
    private static final EmployeeView view = new EmployeeView();

    public static void viewMyShifts(Employee employee, HRManager hr) {
        List<Shift> shifts = hr.getAllShifts();
        boolean hasShifts = false;

        view.printShiftsHeader();

        for (Shift shift : shifts) {
            if (shift.getShiftEmployees().containsKey(employee)) {
                hasShifts = true;
                Role assignedRole = shift.getShiftEmployees().get(employee);
                view.printShift(shift, assignedRole);
            }
        }

        if (!hasShifts) {
            view.printNoShifts();
        }
    }

    public static void updateAvailability(Employee employee, Scanner sc) {
        AvailableShiftDAO dao;
        try {
            dao = new AvailableShiftDAOImpl();
        } catch (SQLException e) {
            view.printDBConnectionError(e.getMessage());
            return;
        }

        if (!employee.getAvailabilityConstraints().isEmpty()) {
            view.printHasConstraintsMenu();
            String choice = sc.nextLine().trim();

            if (choice.equals("1")) {
                for (AvailableShifts a : new ArrayList<>(employee.getAvailabilityConstraints())) {
                    try {
                        dao.deleteByCompositeKey(
                                Integer.parseInt(employee.getId()),
                                a.getDay().name(),
                                a.getShift().name()
                        );
                    } catch (SQLException e) {
                        view.printDBDeleteError(e.getMessage());
                    }
                }
                employee.clearAvailability();
                view.printClearSuccess();
            } else if (choice.equals("2")) {
                editAvailability(employee, sc);
                view.printAvailabilityUpdated();
                return;
            } else if (choice.equals("0")) {
                return;
            } else {
                view.printInvalidChoice();
                return;
            }
        }

        while (true) {
            view.printPromptEnterDay();
            String dayInput = sc.nextLine().trim().toUpperCase();

            if (dayInput.equalsIgnoreCase("DONE")) break;

            DayOfWeek day = parseDay(dayInput);
            if (day == null) {
                view.printInvalidDay(dayInput);
                continue;
            }

            ShiftType shift = readShift(sc);

            if (shiftExists(employee, day, shift)) {
                view.printShiftAlreadyExists();
            } else {
                addNewAvailability(employee, day, shift);
            }
        }

        view.printAvailabilityUpdated();
    }

    private static void editAvailability(Employee employee, Scanner sc) {
        while (true) {
            view.printEditAvailabilityHeader(employee.getAvailabilityConstraints());
            view.printEditMenu();
            String choice = sc.nextLine().trim().toLowerCase();

            if (choice.equals("0")) {
                break;
            } else if (choice.equals("1")) {
                Object[] dayShift = readDayAndShift(sc);
                DayOfWeek day = (DayOfWeek) dayShift[0];
                ShiftType shift = (ShiftType) dayShift[1];

                if (shiftExists(employee, day, shift)) {
                    view.printShiftAlreadyExists();
                } else {
                    addNewAvailability(employee, day, shift);
                }

            } else if (choice.equals("2")) {
                Object[] dayShift = readDayAndShift(sc);
                DayOfWeek day = (DayOfWeek) dayShift[0];
                ShiftType shift = (ShiftType) dayShift[1];

                removeAvailability(employee, day, shift);

            } else {
                view.printInvalidChoice();
            }
        }
    }

    private static Object[] readDayAndShift(Scanner sc) {
        DayOfWeek day = null;
        while (day == null) {
            view.printPromptEnterDayOnly();
            String dayInput = sc.nextLine().trim().toUpperCase();
            day = parseDay(dayInput);
            if (day == null) {
                view.printInvalidDay(dayInput);
            }
        }

        ShiftType shift = readShift(sc);
        return new Object[]{day, shift};
    }

    public static DayOfWeek parseDay(String input) {
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
            view.printPromptEnterShiftType();
            String shiftInput = sc.nextLine().trim().toUpperCase();
            for (ShiftType s : ShiftType.values()) {
                if (s.name().equals(shiftInput)) {
                    shift = s;
                    break;
                }
            }
            if (shift == null) {
                view.printInvalidChoice();
            }
        }
        return shift;
    }

    public static boolean shiftExists(Employee employee, DayOfWeek day, ShiftType shift) {
        for (AvailableShifts a : employee.getAvailabilityConstraints()) {
            if (a.getDay() == day && a.getShift() == shift) {
                return true;
            }
        }
        return false;
    }

    public static void addNewAvailability(Employee employee, DayOfWeek day, ShiftType shift) {
        employee.addAvailability(new AvailableShifts(day, shift));

        try {
            AvailableShiftDAO dao = new AvailableShiftDAOImpl();
            AvailableShiftDTO dto = new AvailableShiftDTO(
                    Integer.parseInt(employee.getId()),
                    day.name(),
                    shift.name()
            );
            dao.save(dto);

        } catch (Exception e) {
            view.printDBSaveError(e.getMessage());
        }
    }

    public static void removeAvailability(Employee employee, DayOfWeek day, ShiftType shift) {
        AvailableShifts toRemove = null;
        for (AvailableShifts a : employee.getAvailabilityConstraints()) {
            if (a.getDay() == day && a.getShift() == shift) {
                toRemove = a;
                break;
            }
        }

        if (toRemove != null) {
            employee.getAvailabilityConstraints().remove(toRemove);
            view.printRemovedAvailability(day, shift);

            try {
                AvailableShiftDAO dao = new AvailableShiftDAOImpl();
                int employeeId = Integer.parseInt(employee.getId());
                dao.deleteByCompositeKey(employeeId, day.name(), shift.name());
            } catch (Exception e) {
                view.printDBDeleteError(e.getMessage());
            }

        } else {
            view.printNoSuchAvailability();
        }
    }
}
