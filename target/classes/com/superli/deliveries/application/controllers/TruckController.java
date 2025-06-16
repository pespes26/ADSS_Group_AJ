package com.superli.deliveries.application.controllers;

import com.superli.deliveries.domain.core.LicenseType;
import com.superli.deliveries.domain.core.Truck;
import com.superli.deliveries.application.services.TruckService;
import com.superli.deliveries.Mappers.TruckMapper;
import com.superli.deliveries.dto.del.TruckDTO;

import java.util.List;
import java.util.Scanner;

/**
 * Controller for managing truck-related actions in a console-based UI.
 */
public class TruckController {

    private final TruckService truckService;
    private final Scanner scanner;

    public TruckController(TruckService truckService) {
        this.truckService = truckService;
        this.scanner = new Scanner(System.in);
    }

    public void runMenu() {
        while (true) {
            System.out.println("\n=== Truck Menu ===");
            System.out.println("1. Show all trucks");
            System.out.println("2. Show available trucks");
            System.out.println("3. Add new truck");
            System.out.println("4. Remove truck by plate number");
            System.out.println("5. Mark truck as unavailable");
            System.out.println("6. Mark truck as available");
            System.out.println("0. Back to main menu");
            System.out.print("Your choice: ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1" -> showAllTrucks();
                case "2" -> showAvailableTrucks();
                case "3" -> addTruck();
                case "4" -> removeTruck();
                case "5" -> markTruckUnavailable();
                case "6" -> markTruckAvailable();
                case "0" -> {
                    System.out.println("Returning to main menu...");
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void showAllTrucks() {
        List<Truck> trucks = truckService.getAllTrucks();
        if (trucks.isEmpty()) {
            System.out.println("No trucks found.");
        } else {
            System.out.println("\nAll Trucks:");

            for (Truck truck : trucks) {
                TruckDTO truckDTO = TruckMapper.toDTO(truck);
                System.out.printf("Plane Number: %-7s | Model: %-15s | License: %-7s | Status: %-10s%n",
                    truckDTO.getlicensePlate(),
                    truckDTO.getModel(),
                    truckDTO.getRequiredLicenseType(),
                    truckService.isTruckAvailable(truckDTO.getlicensePlate()) ? "Available" : "Unavailable");
            }
        }
    }

    private void showAvailableTrucks() {
        List<Truck> available = truckService.getAvailableTrucks();
        if (available.isEmpty()) {
            System.out.println("No available trucks at the moment.");
        } else {
            System.out.println("\nAvailable Trucks:");

            for (Truck truck : available) {
                TruckDTO truckDTO = TruckMapper.toDTO(truck);
                System.out.printf("Plane Number: %-7s | Model: %-15s | License: %-7s | Status: %-10s%n",
                    truckDTO.getlicensePlate(),
                    truckDTO.getModel(),
                    truckDTO.getRequiredLicenseType(),
                    "Available");
            }
        }
    }

    private void addTruck() {
        try {
            System.out.print("Enter plate number: ");
            String plateNum = scanner.nextLine().trim();

            if (truckService.getTruckByPlate(plateNum).isPresent()) {
                System.out.println("Truck with this plate number already exists.");
                return;
            }

            System.out.print("Enter model: ");
            String model = scanner.nextLine().trim();

            System.out.print("Enter net weight (float): ");
            float netWeight = Float.parseFloat(scanner.nextLine().trim());

            System.out.print("Enter max weight (float): ");
            float maxWeight = Float.parseFloat(scanner.nextLine().trim());

            // Display available license types
            System.out.println("Available license types required for this truck:");
            for (LicenseType type : LicenseType.values()) {
                System.out.println("- " + type.getValue());
            }

            LicenseType selectedLicenseType = null;
            while (selectedLicenseType == null) {
                System.out.print("Enter required license type (");
                for (LicenseType type : LicenseType.values()) {
                    System.out.print(type.getValue() + " ");
                }
                System.out.print("): ");
                String licenseInput = scanner.nextLine().trim().toUpperCase();

                try {
                    selectedLicenseType = LicenseType.valueOf(licenseInput);
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid license type. Please select from the list.");
                    continue;
                }
            }

            TruckDTO truckDTO = new TruckDTO(plateNum, model, netWeight, maxWeight, selectedLicenseType);
            Truck truck = TruckMapper.fromDTO(truckDTO);
            truckService.saveTruck(truck);
            System.out.println("Truck added successfully.");
        } catch (Exception e) {
            System.out.println("Error adding truck: " + e.getMessage());
        }
    }

    private void removeTruck() {
        System.out.print("Enter plate number to remove: ");
        String plate = scanner.nextLine().trim();
        if (truckService.getTruckByPlate(plate).isEmpty()) {
            System.out.println("Truck not found.");
            return;
        }
        truckService.deleteTruck(plate);
        System.out.println("Truck removed successfully.");
    }

    private void markTruckUnavailable() {
        System.out.print("Enter plate number to mark as unavailable: ");
        String plate = scanner.nextLine().trim();
        if (truckService.getTruckByPlate(plate).isEmpty()) {
            System.out.println("Truck not found.");
        } else {
            truckService.markTruckAsUnavailable(plate);
            System.out.println("Truck marked as unavailable.");
        }
    }

    private void markTruckAvailable() {
        System.out.print("Enter plate number to mark as available: ");
        String plate = scanner.nextLine().trim();
        if (truckService.getTruckByPlate(plate).isEmpty()) {
            System.out.println("Truck not found.");
        } else {
            truckService.markTruckAsAvailable(plate);
            System.out.println("Truck marked as available.");
        }
    }
}