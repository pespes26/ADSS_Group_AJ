package com.superli.deliveries;

import com.superli.deliveries.controllers.*;
import com.superli.deliveries.service.*;
import com.superli.deliveries.storage.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

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

        // Enhanced TransportController with all required dependencies
        var transportController = new TransportController(
                transportService,
                truckService,
                driverService,
                siteService,
                productService,
                destinationDocService,
                deliveredItemService);

        var destinationDocController = new DestinationDocController(
                destinationDocService,
                deliveredItemService,
                siteService,
                transportService);

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

    // No additional initialization methods needed -
    // all initialization is now handled by MockDataInitializer
}