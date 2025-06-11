package com.superli.deliveries.application.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.superli.deliveries.Mappers.SiteMapper;
import com.superli.deliveries.Mappers.TransportMapper;
import com.superli.deliveries.Mappers.TruckMapper;
import com.superli.deliveries.application.services.DeliveredItemService;
import com.superli.deliveries.application.services.DestinationDocService;
import com.superli.deliveries.application.services.DriverService;
import com.superli.deliveries.application.services.ProductService;
import com.superli.deliveries.application.services.SiteService;
import com.superli.deliveries.application.services.TransportService;
import com.superli.deliveries.application.services.TruckService;
import com.superli.deliveries.domain.core.DeliveredItem;
import com.superli.deliveries.domain.core.DestinationDoc;
import com.superli.deliveries.domain.core.Driver;
import com.superli.deliveries.domain.core.Product;
import com.superli.deliveries.domain.core.Site;
import com.superli.deliveries.domain.core.Transport;
import com.superli.deliveries.domain.core.TransportStatus;
import com.superli.deliveries.domain.core.Truck;
import com.superli.deliveries.dto.SiteDTO;
import com.superli.deliveries.dto.TransportDTO;
import com.superli.deliveries.dto.TruckDTO;

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
                            .mapToInt(doc -> Integer.parseInt(doc.getDestinationDocId()))
                            .max()
                            .orElse(999),
                    999) + 1;
        }
    }

    /**
     * Generate the next sequential document ID
     */
    private String getNextDocumentId() {
        return String.valueOf(nextDocumentId++);
    }

    public void runMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║           TRANSPORT MENU           ║");
            System.out.println("╠════════════════════════════════════╣");
            System.out.println("║ 1. Show all transports             ║");
            System.out.println("║ 2. Show transport by ID            ║");
            System.out.println("║ 3. Create structured transport     ║");
            System.out.println("║ 4. Update transport status         ║");
            System.out.println("║ 5. Delete transport                ║");
            System.out.println("║ 0. Back to main menu               ║");
            System.out.println("╚════════════════════════════════════╝");
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
            System.out.println("No transports found.");
        } else {
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║           ALL TRANSPORTS           ║");
            System.out.println("╚════════════════════════════════════╝");

            for (int i = 0; i < transports.size(); i++) {
                Transport transport = transports.get(i);
                TransportDTO transportDTO = TransportMapper.toDTO(transport);
                System.out.println("\n[" + (i+1) + "] TRANSPORT #" + transportDTO.getTransportId());
                System.out.println("    Status: " + transportDTO.getStatus());
                System.out.println("    Departure: " + transportDTO.getDepartureDateTime());
                System.out.println("    Origin: " + transportDTO.getOriginSiteId());
                System.out.println("    Destinations: " + transportDTO.getDestinationDocIds().size());
                System.out.println("    Total Weight: " + transportDTO.getDepartureWeight() + " kg");
                System.out.println("    " + "-".repeat(40));
            }
        }
    }

    private void showTransportById() {
        System.out.print("Enter Transport ID: ");
        try {
            String id = scanner.nextLine().trim();
            Optional<Transport> transportOpt = transportService.getTransportById(id);
            if (transportOpt.isEmpty()) {
                System.out.println("Transport not found.");
            } else {
                Transport transport = transportOpt.get();
                TransportDTO transportDTO = TransportMapper.toDTO(transport);
                System.out.println("Transport ID: " + transportDTO.getTransportId());
                System.out.println("Status: " + transportDTO.getStatus());
                System.out.println("Departure: " + transportDTO.getDepartureDateTime());
                System.out.println("Origin Site ID: " + transportDTO.getOriginSiteId());
                System.out.println("Truck ID: " + transportDTO.getTruckId());
                System.out.println("Driver ID: " + transportDTO.getDriverId());
                System.out.println("Departure Weight: " + transportDTO.getDepartureWeight() + " kg");
                System.out.println("Destination Documents: " + transportDTO.getDestinationDocIds());
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid ID.");
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
        System.out.println("\n--- STEP 1: Driver and Truck Assignment ---");
        System.out.println("1. Automatic assignment (system finds compatible pair)");
        System.out.println("2. Manual selection");
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

        if (assignmentChoice.equals("1")) {
            // AUTOMATIC ASSIGNMENT
            System.out.println("\n--- Automatic Assignment ---");

            // Find compatible pairs
            List<CompatiblePair> compatiblePairs = findCompatiblePairs(availableTrucks, availableDrivers);

            if (compatiblePairs.isEmpty()) {
                System.out.println("No compatible driver-truck pairs found. Please ensure drivers have appropriate licenses for available trucks.");
                return;
            }

            // Display compatible pairs and let user choose
            System.out.println("\nCompatible driver-truck pairs:");
            for (int i = 0; i < compatiblePairs.size(); i++) {
                CompatiblePair pair = compatiblePairs.get(i);
                TruckDTO truckDTO = TruckMapper.toDTO(pair.truck);
                System.out.printf("%d. Driver: %s (%s) - Truck: %s (%s, max: %.1f kg)\n",
                        i + 1,
                        pair.driver.getFullName(),
                        pair.driver.getLicenseType(),
                        truckDTO.getlicensePlate(),
                        truckDTO.getModel(),
                        truckDTO.getMaxWeight());
            }

            System.out.print("Select a pair (number): ");
            int pairChoice = Integer.parseInt(scanner.nextLine().trim());
            if (pairChoice < 1 || pairChoice > compatiblePairs.size()) {
                System.out.println("Invalid selection.");
                return;
            }
            selectedPair = compatiblePairs.get(pairChoice - 1);
        } else {
            // MANUAL SELECTION
            System.out.println("\n--- Manual Selection ---");

            // Show available trucks
            System.out.println("\nAvailable trucks:");
            for (int i = 0; i < availableTrucks.size(); i++) {
                Truck truck = availableTrucks.get(i);
                TruckDTO truckDTO = TruckMapper.toDTO(truck);
                System.out.printf("%d. %s (%s, max: %.1f kg)\n",
                        i + 1,
                        truckDTO.getModel(),
                        truckDTO.getlicensePlate(),
                        truckDTO.getMaxWeight());
            }

            System.out.print("Select a truck (number): ");
            int truckChoice = Integer.parseInt(scanner.nextLine().trim());
            if (truckChoice < 1 || truckChoice > availableTrucks.size()) {
                System.out.println("Invalid selection.");
                return;
            }
            Truck selectedTruck = availableTrucks.get(truckChoice - 1);

            // Show compatible drivers
            List<Driver> compatibleDrivers = availableDrivers.stream()
                    .filter(driver -> driver.getLicenseType().equals(selectedTruck.getRequiredLicenseType()))
                    .collect(Collectors.toList());

            if (compatibleDrivers.isEmpty()) {
                System.out.println("No drivers available with the required license type: " + selectedTruck.getRequiredLicenseType());
                return;
            }

            System.out.println("\nCompatible drivers:");
            for (int i = 0; i < compatibleDrivers.size(); i++) {
                Driver driver = compatibleDrivers.get(i);
                System.out.printf("%d. %s (%s)\n",
                        i + 1,
                        driver.getFullName(),
                        driver.getLicenseType());
            }

            System.out.print("Select a driver (number): ");
            int driverChoice = Integer.parseInt(scanner.nextLine().trim());
            if (driverChoice < 1 || driverChoice > compatibleDrivers.size()) {
                System.out.println("Invalid selection.");
                return;
            }
            Driver selectedDriver = compatibleDrivers.get(driverChoice - 1);

            selectedPair = new CompatiblePair(selectedTruck, selectedDriver);
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
        System.out.println("✅ Selected origin: " + originSiteDTO.getAddress());

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
        System.out.println("✅ Transport created with ID: " + transport.getTransportId());

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
                        System.out.println("✅ Added destination: " + selectedSite.getAddress());
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
            DestinationDoc doc = new DestinationDoc(docId, transport.getTransportId(), destination, "PLANNED");

            // Add to transport
            transport.addDestinationDoc(doc);

            // Add items
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

                switch (itemChoice) {
                    case "0" -> addingItems = false;
                    case "1" -> {
                        // Add existing product
                        addItemsToDestination(doc);
                    }
                    case "2" -> {
                        // Add new product manually
                        System.out.print("Enter product ID: ");
                        String productId = scanner.nextLine().trim();

                        if (productId.isEmpty()) {
                            System.out.println("Product ID cannot be empty.");
                        } else {
                            System.out.print("Enter product name: ");
                            String productName = scanner.nextLine().trim();

                            if (productName.isEmpty()) {
                                System.out.println("Product name cannot be empty.");
                            } else {
                                System.out.print("Enter weight per unit (kg): ");
                                try {
                                    float weight = Float.parseFloat(scanner.nextLine().trim());

                                    if (weight <= 0) {
                                        System.out.println("Weight must be positive.");
                                    } else {
                                        // Create the new product
                                        Product newProduct = new Product(productId, productName, weight);
                                        productService.saveProduct(newProduct);

                                        System.out.print("Enter quantity: ");
                                        int quantity = Integer.parseInt(scanner.nextLine().trim());

                                        if (quantity <= 0) {
                                            System.out.println("Quantity must be positive.");
                                        } else {
                                            // Calculate weight of this item
                                            float itemWeight = weight * quantity;

                                            // Check if adding this would exceed capacity
                                            if (currentWeight + itemWeight > maxCapacity) {
                                                System.out.println("   Adding this item would exceed truck capacity.");
                                                System.out.println("   Current weight: " + currentWeight + " kg");
                                                System.out.println("   Item weight: " + itemWeight + " kg");
                                                System.out.println("   Maximum capacity: " + maxCapacity + " kg");
                                            } else {
                                                // Add item to document
                                                DeliveredItem item = new DeliveredItem(productId, quantity);
                                                doc.addDeliveredItem(item);

                                                // Update current weight
                                                currentWeight += itemWeight;

                                                System.out.println("✅ Added " + quantity + " units of " +
                                                        productName + " (+" + itemWeight + " kg)");
                                            }
                                        }
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.println("Invalid input. Please enter a number.");
                                }
                            }
                        }
                    }
                    default -> System.out.println("Invalid choice.");
                }
            }

            // Save the document if it has items
            if (!doc.getDeliveryItems().isEmpty()) {
                destinationDocService.saveDoc(doc);
                createdDocs.add(doc);
                System.out.println("✅ Created destination document #" + docId +
                        " for " + destination.getAddress() +
                        " with " + doc.getDeliveryItems().size() + " items.");
            } else {
                System.out.println("ℹ️ No items added for this destination. Document not created.");
            }
        }

        // Update transport weight
        transport.setDepartureWeight(currentWeight);
        transportService.saveTransport(transport);

        System.out.println("\n=== TRANSPORT CREATION COMPLETE ===");
        System.out.println("Transport ID: " + transport.getTransportId());
        System.out.println("Total cargo weight: " + currentWeight + " kg");
        System.out.println("Documents created: " + createdDocs.size());

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        System.out.println("Departure date/time: " + transport.getDepartureDateTime().format(outputFormatter));
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

        System.out.print("Enter transport ID: ");
        try {
            String transportId = scanner.nextLine().trim();
            Optional<Transport> transportOpt = transportService.getTransportById(transportId);

            if (transportOpt.isEmpty()) {
                System.out.println("Transport not found.");
                return;
            }

            Transport transport = transportOpt.get();
            System.out.println("Current status: " + transport.getStatus());

            System.out.println("\nAvailable statuses:");
            System.out.println("1. PLANNED");
            System.out.println("2. DISPATCHED");
            System.out.println("3. COMPLETED");
            System.out.println("4. CANCELLED");

            System.out.print("Select new status (number): ");
            int statusChoice = Integer.parseInt(scanner.nextLine().trim());

            TransportStatus newStatus;
            switch (statusChoice) {
                case 1 -> newStatus = TransportStatus.PLANNED;
                case 2 -> newStatus = TransportStatus.DISPATCHED;
                case 3 -> newStatus = TransportStatus.COMPLETED;
                case 4 -> newStatus = TransportStatus.CANCELLED;
                default -> {
                    System.out.println("Invalid selection.");
                    return;
                }
            }

            boolean updated = transportService.updateTransportStatus(transportId, newStatus);
            if (updated) {
                System.out.println("Transport status updated to: " + newStatus);
            } else {
                System.out.println("Failed to update status.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid ID.");
        }
    }

    private void deleteTransport() {
        System.out.print("Enter transport ID to delete: ");
        try {
            String transportId = scanner.nextLine().trim();
            boolean deleted = transportService.deleteTransport(transportId);

            if (deleted) {
                System.out.println("Transport deleted successfully.");
            } else {
                System.out.println("Transport not found or could not be deleted.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid ID.");
        }
    }

    private void addItemsToDestination(DestinationDoc doc) {
        List<Product> products = productService.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("No products available.");
            return;
        }

        System.out.println("\nAvailable Products:");
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            System.out.println((i + 1) + ". " + product.getName() + " (ID: " + product.getProductId() + ")");
        }

        while (true) {
            System.out.print("\nEnter product number (or 0 to finish): ");
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice == 0) {
                    break;
                }

                if (choice < 1 || choice > products.size()) {
                    System.out.println("Invalid product number.");
                    continue;
                }

                Product selectedProduct = products.get(choice - 1);
                System.out.print("Enter quantity: ");
                int quantity = Integer.parseInt(scanner.nextLine().trim());

                if (quantity <= 0) {
                    System.out.println("Quantity must be positive.");
                    continue;
                }

                DeliveredItem item = new DeliveredItem(selectedProduct.getProductId(), quantity);
                if (deliveredItemService.addDeliveredItem(doc.getDestinationDocId(), item)) {
                    System.out.println("✅ Added " + quantity + " units of " + selectedProduct.getName());
                } else {
                    System.out.println("❌ Failed to add item. Weight limit may be exceeded.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
}