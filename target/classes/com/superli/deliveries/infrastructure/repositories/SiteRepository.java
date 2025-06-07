package com.superli.deliveries.infrastructure.repositories;

// --- ייבואים ---
import com.superli.deliveries.domain.Site;
// ודא שה-import הזה מצביע על המיקום הנכון של הממשק אצלך
import com.superli.deliveries.domain.ports.ISiteRepository; // או com.superli.deliveries.domain.*
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.HashMap; // <--- שימוש ב-HashMap

/**
 * Manages the in-memory storage for Site objects using a HashMap.
 * Acts as an in-memory repository for sites.
 * This class implements the ISiteRepository interface.
 */
//             vvvvvvvvvvvvvvvvvvvvvvvvvvvvv הוספת implements
public class SiteRepository implements ISiteRepository {

    private final Map<String, Site> siteMap; // Key: siteId

    /**
     * Constructs a new SiteRepository.
     */
    public SiteRepository() {
        this.siteMap = new HashMap<>(); // <--- שימוש ב-HashMap
    }

    /**
     * {@inheritDoc}
     */
    @Override // <-- הוספת Override
    public void save(Site site) { // <-- שינוי שם מ-addSite
        Objects.requireNonNull(site, "Site cannot be null");
        Objects.requireNonNull(site.getSiteId(), "Site ID cannot be null");
        siteMap.put(site.getSiteId(), site);
        System.out.println("Site saved/updated: " + site.getSiteId());
    }

    /**
     * {@inheritDoc}
     */
    @Override // <-- הוספת Override
    public Optional<Site> findById(String siteId) { // <-- שינוי שם מ-getSiteById
        return Optional.ofNullable(siteMap.get(siteId));
    }

    /**
     * {@inheritDoc}
     */
    @Override // <-- הוספת Override
    public Collection<Site> findAll() { // <-- שינוי שם מ-getAllSites
        return Collections.unmodifiableCollection(siteMap.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override // <-- הוספת Override
    public Optional<Site> deleteById(String siteId) { // <-- שינוי שם מ-deleteSite
        if (siteId != null) {
            Site removed = siteMap.remove(siteId);
            if(removed != null) {
                System.out.println("Site removed: " + siteId);
            }
            return Optional.ofNullable(removed);
        }
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override // <-- הוספת Override
    public void clearAll() {
        siteMap.clear();
        System.out.println("SiteRepository cleared.");
    }
}