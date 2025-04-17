
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
                    // searchSupplierMenu(scanner, controller);
                    break;
                case 3:
                    // createSupplierMenu(scanner, controller);
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
                    controller.createOrder(orderID, phoneNumber, orderDate, productsInOrder);
                    System.out.println(" Order submitted successfully.");
//                    printOrderDetails();
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
