package com.superli.deliveries.controllers;

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
            System.out.println("\n=== Main Menu ===");
            System.out.println("1. Manage Transports");
            System.out.println("2. Manage Drivers");
            System.out.println("3. Manage Trucks");
            System.out.println("4. Manage Sites");
            System.out.println("5. Manage Zones");
            System.out.println("6. Destination Documents");
            System.out.println("0. Exit");
            System.out.print("Your choice: ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1" -> transportController.runMenu();
                case "2" -> driverController.runMenu();
                case "3" -> truckController.runMenu();
                case "4" -> siteController.runMenu();
                case "5" -> zoneController.runMenu();
                case "6" -> destinationDocController.runMenu();
                case "0" -> {
                    System.out.println("👋 Exiting the system. Goodbye!");
                    return;
                }
                default -> System.out.println("❌ Invalid choice. Please try again.");
            }
        }
    }
}
