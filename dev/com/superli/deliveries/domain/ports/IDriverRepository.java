package com.superli.deliveries.domain.ports; // או .domain, בהתאם למיקום שבחרת

import com.superli.deliveries.domain.core.Driver;
import java.util.Collection;
import java.util.Optional;

/**
 * Interface defining the contract for accessing Driver data.
 */
public interface IDriverRepository {

    /**
     * Saves (adds or updates) a driver. Identified by driver ID.
     *
     * @param driver The Driver object to save. Cannot be null.
     */
    void save(Driver driver);

    /**
     * Finds a driver by their unique ID.
     *
     * @param driverId The ID of the driver to find.
     * @return An Optional containing the Driver if found, or an empty Optional otherwise.
     */
    Optional<Driver> findById(String driverId);

    /**
     * Finds all drivers currently stored.
     *
     * @return A Collection of all Driver objects. Maybe empty but not null.
     */
    Collection<Driver> findAll();

    /**
     * Deletes a driver by their unique ID.
     *
     * @param driverId The ID of the driver to delete.
     * @return An Optional containing the removed driver if it was found and deleted,
     * or an empty Optional otherwise.
     */
    Optional<Driver> deleteById(String driverId);

    /**
     * Clears all driver data. Mainly for testing purposes.
     */
    void clearAll();
}