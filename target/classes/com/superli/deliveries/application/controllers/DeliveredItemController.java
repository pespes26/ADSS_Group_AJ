package com.superli.deliveries.application.controllers;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.superli.deliveries.application.services.DeliveredItemService;
import com.superli.deliveries.application.services.ProductService;
import com.superli.deliveries.application.services.TransportService;
import com.superli.deliveries.domain.core.DeliveredItem;

public class DeliveredItemController {
    private final DeliveredItemService deliveredItemService;
    private final ProductService productService;
    private final TransportService transportService;
    private final Scanner scanner;

    public DeliveredItemController(DeliveredItemService deliveredItemService, 
                                 ProductService productService,
                                 TransportService transportService) {
        this.deliveredItemService = deliveredItemService;
        this.productService = productService;
        this.transportService = transportService;
        this.scanner = new Scanner(System.in);
    }

    public void runMenu() {
        while (true) {
            System.out.println("\n=== Delivered Items Menu ===");
            System.out.println("1. Show all delivered items");
            System.out.println("2. Add new delivered item");
            System.out.println("3. Edit delivered item");
            System.out.println("4. Delete delivered item");
            System.out.println("5. Search items by transport");
            System.out.println("6. Search items by product");
            System.out.println("0. Back to main menu");
            System.out.print("Your choice: ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1" -> showAllDeliveredItems();
                case "2" -> addDeliveredItem();
                case "3" -> editDeliveredItem();
                case "4" -> deleteDeliveredItem();
                case "5" -> searchItemsByTransport();
                case "6" -> searchItemsByProduct();
                case "0" -> {
                    System.out.println("Returning to main menu...");
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void showAllDeliveredItems() {
        List<DeliveredItem> items = deliveredItemService.getAllDeliveredItems();
        if (items.isEmpty()) {
            System.out.println("No delivered items found.");
        } else {
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║         DELIVERED ITEMS            ║");
            System.out.println("╚════════════════════════════════════╝");

            for (int i = 0; i < items.size(); i++) {
                DeliveredItem item = items.get(i);
                System.out.println("\n[" + (i+1) + "] ITEM ID: " + item.getItemId());
                System.out.println("    Destination Doc ID: " + item.getDestinationDocId());
                System.out.println("    Product ID: " + item.getProductId());
                System.out.println("    Quantity: " + item.getQuantity());
                System.out.println("    " + "-".repeat(40));
            }
        }
    }

    private void addDeliveredItem() {
        System.out.print("Enter Item ID: ");
        String id = scanner.nextLine().trim();

        if (deliveredItemService.getDeliveredItemById(id).isPresent()) {
            System.out.println("Item with this ID already exists.");
            return;
        }

        System.out.print("Enter Destination Doc ID: ");
        String destinationDocId = scanner.nextLine().trim();
        if (transportService.getTransportById(destinationDocId).isEmpty()) {
            System.out.println("Transport not found.");
            return;
        }

        System.out.print("Enter Product ID: ");
        String productId = scanner.nextLine().trim();
        if (productService.getProductById(productId).isEmpty()) {
            System.out.println("Product not found.");
            return;
        }

        System.out.print("Enter Quantity: ");
        try {
            int quantity = Integer.parseInt(scanner.nextLine().trim());
            if (quantity <= 0) {
                System.out.println("Quantity must be positive.");
                return;
            }

            DeliveredItem item = new DeliveredItem(id, destinationDocId, productId, quantity);
            deliveredItemService.saveDeliveredItem(item);
            System.out.println("Delivered item added successfully.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity value. Please enter a valid number.");
        }
    }

    private void editDeliveredItem() {
        showAllDeliveredItems();
        System.out.print("\nEnter Item ID to edit: ");
        String id = scanner.nextLine().trim();

        Optional<DeliveredItem> itemOpt = deliveredItemService.getDeliveredItemById(id);
        if (itemOpt.isEmpty()) {
            System.out.println("Item not found.");
            return;
        }

        DeliveredItem item = itemOpt.get();
        System.out.println("\nCurrent Item Information:");
        System.out.println("ID: " + item.getItemId());
        System.out.println("Destination Doc ID: " + item.getDestinationDocId());
        System.out.println("Product ID: " + item.getProductId());
        System.out.println("Quantity: " + item.getQuantity());

        System.out.print("\nEnter new Destination Doc ID (or press Enter to keep current): ");
        String newDestinationDocId = scanner.nextLine().trim();
        if (!newDestinationDocId.isEmpty()) {
            if (transportService.getTransportById(newDestinationDocId).isEmpty()) {
                System.out.println("Transport not found. Update cancelled.");
                return;
            }
            item = new DeliveredItem(item.getItemId(), newDestinationDocId, item.getProductId(), item.getQuantity());
        }

        System.out.print("Enter new Product ID (or press Enter to keep current): ");
        String newProductId = scanner.nextLine().trim();
        if (!newProductId.isEmpty()) {
            if (productService.getProductById(newProductId).isEmpty()) {
                System.out.println("Product not found. Update cancelled.");
                return;
            }
            item = new DeliveredItem(item.getItemId(), item.getDestinationDocId(), newProductId, item.getQuantity());
        }

        System.out.print("Enter new Quantity (or press Enter to keep current): ");
        String quantityInput = scanner.nextLine().trim();
        if (!quantityInput.isEmpty()) {
            try {
                int newQuantity = Integer.parseInt(quantityInput);
                if (newQuantity <= 0) {
                    System.out.println("Quantity must be positive. Update cancelled.");
                    return;
                }
                item = new DeliveredItem(item.getItemId(), item.getDestinationDocId(), item.getProductId(), newQuantity);
            } catch (NumberFormatException e) {
                System.out.println("Invalid quantity value. Update cancelled.");
                return;
            }
        }

        deliveredItemService.saveDeliveredItem(item);
        System.out.println("Delivered item updated successfully.");
    }

    private void deleteDeliveredItem() {
        showAllDeliveredItems();
        System.out.print("\nEnter Item ID to delete: ");
        String id = scanner.nextLine().trim();

        if (deliveredItemService.getDeliveredItemById(id).isEmpty()) {
            System.out.println("Item not found.");
            return;
        }

        System.out.print("Are you sure you want to delete this item? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        if (confirmation.equals("yes")) {
            deliveredItemService.deleteDeliveredItem(id);
            System.out.println("Delivered item deleted successfully.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private void searchItemsByTransport() {
        System.out.print("Enter Destination Doc ID to search: ");
        String destinationDocId = scanner.nextLine().trim();
        
        List<DeliveredItem> items = deliveredItemService.getDeliveredItemsByTransport(destinationDocId);
        if (items.isEmpty()) {
            System.out.println("No items found for this transport.");
        } else {
            System.out.println("\nItems for Transport " + destinationDocId + ":");
            for (DeliveredItem item : items) {
                System.out.println("Item ID: " + item.getItemId());
                System.out.println("Destination Doc ID: " + item.getDestinationDocId());
                System.out.println("Quantity: " + item.getQuantity());
                System.out.println("-".repeat(40));
            }
        }
    }

    private void searchItemsByProduct() {
        System.out.print("Enter Product ID to search: ");
        String productId = scanner.nextLine().trim();
        
        List<DeliveredItem> items = deliveredItemService.getDeliveredItemsByProduct(productId);
        if (items.isEmpty()) {
            System.out.println("No items found for this product.");
        } else {
            System.out.println("\nItems for Product " + productId + ":");
            for (DeliveredItem item : items) {
                System.out.println("Item ID: " + item.getItemId());
                System.out.println("Destination Doc ID: " + item.getDestinationDocId());
                System.out.println("Quantity: " + item.getQuantity());
                System.out.println("-".repeat(40));
            }
        }
    }
} 