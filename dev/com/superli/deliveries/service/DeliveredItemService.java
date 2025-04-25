package com.superli.deliveries.services;

import com.superli.deliveries.domain.DeliveredItem;
import com.superli.deliveries.domain.DestinationDoc;
import com.superli.deliveries.domain.ports.IDestinationDocRepository;

import java.util.List;
import java.util.Optional;

/**
 * Service responsible for managing delivered items within destination documents.
 * Acts as a helper around the DestinationDoc repository.
 */
public class DeliveredItemService {

    private final IDestinationDocRepository destinationDocRepository;

    public DeliveredItemService(IDestinationDocRepository destinationDocRepository) {
        this.destinationDocRepository = destinationDocRepository;
    }

    /**
     * Adds a new delivered item to an existing destination doc.
     * @param docId DestinationDoc ID.
     * @param item The delivered item to add.
     * @return true if added successfully, false otherwise.
     */
    public boolean addDeliveredItem(int docId, DeliveredItem item) {
        Optional<DestinationDoc> docOpt = destinationDocRepository.findById(docId);
        if (docOpt.isPresent()) {
            try {
                docOpt.get().addDeliveredItem(item);
                return true;
            } catch (IllegalArgumentException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
        return false;
    }

    /**
     * Removes a delivered item from a destination doc.
     * @param docId DestinationDoc ID.
     * @param item The delivered item to remove.
     * @return true if removed, false otherwise.
     */
    public boolean removeDeliveredItem(int docId, DeliveredItem item) {
        Optional<DestinationDoc> docOpt = destinationDocRepository.findById(docId);
        return docOpt.map(doc -> doc.removeDeliveredItem(item)).orElse(false);
    }

    /**
     * Retrieves all delivered items for a specific destination doc.
     * @param docId The ID of the destination document.
     * @return A list of delivered items, or an empty list if not found.
     */
    public List<DeliveredItem> getDeliveredItemsForDoc(int docId) {
        return destinationDocRepository.findById(docId)
                .map(DestinationDoc::getDeliveryItems)
                .orElse(List.of());
    }

    /**
     * Updates the quantity of a delivered item for a specific product.
     * @param docId DestinationDoc ID.
     * @param productId Product ID of the delivered item to update.
     * @param newQuantity The new quantity to set.
     * @return true if the item was found and updated, false otherwise.
     */
    public boolean updateQuantity(int docId, String productId, int newQuantity) {
        Optional<DestinationDoc> docOpt = destinationDocRepository.findById(docId);
        if (docOpt.isPresent()) {
            for (DeliveredItem item : docOpt.get().getDeliveryItems()) {
                if (item.getProductId().equals(productId)) {
                    item.setQuantity(newQuantity);
                    return true;
                }
            }
        }
        return false;
    }
}
