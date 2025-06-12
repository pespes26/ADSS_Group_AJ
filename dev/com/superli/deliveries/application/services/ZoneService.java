package com.superli.deliveries.application.services;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.superli.deliveries.Mappers.ZoneMapper;
import com.superli.deliveries.dataaccess.dao.del.ZoneDAO;
import com.superli.deliveries.domain.core.Zone;

public class ZoneService {
    private final ZoneDAO zoneDAO;

    public ZoneService(ZoneDAO zoneDAO) {
        this.zoneDAO = zoneDAO;
    }

    public List<Zone> getAllZones() {
        try {
            return zoneDAO.findAll().stream()
                    .map(ZoneMapper::toDomain)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all zones", e);
        }
    }

    public Optional<Zone> getZoneById(String id) {
        try {
            return zoneDAO.findById(id)
                    .map(ZoneMapper::toDomain);
        } catch (SQLException e) {
            throw new RuntimeException("Error getting zone by id: " + id, e);
        }
    }

    public void saveZone(Zone zone) {
        try {
            zoneDAO.save(ZoneMapper.toDTO(zone));
        } catch (SQLException e) {
            throw new RuntimeException("Error saving zone", e);
        }
    }

    public boolean deleteZone(String id) {
        try {
            zoneDAO.deleteById(id);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public List<Zone> getActiveZones() {
        try {
            return zoneDAO.findActiveZones().stream()
                    .map(ZoneMapper::toDomain)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Error getting active zones", e);
        }
    }

    public Optional<Zone> getZoneByName(String name) {
        try {
            return zoneDAO.findByName(name)
                    .map(ZoneMapper::toDomain);
        } catch (SQLException e) {
            throw new RuntimeException("Error getting zone by name: " + name, e);
        }
    }

    public List<Zone> getZonesByCapacityRange(float minCapacity, float maxCapacity) {
        try {
            return zoneDAO.findByCapacityRange(minCapacity, maxCapacity).stream()
                    .map(ZoneMapper::toDomain)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Error getting zones by capacity range", e);
        }
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
