package ControllerInventorySupplier;

import Inventory.Presentation.MenuController;

import Inventory.Domain.InventoryController;


import java.sql.SQLException;
import java.util.Scanner;

public class InventorySupplierMainMenu {

    public static void main(String[] args) throws SQLException {  //
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome! Would you like to manage Inventory or Supplier?");
        System.out.println("Type '1' for Inventory or '2' for Supplier:");

        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        switch (choice) {
            case 1:
                System.out.println("You have selected Inventory.");

                System.out.println("Do you want to load existing data from the database?");
                System.out.println("Type '1' to load existing data, or '2' to start with an empty system:");
                int dataChoice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                InventoryController inventoryController = new InventoryController();

                if (dataChoice == 1) {
                    inventoryController.loadFromDatabase();
                    System.out.println("Data loaded from database.");
                } else if (dataChoice == 2) {
                    System.out.println("Starting with empty inventory system.");
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