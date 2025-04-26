package Init;

import Domain.InventoryController;

/**
 * Utility class for initializing the inventory system.
 * Responsible for setting up controllers and importing data from external files.
 */
public class SystemInitializer {

    /**
     * Initializes the inventory system from a CSV file.
     * Loads items and products into memory and links branches to the product controller.
     *
     * @param path the path to the CSV file containing inventory data
     * @return an initialized InventoryController instance
     */
    public static InventoryController initializeSystem(String path) {
        InventoryController controller = new InventoryController();
        controller.importData(path);
        controller.getProductController().setBranches(controller.getBranches());
        return controller;
    }

}
