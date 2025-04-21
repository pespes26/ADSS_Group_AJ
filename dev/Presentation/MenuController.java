package Presentation;

import Domain.Discount;
import Service.InventoryServiceFacade;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Scanner;

public class MenuController {
    private final Scanner scan;
    private final InventoryServiceFacade inventoryServiceFacade;

    public MenuController(InventoryServiceFacade inventoryServiceFacade) {
        this.scan = new Scanner(System.in);
        this.inventoryServiceFacade = inventoryServiceFacade;
    }

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
                13. Change item location
                14. Exit
                """);
    }

    private void handleChoice(int choice) {
        switch (choice) {
            case 1 -> showItemDetails();
            case 2 -> addNewItem();
            case 3 -> removeItem();
            case 4 -> showPurchasePrices();
            case 5 -> updateCostPrice();
            case 6 -> markAsDefect();
            case 7 -> generateInventoryReport();
            case 8 -> System.out.println(inventoryServiceFacade.getReportController().defectAndExpiredReport());
            case 9 -> applyDiscount();
            case 10 -> showQuantities();
            case 11 -> System.out.println(inventoryServiceFacade.getReportController().generateReorderAlertReport());
            case 12 -> changeSupplyDetails();
            case 13 -> changeItemLocation();
            case 14 -> {} // exit
            default -> System.out.println("Invalid option. Please try again.");
        }
    }

    private void showItemDetails() {
        System.out.println("Enter item ID: ");
        int itemId = Integer.parseInt(scan.nextLine());
        System.out.println(inventoryServiceFacade.getItemController().showItemDetails(itemId));
    }

    private void addNewItem() {
        System.out.println("Please enter the following details for the new item:");

        System.out.print("Item ID (unique number): ");
        int itemId = Integer.parseInt(scan.nextLine());

        if (inventoryServiceFacade.getItemController().itemExists(itemId)) {
            System.out.println("An item with ID " + itemId + " already exists in the system.");
            System.out.println("Returning to the main menu.");
            return;
        }

        System.out.print("Product Name: ");
        String productName = scan.nextLine();

        System.out.print("Expiring Date (format: dd.mm.yyyy): ");
        String expiringDate = scan.nextLine();

        System.out.print("Location (Warehouse/interiorStore): ");
        String location = scan.nextLine();

        System.out.print("Section in store (e.g., A1, B2): ");
        String section = scan.nextLine();

        System.out.print("Product Catalog Number (unique per product): ");
        int catalogNumber = Integer.parseInt(scan.nextLine());

        System.out.print("Category: ");
        String category = scan.nextLine();

        System.out.print("Sub-Category: ");
        String subCategory = scan.nextLine();

        System.out.print("Item Size (1 - Small, 2 - Medium, 3 - Big): ");
        int size = Integer.parseInt(scan.nextLine());

        System.out.print("Cost Price Before Supplier Discount: ");
        double costPriceBefore = Double.parseDouble(scan.nextLine());

        System.out.print("Product Demand Level (1-5): ");
        int demand = Integer.parseInt(scan.nextLine());

        System.out.print("Supply Time (in days): ");
        int supplyTime = Integer.parseInt(scan.nextLine());

        System.out.print("Manufacturer: ");
        String manufacturer = scan.nextLine();

        System.out.print("Supplier Discount (%): ");
        int supplierDiscount = Integer.parseInt(scan.nextLine());

        System.out.print("Store Discount (%): ");
        int storeDiscount = Integer.parseInt(scan.nextLine());

        // Compose CSV format string for internal use
        String csvInput = itemId + "," + productName + "," + expiringDate + "," + location + "," + section + ","
                + catalogNumber + "," + category + "," + subCategory + "," + size + "," + costPriceBefore + ","
                + demand + "," + supplyTime + "," + manufacturer + "," + supplierDiscount + "," + storeDiscount;

        boolean success = inventoryServiceFacade.getItemController().addItem(csvInput);

        System.out.println("\n-----------------------------------------");
        if (success) {
            System.out.println("Item added successfully.");
            System.out.println("Product Name: " + productName);
            System.out.println("Catalog Number: " + catalogNumber);
            System.out.println("Category: " + category + ", Sub-Category: " + subCategory);
        } else {
            System.out.println("Failed to add item. Please verify all details were entered correctly.");
        }
        System.out.println("-----------------------------------------");
    }


    private void removeItem() {
        System.out.print("Enter item ID: ");
        int itemId = Integer.parseInt(scan.nextLine());

        if (!inventoryServiceFacade.getItemController().itemExists(itemId)) {
            System.out.println("Item does not exist in the inventory.");
            return;
        }

        System.out.println("What is the reason for removing the item?");
        System.out.println("(1) Purchase\n(2) Defect");
        int reason = Integer.parseInt(scan.nextLine());

        if (reason == 1) {
            boolean alert = inventoryServiceFacade.getItemController().checkReorderAlert(itemId);
            String productName = inventoryServiceFacade.getItemController().getItemName(itemId);
            double salePrice = inventoryServiceFacade.getItemController().getSalePriceAfterDiscount(itemId);

            inventoryServiceFacade.getItemController().removeItemByPurchase(itemId);

            System.out.println("\n-----------------------------------------");
            System.out.println("The item \"" + productName + "\" has been marked as purchased and removed.");
            System.out.printf("The item was sold for: %.2f ₪ (after store discount)\n", salePrice);
            if (alert) {
                System.out.println("ALERT: The product \"" + productName + "\" has reached a critical amount!");
                System.out.println("Please consider reordering.");
            }
            System.out.println("-----------------------------------------");

        } else if (reason == 2) {
            boolean alert = inventoryServiceFacade.getItemController().checkReorderAlert(itemId);
            String productName = inventoryServiceFacade.getItemController().getItemName(itemId);

            inventoryServiceFacade.getItemController().removeItemByDefect(itemId);

            System.out.println("\n-----------------------------------------");
            System.out.println("The item \"" + productName + "\" has been marked as defective and removed.");
            if (alert) {
                System.out.println("ALERT: The product \"" + productName + "\" has reached a critical amount!");
                System.out.println("Please consider reordering.");
            }
            System.out.println("-----------------------------------------");

        } else {
            System.out.println("Invalid choice. Please enter 1 or 2.");
        }
    }



    private void showPurchasePrices() {
        System.out.println("Enter Product Catalog Number: ");
        int catalog = Integer.parseInt(scan.nextLine());
        System.out.println(inventoryServiceFacade.getProductController().showProductPurchasesPrices(catalog));
    }

    private void updateCostPrice() {
        System.out.print("Enter Product Catalog Number: ");
        int catalogNumber;

        try {
            catalogNumber = Integer.parseInt(scan.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Product Catalog Number must be a number.");
            return;
        }

        if (!inventoryServiceFacade.getProductController().hasCatalogNumber(catalogNumber)) {
            System.out.println("Product Catalog Number " + catalogNumber + " was not found in the system.");
            return;
        }

        System.out.print("Enter new cost price: ");
        double newPrice;

        try {
            newPrice = Double.parseDouble(scan.nextLine());
            if (newPrice < 0) {
                System.out.println("Cost price cannot be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Price must be a numeric value.");
            return;
        }

        boolean success = inventoryServiceFacade.getProductController().updateCostPriceByCatalogNumber(catalogNumber, newPrice);
        if (success) {
            System.out.println("Cost price for Product Catalog Number " + catalogNumber + " has been updated to " + newPrice + ".");
        } else {
            System.out.println("Failed to update cost price. Please check the inputs and try again.");
        }
    }


    private void markAsDefect() {
        System.out.print("Enter item ID: ");
        int itemId;

        try {
            itemId = Integer.parseInt(scan.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Item ID must be a number.");
            return;
        }

        boolean success = inventoryServiceFacade.getItemController().markItemAsDefective(itemId);
        if (success) {
            System.out.println("Item with ID " + itemId + " has been marked as defective.");
        } else {
            System.out.println("Item with ID " + itemId + " does not exist in the inventory.");
        }
    }


    private void generateInventoryReport() {
        System.out.println("Enter categories separated by commas: ");
        String[] categories = Arrays.stream(scan.nextLine().split(","))
                .map(String::trim)
                .toArray(String[]::new);
        System.out.println(inventoryServiceFacade.getReportController().inventoryReportByCategories(categories));
    }

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

        String category = null, subCategory = null;
        int catalog = -1;

        if (type == 1) {
            System.out.print("Enter category: ");
            category = scan.nextLine();
            if (!inventoryServiceFacade.getProductController().hasCategory(category)) {
                System.out.println("This category does not exist. Returning to menu.");
                return;
            }
        } else if (type == 2) {
            System.out.print("Enter sub-category: ");
            subCategory = scan.nextLine();
            if (!inventoryServiceFacade.getProductController().hasSubCategory(subCategory)) {
                System.out.println("This sub-category does not exist. Returning to menu.");
                return;
            }
        } else if (type == 3) {
            System.out.print("Enter Product Catalog Number: ");
            try {
                catalog = Integer.parseInt(scan.nextLine());
                if (!inventoryServiceFacade.getProductController().hasCatalogNumber(catalog)) {
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
        int discountTypeInput;
        try {
            discountTypeInput = Integer.parseInt(scan.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid discount type. Returning to menu.");
            return;
        }

        if (discountTypeInput != 1 && discountTypeInput != 2) {
            System.out.println("Invalid discount type. Returning to menu.");
            return;
        }

        boolean isSupplier = discountTypeInput == 1;

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
            success = isSupplier
                    ? inventoryServiceFacade.getDiscountController().setSupplierDiscountForCategory(category, discount)
                    : inventoryServiceFacade.getDiscountController().setStoreDiscountForCategory(category, discount);
        } else if (type == 2) {
            success = isSupplier
                    ? inventoryServiceFacade.getDiscountController().setSupplierDiscountForSubCategory(subCategory, discount)
                    : inventoryServiceFacade.getDiscountController().setStoreDiscountForSubCategory(subCategory, discount);
        } else {
            success = isSupplier
                    ? inventoryServiceFacade.getDiscountController().setSupplierDiscountForCatalogNumber(catalog, discount)
                    : inventoryServiceFacade.getDiscountController().setStoreDiscountForCatalogNumber(catalog, discount);
        }

        System.out.println("\n-----------------------------------------");
        if (success) {
            String target = (type == 1) ? category : (type == 2) ? subCategory : "Catalog #" + catalog;
            String discountType = isSupplier ? "Supplier" : "Store";
            System.out.println(discountType + " discount of " + rate + "% was successfully applied to: " + target);
            System.out.println("Active from " + start + " to " + end);
        } else {
            System.out.println("Failed to apply discount. Check if the group exists or if the discount is valid.");
        }
        System.out.println("-----------------------------------------");
    }




    private void showQuantities() {
        System.out.print("Enter Product Catalog Number: ");
        int catalog = Integer.parseInt(scan.nextLine());

        String result = inventoryServiceFacade.getProductController().showProductQuantities(catalog);

        System.out.println("\n-----------------------------");
        System.out.println(result);
        System.out.println("-----------------------------\n");
    }


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

        boolean updated = inventoryServiceFacade.getProductController().updateProductSupplyDetails(catalog, supply, demand);

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

        boolean updated = inventoryServiceFacade.getItemController().updateItemLocation(id, location, section);

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
