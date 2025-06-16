package com.superli.deliveries.application.controllers;

import com.superli.deliveries.application.services.DriverService;
import com.superli.deliveries.domain.core.*;
import com.superli.deliveries.application.services.EmployeeService;

import java.util.*;

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
            System.out.println("4. Edit driver details");
            System.out.println("5. Remove driver by ID");
            System.out.println("6. Mark driver as unavailable");
            System.out.println("7. Mark driver as available");
            System.out.println("0. Back to main menu");
            System.out.print("Your choice: ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1" -> showAllDrivers();
                case "2" -> showAvailableDrivers();
                case "3" -> addDriver();
                case "4" -> editDriver();
                case "5" -> removeDriver();
                case "6" -> markDriverUnavailable();
                case "7" -> markDriverAvailable();
                case "0" -> {
                    System.out.println("Returning to main menu...");
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    public void showAllDrivers() {
        List<Driver> drivers = driverService.getAllDrivers();
        if (drivers.isEmpty()) {
            System.out.println("No drivers found.");
            return;
        }
        System.out.println("\nAll Drivers:");
        for (Driver driver : drivers) {
            boolean isAssigned = driverService.isDriverAssignedToTransport(driver.getDriverId());
            String status = (driver.isAvailable() && !isAssigned) ? "Available" : "Unavailable";
            System.out.printf("ID: %s | Name: %s | License: %s | Status: %s\n",
                driver.getDriverId(), driver.getFullName(), driver.getLicenseType(), status);
        }
    }

    private void showAvailableDrivers() {
        List<Driver> availableDrivers = driverService.getAvailableDrivers();
        if (availableDrivers.isEmpty()) {
            System.out.println("No available drivers at the moment.");
        } else {
            for (Driver driver : availableDrivers) {
                printDriver(driver);
            }
        }
    }

    private void printDriver(Driver driver) {
        System.out.printf("ID: %-4s | Name: %-15s | License: %-2s | Status: %-10s%n",
            driver.getDriverId(),
            driver.getFullName(),
            driver.getLicenseType(),
            driver.isAvailable() ? "Available" : "Unavailable");
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

            System.out.println("Available license types:");
            for (LicenseType type : LicenseType.values()) {
                System.out.println("- " + type.getValue());
            }

            LicenseType selectedLicenseType = null;
            while (selectedLicenseType == null) {
                System.out.print("Enter license type: ");
                String licenseInput = scanner.nextLine().trim().toUpperCase();
                try {
                    selectedLicenseType = LicenseType.valueOf(licenseInput);
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid license type. Try again.");
                }
            }

            Driver driver = new Driver(
                    id,
                    name,
                    "000-000-000",
                    0.0,
                    -1,
                    "Standard Terms",
                    new Date(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new Role("Driver"),
                    selectedLicenseType
            );

            driverService.saveDriver(driver);
            System.out.println("Driver added successfully.");
        } catch (Exception e) {
            System.out.println("Error adding driver: " + e.getMessage());
        }
    }

    private void editDriver() {
        System.out.print("Enter driver ID to edit: ");
        String id = scanner.nextLine().trim();
        Optional<Driver> driverOpt = driverService.getDriverById(id);
        if (driverOpt.isEmpty()) {
            System.out.println("Driver not found.");
            return;
        }
        Driver driver = driverOpt.get();

        System.out.println("1. Edit name\n2. Edit license type\n3. Toggle availability\n0. Cancel");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1" -> {
                System.out.print("Enter new name: ");
                String newName = scanner.nextLine().trim();
                driver.setFullName(newName);
                System.out.println("Name updated.");
            }
            case "2" -> editDriverLicenseType(driver);
            case "3" -> {
                boolean newStatus = !driver.isAvailable();
                driverService.updateDriverAvailability(driver.getDriverId(), newStatus);
                System.out.println("Availability updated.");
            }
            case "0" -> System.out.println("Edit cancelled.");
            default -> System.out.println("Invalid choice.");
        }
    }

    private void editDriverLicenseType(Driver driver) {
        System.out.println("Current license type: " + driver.getLicenseType());
        System.out.print("Enter new license type: ");
        String input = scanner.nextLine().trim().toUpperCase();
        try {
            LicenseType newType = LicenseType.valueOf(input);
            driverService.updateDriverLicenseType(driver.getDriverId(), newType);
            System.out.println("License type updated.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid license type.");
        }
    }

    private void removeDriver() {
        System.out.print("Enter driver ID to remove: ");
        String id = scanner.nextLine().trim();
        if (driverService.getDriverById(id).isEmpty()) {
            System.out.println("Driver not found.");
        } else {
            driverService.deleteDriver(id);
            System.out.println("Driver removed successfully.");
        }
    }

    private void markDriverUnavailable() {
        System.out.println("\nAvailable Drivers:");
        List<Driver> availableDrivers = driverService.getAvailableDrivers();
        if (availableDrivers.isEmpty()) {
            System.out.println("No available drivers at the moment.");
            return;
        }
        for (Driver driver : availableDrivers) {
            printDriver(driver);
        }
        System.out.print("\nEnter driver ID to mark as unavailable: ");
        String id = scanner.nextLine().trim();
        if (driverService.getDriverById(id).isEmpty()) {
            System.out.println("Driver not found.");
        } else {
            driverService.markDriverAsUnavailable(id);
            System.out.println("Driver marked as unavailable.");
        }
    }

    private void markDriverAvailable() {
        System.out.println("\nUnavailable Drivers:");
        List<Driver> unavailableDrivers = driverService.getUnavailableDrivers();
        if (unavailableDrivers.isEmpty()) {
            System.out.println("No unavailable drivers at the moment.");
            return;
        }
        for (Driver driver : unavailableDrivers) {
            printDriver(driver);
        }
        System.out.print("\nEnter driver ID to mark as available: ");
        String id = scanner.nextLine().trim();
        if (driverService.getDriverById(id).isEmpty()) {
            System.out.println("Driver not found.");
        } else {
            try {
                driverService.markDriverAsAvailable(id);
                System.out.println("Driver marked as available.");
            } catch (IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
                System.out.println("Please complete or cancel the driver's current transport first.");
            }
        }
    }
}

