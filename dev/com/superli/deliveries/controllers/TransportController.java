package com.superli.deliveries.controllers;

import com.superli.deliveries.domain.Transport;
import com.superli.deliveries.service.TransportService;
import com.superli.deliveries.presentation.TransportDetailsView;
import com.superli.deliveries.presentation.TransportSummaryView;

import java.util.List;
import java.util.Scanner;

/**
 * Controller responsible for console-based transport management.
 */
public class TransportController {

    private final TransportService transportService;
    private final Scanner scanner;

    public TransportController(TransportService transportService) {
        this.transportService = transportService;
        this.scanner = new Scanner(System.in);
    }

    public void runMenu() {
        while (true) {
            System.out.println("\n=== Transport Menu ===");
            System.out.println("1. Show all transports");
            System.out.println("2. Show transport by ID");
            System.out.println("0. Back to main menu");
            System.out.print("Your choice: ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1" -> showAllTransports();
                case "2" -> showTransportById();
                case "0" -> {
                    System.out.println("Returning to main menu...");
                    return;
                }
                default -> System.out.println("❌ Invalid choice. Try again.");
            }
        }
    }

    private void showAllTransports() {
        List<TransportSummaryView> summaries = transportService.getAllTransportSummaries();
        if (summaries.isEmpty()) {
            System.out.println("No transports found.");
        } else {
            summaries.forEach(System.out::println);
        }
    }

    private void showTransportById() {
        System.out.print("Enter Transport ID: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            TransportDetailsView view = transportService.getTransportDetailsViewById(id);
            if (view == null) {
                System.out.println("❌ Transport not found.");
            } else {
                System.out.println(view);
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input. Please enter a numeric ID.");
        }
    }
}
