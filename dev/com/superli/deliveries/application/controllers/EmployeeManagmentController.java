package com.superli.deliveries.application.controllers;
import com.superli.deliveries.domain.core.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class EmployeeManagmentController {
 public static String addNewEmployee(String id, String fullName, String bankAccount, double salary,
                                      String employeeTerms, List<Role> roleQualifications, LicenseType licenseTypeIfNeeded, boolean isDriver) {
    HRManager hr = ManagerController.getHRManager();

    Date employeeStartDate = new Date();
    List<AvailableShifts> availabilityConstraints = new ArrayList<>();
    Role loginRole = new Role("");

    Employee newEmployee;

    if (isDriver) {
        if (licenseTypeIfNeeded == null) {
            return "License type is required for driver.";
        }

        newEmployee = new Driver(id, fullName, bankAccount, salary, employeeTerms,
                employeeStartDate, roleQualifications, availabilityConstraints, loginRole, licenseTypeIfNeeded);
    } else {
        newEmployee = new Employee(id, fullName, bankAccount, salary, employeeTerms,
                employeeStartDate, roleQualifications, availabilityConstraints, loginRole);
    }

    boolean addedSuccessfully = hr.addEmployee(newEmployee);

    return addedSuccessfully
            ? (isDriver ? "Driver added successfully." : "Employee added successfully.")
            : "Failed to add employee.";
}

    public static String removeEmployeeById (Employee e){
        HRManager hr = ManagerController.getHRManager();

        hr.removeAndArchiveEmployee(e);
        return "Employee with ID " + e.getId() + " removed and archived.";
    }

    public static void printEmployeeById(Scanner sc){
        HRManager hr = ManagerController.getHRManager();
        System.out.print("Enter Employee ID to display: ");
        String id = sc.nextLine();

        Employee employee = hr.FindEmployeeByID(id);

        if (employee == null) {
            System.out.println("Employee with ID " + id + " not found.");
            return;
        }
        employee.printEmployee();

    }

    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;

        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String addNewRoleToEmployee(String id_str, String roleName) {
        HRManager hr = ManagerController.getHRManager();
        Employee employee = hr.FindEmployeeByID(id_str);

        if (employee == null) {
            return "Employee with ID " + id_str + " not found.";
        }

        Role foundRole = null;
        for (Role role : hr.getAllRoles()) {
            if (role.getRoleName().equalsIgnoreCase(roleName)) {
                foundRole = role;
                break;
            }
        }

        if (foundRole == null) {
            return "Role '" + roleName + "' not found in the system.";
        }

        if (employee.getRoleQualifications().contains(foundRole)) {
            return "Employee already has the role '" + roleName + "'.";
        } else {
            employee.addRoleQualification(foundRole);
            return "Role '" + roleName + "' added to employee successfully.";
        }
    }


    public static void editEmployee(Scanner sc) {
        HRManager hr = ManagerController.getHRManager();

        Employee employee = null;
        while (employee == null) {
            System.out.print("Enter Employee ID to edit (or type 'exit' to cancel): ");
            String id = sc.nextLine().trim();

            if (id.equalsIgnoreCase("exit")) {
                System.out.println("Returning to Manager Menu...");
                return;
            }

            employee = hr.FindEmployeeByID(id);

            if (employee == null) {
                System.out.println("Employee with ID " + id + " not found. Please try again.");
            }
        }

        System.out.println("--- Editing Employee: " + employee.getFullName() + " ---");

        boolean editing = true;
        while (editing) {
            System.out.println("\nSelect field to edit:");
            System.out.println("1. Full Name");
            System.out.println("2. Bank Account");
            System.out.println("3. Salary");
            System.out.println("4. Role Qualifications");
            System.out.println("0. Return to Manager Menu");
            System.out.print("Choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.print("Enter new full name: ");
                    String fullName = sc.nextLine().trim();
                    if (!fullName.isEmpty()) {
                        employee.setFullName(fullName);
                        System.out.println("Full name updated successfully.");
                    }
                    break;
                case "2":
                    System.out.print("Enter new bank account: ");
                    String bankAccount = sc.nextLine().trim();
                    if (!bankAccount.isEmpty()) {
                        employee.setBankAccount(bankAccount);
                        System.out.println("Bank account updated successfully.");
                    }
                    break;
                case "3":
                    System.out.print("Enter new salary: ");
                    String salaryStr = sc.nextLine().trim();
                    if (isNumeric(salaryStr)) {
                        double salary = Double.parseDouble(salaryStr);
                        employee.setSalary(salary);
                        System.out.println("Salary updated successfully.");
                    } else {
                        System.out.println("Invalid salary input. Must be a number.");
                    }
                    break;
                case "4":
                    System.out.println("Current role qualifications:");
                    for (Role role : employee.getRoleQualifications()) {
                        System.out.println("- " + role.getRoleName());
                    }
                    System.out.println("Do you want to:");
                    System.out.println("1. Replace all qualifications");
                    System.out.println("2. Add a new role");
                    System.out.print("Choice: ");
                    String roleChoice = sc.nextLine().trim();

                    if (roleChoice.equals("1")) {
                        List<Role> newQualifications = new ArrayList<>();
                        while (true) {
                            System.out.print("Enter role name (or 'done' to finish): ");
                            String roleName = sc.nextLine().trim();
                            if (roleName.equalsIgnoreCase("done")) {
                                break;
                            }
                            newQualifications.add(new Role(roleName));
                        }
                        employee.setRoleQualifications(newQualifications);
                        System.out.println("Role qualifications replaced successfully.");
                    } else if (roleChoice.equals("2")) {
                        System.out.print("Enter new role name to add: ");
                        String newRoleName = sc.nextLine().trim();
                        if (!newRoleName.isEmpty()) {
                            employee.getRoleQualifications().add(new Role(newRoleName));
                            System.out.println("New role added successfully.");
                        }
                    } else {
                        System.out.println("Invalid choice.");
                    }
                    break;
                case "0":
                    System.out.println("Returning to Manager Main Menu...");
                    editing = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
