package Presentation;

import Domain.Controller;

import java.util.Scanner;

public class ProductMenuHandler {

    public static void addNewProduct(Scanner scanner, Controller controller) {
        System.out.println("\nAdding new product...");

        System.out.println("Enter Catalog Number: ");
        int catalog_Number = scanner.nextInt();

        System.out.println("Enter Product ID: ");
        int product_id = scanner.nextInt();

        System.out.println("Enter Product Price: ");
        double price = scanner.nextDouble();

        System.out.println("Enter Unit of Measure: ");
        String unitsOfMeasure = scanner.next();

        controller.createProduct(catalog_Number, product_id, price, unitsOfMeasure);

        int choice = -1;
        while (choice != 2) {
            System.out.println("Do you want to add new discount rule?");
            System.out.println("1. Yes");
            System.out.println("2. No");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("Use case: Add discount rule to product");
                    readAndAddDiscountRules(scanner, controller, product_id);
                    System.out.println("\nProduct added successfully.");
                    return;
                case 2:
                    System.out.println("No discount rules will be added.");
                    System.out.println("\nProduct added successfully.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public static void readAndAddDiscountRules(Scanner scanner, Controller controller, int CatalogNumber) {
        if (controller.productExistsByCatalog(CatalogNumber)) {
            System.out.print("Enter number of discount rules: ");
            int numOfRules = scanner.nextInt();

            for (int i = 0; i < numOfRules; i++) {
                System.out.println("Discount Rule #" + (i + 1));
                System.out.print("Enter minimum amount for discount: ");
                int amount = scanner.nextInt();

                System.out.print("Enter discount percentage (e.g., 10 for 10%): ");
                double discount = scanner.nextDouble();

                controller.add_discountRule(CatalogNumber, discount, amount);
            }
        } else {
            System.out.println("Product not found. Cannot add discount rules.");
        }
    }

    public static Integer getProductCatalogNumberFromUser(Controller controller, Scanner scanner) {
        System.out.println("\nEnter catalogNumber: ");
        int catalogNumber = scanner.nextInt();
        if (controller.productExistsByCatalog(catalogNumber)) {
            return catalogNumber;
        } else {
            System.out.println("Product not found.");
            return null;
        }
    }

    public static void removeProduct(Scanner scanner, Controller controller) {
        Integer catalogNumber = getProductCatalogNumberFromUser(controller, scanner);
        if (catalogNumber != null) {
            System.out.println("\nRemoving product...");
            controller.deleteProductByCatalog(catalogNumber);
            System.out.println("Product removed!.");
        }
    }

    public static void editProductTerms(Scanner scanner, Controller controller) {
        System.out.println("\nEdit Product Supply Terms:");

        Integer catalogNumber = getProductCatalogNumberFromUser(controller, scanner);
        if (catalogNumber == null) return;

        int choice = -1;
        while (choice != 0) {
            System.out.println("\nChoose what you want to update:");
            System.out.println("1. Update Product Price");
            System.out.println("2. Update Unit of Measure");
            System.out.println("3. Add or Update Discount Rule");
            System.out.println("0. Return to previous menu");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    updateProductPrice(scanner, controller, catalogNumber);
                    break;
                case 2:
                    updateProductUnit(scanner, controller, catalogNumber);
                    break;
                case 3:
                    readAndAddDiscountRules(scanner, controller, catalogNumber);
                    break;
                case 0:
                    System.out.println("Returning to previous menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public static void updateProductPrice(Scanner scanner, Controller controller, int catalogNumber) {
        System.out.print("Enter new price: ");
        double newPrice = scanner.nextDouble();
        controller.updateProductPrice(catalogNumber, newPrice);
        System.out.println("Product price updated.");
    }

    public static void updateProductUnit(Scanner scanner, Controller controller, int catalogNumber) {
        System.out.print("Enter new unit of measure: ");
        String newUnit = scanner.next();
        controller.updateProductUnit(catalogNumber, newUnit);
        System.out.println("Product unit updated.");
    }

}
