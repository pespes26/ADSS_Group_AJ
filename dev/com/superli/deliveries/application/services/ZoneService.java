package com.superli.deliveries.application.services;

import com.superli.deliveries.domain.core.Zone;
import com.superli.deliveries.domain.ports.IZoneRepository;
import com.superli.deliveries.dto.ZoneDTO;
import com.superli.deliveries.Mappers.ZoneMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ZoneService {

    private final IZoneRepository zoneRepository;

    public ZoneService(IZoneRepository zoneRepository) {
        this.zoneRepository = zoneRepository;
    }

    public List<ZoneDTO> getAllZones() {
        return zoneRepository.findAll().stream()
                .map(ZoneMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<ZoneDTO> getZoneById(String zoneId) {
        return zoneRepository.findById(zoneId)
                .map(ZoneMapper::toDTO);
    }

    public void saveZone(ZoneDTO zoneDTO) {
        Zone zone = ZoneMapper.fromDTO(zoneDTO);
        zoneRepository.save(zone);
    }

    public boolean deleteZone(String zoneId) {
        Optional<Zone> zoneOpt = zoneRepository.findById(zoneId);
        if (zoneOpt.isPresent()) {
            zoneRepository.deleteById(zoneId);
            return true;
        }
        return false;
    }
}


///**
// * Service layer responsible for business logic related to zones.
// */
//public class ZoneService {
//
//    private final IZoneRepository zoneRepository;
//
//    public ZoneService(IZoneRepository zoneRepository) {
//        this.zoneRepository = zoneRepository;
//    }
//
//    /**
//     * Retrieves all zones in the system.
//     * @return List of Zone objects.
//     */
//    public List<Zone> getAllZones() {
//        return new ArrayList<>(zoneRepository.findAll());
//    }
//
//    /**
//     * Finds a zone by its ID.
//     * @param zoneId The ID of the zone.
//     * @return Optional containing the Zone, if found.
//     */
//    public Optional<Zone> getZoneById(String zoneId) {
//        return zoneRepository.findById(zoneId);
//    }
//
//    /**
//     * Saves or updates a zone.
//     * @param zone The Zone to be saved.
//     */
//    public void saveZone(Zone zone) {
//        zoneRepository.save(zone);
//    }
//
//    /**
//     * Deletes a zone by its ID.
//     * @param zoneId The ID of the zone to delete.
//     * @return true if zone was deleted, false otherwise.
//     */
//    public boolean deleteZone(String zoneId) {
//        return zoneRepository.deleteById(zoneId).isPresent();
//    }
//
//    /**
//     * Clears all zones (useful for testing or reset).
//     */
//    public void clearAllZones() {
//        zoneRepository.clearAll();
//    }
//}
