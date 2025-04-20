package Presentation;

import Domain.Controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class OrderMenuHandler {
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
        System.out.println("\n Products with best supplier price:");
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


}
