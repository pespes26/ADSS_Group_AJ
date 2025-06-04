package com.superli.deliveries.application.services;

import com.superli.deliveries.domain.core.Site;
import com.superli.deliveries.domain.core.Zone;
import com.superli.deliveries.domain.ports.ISiteRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer responsible for business logic related to sites and zones.
 */
public class SiteService {

    private final ISiteRepository siteRepository;

    public SiteService(ISiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

    /**
     * Returns all sites in the system.
     * @return List of all sites.
     */
    public List<Site> getAllSites() {
        return new ArrayList<>(siteRepository.findAll());
    }

    /**
     * Finds a site by its ID.
     * @param siteId The ID of the site.
     * @return Optional containing the site, if found.
     */
    public Optional<Site> getSiteById(String siteId) {
        return siteRepository.findById(siteId);
    }

    /**
     * Saves or updates a site in the repository.
     * @param site The site to save.
     */
    public void saveSite(Site site) {
        siteRepository.save(site);
    }

    /**
     * Removes a site from the system.
     * @param siteId The ID of the site to remove.
     */
    public void deleteSite(String siteId) {
        siteRepository.deleteById(siteId);
    }

    /**
     * Updates the zone of a specific site.
     * @param siteId Site ID to update
     * @param newZone New zone to assign
     * @return true if site was found and updated, false otherwise
     */
    public boolean updateZone(String siteId, Zone newZone) {
        Optional<Site> siteOpt = siteRepository.findById(siteId);
        if (siteOpt.isPresent()) {
            Site site = siteOpt.get();
            site.setZone(newZone);
            siteRepository.save(site);
            return true;
        }
        return false;
    }

    /**
     * Updates the address of a specific site.
     * @param siteId Site ID to update
     * @param newAddress New address to set
     * @return true if site was found and updated, false otherwise
     */
    public boolean updateAddress(String siteId, String newAddress) {
        if (newAddress == null || newAddress.trim().isEmpty()) {
            return false; // Invalid address
        }

        Optional<Site> siteOpt = siteRepository.findById(siteId);
        if (siteOpt.isPresent()) {
            Site site = siteOpt.get();
            site.setAddress(newAddress);
            siteRepository.save(site);
            return true;
        }
        return false;
    }

    /**
     * Updates the phone number of a specific site.
     * @param siteId Site ID to update
     * @param newPhoneNumber New phone number to set (can be null)
     * @return true if site was found and updated, false otherwise
     */
    public boolean updatePhoneNumber(String siteId, String newPhoneNumber) {
        Optional<Site> siteOpt = siteRepository.findById(siteId);
        if (siteOpt.isPresent()) {
            Site site = siteOpt.get();
            site.setPhoneNumber(newPhoneNumber);
            siteRepository.save(site);
            return true;
        }
        return false;
    }

    /**
     * Updates the contact person of a specific site.
     * @param siteId Site ID to update
     * @param newContactPerson New contact person name to set (can be null)
     * @return true if site was found and updated, false otherwise
     */
    public boolean updateContactPerson(String siteId, String newContactPerson) {
        Optional<Site> siteOpt = siteRepository.findById(siteId);
        if (siteOpt.isPresent()) {
            Site site = siteOpt.get();
            site.setContactPersonName(newContactPerson);
            siteRepository.save(site);
            return true;
        }
        return false;
    }

    /**
     * Gets all sites in a specific zone.
     * @param zoneId The zone ID to filter by
     * @return List of sites in the specified zone
     */
    public List<Site> getSitesByZone(String zoneId) {
        return siteRepository.findAll().stream()
                .filter(site -> site.getZone().getZoneId().equals(zoneId))
                .collect(Collectors.toList());
    }
}