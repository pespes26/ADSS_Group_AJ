package Presentation;

import Domain.InventoryController;
import Init.SystemInitializer;
import org.yaml.snakeyaml.Yaml;

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
     * Loads inventory data, prompts the user to select a branch, and launches the menu controller.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        String path = getPathFromConfig();
        InventoryController inventory_controller = SystemInitializer.initializeSystem(path);

        Scanner scan = new Scanner(System.in);

        System.out.println("------------------------------------------------------------");
        System.out.println(" Welcome to Super-Li Inventory System ");
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

        System.out.println("Branch " + branch_id + " selected. All operations will now apply to this branch.");
        System.out.println("------------------------------------------------------------");

        MenuController menu_controller = new MenuController(inventory_controller, branch_id);
        menu_controller.runMenu();
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