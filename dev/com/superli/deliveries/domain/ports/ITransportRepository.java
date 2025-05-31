package com.superli.deliveries.domain.ports;

import com.superli.deliveries.domain.Transport;

import java.util.Collection;
import java.util.Optional;

/**
 * Interface defining the contract for accessing and managing Transport entities in memory.
 */
public interface ITransportRepository {

    /**
     * Saves (adds or updates) a Transport.
     * @param transport The Transport object to save. Cannot be null.
     */
    void save(Transport transport);

    /**
     * Finds a Transport by its unique ID.
     * @param transportId The ID of the transport to find.
     * @return An Optional containing the Transport if found, or empty otherwise.
     */
    Optional<Transport> findById(int transportId);

    /**
     * Finds all Transport records currently stored.
     * @return A Collection of Transport objects.
     */
    Collection<Transport> findAll();

    /**
     * Deletes a Transport by its unique ID.
     * @param transportId The ID of the transport to delete.
     * @return An Optional containing the removed Transport if found and deleted.
     */
    Optional<Transport> deleteById(int transportId);

    /**
     * Clears all transport data.
     */
    void clearAll();
}
