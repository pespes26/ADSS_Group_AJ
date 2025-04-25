package com.superli.deliveries;

import com.superli.deliveries.domain.*;
import com.superli.deliveries.service.*;

/**
 * Utility class for initializing mock data for testing purposes.
 * This class is for development and testing only and should be removed before final deployment.
 */
class MockDataInitializer {

    /**
     * Initializes the system with mock data.
     *
     * @param driverService Service for managing drivers
     * @param truckService Service for managing trucks
     * @param zoneService Service for managing zones
     * @param siteService Service for managing sites
     */
    static void initializeMockData(
            DriverService driverService,
            TruckService truckService,
            ZoneService zoneService,
            SiteService siteService) {

        System.out.println("Initializing mock data for testing...");

        // Initialize zones
        initializeZones(zoneService);

        // Initialize sites (needs zones to be initialized first)
        initializeSites(siteService, zoneService);

        // Initialize drivers
        initializeDrivers(driverService);

        // Initialize trucks
        initializeTrucks(truckService);

        System.out.println("Mock data initialization complete!");
    }

    private static void initializeZones(ZoneService zoneService) {
        // Create some zones
        Zone northZone = new Zone("NORTH", "Northern Region");
        Zone centralZone = new Zone("CENTRAL", "Central Region");
        Zone southZone = new Zone("SOUTH", "Southern Region");

        // Save zones
        zoneService.saveZone(northZone);
        zoneService.saveZone(centralZone);
        zoneService.saveZone(southZone);

        System.out.println("Initialized 3 zones");
    }

    private static void initializeSites(SiteService siteService, ZoneService zoneService) {
        // Get zones (assuming they were created in initializeZones)
        Zone northZone = zoneService.getZoneById("NORTH").orElseThrow();
        Zone centralZone = zoneService.getZoneById("CENTRAL").orElseThrow();
        Zone southZone = zoneService.getZoneById("SOUTH").orElseThrow();

        // Create sites in the north zone
        Site haifaWarehouse = new Site("HAIFA-WH", "3 Port Avenue, Haifa", "04-8123456", "Dan Cohen", northZone);
        Site carmielStore = new Site("CARMIEL-1", "50 Main Street, Carmiel", "04-9561234", "Sarah Levy", northZone);

        // Create sites in the central zone
        Site tlvMainHQ = new Site("TLV-HQ", "23 Rothschild Blvd, Tel Aviv", "03-6123456", "David Mizrahi", centralZone);
        Site petahTikvaWarehouse = new Site("PT-WH", "15 Industrial Zone, Petah Tikva", "03-9123456", "Rachel Green", centralZone);

        // Create sites in the south zone
        Site beerShevaWarehouse = new Site("BS-WH", "7 Ben Gurion, Beer Sheva", "08-6123456", "Moshe Cohen", southZone);
        Site eilatStore = new Site("EILAT-1", "2 Red Sea Mall, Eilat", "08-6372345", "Noa Levi", southZone);

        // Save all sites
        siteService.saveSite(haifaWarehouse);
        siteService.saveSite(carmielStore);
        siteService.saveSite(tlvMainHQ);
        siteService.saveSite(petahTikvaWarehouse);
        siteService.saveSite(beerShevaWarehouse);
        siteService.saveSite(eilatStore);

        System.out.println("Initialized 6 sites");
    }

    private static void initializeDrivers(DriverService driverService) {
        // Create 10 drivers with different license types
        Driver[] drivers = {
                new Driver("D001", "John Smith", LicenseType.C1, true),
                new Driver("D002", "Sarah Johnson", LicenseType.B, false),
                new Driver("D003", "Daniel Brown", LicenseType.C, true),
                new Driver("D004", "Rachel Davis", LicenseType.E, true),
                new Driver("D005", "Michael Wilson", LicenseType.C2, false),
                new Driver("D006", "Emily Taylor", LicenseType.C, true),
                new Driver("D007", "David Miller", LicenseType.C1, true),
                new Driver("D008", "Jessica Moore", LicenseType.B, false),
                new Driver("D009", "James Anderson", LicenseType.E, true),
                new Driver("D010", "Emma Thomas", LicenseType.C2, true)
        };

        // Save all drivers
        for (Driver driver : drivers) {
            driverService.saveDriver(driver);

            // Mark unavailable drivers as unavailable (based on isAvailable field)
            if (!driver.isAvailable()) {
                driverService.markDriverAsUnavailable(driver.getDriverId());
            }
        }

        System.out.println("Initialized 10 drivers");
    }

    private static void initializeTrucks(TruckService truckService) {
        // Create 10 trucks with different license requirements
        Truck[] trucks = {
                new Truck("T001", "Isuzu NPR", 3500.0f, 7500.0f, LicenseType.C1),
                new Truck("T002", "Mercedes Sprinter", 2800.0f, 5000.0f, LicenseType.B),
                new Truck("T003", "Volvo FH", 9000.0f, 24000.0f, LicenseType.C),
                new Truck("T004", "Scania R450", 9500.0f, 26000.0f, LicenseType.E),
                new Truck("T005", "MAN TGX", 8500.0f, 22000.0f, LicenseType.C2),
                new Truck("T006", "DAF XF", 9200.0f, 25000.0f, LicenseType.C),
                new Truck("T007", "Renault T", 8800.0f, 23000.0f, LicenseType.C1),
                new Truck("T008", "Ford Transit", 2500.0f, 4600.0f, LicenseType.B),
                new Truck("T009", "Iveco Stralis", 9300.0f, 25500.0f, LicenseType.E),
                new Truck("T010", "Mitsubishi Fuso", 6000.0f, 15000.0f, LicenseType.C2)
        };

        // Set availability (some available, some not)
        boolean[] availability = {true, false, true, true, false, true, true, true, false, true};

        // Save all trucks
        for (int i = 0; i < trucks.length; i++) {
            truckService.saveTruck(trucks[i]);

            // Mark unavailable trucks
            if (!availability[i]) {
                truckService.markTruckAsUnavailable(trucks[i].getPlateNum());
            }
        }

        System.out.println("Initialized 10 trucks");
    }
}