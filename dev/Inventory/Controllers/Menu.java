package Inventory.Controllers;

import Inventory.Domain.InventoryController;
import Inventory.Init.SystemInitializer;
import org.yaml.snakeyaml.Yaml;
//import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;

/**
 * Main menu class for the Super-Li Inventory System.
 * Initializes the system, loads configuration, and manages the main user flow.
 * Handles branch selection and delegates control to the MenuController.
 */
public class Menu {


    /**
     * Entry point of the Super-Li Inventory System.
     * This method initializes the system based on user selection:
     * - The user can choose to load initial inventory data from a CSV file.
     * - Alternatively, the user can choose to start the system without any data.
     * After initialization, the user is prompted to select a branch (1-10),
     * and the main menu controller is launched to manage further operations.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        // Welcome message
        System.out.println("------------------------------------------------------------");
        System.out.println(" Welcome to Super-Li Inventory System ");
        System.out.println("------------------------------------------------------------");

        // Ask user whether to load data or start empty
        System.out.println("Choose startup mode:");
        System.out.println("1. Load data from CSV file");
        System.out.println("2. Start with empty system");

        int startupChoice = -1;
        while (startupChoice != 1 && startupChoice != 2) {
            try {
                startupChoice = Integer.parseInt(scan.nextLine().trim());
                if (startupChoice != 1 && startupChoice != 2) {
                    System.out.print("Invalid choice. Please enter 1 or 2: ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter 1 or 2: ");
            }
        }

        // Initialize inventory system based on the user's choice
        InventoryController inventory_controller;
        if (startupChoice == 1) {
            String path = getPathFromConfig();
            inventory_controller = SystemInitializer.initializeSystem(path);
        } else {
            inventory_controller = SystemInitializer.initializeSystemWithoutData();
        }

        // Prompt user to select branch ID (1-10)
        System.out.println("------------------------------------------------------------");
        System.out.println("Please select your branch before continuing.");
        System.out.println("Enter your Branch ID (1 - 10):");

        int branch_id = -1;
        while (branch_id < 1 || branch_id > 10) {
            try {
                branch_id = Integer.parseInt(scan.nextLine().trim());
                if (branch_id < 1 || branch_id > 10) {
                    System.out.print("Invalid branch ID. Please enter a number between 1 and 10: ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number between 1 and 10: ");
            }
        }

        // Confirm branch selection
        System.out.println("Branch " + branch_id + " selected. All operations will now apply to this branch.");
        System.out.println("------------------------------------------------------------");

        // Launch the main menu controller
        //Inventory.Presentation.MenuController menu_controller = new Inventory.Presentation.MenuController(inventory_controller, branch_id);
        //menu_controller.runMenu();
    }



    /**
     * Retrieves the file path to the system data from a YAML configuration file named "config.yaml".
     * The configuration file must contain a top-level key named "path".
     *
     * @return The path as a string, or an empty string if loading failed.
     */
    public static String getPathFromConfig() {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = new FileInputStream("config.yaml")) {
            Map<String, Object> config = yaml.load(inputStream);
            return (String) config.get("path");
        } catch (Exception e) {
            System.out.println("Error loading path: " + e.getMessage());
            return "";
        }
    }
}