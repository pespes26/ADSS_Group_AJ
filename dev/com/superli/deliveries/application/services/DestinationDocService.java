package com.superli.deliveries.application.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.superli.deliveries.Mappers.DestinationDocMapper;
import com.superli.deliveries.dataaccess.dao.del.DestinationDocDAO;
import com.superli.deliveries.domain.core.DeliveredItem;
import com.superli.deliveries.domain.core.DestinationDoc;
import com.superli.deliveries.domain.core.Site;

/**
 * Service layer responsible for managing destination documents and their items.
 */
public class DestinationDocService {

    private final DestinationDocDAO destinationDocDAO;
    private final SiteService siteService;

    public DestinationDocService(DestinationDocDAO destinationDocDAO, SiteService siteService) {
        this.destinationDocDAO = destinationDocDAO;
        this.siteService = siteService;
    }

    public List<DestinationDoc> getAllDocs() {
        return destinationDocDAO.findAll().stream()
                .map(dto -> {
                    Optional<Site> site = siteService.getSiteById(dto.getSiteId());
                    return site.map(s -> DestinationDocMapper.fromDTO(dto, s)).orElse(null);
                })
                .filter(doc -> doc != null)
                .collect(Collectors.toList());
    }

    public Optional<DestinationDoc> getDocById(String id) {
        return destinationDocDAO.findById(id)
                .flatMap(dto -> {
                    Optional<Site> site = siteService.getSiteById(dto.getSiteId());
                    return site.map(s -> DestinationDocMapper.fromDTO(dto, s));
                });
    }

    public void saveDoc(DestinationDoc doc) {
        destinationDocDAO.save(DestinationDocMapper.toDTO(doc));
    }

    public void deleteDoc(String id) {
        destinationDocDAO.deleteById(id);
    }

    public List<DestinationDoc> getDocsByTransport(String transportId) {
        return destinationDocDAO.findByTransportId(transportId).stream()
                .map(dto -> {
                    Optional<Site> site = siteService.getSiteById(dto.getSiteId());
                    return site.map(s -> DestinationDocMapper.fromDTO(dto, s)).orElse(null);
                })
                .filter(doc -> doc != null)
                .collect(Collectors.toList());
    }

    public List<DestinationDoc> getDocsByStatus(String status) {
        return destinationDocDAO.findByStatus(status).stream()
                .map(dto -> {
                    Optional<Site> site = siteService.getSiteById(dto.getSiteId());
                    return site.map(s -> DestinationDocMapper.fromDTO(dto, s)).orElse(null);
                })
                .filter(doc -> doc != null)
                .collect(Collectors.toList());
    }

    public List<DestinationDoc> getPendingDocs() {
        return destinationDocDAO.findPendingDocs().stream()
                .map(dto -> {
                    Optional<Site> site = siteService.getSiteById(dto.getSiteId());
                    return site.map(s -> DestinationDocMapper.fromDTO(dto, s)).orElse(null);
                })
                .filter(doc -> doc != null)
                .collect(Collectors.toList());
    }

    /**
     * Adds an item to a specific destination doc (if found).
     * @param docId The ID of the doc to modify.
     * @param item The DeliveredItem to add.
     * @return true if added successfully, false otherwise.
     */
    public boolean addDeliveredItemToDoc(String docId, DeliveredItem item) {
        Optional<DestinationDoc> docOpt = getDocById(docId);
        if (docOpt.isPresent()) {
            DestinationDoc doc = docOpt.get();
            doc.addDeliveredItem(item);
            saveDoc(doc);  // Save the changes to the database
            return true;
        }
        return false;
    }

    /**
     * Removes an item from a destination doc (if found).
     * @param docId The ID of the doc.
     * @param item The DeliveredItem to remove.
     * @return true if removed, false otherwise.
     */
    public boolean removeDeliveredItemFromDoc(String docId, DeliveredItem item) {
        Optional<DestinationDoc> docOpt = getDocById(docId);
        if (docOpt.isPresent()) {
            DestinationDoc doc = docOpt.get();
            boolean removed = doc.removeDeliveredItem(item);
            if (removed) {
                saveDoc(doc);  // Save the changes to the database
            }
            return removed;
        }
        return false;
    }

    /**
     * Updates the status of a destination doc.
     * @param docId The document ID.
     * @param status New status to set.
     * @return true if updated, false otherwise.
     */
    public boolean updateStatus(String docId, String status) {
        Optional<DestinationDoc> docOpt = getDocById(docId);
        if (docOpt.isPresent()) {
            DestinationDoc doc = docOpt.get();
            doc.setStatus(status);
            saveDoc(doc);  // Save the changes to the database
            return true;
        }
        return false;
    }
}
