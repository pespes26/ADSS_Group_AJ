package com.superli.deliveries.controllers;

import com.superli.deliveries.domain.Driver;
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
            System.out.println("0. Back to main menu");
            System.out.print("Your choice: ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1" -> showAllDrivers();
                case "2" -> showAvailableDrivers();
                case "0" -> {
                    System.out.println("Returning to main menu...");
                    return;
                }
                default -> System.out.println("❌ Invalid option. Try again.");
            }
        }
    }

    private void showAllDrivers() {
        List<Driver> drivers = driverService.getAllDrivers();
        if (drivers.isEmpty()) {
            System.out.println("No drivers found.");
        } else {
            drivers.forEach(System.out::println);
        }
    }

    private void showAvailableDrivers() {
        List<Driver> availableDrivers = driverService.getAvailableDrivers();
        if (availableDrivers.isEmpty()) {
            System.out.println("No available drivers at the moment.");
        } else {
            availableDrivers.forEach(System.out::println);
        }
    }
}
