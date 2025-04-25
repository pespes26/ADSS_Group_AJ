package com.superli.deliveries.controllers;

import com.superli.deliveries.domain.Driver;
import com.superli.deliveries.domain.LicenseType;
import com.superli.deliveries.service.DriverService;

import java.util.List;
import java.util.Scanner;

/**
 * Controller that manages driver-related operations in a console-based interface.
 */
public class DriverController {

    private final DriverService driverService;
    private final Scanner scanner;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
        this.scanner = new Scanner(System.in);
    }

    public void runMenu() {
        while (true) {
            System.out.println("\n=== Driver Menu ===");
            System.out.println("1. Show all drivers");
            System.out.println("2. Show available drivers");
            System.out.println("3. Add new driver");
            System.out.println("4. Remove driver by ID");
            System.out.println("5. Mark driver as unavailable");
            System.out.println("6. Mark driver as available");
            System.out.println("0. Back to main menu");
            System.out.print("Your choice: ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1" -> showAllDrivers();
                case "2" -> showAvailableDrivers();
                case "3" -> addDriver();
                case "4" -> removeDriver();
                case "5" -> markDriverUnavailable();
                case "6" -> markDriverAvailable();
                case "0" -> {
                    System.out.println("Returning to main menu...");
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    private void showAllDrivers() {
        List<Driver> drivers = driverService.getAllDrivers();
        if (drivers.isEmpty()) {
            System.out.println("No drivers found.");
        } else {
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║            ALL DRIVERS             ║");
            System.out.println("╚════════════════════════════════════╝");

            for (int i = 0; i < drivers.size(); i++) {
                Driver driver = drivers.get(i);
                System.out.println("\n[" + (i+1) + "] Driver: " + driver.getName() + " (ID: " + driver.getDriverId() + ")");
                System.out.println("    License Type: " + driver.getLicenseType());
                System.out.println("    Status: " + (driver.isAvailable() ? "Available" : "Not Available"));
                System.out.println("    " + "-".repeat(40));
            }
        }
    }

    private void showAvailableDrivers() {
        List<Driver> availableDrivers = driverService.getAvailableDrivers();
        if (availableDrivers.isEmpty()) {
            System.out.println("No available drivers at the moment.");
        } else {
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║         AVAILABLE DRIVERS          ║");
            System.out.println("╚════════════════════════════════════╝");

            for (int i = 0; i < availableDrivers.size(); i++) {
                Driver driver = availableDrivers.get(i);
                System.out.println("\n[" + (i+1) + "] DRIVER: " + driver.getName() + " (ID: " + driver.getDriverId() + ")");
                System.out.println("    License Type: " + driver.getLicenseType());
                System.out.println("    " + "-".repeat(40));
            }
        }
    }

    private void addDriver() {
        try {
            System.out.print("Enter driver ID: ");
            String id = scanner.nextLine().trim();

            if (driverService.getDriverById(id).isPresent()) {
                System.out.println("Driver with this ID already exists.");
                return;
            }

            System.out.print("Enter name: ");
            String name = scanner.nextLine().trim();

            // Display available license types
            System.out.println("Available license types:");
            for (LicenseType type : LicenseType.values()) {
                System.out.println("- " + type.getValue());
            }

            LicenseType selectedLicenseType = null;
            while (selectedLicenseType == null) {
                System.out.print("Enter license type (");
                for (LicenseType type : LicenseType.values()) {
                    System.out.print(type.getValue() + " ");
                }
                System.out.print("): ");
                String licenseInput = scanner.nextLine().trim().toUpperCase();

                try {
                    selectedLicenseType = LicenseType.valueOf(licenseInput);
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid license type. Please select from the list.");
                    continue;
                }
            }

            Driver driver = new Driver(id, name, selectedLicenseType, true);
            driverService.saveDriver(driver);
            System.out.println("Driver added successfully.");
        } catch (Exception e) {
            System.out.println("Error adding driver: " + e.getMessage());
        }
    }

    private void removeDriver() {
        System.out.print("Enter driver ID to remove: ");
        String id = scanner.nextLine().trim();
        if (driverService.getDriverById(id).isEmpty()) {
            System.out.println("Driver not found.");
            return;
        }
        driverService.deleteDriver(id);
        System.out.println("Driver removed successfully.");
    }

    private void markDriverUnavailable() {
        System.out.print("Enter driver ID to mark as unavailable: ");
        String id = scanner.nextLine().trim();
        if (driverService.getDriverById(id).isEmpty()) {
            System.out.println("Driver not found.");
        } else {
            driverService.markDriverAsUnavailable(id);
            System.out.println("Driver marked as unavailable.");
        }
    }

    private void markDriverAvailable() {
        System.out.print("Enter driver ID to mark as available: ");
        String id = scanner.nextLine().trim();
        if (driverService.getDriverById(id).isEmpty()) {
            System.out.println("Driver not found.");
        } else {
            driverService.markDriverAsAvailable(id);
            System.out.println("Driver marked as available.");
        }
    }
}