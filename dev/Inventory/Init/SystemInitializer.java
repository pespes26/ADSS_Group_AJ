package Inventory.Init;
import Inventory.DAO.JdbcItemDAO;
import Inventory.DAO.JdbcProductDAO;
import Inventory.DTO.ItemDTO;
import Inventory.DTO.ProductDTO;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;


import Inventory.Domain.InventoryController;

/**
 * Utility class for initializing the inventory system.
 * Responsible for setting up controllers and loading data from the database.
 */
public class SystemInitializer {

    /**
     * Initializes the inventory system from the SQLite database.
     *
     * @return an initialized InventoryController instance
     */
    public static InventoryController initializeSystemFromDatabase() {
        System.out.println("Initializing the system from the database...");

        InventoryController controller = new InventoryController();
        controller.loadFromDatabase();

        System.out.println("System initialized successfully from the database.");
        return controller;
    }

    public static void preloadProducts() {
        JdbcProductDAO productDAO = new JdbcProductDAO();
        List<ProductDTO> products = Arrays.asList(
                new ProductDTO(1001, "Milk 1L", "Dairy", "Milk", "Tnuva", 1, 5.5, 0.0),
                new ProductDTO(1002, "Bread", "Bakery", "Bread", "Angel", 1, 7.0, 0.0),
                new ProductDTO(1003, "Eggs 12-pack", "Dairy", "Eggs", "Yesh Maof", 12, 12.0, 0.0)
                // Add more products as needed
        );
        for (ProductDTO product : products) {
            try {
                productDAO.Insert(product);
            } catch (SQLException e) {
                System.err.println("Failed to insert product: " + product.getProductName());
            }
        }
    }

    public static void preloadItems() {
        JdbcItemDAO itemDAO = new JdbcItemDAO();
        List<ItemDTO> items = Arrays.asList(
                // item_id is auto-generated, so use 0 or omit in constructor if possible
                new ItemDTO(1001, 0, 1, "Warehouse", "A7", false, "2025-06-30", null),
                new ItemDTO(1002, 0, 1, "Store", "B2", false, "2024-11-15", null),
                new ItemDTO(1003, 0, 1, "Store", "C1", false, "2024-10-20", null)
                // Add more items as needed
        );
        for (ItemDTO item : items) {
            try {
                itemDAO.Insert(item);
            } catch (SQLException e) {
                System.err.println("Failed to insert item with ID: " + item.getItemId());
            }
        }
    }
}
