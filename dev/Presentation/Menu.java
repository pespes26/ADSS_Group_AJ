package Presentation;

import Domain.InventoryController;
import Init.SystemInitializer;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

public class Menu {
    public static void main(String[] args) {
        String path = getPathFromConfig();
        InventoryController inventoryController = SystemInitializer.initializeSystem(path);
        MenuController menuController = new MenuController(inventoryController);
        menuController.runMenu();
    }

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