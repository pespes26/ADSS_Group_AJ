package com.superli.deliveries.controllers;

import com.superli.deliveries.domain.Truck;
import com.superli.deliveries.service.TruckService;

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
            System.out.println("0. Back to main menu");
            System.out.print("Your choice: ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1" -> showAllTrucks();
                case "2" -> showAvailableTrucks();
                case "0" -> {
                    System.out.println("Returning to main menu...");
                    return;
                }
                default -> System.out.println("❌ Invalid choice. Try again.");
            }
        }
    }

    private void showAllTrucks() {
        List<Truck> trucks = truckService.getAllTrucks();
        if (trucks.isEmpty()) {
            System.out.println("No trucks found.");
        } else {
            trucks.forEach(System.out::println);
        }
    }

    private void showAvailableTrucks() {
        List<Truck> available = truckService.getAvailableTrucks();
        if (available.isEmpty()) {
            System.out.println("No available trucks at the moment.");
        } else {
            available.forEach(System.out::println);
        }
    }
}
