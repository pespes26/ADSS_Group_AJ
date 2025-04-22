package Presentation;

import Domain.Controller;
import Domain.Product;

import java.util.Scanner;

public class ProductMenuHandler {

    public static void addNewProduct(Scanner scanner, Controller controller, int supplierID,int agreementID) {
        System.out.println("\nLet's add a new product...");


//----------בדיקת מספר קטלוגי ייחודי

        int catalog_Number;
        while (true) {
            catalog_Number = Inputs.read_int(scanner, "Enter Catalog Number: ");
            boolean isCatalogUnique = validateUniqueCatalogNumber(controller, catalog_Number, supplierID);
            if (isCatalogUnique) {
                break; // המספר הקטלוגי ייחודי → אפשר לצאת מהלולאה
            }
            // אחרת נחזור לראש הלולאה ונבקש שוב
        }
//----------------בדיקת מספר מזהה ייחודי--------------------
        int product_id;
        while (true) {
            product_id = Inputs.read_int(scanner, "Enter Product ID: ");
            boolean isProductIDUnique = validateUniqueProductIDNumber(controller, product_id, supplierID);
            if (isProductIDUnique) {
                break; // רק אם ה-ID ייחודי → נצא מהלולאה
            }
        }

            double price = Inputs.read_double(scanner, "Enter Product Price: ");

            System.out.println("Enter Unit of Measure: ");
            String unitsOfMeasure = scanner.next();

            Product product = controller.createProduct(catalog_Number, product_id, price, unitsOfMeasure);
            controller.addProductToAgreement(product_id, product, agreementID);

            int choice = -1;
            while (choice != 2) {
                System.out.println("Do you want to add new discount rule?");
                System.out.println("1. Yes");
                System.out.println("2. No");
                System.out.print("Enter your choice: ");

                choice = Inputs.read_input_for_choice(scanner);

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
            int numOfRules = Inputs.read_int(scanner, "Enter number of discount rules: ");

            for (int i = 0; i < numOfRules; i++) {
                System.out.println("Discount Rule #" + (i + 1));
                int amount = Inputs.read_int(scanner, "Enter minimum amount for discount: ");
                double discount = Inputs.read_double(scanner, "Enter discount percentage (e.g., 10 for 10%): ");
                controller.add_discountRule(CatalogNumber, discount, amount);
            }
        } else {
            System.out.println("Product not found. Cannot add discount rules.");
        }
    }

    public static Integer getProductCatalogNumberFromUser(Controller controller, Scanner scanner) {
        int catalogNumber = Inputs.read_int(scanner, "Enter catalogNumber: ");
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

            choice = Inputs.read_input_for_choice(scanner);

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
        double newPrice = Inputs.read_double(scanner, "Enter new price: ");
        controller.updateProductPrice(catalogNumber, newPrice);
        System.out.println("Product price updated.");
    }

    public static void updateProductUnit(Scanner scanner, Controller controller, int catalogNumber) {
        System.out.print("Enter new unit of measure: ");
        String newUnit = scanner.next();
        controller.updateProductUnit(catalogNumber, newUnit);
        System.out.println("Product unit updated.");
    }

    public static boolean validateProductExistsByID(Controller controller, int productID) {
        boolean existsProduct = controller.existsProductWithID(productID);
        if (!existsProduct) {
            System.out.println("This product does not exist. Try another one.\n");
        }
        return existsProduct;
    }

    public static boolean validateUniqueCatalogNumber(Controller controller, int catalogNumber, int supplierID) {
        boolean exists = controller.thereIsProductWithSameCatalogNumber(catalogNumber, supplierID);
        if (exists) {
            System.out.println("This product already exists in an agreement with this supplier. Please enter a different product.\n");
            return false;
        }
        return true;
    }

    public static boolean validateUniqueProductIDNumber(Controller controller, int productID, int supplierID){
        boolean exists = controller.thereIsProductWithSameProductID(productID, supplierID);
        if (exists) {
            System.out.println("This product already exists in this agreement! Please add other product.\n");
            return false;
        }
        return true;
    }



}
