package com.superli.deliveries.presentation.HR;

import com.superli.deliveries.application.services.RoleService;
import com.superli.deliveries.application.services.ShiftService;
import com.superli.deliveries.domain.core.*;
import com.superli.deliveries.application.controllers.*;
import com.superli.deliveries.dto.HR.ArchivedEmployeeDTO;
import com.superli.deliveries.dto.HR.ArchivedShiftDTO;

import java.sql.SQLException;
import java.util.*;

public class HRDetailsView {

    public HRDetailsView() {
    }

    public void mainLoginMenu() {
        Scanner sc = new Scanner(System.in);
        try {
            ManagerController.initHRControllers();
        } catch (SQLException e) {
            System.out.println("Failed to initialize HR controllers: " + e.getMessage());
        }
        while (true) {
            System.out.println("\nWelcome to the Shift Management System");
            System.out.println("Choose login type:");
            System.out.println("0 - HRManager");
            System.out.println("1 - Employee");
            System.out.println("2 - exit");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "0":
                    HRManagerMenu();
                    break;
                case "1":
                    employeeLoginMenu();
                    break;
                case "2":
                    System.out.println("Exiting program. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void HRManagerMenu() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter HRManager password: ");
        String password = sc.nextLine();

        if (!password.equals("1111")) {
            System.out.println("Incorrect password. Access denied.");
            return;
        }
        try {

            ShiftService.ensureSystemRolesExist();
        } catch (Exception e) {
            System.out.println("Failed to ensure system roles: " + e.getMessage());
        }
        while (true) {
            System.out.println("\n--- HR Manager Menu ---");
            System.out.println("1. Employee Management");
            System.out.println("2. Role and Shift Management");
            System.out.println("3. Shift Assignment");
            System.out.println("4. Archive");
            System.out.println("0. Logout");
            System.out.print("Enter choice: ");

            String HRManagerChoice = sc.nextLine();

            switch (HRManagerChoice) {
                case "1":
                    EmployeeManagementDisplay(sc);
                    break;
                case "2":
                    RoleAndShiftManagementDisplay(sc);
                    break;
                case "3":
                    ShiftAssignmentDisplay(sc);
                    break;
                case "4":
                    archiveDisplay(sc);
                    break;
                case "0":
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void employeeLoginMenu() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter your ID: ");
        String id = sc.nextLine();

        Employee employee = ManagerController.getHRManager().FindEmployeeByID(id);

        if (employee == null) {
            System.out.println("Employee not found or inactive.");
            return;
        }

        while (true) {
            System.out.println("\n--- Employee Menu ---");
            System.out.println("1. View my shifts");
            System.out.println("2. Update availability constraints");
            System.out.println("0. Logout");
            System.out.print("Enter your choice: ");

            String EmployeeChoice = sc.nextLine();

            switch (EmployeeChoice) {
                case "1":
                    EmployeeController.viewMyShifts(employee, ManagerController.getHRManager());
                    break;
                case "2":
                    EmployeeController.updateAvailability(employee, sc);
                    break;
                case "0":
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void EmployeeManagementDisplay(Scanner sc) {
        HRManager hr = ManagerController.getHRManager();
        System.out.println("1 - Add new employee");
        System.out.println("2 - Remove employee");
        System.out.println("3 - Add a new role to employee");
        System.out.println("4 - Edit Employee");
        System.out.println("5 - Print employee by ID");
        System.out.println("6 - Print Employees");
        System.out.print("Choose action: ");
        String empChoice = sc.nextLine();
        switch (empChoice) {
            case "1":
                System.out.println("--- Add New Employee ---");

                String id;
                while (true) {
                    System.out.print("Enter ID (9 digits): ");
                    id = sc.nextLine().trim();
                    if (!ManagerController.isNumeric(id) || id.length() != 9) {
                        System.out.println("Invalid ID. Please enter a 9-digit numeric ID.");
                    } else if (ManagerController.getHRManager().FindEmployeeByID(id) != null) {
                        System.out.println("Employee with this ID already exists.");
                    } else {
                        break;
                    }
                }

                System.out.print("Enter full name: ");
                String fullName = sc.nextLine();

                System.out.print("Enter bank account: ");
                String bankAccount = sc.nextLine();

                double salary;
                while (true) {
                    System.out.print("Enter salary: ");
                    String salaryStr = sc.nextLine();
                    if (ManagerController.isNumeric(salaryStr)) {
                        salary = Double.parseDouble(salaryStr);
                        break;
                    } else {
                        System.out.println("Invalid salary. Please enter a number.");
                    }
                }

                System.out.print("Enter the employee site: ");
                int site = Integer.parseInt(sc.nextLine());

                System.out.print("Enter employment terms: ");
                String employeeTerms = sc.nextLine();
                LicenseType licenseTypeIfNeeded = null;
                boolean isDriver = false;
                List<Role> roleQualifications = new ArrayList<>();
                System.out.println("Enter role qualifications (type 'done' to finish):");
                while (true) {
                    System.out.print("Role: ");
                    String roleName = sc.nextLine().trim();
                    if (roleName.equalsIgnoreCase("done")) break;

                    Role existingRole = null;
                    for (Role r : ManagerController.getHRManager().getAllRoles()) {
                        if (r.getRoleName().equalsIgnoreCase(roleName)) {
                            existingRole = r;
                            break;
                        }
                    }

                    if (existingRole != null) {
                        roleQualifications.add(existingRole);
                    } else {
                        System.out.println("Invalid role.");
                    }
                    isDriver = roleQualifications.stream()
                            .anyMatch(r -> r.getRoleName().equalsIgnoreCase("driver"));


                    if (isDriver) {
                        System.out.println("Enter license type for driver (B, C, C1, C2, E): ");
                        while (true) {
                            try {
                                String licenseInput = sc.nextLine().trim().toUpperCase();
                                licenseTypeIfNeeded = LicenseType.valueOf(licenseInput);
                                break;
                            } catch (IllegalArgumentException e) {
                                System.out.println("Invalid license type. Please enter one of: B, C, C1, C2, E.");
                            }
                        }
                    }

                }



                String result = ManagerController.addNewEmployee(
                        id, fullName, bankAccount, salary, site, employeeTerms,
                        roleQualifications, licenseTypeIfNeeded,isDriver);

                System.out.println(result);
                break;

            case "2":
                System.out.print("Enter employee ID to remove: ");
                String idToRemove = sc.nextLine().trim();
                Employee e = hr.FindEmployeeByID(idToRemove);
                if (e == null) {
                    System.out.print("Employee with ID " + idToRemove + " not found.");
                }
                else{
                    String resultRemove = ManagerController.removeEmployeeById(e);
                    System.out.println(resultRemove);
                }
                break;

            case "3":
                System.out.print("Enter Employee ID: ");
                String id_str = sc.nextLine().trim();

                Employee employee = ManagerController.getHRManager().FindEmployeeByID(id_str);
                if (employee == null) {
                    System.out.println("Employee with ID " + id_str + " not found.");
                    return;
                }

                System.out.println("Available roles in the system:");
                for (Role r : ManagerController.getHRManager().getAllRoles()) {
                    System.out.println("- " + r.getRoleName());
                }

                System.out.print("Enter role name to add: ");
                String roleName = sc.nextLine().trim();

                String res = ManagerController.addNewRoleToEmployee(id_str, roleName);
                System.out.println(res);
                break;

            case "4":
                ManagerController.editEmployee(sc);

                break;
            case "5":
                System.out.print("Enter Employee ID to display: ");
                String e_id = sc.nextLine();
                System.out.println(ManagerController.printEmployeeById(e_id));
                break;
            case "6":

                for (Employee emp: hr.getEmployees()) {
                    System.out.println(emp.toString());
                }
                break;
            default:
                System.out.println("Invalid action in Employee Management.");
        }
    }

    private void RoleAndShiftManagementDisplay(Scanner sc) {
        HRManager hr = ManagerController.getHRManager();
        System.out.println("1 - Add new role");
        System.out.println("2 - Create Shifts for next week");
        System.out.println("3 - Define required roles for a shift");
        System.out.println("4 - Print all valid roles");
        System.out.print("Choose action: ");
        String roleChoice = sc.nextLine();
        switch (roleChoice) {
            case "1":
                boolean existsSM = false;
                boolean existsTM = false;
                for (Role r : ManagerController.getHRManager().getAllRoles()) {
                    if (r.getRoleName().equalsIgnoreCase("shift manager")) {
                        existsSM = true;
                    }
                    if (r.getRoleName().equalsIgnoreCase("transportation manager")) {
                        existsTM = true;
                    }
                }
                if (!existsSM) {
                    ManagerController.getHRManager().addRole(new Role("shift manager"));
                }
                if (!existsTM) {
                    ManagerController.getHRManager().addRole(new Role("transportation manager"));
                }

                // Repeated role creation by user
                while (true) {
                    System.out.print("Enter new role name (or type 'done' to finish): ");
                    String roleName = sc.nextLine().trim();

                    if (roleName.equalsIgnoreCase("done")) {
                        System.out.println("---Finished adding roles---");
                        break;
                    }

                    String result = ManagerController.addNewRole(roleName);
                    System.out.println(result);
                }
                break;

            case "2":
                System.out.println(ManagerController.createShiftsForTheWeek());
                break;
            case "3":
                List<Shift> shifts = hr.getAllShifts();

                System.out.print("Enter Site ID: ");
                String input = sc.nextLine().trim();
                int siteId;
                try {
                    siteId = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid Site ID.");
                    break;
                }

                List<Shift> siteShifts = hr.getAllShifts().stream()
                        .filter(s -> s.getSiteId() == siteId)
                        .sorted(Comparator.comparing(Shift::getShiftDate).thenComparing(Shift::getShiftType))
                        .toList();

                if (siteShifts.isEmpty()) {
                    System.out.println("No shifts available for this site.");
                    break;
                }

                System.out.println("All shifts for next week:");
                for (int i = 0; i < Math.min(14, siteShifts.size()); i++) {
                    Shift s = siteShifts.get(i);
                    System.out.println((i + 1) + ". " + s.getShiftDate() + " - " + s.getShiftDay() + " - " + s.getShiftType());
                }



                int index;
                Shift selectedShift;
                while (true) {
                    System.out.print("Choose a shift to define required roles: ");
                    try {
                        index = Integer.parseInt(sc.nextLine());
                        if (index <= 0 || index > shifts.size()) {
                            System.out.println("Invalid shift index.");
                        } else {
                            selectedShift = shifts.get(index - 1);
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid number.");
                    }
                }

                List<String> existingRoleNames = RoleService.getAllRoleNames();

                Map<String, Integer> requiredRoles = new HashMap<>();
                System.out.println("Enter required roles for this shift (type 'done' to finish):");

                while (true) {
                    System.out.print("Role name: ");
                    String roleName = sc.nextLine().trim();

                    if (roleName.equalsIgnoreCase("done")) break;

                    if (!existingRoleNames.contains(roleName.toLowerCase())) {
                        System.out.println("Role not found. Please enter a valid existing role.");
                        continue;
                    }

                    while (true) {
                        System.out.print("Required number of employees: ");
                        try {
                            int count = Integer.parseInt(sc.nextLine());
                            if (count < 1) {
                                System.out.println("Count must be at least 1.");
                                continue;
                            }
                            requiredRoles.put(roleName, count);
                            break;
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number.");
                        }
                    }
                }

                if (!requiredRoles.isEmpty()) {
                    try {
                        ManagerController.defineRolesForSpecificShift(
                                selectedShift.getShiftDay(),
                                selectedShift.getShiftType(),
                                requiredRoles,
                                siteId
                        );
                        System.out.println("Roles defined successfully for the selected shift.");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error: " + e.getMessage());
                    } catch (Exception e) {
                        System.out.println("Unexpected error: " + e.getMessage());
                    }
                } else {
                    System.out.println("No roles were defined.");
                }
                break;


            case "4":
                hr.printRoles();
                break;
            default:
                System.out.println("Invalid action in Role and Shift Management.");
        }
    }

    private void ShiftAssignmentDisplay(Scanner sc) {
        while (true) {
            System.out.println("\n--- Shift Assignment Menu ---");
            System.out.println("1 - Assign employee to shift");
            System.out.println("2 - Remove employee from shift");
            System.out.println("3 - View assigned shifts");
            System.out.println("0 - Back to HR Manager Menu");
            System.out.print("Choose action: ");

            String assignChoice = sc.nextLine();

            switch (assignChoice) {
                case "1":
                    ManagerController.assignEmployeeToShift(sc);
                    break;
                case "2":
                    ManagerController.removeEmployeeFromShift(sc);
                    break;
                case "3":
                    List<String> assignedShiftDetails = ManagerController.viewAssignedShifts();
                    for (String line : assignedShiftDetails) {
                        System.out.println(line);
                    }
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid action in Shift Assignment. Please try again.");
            }
        }
    }

    private void archiveDisplay(Scanner sc) {
        while (true) {
            System.out.println("\n--- Archive Menu ---");
            System.out.println("1 - Display archive shifts");
            System.out.println("2 - Display archive employees");
            System.out.println("0 - Back to HR Manager Menu");
            String assignChoice = sc.nextLine();

            switch (assignChoice) {
                case "1":
                    List<ArchivedShiftDTO> archivedShifts = ArchiveController.getAllArchivedShifts();
                    if (archivedShifts.isEmpty()) {
                        System.out.println("No archived shifts available.");
                    } else {
                        System.out.println("--- Archived Shifts ---");
                        for (ArchivedShiftDTO s : archivedShifts) {
                            System.out.println(s.getDate() + " - " + s.getDayOfWeek() + " - " + s.getShiftType());
                        }
                    }
                    break;
                case "2":
                    List<ArchivedEmployeeDTO> archivedEmployees = ArchiveController.getAllArchivedEmployees();
                    if (archivedEmployees.isEmpty()) {
                        System.out.println("No archived employees available.");
                    } else {
                        System.out.println("--- Archived Employees ---");
                        for (ArchivedEmployeeDTO e : archivedEmployees) {
                            System.out.println("Name: " + e.getFullName() + ", ID: " + e.getEmployeeId());
                        }
                    }
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid action. Please try again.");
            }
        }
    }
}