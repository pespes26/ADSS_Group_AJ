package com.superli.deliveries.application;

import java.sql.SQLException;
import java.util.Scanner;

import com.superli.deliveries.application.controllers.DestinationDocController;
import com.superli.deliveries.application.controllers.DriverController;
import com.superli.deliveries.application.controllers.MainMenuController;
import com.superli.deliveries.application.controllers.SiteController;
import com.superli.deliveries.application.controllers.TransportController;
import com.superli.deliveries.application.controllers.TruckController;
import com.superli.deliveries.application.controllers.ZoneController;
import com.superli.deliveries.application.services.DeliveredItemService;
import com.superli.deliveries.application.services.DestinationDocService;
import com.superli.deliveries.application.services.DriverService;
import com.superli.deliveries.application.services.ProductService;
import com.superli.deliveries.application.services.SiteService;
import com.superli.deliveries.application.services.TransportService;
import com.superli.deliveries.application.services.TruckService;
import com.superli.deliveries.application.services.ZoneService;
import com.superli.deliveries.config.DataInitializer;
import com.superli.deliveries.dataaccess.dao.del.DeliveredItemDAO;
import com.superli.deliveries.dataaccess.dao.del.DeliveredItemDAOImpl;
import com.superli.deliveries.dataaccess.dao.del.DestinationDocDAO;
import com.superli.deliveries.dataaccess.dao.del.DestinationDocDAOImpl;
import com.superli.deliveries.dataaccess.dao.del.DriverDAO;
import com.superli.deliveries.dataaccess.dao.del.DriverDAOImpl;
import com.superli.deliveries.dataaccess.dao.del.ProductDAO;
import com.superli.deliveries.dataaccess.dao.del.ProductDAOImpl;
import com.superli.deliveries.dataaccess.dao.del.SiteDAO;
import com.superli.deliveries.dataaccess.dao.del.SiteDAOImpl;
import com.superli.deliveries.dataaccess.dao.del.TransportDAO;
import com.superli.deliveries.dataaccess.dao.del.TransportDAOImpl;
import com.superli.deliveries.dataaccess.dao.del.TruckDAO;
import com.superli.deliveries.dataaccess.dao.del.TruckDAOImpl;
import com.superli.deliveries.dataaccess.dao.del.ZoneDAO;
import com.superli.deliveries.dataaccess.dao.del.ZoneDAOImpl;
import com.superli.deliveries.util.Database;

public class Main {
   // Role enum inside Main
   enum Role {
    SYSTEM_ADMIN,
    TRANSPORT_MANAGER
}

// Static variable to store current user role
private static Role currentUserRole;

    public static void runDeliveriesSystem() {
        main(null);  // מריץ את אותה הפונקציה של main הקיים
    }

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
    // Test database connection
    try {
        var conn = Database.getConnection();
        if (conn != null && !conn.isClosed()) {
            System.out.println("Database connection successful!");
        }
    } catch (SQLException e) {
        System.err.println("Database connection failed: " + e.getMessage());
        return;
    }

    Scanner scanner = new Scanner(System.in);

    // --- User role selection and password authentication ---
    System.out.println("Select user type:");
    System.out.println("1. System Administrator");
    System.out.println("2. Transport Manager");
    System.out.print("Your choice: ");
    int choice = scanner.nextInt();
    scanner.nextLine(); // Clear input buffer after number

    if (choice == 1) {
        currentUserRole = Role.SYSTEM_ADMIN;
        System.out.println("You are logged in as System Administrator.");
    } else if (choice == 2) {
        currentUserRole = Role.TRANSPORT_MANAGER;
        System.out.println("You are logged in as Transport Manager.");
    } else {
        System.out.println("Invalid password or selection. Program terminating.");
        return;
    }

    // --- DAOs ---
    DriverDAO driverDAO = null;
    TruckDAO truckDAO = null;
    SiteDAO siteDAO = null;
    ZoneDAO zoneDAO = null;
    TransportDAO transportDAO = null;
    DestinationDocDAO destinationDocDAO = null;
    ProductDAO productDAO = null;
    DeliveredItemDAO deliveredItemDAO = null;
    try {
        driverDAO = new DriverDAOImpl();
        truckDAO = new TruckDAOImpl();
        siteDAO = new SiteDAOImpl();
        zoneDAO = new ZoneDAOImpl();
        transportDAO = new TransportDAOImpl();
        destinationDocDAO = new DestinationDocDAOImpl();
        productDAO = new ProductDAOImpl();
        deliveredItemDAO = new DeliveredItemDAOImpl();
    } catch (SQLException e) {
        System.err.println("DAO initialization failed: " + e.getMessage());
        return;
    }

    // --- Services ---
    var productService = new ProductService(productDAO);
    var driverService = new DriverService();
    var truckService = new TruckService(truckDAO);
    var zoneService = new ZoneService(zoneDAO);
    var siteService = new SiteService(siteDAO, zoneService);
    var transportService = new TransportService(transportDAO, driverService, truckService, siteService);
    var destinationDocService = new DestinationDocService(destinationDocDAO, siteService);
    var deliveredItemService = new DeliveredItemService(deliveredItemDAO, transportService, productService, destinationDocDAO);

    // --- Initialize Data ---
    var dataInitializer = new DataInitializer(
        driverService,
        truckService,
        zoneService,
        siteService,
        productService,
        transportService,
        destinationDocService,
        deliveredItemService
    );
    dataInitializer.initializeIfNeeded();

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