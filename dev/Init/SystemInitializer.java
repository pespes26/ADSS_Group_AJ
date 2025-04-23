package Init;

import Domain.InventoryController;

public class SystemInitializer {
    public static InventoryController initializeSystem(String path) {
        InventoryController controller = new InventoryController();
        controller.importData(path);
        return controller;
    }
}
