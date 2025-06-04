package com.superli.deliveries.domain.ports;

import com.superli.deliveries.domain.core.Truck;
import java.util.Collection;
import java.util.Optional;

/**
 * Interface defining the contract for accessing Truck data.
 */
public interface ITruckRepository {

    /**
     * Saves (adds or updates) a truck. Identified by license plate number.
     *
     * @param truck The Truck object to save. Cannot be null.
     */
    void save(Truck truck);

    /**
     * Finds a truck by its unique license plate number.
     *
     * @param licensePlateNumber The license plate number of the truck to find.
     * @return An Optional containing the Truck if found, or an empty Optional otherwise.
     */
    Optional<Truck> findByPlate(String licensePlateNumber);

    /**
     * Finds all trucks currently stored.
     *
     * @return A Collection of all Truck objects. May be empty but not null.
     */
    Collection<Truck> findAll();

    /**
     * Deletes a truck by its unique license plate number.
     *
     * @param licensePlateNumber The license plate number of the truck to delete.
     * @return An Optional containing the removed truck if it was found and deleted,
     * or an empty Optional otherwise.
     */
    Optional<Truck> deleteByPlate(String licensePlateNumber);

    /**
     * Clears all truck data. Mainly for testing purposes.
     */
    void clearAll();
}