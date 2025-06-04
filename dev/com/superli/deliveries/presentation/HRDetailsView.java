package com.superli.deliveries.presentation;

import com.superli.deliveries.domain.core.*;
import com.superli.deliveries.application.controllers.*;
import java.util.Scanner;

public class HRDetailsView {

    public HRDetailsView() {
    }

    public void mainLoginMenu() {
        Scanner sc = new Scanner(System.in);

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
                    System.exit(0);
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
                ManagerController.addNewEmployee(sc);
                break;
            case "2":
                ManagerController.removeEmployeeById(sc);
                break;
            case "3":
                ManagerController.addNewRoleToEmployee(sc);
                break;
            case "4":
                ManagerController.editEmployee(sc);
                break;
            case "5":
                ManagerController.printEmployeeById(sc);
                break;
            case "6":
                hr.printEmployees();
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
                ManagerController.addNewRole(sc);
                break;
            case "2":
                ManagerController.createShiftsForTheWeek(sc);
                break;
            case "3":
                ManagerController.defineRolesForSpecificShift(sc);
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
                    ManagerController.viewAssignedShifts();
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
                    ManagerController.displayArchivedShifts();
                    break;
                case "2":
                    ManagerController.displayArchivedEmployees();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid action. Please try again.");
            }
        }
    }
}