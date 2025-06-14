package com.superli.deliveries;

import java.util.Scanner;

import com.superli.deliveries.application.controllers.DeliveredItemController;
import com.superli.deliveries.application.controllers.DestinationDocController;
import com.superli.deliveries.application.controllers.DriverController;
import com.superli.deliveries.application.controllers.ProductController;
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

public class Main {
    public static void main(String[] args) {
        try {
            // Initialize DAOs
            ZoneDAO zoneDAO = new ZoneDAOImpl();
            SiteDAO siteDAO = new SiteDAOImpl();
            DriverDAO driverDAO = new DriverDAOImpl();
            TruckDAO truckDAO = new TruckDAOImpl();
            ProductDAO productDAO = new ProductDAOImpl();
            TransportDAO transportDAO = new TransportDAOImpl();
            DestinationDocDAO destinationDocDAO = new DestinationDocDAOImpl();
            DeliveredItemDAO deliveredItemDAO = new DeliveredItemDAOImpl();

            // Initialize services
            ZoneService zoneService = new ZoneService(zoneDAO);
            SiteService siteService = new SiteService(siteDAO, zoneService);
            DriverService driverService = new DriverService(); // Uses its own DAO
            TruckService truckService = new TruckService(truckDAO);
            ProductService productService = new ProductService(productDAO);
            TransportService transportService = new TransportService(transportDAO, driverService, truckService, siteService);
            DestinationDocService destinationDocService = new DestinationDocService(destinationDocDAO, siteService);
            DeliveredItemService deliveredItemService = new DeliveredItemService(deliveredItemDAO, transportService, productService);

            // Initialize data
            DataInitializer dataInitializer = new DataInitializer(
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

            // Create scanner for user input
            Scanner scanner = new Scanner(System.in);

            // Initialize controllers
            DriverController driverController = new DriverController(driverService);
            TruckController truckController = new TruckController(truckService);
            ZoneController zoneController = new ZoneController(zoneService, siteService, scanner);
            SiteController siteController = new SiteController(siteService, zoneService);
            ProductController productController = new ProductController(productService);
            TransportController transportController = new TransportController(
                transportService,
                truckService,
                driverService,
                siteService,
                productService,
                destinationDocService,
                deliveredItemService
            );
            DestinationDocController destinationDocController = new DestinationDocController(
                destinationDocService,
                deliveredItemService,
                siteService,
                transportService
            );
            DeliveredItemController deliveredItemController = new DeliveredItemController(deliveredItemService, productService, transportService);

            // Start the application
            System.out.println("Welcome to the Delivery Management System!");
            System.out.println("Please select an option:");
            System.out.println("1. Manage Drivers");
            System.out.println("2. Manage Trucks");
            System.out.println("3. Manage Zones");
            System.out.println("4. Manage Sites");
            System.out.println("5. Manage Products");
            System.out.println("6. Manage Transports");
            System.out.println("7. Manage Destination Documents");
            System.out.println("8. Manage Delivered Items");
            System.out.println("0. Exit");

            // TODO: Add menu handling logic here

        } catch (Exception e) {
            System.err.println("Error initializing application: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 