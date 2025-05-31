package com.superli.deliveries.service;

import java.util.List;
import java.util.Optional;

import com.superli.deliveries.domain.DeliveredItem;
import com.superli.deliveries.domain.DestinationDoc;
import com.superli.deliveries.domain.Product;
import com.superli.deliveries.domain.Transport;
import com.superli.deliveries.domain.Truck;
import com.superli.deliveries.domain.ports.IDestinationDocRepository;

/**
 * Service responsible for managing delivered items within destination documents.
 * Also handles weight calculation and validation.
 */
public class DeliveredItemService {

    private final IDestinationDocRepository destinationDocRepository;
    private final TransportService transportService;
    private final ProductService productService; // Assuming this exists or will be created

    public DeliveredItemService(IDestinationDocRepository destinationDocRepository,
                                TransportService transportService,
                                ProductService productService) {
        this.destinationDocRepository = destinationDocRepository;
        this.transportService = transportService;
        this.productService = productService;
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
            DestinationDoc doc = docOpt.get();

            // Check if weight limit would be exceeded
            if (wouldExceedWeightLimit(doc.getTransportId(), item)) {
                System.out.println("⚠️ Warning: Adding this item would exceed the truck's weight limit.");
                return false;
            }

            try {
                doc.addDeliveredItem(item);
                destinationDocRepository.save(doc);

                // Update the transport's departure weight
                updateTransportWeight(doc.getTransportId());

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
        if (docOpt.isPresent()) {
            DestinationDoc doc = docOpt.get();
            boolean removed = doc.removeDeliveredItem(item);
            if (removed) {
                destinationDocRepository.save(doc);

                // Update the transport's departure weight
                updateTransportWeight(doc.getTransportId());
            }
            return removed;
        }
        return false;
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
            DestinationDoc doc = docOpt.get();

            // Find the item with this product ID
            DeliveredItem itemToUpdate = null;
            for (DeliveredItem item : doc.getDeliveryItems()) {
                if (item.getProductId().equals(productId)) {
                    itemToUpdate = item;
                    break;
                }
            }

            if (itemToUpdate != null) {
                // Check weight limit with the new quantity
                int oldQuantity = itemToUpdate.getQuantity();
                itemToUpdate.setQuantity(0); // Temporarily set to 0 to avoid double-counting

                // Create a temporary item with the new quantity for validation
                DeliveredItem tempItem = new DeliveredItem(productId, newQuantity);
                if (wouldExceedWeightLimit(doc.getTransportId(), tempItem)) {
                    // Reset quantity and warn about weight limit
                    itemToUpdate.setQuantity(oldQuantity);
                    System.out.println("⚠️ Warning: New quantity would exceed the truck's weight limit.");
                    return false;
                }

                // Apply the quantity change
                itemToUpdate.setQuantity(newQuantity);
                destinationDocRepository.save(doc);

                // Update the transport's departure weight
                updateTransportWeight(doc.getTransportId());

                return true;
            }
        }
        return false;
    }

    /**
     * Calculates the total weight of all items in a destination document
     * @param docId The document ID
     * @return The total weight, or 0 if document not found
     */
    public float calculateDocumentWeight(int docId) {
        Optional<DestinationDoc> docOpt = destinationDocRepository.findById(docId);
        if (docOpt.isEmpty()) {
            return 0;
        }

        DestinationDoc doc = docOpt.get();
        float totalWeight = 0;

        for (DeliveredItem item : doc.getDeliveryItems()) {
            // Get product weight (assume each product has a weight)
            Optional<Product> productOpt = productService.getProductById(item.getProductId());
            if (productOpt.isPresent()) {
                totalWeight += productOpt.get().getWeight() * item.getQuantity();
            }
        }

        return totalWeight;
    }

    /**
     * Calculates the total weight of all items in all documents for a transport
     * @param transportId The transport ID
     * @return The total weight of all documents
     */
    public float calculateTotalTransportWeight(int transportId) {
        Optional<Transport> transportOpt = transportService.getTransportById(transportId);
        if (transportOpt.isEmpty()) {
            return 0;
        }

        Transport transport = transportOpt.get();
        float totalWeight = 0;

        for (DestinationDoc doc : transport.getDestinationList()) {
            totalWeight += calculateDocumentWeight(doc.getDestinationDocId());
        }

        return totalWeight;
    }

    /**
     * Checks if adding an item would exceed the truck's weight limit
     * @param transportId The transport ID
     * @param item The item to add
     * @return true if weight limit would be exceeded, false otherwise
     */
    public boolean wouldExceedWeightLimit(int transportId, DeliveredItem item) {
        Optional<Transport> transportOpt = transportService.getTransportById(transportId);
        if (transportOpt.isEmpty()) {
            return false;
        }

        Transport transport = transportOpt.get();
        Truck truck = transport.getTruck();

        // Calculate current total weight
        float currentTotalWeight = calculateTotalTransportWeight(transportId);

        // Calculate weight of the new item
        Optional<Product> productOpt = productService.getProductById(item.getProductId());
        if (productOpt.isEmpty()) {
            return false;
        }

        float itemWeight = productOpt.get().getWeight() * item.getQuantity();
        float newTotalWeight = currentTotalWeight + itemWeight;

        // Check if this exceeds the truck's capacity
        float maxCargoWeight = truck.getMaxWeight() - truck.getNetWeight();
        return newTotalWeight > maxCargoWeight;
    }

    /**
     * Updates the total departure weight of a transport based on all items
     * @param transportId The transport ID
     */
    private void updateTransportWeight(int transportId) {
        float totalWeight = calculateTotalTransportWeight(transportId);
        transportService.updateDepartureWeight(transportId, totalWeight);
    }
}