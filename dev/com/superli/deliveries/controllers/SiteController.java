package com.superli.deliveries.controllers;

import com.superli.deliveries.domain.Site;
import com.superli.deliveries.domain.Zone;
import com.superli.deliveries.service.SiteService;
import com.superli.deliveries.service.ZoneService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class SiteController {

    private final SiteService siteService;
    private final ZoneService zoneService;
    private final Scanner scanner;

    public SiteController(SiteService siteService, ZoneService zoneService) {
        this.siteService = siteService;
        this.zoneService = zoneService;
        this.scanner = new Scanner(System.in);
    }

    public void runMenu() {
        while (true) {
            System.out.println("\n=== Site Menu ===");
            System.out.println("1. Show all sites");
            System.out.println("2. Show all zones");
            System.out.println("3. Add new site");
            System.out.println("4. Delete site");
            System.out.println("5. Assign zone to site");
            System.out.println("0. Back to main menu");
            System.out.print("Your choice: ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1" -> showAllSites();
                case "2" -> showAllZones();
                case "3" -> addSite();
                case "4" -> deleteSite();
                case "5" -> assignZoneToSite();
                case "0" -> {
                    System.out.println("Returning to main menu...");
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void showAllSites() {
        List<Site> sites = siteService.getAllSites();
        if (sites.isEmpty()) {
            System.out.println("No sites found.");
        } else {
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║              ALL SITES             ║");
            System.out.println("╚════════════════════════════════════╝");

            for (int i = 0; i < sites.size(); i++) {
                Site site = sites.get(i);
                System.out.println("\n[" + (i+1) + "] SITE: " + site.getSiteId());
                System.out.println("    Address: " + site.getAddress());
                if (site.getPhoneNumber() != null && !site.getPhoneNumber().isEmpty()) {
                    System.out.println("    Phone: " + site.getPhoneNumber());
                }
                if (site.getContactPersonName() != null && !site.getContactPersonName().isEmpty()) {
                    System.out.println("    Contact: " + site.getContactPersonName());
                }
                System.out.println("    Zone: " + site.getZone().getName() + " (" + site.getZone().getZoneId() + ")");
                System.out.println("    " + "-".repeat(40));
            }
        }
    }
    private void showAllZones() {
        List<Zone> zones = siteService.getAllZones();
        if (zones.isEmpty()) {
            System.out.println("No zones found.");
        } else {
            zones.forEach(System.out::println);
        }
    }

    private void addSite() {
        System.out.print("Enter Site ID: ");
        String siteId = scanner.nextLine().trim();

        System.out.print("Enter Address: ");
        String address = scanner.nextLine().trim();

        System.out.print("Enter Phone Number (optional): ");
        String phone = scanner.nextLine().trim();
        phone = phone.isEmpty() ? null : phone;

        System.out.print("Enter Contact Person Name (optional): ");
        String contact = scanner.nextLine().trim();
        contact = contact.isEmpty() ? null : contact;

        System.out.print("Enter Zone ID: ");
        String zoneId = scanner.nextLine().trim();
        Optional<Zone> zoneOpt = zoneService.getZoneById(zoneId);

        if (zoneOpt.isPresent()) {
            Site site = new Site(siteId, address, phone, contact, zoneOpt.get());
            siteService.saveSite(site);
            System.out.println("Site added successfully.");
        } else {
            System.out.println("Zone not found. Cannot add site.");
        }
    }

    private void deleteSite() {
        System.out.print("Enter Site ID to delete: ");
        String siteId = scanner.nextLine().trim();
        siteService.deleteSite(siteId);
        System.out.println("Site deleted.");
    }

    private void assignZoneToSite() {
        System.out.print("Enter Site ID: ");
        String siteId = scanner.nextLine().trim();

        System.out.print("Enter Zone ID to assign: ");
        String zoneId = scanner.nextLine().trim();

        Optional<Zone> zoneOpt = zoneService.getZoneById(zoneId);
        if (zoneOpt.isPresent()) {
            boolean updated = siteService.updateZone(siteId, zoneOpt.get());
            if (updated) {
                System.out.println("Zone updated for site.");
            } else {
                System.out.println("Site not found.");
            }
        } else {
            System.out.println("Zone not found.");
        }
    }
}
