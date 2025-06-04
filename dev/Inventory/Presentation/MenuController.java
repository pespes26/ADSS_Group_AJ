package Inventory.Presentation;

import Inventory.DTO.*;
import Inventory.Domain.*;
import Suppliers.Init.SupplierRepositoryInitializer;
import Inventory.Repository.*;
import InventorySupplier.SystemService.ShortageOrderService;
import Suppliers.Repository.IInventoryOrderRepository;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;


public class MenuController {
    private final Scanner scan;
    private final InventoryController inventory_controller;
    private final int current_branch_id;
    public MenuController(InventoryController inventory_controller, int currentBranchId) {

        this.scan = new Scanner(System.in);
        this.inventory_controller = inventory_controller;
        this.current_branch_id = currentBranchId;
    }

    private void printWelcome() {
        System.out.println("""
    =============================================
    |                                            |
    |   Welcome to the Inventory Module!     |
    |                                            |
    =============================================""");
        System.out.println("        You are currently in Branch #" + current_branch_id);
        System.out.println("=============================================\n");
    }

    public void runMenu() {
        printWelcome();
        int mainChoice = 0;

        while (mainChoice != 3) {
            System.out.println("""
        Main Menu:
        1. Inventory Functions (Part 1)
        2. Supplier & Periodic Orders (Part 2)
        3. Exit
        """);

            try {
                mainChoice = Integer.parseInt(scan.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (mainChoice) {
                case 1 -> runPart1InventoryMenu();
                case 2 -> runPart2InventoryMenu();
                case 3 -> System.out.println("Thank you! Have a nice day :)");
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }    private void runPart2InventoryMenu() {
        int choice = 0;
        while (choice != 6) {            System.out.println("""
                Order Management & Tracking Menu:
                1. Show Shortage Alerts for this branch
                2. Update Periodic Order
                3. Place Supplier Order Due to Shortage
                4. View All Periodic Orders
                5. View Pending Shortage Orders
                6. Back to Main Menu
                """);

            try {
                choice = Integer.parseInt(scan.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }            switch (choice) {
                case 1 -> showShortageAlertsForBranch();
                case 2 -> updatePeriodicOrder();
                case 3 -> placeShortageBasedSupplierOrder();
                case 4 -> viewAllPeriodicOrders();
                case 5 -> viewPendingShortageOrders();
                case 6 -> System.out.println("Returning to Main Menu...");
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }    private void showShortageAlertsForBranch() {
        try {
            // Generate and show shortage report
            String report = inventory_controller.getReportController().generateShortageInventoryReport(current_branch_id);
            System.out.println("\n----------- Shortage Alert Report for Branch #" + current_branch_id + " -----------");
            System.out.println(report);
            System.out.println("----------------------------------------\n");

        } catch (Exception e) {
            System.out.println("❌ Failed to generate shortage report: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updatePeriodicOrder() {
        try {
            List<PeriodicOrderDTO> orders = inventory_controller.getPeriodicOrderRepository()
                .getAllPeriodicOrders()
                .stream()
                .filter(order -> order.getBranchId() == current_branch_id)
                .collect(Collectors.toList());

            if (orders.isEmpty()) {
                System.out.println("\nNo periodic orders found for Branch #" + current_branch_id);
                return;
            }

            // Display all periodic orders
            System.out.println("\n----------- Existing Periodic Orders -----------");
            for (PeriodicOrderDTO order : orders) {
                ProductDTO product = inventory_controller.getProductRepository().getProductByCatalogNumber(order.getProductCatalogNumber());
                String productName = product != null ? product.getProductName() : "Unknown Product";

                System.out.println("Order #" + order.getOrderId());
                System.out.println("Product: " + productName + " (Catalog #" + order.getProductCatalogNumber() + ")");
                System.out.println("Supply Days: " + order.getDaysInTheWeek());
                System.out.println("Quantity: " + order.getQuantity());
                System.out.println("Order Date: " + order.getOrderDate());
                System.out.println("Supplier: " + order.getSupplierName() + " (ID: " + order.getSupplierId() + ")");
                System.out.println("----------------------------------------");
            }

            // Get order selection from user
            System.out.print("\nEnter the Order # you want to update: ");
            int selectedOrderId = Integer.parseInt(scan.nextLine().trim());

            // Find the selected order
            PeriodicOrderDTO selectedOrder = orders.stream()
                .filter(o -> o.getOrderId() == selectedOrderId)
                .findFirst()
                .orElse(null);

            if (selectedOrder == null) {
                System.out.println("❌ Invalid Order #. Please try again.");
                return;
            }

            // Check if order is scheduled for today or tomorrow
            String[] orderDays = selectedOrder.getDaysInTheWeek().toUpperCase().split("[,\\s]+");
            String today = LocalDate.now().getDayOfWeek().name().toUpperCase();
            String tomorrow = LocalDate.now().plusDays(1).getDayOfWeek().name().toUpperCase();

            for (String day : orderDays) {
                if (day.equals(today) || day.equals(tomorrow)) {
                    System.out.println("❌ Cannot update this order - it is scheduled for delivery today/tomorrow.");
                    return;
                }
            }

            // Show update options
            System.out.println("\nWhat would you like to update?");
            System.out.println("1. Supply Days");
            System.out.println("2. Order Quantity");
            System.out.println("3. Both");
            System.out.print("Enter your choice: ");
            
            int updateChoice = Integer.parseInt(scan.nextLine().trim());
            String newDays = null;
            Integer newQuantity = null;

            if (updateChoice == 1 || updateChoice == 3) {
                System.out.println("\nCurrent supply days: " + selectedOrder.getDaysInTheWeek());
                System.out.println("Enter new supply days (e.g., SUNDAY, WEDNESDAY, FRIDAY): ");
                newDays = scan.nextLine().trim().toUpperCase();
            }

            if (updateChoice == 2 || updateChoice == 3) {
                System.out.println("\nCurrent quantity: " + selectedOrder.getQuantity());
                System.out.print("Enter new quantity: ");
                newQuantity = Integer.parseInt(scan.nextLine().trim());
            }

            // Update the order
            if (newDays != null) {
                selectedOrder.setDaysInTheWeek(newDays);
            }
            if (newQuantity != null) {
                selectedOrder.setQuantity(newQuantity);
            }

            // Save updates to database
            inventory_controller.getPeriodicOrderRepository().updatePeriodicOrder(selectedOrder);

            System.out.println("\n✅ Periodic order #" + selectedOrderId + " updated successfully!");
            System.out.println("----------------------------------------");
            if (newDays != null) {
                System.out.println("New supply days: " + newDays);
            }
            if (newQuantity != null) {
                System.out.println("New quantity: " + newQuantity);
            }
            System.out.println("----------------------------------------");

        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input. Please enter valid numbers.");
        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ An error occurred: " + e.getMessage());
        }
    }
    private void placeShortageBasedSupplierOrder() {
        try {
            // Initialize repositories
            IItemRepository itemRepository = new ItemRepositoryImpl();
            IProductRepository productRepository = new ProductRepositoryImpl();
            IShortageOrderRepository shortageOrderRepository = new ShortageOrderRepositoryImpl();

            // Get today's date and day of week
            LocalDate today = LocalDate.now();
            String currentDay = today.getDayOfWeek().name().toUpperCase();

            // Check today's processed orders to avoid duplicate processing
            if (shortageOrderRepository.hasBeenProcessedToday(current_branch_id)) {
                System.out.println("\n❌ Shortage orders for branch " + current_branch_id + " have already been processed today.");
                System.out.println("Please try again tomorrow to process new shortage orders.");
                return;
            }            // Get all pending shortage orders for current branch
            List<ShortageOrderDTO> shortageOrders = shortageOrderRepository.getAll().stream()
                .filter(order -> order.getBranchId() == current_branch_id && "PENDING".equals(order.getStatus()))
                .collect(Collectors.toList());

            boolean anyOrdersProcessed = false;

            if (!shortageOrders.isEmpty()) {
                System.out.println("\n----------- Processing Existing Shortage Orders -----------");

                // Process each shortage order
                for (ShortageOrderDTO order : shortageOrders) {
                // Get product details
                ProductDTO product = productRepository.getProductByCatalogNumber(order.getProductCatalogNumber());
                if (product == null) continue;

                // Get current inventory level
                int currentStock = itemRepository.countItemsByCatalogNumber(order.getProductCatalogNumber(), current_branch_id);

                // Check if this order should be processed today
                if (order.getDaysInTheWeek() != null && order.getDaysInTheWeek().toUpperCase().contains(currentDay)) {
                    // Add new items to inventory
                    for (int i = 0; i < order.getQuantity(); i++) {                        ItemDTO newItem = new ItemDTO(
                            order.getProductCatalogNumber(), 
                            current_branch_id,
                            "Warehouse", // storage_location
                            "A1", // section_in_store (using default section)
                            false, // is_defect
                            LocalDate.now().plusYears(2).toString() // item_expiring_date (setting default 2 years expiry)
                        );
                        itemRepository.addItem(newItem);
                    }                    // Update order status
                    order.setStatus("DELIVERED");
                    shortageOrderRepository.update(order);

                    // Get updated inventory level
                    int newStock = itemRepository.countItemsByCatalogNumber(order.getProductCatalogNumber(), current_branch_id);

                    System.out.println("\n✅ Processed shortage order #" + order.getOrderId() + ":");
                    System.out.println("Product: " + product.getProductName() + " (Catalog #" + product.getCatalogNumber() + ")");
                    System.out.println("Previous stock level: " + currentStock);
                    System.out.println("Added quantity: " + order.getQuantity());
                    System.out.println("New stock level: " + newStock);
                    System.out.println("----------------------------------------");                    anyOrdersProcessed = true;
                }
            }
            } else {
                System.out.println("\nℹ️ No pending shortage orders found for branch " + current_branch_id);
            }            if (anyOrdersProcessed) {
                // Mark orders as processed for today
                shortageOrderRepository.markProcessedForToday(current_branch_id);
                System.out.println("\n✅ Successfully processed existing shortage orders for branch " + current_branch_id);
            }

            System.out.println("\n----------- Checking for New Shortages -----------");

            // Continue with new shortage detection
            List<ItemDTO> items = itemRepository.getAllItems();
            Map<Integer, ProductDTO> productMap = new HashMap<>();
            for (ProductDTO dto : productRepository.getAllProducts()) {
                productMap.put(dto.getCatalogNumber(), dto);
            }

            Map<Integer, Map<Integer, Integer>> shortageMap = new HashMap<>();

            for (ItemDTO item : items) {
                if (item.IsDefective()) continue;

                int branchId = item.getBranchId();
                int catalog = item.getCatalogNumber();

                ProductDTO product = productMap.get(catalog);
                if (product == null) continue;

                int currentQty = shortageMap
                        .computeIfAbsent(branchId, b -> new HashMap<>())
                        .getOrDefault(catalog, 0);
                shortageMap.get(branchId).put(catalog, currentQty + 1);
            }            // ניתוח חוסרים + תנאי זמן
            boolean newShortagesFound = false;
            for (Map.Entry<Integer, Map<Integer, Integer>> branchEntry : shortageMap.entrySet()) {
                int branchId = branchEntry.getKey();
                Map<Integer, Integer> catalogQtyMap = branchEntry.getValue();                if (branchId != current_branch_id) continue; // רק לסניף הנוכחי

                Map<Integer, Integer> shortages = new HashMap<>();

                for (Map.Entry<Integer, Integer> catalogEntry : catalogQtyMap.entrySet()) {
                    int catalog = catalogEntry.getKey();
                    int actualQty = catalogEntry.getValue();

                    ProductDTO product = productMap.get(catalog);
                    if (product == null) continue;

                    int minRequiredQty = Math.max(1, (product.getSupplyTime() + product.getProductDemandLevel()) / 2);                    if (actualQty < minRequiredQty) {
                        int shortageAmount = (minRequiredQty - actualQty) + 10; // New calculation: includes 10 spare units
                        shortages.put(catalog, shortageAmount);
                    }
                }                if (!shortages.isEmpty()) {
                    // Order immediately when any shortage is detected - conditions removed
                    IInventoryOrderRepository supplierRepo = new SupplierRepositoryInitializer().getSupplierOrderRepository();
                    ShortageOrderService shortageOrderService = new ShortageOrderService(supplierRepo, shortageOrderRepository);

                    shortageOrderService.onWakeUp(new HashMap<>(shortages), current_branch_id);
                    System.out.println("✅ New shortage-based orders created for branch " + current_branch_id);
                    System.out.println("    • Total shortages detected: " + shortages.size());                    System.out.println("    • Products with shortages:");
                    for (Map.Entry<Integer, Integer> entry : shortages.entrySet()) {
                        System.out.println("      - Catalog #" + entry.getKey() + ": needs " + entry.getValue() + " units");
                    }
                    newShortagesFound = true;
                }
            }

            if (!newShortagesFound) {
                System.out.println("ℹ️ No current shortages detected for branch " + current_branch_id + ". All products are adequately stocked.");
            }

        } catch (Exception e) {
            System.err.println("❌ Error placing shortage-based supplier order: " + e.getMessage());
            e.printStackTrace();
        }
    }



    private void runPart1InventoryMenu() {
        int choice = 0;
        while (choice != 14) {
            printPart1Menu();
            try {
                choice = Integer.parseInt(scan.nextLine().trim());
                handleChoice(choice);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private void printPart1Menu() {
        System.out.println("""
    Inventory Module Menu:
    1. Show item details
    2. Add item(s) to inventory (new or existing product)
    3. Remove an item
    4. Show the purchase prices of a product
    5. Update the cost price of a product (before discounts)
    6. Mark an item as defective
    7. Generate inventory report
    8. Generate a defective and expired items report
    9. Apply supplier/store discount to a product group
    10. Show product quantity in warehouse and store
    11. Generate a shortage inventory report
    12. Update product supply days and demand level
    13. Update item storage location
    14. Exit
    """);
    }

    private void handleChoice(int choice) {
        switch (choice) {
            case 1 -> showItemDetails();
            case 2 -> addItemsToInventory();
            case 3 -> removeItem();
            case 4 -> showPurchasePrices();
            case 5 -> updateCostPrice();
            case 6 -> markAsDefect();
            case 7 -> generateInventoryReport();
            case 8 -> generateDefectAndExpiredReport();
            case 9 -> applyDiscount();
            case 10 -> showProductQuantities();
            case 11 -> generateShortageInventoryReport();
            case 12 -> updateProductSupplyAndDemand();
            case 13 -> updateItemStorageLocation();
            case 14 -> System.out.println("Exiting the Inventory menu.");
            default -> System.out.println("Invalid option. Please try again.");
        }
    }



    private void showItemDetails() {
        System.out.print("Enter item ID: ");
        int item_Id;
        try {
            item_Id = Integer.parseInt(scan.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid numeric Item ID.");
            return;
        }

        String details = inventory_controller.getItemController().showItemDetails(item_Id, current_branch_id);

        if (details != null && !details.trim().isEmpty()
                && !details.contains("not found")
                && !details.contains("does not exist")) {
            System.out.println("\n----------- Item Details -----------");
            System.out.println(details);
        } else {
            System.out.println("No item with ID " + item_Id + " was found in Branch " + current_branch_id + ".");
        }
    }

    private void addItemsToInventory() {
        System.out.println("Enter Product Catalog Number:");
        int catalogNumber = Integer.parseInt(scan.nextLine());

        boolean productExists = !inventory_controller.getProductController().isUnknownCatalogNumber(catalogNumber);

        if (productExists) {
            System.out.println("Product found in the system.");
            System.out.print("How many units would you like to add? ");
            int quantityToAdd = Integer.parseInt(scan.nextLine());

            System.out.print("Enter storage location for all units (Warehouse or InteriorStore): ");
            String storageLocation = scan.nextLine().trim();

            System.out.print("Enter expiry date for all units (format: dd/MM/yyyy): ");
            String expiryDate = scan.nextLine().trim();

            int nextItemId = inventory_controller.getItemController().getNextAvailableItemId();

            for (int i = 0; i < quantityToAdd; i++) {
                int currentItemId = nextItemId + i;
                inventory_controller.getItemController().addItem(
                        currentItemId,
                        current_branch_id,
                        catalogNumber,
                        storageLocation,
                        expiryDate
                );
            }

            System.out.println("\n-----------------------------------------");
            System.out.println(quantityToAdd + " items successfully added for Product Catalog Number " + catalogNumber + ".");
            System.out.println("-----------------------------------------\n");
        } else {
            System.out.println("Product not found. Please add the product first.");
        }
    }

    private void removeItem() {
        System.out.print("Enter item ID: ");
        int item_Id = Integer.parseInt(scan.nextLine());

        if (!inventory_controller.getItemController().itemExistsInBranch(item_Id, current_branch_id)) {
            System.out.println("Item does not exist in Branch " + current_branch_id + ".");
            return;
        }                // Get item details before removal
                ItemDTO item = inventory_controller.getItemController().getItem(item_Id, current_branch_id);
        if (item == null) {
            System.out.println("❌ Failed to retrieve item details before removal.");
            return;
        }

        int catalog_number = item.getCatalogNumber();
        String product_name = inventory_controller.getItemController().getItemName(item_Id, current_branch_id);
        double sale_price = 0.0;

        try {
            ProductDTO productDTO = inventory_controller.getProductRepository().getProductByCatalogNumber(catalog_number);
            if (productDTO != null) {
                sale_price = productDTO.getSalePriceAfterStoreDiscount();
            }
        } catch (Exception e) {
            System.out.println("❌ Failed to retrieve product details: " + e.getMessage());
        }

        System.out.println("What is the reason for removing the item?");
        System.out.println("(1) Purchase\n(2) Defect");
        int reason = Integer.parseInt(scan.nextLine());

        if (reason == 1) {
            inventory_controller.getItemController().removeItemByPurchase(item_Id, current_branch_id);
        } else if (reason == 2) {
            inventory_controller.getItemController().removeItemByDefect(item_Id, current_branch_id);
        } else {
            System.out.println("Invalid choice. Please enter 1 or 2.");
            return;
        }

        boolean alert = inventory_controller.getReportController().shouldTriggerAlertAfterRemoval(current_branch_id, catalog_number);

        System.out.println("\n-----------------------------------------");
        if (reason == 1) {
            System.out.println("The item \"" + product_name + "\" has been marked as purchased and removed from Branch " + current_branch_id + ".");
            System.out.printf("The item was sold for: %.2f ₪ (after store discount)\n", sale_price);
        } else {
            System.out.println("The item \"" + product_name + "\" has been marked as defective and removed from Branch " + current_branch_id + ".");
        }

        if (alert) {
            System.out.println("ALERT: The product \"" + product_name + "\" in Branch " + current_branch_id + " has reached a critical amount!");
            System.out.println("Please consider reordering.");
        }
        System.out.println("-----------------------------------------");
    }



    /**
     * Displays the sale prices for purchased items of a specific product in the current branch.
     * This method prompts the user to input a product catalog number, retrieves the purchase
     * prices from the ProductController, and prints them.
     * If the product is not found or there are no purchases for it in the current branch,
     * a clear and specific message is displayed instead of a system or abstract error.
     */
    private void showPurchasePrices() {
        System.out.print("Enter Product Catalog Number: ");

        int catalog;
        try {
            // Read the catalog number input
            catalog = Integer.parseInt(scan.nextLine().trim());
        } catch (NumberFormatException e) {
            // Handle invalid input
            System.out.println("Invalid input. Please enter a valid numeric Product Catalog Number.");
            return;
        }

        // Retrieve the purchase prices details
        String result = inventory_controller.getProductController().showProductPurchasesPrices(catalog, current_branch_id);

        // Check if the product does not exist or there are no purchases
        if (result != null && !result.trim().isEmpty()) {
            if (result.contains("No purchased items found")) {
                System.out.println("No purchases found for Product Catalog Number " + catalog + " in Branch " + current_branch_id + ".");
            } else if (result.contains("does not exist") || result.contains("Invalid Product Catalog Number")) {
                System.out.println("The product with Catalog Number " + catalog + " does not exist in Branch " + current_branch_id + ".");
            } else {
                // Valid purchase details found — display them
                System.out.println("\n----------- Purchase Prices -----------");
                System.out.println(result);
            }
        } else {
            System.out.println("The product with Catalog Number " + catalog + " does not exist in Branch " + current_branch_id + ".");
        }
    }


    /**
     * Updates the cost price (before discounts) of a product identified by its catalog number.
     * This method prompts the user to enter the product's catalog number and the new cost price.
     * If the catalog number does not exist in the inventory, a clear and specific message is displayed.
     * Upon success, the cost price is updated for the product across all branches.
     */
    private void updateCostPrice() {
        System.out.println("------------------------------------------------------");
        System.out.println("Note: Updating the cost price will apply to ALL branches.");
        System.out.println("------------------------------------------------------");

        System.out.print("Enter Product Catalog Number: ");
        int catalog_number;

        try {
            // Read the catalog number input
            catalog_number = Integer.parseInt(scan.nextLine().trim());
        } catch (NumberFormatException e) {
            // Handle invalid input
            System.out.println("Invalid input. Product Catalog Number must be a numeric value.");
            return;
        }

        // Check if the product exists before continuing
        if (inventory_controller.getProductController().isUnknownCatalogNumber(catalog_number)) {
            System.out.println("The product with Catalog Number " + catalog_number + " does not exist in the system.");
            return;
        }

        System.out.print("Enter new cost price: ");
        double new_price;

        try {
            // Read the new cost price
            new_price = Double.parseDouble(scan.nextLine().trim());
            if (new_price < 0) {
                System.out.println("Cost price cannot be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            // Handle invalid price input
            System.out.println("Invalid input. Price must be a numeric value.");
            return;
        }

        try {
            // Attempt to update the cost price
            boolean success = inventory_controller.getProductController().updateCostPriceByCatalogNumber(catalog_number, new_price);

            System.out.println("\n------------------------------------------------------");
            if (success) {
                // Successfully updated
                System.out.println("Cost price for Product Catalog Number " + catalog_number + " has been updated to " + new_price + " ₪.");
                System.out.println("The new cost price has been applied across all branches.");
            } else {
                // Shouldn't normally reach here because we already checked existence
                System.out.println("Failed to update cost price. Please verify the catalog number and try again.");
            }
            System.out.println("------------------------------------------------------");

        } catch (SQLException e) {
            System.err.println("❌ Database error while updating product cost: " + e.getMessage());
            System.out.println("Please try again later or contact support.");
        }
    }



    private void markAsDefect() {
        System.out.print("Enter item ID: ");
        int itemId;

        try {
            itemId = Integer.parseInt(scan.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Item ID must be a numeric value.");
            return;
        }

        // Check if the item exists in the current branch
        if (!inventory_controller.getItemController().itemExistsInBranch(itemId, current_branch_id)) {
            System.out.println("Item with ID " + itemId + " was not found in Branch " + current_branch_id + ".");
            return;
        }

        // Proceed to mark as defective
        boolean success = inventory_controller.getItemController().markItemAsDefective(itemId, current_branch_id);

        if (success) {
            System.out.println("Item with ID " + itemId + " in Branch " + current_branch_id + " has been marked as defective.");
        } else {
            System.out.println("Failed to mark item as defective. Please try again.");
        }
    }



    private void generateInventoryReport() {
        System.out.println("\n===================================================");
        System.out.println("Inventory Report Options (Branch " + current_branch_id + ")");
        System.out.println("===================================================");

        System.out.println("Choose report type:");
        System.out.println("1. By Categories");
        System.out.println("2. By Sub-Categories");
        System.out.println("3. By Catalog Number");
        System.out.print("Enter your choice (1-3): ");

        int reportChoice;
        try {
            reportChoice = Integer.parseInt(scan.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Returning to menu.");
            return;
        }

        System.out.println("\nFilter by Size:");
        System.out.println("1. Small");
        System.out.println("2. Medium");
        System.out.println("3. Big");
        System.out.println("4. All Sizes");
        System.out.print("Enter your size filter choice (1-4): ");

        int sizeChoice;
        try {
            sizeChoice = Integer.parseInt(scan.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Returning to menu.");
            return;
        }

        List<Integer> sizeFilters = getSizeFilters(sizeChoice);
        if (sizeFilters.isEmpty()) {
            System.out.println("Invalid size selection. Returning to menu.");
            return;
        }

        switch (reportChoice) {
            case 1 -> generateReportByCategories(sizeFilters);
            case 2 -> generateReportBySubCategories(sizeFilters); // You can implement this later
            case 3 -> generateReportByCatalogNumber(sizeFilters); // You can implement this later
            default -> System.out.println("Invalid report choice. Returning to menu.");
        }
    }


    private void generateReportByCategories(List<Integer> sizeFilters) {
        System.out.println("\n----------- Inventory Report by Categories -----------");

        System.out.print("Enter categories separated by commas: ");
        String[] categories = Arrays.stream(scan.nextLine().split(","))
                .map(String::trim)
                .toArray(String[]::new);

        String report = inventory_controller.getReportController()
                .inventoryReportByCategories(categories, current_branch_id, sizeFilters);

        if (report != null && !report.trim().isEmpty() && !report.contains("does not exist") && !report.contains("No valid categories")) {
            System.out.println("\n----------- Report Output -----------");
            System.out.println(report);
        } else {
            System.out.println("No matching categories found in Branch " + current_branch_id + ".");
        }
    }


    private void generateReportBySubCategories(List<Integer> sizeFilters) {
        System.out.println("\n----------- Inventory Report by Sub-Categories -----------");

        System.out.print("Enter sub-categories separated by commas: ");
        String[] subCategories = Arrays.stream(scan.nextLine().split(","))
                .map(String::trim)
                .toArray(String[]::new);

        String report = inventory_controller.getReportController()
                .inventoryReportBySubCategories(subCategories, current_branch_id, sizeFilters);

        if (report != null && !report.trim().isEmpty() && !report.contains("does not exist") && !report.contains("No valid sub-categories")) {
            System.out.println("\n----------- Report Output -----------");
            System.out.println(report);
        } else {
            System.out.println("No matching sub-categories found in Branch " + current_branch_id + ".");
        }
    }


    private void generateReportByCatalogNumber(List<Integer> sizeFilters) {
        System.out.println("\n----------- Inventory Report by Catalog Numbers -----------");

        System.out.print("Enter catalog numbers separated by commas: ");
        String[] catalogNumbers = Arrays.stream(scan.nextLine().split(","))
                .map(String::trim)
                .toArray(String[]::new);

        String report = inventory_controller.getReportController()
                .inventoryReportByCatalogNumbers(catalogNumbers, current_branch_id, sizeFilters);

        if (report != null && !report.trim().isEmpty() && !report.contains("does not exist") && !report.contains("No valid catalog numbers")) {
            System.out.println("\n----------- Report Output -----------");
            System.out.println(report);
        } else {
            System.out.println("No matching catalog numbers found in Branch " + current_branch_id + ".");
        }
    }


    private List<Integer> getSizeFilters(int sizeChoice) {
        return switch (sizeChoice) {
            case 1 -> List.of(1); // Small
            case 2 -> List.of(2); // Medium
            case 3 -> List.of(3); // Big
            case 4 -> List.of(1, 2, 3); // All Sizes
            default -> List.of();
        };
    }

    /**
     * Generates and displays a report of defective and expired items for the current branch.
     * This method retrieves the defective and expired items report from the ReportController.
     * If no defective or expired items are found, a clear and user-friendly message is displayed
     * instead of raw or abstract system error messages.
     */
    private void generateDefectAndExpiredReport() {
        // Retrieve the defect and expired report
        String report = inventory_controller.getReportController().defectAndExpiredReport(current_branch_id);

        // Check if the report contains meaningful data
        if (report != null && !report.trim().isEmpty()
                && !(report.contains("No defective items") && report.contains("No expired items"))) {
            // Valid defect/expired report found — display it
            System.out.println("\n----------- Defect and Expired Items Report -----------");
            System.out.println(report);
        } else {
            // No defective or expired items — display a friendly message
            System.out.println("No defective or expired items found in Branch " + current_branch_id + ".");
        }
    }





    private void applyDiscount() {
        System.out.println("Apply Discount");
        System.out.println("Note: The discount you apply will affect ALL branches across the network.");
        System.out.println("Choose group to apply discount on:");
        System.out.println("(1) Category\n(2) Sub-Category\n(3) Product Size\n(4) Product Catalog Number");

        int type;
        try {
            type = Integer.parseInt(scan.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid group choice. Returning to menu.");
            return;
        }

        String category = null, sub_category = null;
        int catalog = -1;
        int size = -1;

        if (type == 1) {
            System.out.print("Enter category: ");
            category = scan.nextLine().trim();
            if (!inventory_controller.getProductController().hasCategory(category)) {
                System.out.println("This category does not exist. Returning to menu.");
                return;
            }
        } else if (type == 2) {
            System.out.print("Enter sub-category: ");
            sub_category = scan.nextLine().trim();
            if (!inventory_controller.getProductController().hasSubCategory(sub_category)) {
                System.out.println("This sub-category does not exist. Returning to menu.");
                return;
            }
        } else if (type == 3) {
            System.out.print("Enter product size (integer): ");
            try {
                size = Integer.parseInt(scan.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid product size. Returning to menu.");
                return;
            }        // Convert size to final for use in lambda
        final int finalSize = size;
            boolean exists = inventory_controller.getProductController()
                    .getAllProducts()
                    .stream()
                    .anyMatch(p -> p.getSize() == finalSize);

            if (!exists) {
                System.out.println("No products found with size " + size + ". Returning to menu.");
                return;
            }

        } else if (type == 4) {
            System.out.print("Enter Product Catalog Number: ");
            try {
                catalog = Integer.parseInt(scan.nextLine());
                if (!inventory_controller.getProductController().productExists(catalog)) {
                    System.out.println("Product Catalog Number not found. Returning to menu.");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid Product Catalog Number. Returning to menu.");
                return;
            }
        } else {
            System.out.println("Invalid group choice. Returning to menu.");
            return;
        }

        System.out.println("Choose discount type:\n(1) Supplier Discount\n(2) Store Discount");
        int discount_type_input;
        try {
            discount_type_input = Integer.parseInt(scan.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid discount type. Returning to menu.");
            return;
        }

        if (discount_type_input != 1 && discount_type_input != 2) {
            System.out.println("Invalid discount type. Returning to menu.");
            return;
        }

        boolean is_supplier = discount_type_input == 1;

        double rate = -1;
        while (rate < 0 || rate > 100) {
            System.out.print("Enter discount rate (%): ");
            try {
                rate = Double.parseDouble(scan.nextLine());
                if (rate < 0 || rate > 100) {
                    System.out.println("Discount must be between 0 and 100. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a numeric value.");
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.yyyy");
        LocalDate start = LocalDate.now();
        LocalDate end;

        while (true) {
            try {
                System.out.print("Enter end date (format: day.month.year, e.g., 30.6.2026): ");
                String endInput = scan.nextLine().replace("-", ".").replace("/", ".");
                end = LocalDate.parse(endInput, formatter);
                if (end.isBefore(start)) {
                    System.out.println("End date must be after today's date (" + start + "). Please try again.");
                } else {
                    break;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please enter the date in the format: day.month.year (e.g., 30.5.2025)");
            }
        }

        Discount discount = new Discount(rate, start, end);

        boolean success;
        if (type == 1) {
            success = is_supplier
                    ? inventory_controller.getDiscountController().setSupplierDiscountForCategory(category, discount)
                    : inventory_controller.getDiscountController().setStoreDiscountForCategory(category, discount);
        } else if (type == 2) {
            success = is_supplier
                    ? inventory_controller.getDiscountController().setSupplierDiscountForSubCategory(sub_category, discount)
                    : inventory_controller.getDiscountController().setStoreDiscountForSubCategory(sub_category, discount);
        } else if (type == 3) {
            success = is_supplier
                    ? inventory_controller.getDiscountController().setSupplierDiscountForSize(size, discount)
                    : inventory_controller.getDiscountController().setStoreDiscountForSize(size, discount);
        } else {
            success = is_supplier
                    ? inventory_controller.getDiscountController().setSupplierDiscountForCatalogNumber(catalog, discount)
                    : inventory_controller.getDiscountController().setStoreDiscountForCatalogNumber(catalog, discount);
        }

        System.out.println("\n-----------------------------------------");
        if (success) {
            String target = switch (type) {
                case 1 -> category;
                case 2 -> sub_category;
                case 3 -> "Size " + size;
                default -> "Catalog #" + catalog;
            };
            String discountType = is_supplier ? "Supplier" : "Store";
            System.out.println(discountType + " discount of " + rate + "% was successfully applied to: " + target);
            System.out.println("Active from " + start + " to " + end);
            System.out.println("Note: This discount has been applied to ALL matching products in the current branch.");
        } else {
            System.out.println("Failed to apply discount. Check if the group exists or if the discount is valid.");
        }
        System.out.println("-----------------------------------------");
    }



    private void showProductQuantities() {
        System.out.print("Enter Product Catalog Number: ");

        int catalog;
        try {
            catalog = Integer.parseInt(scan.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid numeric Product Catalog Number.");
            return;
        }

        // ✅ Update quantities in the database before displaying
        try {
            inventory_controller.getProductController().updateAllProductQuantities();
        } catch (Exception e) {
            System.err.println("❌ Failed to update product quantities in DB: " + e.getMessage());
            return;
        }

        // ⬇ Continue as usual
        String result = inventory_controller.getProductController().showProductQuantities(catalog, current_branch_id);

        if (result != null && !result.trim().isEmpty()) {
            if (result.contains("does not exist") || result.contains("No items found") || result.contains("Invalid Product Catalog Number")) {
                System.out.println("The product with Catalog Number " + catalog + " does not exist in Branch " + current_branch_id + ".");
            } else {
                System.out.println("\n-----------------------------");
                System.out.println("Product Quantities for Branch " + current_branch_id + ":");
                System.out.println(result);
                System.out.println("-----------------------------\n");
            }
        } else {
            System.out.println("The product with Catalog Number " + catalog + " does not exist in Branch " + current_branch_id + ".");
        }
    }


    /**
     * Generates and displays a shortage inventory report for the current branch.
     *
     * <p>
     * This method calls the {@code generateShortageInventoryReport} function in the
     * {@code ReportController} to retrieve a report listing products whose current stock
     * is below the minimum required quantity.
     * The minimum required quantity for each product is calculated automatically based on
     * the average of its demand level and supply time (rounded and floored to at least 1).
     *
     * <p>
     * If the report includes valid shortage data, it is printed to the console with a header.
     * If there are no products requiring reordering or the branch is invalid, a clear
     * message is shown instead.
     *
     * <p><b>Note:</b> This report is read-only and does not trigger any automatic supplier orders.
     */
    private void generateShortageInventoryReport() {
        // Retrieve the reorder alert report
        String report = inventory_controller.getReportController().generateShortageInventoryReport(current_branch_id);

        // Check if the report contains a real shortage alert
        String noShortageMessage = "All the products in Branch " + current_branch_id + " are above their minimum required amount.";
        String branchNotFoundMessage = "Branch " + current_branch_id + " not found.";

        if (report != null && !report.trim().isEmpty()
                && !report.equals(noShortageMessage)
                && !report.equals(branchNotFoundMessage)) {

            // Valid reorder report found — display it
            System.out.println("\n----------- Reorder Alert Report -----------");
            System.out.println(report);
        } else {
            // No products require reordering — display a friendly message
            System.out.println("No products currently require reordering in Branch " + current_branch_id + ".");
        }
    }



    /**
     * Updates the supply days in the week and/or demand level of a specific product across all branches.
     *
     * <p>
     * This method allows the user to:
     * <ul>
     *     <li>Enter the product's catalog number.</li>
     *     <li>Choose which details to update:
     *         <ul>
     *             <li>(1) Supply Days in the Week</li>
     *             <li>(2) Demand Level</li>
     *             <li>(3) Both Supply Days and Demand Level</li>
     *         </ul>
     *     </li>
     *     <li>Enter new values for the selected fields.</li>
     * </ul>
     *
     * If the product exists, the updated values are saved and a success message is displayed.
     * If the product does not exist, an error message is shown.
     */
    private void updateProductSupplyAndDemand() {
        try {
            System.out.println("Enter Product Catalog Number:");
            int catalog = Integer.parseInt(scan.nextLine());

            System.out.println("What would you like to update?");
            System.out.println("(1) Supply Days in the Week\n(2) Demand Level\n(3) Both");
            int option = Integer.parseInt(scan.nextLine());

            String supplyDays = null;
            Integer demand = null;

            if (option == 1 || option == 3) {
                System.out.println("Enter supply days (e.g., Sunday,Wednesday,Friday):");
                supplyDays = scan.nextLine().trim();
            }

            if (option == 2 || option == 3) {
                System.out.println("Enter new demand level (1–5):");
                demand = Integer.parseInt(scan.nextLine());
            }

            // Attempt to update the product supply and/or demand details
            boolean updated = inventory_controller.getProductController().updateProductSupplyDetails(catalog, supplyDays, demand);

            if (updated) {
                System.out.println("\n-----------------------------------------");
                System.out.println("The supply details for the product with Catalog Number " + catalog + " have been updated across all branches.");
                if (supplyDays != null) {
                    System.out.println("New supply days: " + supplyDays);
                }
                if (demand != null) {
                    System.out.println("New demand level: " + demand);
                }
                System.out.println("-----------------------------------------\n");
            } else {
                System.out.println("Product with Catalog Number " + catalog + " not found in inventory.");
            }

        } catch (SQLException e) {
            System.err.println("❌ Database error while updating product supply/demand: " + e.getMessage());
            System.out.println("Please try again or contact support.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter numeric values where expected.");
        }
    }


    /**
     * Updates the storage location and/or section of a specific item within the current branch.
     *
     * <p>
     * This method allows the user to:
     * <ul>
     *     <li>Enter the item's ID.</li>
     *     <li>Choose what to update:
     *         <ul>
     *             <li>(1) Storage location (Warehouse or InteriorStore)</li>
     *             <li>(2) Section within the storage location (e.g., A1, B2)</li>
     *             <li>(3) Both location and section</li>
     *         </ul>
     *     </li>
     *     <li>Input new values for the selected fields.</li>
     * </ul>
     *
     * If the item exists, the changes are applied and a success message is displayed.
     * If the item does not exist in the current branch, an error message is shown.
     */
    private void updateItemStorageLocation() {
        // Prompt the user to enter the item ID
        System.out.println("Enter item ID:");
        int id = Integer.parseInt(scan.nextLine());

        // Ask the user what they want to change: location, section, or both
        System.out.println("What would you like to change?\n(1) Location\n(2) Section\n(3) Both");
        int choice = Integer.parseInt(scan.nextLine());

        // Initialize variables to hold new location and/or section values
        String location = null, section = null;

        // If the user wants to change the location (or both location and section)
        if (choice == 1 || choice == 3) {
            System.out.println("Enter new location (Warehouse or InteriorStore):");
            location = scan.nextLine();
        }

        // If the user wants to change the section (or both location and section)
        if (choice == 2 || choice == 3) {
            System.out.println("Enter new section (e.g., A1, B2, etc.):");
            section = scan.nextLine();
        }

        // Attempt to update the item's location and/or section in the selected branch
        boolean updated = inventory_controller.getItemController().updateItemLocation(id, current_branch_id, location, section);

        // Display the result of the update
        if (updated) {
            System.out.println("\n-----------------------------------------");
            System.out.println("Item with ID " + id + " updated successfully in Branch " + current_branch_id + ".");
            if (location != null) {
                System.out.println("New location: " + location);
            }
            if (section != null) {
                System.out.println("New section: " + section);
            }
            System.out.println("-----------------------------------------\n");
        } else {
            // Item not found in the current branch
            System.out.println("Item with ID " + id + " was not found in Branch " + current_branch_id + ".");
        }
    }    private void viewAllPeriodicOrders() {
        try {
            List<PeriodicOrderDTO> orders = inventory_controller.getPeriodicOrderRepository()
                .getAllPeriodicOrders();

            // Filter for current branch            
            orders = orders.stream()
                .filter(order -> order.getBranchId() == current_branch_id)
                .collect(Collectors.toList());
            
            if (orders.isEmpty()) {
                System.out.println("\nNo periodic orders found for Branch #" + current_branch_id);
                return;
            }

            System.out.println("\n----------- All Periodic Orders -----------");
            for (PeriodicOrderDTO order : orders) {
                ProductDTO product = inventory_controller.getProductRepository().getProductByCatalogNumber(order.getProductCatalogNumber());
                String productName = product != null ? product.getProductName() : "Unknown Product";

                System.out.println("Periodic Order #" + order.getOrderId());
                System.out.println("Product: " + productName + " (Catalog #" + order.getProductCatalogNumber() + ")");
                System.out.println("Supply Days: " + order.getDaysInTheWeek());
                System.out.println("Quantity: " + order.getQuantity());
                System.out.println("Order Date: " + order.getOrderDate());
                System.out.println("Supplier: " + order.getSupplierName() + " (ID: " + order.getSupplierId() + ")");
                System.out.println("----------------------------------------");
            }
        } catch (SQLException e) {
            System.out.println("❌ Failed to retrieve periodic orders: " + e.getMessage());
        }
    }

    private void viewPendingShortageOrders() {
        try {
            List<ShortageOrderDTO> orders = inventory_controller.getShortageOrderRepository()
                .getAll();

            // Filter for current branch and pending status
            orders = orders.stream()
                .filter(order -> order.getBranchId() == current_branch_id && 
                               order.getStatus().equals("PENDING"))
                .collect(Collectors.toList());
            
            if (orders.isEmpty()) {
                System.out.println("\nNo pending shortage orders found for Branch #" + current_branch_id);
                return;
            }

            System.out.println("\n----------- Pending Shortage Orders -----------");
            for (ShortageOrderDTO order : orders) {
                ProductDTO product = inventory_controller.getProductRepository().getProductByCatalogNumber(order.getProductCatalogNumber());
                String productName = product != null ? product.getProductName() : "Unknown Product";

                System.out.println("Shortage Order #" + order.getOrderId());
                System.out.println("Product: " + productName + " (Catalog #" + order.getProductCatalogNumber() + ")");
                System.out.println("Quantity: " + order.getQuantity());
                System.out.println("Current Stock: " + order.getCurrentStock());
                System.out.println("Order Date: " + order.getOrderDate());
                System.out.println("Supplier: " + order.getSupplierName() + " (ID: " + order.getSupplierId() + ")");
                System.out.println("Status: " + order.getStatus());
                System.out.println("----------------------------------------");
            }
        } catch (SQLException e) {
            System.out.println("❌ Failed to retrieve shortage orders: " + e.getMessage());
        }
    }
}
