package com.superli.deliveries;

import com.superli.deliveries.controllers.*;
import com.superli.deliveries.service.*;
import com.superli.deliveries.storage.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Repositories
        DriverRepository driverRepo = new DriverRepository();
        TruckRepository truckRepo = new TruckRepository();
        TransportRepository transportRepo = new TransportRepository();
        SiteRepository siteRepo = new SiteRepository();
        ZoneRepository zoneRepo = new ZoneRepository();
        DestinationDocRepository destinationDocRepo = new DestinationDocRepository();

        // Services
        DriverService driverService = new DriverService(driverRepo);
        TruckService truckService = new TruckService(truckRepo);
        TransportService transportService = new TransportService(transportRepo);
        SiteService siteService = new SiteService(siteRepo);
        ZoneService zoneService = new ZoneService(zoneRepo);
        DestinationDocService destinationDocService = new DestinationDocService(destinationDocRepo);

        // Controllers
        DriverController driverController = new DriverController(driverService);
        TruckController truckController = new TruckController(truckService);
        TransportController transportController = new TransportController(transportService);
        SiteController siteController = new SiteController(siteService);
        ZoneController zoneController = new ZoneController(zoneService, scanner);
        DestinationDocController destinationDocController = new DestinationDocController(destinationDocService);

        // Main Menu
        MainMenuController mainMenuController = new MainMenuController(
                transportController,
                driverController,
                truckController,
                siteController,
                zoneController,
                destinationDocController,
                scanner
        );

        mainMenuController.run();
    }
}
