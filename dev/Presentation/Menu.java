package Presentation;

import Domain.InventoryController;
import Init.SystemInitializer;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

/**
 * Entry point of the system. Responsible for initializing the inventory system
 * and starting the user interaction menu.
 */
public class Menu {
    /**
     * The main method that initializes the system using a configuration file
     * and launches the main menu interface.
     *
     * @param args Command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        String path = getPathFromConfig();
        InventoryController inventory_controller = SystemInitializer.initializeSystem(path);
        MenuController menu_controller = new MenuController(inventory_controller);
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