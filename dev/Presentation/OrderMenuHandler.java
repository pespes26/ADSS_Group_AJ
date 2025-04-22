package Presentation;
import Domain.Controller;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.time.LocalDateTime;           // נוסיף בתחילת הקובץ
import java.time.format.DateTimeFormatter;

public class OrderMenuHandler {
    public static void createOrder(Scanner scanner, Controller controller) {

        // בדיקה אם אפשר ליצור הזמנה
        /*if (!controller.canCreateOrder()) {
            System.out.println("Cannot create order - missing suppliers, agreements, or products.");
            return; // לא ממשיכים אם לא ניתן ליצור הזמנה
        }*/

        System.out.println("\nStarting a new order...");
        System.out.println("Before we begin, please enter a few details:\n");

        //order id to a new order
        int orderID;
        while (true) { // לולאה שתרוץ עד שהמשתמש יזין מזהה הזמנה חוקי
            orderID = Inputs.read_int(scanner, "Enter Order ID: ");
            if (!controller.thereIsOrder(orderID)) { // בדיקה האם כבר קיימת הזמנה עם אותו מזהה
                break;
            }
            System.out.println("This Order ID already exists. Try another one.");
        }

        //phone number
        int phoneNumber = Inputs.read_int(scanner, "Enter phone number: ");//input checking
   /*     while (true) {// לולאה שתרוץ עד שיתקבל מספר חוקי
            System.out.print("Enter phone number: ");
            if (scanner.hasNextInt()) {
                phoneNumber = scanner.nextInt();// שומר את המספר ומפסיק את הלולאה
                break;
            } else {
                System.out.println("Invalid choice. Please Enter phone number again: ");
                scanner.next();// מדלג על הקלט הבעייתי (כדי שלא ניתקע בלולאה)
            }
        }*/
        //////////////////////////////
        //maybe change it to local time// ask Maor
        ///////////////////////////////



        // === שימוש בזמן נוכחי של המחשב ===
        LocalDateTime orderDate = LocalDateTime.now();  // תאריך+שעה נוכחיים
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        System.out.println("Order date is set to: " + orderDate.format(formatter)); // הודעה למשתמש


        Map<Integer, Integer> productsInOrder = new HashMap<>();
        System.out.println("Let's create an order!\n");
        int option = -1;

        while (option != 0) {
            System.out.println("\n========== Order Menu ==========");
            System.out.print("Choose how to proceed:\n");
            System.out.println("1. Add new product to order");
            System.out.println("2. Submit order and return to main menu");
            System.out.println("0. Cancel order and return to previous menu");
            System.out.print("Enter your choice: ");

            option = Inputs.read_input_for_choice(scanner);

            switch (option) {
                case 1:
                    int productID;
                    while (true) {
                        productID = Inputs.read_int(scanner, "Enter Product ID: ");
                        boolean existsProduct = ProductMenuHandler.validateProductExistsByID(controller, productID);
                        if (existsProduct) { // אם יש מוצר כזה אז בסדר
                            break;//אם אין מוצר עם ID כזה הלולאה עוצרת
                        }
                    }

                    int quantity = Inputs.read_int(scanner, "Enter quantity: ");

                    productsInOrder.put(productID, quantity);
                    System.out.println(" Product added to order.");
                    break;

                case 2:
                    Map<Integer, Map.Entry<Integer, Double>> bestPrice = controller.orderWithBestPrice(productsInOrder);
                    printProductsWithBestPrice(bestPrice);
                    controller.createOrder(orderID, phoneNumber, orderDate, productsInOrder);//יוצר מופע של הזמנה
                    System.out.println("\nOrder submitted successfully.");
                    return;
                case 0:
                    System.out.println("\nReturning to previous menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }


    public static void printProductsWithBestPrice(Map<Integer, Map.Entry<Integer, Double>> bestPriceMap) {
        System.out.println("\nProducts with best supplier price:");
        System.out.printf("%-12s %-10s %-10s%n", "Product ID", "Amount", "Best Price");
        System.out.println("---------------------------------------------");

        double totalCost = 0;

        for (Map.Entry<Integer, Map.Entry<Integer, Double>> entry : bestPriceMap.entrySet()) {
            int productID = entry.getKey();
            int amount = entry.getValue().getKey();
            double price = entry.getValue().getValue();

            double productCost = amount * price;
            totalCost += productCost;

            System.out.printf("%-12d %-10d %-10.2f%n", productID, amount, price);
        }

        System.out.println("---------------------------------------------");
        System.out.printf("Total order cost: %.2f ₪%n\n", totalCost);
    }


//    public static void printProductsInOrder1(Controller controller, int orderID) {
//        Map<Integer, Integer> productsInOrder = controller.getProductsInOrder(orderID);
//
//        if (productsInOrder == null || productsInOrder.isEmpty()) { //if not have orders
//            System.out.println("No products in this order.");
//            return;
//        }
//
//        System.out.println("\n Products in order:");
//        System.out.printf("%-12s %-10s%n", "Product ID", "Amount");
//        System.out.println("---------------------------");
//
//
//        for (Map.Entry<Integer, Integer> entry : productsInOrder.entrySet()) {
//            int productID = entry.getKey();        // מזהה המוצר
//            int amount = entry.getValue();         // כמות שהוזמנה
//
//            System.out.printf("%-12d %-10d%n", productID, amount);
//        }
//    }

    public static void printOrderSummaryFromController(Controller controller, int orderID) {

        Map<Integer, Integer> productsInOrder = controller.getProductsInOrder(orderID);
        if (productsInOrder == null || productsInOrder.isEmpty()) {
            System.out.println("No products in this order.");
            return;
        }

        System.out.println("\n========== Order Summary ==========");
        System.out.println("Order ID      : " + orderID);
        System.out.println("Phone Number  : " + controller.getPhoneNumber(orderID));
        System.out.println("Order Date    : " + controller.getFormattedOrderDate(orderID));

        System.out.println("\nOrdered Products:");
        System.out.printf("%-15s %-10s%n", "Product ID", "Quantity");
        System.out.println("------------------------------");

        for (Map.Entry<Integer, Integer> entry : productsInOrder.entrySet()) {
            System.out.printf("%-15d %-10d%n", entry.getKey(), entry.getValue());
        }
    }


    public static void SearchPastOrder(Scanner scanner,Controller controller){
        int orderID = Inputs.read_int(scanner, "Enter Order ID: ");

        if(!controller.thereIsOrder(orderID)){
            System.out.println("There is no such order.");
        }
        else {
            System.out.println("There is such order.");
        }
        printOrderSummaryFromController(controller,orderID);//מדפיסה את המוצרים שהיו בהזמנה עם הכמות והמחיר
    }


}
