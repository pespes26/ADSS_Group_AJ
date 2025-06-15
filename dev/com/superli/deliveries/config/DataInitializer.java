package com.superli.deliveries.config;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.superli.deliveries.application.services.DeliveredItemService;
import com.superli.deliveries.application.services.DestinationDocService;
import com.superli.deliveries.application.services.DriverService;
import com.superli.deliveries.application.services.ProductService;
import com.superli.deliveries.application.services.SiteService;
import com.superli.deliveries.application.services.TransportService;
import com.superli.deliveries.application.services.TruckService;
import com.superli.deliveries.application.services.ZoneService;
import com.superli.deliveries.domain.core.AvailableShifts;
import com.superli.deliveries.domain.core.DeliveredItem;
import com.superli.deliveries.domain.core.DestinationDoc;
import com.superli.deliveries.domain.core.Driver;
import com.superli.deliveries.domain.core.LicenseType;
import com.superli.deliveries.domain.core.Product;
import com.superli.deliveries.domain.core.Role;
import com.superli.deliveries.domain.core.ShiftType;
import com.superli.deliveries.domain.core.Site;
import com.superli.deliveries.domain.core.Transport;
import com.superli.deliveries.domain.core.Truck;
import com.superli.deliveries.domain.core.Zone;

/**
 * Initializes mock data for development and testing purposes.
 * Ensures data consistency and respects domain rules.
 */
public class DataInitializer {
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private static boolean initialized = false;
    private static final Random random = new Random();

    private final DriverService driverService;
    private final TruckService truckService;
    private final ZoneService zoneService;
    private final SiteService siteService;
    private final ProductService productService;
    private final TransportService transportService;
    private final DestinationDocService destinationDocService;
    private final DeliveredItemService deliveredItemService;

    public DataInitializer(
            DriverService driverService,
            TruckService truckService,
            ZoneService zoneService,
            SiteService siteService,
            ProductService productService,
            TransportService transportService,
            DestinationDocService destinationDocService,
            DeliveredItemService deliveredItemService) {
        this.driverService = driverService;
        this.truckService = truckService;
        this.zoneService = zoneService;
        this.siteService = siteService;
        this.productService = productService;
        this.transportService = transportService;
        this.destinationDocService = destinationDocService;
        this.deliveredItemService = deliveredItemService;
    }

    public void initializeIfNeeded() {
        if (initialized) {
            log.info("Data already initialized, skipping...");
            return;
        }

        try {
            log.info("Starting data initialization...");
            
            // Initialize in correct order to respect dependencies
            List<Zone> zones = initializeZones();
            List<Site> sites = initializeSites(zones);
            List<Driver> drivers = initializeDrivers(sites);
            List<Truck> trucks = initializeTrucks();
            List<Product> products = initializeProducts();
            List<Transport> transports = initializeTransports(drivers, trucks, sites);
            initializeDestinationDocs(transports, sites, products);

            initialized = true;
            log.info("Data initialization completed successfully");
        } catch (Exception e) {
            log.error("Failed to initialize data", e);
            throw new RuntimeException("Data initialization failed", e);
        }
    }

    private List<Zone> initializeZones() {
        log.info("Initializing zones...");
        List<Zone> zones = new ArrayList<>();
        
        zones.add(new Zone("Z001", "North Zone"));
        zones.add(new Zone("Z002", "South Zone"));
        zones.add(new Zone("Z003", "East Zone"));
        zones.add(new Zone("Z004", "West Zone"));
        zones.add(new Zone("Z005", "Central Zone"));

        for (Zone zone : zones) {
            zoneService.saveZone(zone);
        }
        
        log.info("Initialized {} zones", zones.size());
        return zones;
    }

    private List<Site> initializeSites(List<Zone> zones) {
        log.info("Initializing sites...");
        List<Site> sites = new ArrayList<>();

        sites.add(new Site("S001", "123 North St", "555-0001", "John Smith", zones.get(0)));
        sites.add(new Site("S002", "456 South Ave", "555-0002", "Jane Doe", zones.get(1)));
        sites.add(new Site("S003", "789 East Rd", "555-0003", "Bob Johnson", zones.get(2)));
        sites.add(new Site("S004", "321 West Blvd", "555-0004", "Alice Brown", zones.get(3)));
        sites.add(new Site("S005", "654 Central Ln", "555-0005", "Charlie Wilson", zones.get(4)));

        for (Site site : sites) {
            siteService.saveSite(site);
        }

        log.info("Initialized {} sites", sites.size());
        return sites;
    }

    private List<Driver> initializeDrivers(List<Site> sites) {
        log.info("Initializing drivers...");
        List<Driver> drivers = new ArrayList<>();

        // Create driver role
        Role driverRole = new Role("DRIVER");

        // Create drivers with different license types
        drivers.add(createDriver("D001", "Mike Johnson", LicenseType.B, true, driverRole, sites.get(0)));
        drivers.add(createDriver("D002", "Sarah Williams", LicenseType.C, true, driverRole, sites.get(1)));
        drivers.add(createDriver("D003", "David Brown", LicenseType.C1, true, driverRole, sites.get(2)));
        drivers.add(createDriver("D004", "Lisa Davis", LicenseType.C2, true, driverRole, sites.get(3)));
        drivers.add(createDriver("D005", "Tom Wilson", LicenseType.E, true, driverRole, sites.get(4)));

        for (Driver driver : drivers) {
            driverService.saveDriver(driver);
        }

        log.info("Initialized {} drivers", drivers.size());
        return drivers;
    }

//    private Driver createDriver(String id, String name, LicenseType licenseType, boolean available,
//                              Role role, Site site) {
//        List<Role> roles = new ArrayList<>();
//        roles.add(role);
//        List<AvailableShifts> constraints = new ArrayList<>();
//        constraints.add(new AvailableShifts(DayOfWeek.MONDAY, ShiftType.MORNING));
//        constraints.add(new AvailableShifts(DayOfWeek.TUESDAY, ShiftType.MORNING));
//        constraints.add(new AvailableShifts(DayOfWeek.WEDNESDAY, ShiftType.MORNING));
//        constraints.add(new AvailableShifts(DayOfWeek.THURSDAY, ShiftType.MORNING));
//        constraints.add(new AvailableShifts(DayOfWeek.FRIDAY, ShiftType.MORNING));
//        // Convert siteId from "S001" to int 1
//        int siteId = Integer.parseInt(site.getSiteId().substring(1));
//        return new Driver(
//            id,
//            name,
//            "ACC" + id,
//            2500.0,
//            siteId,
//            "FULL_TIME",
//            new Date(),
//            roles,
//            constraints,
//            role,
//            licenseType
//        );
//    }
    private Driver createDriver(String id, String name, LicenseType licenseType, boolean available,
                                Role role, Site site) {
        List<Role> roles = new ArrayList<>();
        roles.add(role);

        List<AvailableShifts> constraints = new ArrayList<>();
        constraints.add(new AvailableShifts(DayOfWeek.MONDAY, ShiftType.MORNING));
        constraints.add(new AvailableShifts(DayOfWeek.TUESDAY, ShiftType.MORNING));
        constraints.add(new AvailableShifts(DayOfWeek.WEDNESDAY, ShiftType.MORNING));
        constraints.add(new AvailableShifts(DayOfWeek.THURSDAY, ShiftType.MORNING));
        constraints.add(new AvailableShifts(DayOfWeek.FRIDAY, ShiftType.MORNING));

        // Convert siteId from "S001" to int 1
        int siteId = Integer.parseInt(site.getSiteId().substring(1));

        Driver driver = new Driver(
                id,
                name,
                "ACC" + id,
                2500.0,
                siteId,
                "FULL_TIME",
                new Date(),
                roles,
                constraints,
                role,
                licenseType
        );

        driver.setAvailable(available);

        return driver;
    }


    private List<Truck> initializeTrucks() {
        log.info("Initializing trucks...");
        List<Truck> trucks = new ArrayList<>();
        trucks.add(new Truck("ABC123", "Volvo FH16", 5000.0f, 20000.0f, LicenseType.B, true));
        trucks.add(new Truck("DEF456", "Scania R500", 4500.0f, 18000.0f, LicenseType.C, true));
        trucks.add(new Truck("GHI789", "Mercedes Actros", 4000.0f, 16000.0f, LicenseType.C1, true));
        trucks.add(new Truck("JKL012", "DAF XF", 3500.0f, 14000.0f, LicenseType.C2, true));
        trucks.add(new Truck("MNO345", "MAN TGX", 3000.0f, 12000.0f, LicenseType.E, true));
        for (Truck truck : trucks) {
            truckService.saveTruck(truck);
        }
        log.info("Initialized {} trucks", trucks.size());
        return trucks;
    }

    private List<Product> initializeProducts() {
        log.info("Initializing products...");
        List<Product> products = new ArrayList<>();

        products.add(new Product("P001", "Milk", 1.0f));
        products.add(new Product("P002", "Bread", 0.5f));
        products.add(new Product("P003", "Eggs", 0.3f));
        products.add(new Product("P004", "Cheese", 0.8f));
        products.add(new Product("P005", "Yogurt", 0.4f));

        for (Product product : products) {
            productService.saveProduct(product);
        }

        log.info("Initialized {} products", products.size());
        return products;
    }

    private List<Transport> initializeTransports(List<Driver> drivers, List<Truck> trucks, List<Site> sites) {
        log.info("Initializing transports...");
        List<Transport> transports = new ArrayList<>();

        // Create transports with matching driver license types and trucks
        for (int i = 0; i < 5; i++) {
            Driver driver = drivers.get(i);
            Truck truck = trucks.get(i);
            Site site = sites.get(i);

            // Ensure driver license matches truck requirements
            if (driver.getLicenseType() == truck.getRequiredLicenseType()) {
                Transport transport = new Transport(
                    "TR" + String.format("%03d", i + 1),
                    truck,
                    driver,
                    site,
                    LocalDateTime.now().plusDays(i + 1)
                );
                transportService.saveTransport(transport);
                transports.add(transport);
            }
        }

        log.info("Initialized {} transports", transports.size());
        return transports;
    }

    private void initializeDestinationDocs(List<Transport> transports, List<Site> sites, List<Product> products) {
        log.info("Initializing destination documents...");
        for (Transport transport : transports) {
            // Create a destination document for each transport
            DestinationDoc doc = new DestinationDoc(
                "DOC" + transport.getTransportId(),
                transport.getTransportId(),
                sites.get(random.nextInt(sites.size())),
                "PENDING"
            );
            destinationDocService.saveDoc(doc);
            // Add some delivered items to each document
            for (int i = 0; i < 3; i++) {
                Product product = products.get(random.nextInt(products.size()));
                DeliveredItem item = new DeliveredItem(doc.getDestinationDocId(), product.getProductId(), product.getName(), random.nextInt(10) + 1);
                deliveredItemService.addDeliveredItem(doc.getDestinationDocId(), item);
            }
        }
        log.info("Initialized destination documents and items");
    }

    public static void resetInitializationFlag() {
        initialized = false;
    }
} 