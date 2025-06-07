package com.superli.deliveries.domain.ports; // או .domain, בהתאם למיקום שבחרת

import com.superli.deliveries.domain.Site;
import java.util.Collection;
import java.util.Optional;

/**
 * Interface defining the contract for accessing Site data.
 */
public interface ISiteRepository {

    /**
     * Saves (adds or updates) a site. Identified by site ID.
     *
     * @param site The Site object to save. Cannot be null.
     */
    void save(Site site);

    /**
     * Finds a site by its unique ID.
     *
     * @param siteId The ID of the site to find.
     * @return An Optional containing the Site if found, or an empty Optional otherwise.
     */
    Optional<Site> findById(String siteId);

    /**
     * Finds all sites currently stored.
     *
     * @return A Collection of all Site objects. May be empty but not null.
     */
    Collection<Site> findAll();

    /**
     * Deletes a site by its unique ID.
     *
     * @param siteId The ID of the site to delete.
     * @return An Optional containing the removed site if it was found and deleted,
     * or an empty Optional otherwise.
     */
    Optional<Site> deleteById(String siteId);

    /**
     * Clears all site data. Mainly for testing purposes.
     */
    void clearAll();
}