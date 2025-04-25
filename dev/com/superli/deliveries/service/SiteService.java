package com.superli.deliveries.service;

import com.superli.deliveries.domain.Site;
import com.superli.deliveries.domain.Zone;
import com.superli.deliveries.domain.ports.ISiteRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer responsible for business logic related to sites and zones.
 */
public class SiteService {

    private final ISiteRepository siteRepository;

    public SiteService(ISiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

    public List<Site> getAllSites() {
        return new ArrayList<>(siteRepository.findAll());
    }

    public Optional<Site> getSiteById(String siteId) {
        return siteRepository.findById(siteId);
    }

    public void saveSite(Site site) {
        siteRepository.save(site);
    }

    public void deleteSite(String siteId) {
        siteRepository.deleteById(siteId);
    }

    public boolean updateZone(String siteId, Zone newZone) {
        Optional<Site> siteOpt = siteRepository.findById(siteId);
        if (siteOpt.isPresent()) {
            siteOpt.get().setZone(newZone);  // <<< השורה המתוקנת
            return true;
        }
        return false;
    }

    public List<Zone> getAllZones() {
        return siteRepository.findAll().stream()
                .map(Site::getZone) // <<< גם כאן
                .distinct()
                .collect(Collectors.toList());
    }

}
