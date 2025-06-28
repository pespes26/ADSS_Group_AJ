package com.superli.deliveries.application.services;
import com.superli.deliveries.application.controllers.ManagerController;
import com.superli.deliveries.dataaccess.dao.HR.*;
import com.superli.deliveries.dataaccess.dao.del.SiteDAO;
import com.superli.deliveries.dataaccess.dao.del.SiteDAOImpl;
import com.superli.deliveries.domain.core.*;
import com.superli.deliveries.dto.HR.EmployeeDTO;
import com.superli.deliveries.dto.HR.RoleDTO;
import com.superli.deliveries.dto.HR.ShiftDTO;
import com.superli.deliveries.dto.HR.ShiftRoleDTO;
import com.superli.deliveries.dto.del.SiteDTO;
import com.superli.deliveries.util.Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.DayOfWeek;

public class ShiftService {
    public static String createShiftsForTheWeek() {
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

        try {
            ShiftDAO shiftDAO = new ShiftDAOImpl();
            shiftDAO.clearAllAndArchive();
            System.out.println("Shifts archived successfully.");
        } catch (SQLException e) {
            System.out.println("Failed to archive shifts: " + e.getMessage());
            e.printStackTrace();
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
            return "Shifts for next week already exist. Cannot create again.";

        }

        try {
            SiteDAO site = new SiteDAOImpl();
            List<SiteDTO> sites = site.findAll();


            for (int i = 0; i < 7; i++) {
                for (SiteDTO s : sites) {
                    int siteId = Integer.parseInt(s.getSiteId());


                    LocalDate shiftDate = upcomingSunday.plusDays(i);
                    DayOfWeek day = DayOfWeek.valueOf(shiftDate.getDayOfWeek().name());

                    Date date = java.sql.Date.valueOf(shiftDate);


                    Shift morningShift = new Shift(siteId, date, ShiftType.MORNING, day, new ArrayList<>(), new HashMap<>(), null);
                    Shift eveningShift = new Shift(siteId, date, ShiftType.EVENING, day, new ArrayList<>(), new HashMap<>(), null);

                    hr.addShift(morningShift);
                    hr.addShift(eveningShift);

                    Map<String, Integer> defaultRoles = new HashMap<>();
                    defaultRoles.put("shift manager", 1);
                    defaultRoles.put("transportation manager", 1);
                    ManagerController.defineRolesForSpecificShift(day, ShiftType.MORNING, defaultRoles,siteId);
                    ManagerController.defineRolesForSpecificShift(day, ShiftType.EVENING, defaultRoles,siteId);
                }
            }
            return"Shifts for next week were created.";
        }
        catch (SQLException e) {
            return "Error while creating shifts for the week: " + e.getMessage();
        }
    }


    public static void removeEmployeeFromShift(Scanner sc) {
        try {
            ShiftDAOImpl shiftDAO = new ShiftDAOImpl();
            EmployeeDAOImpl employeeDAO = new EmployeeDAOImpl(Database.getConnection());


            List<ShiftDTO> shifts = shiftDAO.findAll();
            if (shifts.isEmpty()) {
                System.out.println("No shifts available.");
                return;
            }

            System.out.println("Choose a shift:");
            for (int i = 0; i < Math.min(14,shifts.size()); i++) {
                ShiftDTO s = shifts.get(i);
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                String formattedDate = formatter.format(s.getShiftDate());
                System.out.println((i + 1) + ". " + formattedDate + " - " + s.getShiftDay() + " - " + s.getShiftType() + " | Site ID: " + s.getSiteId());

            }

            System.out.print("Enter shift number: ");
            int shiftIndex = Integer.parseInt(sc.nextLine());
            if (shiftIndex < 1 || shiftIndex > shifts.size()) {
                System.out.println("Invalid shift selection.");
                return;
            }

            ShiftDTO selectedShift = shifts.get(shiftIndex - 1);
            Map<Integer, Integer> assignedEmployees = selectedShift.getAssignedEmployees();

            if (assignedEmployees == null || assignedEmployees.isEmpty()) {
                System.out.println("No employees assigned to this shift.");
                return;
            }

            List<Integer> employeeIds = new ArrayList<>(assignedEmployees.keySet());

            System.out.println("Assigned employees:");
            for (int i = 0; i < employeeIds.size(); i++) {
                Optional<EmployeeDTO> empOpt = employeeDAO.findById(String.valueOf(employeeIds.get(i)));
                if (empOpt.isPresent()) {
                    EmployeeDTO e = empOpt.get();
                    System.out.println((i + 1) + ". (ID: " + e.getId() + ") - " + e.getFullName());
                } else {
                    System.out.println((i + 1) + ". (ID: " + employeeIds.get(i) + ") - [Not Found]");
                }
            }

            System.out.print("Choose an employee to remove: ");
            int employeeIndex = Integer.parseInt(sc.nextLine());
            if (employeeIndex < 1 || employeeIndex > employeeIds.size()) {
                System.out.println("Invalid employee selection.");
                return;
            }

            int employeeIdToRemove = employeeIds.get(employeeIndex - 1);


            shiftDAO.removeAssignment(
                    String.valueOf(employeeIdToRemove),
                    selectedShift.getShiftDay(),
                    selectedShift.getShiftType(),
                    selectedShift.getShiftDate().toString(),
                    selectedShift.getSiteId()
            );

            System.out.println("Employee removed from the shift successfully.");

        } catch (Exception e) {
            System.out.println("Error removing employee from shift: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static List<String> viewAssignedShifts() {
        List<String> output = new ArrayList<>();

        try {
            ShiftDAOImpl shiftDAO = new ShiftDAOImpl();
            ShiftRoleDAOImpl shiftRoleDAO = new ShiftRoleDAOImpl(Database.getConnection());
            RoleDAOImpl roleDAO = new RoleDAOImpl(Database.getConnection());
            EmployeeDAOImpl employeeDAO = new EmployeeDAOImpl(Database.getConnection());

            List<ShiftDTO> shifts = shiftDAO.findAll();
            if (shifts.isEmpty()) {
                output.add("No shifts available.");
                return output;
            }

            boolean foundAssigned = false;
            output.add("--- Assigned Shifts ---");

            for (ShiftDTO shift : shifts) {
                Map<Integer, Integer> assigned = shift.getAssignedEmployees();
                if (assigned == null || assigned.isEmpty())
                    continue;

                foundAssigned = true;

                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                String formattedDate = formatter.format(shift.getShiftDate());
                output.add("Shift: " + formattedDate + " - " + shift.getShiftDay() + " - " + shift.getShiftType() + "  Site ID:" + shift.getSiteId());

                Map<Integer, Integer> requiredRoles = shiftRoleDAO.getRequiredRolesForShift(
                        shift.getShiftDay(), shift.getShiftType(), shift.getSiteId());

                Map<Integer, Integer> assignedRoleCounts = new HashMap<>();
                for (int roleId : assigned.values()) {
                    assignedRoleCounts.put(roleId, assignedRoleCounts.getOrDefault(roleId, 0) + 1);
                }

                List<String> missingRoles = new ArrayList<>();
                for (Map.Entry<Integer, Integer> entry : requiredRoles.entrySet()) {
                    int roleId = entry.getKey();
                    int requiredCount = entry.getValue();
                    int assignedCount = assignedRoleCounts.getOrDefault(roleId, 0);
                    if (assignedCount < requiredCount) {
                        Optional<RoleDTO> role = roleDAO.findById(roleId);
                        missingRoles.add(role.map(RoleDTO::getName).orElse("Unknown Role") + " (missing " + (requiredCount - assignedCount) + ")");
                    }
                }

                if (!missingRoles.isEmpty()) {
                    output.add("*** WARNING: Shift is incomplete! Missing assignments for:");
                    for (String msg : missingRoles) {
                        output.add(" - " + msg);
                    }
                }

                output.add("Assigned Employees:");
                for (Map.Entry<Integer, Integer> entry : assigned.entrySet()) {
                    Optional<EmployeeDTO> empOpt = employeeDAO.findById(String.valueOf(entry.getKey()));
                    Optional<RoleDTO> roleOpt = roleDAO.findById(entry.getValue());

                    if (empOpt.isPresent() && roleOpt.isPresent()) {
                        EmployeeDTO emp = empOpt.get();
                        RoleDTO role = roleOpt.get();
                        output.add("- (ID: " + emp.getId() + ") " + emp.getFullName() + " | Role: " + role.getName());
                    } else {
                        String empInfo = empOpt.map(emp -> "(ID: " + emp.getId() + ") " + emp.getFullName())
                                .orElse("Employee ID: " + entry.getKey() + " (not found)");

                        String roleInfo = roleOpt.map(RoleDTO::getName)
                                .orElse("Role ID: " + entry.getValue() + " (not found)");

                        output.add("- " + empInfo + " | Role: " + roleInfo);
                    }
                }

                output.add("");
            }

            if (!foundAssigned) {
                output.add("No employees have been assigned to any shifts yet.");
            }

        } catch (SQLException e) {
            output.add("Error retrieving assigned shifts: " + e.getMessage());
        }

        return output;
    }

    // Entry point for assigning employees to a specific shift in a given site
    public static void assignEmployeeToShift(Scanner sc) {
        HRManager hr = ManagerController.getHRManager();

        // Prompt user to enter the site ID
        System.out.print("Enter Site ID for this shift: ");
        int siteId;
        try {
            siteId = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid Site ID. Aborting.");
            return;
        }

        // Filter all shifts that belong to the selected site and sort them
        List<Shift> shifts = hr.getAllShifts().stream()
                .filter(s -> s.getSiteId() == siteId)
                .sorted(Comparator.comparing(Shift::getShiftDate).thenComparing(Shift::getShiftType))
                .toList();

        // No shifts for the selected site
        if (shifts.isEmpty()) {
            System.out.println("No shifts available for this site.");
            return;
        }

        // Display available shifts for user to choose from
        System.out.println("All shifts for next week:");
        for (int i = 0; i < Math.min(14, shifts.size()); i++) {
            Shift s = shifts.get(i);
            System.out.println((i + 1) + ". " + s.getShiftDate() + " - " + s.getShiftDay() + " - " + s.getShiftType());
        }

        // Get shift selection from user
        System.out.print("Choose a shift:");
        int shiftIndex;
        try {
            shiftIndex = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }

        // Validate selected index
        if (shiftIndex < 1 || shiftIndex > Math.min(14, shifts.size())) {
            System.out.println("Invalid shift selection.");
            return;
        }

        Shift selectedShift = shifts.get(shiftIndex - 1);

        // Maps to store required and already assigned roles
        Map<Integer, Integer> requiredRoleMap;
        Map<Role, Integer> requiredRoles = new HashMap<>();
        Map<Integer, Integer> alreadyAssignedCount = new HashMap<>();
        Map<String, Set<String>> assignedEmployeesPerRole = new HashMap<>();

        try {
            // === Step 1: Set up database access ===
            Connection conn = Database.getConnection();
            ShiftRoleDAO shiftRoleDAO = new ShiftRoleDAOImpl(conn);
            RoleDAO roleDAO = new RoleDAOImpl(conn);
            ShiftDAOImpl shiftDAO = new ShiftDAOImpl(conn);

            // === Step 2: Load required roles for the selected shift ===
            requiredRoleMap = shiftRoleDAO.getRequiredRolesForShift(
                    selectedShift.getShiftDay().toString(),
                    selectedShift.getShiftType().toString(),
                    siteId
            );

            // If no roles were defined for the shift
            if (requiredRoleMap.isEmpty()) {
                System.out.println("No required roles defined for this shift. Please define roles before assigning employees.");
                return;
            }

            // === Step 3: Load existing shift assignments to avoid duplicates ===
            List<ShiftDTO> siteShifts = shiftDAO.findAll();
            for (ShiftDTO dto : siteShifts) {
                if (dto.getShiftDate().equals(selectedShift.getShiftDate()) &&
                        dto.getShiftType().equalsIgnoreCase(selectedShift.getShiftType().toString()) &&
                        dto.getShiftDay().equalsIgnoreCase(selectedShift.getShiftDay().toString())) {

                    Map<Integer, Integer> assignments = dto.getAssignedEmployees();

                    for (Map.Entry<Integer, Integer> entry : assignments.entrySet()) {
                        String empId = String.valueOf(entry.getKey());
                        int roleId = entry.getValue();

                        // Get role name from DB
                        String roleName = roleDAO.findById(roleId)
                                .orElseThrow(() -> new RuntimeException("Role not found for ID: " + roleId))
                                .getName();

                        // Track how many employees already assigned per role
                        alreadyAssignedCount.put(roleId, alreadyAssignedCount.getOrDefault(roleId, 0) + 1);

                        // Track which employees are already assigned to which roles
                        assignedEmployeesPerRole
                                .computeIfAbsent(roleName.toLowerCase(), k -> new HashSet<>())
                                .add(empId);
                    }
                }
            }

            // === Step 4: Compute how many more employees are needed for each role ===
            for (Map.Entry<Integer, Integer> entry : requiredRoleMap.entrySet()) {
                int roleId = entry.getKey();
                int required = entry.getValue();
                int already = alreadyAssignedCount.getOrDefault(roleId, 0);
                int toAssign = required - already;

                if (toAssign <= 0) continue;

                RoleDTO dto = roleDAO.findById(roleId)
                        .orElseThrow(() -> new RuntimeException("Role not found for ID: " + roleId));
                Role role = new Role(dto.getName());

                requiredRoles.put(role, toAssign);
            }

        } catch (Exception e) {
            System.err.println("Failed to load required roles: " + e.getMessage());
            return;
        }

        // If all required roles are already filled
        if (requiredRoles.isEmpty()) {
            System.out.println("All required roles for this shift are already fully assigned.");
            return;
        }

        // Display required roles
        System.out.println("\nRequired roles for this shift:");
        for (Map.Entry<Role, Integer> entry : requiredRoles.entrySet()) {
            System.out.println("- " + entry.getKey().getRoleName() + " (x" + entry.getValue() + ")");
        }

        // Flatten role list for assignment menu
        List<Role> unassignedRoles = new ArrayList<>();
        requiredRoles.forEach((role, count) -> {
            for (int i = 0; i < count; i++) unassignedRoles.add(role);
        });

        Map<Role, List<Employee>> assignments = new HashMap<>();

        try {
            Connection conn = Database.getConnection();
            ShiftDAOImpl shiftDAO = new ShiftDAOImpl(conn);
            RoleDAO roleDAO = new RoleDAOImpl(conn);

            // === Step 5: Assignment loop – assign employees to roles ===
            while (!unassignedRoles.isEmpty()) {
                // Display roles still unassigned
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

                // === Step 6: Filter eligible employees ===
                List<Employee> candidates = new ArrayList<>();
                for (Employee e : hr.getEmployees()) {
                    // Only employees in this site or global (-1)
                    if ((e.getSiteId() != siteId && e.getSiteId() != -1)) continue;

                    // Skip if already assigned
                    Set<String> alreadyAssigned = assignedEmployeesPerRole.getOrDefault(
                            selectedRole.getRoleName().toLowerCase(), Set.of());
                    if (alreadyAssigned.contains(e.getId())) {
                        System.out.println("Skipping " + e.getFullName() + " — already assigned as " + selectedRole.getRoleName());
                        continue;
                    }

                    // Must be qualified and available
                    for (Role r : e.getRoleQualifications()) {
                        if (r.getRoleName().equalsIgnoreCase(selectedRole.getRoleName()) &&
                                e.isAvailable(selectedShift.getShiftDay(), selectedShift.getShiftType())) {
                            candidates.add(e);
                            break;
                        }
                    }
                }

                // === Step 7: Display candidates and assign ===
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
                assignments.computeIfAbsent(selectedRole, k -> new ArrayList<>()).add(selectedEmployee);
                System.out.println("Assigned " + selectedEmployee.getId() + " as " + selectedRole.getRoleName());

                unassignedRoles.remove(roleChoice - 1);
            }

            // === Step 8: Save all assignments to the database ===
            ShiftDTO shiftDTO = new ShiftDTO();
            shiftDTO.setShiftDate(selectedShift.getShiftDate());
            shiftDTO.setShiftDay(selectedShift.getShiftDay().toString());
            shiftDTO.setShiftType(selectedShift.getShiftType().toString());
            shiftDTO.setSiteId(siteId);

            Map<Integer, Integer> assignedMap = new HashMap<>();

            for (Map.Entry<Role, List<Employee>> entry : assignments.entrySet()) {
                Role role = entry.getKey();
                RoleDTO dto = roleDAO.findByName(role.getRoleName())
                        .orElseThrow(() -> new RuntimeException("Role not found: " + role.getRoleName()));

                for (Employee emp : entry.getValue()) {
                    int empId = Integer.parseInt(emp.getId());

                    // Avoid duplicate assignments
                    if (assignedMap.containsKey(empId)) {
                        System.out.println("Skipping duplicate assignment for employee " + emp.getId());
                        continue;
                    }

                    // Persist assignment in DB
                    shiftDAO.saveAssignment(
                            emp.getId(),
                            shiftDTO.getShiftDay(),
                            shiftDTO.getShiftType(),
                            shiftDTO.getShiftDate().toString(),
                            dto.getId(),
                            siteId
                    );
                    assignedMap.put(empId, dto.getId());
                }
            }

            // Save final mapping into DTO (optional)
            shiftDTO.setAssignedEmployees(assignedMap);
            System.out.println("Shift assignments saved to the database.");

        } catch (Exception e) {
            System.err.println("Failed to save shift assignments: " + e.getMessage());
        }
    }

    public static void defineRolesForSpecificShift(DayOfWeek day, ShiftType type, Map<String, Integer> roleNameToCountMap, int siteId) {
        try {
            HRManager hr = ManagerController.getHRManager();
            Connection conn = Database.getConnection();


            SiteDAO siteDAO = new SiteDAOImpl();
            if (siteDAO.findById(String.valueOf(siteId)).isEmpty()) {
                throw new IllegalArgumentException("Site with ID " + siteId + " does not exist.");
            }

            RoleDAO roleDAO = new RoleDAOImpl(conn);
            ShiftRoleDAO shiftRoleDAO = new ShiftRoleDAOImpl(conn);

            for (Map.Entry<String, Integer> entry : roleNameToCountMap.entrySet()) {
                String roleName = entry.getKey();
                int count = entry.getValue();

                Optional<RoleDTO> roleOpt = roleDAO.findByName(roleName);
                if (roleOpt.isEmpty()) {
                    throw new IllegalArgumentException("Role not found in system: " + roleName);
                }

                RoleDTO roleDTO = roleOpt.get();

                ShiftRoleDTO dto = new ShiftRoleDTO();
                dto.setDayOfWeek(day.toString());
                dto.setShiftType(type.toString());
                dto.setRoleId(roleDTO.getId());
                dto.setRequiredCount(count);
                dto.setSiteId(siteId);

                shiftRoleDAO.save(dto);
            }

        } catch (SQLException e) {
            System.err.println("Error defining roles: " + e.getMessage());
        }
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
                    System.out.println("Inserted missing system role: " + roleName);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error while ensuring system roles: " + e.getMessage());
        }
    }


}