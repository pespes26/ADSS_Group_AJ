package com.superli.deliveries.application.controllers;

import com.superli.deliveries.domain.core.DeliveredItem;
import com.superli.deliveries.domain.core.DestinationDoc;
import com.superli.deliveries.domain.core.Site;
import com.superli.deliveries.domain.core.Transport;
import com.superli.deliveries.application.services.DeliveredItemService;
import com.superli.deliveries.application.services.DestinationDocService;
import com.superli.deliveries.application.services.SiteService;
import com.superli.deliveries.application.services.TransportService;
import com.superli.deliveries.Mappers.DestinationDocMapper;
import com.superli.deliveries.Mappers.SiteMapper;
import com.superli.deliveries.Mappers.TransportMapper;
import com.superli.deliveries.dto.DestinationDocDTO;
import com.superli.deliveries.dto.SiteDTO;
import com.superli.deliveries.dto.TransportDTO;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

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
        List<DestinationDoc> existingDocs = destinationDocService.getAllDocs();
        if (!existingDocs.isEmpty()) {
            nextDocId = existingDocs.stream()
                    .mapToInt(doc -> Integer.parseInt(doc.getDestinationDocId()))
                    .max()
                    .orElse(0) + 1;
        }
    }

    /**
     * Generate a new unique document ID
     */
    private String generateNextDocId() {
        return String.valueOf(nextDocId++);
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
        List<DestinationDoc> docs = destinationDocService.getAllDocs();
        if (docs.isEmpty()) {
            System.out.println("No destination documents found.");
        } else {
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║      ALL DESTINATION DOCUMENTS     ║");
            System.out.println("╚════════════════════════════════════╝");

            for (int i = 0; i < docs.size(); i++) {
                DestinationDoc doc = docs.get(i);
                DestinationDocDTO docDTO = DestinationDocMapper.toDTO(doc);
                System.out.println("\n[" + (i+1) + "] DOCUMENT #" + docDTO.getDestinationDocId());
                System.out.println("    Transport ID: " + docDTO.getTransportId());
                System.out.println("    Site ID: " + docDTO.getSiteId());
                System.out.println("    Status: " + docDTO.getStatus());
                System.out.println("    " + "-".repeat(40));
            }
        }
    }

    private void showDestinationDocById() {
        System.out.print("Enter document ID: ");
        String docId = scanner.nextLine().trim();
        Optional<DestinationDoc> docOpt = destinationDocService.getDocById(docId);
        if (docOpt.isPresent()) {
            DestinationDoc doc = docOpt.get();
            DestinationDocDTO docDTO = DestinationDocMapper.toDTO(doc);
            System.out.println("Document ID: " + docDTO.getDestinationDocId());
            System.out.println("Transport ID: " + docDTO.getTransportId());
            System.out.println("Site ID: " + docDTO.getSiteId());
            System.out.println("Status: " + docDTO.getStatus());
        } else {
            System.out.println("Document not found.");
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
        String transportId = scanner.nextLine().trim();
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
            SiteDTO siteDTO = SiteMapper.toDTO(site);
            System.out.println((i+1) + ". " + siteDTO.getSiteId() + " - " + siteDTO.getAddress());
        }

        System.out.print("Select destination site (number): ");
        try {
            int siteChoice = Integer.parseInt(scanner.nextLine().trim());

            if (siteChoice < 1 || siteChoice > sites.size()) {
                System.out.println("Invalid selection.");
                return;
            }

            Site selectedSite = sites.get(siteChoice - 1);

            // Step 3: Create the document
            String docId = generateNextDocId();
            DestinationDocDTO docDTO = new DestinationDocDTO(docId, transportId, selectedSite.getSiteId(), "PLANNED");
            DestinationDoc newDoc = DestinationDocMapper.fromDTO(docDTO, selectedSite);
            destinationDocService.saveDoc(newDoc);

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
        String docId = scanner.nextLine().trim();
        Optional<DestinationDoc> docOpt = destinationDocService.getDocById(docId);

        if (docOpt.isEmpty()) {
            System.out.println("Document not found.");
            return;
        }

        DestinationDoc doc = docOpt.get();
        DestinationDocDTO docDTO = DestinationDocMapper.toDTO(doc);

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
    }

    /**
     * Update the status of a destination document
     */
    private void updateDocumentStatus() {
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║       UPDATE DOCUMENT STATUS       ║");
        System.out.println("╚════════════════════════════════════╝");

        System.out.print("Enter document ID: ");
        String docId = scanner.nextLine().trim();
        Optional<DestinationDoc> docOpt = destinationDocService.getDocById(docId);

        if (docOpt.isEmpty()) {
            System.out.println("Document not found.");
            return;
        }

        DestinationDoc doc = docOpt.get();
        DestinationDocDTO docDTO = DestinationDocMapper.toDTO(doc);
        System.out.println("Current status: " + docDTO.getStatus());

        System.out.println("\nAvailable statuses:");
        System.out.println("1. PLANNED");
        System.out.println("2. IN_PROGRESS");
        System.out.println("3. COMPLETED");
        System.out.println("4. CANCELLED");

        System.out.print("Select new status (number): ");
        try {
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
        String docId = scanner.nextLine().trim();
        destinationDocService.deleteDoc(docId);
        System.out.println("Document deleted successfully.");
    }
}