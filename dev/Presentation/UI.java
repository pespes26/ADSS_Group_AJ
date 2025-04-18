
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
                     createSupplier(scanner, controller);
                    afterSupplierCreatedMenu(scanner, controller);
                    break;
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
                    controller.createOrder(orderID, phoneNumber, orderDate, productsInOrder);//יוצר מופע של הזמנה
                    Map<Integer, Map.Entry<Integer, Double>> productsInOrder1 = controller.getProductsInOrder(orderID);// מבנה נתונים עם כל המוצרים.
                    System.out.println(" Order submitted successfully.");
                    printProductsInOrder1(productsInOrder1); //מדפיס את המוצרים
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public static void printProductsInOrder1(Map<Integer, Map.Entry<Integer, Double>> productsInOrder1) {
        System.out.println("\nProducts in order:");
        for (Map.Entry<Integer, Map.Entry<Integer, Double>> entry : productsInOrder1.entrySet()) {
            int productID = entry.getKey();                         // המפתח
            int productAmount = entry.getValue().getKey();          // הכמות
            double productPrice = entry.getValue().getValue();      // המחיר

            System.out.println("Product ID: " + productID +
                    ", Amount: " + productAmount +
                    ", Price: " + productPrice);
        }
    }


    public static void searchSupplierMenu(Scanner scanner,Controller controller) {
        System.out.println("Let's search supplier!");
        System.out.print("Enter Supplier ID: ");
        int supplierID = scanner.nextInt();
        if (!controller.searchSupplierByID(supplierID)) {
            System.out.println("Supplier does not exist.");
        } else {
            System.out.println("supplier is found!");

            int choice = -1;
            while (choice != 2) {
                System.out.println("\nWhat would you like to do next?");
                System.out.println("1. Create a new agreement with this supplier ");
                System.out.println("2. Edit specific agreement with this supplier ");
                System.out.println("3. Delete agreement with this supplier ");
                System.out.println("4. Delete this supplier");
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        System.out.println("Creating a new agreement...");
                        createNewAgreement(scanner, controller, supplierID);
                        break;
                    case 2:
                        editSpecificAgreement(scanner, controller, supplierID);
                        break;
                    case 3:
                        DeleteAgreement(scanner, controller);
                        break;
                    case 4:
                        DeleteSupplier(scanner, controller);
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        }
    }

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



    //==============================SupplierMenu
    public static void createSupplier(Scanner scanner, Controller controller) {
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

    }

    public static void afterSupplierCreatedMenu(Scanner scanner, Controller controller) {/// ////////////////to do
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
                    // אפשר להוסיף קריאה לפונקציה createAgreementMenu(scanner, controller, supplierID);
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
