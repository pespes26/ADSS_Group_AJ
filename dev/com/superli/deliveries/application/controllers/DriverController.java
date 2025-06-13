package com.superli.deliveries.application.controllers;

import com.superli.deliveries.application.services.DriverService;
import com.superli.deliveries.domain.core.*;

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

    private void showAllDrivers() {
        List<Driver> drivers = driverService.getAllDrivers();
        if (drivers.isEmpty()) {
            System.out.println("No drivers found.");
        } else {
            for (Driver driver : drivers) {
                printDriver(driver);
            }
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
        System.out.println("Driver: " + driver.getFullName() + " (ID: " + driver.getDriverId() + ")");
        System.out.println("License Type: " + driver.getLicenseType());
        System.out.println("Status: " + (driver.isAvailable() ? "Available" : "Not Available"));
        System.out.println("----------------------------------------");
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
        System.out.print("Enter driver ID: ");
        String id = scanner.nextLine().trim();
        driverService.markDriverAsUnavailable(id);
        System.out.println("Driver marked as unavailable.");
    }

    private void markDriverAvailable() {
        System.out.print("Enter driver ID: ");
        String id = scanner.nextLine().trim();
        driverService.markDriverAsAvailable(id);
        System.out.println("Driver marked as available.");
    }
}



//package com.superli.deliveries.application.controllers;
//
//import com.superli.deliveries.application.services.*;
//import com.superli.deliveries.domain.core.*;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.Scanner;
//
///**
// * Controller that manages driver-related operations in a console-based interface.
// */
//public class DriverController {
//
//    private final DriverService driverService;
//    private final Scanner scanner;
//
//    public DriverController(DriverService driverService) {
//        this.driverService = driverService;
//        this.scanner = new Scanner(System.in);
//    }
//
//    public void runMenu() {
//        while (true) {
//            System.out.println("\n=== Driver Menu ===");
//            System.out.println("1. Show all drivers");
//            System.out.println("2. Show available drivers");
//            System.out.println("3. Add new driver");
//            System.out.println("4. Edit driver details");
//            System.out.println("5. Remove driver by ID");
//            System.out.println("6. Mark driver as unavailable");
//            System.out.println("7. Mark driver as available");
//            System.out.println("0. Back to main menu");
//            System.out.print("Your choice: ");
//
//            String input = scanner.nextLine().trim();
//            switch (input) {
//                case "1" -> showAllDrivers();
//                case "2" -> showAvailableDrivers();
//                case "3" -> addDriver();
//                case "4" -> editDriver();
//                case "5" -> removeDriver();
//                case "6" -> markDriverUnavailable();
//                case "7" -> markDriverAvailable();
//                case "0" -> {
//                    System.out.println("Returning to main menu...");
//                    return;
//                }
//                default -> System.out.println("Invalid option. Try again.");
//            }
//        }
//    }
//
//    private void showAllDrivers() {
//        List<Driver> drivers = driverService.getAllDrivers();
//        if (drivers.isEmpty()) {
//            System.out.println("No drivers found.");
//        } else {
//            System.out.println("\n╔════════════════════════════════════╗");
//            System.out.println("║            ALL DRIVERS             ║");
//            System.out.println("╚════════════════════════════════════╝");
//
//            for (int i = 0; i < drivers.size(); i++) {
//                Driver driver = drivers.get(i);
//                System.out.println("\n[" + (i+1) + "] DRIVER: " + driver.getFullName() + " (ID: " + driver.getDriverId() + ")");
//                System.out.println("    License Type: " + driver.getLicenseType());
//                System.out.println("    Status: " + (driver.isAvailable() ? "Available" : "Not Available"));
//                System.out.println("    " + "-".repeat(40));
//            }
//        }
//    }
//
//    private void showAvailableDrivers() {
//        List<Driver> availableDrivers = driverService.getAvailableDrivers();
//        if (availableDrivers.isEmpty()) {
//            System.out.println("No available drivers at the moment.");
//        } else {
//            System.out.println("\n╔════════════════════════════════════╗");
//            System.out.println("║         AVAILABLE DRIVERS          ║");
//            System.out.println("╚════════════════════════════════════╝");
//
//            for (int i = 0; i < availableDrivers.size(); i++) {
//                Driver driver = availableDrivers.get(i);
//                System.out.println("\n[" + (i+1) + "] DRIVER: " + driver.getFullName() + " (ID: " + driver.getDriverId() + ")");
//                System.out.println("    License Type: " + driver.getLicenseType());
//                System.out.println("    " + "-".repeat(40));
//            }
//        }
//    }
//
//    private void addDriver() {
//        try {
//            System.out.print("Enter driver ID: ");
//            String id = scanner.nextLine().trim();
//
//            if (driverService.getDriverById(id).isPresent()) {
//                System.out.println("Driver with this ID already exists.");
//                return;
//            }
//
//            System.out.print("Enter name: ");
//            String name = scanner.nextLine().trim();
//
//            // Display available license types
//            System.out.println("Available license types:");
//            for (LicenseType type : LicenseType.values()) {
//                System.out.println("- " + type.getValue());
//            }
//
//            LicenseType selectedLicenseType = null;
//            while (selectedLicenseType == null) {
//                System.out.print("Enter license type (");
//                for (LicenseType type : LicenseType.values()) {
//                    System.out.print(type.getValue() + " ");
//                }
//                System.out.print("): ");
//                String licenseInput = scanner.nextLine().trim().toUpperCase();
//
//                try {
//                    selectedLicenseType = LicenseType.valueOf(licenseInput);
//                } catch (IllegalArgumentException e) {
//                    System.out.println("Invalid license type. Please select from the list.");
//                    continue;
//                }
//            }
//
//            Driver driver = new Driver(id, name, selectedLicenseType, true);
//            driverService.saveDriver(driver);
//            System.out.println("Driver added successfully.");
//        } catch (Exception e) {
//            System.out.println("Error adding driver: " + e.getMessage());
//        }
//    }
//
//    private void editDriver() {
//        System.out.println("\n╔════════════════════════════════════╗");
//        System.out.println("║          EDIT DRIVER INFO          ║");
//        System.out.println("╚════════════════════════════════════╝");
//
//        // First, show all drivers and ask for the ID to edit
//        showAllDrivers();
//
//        System.out.print("\nEnter the ID of the driver you want to edit: ");
//        String driverId = scanner.nextLine().trim();
//
//        Optional<Driver> driverOpt = driverService.getDriverById(driverId);
//        if (driverOpt.isEmpty()) {
//            System.out.println("Driver not found. Please try again.");
//            return;
//        }
//
//        Driver driver = driverOpt.get();
//
//        // Display current info
//        System.out.println("\nCurrent Driver Information:");
//        System.out.println("ID: " + driver.getDriverId());
//        System.out.println("Name: " + driver.getFullName());
//        System.out.println("License Type: " + driver.getLicenseType());
//        System.out.println("Status: " + (driver.isAvailable() ? "Available" : "Not Available"));
//
//        // Edit menu
//        System.out.println("\nWhat would you like to edit?");
//        System.out.println("1. Name");
//        System.out.println("2. License Type");
//        System.out.println("3. Availability Status");
//        System.out.println("0. Cancel");
//        System.out.print("Your choice: ");
//
//        String choice = scanner.nextLine().trim();
//
//        switch (choice) {
//            // case "1" -> editDriverName(driver);
//            case "1" -> editDriverLicenseType(driver);
//            case "2" -> editDriverAvailability(driver);
//            case "0" -> System.out.println("Edit cancelled.");
//            default -> System.out.println("Invalid choice. Edit cancelled.");
//        }
//    }
//
//    // private void editDriverName(Driver driver) {
//    //     System.out.println("\nCurrent name: " + driver.getFullName());
//    //     System.out.print("Enter new name (or press Enter to keep current): ");
//    //     String newName = scanner.nextLine().trim();
//
//    //     if (newName.isEmpty()) {
//    //         System.out.println("Name not changed.");
//    //         return;
//    //     }
//
//    //     boolean success = driverService.updateDriverName(driver.getDriverId(), newName);
//    //     if (success) {
//    //         System.out.println("Name updated successfully to: " + newName);
//    //     } else {
//    //         System.out.println("Failed to update name. Please try again.");
//    //     }
//    // }
//
//    private void editDriverLicenseType(Driver driver) {
//        System.out.println("\nCurrent license type: " + driver.getLicenseType());
//
//        // Display available license types
//        System.out.println("Available license types:");
//        for (LicenseType type : LicenseType.values()) {
//            System.out.println("- " + type.getValue());
//        }
//
//        System.out.print("Enter new license type (or press Enter to keep current): ");
//        String licenseInput = scanner.nextLine().trim().toUpperCase();
//
//        if (licenseInput.isEmpty()) {
//            System.out.println("License type not changed.");
//            return;
//        }
//
//        try {
//            LicenseType newLicenseType = LicenseType.valueOf(licenseInput);
//            boolean success = driverService.updateDriverLicenseType(driver.getDriverId(), newLicenseType);
//
//            if (success) {
//                System.out.println("License type updated successfully to: " + newLicenseType);
//            } else {
//                System.out.println("Failed to update license type. Please try again.");
//            }
//        } catch (IllegalArgumentException e) {
//            System.out.println("Invalid license type. No changes made.");
//        }
//    }
//
//    private void editDriverAvailability(Driver driver) {
//        System.out.println("\nCurrent availability status: " + (driver.isAvailable() ? "Available" : "Not Available"));
//        System.out.print("Enter new status (A for Available, N for Not Available, or press Enter to keep current): ");
//        String input = scanner.nextLine().trim().toUpperCase();
//
//        if (input.isEmpty()) {
//            System.out.println("Availability status not changed.");
//            return;
//        }
//
//        if (input.equals("A") || input.equals("AVAILABLE")) {
//            boolean success = driverService.updateDriverAvailability(driver.getDriverId(), true);
//            if (success) {
//                System.out.println("Driver marked as Available.");
//            } else {
//                System.out.println("Failed to update availability status. Please try again.");
//            }
//        } else if (input.equals("N") || input.equals("NOT AVAILABLE")) {
//            boolean success = driverService.updateDriverAvailability(driver.getDriverId(), false);
//            if (success) {
//                System.out.println("Driver marked as Not Available.");
//            } else {
//                System.out.println("Failed to update availability status. Please try again.");
//            }
//        } else {
//            System.out.println("Invalid input. No changes made.");
//        }
//    }
//
//    private void removeDriver() {
//        System.out.print("Enter driver ID to remove: ");
//        String id = scanner.nextLine().trim();
//        if (driverService.getDriverById(id).isEmpty()) {
//            System.out.println("Driver not found.");
//            return;
//        }
//        driverService.deleteDriver(id);
//        System.out.println("Driver removed successfully.");
//    }
//
//    private void markDriverUnavailable() {
//        System.out.print("Enter driver ID to mark as unavailable: ");
//        String id = scanner.nextLine().trim();
//        if (driverService.getDriverById(id).isEmpty()) {
//            System.out.println("Driver not found.");
//        } else {
//            driverService.markDriverAsUnavailable(id);
//            System.out.println("Driver marked as unavailable.");
//        }
//    }
//
//    private void markDriverAvailable() {
//        System.out.print("Enter driver ID to mark as available: ");
//        String id = scanner.nextLine().trim();
//        if (driverService.getDriverById(id).isEmpty()) {
//            System.out.println("Driver not found.");
//        } else {
//            driverService.markDriverAsAvailable(id);
//            System.out.println("Driver marked as available.");
//        }
//    }
//}