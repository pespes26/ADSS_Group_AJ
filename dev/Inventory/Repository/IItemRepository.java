package Inventory.Repository;

import Inventory.DTO.ItemDTO;

import java.util.List;

/**
 * Repository interface for managing item-related operations.
 */
public interface IItemRepository {
    /**
     * Adds a new item to the system.
     * @param item the item to add
     */
    void addItem(ItemDTO item);

    /**
     * Updates an existing item.
     * @param item the item with updated data
     */
    void updateItem(ItemDTO item);

    /**
     * Deletes an item by its ID.
     * @param itemId the ID of the item to delete
     */
    void deleteItem(int itemId);

    /**
     * Retrieves an item by its ID.
     * @param itemId the ID of the item
     * @return the item DTO, or null if not found
     */
    ItemDTO getItemById(int itemId);

    /**
     * Retrieves all items in the system.
     * @return list of all item DTOs
     */
    List<ItemDTO> getAllItems();

    /**
     * Retrieves all items associated with a specific product.
     * @param productId the catalog number of the product
     * @return list of matching item DTOs
     */
    List<ItemDTO> getItemsByProductId(int productId);

    /**
     * Marks an item as defective by its ID.
     * @param itemId the ID of the item to mark
     */
    void markItemAsDefect(int itemId);

    /**
     * Retrieves all items marked as defective.
     * @return list of defective item DTOs
     */
    List<ItemDTO> getDefectiveItems();
}
