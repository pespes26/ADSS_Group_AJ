package com.superli.deliveries.application.controllers;

import com.superli.deliveries.Mappers.SiteMapper;
import com.superli.deliveries.domain.core.Zone;
import com.superli.deliveries.application.services.SiteService;
import com.superli.deliveries.application.services.ZoneService;
import com.superli.deliveries.dto.SiteDTO;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

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
        List<SiteDTO> sites = siteService.getAllSites().stream()
                .map(SiteMapper::toDTO)
                .collect(Collectors.toList());
                
        if (sites.isEmpty()) {
            System.out.println("No sites found.");
            return;
        }

        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║              ALL SITES             ║");
        System.out.println("╚════════════════════════════════════╝");

        for (int i = 0; i < sites.size(); i++) {
            SiteDTO site = sites.get(i);
            System.out.println("\n[" + (i+1) + "] SITE: " + site.getSiteId());
            System.out.println("    Address: " + site.getAddress());
            if (site.getPhoneNumber() != null && !site.getPhoneNumber().isEmpty()) {
                System.out.println("    Phone: " + site.getPhoneNumber());
            }
            if (site.getContactPersonName() != null && !site.getContactPersonName().isEmpty()) {
                System.out.println("    Contact: " + site.getContactPersonName());
            }
            System.out.println("    Zone ID: " + site.getZoneId());
            System.out.println("    " + "-".repeat(40));
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
            SiteDTO siteDTO = new SiteDTO(siteId, address, phone, contact, zoneId);
            siteService.saveSite(SiteMapper.fromDTO(siteDTO, zoneOpt.get()));
            System.out.println("Site added successfully.");
        } else {
            System.out.println("Zone not found. Cannot add site.");
        }
    }

    private void editSite() {
        showAllSites();
        System.out.print("\nEnter the ID of the site you want to edit: ");
        String siteId = scanner.nextLine().trim();

        Optional<SiteDTO> siteOpt = siteService.getSiteById(siteId)
                .map(SiteMapper::toDTO);
                
        if (siteOpt.isEmpty()) {
            System.out.println("Site not found. Please try again.");
            return;
        }

        SiteDTO siteDTO = siteOpt.get();
        System.out.println("\nCurrent Site Information:");
        System.out.println("ID: " + siteDTO.getSiteId());
        System.out.println("Address: " + siteDTO.getAddress());
        System.out.println("Phone Number: " + (siteDTO.getPhoneNumber() != null ? siteDTO.getPhoneNumber() : "N/A"));
        System.out.println("Contact Person: " + (siteDTO.getContactPersonName() != null ? siteDTO.getContactPersonName() : "N/A"));
        System.out.println("Zone ID: " + siteDTO.getZoneId());

        System.out.println("\nWhat would you like to edit?");
        System.out.println("1. Address");
        System.out.println("2. Phone Number");
        System.out.println("3. Contact Person");
        System.out.println("4. Zone");
        System.out.println("0. Cancel");
        System.out.print("Your choice: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1" -> editSiteAddress(siteDTO);
            case "2" -> editSitePhoneNumber(siteDTO);
            case "3" -> editSiteContactPerson(siteDTO);
            case "4" -> editSiteZone(siteDTO);
            case "0" -> System.out.println("Edit cancelled.");
            default -> System.out.println("Invalid choice. Edit cancelled.");
        }
    }

    private void editSiteAddress(SiteDTO siteDTO) {
        System.out.println("\nCurrent address: " + siteDTO.getAddress());
        System.out.print("Enter new address (or press Enter to keep current): ");
        String newAddress = scanner.nextLine().trim();

        if (newAddress.isEmpty()) {
            System.out.println("Address not changed.");
            return;
        }

        siteDTO.setAddress(newAddress);
        Optional<Zone> zoneOpt = zoneService.getZoneById(siteDTO.getZoneId());
        if (zoneOpt.isPresent()) {
            siteService.saveSite(SiteMapper.fromDTO(siteDTO, zoneOpt.get()));
            System.out.println("Address updated successfully to: " + newAddress);
        } else {
            System.out.println("Error: Zone not found. Update cancelled.");
        }
    }

    private void editSitePhoneNumber(SiteDTO siteDTO) {
        System.out.println("\nCurrent phone number: " + (siteDTO.getPhoneNumber() != null ? siteDTO.getPhoneNumber() : "N/A"));
        System.out.print("Enter new phone number (or press Enter to keep current, 'clear' to remove): ");
        String newPhone = scanner.nextLine().trim();

        if (newPhone.isEmpty()) {
            System.out.println("Phone number not changed.");
            return;
        }

        if (newPhone.equalsIgnoreCase("clear")) {
            siteDTO.setPhoneNumber(null);
        } else {
            siteDTO.setPhoneNumber(newPhone);
        }

        Optional<Zone> zoneOpt = zoneService.getZoneById(siteDTO.getZoneId());
        if (zoneOpt.isPresent()) {
            siteService.saveSite(SiteMapper.fromDTO(siteDTO, zoneOpt.get()));
            System.out.println("Phone number updated successfully.");
        } else {
            System.out.println("Error: Zone not found. Update cancelled.");
        }
    }

    private void editSiteContactPerson(SiteDTO siteDTO) {
        System.out.println("\nCurrent contact person: " + (siteDTO.getContactPersonName() != null ? siteDTO.getContactPersonName() : "N/A"));
        System.out.print("Enter new contact person (or press Enter to keep current, 'clear' to remove): ");
        String newContact = scanner.nextLine().trim();

        if (newContact.isEmpty()) {
            System.out.println("Contact person not changed.");
            return;
        }

        if (newContact.equalsIgnoreCase("clear")) {
            siteDTO.setContactPersonName(null);
        } else {
            siteDTO.setContactPersonName(newContact);
        }

        Optional<Zone> zoneOpt = zoneService.getZoneById(siteDTO.getZoneId());
        if (zoneOpt.isPresent()) {
            siteService.saveSite(SiteMapper.fromDTO(siteDTO, zoneOpt.get()));
            System.out.println("Contact person updated successfully.");
        } else {
            System.out.println("Error: Zone not found. Update cancelled.");
        }
    }

    private void editSiteZone(SiteDTO siteDTO) {
        System.out.println("\nCurrent zone ID: " + siteDTO.getZoneId());
        List<Zone> zones = zoneService.getAllZones();
        
        if (zones.isEmpty()) {
            System.out.println("No zones available. Cannot update zone.");
            return;
        }

        System.out.println("\nAvailable zones:");
        for (int i = 0; i < zones.size(); i++) {
            Zone zone = zones.get(i);
            System.out.println((i+1) + ". " + zone.getName() + " (" + zone.getZoneId() + ")");
        }

        System.out.print("Enter new Zone ID (or press Enter to keep current): ");
        String newZoneId = scanner.nextLine().trim();

        if (newZoneId.isEmpty()) {
            System.out.println("Zone not changed.");
            return;
        }

        Optional<Zone> zoneOpt = zoneService.getZoneById(newZoneId);
        if (zoneOpt.isPresent()) {
            siteDTO.setZoneId(newZoneId);
            siteService.saveSite(SiteMapper.fromDTO(siteDTO, zoneOpt.get()));
            System.out.println("Zone updated successfully.");
        } else {
            System.out.println("Zone not found. Update cancelled.");
        }
    }

    private void deleteSite() {
        showAllSites();
        System.out.print("\nEnter the ID of the site you want to delete: ");
        String siteId = scanner.nextLine().trim();

        Optional<SiteDTO> siteOpt = siteService.getSiteById(siteId)
                .map(SiteMapper::toDTO);

        if (siteOpt.isEmpty()) {
            System.out.println("Site not found. Please try again.");
            return;
        }

        System.out.print("Are you sure you want to delete this site? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (confirmation.equals("yes")) {
            siteService.deleteSite(siteId);
            System.out.println("Site deleted successfully.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private void assignZoneToSite() {
        showAllSites();
        System.out.print("\nEnter the ID of the site you want to assign a zone to: ");
        String siteId = scanner.nextLine().trim();

        Optional<SiteDTO> siteOpt = siteService.getSiteById(siteId)
                .map(SiteMapper::toDTO);

        if (siteOpt.isEmpty()) {
            System.out.println("Site not found. Please try again.");
            return;
        }

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
            SiteDTO siteDTO = siteOpt.get();
            siteDTO.setZoneId(zoneId);
            siteService.saveSite(SiteMapper.fromDTO(siteDTO, zoneOpt.get()));
            System.out.println("Zone assigned successfully.");
        } else {
            System.out.println("Zone not found. Assignment cancelled.");
        }
    }
}