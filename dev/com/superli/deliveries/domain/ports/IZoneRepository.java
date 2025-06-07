package com.superli.deliveries.domain.ports;

import com.superli.deliveries.domain.core.Zone;
import java.util.Collection;
import java.util.Optional;

/**
 * Interface defining the contract for accessing Zone data.
 */
public interface IZoneRepository {

    /**
     * Saves (adds or updates) a zone. Identified by zone ID.
     *
     * @param zone The Zone object to save. Cannot be null.
     */
    void save(Zone zone);

    /**
     * Finds a zone by its unique ID.
     *
     * @param zoneId The ID of the zone to find.
     * @return An Optional containing the Zone if found, or an empty Optional otherwise.
     */
    Optional<Zone> findById(String zoneId);

    /**
     * Finds all zones currently stored.
     *
     * @return A Collection of all Zone objects. May be empty but not null.
     */
    Collection<Zone> findAll();

    /**
     * Deletes a zone by its unique ID.
     *
     * @param zoneId The ID of the zone to delete.
     * @return An Optional containing the removed zone if it was found and deleted,
     * or an empty Optional otherwise.
     */
    Optional<Zone> deleteById(String zoneId);

    /**
     * Clears all zone data. Mainly for testing purposes.
     */
    void clearAll();
}
