package com.superli.deliveries;

import com.superli.deliveries.domain.*;
import com.superli.deliveries.service.*;
import com.superli.deliveries.storage.ProductRepository;

/**
 * Utility class for initializing mock data for testing purposes.
 * This class is for development and testing only and should be removed before final deployment.
 */
public class MockDataInitializer {

    /**
     * Initializes the system with mock data.
     *
     * @param driverService Service for managing drivers
     * @param truckService Service for managing trucks
     * @param zoneService Service for managing zones
     * @param siteService Service for managing sites
     * @param productService Service for managing products
     */
    public static void initializeMockData(
            DriverService driverService,
            TruckService truckService,
            ZoneService zoneService,
            SiteService siteService,
            ProductService productService) {

        System.out.println("Initializing mock data for testing...");

        // Initialize zones
        initializeZones(zoneService);

        // Initialize sites (needs zones to be initialized first)
        initializeSites(siteService, zoneService);

        // Initialize drivers
        initializeDrivers(driverService);

        // Initialize trucks
        initializeTrucks(truckService);

        // Initialize products
        initializeProducts(productService);

        System.out.println("Mock data initialization complete!");
    }

    private static void initializeZones(ZoneService zoneService) {
        // Create some zones with plain names in the 'name' field
        // and combined name+number in the 'id' field
        Zone northZone = new Zone("North1", "North");
        Zone centralZone = new Zone("Central2", "Central");
        Zone southZone = new Zone("South3", "South");
        Zone jerusalemZone = new Zone("Jerusalem4", "Jerusalem");
        Zone haifaZone = new Zone("Haifa5", "Haifa");

        // Save zones
        zoneService.saveZone(northZone);
        zoneService.saveZone(centralZone);
        zoneService.saveZone(southZone);
        zoneService.saveZone(jerusalemZone);
        zoneService.saveZone(haifaZone);

        System.out.println("Initialized 5 zones");
    }

    private static void initializeSites(SiteService siteService, ZoneService zoneService) {
        // Get zones (assuming they were created in initializeZones)
        Zone northZone = zoneService.getZoneById("North1").orElseThrow();
        Zone centralZone = zoneService.getZoneById("Central2").orElseThrow();
        Zone southZone = zoneService.getZoneById("South3").orElseThrow();
        Zone jerusalemZone = zoneService.getZoneById("Jerusalem4").orElseThrow();
        Zone haifaZone = zoneService.getZoneById("Haifa5").orElseThrow();

        // Create sites in the north zone
        Site haifaWarehouse = new Site("S001", "3 Port Avenue, Haifa", "04-8123456", "Dan Cohen", northZone);
        Site carmielStore = new Site("S002", "50 Main Street, Carmiel", "04-9561234", "Sarah Levy", northZone);

        // Create sites in the central zone
        Site tlvMainHQ = new Site("S003", "23 Rothschild Blvd, Tel Aviv", "03-6123456", "David Mizrahi", centralZone);
        Site petahTikvaWarehouse = new Site("S004", "15 Industrial Zone, Petah Tikva", "03-9123456", "Rachel Green", centralZone);

        // Create sites in the south zone
        Site beerShevaWarehouse = new Site("S005", "7 Ben Gurion, Beer Sheva", "08-6123456", "Moshe Cohen", southZone);
        Site eilatStore = new Site("S006", "2 Red Sea Mall, Eilat", "08-6372345", "Noa Levi", southZone);

        // Create sites in Jerusalem zone
        Site jerusalemCenter = new Site("S007", "12 King David St, Jerusalem", "02-5454545", "Rachel Goldstein", jerusalemZone);
        Site oldCityBranch = new Site("S008", "7 Jaffa Gate, Jerusalem", "02-5897654", "Daniel Azoulay", jerusalemZone);

        // Create sites in Haifa zone
        Site haifaPort = new Site("S009", "34 Port Avenue, Haifa", "04-8123456", "Yossi Mizrahi", haifaZone);
        Site baysideFacility = new Site("S010", "123 Beach Road, Haifa", "04-8349876", "Noa Golan", haifaZone);

        // Save all sites
        siteService.saveSite(haifaWarehouse);
        siteService.saveSite(carmielStore);
        siteService.saveSite(tlvMainHQ);
        siteService.saveSite(petahTikvaWarehouse);
        siteService.saveSite(beerShevaWarehouse);
        siteService.saveSite(eilatStore);
        siteService.saveSite(jerusalemCenter);
        siteService.saveSite(oldCityBranch);
        siteService.saveSite(haifaPort);
        siteService.saveSite(baysideFacility);

        System.out.println("Initialized 10 sites");
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

    private static void initializeProducts(ProductService productService) {
        // Create sample products with realistic weights
        Product[] products = {
                new Product("P001", "Milk 3% 1L", 1.05f),
                new Product("P002", "Sugar 1kg", 1.02f),
                new Product("P003", "Flour 1kg", 1.03f),
                new Product("P004", "Rice 5kg", 5.1f),
                new Product("P005", "Olive Oil 1L", 0.95f),
                new Product("P006", "Paper Towels 6pk", 0.85f),
                new Product("P007", "Mineral Water 6pk", 6.2f),
                new Product("P008", "Laundry Detergent 3kg", 3.15f),
                new Product("P009", "Bleach 2L", 2.25f),
                new Product("P010", "Pasta 500g", 0.52f),
                new Product("P011", "Coffee Beans 1kg", 1.05f),
                new Product("P012", "Orange Juice 1L", 1.05f),
                new Product("P013", "Dishwashing Liquid 750ml", 0.8f),
                new Product("P014", "Toilet Paper 12pk", 2.4f),
                new Product("P015", "Breakfast Cereal 500g", 0.55f),
                new Product("P016", "Canned Tuna 4pk", 0.95f),
                new Product("P017", "Tea Bags 100ct", 0.35f),
                new Product("P018", "Shampoo 750ml", 0.85f),
                new Product("P019", "Baby Diapers 48pk", 1.85f),
                new Product("P020", "Cat Food 5kg", 5.25f)
        };

        // Save all products
        for (Product product : products) {
            productService.saveProduct(product);
        }

        System.out.println("Initialized 20 products");
    }
}