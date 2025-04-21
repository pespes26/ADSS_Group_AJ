package Presentation;

import Service.InventoryServiceFacade;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

public class Menu {
    public static void main(String[] args) {

        InventoryServiceFacade inventoryServiceFacade = new InventoryServiceFacade();
        String path = getPathFromConfig();
        inventoryServiceFacade.importData(path);

        MenuController menuController = new MenuController(inventoryServiceFacade);
        menuController.runMenu();
    }

    public static String getPathFromConfig() {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = new FileInputStream("config.yaml")) {
            Map<String, Object> config = yaml.load(inputStream);
            return (String) config.get("path");
        } catch (Exception e) {
            System.out.println("Error loading path from config.yaml: " + e.getMessage());
        }
        return "";
    }
}
