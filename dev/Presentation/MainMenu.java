package Presentation;
import Domain.Controller;
import java.util.Scanner;

public class MainMenu {

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
                     SupplierMenuHandler.searchSupplierMenu(scanner, controller);
                    break;
                case 3:
                     int supplier_ID = SupplierMenuHandler.createSupplier(scanner, controller);
                    SupplierMenuHandler.afterSupplierCreatedMenu(scanner, controller, supplier_ID);
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
    }///////////////////////////////////////////////////////////////////////
}
