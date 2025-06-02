package ControllerInventorySupplier;

import Inventory.DAO.IPeriodicOrderDAO;
import Inventory.DTO.PeriodicOrderDTO;
import Inventory.DTO.ShortageOrderDTO;
import Inventory.Repository.IPeriodicOrderRepository;
import Inventory.Repository.IShortageOrderRepository;
import Inventory.Repository.PeriodicOrderRepositoryImpl;
import Inventory.Repository.ShortageOrderRepositoryImpl;


import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class SharedMenu {

    public static void run(Scanner scanner) throws SQLException {


        int choice = -1;
        while (choice != 5) {
            System.out.println("\n=========== Shared Inventory-Supplier Menu ===========");
            System.out.println("1. Show all periodic orders");
            System.out.println("2. Show all shortage-based orders");
            System.out.println("3. Check and place shortage-based order if needed");
            System.out.println("4. Add a new periodic order for a product");
            System.out.println("5. Back to main menu");
            System.out.print("Enter your choice (1-5): ");

            choice = getValidatedInt(scanner);

            switch (choice) {
                case 1 -> displayAllPeriodicOrders();
                case 2 -> displayAllShortageOrders();
                case 3 -> System.out.println("still not implemented");
                case 4 -> System.out.println("still not implemented");
                case 5 -> System.out.println("Returning to main menu...");
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void displayAllPeriodicOrders() {
        IPeriodicOrderRepository periodicOrderRepository = new PeriodicOrderRepositoryImpl();

        try {
            List<PeriodicOrderDTO> orders = periodicOrderRepository.getAllPeriodicOrders();

            if (orders.isEmpty()) {
                System.out.println("📭 No periodic orders found in the system.");
                return;
            }

            System.out.println("📋 Periodic Orders List:");
            System.out.println("----------------------------------------------------");

            for (PeriodicOrderDTO order : orders) {
                System.out.printf("""
                    🆔 Order ID: %d
                    📦 Catalog Number: %d
                    🔢 Quantity: %d
                    🏢 Supplier ID: %d
                    📃 Agreement ID: %d
                    📅 Days in Week: %s
                    📆 Order Date: %s
                    
                    """,
                        order.getOrderId(),
                        order.getProductCatalogNumber(),
                        order.getQuantity(),
                        order.getSupplierId(),
                        order.getAgreementId(),
                        order.getDaysInTheWeek(),
                        order.getOrderDate()
                );
            }

        } catch (SQLException e) {
            System.err.println("❌ Error retrieving periodic orders: " + e.getMessage());
        }
    }

    private static void displayAllShortageOrders() {
        IShortageOrderRepository shortageOrderRepo = new ShortageOrderRepositoryImpl();

        try {
            List<ShortageOrderDTO> orders = shortageOrderRepo.getAll(); // כרגע תחזיר רשימה ריקה

            if (orders.isEmpty()) {
                System.out.println("📭 No shortage-based orders found in the system.");
                return;
            }

            System.out.println("📋 Shortage-Based Orders List:");
            System.out.println("----------------------------------------------------");

            for (ShortageOrderDTO order : orders) {
                System.out.printf("""
                🆔 Order ID: %d
                📦 Catalog Number: %d
                🔢 Quantity: %d
                💰 Cost Price (Before Discount): %.2f ₪
                💸 Supplier Discount: %.2f %%
                📆 Order Date: %s
                
                """,
                        order.getOrderId(),
                        order.getProductCatalogNumber(),
                        order.getQuantity(),
                        order.getCostPriceBeforeSupplierDiscount(),
                        order.getSupplierDiscount(),
                        order.getOrderDate()
                );
            }

        } catch (SQLException e) {
            System.err.println("❌ Error retrieving shortage-based orders: " + e.getMessage());
        }
    }


    private static int getValidatedInt(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. Please enter a number: ");
            scanner.next();
        }
        int value = scanner.nextInt();
        scanner.nextLine(); // consume newline
        return value;
    }
}
