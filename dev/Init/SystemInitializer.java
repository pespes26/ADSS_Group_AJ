package Init;

import Domain.InventoryController;

/**
 * Responsible for initializing the inventory system by creating the controller
 * and importing data from the provided file path.
 */
public class SystemInitializer {

    /**
     * Initializes the inventory system.
     * This method creates a new instance of {@link InventoryController},
     * imports data from the CSV file located at the specified path,
     * and returns the fully initialized controller.
     *
     * @param path The file path to the CSV data source.
     * @return An initialized {@link InventoryController} with data loaded from the file.
     */
    public static InventoryController initializeSystem(String path) {
        InventoryController controller = new InventoryController();
        controller.importData(path);
        return controller;
    }
}
