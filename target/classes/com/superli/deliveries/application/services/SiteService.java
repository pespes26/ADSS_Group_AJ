package com.superli.deliveries.application.services;

import com.superli.deliveries.dataaccess.dao.del.SiteDAO;
import com.superli.deliveries.domain.core.Site;
import com.superli.deliveries.domain.core.Zone;
import com.superli.deliveries.Mappers.SiteMapper;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SiteService {
    private final SiteDAO siteDAO;
    private final ZoneService zoneService;

    public SiteService(SiteDAO siteDAO, ZoneService zoneService) {
        this.siteDAO = siteDAO;
        this.zoneService = zoneService;
    }

    public List<Site> getAllSites() {
        try {
            return siteDAO.findAll().stream()
                    .map(dto -> {
                        Optional<Zone> zone = zoneService.getZoneById(dto.getZoneId());
                        return zone.map(z -> SiteMapper.fromDTO(dto, z))
                                .orElseThrow(() -> new RuntimeException("Zone not found for site: " + dto.getSiteId()));
                    })
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all sites", e);
        }
    }

    public Optional<Site> getSiteById(String id) {
        try {
            return siteDAO.findById(id)
                    .map(dto -> {
                        Optional<Zone> zone = zoneService.getZoneById(dto.getZoneId());
                        return zone.map(z -> SiteMapper.fromDTO(dto, z))
                                .orElseThrow(() -> new RuntimeException("Zone not found for site: " + dto.getSiteId()));
                    });
        } catch (SQLException e) {
            throw new RuntimeException("Error getting site by id: " + id, e);
        }
    }

    public void saveSite(Site site) {
        try {
            siteDAO.save(SiteMapper.toDTO(site));
        } catch (SQLException e) {
            throw new RuntimeException("Error saving site", e);
        }
    }

    public void deleteSite(String id) {
        try {
            siteDAO.deleteById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting site: " + id, e);
        }
    }

    public List<Site> getSitesByZone(String zoneId) {
        try {
            Optional<Zone> zone = zoneService.getZoneById(zoneId);
            if (zone.isEmpty()) {
                throw new RuntimeException("Zone not found: " + zoneId);
            }
            return siteDAO.findByZoneId(zoneId).stream()
                    .map(dto -> SiteMapper.fromDTO(dto, zone.get()))
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Error getting sites by zone: " + zoneId, e);
        }
    }

    public List<Site> getActiveSites() {
        try {
            return siteDAO.findActiveSites().stream()
                    .map(dto -> {
                        Optional<Zone> zone = zoneService.getZoneById(dto.getZoneId());
                        return zone.map(z -> SiteMapper.fromDTO(dto, z))
                                .orElseThrow(() -> new RuntimeException("Zone not found for site: " + dto.getSiteId()));
                    })
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Error getting active sites", e);
        }
    }

    public Optional<Site> getSiteByAddress(String address) {
        try {
            return siteDAO.findByAddress(address)
                    .map(dto -> {
                        Optional<Zone> zone = zoneService.getZoneById(dto.getZoneId());
                        return zone.map(z -> SiteMapper.fromDTO(dto, z))
                                .orElseThrow(() -> new RuntimeException("Zone not found for site: " + dto.getSiteId()));
                    });
        } catch (SQLException e) {
            throw new RuntimeException("Error getting site by address: " + address, e);
        }
    }
}


///**
// * Service layer responsible for business logic related to sites and zones.
// */
//public class SiteService {
//
//    private final ISiteRepository siteRepository;
//
//    public SiteService(ISiteRepository siteRepository) {
//        this.siteRepository = siteRepository;
//    }
//
//    /**
//     * Returns all sites in the system.
//     * @return List of all sites.
//     */
//    public List<Site> getAllSites() {
//        return new ArrayList<>(siteRepository.findAll());
//    }
//
//    /**
//     * Finds a site by its ID.
//     * @param siteId The ID of the site.
//     * @return Optional containing the site, if found.
//     */
//    public Optional<Site> getSiteById(String siteId) {
//        return siteRepository.findById(siteId);
//    }
//
//    /**
//     * Saves or updates a site in the repository.
//     * @param site The site to save.
//     */
//    public void saveSite(Site site) {
//        siteRepository.save(site);
//    }
//
//    /**
//     * Removes a site from the system.
//     * @param siteId The ID of the site to remove.
//     */
//    public void deleteSite(String siteId) {
//        siteRepository.deleteById(siteId);
//    }
//
//    /**
//     * Updates the zone of a specific site.
//     * @param siteId Site ID to update
//     * @param newZone New zone to assign
//     * @return true if site was found and updated, false otherwise
//     */
//    public boolean updateZone(String siteId, Zone newZone) {
//        Optional<Site> siteOpt = siteRepository.findById(siteId);
//        if (siteOpt.isPresent()) {
//            Site site = siteOpt.get();
//            site.setZone(newZone);
//            siteRepository.save(site);
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * Updates the address of a specific site.
//     * @param siteId Site ID to update
//     * @param newAddress New address to set
//     * @return true if site was found and updated, false otherwise
//     */
//    public boolean updateAddress(String siteId, String newAddress) {
//        if (newAddress == null || newAddress.trim().isEmpty()) {
//            return false; // Invalid address
//        }
//
//        Optional<Site> siteOpt = siteRepository.findById(siteId);
//        if (siteOpt.isPresent()) {
//            Site site = siteOpt.get();
//            site.setAddress(newAddress);
//            siteRepository.save(site);
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * Updates the phone number of a specific site.
//     * @param siteId Site ID to update
//     * @param newPhoneNumber New phone number to set (can be null)
//     * @return true if site was found and updated, false otherwise
//     */
//    public boolean updatePhoneNumber(String siteId, String newPhoneNumber) {
//        Optional<Site> siteOpt = siteRepository.findById(siteId);
//        if (siteOpt.isPresent()) {
//            Site site = siteOpt.get();
//            site.setPhoneNumber(newPhoneNumber);
//            siteRepository.save(site);
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * Updates the contact person of a specific site.
//     * @param siteId Site ID to update
//     * @param newContactPerson New contact person name to set (can be null)
//     * @return true if site was found and updated, false otherwise
//     */
//    public boolean updateContactPerson(String siteId, String newContactPerson) {
//        Optional<Site> siteOpt = siteRepository.findById(siteId);
//        if (siteOpt.isPresent()) {
//            Site site = siteOpt.get();
//            site.setContactPersonName(newContactPerson);
//            siteRepository.save(site);
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * Gets all sites in a specific zone.
//     * @param zoneId The zone ID to filter by
//     * @return List of sites in the specified zone
//     */
//    public List<Site> getSitesByZone(String zoneId) {
//        return siteRepository.findAll().stream()
//                .filter(site -> site.getZone().getZoneId().equals(zoneId))
//                .collect(Collectors.toList());
//    }
//}