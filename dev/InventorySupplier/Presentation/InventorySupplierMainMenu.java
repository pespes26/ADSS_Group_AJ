package InventorySupplier.Presentation;

import Inventory.Domain.InventoryController;
import Inventory.Init.InventoryInitializer;
import Inventory.Presentation.MenuController;
import Suppliers.Presentation.MainMenu;
import Suppliers.Init.SuppliersInitializer;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class InventorySupplierMainMenu {

    public static void main(String[] args) throws SQLException {
        Scanner scanner = new Scanner(System.in);        // Initialize tables for both modules upfront
        InventoryInitializer.initializeAllTables();
        SuppliersInitializer.initializeAllTables();

        // Create InventoryController upfront (not just inside handleInventory)
        InventoryController inventoryController = new InventoryController();

        System.out.println("Welcome to the Inventory-Suppliers Menu! Do you want to load data to database?");
        System.out.println("1. Load data");
        System.out.println("2. Start with an empty system");
        System.out.print("Enter your choice: ");
        int dataChoice = getValidatedInt(scanner);

        switch (dataChoice) {
            case 1 -> {
                System.out.println("Loading existing data from the database...");

                SuppliersInitializer suppliersInitializer = new SuppliersInitializer();
                LinkedHashMap<Integer, Integer> supplierIdAndAgreementsID = suppliersInitializer.initializeDatabase(true);

                InventoryInitializer.preloadAllInitialData(supplierIdAndAgreementsID);

                // קריאה לטעינה מה-DB לזיכרון:
                inventoryController.loadFromDatabase();

                System.out.println("✅ Preload completed.");
            }
            case 2 -> System.out.println("Starting with an empty system.");
            default -> System.out.println("Invalid choice. Starting with empty system by default.");
        }

        boolean exitSystem = false;

        while (!exitSystem) {
            System.out.println("\n==============================================");
            System.out.println("Welcome to the Inventory-Suppliers Menu! What would you like to manage?");
            System.out.println("1. Inventory System");
            System.out.println("2. Supplier System");
            System.out.println("3. Exit the Inventory-Suppliers system");
            System.out.print("Enter your choice (1-3): ");

            int choice = getValidatedInt(scanner);

            switch (choice) {
                case 1 -> handleInventory(scanner, inventoryController);
                case 2 -> handleSuppliers(scanner);
                case 3 -> {
                    System.out.println("Exiting system and clearing all data...");
                    InventoryInitializer.clearAllTables();
                    new SuppliersInitializer().clearAllData();
                    System.out.println("✅ All data deleted. Goodbye!");
                    exitSystem = true;
                }
                default -> System.out.println("Invalid choice. Please enter 1 to 4.");
            }
        }

        scanner.close();
    }

    private static void handleInventory(Scanner scanner, InventoryController inventoryController) {
        System.out.println("You have selected Inventory.");

        System.out.print("Enter your Branch ID (1-10): ");
        int branchId = getValidatedInt(scanner);

        MenuController inventoryMenu = new MenuController(inventoryController, branchId);
        inventoryMenu.runMenu(); // Returns to main menu
    }

    private static void handleSuppliers(Scanner scanner) throws SQLException {
        System.out.println("You have selected Suppliers Menu.");
        MainMenu.run(scanner,false);
    }

    private static int getValidatedInt(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. Please enter a number: ");
            scanner.next();
        }
        int result = scanner.nextInt();
        scanner.nextLine(); // Clear buffer to avoid issues with nextLine
        return result;
    }
}
