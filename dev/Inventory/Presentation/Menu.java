package Inventory.Presentation;

import Inventory.Domain.InventoryController;
import Inventory.Init.SystemInitializer;

import java.util.Scanner;

/**
 * Main menu class for the Super-Li Inventory System.
 * Initializes the system from the SQLite database and manages the main user flow.
 * Handles branch selection and delegates control to the MenuController.
 */
public class Menu {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        // Welcome message
        System.out.println("------------------------------------------------------------");
        System.out.println(" Welcome to Super-Li Inventory System ");
        System.out.println("------------------------------------------------------------");



        // Prompt user to select branch ID (1-10)
        System.out.println("------------------------------------------------------------");
        System.out.println("Please select your branch before continuing.");
        System.out.println("Enter your Branch ID (1 - 10):");

        int branchId = -1;
        while (branchId < 1 || branchId > 10) {
            try {
                branchId = Integer.parseInt(scan.nextLine().trim());
                if (branchId < 1 || branchId > 10) {
                    System.out.print("Invalid branch ID. Please enter a number between 1 and 10: ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number between 1 and 10: ");
            }
        }
        InventoryController inventoryController = SystemInitializer.initializeSystemFromDatabase();

        // Confirm branch selection
        System.out.println("Branch " + branchId + " selected. All operations will now apply to this branch.");
        System.out.println("------------------------------------------------------------");

        // Launch the main menu controller
        MenuController menuController = new MenuController(inventoryController, branchId);
        menuController.runMenu();
    }
}
