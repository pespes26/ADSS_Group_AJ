//package Suppliers.Presentation;
//import Suppliers.Domain.Controller;
//import Suppliers.Domain.OrderByShortageController;
//import Suppliers.Domain.OrderManagementController;
//import Suppliers.dataaccess.DAO.IDiscountDAO;
//import Suppliers.dataaccess.DAO.IOrderDAO;
//import Suppliers.dataaccess.DAO.JdbcDiscountDAO;
//import Suppliers.dataaccess.DAO.JdbcOrderDAO;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Scanner;
//import java.time.LocalDateTime;           // נוסיף בתחילת הקובץ
//import java.time.format.DateTimeFormatter;
//
//public class OrderMenuHandler {
//    private final OrderManagementController orderManagementController;
//
//    public OrderMenuHandler() {
//        IOrderDAO orderDAO = new JdbcOrderDAO();
//        orderManagementController = new OrderManagementController(orderDAO);
//    }
//
//    //    public static void createOrder(Scanner scanner, Controller controller) {
////        if (controller.existsZeroProducts()) {
////            System.out.println("No products exist in the system!");
////        } else {
////
////            System.out.println("\nStarting a new order...");
////            System.out.println("Before we begin, please enter a few details:\n");
////
////            //order id to a new order
////            int orderID;
////
////            while (true) { // לולאה שתרוץ עד שהמשתמש יזין מזהה הזמנה חוקי
////                orderID = Inputs.read_int(scanner, "Enter Order ID: ");
////                if (!controller.thereIsOrder(orderID)) { // בדיקה האם כבר קיימת הזמנה עם אותו מזהה
////                    break;
////                }
////                System.out.println("This Order ID already exists. Try another one.");
////            }
////
////            //phone number
////            int phoneNumber = Inputs.read_int(scanner, "Enter phone number: ");//input checking
////
////
////            // === שימוש בזמן נוכחי של המחשב ===
////            LocalDateTime orderDate = LocalDateTime.now();  // תאריך+שעה נוכחיים
////            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
////            System.out.println("Order date is set to: " + orderDate.format(formatter)); // הודעה למשתמש
////
////
////            Map<Integer, Integer> productsInOrder = new HashMap<>();
////            System.out.println("Let's create an order!\n");
////            int option = -1;
////
////            while (option != 0) {
////                System.out.println("\n========== Order Menu ==========");
////                System.out.print("Choose how to proceed:\n");
////                System.out.println("1. Add new product to order");
////                System.out.println("2. Submit order and return to main menu");
////                System.out.println("0. Cancel order and return to previous menu");
////                System.out.print("Enter your choice: ");
////
////                option = Inputs.read_input_for_choice(scanner);
////
////                switch (option) {
////                    case 1:
////                        int productID;
////                            productID = Inputs.read_int(scanner, "Enter Product ID: ");
////                            boolean existsProduct = ProductMenuHandler.validateProductExistsByID(controller, productID);
////                        if (!existsProduct) {
////                            System.out.println("Returning to order menu.");
////                            break; // חזרה לתפריט הראשי של ההזמנה
////                        }
////
////
////                        int quantity = Inputs.read_int(scanner, "Enter quantity: ");
////
////                        productsInOrder.put(productID, quantity);
////                        System.out.println(" Product added to order.");
////                        break;
////
////                    case 2:
////                        Map<Integer, Map.Entry<Integer, Double>> bestPrice = controller.orderWithBestPrice(productsInOrder);
////                        printProductsWithBestPrice(bestPrice);
//////                        controller.createOrder(orderID, phoneNumber, orderDate, productsInOrder,);//יוצר מופע של הזמנה/======================================
////                        System.out.println("\nOrder submitted successfully.");
////                        return;
////                    case 0:
////                        System.out.println("\nReturning to previous menu...");
////                        break;
////                    default:
////                        System.out.println("Invalid choice. Please try again.");
////                }
////            }
////        }
////    }
////
////
////    public static void printProductsWithBestPrice(Map<Integer, Map.Entry<Integer, Double>> bestPriceMap) {
////        System.out.println("\nProducts with best supplier price:");
////        System.out.printf("%-12s %-10s %-10s%n", "Product ID", "Amount", "Best Price");
////        System.out.println("---------------------------------------------");
////
////        double totalCost = 0;
////
////        for (Map.Entry<Integer, Map.Entry<Integer, Double>> entry : bestPriceMap.entrySet()) {
////            int productID = entry.getKey();
////            int amount = entry.getValue().getKey();
////            double price = entry.getValue().getValue();
////
////            double productCost = amount * price;
////            totalCost += productCost;
////
////            System.out.printf("%-12d %-10d %-10.2f%n", productID, amount, price);
////        }
////
////        System.out.println("---------------------------------------------");
////        System.out.printf("Total order cost: %.2f ₪%n\n", totalCost);
////    }
////
////
////    public static void printOrderSummaryFromController(Controller controller, int orderID) {
////
////        Map<Integer, Integer> productsInOrder = controller.getProductsInOrder(orderID);
////        if (productsInOrder == null || productsInOrder.isEmpty()) {
////            System.out.println("No products in this order.");
////            return;
////        }
////
////        System.out.println("\n========== Order Summary ==========");
////        System.out.println("Order ID      : " + orderID);
////        System.out.println("Phone Number  : " + controller.getPhoneNumber(orderID));
////        System.out.println("Order Date    : " + controller.getFormattedOrderDate(orderID));
////
////        System.out.println("\nOrdered Products:");
////        System.out.printf("%-15s %-10s%n", "Product ID", "Quantity");
////        System.out.println("------------------------------");
////
////        for (Map.Entry<Integer, Integer> entry : productsInOrder.entrySet()) {
////            System.out.printf("%-15d %-10d%n", entry.getKey(), entry.getValue());
////        }
////    }
//
//
//    public static void printPastOrder(Scanner scanner) {
//        orderManagementController
//    }
//
//    public static void SearchPastOrder(Scanner scanner,Controller controller){
//        int orderID = Inputs.read_int(scanner, "Enter Order ID: ");
//
//        if(!controller.thereIsOrder(orderID)){
//            System.out.println("There is no such order.");
//        }
//        else {
//            System.out.println("There is such order.");
//        }
//        printOrderSummaryFromController(controller,orderID);//מדפיסה את המוצרים שהיו בהזמנה עם הכמות והמחיר
//    }
//
//
//}


package Suppliers.Presentation;

import Suppliers.DTO.OrderDTO;
import Suppliers.DTO.OrderItemDTO;
import Suppliers.Domain.OrderManagementController;
import Suppliers.dataaccess.DAO.IOrderDAO;
import Suppliers.dataaccess.DAO.JdbcOrderDAO;

import java.sql.SQLException;
import java.util.List;
import java.time.format.DateTimeFormatter;

public class OrderMenuHandler {
    private final OrderManagementController orderManagementController;

    public OrderMenuHandler() {
        IOrderDAO orderDAO = new JdbcOrderDAO();
        this.orderManagementController = new OrderManagementController(orderDAO);
    }

    public void printPastOrder() throws SQLException {
        List<OrderDTO> orderDTOList = orderManagementController.getAllOrders();

        if (orderDTOList == null || orderDTOList.isEmpty()) {
            System.out.println("No previous orders found.");
            return;
        }

        System.out.println("===== Past Orders =====");

        for (OrderDTO order : orderDTOList) {
            System.out.println("Order ID: " + order.getOrderID());
            System.out.println("Customer Phone: " + order.getPhoneNumber());
            System.out.println("Order Date: " + order.getOrderDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            System.out.println("Items:");

            for (OrderItemDTO item : order.getItems()) {
                System.out.println("  ➤ Product ID: " + item.getProductId());
                System.out.println("     Quantity: " + item.getQuantity());
                System.out.println("     Supplier ID: " + item.getSupplierId());
            }

            System.out.println("-------------------------------");
        }
    }
}

