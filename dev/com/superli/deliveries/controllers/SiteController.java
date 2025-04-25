package com.superli.deliveries.controllers;

import com.superli.deliveries.domain.Site;
import com.superli.deliveries.domain.Zone;
import com.superli.deliveries.service.SiteService;

import java.util.List;
import java.util.Scanner;

/**
 * Controller for managing sites and shipment zones via console.
 */
public class SiteController {

    private final SiteService siteService;
    private final Scanner scanner;

    public SiteController(SiteService siteService) {
        this.siteService = siteService;
        this.scanner = new Scanner(System.in);
    }

    public void runMenu() {
        while (true) {
            System.out.println("\n=== Site Menu ===");
            System.out.println("1. Show all sites");
            System.out.println("2. Show all shipment zones");
            System.out.println("0. Back to main menu");
            System.out.print("Your choice: ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1" -> showAllSites();
                case "2" -> showAllZones();
                case "0" -> {
                    System.out.println("Returning to main menu...");
                    return;
                }
                default -> System.out.println("❌ Invalid choice. Try again.");
            }
        }
    }

    private void showAllSites() {
        List<Site> sites = siteService.getAllSites();
        if (sites.isEmpty()) {
            System.out.println("No sites found.");
        } else {
            sites.forEach(System.out::println);
        }
    }

    private void showAllZones() {
        List<Zone> zones = siteService.getAllZones();
        if (zones.isEmpty()) {
            System.out.println("No shipment zones found.");
        } else {
            zones.forEach(System.out::println);
        }
    }
}
