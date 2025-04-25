package com.superli.deliveries.controllers;

import com.superli.deliveries.domain.Zone;
import com.superli.deliveries.service.ZoneService;

import java.util.List;
import java.util.Scanner;

public class ZoneController {

    private final Scanner scanner;
    private final ZoneService zoneService;

    public ZoneController(ZoneService zoneService, Scanner scanner) {
        this.zoneService = zoneService;
        this.scanner = scanner;
    }

    public void runMenu() {
        boolean back = false;

        while (!back) {
            System.out.println("\n--- Zone Management ---");
            System.out.println("1. Add Zone");
            System.out.println("2. View All Zones");
            System.out.println("3. Delete Zone");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1 -> addZone();
                case 2 -> viewAllZones();
                case 3 -> deleteZone();
                case 0 -> back = true;
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void addZone() {
        System.out.print("Enter Zone ID: ");
        String id = scanner.nextLine();

        System.out.print("Enter Zone Name: ");
        String name = scanner.nextLine();

        Zone zone = new Zone(id, name);
        zoneService.saveZone(zone);
        System.out.println("Zone added successfully.");
    }

    private void viewAllZones() {
        List<Zone> zones = zoneService.getAllZones();
        if (zones.isEmpty()) {
            System.out.println("No zones found.");
        } else {
            System.out.println("\nList of Zones:");
            for (Zone zone : zones) {
                System.out.println(zone);
            }
        }
    }

    private void deleteZone() {
        System.out.print("Enter Zone ID to delete: ");
        String id = scanner.nextLine();

        boolean removed = zoneService.deleteZone(id);
        if (removed) {
            System.out.println("Zone deleted successfully.");
        } else {
            System.out.println("Zone not found.");
        }
    }
}
