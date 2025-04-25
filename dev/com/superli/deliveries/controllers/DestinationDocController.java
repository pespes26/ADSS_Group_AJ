package com.superli.deliveries.controllers;

import com.superli.deliveries.domain.DestinationDoc;
import com.superli.deliveries.service.DestinationDocService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Controller for managing DestinationDoc (delivery documents) via console UI.
 */
public class DestinationDocController {

    private final DestinationDocService destinationDocService;
    private final Scanner scanner;

    public DestinationDocController(DestinationDocService destinationDocService) {
        this.destinationDocService = destinationDocService;
        this.scanner = new Scanner(System.in);
    }

    public void runMenu() {
        while (true) {
            System.out.println("\n=== Destination Document Menu ===");
            System.out.println("1. Show all destination documents");
            System.out.println("2. Show destination document by ID");
            System.out.println("3. Delete destination document");
            System.out.println("0. Back to main menu");
            System.out.print("Your choice: ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1" -> showAllDestinationDocs();
                case "2" -> showDestinationDocById();
                case "3" -> deleteDestinationDoc();
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
            docs.forEach(System.out::println);
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
            System.out.println("❌ Invalid ID. Please enter a number.");
        }
    }

    private void deleteDestinationDoc() {
        System.out.print("Enter document ID to delete: ");
        try {
            int docId = Integer.parseInt(scanner.nextLine().trim());
            boolean deleted = destinationDocService.deleteDestinationDoc(docId);
            if (deleted) {
                System.out.println("✅ Document deleted successfully.");
            } else {
                System.out.println("❌ Document not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid ID. Please enter a number.");
        }
    }
}
