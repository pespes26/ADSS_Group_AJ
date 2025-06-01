package ControllerInventorySupplier;

import Inventory.Init.SystemInitializer;
import Inventory.Presentation.MenuController;
import Inventory.Domain.InventoryController;

import java.sql.SQLException;
import java.util.Scanner;

public class InventorySupplierMainMenu {

    public static void main(String[] args) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome! Would you like to manage Inventory or Supplier?");
        System.out.println("Type '1' for Inventory or '2' for Supplier:");

        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        switch (choice) {
            case 1:
                System.out.println("You have selected Inventory.");

                // Create InventoryController instance
                InventoryController inventoryController = new InventoryController();

                System.out.println("Do you want to load existing data from the database?");
                System.out.println("Type '1' to load existing data, or '2' to start with an empty system:");
                int dataChoice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                // Initialize all database tables
                SystemInitializer.initializeAllTables();

                if (dataChoice == 1) {
                    // Load existing data
                    System.out.println("Initializing the system from the database...");
                    System.out.println("🔄 Preloading products and items...");
                    SystemInitializer.preloadProducts();
                    SystemInitializer.preloadItems();
                    System.out.println("✅ Preload completed.");

                    inventoryController.loadFromDatabase();
                } else if (dataChoice == 2) {
                    // ⬅️ כאן תכניס את השורה החדשה
                    SystemInitializer.clearAllTables();

                    System.out.println("Starting with empty inventory system.");
                    // אין צורך לטעון נתונים – התחל ריק
                } else {
                    System.out.println("Invalid choice. Starting with empty system by default.");
                }

                System.out.print("Enter your Branch ID (number between 1 and 10): ");
                int branchId = scanner.nextInt();
                scanner.nextLine();

                MenuController inventoryMenu = new MenuController(inventoryController, branchId);
                inventoryMenu.runMenu();
                break;

            case 2:
                System.out.println("You have selected Supplier.");
                Suppliers.Presentation.MainMenu.main(new String[0]);
                break;

            default:
                System.out.println("Invalid choice! Please run the program again.");
        }

        scanner.close();
    }
}