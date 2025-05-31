package Suppliers.Presentation;
import Suppliers.Domain.AgreementManagementController;
import Suppliers.Domain.Controller;

import java.sql.SQLException;
import java.util.Scanner;

public class MainMenu {

    public static void main(String[] args) throws SQLException {
        SupplierMenuHandler supplierMenuHandler = new SupplierMenuHandler();
        OrderMenuHandler orderMenuHandler = new OrderMenuHandler();
        ProductMenuHandler productMenuHandler = new ProductMenuHandler();
        AgreementMenuHandler agreementMenuHandler = new AgreementMenuHandler();
        Controller controller = new Controller();

        Scanner scanner = new Scanner(System.in);
        SystemInitializer initializer = new SystemInitializer();
        SystemInitializer.initializeAllTables();

// Ask user whether to load sample data
        System.out.print("Do you want to initialize the system with sample data? (yes/no): ");
        String input = scanner.nextLine().trim().toLowerCase();
        boolean withSampleData = input.equals("yes");

// Initialize database accordingly
        initializer.initializeDatabase(withSampleData);


        // Welcome message
        System.out.println("=====================================");
        System.out.println("|                                   |");
        System.out.println("|  Welcome to the Supplier Module!  |");
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



            if (!scanner.hasNextInt()) {
                System.out.println("Invalid choice. Please try again.");
                System.out.println(); // רווח בין הפעולות
                scanner.next(); //מדלג על הקלט הלא תקין כמו string
                continue; //back to the menu
            }

            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    OrderMenuHandler.createOrder(scanner, controller); // תיקון הקריאה לפונקציה
                    break;
                case 2:
                    supplierMenuHandler.searchSupplierMenu(scanner);

                    break;
                case 3:
                    int supplier_ID = supplierMenuHandler.createSupplier(scanner);
                    supplierMenuHandler.afterSupplierCreatedMenu(scanner, supplier_ID);

                    break;
                case 4:
                    System.out.println(); // רווח בין הפעולות
                    OrderMenuHandler.SearchPastOrder(scanner, controller);
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
}
