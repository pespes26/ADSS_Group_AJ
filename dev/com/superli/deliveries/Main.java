package com.superli.deliveries;

import com.superli.deliveries.application.controllers.*;
import com.superli.deliveries.infrastructure.repositories.*;
import com.superli.deliveries.application.services.*;

import java.util.Scanner;

public class Main {

    // Role enum inside Main
    enum Role {
        SYSTEM_ADMIN,
        TRANSPORT_MANAGER
    }

    // Static variable to store current user role
    private static Role currentUserRole;

    /**
     * Checks if the current user is a system administrator.
     * @return true if current user is a system administrator, false otherwise
     */
    public static boolean isSystemAdmin() {
        return currentUserRole == Role.SYSTEM_ADMIN;
    }

    /**
     * Checks if the current user is a transport manager.
     * @return true if current user is a transport manager, false otherwise
     */
    public static boolean isTransportManager() {
        return currentUserRole == Role.TRANSPORT_MANAGER;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // --- User role selection and password authentication ---
        System.out.println("Select user type:");
        System.out.println("1. System Administrator");
        System.out.println("2. Transport Manager");
        System.out.print("Your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Clear input buffer after number

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (choice == 1 && password.equals("admin")) {
            currentUserRole = Role.SYSTEM_ADMIN;
            System.out.println("You are logged in as System Administrator.");
        } else if (choice == 2 && password.equals("transport123")) {
            currentUserRole = Role.TRANSPORT_MANAGER;
            System.out.println("You are logged in as Transport Manager.");
        } else {
            System.out.println("Invalid password or selection. Program terminating.");
            return;
        }

        // --- Repositories (in-memory) ---
        var driverRepo = new DriverRepository();
        var truckRepo = new TruckRepository();
        var siteRepo = new SiteRepository();
        var zoneRepo = new ZoneRepository();
        var transportRepo = new TransportRepository();
        var destinationDocRepo = new DestinationDocRepository();
        var productRepo = new ProductRepository(); // Repository for products

        // --- Services ---
        var productService = new ProductService(productRepo);
        var driverService = new DriverService(driverRepo);
        var truckService = new TruckService(truckRepo);
        var zoneService = new ZoneService(zoneRepo);
        var siteService = new SiteService(siteRepo);
        var transportService = new TransportService(transportRepo, driverService, truckService, siteService);
        var destinationDocService = new DestinationDocService(destinationDocRepo);

        // Initialize services that have interdependencies after creating initial services
        var deliveredItemService = new DeliveredItemService(destinationDocRepo, transportService, productService);

        // --- Initialize Mock Data for Testing ---
        // NOTE: This should be removed before final submission
        MockDataInitializer.initializeMockData(driverService, truckService, zoneService, siteService, productService);

        // --- Controllers ---
        var driverController = new DriverController(driverService);
        var truckController = new TruckController(truckService);
        var siteController = new SiteController(siteService, zoneService);
        var zoneController = new ZoneController(zoneService, siteService, scanner);

        var transportController = new TransportController(
                transportService,
                truckService,
                driverService,
                siteService,
                productService,
                destinationDocService,
                deliveredItemService
        );

        var destinationDocController = new DestinationDocController(
                destinationDocService,
                deliveredItemService,
                siteService,
                transportService
        );

        // --- Main Menu Controller ---
        var mainMenuController = new MainMenuController(
                transportController,
                driverController,
                truckController,
                siteController,
                zoneController,
                destinationDocController,
                scanner
        );

        // --- Run the application ---
        mainMenuController.run();
    }
}