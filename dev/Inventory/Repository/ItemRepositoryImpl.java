package Inventory.Repository;

import java.util.List;

import Inventory.DTO.ItemDTO;
import Inventory.DAO.IItemsDAO;
import Inventory.DAO.JdbcItemDAO;

import java.sql.SQLException;

/**
 * Implementation of the ItemRepository interface using a JDBC DAO.
 * Provides methods to interact with the items in the inventory.
 */
public class ItemRepositoryImpl implements IItemRepository {
    private final IItemsDAO itemDAO;

    /**
     * Constructs a new ItemRepositoryImpl using a JdbcItemDAO.
     */
    public ItemRepositoryImpl() {
        this.itemDAO = new JdbcItemDAO();
    }

    /**
     * Adds a new item to the database.
     *
     * @param item the item to be added
     */
    @Override
    public void addItem(ItemDTO item) {
        try {
            itemDAO.Insert(item);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates an existing item in the database.
     *
     * @param item the item with updated values
     */
    @Override
    public void updateItem(ItemDTO item) {
        try {
            itemDAO.Update(item);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes an item from the database by its ID.
     *
     * @param itemId the ID of the item to delete
     */
    @Override
    public void deleteItem(int itemId) {
        try {
            itemDAO.DeleteByItemId(itemId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves an item from the database by its ID.
     *
     * @param itemId the ID of the item to retrieve
     * @return the matching ItemDTO, or null if not found
     */
    @Override
    public ItemDTO getItemById(int itemId) {
        try {
            return itemDAO.GetById(itemId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves all items from the database.
     *
     * @return a list of all ItemDTOs
     */
    @Override
    public List<ItemDTO> getAllItems() {
        try {
            return itemDAO.getAllItems();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<ItemDTO> getItemsByProductId(int productId) {
        return itemDAO.getItemsByProductId(productId);
    }

    /**
     * Marks an item as defective in the database by its ID.
     *
     * @param itemId the ID of the item to mark as defective
     */
    @Override
    public void markItemAsDefect(int itemId) {
        try {
            itemDAO.signAsDefective(itemId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves all items marked as defective from the database.
     *
     * @return a list of defective items
     */
    @Override
    public List<ItemDTO> getDefectiveItems() {
        return itemDAO.findDefectiveItems();
    }
}
