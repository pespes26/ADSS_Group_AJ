package com.superli.deliveries.application.controllers;
import com.superli.deliveries.dataaccess.dao.HR.*;
import com.superli.deliveries.domain.core.*;
import com.superli.deliveries.dto.HR.RoleDTO;
import com.superli.deliveries.dto.HR.ShiftDTO;
import com.superli.deliveries.dto.HR.ShiftRoleDTO;
import com.superli.deliveries.util.Database;

import java.sql.Connection;
import java.sql.SQLException;
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


            Shift morningShift = new Shift(shiftIdCounter++, date, ShiftType.MORNING, day, new ArrayList<>(), new HashMap<>(), null);
            Shift eveningShift = new Shift(shiftIdCounter++, date, ShiftType.EVENING, day, new ArrayList<>(), new HashMap<>(), null);

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
        if (shiftIndex < 0 || shiftIndex >= shifts.size()) {
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
        if (employeeIndex < 1 || employeeIndex > assignedEmployees.size()) {
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

        Map<Integer, Integer> requiredRoleMap;
        Map<Role, Integer> requiredRoles = new HashMap<>();

        try {
            Connection conn = Database.getConnection();
            ShiftRoleDAO shiftRoleDAO = new ShiftRoleDAOImpl(conn);
            RoleDAO roleDAO = new RoleDAOImpl(conn);

            requiredRoleMap = shiftRoleDAO.getRequiredRolesForShift(
                    selectedShift.getShiftDay().toString(),
                    selectedShift.getShiftType().toString()
            );

            if (requiredRoleMap.isEmpty()) {
                System.out.println("No required roles defined for this shift. Please define roles before assigning employees.");
                return;
            }

            for (Map.Entry<Integer, Integer> entry : requiredRoleMap.entrySet()) {
                RoleDTO dto = roleDAO.findById(entry.getKey())
                        .orElseThrow(() -> new RuntimeException("Role not found for ID: " + entry.getKey()));
                requiredRoles.put(new Role(dto.getName()), entry.getValue());
            }

        } catch (Exception e) {
            System.err.println("Failed to load required roles: " + e.getMessage());
            return;
        }

        System.out.println("\nRequired roles for this shift:");
        for (Map.Entry<Role, Integer> entry : requiredRoles.entrySet()) {
            System.out.println("- " + entry.getKey().getRoleName() + " (x" + entry.getValue() + ")");
        }

        List<Role> unassignedRoles = new ArrayList<>();
        requiredRoles.forEach((role, count) -> {
            for (int i = 0; i < count; i++) unassignedRoles.add(role);
        });

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
                break;
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
                    if (r.getRoleName().equalsIgnoreCase(selectedRole.getRoleName()) &&
                        e.isAvailable(selectedShift.getShiftDay(), selectedShift.getShiftType()) &&
                        !assignedEmployees.contains(e)) {
                        candidates.add(e);
                        break;
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
                System.out.println((i + 1) + ". " + e.getFullName() + " (ID: " + e.getId() + ")");
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

        boolean hasShiftManager = assignments.keySet().stream()
                .anyMatch(r -> r.getRoleName().equalsIgnoreCase("shift manager"));
        boolean hasTransportationManager = assignments.keySet().stream()
                .anyMatch(r -> r.getRoleName().equalsIgnoreCase("transportation manager"));

        boolean missingRoles = assignments.size() < requiredRoles.values().stream().mapToInt(i -> i).sum();

        if (!hasShiftManager || !hasTransportationManager || missingRoles) {
            System.out.println("\nWarning: The shift is **invalid** due to the following reasons:");
            if (!hasShiftManager)
                System.out.println("- Missing assignment for: Shift Manager.");
            if (!hasTransportationManager)
                System.out.println("- Missing assignment for: Transportation Manager.");
            if (missingRoles)
                System.out.println("- Not all required roles have been assigned.");

            System.out.println("The shift will not be considered valid until all mandatory roles are assigned properly.");
        } else {
            System.out.println("\nAll required roles have been assigned successfully. The shift is now valid.");
        }

        // שמירה ל־DB
        try {
            ShiftDTO shiftDTO = new ShiftDTO();
            shiftDTO.setShiftDate(selectedShift.getShiftDate());
            shiftDTO.setShiftDay(selectedShift.getShiftDay().toString());
            shiftDTO.setShiftType(selectedShift.getShiftType().toString());

            Map<Integer, Integer> assignedMap = new HashMap<>();
            Connection conn = Database.getConnection();
            RoleDAO roleDAO = new RoleDAOImpl(conn);

            for (Map.Entry<Role, Employee> entry : assignments.entrySet()) {
                RoleDTO dto = roleDAO.findByName(entry.getKey().getRoleName())
                        .orElseThrow(() -> new RuntimeException("Role not found: " + entry.getKey().getRoleName()));
                int employeeId = Integer.parseInt(entry.getValue().getId());
                assignedMap.put(employeeId, dto.getId());
            }

            shiftDTO.setAssignedEmployees(assignedMap);
            ShiftDAO shiftDAO = new ShiftDAOImpl(conn); // מעביר את אותו conn
            shiftDAO.save(shiftDTO);


            System.out.println("Shift assignments saved to the database.");

        } catch (Exception e) {
            System.err.println("Failed to save shift assignments: " + e.getMessage());
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
            System.out.println(i + 1 + ". " + s.getShiftDate() + " - " + s.getShiftDay() + " - " + s.getShiftType());
        }

        int index;
        Shift selectedShift;
        while (true) {
            System.out.print("Choose a shift to define required roles: ");
            index = Integer.parseInt(sc.nextLine());
            if (index <= 0 || index > shifts.size()) {
                System.out.println("Invalid shift index.");
            } else {
                selectedShift = shifts.get(index - 1);
                break;
            }
        }

        if (hr.getAllRoles().stream().noneMatch(r -> r.getRoleName().equalsIgnoreCase("shift manager"))) {
            hr.addRole(new Role("shift manager"));
        }
        if (hr.getAllRoles().stream().noneMatch(r -> r.getRoleName().equalsIgnoreCase("transportation manager"))) {
            hr.addRole(new Role("transportation manager"));
        }

        System.out.println("✅ Mandatory roles were automatically assigned to shift.");
        System.out.println("Enter required roles for this shift (type 'done' to finish):");

        while (true) {
            System.out.print("Role name: ");
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
                int requiredCount;
                while (true) {
                    System.out.print("Enter required number of employees for role '" + roleInput + "': ");
                    String input = sc.nextLine();
                    try {
                        requiredCount = Integer.parseInt(input);
                        if (requiredCount < 1) {
                            System.out.println("Required count must be at least 1.");
                        } else break;
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid number.");
                    }
                }

                try {
                    Connection conn = Database.getConnection();
                    RoleDAO roleDAO = new RoleDAOImpl(conn);
                    ShiftRoleDAO shiftRoleDAO = new ShiftRoleDAOImpl(conn);

                    RoleDTO roleDTO = roleDAO.findByName(roleInput)
                            .orElseThrow(() -> new RuntimeException("Role not found in DB: " + roleInput));

                    ShiftRoleDTO dto = new ShiftRoleDTO();
                    dto.setDayOfWeek(selectedShift.getShiftDay().toString());
                    dto.setShiftType(selectedShift.getShiftType().toString());
                    dto.setRoleId(roleDTO.getId());
                    dto.setRequiredCount(requiredCount);

                    shiftRoleDAO.save(dto);
                    System.out.println("Inserted/Updated role '" + roleInput + "' with count " + requiredCount);
                } catch (SQLException | RuntimeException e) {
                    System.out.println("Error while inserting role: " + e.getMessage());
                }

            } else {
                System.out.println("Role not found in system.");
            }
        }

        System.out.println("Finished updating required roles for shift on " + selectedShift.getShiftDate() + ".");
    }

    public static void ensureSystemRolesExist() {
        try {
            Connection conn = Database.getConnection();
            RoleDAO roleDAO = new RoleDAOImpl(conn);

            String[] systemRoles = { "shift manager", "transportation manager" };

            for (String roleName : systemRoles) {
                Optional<RoleDTO> existing = roleDAO.findByName(roleName);
                if (existing.isEmpty()) {
                    RoleDTO newRole = new RoleDTO();
                    newRole.setName(roleName);
                    roleDAO.save(newRole);
                    System.out.println("✅ Inserted missing system role: " + roleName);
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Error while ensuring system roles: " + e.getMessage());
        }
    }


}
