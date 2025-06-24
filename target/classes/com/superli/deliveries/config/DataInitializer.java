package com.superli.deliveries.config;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.superli.deliveries.application.services.*;
import com.superli.deliveries.domain.core.*;
import com.superli.deliveries.util.Database;

/**
 * Initializes mock data for development and testing purposes.
 * Ensures data consistency and respects domain rules.
 */
public class DataInitializer {
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private static boolean initialized = false;
    private static final Random random = new Random();
    private static Connection conn;

    // Service dependencies
    private final DriverService driverService;
    private final TruckService truckService;
    private final ZoneService zoneService;
    private final SiteService siteService;
    private final ProductService productService;
    private final TransportService transportService;
    private final DestinationDocService destinationDocService;
    private final DeliveredItemService deliveredItemService;

    // Constants for randomization
    private static final int MIN_DRIVERS = 8;
    private static final int MAX_DRIVERS = 15;
    private static final int MIN_TRUCKS = 6;
    private static final int MAX_TRUCKS = 12;
    private static final int MIN_SITES = 6;
    private static final int MAX_SITES = 10;
    private static final int MIN_ZONES = 4;
    private static final int MAX_ZONES = 8;
    private static final int MIN_TRANSPORTS = 5;
    private static final int MAX_TRANSPORTS = 10;

    // Sample data arrays
    private static final String[] FIRST_NAMES = {
        "John", "Sarah", "Michael", "Emma", "David", "Lisa", "James", "Maria",
        "Robert", "Anna", "William", "Sophia", "Daniel", "Olivia", "Thomas"
    };
    private static final String[] LAST_NAMES = {
        "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller",
        "Davis", "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez"
    };
    private static final String[] STREET_NAMES = {
        "Main", "Oak", "Maple", "Cedar", "Pine", "Elm", "Birch", "Willow",
        "Spruce", "Cherry", "Aspen", "Cypress", "Juniper", "Magnolia"
    };
    private static final String[] STREET_TYPES = {
        "Street", "Avenue", "Road", "Boulevard", "Lane", "Drive", "Court",
        "Place", "Circle", "Way", "Terrace", "Parkway"
    };
    private static final String[] TRUCK_MODELS = {
        "Volvo FH16", "Scania R500", "Mercedes Actros", "DAF XF", "MAN TGX",
        "Renault T", "Iveco Stralis", "Mitsubishi Fuso", "Isuzu NPR",
        "Ford Transit", "Mercedes Sprinter", "Volvo FM"
    };
    private static final String[] PRODUCT_NAMES = {
        "Milk", "Bread", "Eggs", "Cheese", "Yogurt", "Butter", "Sugar",
        "Flour", "Rice", "Pasta", "Cereal", "Coffee", "Tea", "Juice",
        "Water", "Soda", "Chips", "Cookies", "Crackers", "Nuts"
    };

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
        
        try {
            this.conn = Database.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database connection", e);
        }
    }

    public void initializeIfNeeded() {
        if (initialized) {
            log.info("Data already initialized, skipping...");
            return;
        }

        try {
            // Check if any of the required tables already have data
            if (!areTablesEmpty()) {
                log.info("Database already contains data, skipping initialization...");
                initialized = true;
                return;
            }

            log.info("Starting data initialization...");
            
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

    /**
     * Checks if the required tables are empty.
     * @return true if all required tables are empty, false otherwise
     */
    private boolean areTablesEmpty() {
        try {
            // Check each table in order of dependencies
            if (!isTableEmpty("zones")) return false;
            if (!isTableEmpty("sites")) return false;
            if (!isTableEmpty("drivers")) return false;
            if (!isTableEmpty("trucks")) return false;
            if (!isTableEmpty("transports")) return false;
            if (!isTableEmpty("destination_docs")) return false;
            if (!isTableEmpty("delivered_items")) return false;
            if (!isTableEmpty("products")) return false;
            return true;
        } catch (SQLException e) {
            log.error("Error checking if tables are empty", e);
            throw new RuntimeException("Failed to check if tables are empty", e);
        }
    }

    /**
     * Checks if a specific table is empty.
     * @param tableName the name of the table to check
     * @return true if the table is empty, false otherwise
     * @throws SQLException if there is an error accessing the database
     */
    private boolean isTableEmpty(String tableName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + tableName;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        }
        return true;
    }

    private List<Zone> initializeZones() {
        log.info("Initializing zones...");
        List<Zone> zones = new ArrayList<>();
        int numZones = random.nextInt(MAX_ZONES - MIN_ZONES + 1) + MIN_ZONES;
        
        String[] zoneNames = {"North", "South", "East", "West", "Central", "Coastal", "Mountain", "Valley"};
        for (int i = 0; i < numZones; i++) {
            String zoneId = String.format("Z%03d", i + 1);
            String zoneName = zoneNames[i] + " Zone";
            zones.add(new Zone(zoneId, zoneName));
            zoneService.saveZone(zones.get(i));
        }
        
        log.info("Initialized {} zones", zones.size());
        return zones;
    }

    private List<Site> initializeSites(List<Zone> zones) {
        log.info("Initializing sites...");
        List<Site> sites = new ArrayList<>();
        int numSites = random.nextInt(MAX_SITES - MIN_SITES + 1) + MIN_SITES;

        for (int i = 0; i < numSites; i++) {
            String siteId = String.format("S%03d", i + 1);
            String streetNum = String.valueOf(random.nextInt(999) + 1);
            String streetName = STREET_NAMES[random.nextInt(STREET_NAMES.length)];
            String streetType = STREET_TYPES[random.nextInt(STREET_TYPES.length)];
            String address = streetNum + " " + streetName + " " + streetType;
            String phone = String.format("555-%04d", random.nextInt(10000));
            String contactName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)] + " " + 
                               LAST_NAMES[random.nextInt(LAST_NAMES.length)];
            Zone zone = zones.get(random.nextInt(zones.size()));
            
            sites.add(new Site(siteId, address, phone, contactName, zone));
            siteService.saveSite(sites.get(i));
        }

        log.info("Initialized {} sites", sites.size());
        return sites;
    }

    private List<Driver> initializeDrivers(List<Site> sites) {
        log.info("Initializing drivers...");
        List<Driver> drivers = new ArrayList<>();
        int numDrivers = random.nextInt(MAX_DRIVERS - MIN_DRIVERS + 1) + MIN_DRIVERS;
        Role driverRole = new Role("DRIVER");

        for (int i = 0; i < numDrivers; i++) {
            String driverId = String.format("D%03d", i + 1);
            String name = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)] + " " + 
                         LAST_NAMES[random.nextInt(LAST_NAMES.length)];
            LicenseType licenseType = LicenseType.values()[random.nextInt(LicenseType.values().length)];
            boolean available = random.nextBoolean();
            Site site = sites.get(random.nextInt(sites.size()));
            
            drivers.add(createDriver(driverId, name, licenseType, available, driverRole, site));
            driverService.saveDriver(drivers.get(i));
        }

        log.info("Initialized {} drivers", drivers.size());
        return drivers;
    }

    private Driver createDriver(String id, String name, LicenseType licenseType, boolean available,
                              Role role, Site site) {
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        List<AvailableShifts> constraints = new ArrayList<>();
        DayOfWeek[] days = DayOfWeek.values();
        ShiftType[] shifts = ShiftType.values();
        
        // Randomly assign 3-5 shifts
        int numShifts = random.nextInt(3) + 3;
        for (int i = 0; i < numShifts; i++) {
            DayOfWeek day = days[random.nextInt(days.length)];
            ShiftType shift = shifts[random.nextInt(shifts.length)];
            constraints.add(new AvailableShifts(day, shift));
        }

        int siteId = Integer.parseInt(site.getSiteId().substring(1));
        return new Driver(
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
    }

    private List<Truck> initializeTrucks() {
        log.info("Initializing trucks...");
        List<Truck> trucks = new ArrayList<>();
        int numTrucks = random.nextInt(MAX_TRUCKS - MIN_TRUCKS + 1) + MIN_TRUCKS;

        for (int i = 0; i < numTrucks; i++) {
            String plateNum = generatePlateNumber();
            String model = TRUCK_MODELS[random.nextInt(TRUCK_MODELS.length)];
            float netWeight = 2500.0f + random.nextFloat() * 2500.0f;
            float maxWeight = netWeight * (2.0f + random.nextFloat() * 2.0f);
            LicenseType licenseType = LicenseType.values()[random.nextInt(LicenseType.values().length)];
            boolean available = random.nextBoolean();
            
            trucks.add(new Truck(plateNum, model, netWeight, maxWeight, licenseType, available));
            truckService.saveTruck(trucks.get(i));
        }

        log.info("Initialized {} trucks", trucks.size());
        return trucks;
    }

    private String generatePlateNumber() {
        StringBuilder plate = new StringBuilder();
        // Generate 3 random letters
        for (int i = 0; i < 3; i++) {
            plate.append((char) ('A' + random.nextInt(26)));
        }
        // Generate 3 random numbers
        for (int i = 0; i < 3; i++) {
            plate.append(random.nextInt(10));
        }
        return plate.toString();
    }

    private List<Product> initializeProducts() {
        log.info("Initializing products...");
        List<Product> products = new ArrayList<>();

        for (int i = 0; i < PRODUCT_NAMES.length; i++) {
            String productId = String.format("P%03d", i + 1);
            String name = PRODUCT_NAMES[i];
            float weight = 0.1f + random.nextFloat() * 5.0f; // Random weight between 0.1 and 5.0 kg
            
            products.add(new Product(productId, name, weight));
            productService.saveProduct(products.get(i));
        }

        log.info("Initialized {} products", products.size());
        return products;
    }

    private List<Transport> initializeTransports(List<Driver> drivers, List<Truck> trucks, List<Site> sites) {
        log.info("Initializing transports...");
        List<Transport> transports = new ArrayList<>();
        int numTransports = random.nextInt(MAX_TRANSPORTS - MIN_TRANSPORTS + 1) + MIN_TRANSPORTS;
        
        // Filter available drivers and trucks
        List<Driver> availableDrivers = drivers.stream()
            .filter(Driver::isAvailable)
            .toList();
        List<Truck> availableTrucks = trucks.stream()
            .filter(Truck::isAvailable)
            .toList();

        // Create transports using available resources
        for (int i = 0; i < numTransports && !availableDrivers.isEmpty() && !availableTrucks.isEmpty(); i++) {
            String transportId = String.format("T%03d", i + 1);
            
            // Randomly select available driver and truck
            Driver driver = availableDrivers.get(random.nextInt(availableDrivers.size()));
            Truck truck = availableTrucks.stream()
                .filter(t -> t.getRequiredLicenseType() == driver.getLicenseType())
                .findFirst()
                .orElse(null);
            
            if (truck != null) {
                Site originSite = sites.get(random.nextInt(sites.size()));
                LocalDateTime departureTime = LocalDateTime.now().plusDays(random.nextInt(7));
                
                Transport transport = new Transport(transportId, truck, driver, originSite, departureTime);
                transport.setStatus(TransportStatus.PLANNED);
                transport.setDepartureWeight(random.nextFloat() * (truck.getMaxWeight() - truck.getNetWeight()));
                
                transports.add(transport);
                transportService.saveTransport(transport);
                
                // Mark driver and truck as unavailable
                driverService.markDriverAsUnavailable(driver.getDriverId());
                truckService.markTruckAsUnavailable(truck.getPlateNum());
                
                // Remove used resources from available lists
                availableDrivers = availableDrivers.stream()
                    .filter(d -> !d.getDriverId().equals(driver.getDriverId()))
                    .toList();
                availableTrucks = availableTrucks.stream()
                    .filter(t -> !t.getPlateNum().equals(truck.getPlateNum()))
                    .toList();
            }
        }

        log.info("Initialized {} transports", transports.size());
        return transports;
    }

    private void initializeDestinationDocs(List<Transport> transports, List<Site> sites, List<Product> products) {
        log.info("Initializing destination documents...");
        for (Transport transport : transports) {
            // Create 1-3 destination documents per transport
            int numDocs = random.nextInt(3) + 1;
            for (int i = 0; i < numDocs; i++) {
                String docId = String.format("DOC%s-%d", transport.getTransportId(), i + 1);
                Site destination = sites.get(random.nextInt(sites.size()));
                
                DestinationDoc doc = new DestinationDoc(
                    docId,
                    transport.getTransportId(),
                    destination,
                    "PENDING"
                );
                destinationDocService.saveDoc(doc);
                
                // Add 2-5 items to each document
                int numItems = random.nextInt(4) + 2;
                for (int j = 0; j < numItems; j++) {
                    Product product = products.get(random.nextInt(products.size()));
                    String itemId = String.format("I%s-%d", docId, j + 1);
                    int quantity = random.nextInt(10) + 1;
                    
                    DeliveredItem item = new DeliveredItem(itemId, doc.getDestinationDocId(), 
                        product.getProductId(), quantity);
                    deliveredItemService.addDeliveredItem(doc.getDestinationDocId(), item);
                }
            }
        }
        log.info("Initialized destination documents and items");
    }

    public static void resetInitializationFlag() {
        initialized = false;
    }
} 