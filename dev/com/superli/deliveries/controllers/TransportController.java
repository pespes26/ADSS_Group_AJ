package com.superli.deliveries.controllers;

import com.superli.deliveries.domain.Driver;
import com.superli.deliveries.domain.Transport;
import com.superli.deliveries.domain.Truck;
import com.superli.deliveries.presentation.TransportDetailsView;
import com.superli.deliveries.presentation.TransportSummaryView;
import com.superli.deliveries.service.DriverService;
import com.superli.deliveries.service.TransportService;
import com.superli.deliveries.service.TruckService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class TransportController {

    private final TransportService transportService;
    private final TruckService truckService;
    private final DriverService driverService;
    private final Scanner scanner;

    public TransportController(TransportService transportService, TruckService truckService, DriverService driverService) {
        this.transportService = transportService;
        this.truckService = truckService;
        this.driverService = driverService;
        this.scanner = new Scanner(System.in);
    }

    public void runMenu() {
        while (true) {
            System.out.println("\n=== Transport Menu ===");
            System.out.println("1. Show all transports");
            System.out.println("2. Show transport by ID");
            System.out.println("3. Create transport (auto/manual)");
            System.out.println("0. Back to main menu");
            System.out.print("Your choice: ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1" -> showAllTransports();
                case "2" -> showTransportById();
                case "3" -> createTransport();
                case "0" -> {
                    System.out.println("Returning to main menu...");
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }


    private void showAllTransports() {
        List<TransportSummaryView> summaries = transportService.getAllTransportSummaries();
        if (summaries.isEmpty()) {
            System.out.println("No transports found.");
        } else {
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║           ALL TRANSPORTS           ║");
            System.out.println("╚════════════════════════════════════╝");

            for (int i = 0; i < summaries.size(); i++) {
                TransportSummaryView transport = summaries.get(i);
                System.out.println("\n[" + (i+1) + "] TRANSPORT #" + transport.getTransportId());
                System.out.println("    Status: " + transport.getStatus());
                System.out.println("    Departure: " + transport.getDepartureDateTime());
                System.out.println("    Origin: " + transport.getOriginSite().getAddress());
                System.out.println("    Destinations: " + transport.getDestinationsList().size());
                System.out.println("    Total Weight: " + transport.getTotalWeight() + " kg");
                System.out.println("    " + "-".repeat(40));
            }
        }
    }


    private void showTransportById() {
        System.out.print("Enter Transport ID: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            TransportDetailsView view = transportService.getTransportDetailsViewById(id);
            if (view == null) {
                System.out.println("Transport not found.");
            } else {
                System.out.println(view);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a numeric ID.");
        }
    }

    private void createTransport() {
        System.out.println("\nCreate transport:");
        System.out.println("1. Auto assign truck & driver");
        System.out.println("2. Manual selection");
        System.out.print("Choose option: ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1" -> createTransportAuto();
            case "2" -> createTransportManual();
            default -> System.out.println("Invalid choice.");
        }
    }

    private void createTransportAuto() {
        Optional<Transport> result = transportService.createTransportAuto();
        if (result.isPresent()) {
            Transport transport = result.get();
            System.out.println("Transport created!");
            System.out.println("Selected truck: " + transport.getTruck());
            System.out.println("Selected driver: " + transport.getDriver());
            System.out.println(transport);
        } else {
            System.out.println("No compatible truck and driver found.");
        }
    }

    private void createTransportManual() {
        List<Truck> availableTrucks = truckService.getAvailableTrucks();
        if (availableTrucks.isEmpty()) {
            System.out.println("No available trucks.");
            return;
        }

        System.out.println("Available trucks:");
        for (int i = 0; i < availableTrucks.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, availableTrucks.get(i));
        }

        System.out.print("Choose truck by number: ");
        try {
            int truckChoice = Integer.parseInt(scanner.nextLine());
            if (truckChoice < 1 || truckChoice > availableTrucks.size()) {
                System.out.println("Invalid truck selection.");
                return;
            }

            Truck selectedTruck = availableTrucks.get(truckChoice - 1);
            List<Driver> compatibleDrivers = driverService.getAvailableDrivers().stream()
                    .filter(driver -> driver.getLicenseType() == selectedTruck.getRequiredLicenseType())
                    .toList();

            if (compatibleDrivers.isEmpty()) {
                System.out.println("No compatible drivers for this truck.");
                return;
            }

            System.out.println("Compatible drivers:");
            for (int i = 0; i < compatibleDrivers.size(); i++) {
                System.out.printf("%d. %s\n", i + 1, compatibleDrivers.get(i));
            }

            System.out.print("Choose driver by number: ");
            int driverChoice = Integer.parseInt(scanner.nextLine());
            if (driverChoice < 1 || driverChoice > compatibleDrivers.size()) {
                System.out.println("Invalid driver selection.");
                return;
            }

            Driver selectedDriver = compatibleDrivers.get(driverChoice - 1);
            Optional<Transport> result = transportService.createTransportManual(selectedTruck, selectedDriver);

            if (result.isPresent()) {
                System.out.println("Transport created manually:");
                System.out.println(result.get());
            } else {
                System.out.println("Could not create transport.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
}