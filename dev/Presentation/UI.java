
import Domain.Controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class UI {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Controller controller= new Controller();

        // Welcome message
        System.out.println("=====================================");
        System.out.println("|                                   |");
        System.out.println("|     Welcome to the Supplier Module!     |");
        System.out.println("|                                   |");
        System.out.println("=====================================\n");

        int choice = -1;
        while (choice != 0) {
            System.out.println("========== Main Menu ==========");
            System.out.println("1. Create a new order");
            System.out.println("2. Search supplier");
            System.out.println("3. Create new supplier");
            System.out.println("4. Search for a past order");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");

            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    createOrder(scanner, controller); // תיקון הקריאה לפונקציה
                    break;
                case 2:
                     searchSupplierMenu(scanner, controller);
                    break;
                case 3:
                     int supplier_ID = createSupplier(scanner, controller);
                    afterSupplierCreatedMenu(scanner, controller, supplier_ID);
                    break;
                case 4:
                    SearchPastOrder(scanner, controller);
                case 0:
                    System.out.println("Exiting the system. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

            System.out.println(); // רווח בין הפעולות
        }

        scanner.close();
    }

    //==============================createOrderMenu==============================
    public static void createOrder(Scanner scanner, Controller controller) {
        System.out.println(" Starting new order...");

        System.out.print("Enter Order ID: ");
        int orderID = scanner.nextInt();

        System.out.print("Enter phone number: ");
        int phoneNumber = scanner.nextInt();

        System.out.print("Enter order day (as number): ");
        int day = scanner.nextInt();
        System.out.print("Enter month: ");
        int month = scanner.nextInt();
        System.out.print("Enter year: ");
        int year = scanner.nextInt();

        Date orderDate = new Date(year - 1900, month - 1, day); // יצירת תאריך (הערה: deprecated, לשימוש בסיסי)

        Map<Integer, Integer> productsInOrder = new HashMap<>();

        while (true) {
            System.out.println("\n========== Order Menu ==========");
            System.out.println("1. Add new product to order");
            System.out.println("2. Submit order and return to main menu");
            System.out.print("Enter your choice: ");
            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    System.out.print("Enter Product ID: ");
                    int productID = scanner.nextInt();

                    System.out.print("Enter quantity: ");
                    int quantity = scanner.nextInt();

                    productsInOrder.put(productID, quantity);
                    System.out.println(" Product added to order.");
                    break;

                case 2:
                    Map<Integer, Map.Entry<Integer, Double>> bestPrice = controller.orderWithBestPrice(productsInOrder);
                    printProductsWithBestPrice(bestPrice);
                    controller.createOrder(orderID, phoneNumber, orderDate, productsInOrder);//יוצר מופע של הזמנה
                    System.out.println(" Order submitted successfully.");
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public static void printProductsWithBestPrice(Map<Integer, Map.Entry<Integer, Double>> bestPriceMap) {
        System.out.println("\n📦 Products with best supplier price:");
        System.out.printf("%-12s %-10s %-10s%n", "Product ID", "Amount", "Best Price");
        System.out.println("---------------------------------------------");

        for (Map.Entry<Integer, Map.Entry<Integer, Double>> entry : bestPriceMap.entrySet()) {
            int productID = entry.getKey();
            int amount = entry.getValue().getKey();
            double price = entry.getValue().getValue();

            System.out.printf("%-12d %-10d %-10.2f%n", productID, amount, price);
        }
    }

    public static void printProductsInOrder1(Controller controller, int orderID) {
        Map<Integer, Integer> productsInOrder = controller.getProductsInOrder(orderID);

        System.out.println("\n🧾 Products in order:");
        System.out.printf("%-12s %-10s%n", "Product ID", "Amount");
        System.out.println("---------------------------");

        for (Map.Entry<Integer, Integer> entry : productsInOrder.entrySet()) {
            int productID = entry.getKey();        // מזהה המוצר
            int amount = entry.getValue();         // כמות שהוזמנה

            System.out.printf("%-12d %-10d%n", productID, amount);
        }
    }


    public static void SearchPastOrder(Scanner scanner,Controller controller){
        System.out.print("Enter Order ID: ");
        int orderID = scanner.nextInt();

        if(!controller.thereIsOrder(orderID)){
            System.out.println("There is no such order.");
        }
        else {
            System.out.println("There is such order.");
        }
        printProductsInOrder1(controller,orderID);//מדפיסה את המוצרים שהיו בהזמנה עם הכמות והמחיר
    }

    //==============================searchSupplierMenu==============================
    public static void searchSupplierMenu(Scanner scanner,Controller controller) {
        System.out.println("Let's search supplier!");
        Integer supplierID = getValidSupplierID(scanner, controller);
        if(supplierID != null) {
            int choice = -1;
            while (choice != 0) {
                System.out.println("\nWhat would you like to do next?");
                System.out.println("1. Create a new agreement with this supplier ");
                System.out.println("2. Edit specific agreement with this supplier ");
                System.out.println("3. Delete agreement with this supplier ");
                System.out.println("4. Delete this supplier");
                System.out.println("0. Return to main menu");
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        System.out.println("Creating a new agreement...");
                        createNewAgreement(scanner, controller, supplierID);
                        break;
                    case 2:
                        editSpecificAgreementMenu(scanner, controller, supplierID);
                        break;
                    case 3:
                         DeleteAgreement(scanner, controller);
                        break;
                    case 4:
                        DeleteSupplier(scanner, controller);
                        break;
                    case 0:
                        System.out.println("Return to Main Menu. ");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        }
    }


    public static Integer getValidAgreementID(Scanner scanner, Controller controller) {
        System.out.print("Enter Agreement ID: ");
        int agreementID = scanner.nextInt();

        if (!controller.thereIsAgreement(agreementID)) {
            System.out.println("Agreement does not exist.");
            return null; // מייצג כישלון
        }

        System.out.println("Agreement found.");
        return agreementID; // מייצג הצלחה
    }


    public static void DeleteAgreement(Scanner scanner, Controller controller) {
        Integer agreementID = getValidAgreementID(scanner, controller);
        if(agreementID!=null) {
            controller.deleteAgreement(agreementID);
            System.out.println("Agreement deleted.");
        }
    }

    //==============================editSpecificAgreementMenu==============================
    public static void editSpecificAgreementMenu(Scanner scanner, Controller controller, int supplierID) {
        System.out.println("Let's edit agreement...");
        Integer agreementID = getValidAgreementID(scanner, controller);
        if(agreementID!= null) {
            int choice = -1;
            while (choice != 0) {
                System.out.println("\nWhat would you like to do next?");
                System.out.println("1. Add new product to this agreement ");
                System.out.println("2. remove product from this agreement ");
                System.out.println("3. Edit product supply terms ");
                System.out.println("3. Edit the delivery days ");
                System.out.println("4. Change selfPickup status ");
                System.out.print("0.Return to main menu: ");
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();
            }
            switch (choice) {
                case 1:
                    System.out.println("Adding new product to agreement...");
                    // TODO: addNewProductToAgreement(scanner, controller, agreementID);
                    System.out.println("Product added successfully.\n");
                    break;

                case 2:
                    System.out.println("Removing product from agreement...");
                    // TODO: removeProductFromAgreement(scanner, controller);
                    System.out.println("Product removed successfully.\n");
                    break;

                case 3:
                    System.out.println("Editing product supply terms...");
                    // TODO: editProductSupplyTerms(scanner, controller);
                    System.out.println("Supply terms updated successfully.\n");
                    break;

                case 4:
                    System.out.println("Editing delivery days...");
                    // TODO: editDeliveryDays(scanner, controller, agreementID);
                    System.out.println("Delivery days updated.\n");
                    break;

                case 5:
                    System.out.println("Toggling self-pickup status...");
                    // TODO: controller.toggleSelfPickup(agreementID);
                    System.out.println("Self-pickup status changed.\n");
                    break;

                case 0:
                    System.out.println("Returning to main menu...");
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.\n");
                    break;

            }
        }
    }


    //==============================createNewAgreementMenu==============================
    //==============================createNewAgreement
    public static void createNewAgreement(Scanner scanner, Controller controller, int supplierID) {
        System.out.println("Creating new agreement...");

        System.out.print("Enter Agreement ID: ");
        int agreementID = scanner.nextInt();
        scanner.nextLine(); // קולט את ה־\n שנשאר אחרי nextInt()

        System.out.print("Enter Delivery Days (comma separated, e.g. Mon,Wed,Fri): ");
        String[] deliveryDays = scanner.nextLine().split(",");

        System.out.print("Self Pickup? (Y/N): ");
        String selfPickupInput = scanner.next();
        boolean selfPickup = selfPickupInput.equalsIgnoreCase("Y");

        controller.createAgreement(agreementID, supplierID, deliveryDays, selfPickup);
        System.out.println("Agreement created successfully.");
    }
    //==============================createNewAgreementMenu
//    public static void addNewProductToAgreement(Scanner scanner, Controller controller,int agreementID){
//        System.out.println("Let's Adding new product to agreement...");
//
//        System.out.print("Enter Product Catalog Number: ");
//        String catalogNumber = scanner.nextLine();
//
//        System.out.print("Enter Product ID: ");
//        int productID = scanner.nextInt();
//        scanner.nextLine();
//
//        System.out.print("Enter price: ");
//        double price = scanner.nextDouble();
//
//        System.out.print("Enter Units of Measure: ");
//        String unitsOfMeasure = scanner.nextLine();
//
//        // TODO: System.out.print("Enter Discounts: ");//רן צריך לעשות
//
//        controller.addProductToAgreement(agreementID,catalogNumber, productID, price, //TODO discounts );
//
//    }



    //==============================createSupplierMenu==============================
    public static int createSupplier(Scanner scanner, Controller controller) {
        System.out.println("Let's create a new supplier!");

        System.out.print("Enter Supplier ID: ");
        int supplierID = scanner.nextInt();

        System.out.print("Enter Supplier Name: ");
        String supplierName = scanner.next();

        System.out.print("Enter Company ID: ");
        int companyID = scanner.nextInt();

        System.out.print("Enter Bank Account: ");
        int bankAccount = scanner.nextInt();

        System.out.print("Enter Payment Method: ");
        String paymentMethod = scanner.next();

        System.out.print("Enter Phone Number: ");
        int phoneNumber = scanner.nextInt();

        System.out.print("Enter Email: ");
        String email = scanner.next();

        System.out.print("Enter Payment Day: ");
        String paymentDay = scanner.next();

        controller.createSupplier(supplierName, supplierID, companyID, bankAccount, paymentMethod, phoneNumber, email, paymentDay);

        System.out.println("Supplier created successfully!");
        return supplierID;
    }

    public static Integer getValidSupplierID(Scanner scanner, Controller controller) {
        System.out.print("Enter Supplier ID: ");
        int supplierID = scanner.nextInt();

        if (!controller.thereIsSupplier(supplierID)) {
            System.out.println("Supplier does not exist.");
            return null; // מייצג כישלון
        }

        System.out.println("Supplier found.");
        return supplierID; // מייצג הצלחה
    }

    public static void DeleteSupplier(Scanner scanner, Controller controller){
        System.out.println("OK Let's Delete a supplier!");
        Integer supplierID = getValidSupplierID(scanner, controller);
        if (supplierID != null) {
            controller.deleteSupplier(supplierID);
        }
    }

    //==============================afterSupplierCreatedMenu
    public static void afterSupplierCreatedMenu(Scanner scanner, Controller controller, int supplier_ID) {/// ////////////////to do
        int choice = -1;
        while (choice != 2) {
            System.out.println("\nWhat would you like to do next?");
            System.out.println("1. Create a new agreement with this supplier");
            System.out.println("2. Return to main menu");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("Creating a new agreement...");
                    createNewAgreement(scanner,controller,supplier_ID);
                    break;
                case 2:
                    System.out.println("Returning to main menu.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

}
