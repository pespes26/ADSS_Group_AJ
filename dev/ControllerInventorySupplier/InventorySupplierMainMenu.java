package ControllerInventorySupplier;

import Inventory.Domain.InventoryController;
import Inventory.Init.InventoryInitializer;
import Inventory.Presentation.MenuController;
import Suppliers.Presentation.MainMenu;
import Suppliers.Presentation.SuppliersInitializer;

import java.sql.SQLException;
import java.util.Scanner;

public class InventorySupplierMainMenu {

    public static void main(String[] args) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        // יצירת טבלאות של שני המודולים מראש
        InventoryInitializer.initializeAllTables();
        SuppliersInitializer.initializeAllTables();

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
                case 1 -> handleInventory(scanner);
                case 2 -> handleSuppliers(scanner);
                case 3 -> {
                    System.out.println("Exiting system and clearing all data...");
                    InventoryInitializer.clearAllTables();
                    new SuppliersInitializer().clearAllData();
                    System.out.println("✅ All data deleted. Goodbye!");
                    exitSystem = true;
                }
                default -> System.out.println("Invalid choice. Please enter 1, 2 or 3.");
            }
        }

        scanner.close();
    }

    private static void handleInventory(Scanner scanner) {
        System.out.println("You have selected Inventory.");

        InventoryController inventoryController = new InventoryController();

        System.out.println("Do you want to load existing data from the database?");
        System.out.println("1. Load existing data");
        System.out.println("2. Start with an empty system");
        System.out.print("Enter your choice: ");

        int dataChoice = getValidatedInt(scanner);

        if (dataChoice == 1) {
            System.out.println("🔄 Preloading products and items...");
            InventoryInitializer.preloadAllInitialData();
            inventoryController.loadFromDatabase();
            System.out.println("✅ Preload completed.");
        } else if (dataChoice == 2) {
            InventoryInitializer.clearAllTables();
            System.out.println("Starting with empty inventory system.");
        } else {
            System.out.println("Invalid choice. Starting with empty system by default.");
        }

        System.out.print("Enter your Branch ID (1-10): ");
        int branchId = getValidatedInt(scanner);

        MenuController inventoryMenu = new MenuController(inventoryController, branchId);
        inventoryMenu.runMenu(); // חוזר לתפריט הראשי
    }

    private static void handleSuppliers(Scanner scanner) throws SQLException {
        System.out.println("You have selected Suppliers Menu.");

        System.out.print("Do you want to initialize the system with sample data? (yes/no): ");
        String input = scanner.nextLine().trim().toLowerCase();
        boolean withSampleData = input.equals("yes");

        MainMenu.run(scanner, withSampleData);
    }

    private static int getValidatedInt(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. Please enter a number: ");
            scanner.next();
        }
        int result = scanner.nextInt();
        scanner.nextLine(); // 🧹 מנקה את ה־newline
        return result;
    }
}
