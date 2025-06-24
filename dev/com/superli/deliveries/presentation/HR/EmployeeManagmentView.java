package com.superli.deliveries.presentation;

import com.superli.deliveries.domain.core.Employee;

public class EmployeeManagmentView {

    public void promptEmployeeId() {
        System.out.print("Enter Employee ID to edit (or type 'exit' to cancel): ");
    }

    public void printReturningToMenu() {
        System.out.println("Returning to Manager Menu...");
    }

    public void printEmployeeNotFound(String id) {
        System.out.println("Employee with ID " + id + " not found. Please try again.");
    }

    public void printEditingHeader(Employee employee) {
        System.out.println("--- Editing Employee: " + employee.getFullName() + " ---");
    }

    public void printEditMenu() {
        System.out.println("\nSelect field to edit:");
        System.out.println("1. Full Name");
        System.out.println("2. Bank Account");
        System.out.println("3. Salary");
        System.out.println("4. Role Qualifications");
        System.out.println("0. Return to Manager Menu");
        System.out.print("Choice: ");
    }

    public void promptFullName() {
        System.out.print("Enter new full name: ");
    }

    public void promptBankAccount() {
        System.out.print("Enter new bank account: ");
    }

    public void promptSalary() {
        System.out.print("Enter new salary: ");
    }

    public void promptRoleName() {
        System.out.print("Enter role name (or 'done' to finish): ");
    }

    public void printRoleNotFound(String roleName) {
        System.out.println("Role '" + roleName + "' not found in DB.");
    }

    public void printInvalidChoice() {
        System.out.println("Invalid choice. Please try again.");
    }

    public void printUpdateError(String message) {
        System.err.println("Failed to update employee in DB: " + message);
    }

    public void printDatabaseError(String message) {
        System.err.println("Database error: " + message);
    }
}
