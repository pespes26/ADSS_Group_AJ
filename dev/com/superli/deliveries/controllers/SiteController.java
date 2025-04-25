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
            System.out.println("2. Add new site");
            System.out.println("3. Edit site details");
            System.out.println("4. Delete site");
            System.out.println("5. Assign zone to site");
            System.out.println("0. Back to main menu");
            System.out.print("Your choice: ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1" -> showAllSites();
                case "2" -> addSite();
                case "3" -> editSite();
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

        // Show available zones to choose from
        List<Zone> zones = zoneService.getAllZones();
        if (zones.isEmpty()) {
            System.out.println("No zones available. Please create a zone first.");
            return;
        }

        System.out.println("\nAvailable zones:");
        for (int i = 0; i < zones.size(); i++) {
            Zone zone = zones.get(i);
            System.out.println((i+1) + ". " + zone.getName() + " (" + zone.getZoneId() + ")");
        }

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

    private void editSite() {
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║           EDIT SITE INFO           ║");
        System.out.println("╚════════════════════════════════════╝");

        // First, show all sites and ask for the ID to edit
        showAllSites();

        System.out.print("\nEnter the ID of the site you want to edit: ");
        String siteId = scanner.nextLine().trim();

        Optional<Site> siteOpt = siteService.getSiteById(siteId);
        if (siteOpt.isEmpty()) {
            System.out.println("Site not found. Please try again.");
            return;
        }

        Site site = siteOpt.get();

        // Display current info
        System.out.println("\nCurrent Site Information:");
        System.out.println("ID: " + site.getSiteId());
        System.out.println("Address: " + site.getAddress());
        System.out.println("Phone Number: " + (site.getPhoneNumber() != null ? site.getPhoneNumber() : "N/A"));
        System.out.println("Contact Person: " + (site.getContactPersonName() != null ? site.getContactPersonName() : "N/A"));
        System.out.println("Zone: " + site.getZone().getName() + " (" + site.getZone().getZoneId() + ")");

        // Edit menu
        System.out.println("\nWhat would you like to edit?");
        System.out.println("1. Address");
        System.out.println("2. Phone Number");
        System.out.println("3. Contact Person");
        System.out.println("4. Zone");
        System.out.println("0. Cancel");
        System.out.print("Your choice: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1" -> editSiteAddress(site);
            case "2" -> editSitePhoneNumber(site);
            case "3" -> editSiteContactPerson(site);
            case "4" -> editSiteZone(site);
            case "0" -> System.out.println("Edit cancelled.");
            default -> System.out.println("Invalid choice. Edit cancelled.");
        }
    }

    private void editSiteAddress(Site site) {
        System.out.println("\nCurrent address: " + site.getAddress());
        System.out.print("Enter new address (or press Enter to keep current): ");
        String newAddress = scanner.nextLine().trim();

        if (newAddress.isEmpty()) {
            System.out.println("Address not changed.");
            return;
        }

        site.setAddress(newAddress);
        siteService.saveSite(site);
        System.out.println("Address updated successfully to: " + newAddress);
    }

    private void editSitePhoneNumber(Site site) {
        System.out.println("\nCurrent phone number: " + (site.getPhoneNumber() != null ? site.getPhoneNumber() : "N/A"));
        System.out.print("Enter new phone number (or press Enter to keep current, 'clear' to remove): ");
        String newPhone = scanner.nextLine().trim();

        if (newPhone.isEmpty()) {
            System.out.println("Phone number not changed.");
            return;
        }

        if (newPhone.equalsIgnoreCase("clear")) {
            site.setPhoneNumber(null);
            siteService.saveSite(site);
            System.out.println("Phone number cleared.");
        } else {
            site.setPhoneNumber(newPhone);
            siteService.saveSite(site);
            System.out.println("Phone number updated successfully to: " + newPhone);
        }
    }

    private void editSiteContactPerson(Site site) {
        System.out.println("\nCurrent contact person: " + (site.getContactPersonName() != null ? site.getContactPersonName() : "N/A"));
        System.out.print("Enter new contact person (or press Enter to keep current, 'clear' to remove): ");
        String newContact = scanner.nextLine().trim();

        if (newContact.isEmpty()) {
            System.out.println("Contact person not changed.");
            return;
        }

        if (newContact.equalsIgnoreCase("clear")) {
            site.setContactPersonName(null);
            siteService.saveSite(site);
            System.out.println("Contact person cleared.");
        } else {
            site.setContactPersonName(newContact);
            siteService.saveSite(site);
            System.out.println("Contact person updated successfully to: " + newContact);
        }
    }

    private void editSiteZone(Site site) {
        System.out.println("\nCurrent zone: " + site.getZone().getName() + " (" + site.getZone().getZoneId() + ")");

        // Show available zones
        List<Zone> zones = zoneService.getAllZones();
        System.out.println("\nAvailable zones:");
        for (int i = 0; i < zones.size(); i++) {
            Zone zone = zones.get(i);
            System.out.println((i+1) + ". " + zone.getName() + " (" + zone.getZoneId() + ")");
        }

        System.out.print("Enter zone ID (or press Enter to keep current): ");
        String zoneId = scanner.nextLine().trim();

        if (zoneId.isEmpty()) {
            System.out.println("Zone not changed.");
            return;
        }

        Optional<Zone> zoneOpt = zoneService.getZoneById(zoneId);
        if (zoneOpt.isPresent()) {
            Zone newZone = zoneOpt.get();
            site.setZone(newZone);
            siteService.saveSite(site);
            System.out.println("Zone updated successfully to: " + newZone.getName() + " (" + newZone.getZoneId() + ")");
        } else {
            System.out.println("Zone not found. No changes made.");
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