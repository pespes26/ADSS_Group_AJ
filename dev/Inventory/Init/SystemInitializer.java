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
                new ProductDTO(1004, "Orange Juice 1L", "Beverages", "Juices", "Prigat", 1, 6.5, 10.0),
                new ProductDTO(1005, "Butter 200g", "Dairy", "Butter", "Tnuva", 1, 8.0, 5.0),
                new ProductDTO(1006, "White Rice 1kg", "Grocery", "Rice", "Osem", 1, 4.5, 15.0),
                new ProductDTO(1007, "Tomato Sauce 500ml", "Grocery", "Sauces", "Heinz", 1, 6.0, 8.0),
                new ProductDTO(1008, "Yellow Cheese 200g", "Dairy", "Cheese", "Tnuva", 1, 9.5, 12.0),
                new ProductDTO(1009, "Toilet Paper 12-pack", "Household", "Toiletries", "Sano", 12, 20.0, 20.0),
                new ProductDTO(1010, "Chocolate Bar 100g", "Snacks", "Chocolate", "Elite", 1, 5.0, 7.5),
                new ProductDTO(1011, "Mineral Water 1.5L", "Beverages", "Water", "Neviot", 1, 3.0, 5.0),
                new ProductDTO(1012, "Dish Soap 750ml", "Cleaning", "Detergents", "Sano", 1, 7.5, 10.0),
                new ProductDTO(1013, "Cornflakes 750g", "Cereal", "Breakfast", "Telma", 1, 12.0, 18.0)

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
                // Branch 1
                new ItemDTO(1004, 1, "Warehouse", "A1", false, "2025-06-30", null),
                new ItemDTO(1005, 1, "Store", "B1", false, "2025-07-15", null),
                new ItemDTO(1006, 1, "Store", "C1", false, "2025-09-10", null),

                // Branch 2
                new ItemDTO(1007, 2, "Warehouse", "A2", false, "2025-05-01", null),
                new ItemDTO(1008, 2, "Store", "B2", false, "2025-06-20", null),
                new ItemDTO(1009, 2, "Store", "C2", false, "2025-07-25", null),

                // Branch 3
                new ItemDTO(1010, 3, "Warehouse", "A3", false, "2025-08-30", null),
                new ItemDTO(1011, 3, "Store", "B3", false, "2025-10-10", null),
                new ItemDTO(1012, 3, "Store", "C3", false, "2025-11-15", null),

                // Branch 4
                new ItemDTO(1013, 4, "Warehouse", "A4", false, "2025-12-01", null),
                new ItemDTO(1004, 4, "Store", "B4", false, "2026-01-20", null),
                new ItemDTO(1005, 4, "Store", "C4", false, "2026-02-28", null),

                // Branch 5
                new ItemDTO(1006, 5, "Warehouse", "A5", false, "2025-09-05", null),
                new ItemDTO(1007, 5, "Store", "B5", false, "2025-10-01", null),
                new ItemDTO(1008, 5, "Store", "C5", false, "2025-11-11", null),

                // Branch 6
                new ItemDTO(1009, 6, "Warehouse", "A6", false, "2025-12-12", null),
                new ItemDTO(1010, 6, "Store", "B6", false, "2026-01-01", null),
                new ItemDTO(1011, 6, "Store", "C6", false, "2026-02-14", null),

                // Branch 7
                new ItemDTO(1012, 7, "Warehouse", "A7", false, "2026-03-03", null),
                new ItemDTO(1013, 7, "Store", "B7", false, "2026-04-04", null),
                new ItemDTO(1004, 7, "Store", "C7", false, "2026-05-05", null),

                // Branch 8
                new ItemDTO(1005, 8, "Warehouse", "A8", false, "2026-06-06", null),
                new ItemDTO(1006, 8, "Store", "B8", false, "2026-07-07", null),
                new ItemDTO(1007, 8, "Store", "C8", false, "2026-08-08", null),

                // Branch 9
                new ItemDTO(1008, 9, "Warehouse", "A9", false, "2026-09-09", null),
                new ItemDTO(1009, 9, "Store", "B9", false, "2026-10-10", null),
                new ItemDTO(1010, 9, "Store", "C9", false, "2026-11-11", null),

                // Branch 10
                new ItemDTO(1011, 10, "Warehouse", "A10", false, "2026-12-12", null),
                new ItemDTO(1012, 10, "Store", "B10", false, "2027-01-01", null),
                new ItemDTO(1013, 10, "Store", "C10", false, "2027-02-02", null)
                // Add more items as needed
        );
        for (ItemDTO item : items) {
            try {
                itemDAO.Insert(item);
                item.setItemId(itemDAO.GetId(item));
            } catch (SQLException e) {
                System.err.println("Failed to insert item with ID: " + item.getItemId());
            }
        }

    }
}
