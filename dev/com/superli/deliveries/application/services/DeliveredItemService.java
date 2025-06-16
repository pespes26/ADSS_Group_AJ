package com.superli.deliveries.application.services;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.superli.deliveries.dataaccess.dao.del.DeliveredItemDAO;
import com.superli.deliveries.dataaccess.dao.del.DestinationDocDAO;
import com.superli.deliveries.domain.core.DeliveredItem;
import com.superli.deliveries.domain.core.DestinationDoc;
import com.superli.deliveries.domain.core.Product;
import com.superli.deliveries.domain.core.Transport;
import com.superli.deliveries.domain.core.Truck;
import com.superli.deliveries.dto.del.DeliveredItemDTO;
import com.superli.deliveries.dto.del.DestinationDocDTO;
/**
 * Service responsible for managing delivered items within destination documents.
 * Also handles weight calculation and validation.
 */
public class DeliveredItemService {

    private final DeliveredItemDAO deliveredItemDAO;
    private final TransportService transportService;
    private final ProductService productService;
    private final DestinationDocDAO destinationDocDAO;

    public DeliveredItemService(DeliveredItemDAO deliveredItemDAO,
                                TransportService transportService,
                                ProductService productService,
                                DestinationDocDAO destinationDocDAO) {
        this.deliveredItemDAO = deliveredItemDAO;
        this.transportService = transportService;
        this.productService = productService;
        this.destinationDocDAO = destinationDocDAO;
    }

    /**
     * Retrieves all delivered items.
     * @return A list of all delivered items.
     */
    public List<DeliveredItem> getAllDeliveredItems() {
        try {
            return deliveredItemDAO.findAll().stream()
                    .map(dto -> new DeliveredItem(
                            dto.getId(),
                            dto.getDestinationDocId(),
                            dto.getProductId(),
                            dto.getQuantity()))
                            .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all delivered items", e);
        }
    }

    /**
     * Retrieves a delivered item by its ID.
     * @param id The ID of the delivered item to retrieve.
     * @return An Optional containing the delivered item if found, empty otherwise.
     */
    public Optional<DeliveredItem> getDeliveredItemById(String id) {
        try {
            return deliveredItemDAO.findById(id)
                    .map(dto -> new DeliveredItem(
                            dto.getId(),
                            dto.getDestinationDocId(),
                            dto.getProductId(),
                            dto.getQuantity()));
        } catch (SQLException e) {
            throw new RuntimeException("Error getting delivered item by ID: " + id, e);
        }
    }

    /**
     * Saves a delivered item.
     * @param item The delivered item to save.
     * @return The saved delivered item.
     */
    public DeliveredItem saveDeliveredItem(DeliveredItem item) {
        try {
            DeliveredItemDTO dto = new DeliveredItemDTO(
                    item.getItemId(),
                    item.getDestinationDocId(),
                    item.getProductId(),
                    item.getQuantity());
            DeliveredItemDTO savedDto = deliveredItemDAO.save(dto);
            return new DeliveredItem(
                    savedDto.getId(),
                    savedDto.getDestinationDocId(),
                    savedDto.getProductId(),
                    savedDto.getQuantity());
        } catch (SQLException e) {
            throw new RuntimeException("Error saving delivered item", e);
        }
    }

    /**
     * Deletes a delivered item by its ID.
     * @param id The ID of the delivered item to delete.
     */
    public void deleteDeliveredItem(String id) {
        try {
            deliveredItemDAO.deleteById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting delivered item: " + id, e);
        }
    }

    /**
     * Retrieves all delivered items for a specific transport.
     * @param transportId The ID of the transport.
     * @return A list of delivered items for the transport.
     */
    public List<DeliveredItem> getDeliveredItemsByTransport(String transportId) {
        try {
            return deliveredItemDAO.findAll().stream()
                    .filter(dto -> dto.getDestinationDocId().equals(transportId))
                    .map(dto -> new DeliveredItem(
                            dto.getId(),
                            dto.getDestinationDocId(),
                            dto.getProductId(),
                            dto.getQuantity()))
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Error getting delivered items for transport: " + transportId, e);
        }
    }

    /**
     * Retrieves all delivered items for a specific product.
     * @param productId The ID of the product.
     * @return A list of delivered items for the product.
     */
    public List<DeliveredItem> getDeliveredItemsByProduct(String productId) {
        try {
            return deliveredItemDAO.findAll().stream()
                    .filter(dto -> dto.getProductId().equals(productId))
                    .map(dto -> new DeliveredItem(
                            dto.getId(),
                            dto.getDestinationDocId(),
                            dto.getProductId(),
                            dto.getQuantity()))
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Error getting delivered items for product: " + productId, e);
        }
    }

    /**
     * Adds a new delivered item to an existing destination doc.
     * @param docId DestinationDoc ID.
     * @param item The delivered item to add.
     * @return true if added successfully, false otherwise.
     */
    public boolean addDeliveredItem(String docId, DeliveredItem item) {
        try {
            // Get the product to calculate weight
            Optional<Product> productOpt = productService.getProductById(item.getProductId());
            if (productOpt.isEmpty()) {
                System.out.println("❌ Product not found!");
                return false;
            }

            Product product = productOpt.get();
            double itemWeight = product.getWeight() * item.getQuantity();

            // Check if weight limit would be exceeded
            if (wouldExceedWeightLimit(docId, item)) {
                System.out.println("❌ Adding this quantity would exceed the truck's weight limit!");
                return false;
            }

            // Generate a new item ID
            String itemId = getNextItemId();
            item = new DeliveredItem(itemId, docId, item.getProductId(), item.getQuantity());
            
            DeliveredItemDTO dto = new DeliveredItemDTO(
                    itemId,
                    docId,
                    item.getProductId(),
                    item.getQuantity());
            deliveredItemDAO.save(dto);

            // Update the transport's departure weight
            updateTransportWeight(docId);

            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Error adding delivered item", e);
        }
    }

    /**
     * Generates the next available item ID
     */
    private String getNextItemId() {
        try {
            List<DeliveredItemDTO> items = deliveredItemDAO.findAll();
            int maxId = 0;
            for (DeliveredItemDTO item : items) {
                String id = item.getId();
                if (id != null && id.startsWith("I")) {
                    try {
                        int num = Integer.parseInt(id.substring(1));
                        maxId = Math.max(maxId, num);
                    } catch (NumberFormatException e) {
                        // Skip invalid IDs
                    }
                }
            }
            return String.format("I%03d", maxId + 1);
        } catch (SQLException e) {
            throw new RuntimeException("Error generating item ID", e);
        }
    }

    /**
     * Removes a delivered item from a destination doc.
     * @param docId DestinationDoc ID.
     * @param item The delivered item to remove.
     * @return true if removed, false otherwise.
     */
    public boolean removeDeliveredItem(String docId, DeliveredItem item) {
        try {
            List<DeliveredItemDTO> items = deliveredItemDAO.findAll();
            Optional<DeliveredItemDTO> itemToRemove = items.stream()
                    .filter(dto -> dto.getDestinationDocId().equals(docId) && 
                                 dto.getProductId().equals(item.getProductId()))
                    .findFirst();

            if (itemToRemove.isPresent()) {
                deliveredItemDAO.deleteById(itemToRemove.get().getId());

                // Update the transport's departure weight
                updateTransportWeight(docId);
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error removing delivered item", e);
        }
    }

    /**
     * Retrieves all delivered items for a specific destination doc.
     * @param docId The ID of the destination document.
     * @return A list of delivered items, or an empty list if not found.
     */
    public List<DeliveredItem> getDeliveredItemsForDoc(String docId) {
        try {
            return deliveredItemDAO.findAll().stream()
                    .filter(dto -> dto.getDestinationDocId().equals(docId))
                    .map(dto -> new DeliveredItem(
                            dto.getId(),
                            dto.getDestinationDocId(),
                            dto.getProductId(),
                            dto.getQuantity()))
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Error getting delivered items for doc: " + docId, e);
        }
    }

    /**
     * Updates the quantity of a delivered item for a specific product.
     * @param docId DestinationDoc ID.
     * @param productId Product ID of the delivered item to update.
     * @param newQuantity The new quantity to set.
     * @return true if the item was found and updated, false otherwise.
     */
    public boolean updateItemQuantity(String docId, String productId, int newQuantity) {
        try {
            List<DeliveredItemDTO> items = deliveredItemDAO.findAll();
            Optional<DeliveredItemDTO> itemToUpdate = items.stream()
                    .filter(dto -> dto.getDestinationDocId().equals(docId) && 
                                 dto.getProductId().equals(productId))
                    .findFirst();

            if (itemToUpdate.isPresent()) {
                DeliveredItemDTO dto = itemToUpdate.get();
                dto.setQuantity(newQuantity);
                deliveredItemDAO.save(dto);

                // Update the transport's departure weight
                updateTransportWeight(docId);
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating item quantity", e);
        }
    }

    /**
     * Calculates the total weight of all items in a destination document
     * @param docId The document ID
     * @return The total weight, or 0 if document not found
     */
    public float calculateDocumentWeight(String docId) {
        List<DeliveredItem> items = getDeliveredItemsForDoc(docId);
        float totalWeight = 0;

        for (DeliveredItem item : items) {
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
    public float calculateTotalTransportWeight(String transportId) {
        Optional<Transport> transportOpt = transportService.getTransportById(transportId);
        if (transportOpt.isEmpty()) {
            return 0;
        }

        Transport transport = transportOpt.get();
        float totalWeight = 0;

        for (DestinationDoc doc : transport.getDestinationDocs()) {
            totalWeight += calculateDocumentWeight(doc.getDestinationDocId());
        }

        return totalWeight;
    }

    /**
     * Checks if adding an item would exceed the truck's weight limit
     * @param docId The document ID
     * @param item The item to check
     * @return true if adding the item would exceed the limit
     */
    private boolean wouldExceedWeightLimit(String docId, DeliveredItem item) {
        // Get the destination document to find its transport
        Optional<DestinationDocDTO> docOpt = destinationDocDAO.findById(docId);
        if (docOpt.isEmpty()) {
            return false;
        }
        String transportId = docOpt.get().getTransportId();
        
        // Get the transport and its truck
        Optional<Transport> transportOpt = transportService.getTransportById(transportId);
        if (transportOpt.isEmpty()) {
            return false;
        }
        Transport transport = transportOpt.get();
        Truck truck = transport.getTruck();
        
        // Calculate current weight
        float currentWeight = calculateTotalTransportWeight(transportId);
        
        // Calculate new item weight
        Optional<Product> productOpt = productService.getProductById(item.getProductId());
        if (productOpt.isEmpty()) {
            return false;
        }
        float itemWeight = productOpt.get().getWeight() * item.getQuantity();
        
        // Check if total weight would exceed truck's max weight
        float totalWeight = currentWeight + itemWeight;
        return totalWeight > truck.getMaxWeight();
    }

    /**
     * Updates the transport's departure weight based on all items in its documents
     * @param docId The document ID
     */
    private void updateTransportWeight(String docId) {
        // Get the destination document to find its transport
        Optional<DestinationDocDTO> docOpt = destinationDocDAO.findById(docId);
        if (docOpt.isEmpty()) {
            return;
        }
        String transportId = docOpt.get().getTransportId();
        
        // Get the transport
        Optional<Transport> transportOpt = transportService.getTransportById(transportId);
        if (transportOpt.isEmpty()) {
            return;
        }
        Transport transport = transportOpt.get();
        
        // Calculate new total weight
        float totalWeight = calculateTotalTransportWeight(transportId);
        
        // Update the transport's weight
        transport.setDepartureWeight(totalWeight);
        transportService.saveTransport(transport);
        
        // Verify the update
        Optional<Transport> verifyOpt = transportService.getTransportById(transportId);
        if (verifyOpt.isPresent() && Math.abs(verifyOpt.get().getDepartureWeight() - totalWeight) > 0.001f) {
            throw new RuntimeException("Weight update verification failed");
        }
    }
}