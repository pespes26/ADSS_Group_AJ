package com.superli.deliveries.service;

import com.superli.deliveries.domain.Zone;
import com.superli.deliveries.domain.ports.IZoneRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service layer responsible for business logic related to zones.
 */
public class ZoneService {

    private final IZoneRepository zoneRepository;

    public ZoneService(IZoneRepository zoneRepository) {
        this.zoneRepository = zoneRepository;
    }

    /**
     * Retrieves all zones in the system.
     * @return List of Zone objects.
     */
    public List<Zone> getAllZones() {
        return new ArrayList<>(zoneRepository.findAll());
    }

    /**
     * Finds a zone by its ID.
     * @param zoneId The ID of the zone.
     * @return Optional containing the Zone, if found.
     */
    public Optional<Zone> getZoneById(String zoneId) {
        return zoneRepository.findById(zoneId);
    }

    /**
     * Saves or updates a zone.
     * @param zone The Zone to be saved.
     */
    public void saveZone(Zone zone) {
        zoneRepository.save(zone);
    }

    /**
     * Deletes a zone by its ID.
     * @param zoneId The ID of the zone to delete.
     * @return true if zone was deleted, false otherwise.
     */
    public boolean deleteZone(String zoneId) {
        return zoneRepository.deleteById(zoneId).isPresent();
    }

    /**
     * Clears all zones (useful for testing or reset).
     */
    public void clearAllZones() {
        zoneRepository.clearAll();
    }
}
