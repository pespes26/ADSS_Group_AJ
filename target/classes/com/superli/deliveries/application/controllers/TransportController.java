package com.superli.deliveries.application.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.superli.deliveries.Mappers.*;
import com.superli.deliveries.application.services.*;
import com.superli.deliveries.dataaccess.dao.HR.EmployeeDAOImpl;
import com.superli.deliveries.domain.core.*;
import com.superli.deliveries.dto.HR.EmployeeDTO;
import com.superli.deliveries.dto.del.*;

/**
 * Controller for managing transport operations via console UI.
 * Enhanced to support a structured transport creation process.
 */
public class TransportController {

    private final TransportService transportService;
    private final TruckService truckService;
    private final DriverService driverService;
    private final SiteService siteService;
    private final ProductService productService;
    private final DestinationDocService destinationDocService;
    private final DeliveredItemService deliveredItemService;
    private final Scanner scanner;

    // Document ID generator starting from 1000
    private int nextDocumentId = 1000;

    public TransportController(
            TransportService transportService,
            TruckService truckService,
            DriverService driverService,
            SiteService siteService,
            ProductService productService,
            DestinationDocService destinationDocService,
            DeliveredItemService deliveredItemService) {
        this.transportService = transportService;
        this.truckService = truckService;
        this.driverService = driverService;
        this.siteService = siteService;
        this.productService = productService;
        this.destinationDocService = destinationDocService;
        this.deliveredItemService = deliveredItemService;
        this.scanner = new Scanner(System.in);

        // Initialize document ID counter
        initializeNextDocumentId();
    }

    /**
     * Initialize the next document ID based on existing documents
     */
    private void initializeNextDocumentId() {
        List<DestinationDoc> existingDocs = destinationDocService.getAllDocs();
        if (!existingDocs.isEmpty()) {
            // Find the maximum document ID and set the next ID to max + 1
            nextDocumentId = Math.max(
                    existingDocs.stream()
                            .mapToInt(doc -> {
                                // Extract numeric part from document ID (e.g., "DOCTR001" -> 1)
                                String id = doc.getDestinationDocId();
                                return Integer.parseInt(id.replaceAll("[^0-9]", ""));
                            })
                            .max()
                            .orElse(999),
                    999) + 1;
        }
    }

    /**
     * Generate the next sequential document ID
     */
    private String getNextDocumentId() {
        return String.format("DOCTR%03d", nextDocumentId++);
    }

    private String getNextProductId() {
        List<Product> products = productService.getAllProducts();
        int maxId = 0;
        for (Product product : products) {
            String id = product.getProductId();
            if (id.startsWith("P")) {
                try {
                    int num = Integer.parseInt(id.substring(1));
                    maxId = Math.max(maxId, num);
                } catch (NumberFormatException e) {
                    // Skip invalid IDs
                }
            }
        }
        return String.format("P%03d", maxId + 1);
    }

    public void runMenu() {
        boolean running = true;
        while (running) {

            System.out.println("\n=== Transport Menu ===");
            System.out.println("1. Show all transports             ");
            System.out.println("2. Show transport by ID            ");
            System.out.println("3. Create structured transport     ");
            System.out.println("4. Update transport status         ");
            System.out.println("5. Delete transport                ");
            System.out.println("0. Back to main menu               ");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    showAllTransports();
                    break;
                case "2":
                    showTransportById();
                    break;
                case "3":
                    createStructuredTransport();
                    break;
                case "4":
                    updateTransportStatus();
                    break;
                case "5":
                    deleteTransport();
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void showAllTransports() {
        List<Transport> transports = transportService.getAllTransports();
        if (transports.isEmpty()) {
            System.out.println("\nNo transports found in the system.");
        } else {

            System.out.println("All Transports: ");


            for (int i = 0; i < transports.size(); i++) {
                Transport transport = transports.get(i);
                TransportDTO transportDTO = TransportMapper.toDTO(transport);
                System.out.println("\n[" + (i+1) + "] TRANSPORT #" + transportDTO.getTransportId());
                System.out.println("    Status: " + transportDTO.getStatus());
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                LocalDateTime departure = LocalDateTime.parse(transportDTO.getDepartureDateTime());
                System.out.println("    Date: " + departure.format(dateFormatter));
                System.out.println("    Time: " + departure.format(timeFormatter));
                System.out.println("    Origin: " + transport.getOriginSite().getAddress());
                System.out.println("    Destinations: " + transportDTO.getDestinationDocIds().size() + " sites");
                System.out.println("    Weight: " + String.format("%.2f", transportDTO.getDepartureWeight()) + " kg / " + 
                    String.format("%.2f", transport.getTruck().getMaxWeight()) + " kg");
                System.out.println("    Truck: " + transport.getTruck().getPlateNum() + 
                    " (" + transport.getTruck().getModel() + ")");
                System.out.println("    Driver: " + transport.getDriver().getFullName());
                System.out.println("    " + "─".repeat(40));
            }
            System.out.println("\nTotal transports: " + transports.size());
        }
    }

    private void showTransportById() {
        System.out.print("\nEnter Transport ID: ");
        try {
            String id = scanner.nextLine().trim();
            Optional<Transport> transportOpt = transportService.getTransportById(id);
            if (transportOpt.isEmpty()) {
                System.out.println("\nTransport with ID '" + id + "' not found.");
            } else {
                Transport transport = transportOpt.get();
                TransportDTO transportDTO = TransportMapper.toDTO(transport);
                
                System.out.println("\n╔════════════════════════════════════╗");
                System.out.println("║         TRANSPORT DETAILS          ║");
                System.out.println("╚════════════════════════════════════╝");
                
                System.out.println("\nBasic Information:");
                System.out.println("  ID: " + transportDTO.getTransportId());
                System.out.println("  Status: " + transportDTO.getStatus());
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                LocalDateTime departure = LocalDateTime.parse(transportDTO.getDepartureDateTime());
                System.out.println("  Date: " + departure.format(dateFormatter));
                System.out.println("  Time: " + departure.format(timeFormatter));
                
                System.out.println("\nWeight Information:");
                System.out.println("  Current Weight: " + String.format("%.2f", transportDTO.getDepartureWeight()) + " kg");
                System.out.println("  Truck Capacity: " + String.format("%.2f", transport.getTruck().getMaxWeight()) + " kg");
                System.out.println("  Available Capacity: " + 
                    String.format("%.2f", (transport.getTruck().getMaxWeight() - transportDTO.getDepartureWeight())) + " kg");
                
                System.out.println("\nRoute Information:");
                System.out.println("  Origin: " + transport.getOriginSite().getAddress());
                System.out.println("  Destinations: " + transportDTO.getDestinationDocIds().size() + " sites");
                
                if (!transport.getDestinationDocs().isEmpty()) {
                    System.out.println("\nDestination Details:");
                    for (int i = 0; i < transport.getDestinationDocs().size(); i++) {
                        DestinationDoc doc = transport.getDestinationDocs().get(i);
                        System.out.println("  " + (i+1) + ". " + doc.getDestinationId().getAddress());
                        System.out.println("     Status: " + doc.getStatus());
                        System.out.println("     Items: " + doc.getDeliveryItems().size() + " items");
                    }
                } else {
                    System.out.println("\nNo destinations assigned to this transport.");
                }
                
                System.out.println("\nVehicle Information:");
                System.out.println("  Truck: " + transport.getTruck().getPlateNum() + 
                    " (" + transport.getTruck().getModel() + ")");
                System.out.println("  Driver: " + transport.getDriver().getFullName() + 
                    " (License: " + transport.getDriver().getLicenseType() + ")");
                
                System.out.println("\n" + "═".repeat(40));
            }
        } catch (NumberFormatException e) {
            System.out.println("\nInvalid input. Please enter a valid transport ID.");
        }
    }

    /**
     * Creates a transport following a structured step-by-step process.
     * 1. Match driver and truck (auto or manual)
     * 2. Select origin site
     * 3. Select destinations
     * 4. Add items for each destination
     * 5. Check weight after each item
     * 6. Generate delivery documents
     */
    private void createStructuredTransport() {
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║       STRUCTURED TRANSPORT CREATION      ║");
        System.out.println("╚══════════════════════════════════════════╝");

        // STEP 1: Choose driver-truck assignment method
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║         STEP 1: DRIVER & TRUCK           ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║ 1. Automatic assignment                  ║");
        System.out.println("║ 2. Manual selection                      ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.print("Choose assignment method: ");

        String assignmentChoice = scanner.nextLine().trim();

        // Get available trucks and drivers first (needed for both paths)
        List<Truck> availableTrucks = truckService.getAvailableTrucks();
        List<Driver> availableDrivers = driverService.getAvailableDrivers();

        // Check if we have any trucks available
        if (availableTrucks.isEmpty()) {
            System.out.println("No trucks available. Please add or make trucks available first.");
            return;
        }

        // Check if we have any drivers available
        if (availableDrivers.isEmpty()) {
            System.out.println("No drivers available. Please add or make drivers available first.");
            return;
        }

        CompatiblePair selectedPair;
        if ("1".equals(assignmentChoice)) {
            // Automatic assignment
            List<CompatiblePair> compatiblePairs = findCompatiblePairs(availableTrucks, availableDrivers);
            if (compatiblePairs.isEmpty()) {
                System.out.println("No compatible driver-truck pairs found.");
                return;
            }

            // Display compatible pairs
            System.out.println("\nCompatible driver-truck pairs:");
            for (int i = 0; i < compatiblePairs.size(); i++) {
                CompatiblePair pair = compatiblePairs.get(i);
                System.out.printf("%d. Driver: %s (License: %s) - Truck: %s (Required License: %s)%n",
                    i + 1,
                    pair.driver().getFullName(),
                    pair.driver().getLicenseType(),
                    pair.truck().getModel(),
                    pair.truck().getRequiredLicenseType());
            }

            // Let user select a pair
            System.out.print("\nSelect a pair (1-" + compatiblePairs.size() + "): ");
            int selection = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (selection < 0 || selection >= compatiblePairs.size()) {
                System.out.println("Invalid selection.");
                return;
            }
            selectedPair = compatiblePairs.get(selection);
        } else if ("2".equals(assignmentChoice)) {
            // Manual selection
            // First select truck
            System.out.println("\nAvailable trucks:");
            for (int i = 0; i < availableTrucks.size(); i++) {
                Truck truck = availableTrucks.get(i);
                System.out.printf("%d. %s (Required License: %s)%n",
                    i + 1,
                    truck.getModel(),
                    truck.getRequiredLicenseType());
            }

            System.out.print("\nSelect a truck (1-" + availableTrucks.size() + "): ");
            int truckSelection = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (truckSelection < 0 || truckSelection >= availableTrucks.size()) {
                System.out.println("Invalid truck selection.");
                return;
            }
            Truck selectedTruck = availableTrucks.get(truckSelection);

            // Then select compatible driver
            List<Driver> compatibleDrivers = availableDrivers.stream()
                .filter(d -> d.getLicenseType().equals(selectedTruck.getRequiredLicenseType()))
                .collect(Collectors.toList());

            if (compatibleDrivers.isEmpty()) {
                System.out.println("No drivers available with the required license type: " + selectedTruck.getRequiredLicenseType());
                return;
            }

            System.out.println("\nCompatible drivers:");
            for (int i = 0; i < compatibleDrivers.size(); i++) {
                Driver driver = compatibleDrivers.get(i);
                System.out.printf("%d. %s (License: %s)%n",
                    i + 1,
                    driver.getFullName(),
                    driver.getLicenseType());
            }

            System.out.print("\nSelect a driver (1-" + compatibleDrivers.size() + "): ");
            int driverSelection = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (driverSelection < 0 || driverSelection >= compatibleDrivers.size()) {
                System.out.println("Invalid driver selection.");
                return;
            }
            Driver selectedDriver = compatibleDrivers.get(driverSelection);

            selectedPair = new CompatiblePair(selectedTruck, selectedDriver);
        } else {
            System.out.println("Invalid choice.");
            return;
        }

        // STEP 2: Select origin site
        System.out.println("\n--- STEP 2: Select Origin Site ---");
        List<Site> sites = siteService.getAllSites();
        if (sites.isEmpty()) {
            System.out.println("No sites available. Please add sites first.");
            return;
        }

        System.out.println("\nAvailable sites:");
        for (int i = 0; i < sites.size(); i++) {
            Site site = sites.get(i);
            SiteDTO siteDTO = SiteMapper.toDTO(site);
            System.out.printf("%d. %s (%s)\n",
                    i + 1,
                    siteDTO.getSiteId(),
                    siteDTO.getAddress());
        }

        System.out.print("Select origin site (number): ");
        int siteChoice = Integer.parseInt(scanner.nextLine().trim());
        if (siteChoice < 1 || siteChoice > sites.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        Site originSite = sites.get(siteChoice - 1);
        SiteDTO originSiteDTO = SiteMapper.toDTO(originSite);
        System.out.println(" Selected origin: " + originSiteDTO.getAddress());

        // STEP 3: Create the transport with current timestamp (up to minute precision)
        LocalDateTime now = LocalDateTime.now();
        // Truncate to minutes (remove seconds and nanoseconds)
        LocalDateTime truncatedNow = now.withSecond(0).withNano(0);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        System.out.println("\nCreating transport with timestamp: " + truncatedNow.format(formatter));

        // Create the transport
        Optional<Transport> transportOpt = transportService.createTransportManualWithSite(
                selectedPair.truck,
                selectedPair.driver,
                originSite,
                truncatedNow);

        if (transportOpt.isEmpty()) {
            System.out.println("Failed to create transport. Please try again.");
            return;
        }

        Transport transport = transportOpt.get();
        System.out.println(" Transport created with ID: " + transport.getTransportId());

        // STEP 4 & 5: Select destinations and add items
        System.out.println("\n--- STEP 3: Selecting Destinations ---");

        List<Site> destinationSites = new ArrayList<>(sites);
        // Remove the origin site from potential destinations
        destinationSites.removeIf(site -> site.getSiteId() == originSite.getSiteId());

        if (destinationSites.isEmpty()) {
            System.out.println("No destination sites available. Please add more sites.");
            return;
        }

        System.out.println("\nAvailable destination sites:");
        for (int i = 0; i < destinationSites.size(); i++) {
            Site site = destinationSites.get(i);
            System.out.printf("%d. %s - %s (Zone: %s)\n",
                    i + 1,
                    site.getSiteId(),
                    site.getAddress(),
                    site.getZone().getName());
        }

        List<Site> selectedDestinations = new ArrayList<>();
        boolean selectingDestinations = true;

        while (selectingDestinations) {
            System.out.print("\nSelect destination site (number, or 0 to finish): ");
            int destChoice;
            try {
                destChoice = Integer.parseInt(scanner.nextLine().trim());
                if (destChoice == 0) {
                    selectingDestinations = false;
                } else if (destChoice < 1 || destChoice > destinationSites.size()) {
                    System.out.println("Invalid selection.");
                } else {
                    Site selectedSite = destinationSites.get(destChoice - 1);

                    // Check if already selected
                    if (selectedDestinations.contains(selectedSite)) {
                        System.out.println("This site is already selected.");
                    } else {
                        selectedDestinations.add(selectedSite);
                        System.out.println("Added destination: " + selectedSite.getAddress());
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }

        if (selectedDestinations.isEmpty()) {
            System.out.println("No destinations selected. Transport will be empty.");
            return;
        }

        System.out.println("\nSelected " + selectedDestinations.size() + " destinations.");

        // Calculate truck capacity
        float maxCapacity = selectedPair.truck.getMaxWeight() - selectedPair.truck.getNetWeight();
        float currentWeight = 0.0f;

        System.out.println("\nTruck maximum cargo capacity: " + maxCapacity + " kg");

        // STEP 6: Create destination documents and add items
        System.out.println("\n--- STEP 4: Adding Items to Destinations ---");

        List<DestinationDoc> createdDocs = new ArrayList<>();

        for (Site destination : selectedDestinations) {
            System.out.println("\nAdding items for destination: " + destination.getAddress());

            // Create a destination document
            String docId = getNextDocumentId();
            DestinationDoc doc = new DestinationDoc(docId, transport.getTransportId(), destination, "PENDING");

            // Add to transport
            transport.addDestinationDoc(doc);
            transportService.saveTransport(transport);

            boolean addingItems = true;
            while (addingItems) {
                System.out.println("\nCurrent cargo weight: " + currentWeight + " kg");
                System.out.println("Remaining capacity: " + (maxCapacity - currentWeight) + " kg");

                System.out.println("\nOptions:");
                System.out.println("1. Add existing product");
                System.out.println("2. Add new product");
                System.out.println("0. Finish adding items for this destination");

                System.out.print("Your choice: ");
                String itemChoice = scanner.nextLine().trim();

                if (itemChoice.equals("0")) {
                    addingItems = false;
                    continue;
                }

                if (itemChoice.equals("1")) {
                    // Show available products
                    List<Product> products = productService.getAllProducts();
                    if (products.isEmpty()) {
                        System.out.println("No products available. Please add products first.");
                        continue;
                    }

                    System.out.println("\nAvailable Products:");
                    for (int i = 0; i < products.size(); i++) {
                        Product p = products.get(i);
                        System.out.println((i+1) + ". " + p.getName() + " (ID: " + p.getProductId() + ")");
                    }

                    System.out.print("\nEnter product number (or 0 to finish): ");
                    String productChoice = scanner.nextLine().trim();

                    if (productChoice.equals("0")) {
                        continue;
                    }

                    try {
                        int productIndex = Integer.parseInt(productChoice) - 1;
                        if (productIndex >= 0 && productIndex < products.size()) {
                            Product selectedProduct = products.get(productIndex);
                            System.out.print("Enter quantity: ");
                            int quantity = Integer.parseInt(scanner.nextLine().trim());

                            // Calculate weight for this item
                            double itemWeight = selectedProduct.getWeight() * quantity;
                            if (currentWeight + itemWeight > maxCapacity) {
                                System.out.println("Adding this quantity would exceed truck capacity!");
                                continue;
                            }

                            // Create and add the delivered item
                            DeliveredItem item = new DeliveredItem(null, doc.getDestinationDocId(), selectedProduct.getProductId(), quantity);
                            if (deliveredItemService.addDeliveredItem(doc.getDestinationDocId(), item)) {
                                doc.addDeliveryItem(item);
                                currentWeight += itemWeight;
                                System.out.print("Added " + quantity + " units of " + selectedProduct.getName() + "\n");
                            } else {
                                System.out.println("Failed to add item!");
                            }
                        } else {
                            System.out.println("Invalid product selection!");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input! Please enter a valid number.");
                    }
                } else if (itemChoice.equals("2")) {
                    // Add new product
                    System.out.print("Enter product name: ");
                    String name = scanner.nextLine().trim();
                    System.out.print("Enter product weight (kg): ");
                    double weight = Double.parseDouble(scanner.nextLine().trim());
                    System.out.print("Enter quantity: ");
                    int quantity = Integer.parseInt(scanner.nextLine().trim());

                    // Calculate weight for this item
                    double itemWeight = weight * quantity;
                    if (currentWeight + itemWeight > maxCapacity) {
                        System.out.println("Adding this quantity would exceed truck capacity!");
                        continue;
                    }

                    // Create new product
                    String productId = productService.getNextProductId();
                    Product newProduct = new Product(productId, name, (float)weight);
                    productService.saveProduct(newProduct);

                    // Create and add the delivered item
                    DeliveredItem item = new DeliveredItem(null, doc.getDestinationDocId(), productId, quantity);
                    if (deliveredItemService.addDeliveredItem(doc.getDestinationDocId(), item)) {
                        doc.addDeliveryItem(item);
                        currentWeight += itemWeight;
                        System.out.println("Added " + quantity + " units of " + name);
                    } else {
                        System.out.println("Failed to add item!");
                    }
                } else {
                    System.out.println("Invalid choice!");
                }
            }

            if (!doc.getDeliveryItems().isEmpty()) {
                destinationDocService.saveDoc(doc);
                transport.setDepartureWeight(currentWeight);
                transportService.saveTransport(transport);  // Save the transport after updating weight
                createdDocs.add(doc);
                System.out.println("Created destination document #" + docId +
                        " for " + destination.getAddress() +
                        " with " + doc.getDeliveryItems().size() + " items.");
            } else {
                System.out.println("No items added for this destination. Document not created.");
            }
        }

        System.out.println("\n=== TRANSPORT CREATION COMPLETE ===");
        System.out.println("Transport ID: " + transport.getTransportId());
        System.out.println("Total cargo weight: " + String.format("%.2f", currentWeight) + " kg");
        System.out.println("Documents created: " + createdDocs.size());

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        System.out.println("Date: " + transport.getDepartureDateTime().format(dateFormatter));
        System.out.println("Time: " + transport.getDepartureDateTime().format(timeFormatter));
    }

    /**
     * Helper class to store compatible truck-driver pairs
     */
    private static class CompatiblePair {
        public final Truck truck;
        public final Driver driver;

        public CompatiblePair(Truck truck, Driver driver) {
            this.truck = truck;
            this.driver = driver;
        }

        public Truck truck() {
            return truck;
        }

        public Driver driver() {
            return driver;
        }
    }

    /**
     * Find all compatible truck-driver pairs based on license type
     */
    private List<CompatiblePair> findCompatiblePairs(List<Truck> trucks, List<Driver> drivers) {
        List<CompatiblePair> pairs = new ArrayList<>();

        for (Truck truck : trucks) {
            for (Driver driver : drivers) {
                if (driver.getLicenseType().equals(truck.getRequiredLicenseType())) {
                    pairs.add(new CompatiblePair(truck, driver));
                }
            }
        }

        return pairs;
    }


    private void updateTransportStatus() {
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║       UPDATE TRANSPORT STATUS      ║");
        System.out.println("╚════════════════════════════════════╝");

        System.out.print("\nEnter transport ID: ");
        String transportId = scanner.nextLine().trim();

        Optional<Transport> transportOpt = transportService.getTransportById(transportId);
        if (transportOpt.isEmpty()) {
            System.out.println("\nTransport with ID '" + transportId + "' not found.");
            return;
        }

        Transport transport = transportOpt.get();
        TransportStatus currentStatus = transport.getStatus();
        System.out.println("\nCurrent status: " + currentStatus);

        List<TransportStatus> validStatuses = transportService.getValidNextStatuses(currentStatus);
        if (validStatuses.isEmpty()) {
            System.out.println("\nNo status changes are allowed for transports in " + currentStatus + " status.");
            return;
        }

        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║        AVAILABLE NEXT STATUSES      ║");
        System.out.println("╠════════════════════════════════════╣");
        for (int i = 0; i < validStatuses.size(); i++) {
            System.out.println("║ " + (i + 1) + ". " + validStatuses.get(i) + 
                " ".repeat(25 - validStatuses.get(i).toString().length()) + "║");
        }
        System.out.println("╚════════════════════════════════════╝");

        System.out.print("\nEnter status number: ");
        String choice = scanner.nextLine().trim();
        
        try {
            int statusIndex = Integer.parseInt(choice) - 1;
            if (statusIndex < 0 || statusIndex >= validStatuses.size()) {
                System.out.println("\nInvalid status number. Please enter a number between 1 and " + validStatuses.size());
                return;
            }
            
            TransportStatus newStatus = validStatuses.get(statusIndex);
            try {
                boolean updated = transportService.updateTransportStatus(transportId, newStatus);
                if (updated) {
                    System.out.println("\nTransport status updated successfully to " + newStatus);
                    if (newStatus == TransportStatus.COMPLETED || newStatus == TransportStatus.CANCELLED) {
                        System.out.println("Driver and truck have been marked as available.");
                    }
                } else {
                    System.out.println("\nFailed to update transport status. The status transition may not be valid.");
                }
            } catch (IllegalStateException e) {
                System.out.println("\nError: " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            System.out.println("\nInvalid input. Please enter a number.");
        }
    }

    private void deleteTransport() {
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║         DELETE TRANSPORT           ║");
        System.out.println("╚════════════════════════════════════╝");
        
        System.out.print("\nEnter transport ID to delete: ");
        try {
            String transportId = scanner.nextLine().trim();
            boolean deleted = transportService.deleteTransport(transportId);

            if (deleted) {
                System.out.println("\nTransport deleted successfully.");
            } else {
                System.out.println("\nTransport not found or could not be deleted.");
            }

        } catch (NumberFormatException e) {
            System.out.println("\nInvalid input. Please enter a valid ID.");
        }
    }
}