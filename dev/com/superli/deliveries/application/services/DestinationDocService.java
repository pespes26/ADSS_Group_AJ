package com.superli.deliveries.application.services;

import com.superli.deliveries.domain.core.DestinationDoc;
import com.superli.deliveries.domain.core.DeliveredItem;
import com.superli.deliveries.domain.ports.IDestinationDocRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service layer responsible for managing destination documents and their items.
 */
public class DestinationDocService {

    private final IDestinationDocRepository destinationDocRepository;

    public DestinationDocService(IDestinationDocRepository destinationDocRepository) {
        this.destinationDocRepository = destinationDocRepository;
    }

    public List<DestinationDoc> getAllDestinationDocs() {
        return new ArrayList<>(destinationDocRepository.findAll());
    }

    public Optional<DestinationDoc> getById(int destinationDocId) {
        return destinationDocRepository.findById(destinationDocId);
    }

    public void saveDestinationDoc(DestinationDoc destinationDoc) {
        destinationDocRepository.save(destinationDoc);
    }

    public boolean deleteDestinationDoc(int destinationDocId) {
        return destinationDocRepository.deleteById(destinationDocId).isPresent();
    }

    /**
     * Adds an item to a specific destination doc (if found).
     * @param docId The ID of the doc to modify.
     * @param item The DeliveredItem to add.
     * @return true if added successfully, false otherwise.
     */
    public boolean addDeliveredItemToDoc(int docId, DeliveredItem item) {
        Optional<DestinationDoc> docOpt = destinationDocRepository.findById(docId);
        if (docOpt.isPresent()) {
            docOpt.get().addDeliveredItem(item);
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
    public boolean removeDeliveredItemFromDoc(int docId, DeliveredItem item) {
        Optional<DestinationDoc> docOpt = destinationDocRepository.findById(docId);
        if (docOpt.isPresent()) {
            return docOpt.get().removeDeliveredItem(item);
        }
        return false;
    }

    /**
     * Updates the status of a destination doc.
     * @param docId The document ID.
     * @param status New status to set.
     * @return true if updated, false otherwise.
     */
    public boolean updateStatus(int docId, String status) {
        Optional<DestinationDoc> docOpt = destinationDocRepository.findById(docId);
        if (docOpt.isPresent()) {
            docOpt.get().setStatus(status);
            return true;
        }
        return false;
    }
}
