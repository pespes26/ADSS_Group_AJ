package Init;

import Domain.InventoryController;
import java.io.File;

/**
 * Utility class for initializing the inventory system.
 * Responsible for setting up controllers and importing data from external files.
 */
public class SystemInitializer {

    /**
     * Initializes the inventory system from a CSV file.
     * If the file does not exist, starts empty.
     *
     * @param path the path to the CSV file containing inventory data
     * @return an initialized InventoryController instance
     */
    public static InventoryController initializeSystem(String path) {
        InventoryController controller = new InventoryController();
        if (path != null && !path.isEmpty()) {
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                controller.importData(path);
                controller.getProductController().setBranches(controller.getBranches());
            } else {
                System.out.println("Warning: Data file not found. Starting with an empty system.");
            }
        } else {
            System.out.println("No data file provided. Starting with an empty system.");
        }
        return controller;
    }

    /**
     * Initializes the inventory system without loading any data.
     *
     * @return an empty InventoryController instance
     */
    public static InventoryController initializeSystemWithoutData() {
        System.out.println("Starting the system without any initial data.");
        return new InventoryController();
    }
}
