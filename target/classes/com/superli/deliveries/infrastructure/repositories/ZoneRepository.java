package com.superli.deliveries.infrastructure.repositories;

import com.superli.deliveries.domain.Zone;
import com.superli.deliveries.domain.ports.IZoneRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.HashMap;

/**
 * Manages the in-memory storage for Zone objects using a HashMap.
 * Acts as an in-memory repository for shipment zones.
 * Implements IZoneRepository.
 */
public class ZoneRepository implements IZoneRepository {

    private final Map<String, Zone> zoneMap; // Key: zoneId

    public ZoneRepository() {
        this.zoneMap = new HashMap<>();
    }

    @Override
    public void save(Zone zone) {
        Objects.requireNonNull(zone, "Zone cannot be null");
        Objects.requireNonNull(zone.getZoneId(), "Zone ID cannot be null");
        zoneMap.put(zone.getZoneId(), zone);
        System.out.println("Zone saved/updated: " + zone.getZoneId());
    }

    @Override
    public Optional<Zone> findById(String zoneId) {
        return Optional.ofNullable(zoneMap.get(zoneId));
    }

    @Override
    public Collection<Zone> findAll() {
        return Collections.unmodifiableCollection(zoneMap.values());
    }

    @Override
    public Optional<Zone> deleteById(String zoneId) {
        if (zoneId != null) {
            Zone removed = zoneMap.remove(zoneId);
            if (removed != null) {
                System.out.println("Zone removed: " + zoneId);
            }
            return Optional.ofNullable(removed);
        }
        return Optional.empty();
    }

    @Override
    public void clearAll() {
        zoneMap.clear();
        System.out.println("ZoneRepository cleared.");
    }
}
