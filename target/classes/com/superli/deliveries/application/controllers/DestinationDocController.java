package com.superli.deliveries.controllers;

import com.superli.deliveries.domain.DeliveredItem;
import com.superli.deliveries.domain.DestinationDoc;
import com.superli.deliveries.domain.Site;
import com.superli.deliveries.domain.Transport;
import com.superli.deliveries.service.DeliveredItemService;
import com.superli.deliveries.service.DestinationDocService;
import com.superli.deliveries.service.SiteService;
import com.superli.deliveries.service.TransportService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Controller for managing DestinationDoc (delivery documents) via console UI.
 * Enhanced with functionality for creating and managing destination documents.
 */
public class DestinationDocController {

    private final DestinationDocService destinationDocService;
    private final DeliveredItemService deliveredItemService;
    private final SiteService siteService;
    private final TransportService transportService;
    private final Scanner scanner;

    // Counter for generating sequential document IDs
    private int nextDocId = 1;

    public DestinationDocController(DestinationDocService destinationDocService,
                                    DeliveredItemService deliveredItemService,
                                    SiteService siteService,
                                    TransportService transportService) {
        this.destinationDocService = destinationDocService;
        this.deliveredItemService = deliveredItemService;
        this.siteService = siteService;
        this.transportService = transportService;
        this.scanner = new Scanner(System.in);

        // Initialize nextDocId based on existing documents
        initializeNextDocId();
    }

    /**
     * Initialize the next document ID counter based on existing documents
     */
    private void initializeNextDocId() {
        List<DestinationDoc> existingDocs = destinationDocService.getAllDestinationDocs();
        if (!existingDocs.isEmpty()) {
            nextDocId = existingDocs.stream()
                    .mapToInt(DestinationDoc::getDestinationDocId)
                    .max()
                    .orElse(0) + 1;
        }
    }

    /**
     * Generate a new unique document ID
     */
    private int generateNextDocId() {
        return nextDocId++;
    }

    public void runMenu() {
        while (true) {
            System.out.println("\n=== Destination Document Menu ===");
            System.out.println("1. Show all destination documents");
            System.out.println("2. Show destination document by ID");
            System.out.println("3. Create new destination document for transport");
            System.out.println("4. Add items to destination document");
            System.out.println("5. Update destination document status");
            System.out.println("6. Delete destination document");
            System.out.println("0. Back to main menu");
            System.out.print("Your choice: ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1" -> showAllDestinationDocs();
                case "2" -> showDestinationDocById();
                case "3" -> createDestinationDoc();
                case "4" -> addItemsToDestinationDoc();
                case "5" -> updateDocumentStatus();
                case "6" -> deleteDestinationDoc();
                case "0" -> {
                    System.out.println("Returning to main menu...");
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    private void showAllDestinationDocs() {
        List<DestinationDoc> docs = destinationDocService.getAllDestinationDocs();
        if (docs.isEmpty()) {
            System.out.println("No destination documents found.");
        } else {
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║      ALL DESTINATION DOCUMENTS     ║");
            System.out.println("╚════════════════════════════════════╝");

            for (int i = 0; i < docs.size(); i++) {
                DestinationDoc doc = docs.get(i);
                System.out.println("\n[" + (i+1) + "] DOCUMENT #" + doc.getDestinationDocId());
                System.out.println("    Transport ID: " + doc.getTransportId());
                System.out.println("    Destination: " + doc.getDestinationId().getAddress());
                System.out.println("    Status: " + doc.getStatus());
                System.out.println("    Items: " + doc.getDeliveryItems().size());
                System.out.println("    " + "-".repeat(40));
            }
        }
    }

    private void showDestinationDocById() {
        System.out.print("Enter document ID: ");
        try {
            int docId = Integer.parseInt(scanner.nextLine().trim());
            Optional<DestinationDoc> doc = destinationDocService.getById(docId);
            doc.ifPresentOrElse(
                    System.out::println,
                    () -> System.out.println("Document not found.")
            );
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID. Please enter a number.");
        }
    }

    /**
     * Creates a new destination document for an existing transport
     */
    private void createDestinationDoc() {
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║       CREATE DESTINATION DOC       ║");
        System.out.println("╚════════════════════════════════════╝");

        // Step 1: Select a transport
        System.out.print("Enter Transport ID: ");
        try {
            int transportId = Integer.parseInt(scanner.nextLine().trim());
            Optional<Transport> transportOpt = transportService.getTransportById(transportId);

            if (transportOpt.isEmpty()) {
                System.out.println("Transport not found. Please create a transport first.");
                return;
            }

            Transport transport = transportOpt.get();

            // Step 2: Select a destination site
            List<Site> sites = siteService.getAllSites();
            if (sites.isEmpty()) {
                System.out.println("No sites available. Please add sites first.");
                return;
            }

            System.out.println("\nAvailable destination sites:");
            for (int i = 0; i < sites.size(); i++) {
                Site site = sites.get(i);
                System.out.println((i+1) + ". " + site.getSiteId() + " - " + site.getAddress());
            }

            System.out.print("Select destination site (number): ");
            int siteChoice = Integer.parseInt(scanner.nextLine().trim());

            if (siteChoice < 1 || siteChoice > sites.size()) {
                System.out.println("Invalid selection.");
                return;
            }

            Site selectedSite = sites.get(siteChoice - 1);

            // Step 3: Create the document
            int docId = generateNextDocId();
            DestinationDoc newDoc = new DestinationDoc(docId, transportId, selectedSite, "PLANNED");
            destinationDocService.saveDestinationDoc(newDoc);

            // Step 4: Add to transport
            if (transportService.addDestinationDocToTransport(transportId, newDoc)) {
                System.out.println("Destination document created and added to transport.");
                System.out.println("Document ID: " + docId);
            } else {
                System.out.println("Error adding document to transport.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        }
    }

    /**
     * Add items to an existing destination document
     */
    private void addItemsToDestinationDoc() {
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║      ADD ITEMS TO DESTINATION      ║");
        System.out.println("╚════════════════════════════════════╝");

        // Step 1: Select a document
        System.out.print("Enter destination document ID: ");
        try {
            int docId = Integer.parseInt(scanner.nextLine().trim());
            Optional<DestinationDoc> docOpt = destinationDocService.getById(docId);

            if (docOpt.isEmpty()) {
                System.out.println("Document not found.");
                return;
            }

            DestinationDoc doc = docOpt.get();

            // Show current items
            List<DeliveredItem> currentItems = doc.getDeliveryItems();
            if (!currentItems.isEmpty()) {
                System.out.println("\nCurrent items in document:");
                for (int i = 0; i < currentItems.size(); i++) {
                    DeliveredItem item = currentItems.get(i);
                    System.out.println((i+1) + ". Product: " + item.getProductId() + ", Quantity: " + item.getQuantity());
                }
            }

            // Add new items
            boolean addMore = true;
            while (addMore) {
                // Get product ID
                System.out.print("\nEnter product ID: ");
                String productId = scanner.nextLine().trim();

                // Check if product ID already exists in document
                boolean alreadyExists = currentItems.stream()
                        .anyMatch(item -> item.getProductId().equals(productId));

                if (alreadyExists) {
                    System.out.println("This product already exists in the document. Use update functionality instead.");
                    continue;
                }

                // Get quantity
                System.out.print("Enter quantity: ");
                try {
                    int quantity = Integer.parseInt(scanner.nextLine().trim());

                    // Create and add the item
                    DeliveredItem newItem = new DeliveredItem(productId, quantity);
                    boolean added = deliveredItemService.addDeliveredItem(docId, newItem);

                    if (added) {
                        System.out.println("Item added successfully.");
                    } else {
                        System.out.println("Failed to add item.");
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Invalid quantity. Please enter a number.");
                }

                // Ask if user wants to add more items
                System.out.print("\nAdd another item? (y/n): ");
                String choice = scanner.nextLine().trim().toLowerCase();
                addMore = choice.equals("y") || choice.equals("yes");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid document ID. Please enter a number.");
        }
    }

    /**
     * Update the status of a destination document
     */
    private void updateDocumentStatus() {
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║       UPDATE DOCUMENT STATUS       ║");
        System.out.println("╚════════════════════════════════════╝");

        System.out.print("Enter document ID: ");
        try {
            int docId = Integer.parseInt(scanner.nextLine().trim());
            Optional<DestinationDoc> docOpt = destinationDocService.getById(docId);

            if (docOpt.isEmpty()) {
                System.out.println("Document not found.");
                return;
            }

            DestinationDoc doc = docOpt.get();
            System.out.println("Current status: " + doc.getStatus());

            System.out.println("\nAvailable statuses:");
            System.out.println("1. PLANNED");
            System.out.println("2. IN_PROGRESS");
            System.out.println("3. COMPLETED");
            System.out.println("4. CANCELLED");

            System.out.print("Select new status (number): ");
            int statusChoice = Integer.parseInt(scanner.nextLine().trim());

            String newStatus;
            switch (statusChoice) {
                case 1 -> newStatus = "PLANNED";
                case 2 -> newStatus = "IN_PROGRESS";
                case 3 -> newStatus = "COMPLETED";
                case 4 -> newStatus = "CANCELLED";
                default -> {
                    System.out.println("Invalid selection.");
                    return;
                }
            }

            boolean updated = destinationDocService.updateStatus(docId, newStatus);
            if (updated) {
                System.out.println("Document status updated to: " + newStatus);
            } else {
                System.out.println("Failed to update status.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    private void deleteDestinationDoc() {
        System.out.print("Enter document ID to delete: ");
        try {
            int docId = Integer.parseInt(scanner.nextLine().trim());
            boolean deleted = destinationDocService.deleteDestinationDoc(docId);
            if (deleted) {
                System.out.println("Document deleted successfully.");
            } else {
                System.out.println("Document not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID. Please enter a number.");
        }
    }
}