package application.controllers;

import application.services.*;


import domain.core.*;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ZoneController {

    private final Scanner scanner;
    private final ZoneService zoneService;
    private final SiteService siteService;

    public ZoneController(ZoneService zoneService, SiteService siteService, Scanner scanner) {
        this.zoneService = zoneService;
        this.siteService = siteService;
        this.scanner = scanner;
    }

    public void runMenu() {
        boolean back = false;

        while (!back) {
            System.out.println("\n=== Zone Management ===");
            System.out.println("1. Add Zone");
            System.out.println("2. View All Zones");
            System.out.println("3. Edit Zone");
            System.out.println("4. Show Sites in Zone");
            System.out.println("5. Delete Zone");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose an option: ");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> addZone();
                case "2" -> viewAllZones();
                case "3" -> editZone();
                case "4" -> showSitesInZone();
                case "5" -> deleteZone();
                case "0" -> back = true;
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void addZone() {
        System.out.print("Enter Zone ID: ");
        String id = scanner.nextLine().trim();

        System.out.print("Enter Zone Name: ");
        String name = scanner.nextLine().trim();

        // Automatic name formatting: combine name and ID
        String formattedName = formatZoneName(name, id);

        try {
            Zone zone = new Zone(id, formattedName);
            zoneService.saveZone(zone);
            System.out.println("Zone added successfully with formatted name: " + formattedName);
        } catch (IllegalArgumentException e) {
            System.out.println("Error creating zone: " + e.getMessage());
        }
    }

    private void viewAllZones() {
        List<Zone> zones = zoneService.getAllZones();
        if (zones.isEmpty()) {
            System.out.println("No zones found.");
        } else {
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║              ALL ZONES             ║");
            System.out.println("╚════════════════════════════════════╝");

            for (int i = 0; i < zones.size(); i++) {
                Zone zone = zones.get(i);
                System.out.println("\n[" + (i+1) + "] ZONE: " + zone.getName() + " (ID: " + zone.getZoneId() + ")");
                System.out.println("    " + "-".repeat(40));
            }
        }
    }

    private void editZone() {
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║           EDIT ZONE INFO           ║");
        System.out.println("╚════════════════════════════════════╝");

        // First, show all zones
        viewAllZones();

        if (zoneService.getAllZones().isEmpty()) {
            return; // No zones to edit
        }

        System.out.print("\nEnter Zone ID to edit: ");
        String zoneId = scanner.nextLine().trim();

        Optional<Zone> zoneOpt = zoneService.getZoneById(zoneId);
        if (zoneOpt.isEmpty()) {
            System.out.println("Zone not found.");
            return;
        }

        Zone zone = zoneOpt.get();

        // Display current info
        System.out.println("\nCurrent Zone Information:");
        System.out.println("ID: " + zone.getZoneId());
        System.out.println("Name: " + zone.getName());

        // Get just the name part without the ID (if possible)
        String currentBaseName = getBaseZoneName(zone.getName(), zone.getZoneId());

        System.out.print("\nEnter new Zone Name (or press Enter to keep current): ");
        String newName = scanner.nextLine().trim();

        if (newName.isEmpty()) {
            System.out.println("Name not changed.");
            return;
        }

        // Format the new name
        String formattedName = formatZoneName(newName, zoneId);

        try {
            // Since Zone has final fields, we need to create a new one
            Zone updatedZone = new Zone(zoneId, formattedName);
            zoneService.saveZone(updatedZone);
            System.out.println("Zone updated successfully with formatted name: " + formattedName);
        } catch (IllegalArgumentException e) {
            System.out.println("Error updating zone: " + e.getMessage());
        }
    }

    private void showSitesInZone() {
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║           SITES IN ZONE            ║");
        System.out.println("╚════════════════════════════════════╝");

        // First, show all zones
        viewAllZones();

        if (zoneService.getAllZones().isEmpty()) {
            return; // No zones to show sites for
        }

        System.out.print("\nEnter Zone ID to view its sites: ");
        String zoneId = scanner.nextLine().trim();

        Optional<Zone> zoneOpt = zoneService.getZoneById(zoneId);
        if (zoneOpt.isEmpty()) {
            System.out.println("Zone not found.");
            return;
        }

        Zone zone = zoneOpt.get();

        // Find all sites in this zone
        List<Site> sitesInZone = siteService.getAllSites().stream()
                .filter(site -> site.getZone().getZoneId().equals(zoneId))
                .collect(Collectors.toList());

        if (sitesInZone.isEmpty()) {
            System.out.println("\nNo sites found in zone: " + zone.getName() + " (" + zone.getZoneId() + ")");
        } else {
            System.out.println("\nSites in zone: " + zone.getName() + " (" + zone.getZoneId() + ")");
            System.out.println("-".repeat(50));

            for (int i = 0; i < sitesInZone.size(); i++) {
                Site site = sitesInZone.get(i);
                System.out.println("\n[" + (i+1) + "] SITE: " + site.getSiteId());
                System.out.println("    Address: " + site.getAddress());
                if (site.getPhoneNumber() != null && !site.getPhoneNumber().isEmpty()) {
                    System.out.println("    Phone: " + site.getPhoneNumber());
                }
                if (site.getContactPersonName() != null && !site.getContactPersonName().isEmpty()) {
                    System.out.println("    Contact: " + site.getContactPersonName());
                }
                System.out.println("    " + "-".repeat(40));
            }
        }
    }

    private void deleteZone() {
        System.out.print("Enter Zone ID to delete: ");
        String id = scanner.nextLine().trim();

        // Check if any sites are using this zone
        List<Site> sitesInZone = siteService.getAllSites().stream()
                .filter(site -> site.getZone().getZoneId().equals(id))
                .collect(Collectors.toList());

        if (!sitesInZone.isEmpty()) {
            System.out.println("Cannot delete zone because it is used by " + sitesInZone.size() + " sites.");
            System.out.println("You must reassign these sites to another zone first.");
            return;
        }

        boolean removed = zoneService.deleteZone(id);
        if (removed) {
            System.out.println("Zone deleted successfully.");
        } else {
            System.out.println("Zone not found.");
        }
    }

    /**
     * Formats a zone name by combining the base name with the ID.
     *
     * @param baseName The base name of the zone
     * @param id The zone ID
     * @return The formatted name
     */
    private String formatZoneName(String baseName, String id) {
        // Remove any spaces, special characters, etc.
        String cleanName = baseName.replaceAll("[^a-zA-Z0-9]", "");
        return cleanName + id;
    }

    /**
     * Attempts to extract the base name from a formatted zone name.
     *
     * @param formattedName The formatted zone name
     * @param id The zone ID
     * @return The base name, or the formatted name if extraction fails
     */
    private String getBaseZoneName(String formattedName, String id) {
        if (formattedName.endsWith(id)) {
            return formattedName.substring(0, formattedName.length() - id.length());
        }
        return formattedName;
    }
}