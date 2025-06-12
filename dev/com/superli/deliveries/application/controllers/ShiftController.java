package com.superli.deliveries.application.controllers;
import com.superli.deliveries.domain.core.*;

import java.util.*;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.DayOfWeek;


public class ShiftController {
    public static void createShiftsForTheWeek(Scanner sc) {
        HRManager hr = ManagerController.getHRManager();
        LocalDate upcomingSunday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.SUNDAY));

        List<Shift> shiftsToArchive = new ArrayList<>();
        List<Shift> existingShifts = hr.getAllShifts();

        for (Shift s : existingShifts) {
            LocalDate shiftDate = ((java.sql.Date) s.getShiftDate()).toLocalDate();

            if (shiftDate.isBefore(upcomingSunday)) {
                shiftsToArchive.add(s);
            }
        }

        if (!shiftsToArchive.isEmpty()) {
            hr.getArchivedShifts().archiveShift(shiftsToArchive);
            existingShifts.removeAll(shiftsToArchive);
        }

        boolean alreadyCreated = false;
        for (Shift s : existingShifts) {
            LocalDate shiftDate = ((java.sql.Date) s.getShiftDate()).toLocalDate();
            if (!shiftDate.isBefore(upcomingSunday)) {
                alreadyCreated = true;
                break;
            }
        }

        if (alreadyCreated) {
            System.out.println("Shifts for next week already exist. Cannot create again.");
            return;
        }

        int shiftIdCounter = 1;
        for (int i = 0; i < 7; i++) {
            LocalDate shiftDate = upcomingSunday.plusDays(i);
            DayOfWeek day = DayOfWeek.valueOf(shiftDate.getDayOfWeek().name());

            Date date = java.sql.Date.valueOf(shiftDate);


            Shift morningShift = new Shift(shiftIdCounter++,date, ShiftType.MORNING, day, new ArrayList<>(), new HashMap<>(), null);
            Shift eveningShift = new Shift(shiftIdCounter++,date, ShiftType.EVENING, day, new ArrayList<>(), new HashMap<>(), null);

            hr.addShift(morningShift);
            hr.addShift(eveningShift);
            Role shiftManager = null;
            Role transportationManager = null;

            for (Role r : hr.getAllRoles()) {
                if (r.getRoleName().equalsIgnoreCase("Shift manager")) {
                    shiftManager = r;
                }
                if (r.getRoleName().equalsIgnoreCase("transportation manager")) {
                    transportationManager = r;
                }
            }

            List<Role> requiredRoles = new ArrayList<>();
            if (shiftManager != null) requiredRoles.add(shiftManager);
            if (transportationManager != null) requiredRoles.add(transportationManager);

            hr.defineRequiredRolesForShift(morningShift, requiredRoles);
            hr.defineRequiredRolesForShift(eveningShift, requiredRoles);


        }

        System.out.println("Shifts for next week were created.");
    }


    public static void removeEmployeeFromShift(Scanner sc) {
        HRManager hr = ManagerController.getHRManager();
        List<Shift> shifts = hr.getAllShifts();
        if (shifts.isEmpty()) {
            System.out.println("No shifts available.");
            return;
        }

        System.out.println("Choose a shift:");
        for (int i = 0; i < shifts.size(); i++) {
            Shift s = shifts.get(i);
            System.out.println(i + ". " + s.getShiftDate() + " - " + s.getShiftDay() + " - " + s.getShiftType());
        }

        System.out.print("Enter shift number: ");
        int shiftIndex = Integer.parseInt(sc.nextLine());
        if (shiftIndex < 0  || shiftIndex >= shifts.size()) {
            System.out.println("Invalid shift selection.");
            return;
        }

        Shift selectedShift = shifts.get(shiftIndex);

        if (selectedShift.getShiftEmployees().isEmpty()) {
            System.out.println("No employees assigned to this shift.");
            return;
        }

        List<Employee> assignedEmployees = new ArrayList<>(selectedShift.getShiftEmployees().keySet());

        System.out.println("Assigned employees:");
        for (int i = 0; i < assignedEmployees.size(); i++) {
            Employee e = assignedEmployees.get(i);
            System.out.println((i + 1) + ". (ID: " + e.getId() + ") - " + e.getFullName());
        }

        System.out.print("Choose an employee to remove: ");
        int employeeIndex = Integer.parseInt(sc.nextLine());
        if (employeeIndex < 1  || employeeIndex > assignedEmployees.size()) {
            System.out.println("Invalid employee selection.");
            return;
        }

        Employee selectedEmployee = assignedEmployees.get(employeeIndex - 1);

        hr.removeEmployeeFromShift(selectedShift, selectedEmployee);
        System.out.println("Employee removed from the shift successfully.");
    }

    public static void viewAssignedShifts() {
        HRManager hr = ManagerController.getHRManager();
        List<Shift> shifts = hr.getAllShifts();
        if (shifts.isEmpty()) {
            System.out.println("No shifts available.");
            return;
        }

        boolean foundAssigned = false;
        System.out.println("--- Assigned Shifts ---");

        for (Shift shift : shifts) {
            if (!shift.getShiftEmployees().isEmpty()) {
                Set<Role> assignedRoles = new HashSet<>(shift.getShiftEmployees().values());
                List<Role> requiredRoles = shift.getShiftRequiredRoles();

                List<String> missingRoles = new ArrayList<>();
                for (Role reqRole : requiredRoles) {
                    boolean assigned = false;
                    for (Role assignedRole : assignedRoles) {
                        if (reqRole.getRoleName().equalsIgnoreCase(assignedRole.getRoleName())) {
                            assigned = true;
                            break;
                        }
                    }
                    if (!assigned) {
                        missingRoles.add(reqRole.getRoleName());
                    }
                }

                System.out.println("Shift: " + shift.getShiftDate() + " - " + shift.getShiftDay() + " - " + shift.getShiftType());

                if (!missingRoles.isEmpty()) {
                    System.out.println("*** WARNING: Shift is incomplete! Missing assignments for: ***");
                    for (String roleName : missingRoles) {
                        System.out.println(" - " + roleName);
                    }
                }

                System.out.println("Assigned Employees:");
                for (Map.Entry<Employee, Role> entry : shift.getShiftEmployees().entrySet()) {
                    Employee e = entry.getKey();
                    Role r = entry.getValue();
                    System.out.println("- (ID: " + e.getId() + ") " + e.getFullName() + " | Role: " + r.getRoleName());
                }
                System.out.println();
                foundAssigned = true;
            }
        }

        if (!foundAssigned) {
            System.out.println("No employees have been assigned to any shifts yet.");
        }
    }

    public static void assignEmployeeToShift(Scanner sc) {
        HRManager hr = ManagerController.getHRManager();

        List<Shift> shifts = hr.getAllShifts();
        if (shifts.isEmpty()) {
            System.out.println("No shifts available.");
            return;
        }

        System.out.println("Choose a shift:");
        for (int i = 0; i < shifts.size(); i++) {
            Shift s = shifts.get(i);
            System.out.println((i + 1) + ". " + s.getShiftDate() + " - " + s.getShiftDay() + " - " + s.getShiftType());
        }

        int shiftIndex = Integer.parseInt(sc.nextLine());
        if (shiftIndex < 1 || shiftIndex > shifts.size()) {
            System.out.println("Invalid shift selection.");
            return;
        }
        Shift selectedShift = shifts.get(shiftIndex - 1);

        List<Role> requiredRoles = new ArrayList<>(selectedShift.getShiftRequiredRoles());
        if (requiredRoles.isEmpty()) {
            System.out.println("No required roles defined for this shift. Please define roles before assigning employees.");
            return;
        }

        System.out.println("\nRequired roles for this shift:");
        for (int i = 0; i < requiredRoles.size(); i++) {
            System.out.println((i + 1) + ". " + requiredRoles.get(i).getRoleName());
        }

        List<Role> unassignedRoles = new ArrayList<>(requiredRoles);
        Map<Role, Employee> assignments = new HashMap<>();
        List<Employee> assignedEmployees = new ArrayList<>();

        while (!unassignedRoles.isEmpty()) {
            System.out.println("\nRoles left to assign:");
            for (int i = 0; i < unassignedRoles.size(); i++) {
                System.out.println((i + 1) + ". " + unassignedRoles.get(i).getRoleName());
            }

            System.out.print("Choose a role to assign (or 0 to exit): ");
            int roleChoice = Integer.parseInt(sc.nextLine());
            if (roleChoice == 0) {
                System.out.println("Exiting role assignment menu.");
                return;
            }
            if (roleChoice < 1 || roleChoice > unassignedRoles.size()) {
                System.out.println("Invalid role selection.");
                continue;
            }

            Role selectedRole = unassignedRoles.get(roleChoice - 1);
            System.out.println("\nAssigning role: " + selectedRole.getRoleName());

            List<Employee> candidates = new ArrayList<>();
            for (Employee e : hr.getEmployees()) {
                for (Role r : e.getRoleQualifications()) {
                    if (r.getRoleName().equalsIgnoreCase(selectedRole.getRoleName())) {
                        if (e.isAvailable(selectedShift.getShiftDay(), selectedShift.getShiftType()) &&
                            !assignedEmployees.contains(e)) {
                            candidates.add(e);
                            break;
                        }
                    }
                }
            }

            if (candidates.isEmpty()) {
                System.out.println("No available employees qualified for role: " + selectedRole.getRoleName());
                continue;
            }

            System.out.println("Available employees for " + selectedRole.getRoleName() + ":");
            for (int i = 0; i < candidates.size(); i++) {
                Employee e = candidates.get(i);
                System.out.println((i+1) + ". " + e.getFullName() + " (ID: " + e.getId() + ")");
            }

            System.out.print("Choose an employee to assign for this role (or 0 to skip): ");
            int empChoice = Integer.parseInt(sc.nextLine());
            if (empChoice == 0) {
                System.out.println("Skipping assignment for role: " + selectedRole.getRoleName());
                continue;
            }
            if (empChoice < 1 || empChoice > candidates.size()) {
                System.out.println("Invalid employee selection.");
                continue;
            }

            Employee selectedEmployee = candidates.get(empChoice - 1);
            assignments.put(selectedRole, selectedEmployee);
            selectedShift.addEmployeeToShift(selectedEmployee, selectedRole);

            System.out.println("Assigned " + selectedEmployee.getId() + " as " + selectedRole.getRoleName());

            assignedEmployees.add(selectedEmployee);
            unassignedRoles.remove(selectedRole);
        }
        boolean hasShiftManager = false;
        boolean hasTransportationManager = false;

        for (Role role : assignments.keySet()) {
            String roleName = role.getRoleName().toLowerCase();
            if (roleName.equals("shift manager")) {
                hasShiftManager = true;
            }
            else if (roleName.equals("transportation manager")) {
                hasTransportationManager = true;
            }
        }


        boolean missingRoles = assignments.size() < requiredRoles.size();

        if (!hasShiftManager || !hasTransportationManager || missingRoles) {
            System.out.println("\nWarning: The shift is **invalid** due to the following reasons:");
            if (!hasShiftManager)
                System.out.println("- Missing assignment for: Shift Manager.");
            if (!hasTransportationManager)
                System.out.println("- Missing assignment for: Transportation Manager.");
            if (missingRoles)
                System.out.println("- Not all required roles have been assigned.");

            System.out.println("The shift will not be considered valid until all mandatory roles are assigned properly.");
            assignments.clear();
        } else {
            System.out.println("\nAll required roles have been assigned successfully. The shift is now valid.");
        }
    }

    public static void defineRolesForSpecificShift(Scanner sc) {
        HRManager hr = ManagerController.getHRManager();
        List<Shift> shifts = hr.getAllShifts();
        if (shifts.isEmpty()) {
            System.out.println("No shifts available.");
            return;
        }


        System.out.println("All shifts for next week:");
        for (int i = 0; i < shifts.size(); i++) {
            Shift s = shifts.get(i);
            System.out.println(i+1 + ". " + s.getShiftDate() + " - " + s.getShiftDay() + " - " + s.getShiftType());
        }
        int index;
        Shift selectedShift;
        while (true) {
            System.out.print("Choose a shift to define required roles:");
            index = Integer.parseInt(sc.nextLine());
            if (index < 0 || index > shifts.size()) {
                System.out.println("Invalid shift index.");
                continue;
            }
            selectedShift = shifts.get(index-1);
            break;
        }


        List<Role> rolesToAssign = new ArrayList<>();
        Role shiftManager = new Role("Shift manager");
        rolesToAssign.add(shiftManager);
        System.out.println("Enter required roles for this shift (type 'done' to finish):");

        while (true) {
            System.out.print("Role: ");
            String roleInput = sc.nextLine();

            if (roleInput.equalsIgnoreCase("done")) break;

            Role found = null;
            for (Role r : hr.getAllRoles()) {
                if (r.getRoleName().equalsIgnoreCase(roleInput)) {
                    found = r;
                    break;
                }
            }

            if (found != null) {
                if (!rolesToAssign.contains(found)) {
                    rolesToAssign.add(found);
                    System.out.println("Role added.");
                } else {
                    System.out.println("Role already added.");
                }
            } else {
                System.out.println("Role not found in system.");
            }
        }

        hr.defineRequiredRolesForShift(selectedShift, rolesToAssign);
        System.out.println("Roles updated for shift on " + selectedShift.getShiftDate() + ".");
    }
}
