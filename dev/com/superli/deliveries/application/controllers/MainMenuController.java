package com.superli.deliveries.application.controllers;

import com.superli.deliveries.application.services.*;
import com.superli.deliveries.domain.core.*;
import java.util.Scanner;

public class MainMenuController {

    private final TransportController transportController;
    private final DriverController driverController;
    private final TruckController truckController;
    private final SiteController siteController;
    private final ZoneController zoneController;
    private final DestinationDocController destinationDocController;

    private final Scanner scanner;

    public MainMenuController(TransportController transportController,
                              DriverController driverController,
                              TruckController truckController,
                              SiteController siteController,
                              ZoneController zoneController,
                              DestinationDocController destinationDocController,
                              Scanner scanner) {
        this.transportController = transportController;
        this.driverController = driverController;
        this.truckController = truckController;
        this.siteController = siteController;
        this.zoneController = zoneController;
        this.destinationDocController = destinationDocController;
        this.scanner = scanner;
    }

    public void run() {
        while (true) {
            if (Main.isSystemAdmin()) {
                showAdminMenu();
            } else if (Main.isTransportManager()) {
                showTransportManagerMenu();
            } else {
                System.out.println("Permission error. Terminating program.");
                return;
            }

            String input = scanner.nextLine().trim();

            if (Main.isSystemAdmin()) {
                handleAdminChoice(input);
            } else if (Main.isTransportManager()) {
                handleTransportManagerChoice(input);
            }
        }
    }

    private void showAdminMenu() {
        System.out.println("\n=== Admin Menu ===");
        System.out.println("1. Manage Transports");
        System.out.println("2. Manage Drivers");
        System.out.println("3. Manage Trucks");
        System.out.println("4. Manage Sites");
        System.out.println("5. Manage Zones");
        System.out.println("6. Destination Documents");
        System.out.println("0. Exit");
        System.out.print("Your choice: ");
    }

    private void showTransportManagerMenu() {
        System.out.println("\n=== Transport Manager Menu ===");
        System.out.println("1. Manage Transports");
        System.out.println("2. Manage Drivers");
        System.out.println("3. Manage Trucks");
        System.out.println("4. Destination Documents");
        System.out.println("0. Exit");
        System.out.print("Your choice: ");
    }

    private void handleAdminChoice(String input) {
        switch (input) {
            case "1" -> transportController.runMenu();
            case "2" -> driverController.runMenu();
            case "3" -> truckController.runMenu();
            case "4" -> siteController.runMenu();
            case "5" -> zoneController.runMenu();
            case "6" -> destinationDocController.runMenu();
            case "0" -> {
                System.out.println("Exiting the system. Goodbye!");
                System.exit(0);
            }
            default -> System.out.println("Invalid choice. Please try again.");
        }
    }

    private void handleTransportManagerChoice(String input) {
        switch (input) {
            case "1" -> transportController.runMenu();
            case "2" -> driverController.runMenu();
            case "3" -> truckController.runMenu();
            case "4" -> destinationDocController.runMenu();
            case "0" -> {
                System.out.println("Exiting the system. Goodbye!");
                System.exit(0);
            }
            default -> System.out.println("Invalid choice. Please try again.");
        }
    }
}