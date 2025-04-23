package Presentation;

import Domain.Discount;
import Domain.InventoryController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Controller class for managing the user interface menu.
 * Handles user input, invokes the appropriate system functionality,
 * and connects to the {@link InventoryController} for backend operations.
 */
public class MenuController {
    private final Scanner scan; // Scanner for reading user input
    private final InventoryController inventory_controller; // Reference to the inventory system logic

    /**
     * Constructs a MenuController and links it to the inventory controller.
     *
     * @param inventory_controller The main inventory controller used to perform system actions.
     */
    public MenuController(InventoryController inventory_controller) {
        this.scan = new Scanner(System.in);
        this.inventory_controller = inventory_controller;
    }

    /**
     * Launches the main menu loop.
     * Continuously prompts the user for input, processes their selection,
     * and delegates the requested operation to the appropriate controller.
     * Loop ends when the user selects the exit option (option 14).
     */
    public void runMenu() {
        int choice = 0;

        while (choice != 14) {
            printMenu();
            try {
                choice = Integer.parseInt(scan.nextLine().trim());
                handleChoice(choice);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }

        System.out.println("Thank you! Have a nice day :)");
    }

    /**
     * Prints the main menu options to the console.
     * The menu includes all available operations the user can perform,
     * such as viewing item details, adding/removing items, generating reports,
     * applying discounts, and exiting the system.
     */
    private void printMenu() {
        System.out.println("""
                Menu:
                1. Show item details
                2. Add a new item
                3. Remove an item
                4. Show the purchase prices of a product
                5. Update the cost price of a product (before discounts)
                6. Mark an item as defective
                7. Generate inventory report by categories
                8. Generate a defective and expired items report
                9. Apply supplier/store discount to a product group
                10. Show product quantity in warehouse and store
                11. Show low-stock products that need reordering
                12. Change product supply details
                13. Change item location details
                14. Exit
                """);
    }

    /**
     * Handles the user’s menu selection based on the given choice.
     * Executes the appropriate function depending on the selected option number.
     *
     * @param choice The numeric input corresponding to a menu option.
     */
    private void handleChoice(int choice) {
        switch (choice) {
            case 1 -> showItemDetails();
            case 2 -> addNewItem();
            case 3 -> removeItem();
            case 4 -> showPurchasePrices();
            case 5 -> updateCostPrice();
            case 6 -> markAsDefect();
            case 7 -> generateInventoryReport();
            case 8 -> System.out.println(inventory_controller.getReportController().defectAndExpiredReport());
            case 9 -> applyDiscount();
            case 10 -> showQuantities();
            case 11 -> System.out.println(inventory_controller.getReportController().generateReorderAlertReport());
            case 12 -> changeSupplyDetails();
            case 13 -> changeItemLocation();
            case 14 -> {} // exit
            default -> System.out.println("Invalid option. Please try again.");
        }
    }

    /**
     * Prompts the user to enter an item ID and displays the detailed information
     * of the corresponding item.
     * Delegates the display logic to the {@code showItemDetails} method
     * in the {@code ItemController}.
     */
    private void showItemDetails() {
        System.out.println("Enter item ID: ");
        int item_Id = Integer.parseInt(scan.nextLine());
        System.out.println(inventory_controller.getItemController().showItemDetails(item_Id));
    }

    /**
     * Prompts the user to enter details for a new item and adds it to the inventory system.
     * The details include product metadata, pricing, demand, supply time, and discounts.
     * If the item ID already exists in the system, the method will notify the user
     * and return to the main menu.
     * All input fields are collected and formatted into a CSV-style string, which is then passed
     * to the {@code addItem} method of the {@code ItemController}.
     */
    private void addNewItem() {
        System.out.println("Please enter the following details for the new item:");

        System.out.print("Item ID (unique number): ");
        int item_Id = Integer.parseInt(scan.nextLine());

        if (inventory_controller.getItemController().itemExists(item_Id)) {
            System.out.println("An item with ID " + item_Id + " already exists in the system.");
            System.out.println("Returning to the main menu.");
            return;
        }

        System.out.print("Product Name: ");
        String product_name = scan.nextLine();

        System.out.print("Expiring Date (format: dd.mm.yyyy): ");
        String expiring_date = scan.nextLine();

        System.out.print("Location (Warehouse/interiorStore): ");
        String location = scan.nextLine();

        System.out.print("Section in store (e.g., A1, B2): ");
        String section = scan.nextLine();

        System.out.print("Product Catalog Number (unique per product): ");
        int catalog_number = Integer.parseInt(scan.nextLine());

        System.out.print("Category: ");
        String category = scan.nextLine();

        System.out.print("Sub-Category: ");
        String sub_category = scan.nextLine();

        System.out.print("Item Size (1 - Small, 2 - Medium, 3 - Big): ");
        int size = Integer.parseInt(scan.nextLine());

        System.out.print("Cost Price Before Supplier Discount: ");
        double cost_price_before = Double.parseDouble(scan.nextLine());

        System.out.print("Product Demand Level (1-5): ");
        int demand = Integer.parseInt(scan.nextLine());

        System.out.print("Supply Time (in days): ");
        int supply_time = Integer.parseInt(scan.nextLine());

        System.out.print("Manufacturer: ");
        String manufacturer = scan.nextLine();

        System.out.print("Supplier Discount (%): ");
        int supplier_discount = Integer.parseInt(scan.nextLine());

        System.out.print("Store Discount (%): ");
        int store_discount = Integer.parseInt(scan.nextLine());

        // Compose CSV format string for internal use
        String csvInput = item_Id + "," + product_name + "," + expiring_date + "," + location + "," + section + ","
                + catalog_number + "," + category + "," + sub_category + "," + size + "," + cost_price_before + ","
                + demand + "," + supply_time + "," + manufacturer + "," + supplier_discount + "," + store_discount;

        boolean success = inventory_controller.getItemController().addItem(csvInput);

        System.out.println("\n-----------------------------------------");
        if (success) {
            System.out.println("Item added successfully.");
            System.out.println("Product Name: " + product_name);
            System.out.println("Catalog Number: " + catalog_number);
            System.out.println("Category: " + category + ", Sub-Category: " + sub_category);
        } else {
            System.out.println("Failed to add item. Please verify all details were entered correctly.");
        }
        System.out.println("-----------------------------------------");
    }

    /**
     * Removes an item from the inventory based on the provided item ID.
     * The user is prompted to specify the reason for removal:
     * <ul>
     *   <li>1 – Purchase: the item is removed and marked as sold, and the sale price is displayed.</li>
     *   <li>2 – Defect: the item is removed and marked as defective.</li>
     * </ul>
     * If the removal causes the associated product to fall below its minimum threshold,
     * a reorder alert is displayed.
     * If the item does not exist or the reason is invalid, a corresponding message is printed.
     */
    private void removeItem() {
        System.out.print("Enter item ID: ");
        int item_Id = Integer.parseInt(scan.nextLine());

        if (!inventory_controller.getItemController().itemExists(item_Id)) {
            System.out.println("Item does not exist in the inventory.");
            return;
        }

        System.out.println("What is the reason for removing the item?");
        System.out.println("(1) Purchase\n(2) Defect");
        int reason = Integer.parseInt(scan.nextLine());

        if (reason == 1) {
            boolean alert = inventory_controller.getItemController().checkReorderAlert(item_Id);
            String product_name = inventory_controller.getItemController().getItemName(item_Id);
            double sale_price = inventory_controller.getItemController().getSalePriceAfterDiscount(item_Id);

            inventory_controller.getItemController().removeItemByPurchase(item_Id);

            System.out.println("\n-----------------------------------------");
            System.out.println("The item \"" + product_name + "\" has been marked as purchased and removed.");
            System.out.printf("The item was sold for: %.2f ₪ (after store discount)\n", sale_price);
            if (alert) {
                System.out.println("ALERT: The product \"" + product_name + "\" has reached a critical amount!");
                System.out.println("Please consider reordering.");
            }
            System.out.println("-----------------------------------------");

        } else if (reason == 2) {
            boolean alert = inventory_controller.getItemController().checkReorderAlert(item_Id);
            String product_name = inventory_controller.getItemController().getItemName(item_Id);

            inventory_controller.getItemController().removeItemByDefect(item_Id);

            System.out.println("\n-----------------------------------------");
            System.out.println("The item \"" + product_name + "\" has been marked as defective and removed.");
            if (alert) {
                System.out.println("ALERT: The product \"" + product_name + "\" has reached a critical amount!");
                System.out.println("Please consider reordering.");
            }
            System.out.println("-----------------------------------------");

        } else {
            System.out.println("Invalid choice. Please enter 1 or 2.");
        }
    }


    /**
     * Prompts the user to enter a product's catalog number and displays
     * the sale prices (after store discounts) of all items that were purchased
     * under that product.
     * Delegates the logic to {@code showProductPurchasesPrices} in {@code ProductController}.
     */
    private void showPurchasePrices() {
        System.out.println("Enter Product Catalog Number: ");
        int catalog = Integer.parseInt(scan.nextLine());
        System.out.println(inventory_controller.getProductController().showProductPurchasesPrices(catalog));
    }

    /**
     * Updates the cost price (before discounts) of a product identified by its catalog number.
     * The user is prompted to enter:
     * <ul>
     *   <li>The product's catalog number</li>
     *   <li>The new cost price</li>
     * </ul>
     * The method performs input validation to ensure:
     * <ul>
     *   <li>The catalog number is numeric and exists in the system</li>
     *   <li>The new cost price is a non-negative number</li>
     * </ul>
     *
     * Upon success, the cost price is updated and the change is confirmed to the user.
     * If any validation fails or the update fails, appropriate error messages are shown.
     */
    private void updateCostPrice() {
        System.out.print("Enter Product Catalog Number: ");
        int catalog_number;

        try {
            catalog_number = Integer.parseInt(scan.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Product Catalog Number must be a number.");
            return;
        }

        if (inventory_controller.getProductController().isUnknownCatalogNumber(catalog_number)) {
            System.out.println("Product Catalog Number " + catalog_number + " was not found in the system.");
            return;
        }

        System.out.print("Enter new cost price: ");
        double new_price;

        try {
            new_price = Double.parseDouble(scan.nextLine());
            if (new_price < 0) {
                System.out.println("Cost price cannot be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Price must be a numeric value.");
            return;
        }

        boolean success = inventory_controller.getProductController().updateCostPriceByCatalogNumber(catalog_number, new_price);
        if (success) {
            System.out.println("Cost price for Product Catalog Number " + catalog_number + " has been updated to " + new_price + ".");
        } else {
            System.out.println("Failed to update cost price. Please check the inputs and try again.");
        }
    }

    /**
     * Marks an item as defective based on the provided item ID.
     * <p>
     * Prompts the user to enter the item ID, verifies its validity and existence,
     * and delegates the update to {@code markItemAsDefective} in the {@code ItemController}.
     * If the item exists, it is marked as defective and a confirmation message is shown.
     * Otherwise, an error message is displayed.
     */
    private void markAsDefect() {
        System.out.print("Enter item ID: ");
        int item_Id;

        try {
            item_Id = Integer.parseInt(scan.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Item ID must be a number.");
            return;
        }

        boolean success = inventory_controller.getItemController().markItemAsDefective(item_Id);
        if (success) {
            System.out.println("Item with ID " + item_Id + " has been marked as defective.");
        } else {
            System.out.println("Item with ID " + item_Id + " does not exist in the inventory.");
        }
    }

    /**
     * Generates and displays an inventory report grouped by categories.
     * <p>
     * Prompts the user to enter one or more category names (comma-separated),
     * then trims and passes them to {@code inventoryReportByCategories} in the {@code ReportController}.
     * The resulting report shows sub-categories, sizes, and items grouped accordingly.
     */
    private void generateInventoryReport() {
        System.out.println("Enter categories separated by commas: ");
        String[] categories = Arrays.stream(scan.nextLine().split(","))
                .map(String::trim)
                .toArray(String[]::new);
        System.out.println(inventory_controller.getReportController().inventoryReportByCategories(categories));
    }

    /**
     * Applies a discount (supplier or store) to a group of products based on user selection.
     * <p>
     * The user is prompted to choose the group to which the discount will apply:
     * <ul>
     *     <li>Category</li>
     *     <li>Sub-Category</li>
     *     <li>Specific Product (by catalog number)</li>
     * </ul>
     *
     * Then, the user selects the discount type (supplier or store),
     * enters a discount rate (0–100), and provides an end date (with today as the start).
     * <p>
     * The method validates all input:
     * <ul>
     *     <li>Ensures the selected group exists</li>
     *     <li>Ensures discount rate is within valid bounds</li>
     *     <li>Ensures the end date is after the current date</li>
     * </ul>
     *
     * A {@link Domain.Discount} object is created and passed to the appropriate method
     * in the {@code DiscountController}.
     * A success or failure message is displayed accordingly.
     */
    private void applyDiscount() {
        System.out.println("Apply Discount");
        System.out.println("Choose group to apply discount on:");
        System.out.println("(1) Category\n(2) Sub-Category\n(3) Product Catalog Number");

        int type;
        try {
            type = Integer.parseInt(scan.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid group choice. Returning to menu.");
            return;
        }

        String category = null, sub_category = null;
        int catalog = -1;

        if (type == 1) {
            System.out.print("Enter category: ");
            category = scan.nextLine();
            if (!inventory_controller.getProductController().hasCategory(category)) {
                System.out.println("This category does not exist. Returning to menu.");
                return;
            }
        } else if (type == 2) {
            System.out.print("Enter sub-category: ");
            sub_category = scan.nextLine();
            if (!inventory_controller.getProductController().hasSubCategory(sub_category)) {
                System.out.println("This sub-category does not exist. Returning to menu.");
                return;
            }
        } else if (type == 3) {
            System.out.print("Enter Product Catalog Number: ");
            try {
                catalog = Integer.parseInt(scan.nextLine());
                if (!inventory_controller.getProductController().isUnknownCatalogNumber(catalog)) {
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
                System.out.print("Enter end date (format: day.month.year, e.g., 30.5.2025): ");
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
        } else {
            success = is_supplier
                    ? inventory_controller.getDiscountController().setSupplierDiscountForCatalogNumber(catalog, discount)
                    : inventory_controller.getDiscountController().setStoreDiscountForCatalogNumber(catalog, discount);
        }

        System.out.println("\n-----------------------------------------");
        if (success) {
            String target = (type == 1) ? category : (type == 2) ? sub_category : "Catalog #" + catalog;
            String discountType = is_supplier ? "Supplier" : "Store";
            System.out.println(discountType + " discount of " + rate + "% was successfully applied to: " + target);
            System.out.println("Active from " + start + " to " + end);
        } else {
            System.out.println("Failed to apply discount. Check if the group exists or if the discount is valid.");
        }
        System.out.println("-----------------------------------------");
    }



    /**
     * Displays the current quantity of a product in both the warehouse and store.
     * <p>
     * Prompts the user to enter a product catalog number and retrieves the relevant
     * quantities using {@code showProductQuantities} from {@code ProductController}.
     * The result is printed in a formatted block.
     */
    private void showQuantities() {
        System.out.print("Enter Product Catalog Number: ");
        int catalog = Integer.parseInt(scan.nextLine());

        String result = inventory_controller.getProductController().showProductQuantities(catalog);

        System.out.println("\n-----------------------------");
        System.out.println(result);
        System.out.println("-----------------------------\n");
    }

    /**
     * Allows the user to update the supply time and/or demand level of a product.
     * <p>
     * Prompts the user to enter the product's catalog number and select which details to update:
     * <ul>
     *     <li>1 – Supply time only</li>
     *     <li>2 – Demand level only</li>
     *     <li>3 – Both</li>
     * </ul>
     *
     * The chosen values are then passed to {@code updateProductSupplyDetails}
     * in the {@code ProductController}, and a confirmation message is printed if successful.
     */
    private void changeSupplyDetails() {
        System.out.println("Enter Product Catalog Number:");
        int catalog = Integer.parseInt(scan.nextLine());

        System.out.println("What would you like to update?\n(1) Supply Time\n(2) Demand\n(3) Both");
        int option = Integer.parseInt(scan.nextLine());

        Integer supply = null, demand = null;

        if (option == 1 || option == 3) {
            System.out.println("Enter new supply time (in days):");
            supply = Integer.parseInt(scan.nextLine());
        }

        if (option == 2 || option == 3) {
            System.out.println("Enter new demand level (1–5):");
            demand = Integer.parseInt(scan.nextLine());
        }

        boolean updated = inventory_controller.getProductController().updateProductSupplyDetails(catalog, supply, demand);

        if (updated) {
            System.out.println("Product supply details updated successfully.");
            if (supply != null) {
                System.out.println("New supply time: " + supply + " days");
            }
            if (demand != null) {
                System.out.println("New demand level: " + demand);
            }
        } else {
            System.out.println("Product with Product Catalog Number " + catalog + " not found in inventory.");
        }
    }

    /**
     * Updates the storage location and/or section of a specific item in the inventory.
     * <p>
     * The user is prompted to enter the item ID, choose which fields to update
     * (location, section, or both), and then provide the new values accordingly.

     * The update is applied via {@code updateItemLocation} in the {@code ItemController}.
     * If the item exists, a success message is displayed with the updated values;
     * otherwise, an error message is shown.
     */
    private void changeItemLocation() {
        System.out.println("Enter item ID:");
        int id = Integer.parseInt(scan.nextLine());

        System.out.println("What would you like to change?\n(1) Location\n(2) Section\n(3) Both");
        int choice = Integer.parseInt(scan.nextLine());

        String location = null, section = null;

        if (choice == 1 || choice == 3) {
            System.out.println("Enter new location (Warehouse or InteriorStore):");
            location = scan.nextLine();
        }

        if (choice == 2 || choice == 3) {
            System.out.println("Enter new section (e.g., A1, B2, etc.):");
            section = scan.nextLine();
        }

        boolean updated = inventory_controller.getItemController().updateItemLocation(id, location, section);

        if (updated) {
            System.out.println("Item location updated successfully.");
            if (location != null) {
                System.out.println("New location: " + location);
            }
            if (section != null) {
                System.out.println("New section: " + section);
            }
        } else {
            System.out.println("Item with ID " + id + " was not found in inventory.");
        }
    }
}
