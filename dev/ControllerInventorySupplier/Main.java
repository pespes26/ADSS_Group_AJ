package ControllerInventorySupplier;

import Inventory.Controllers.Menu;
import Inventory.Controllers.MenuController;

import Inventory.Domain.InventoryController;
import Suppliers.Presentation.MainMenu;

//import dev.Suppliers.DataBase.DatabaseConnection;
//import dev.Suppliers.DataBase.SupllierCreatDb;
//import dev.Suppliers.DataBase.main_supplier;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws SQLException {  //
        Scanner scanner = new Scanner(System.in);
        //DatabaseConnection.connect();
        //SupllierCreatDb.createTables();
        //DatabaseConnection.main();
        //Menu.initializeInventorySystem();


        System.out.println("Welcome! Would you like to manage Inventory or Supplier?");
        System.out.println("Type '1' for Inventory or '2' for Supplier:");

        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                System.out.println("You have selected Inventory.");

                // יצירת InventoryController
                InventoryController inventoryController = new InventoryController();

                // הזנת מזהה סניף
                System.out.print("Enter your Branch ID (number between 1 and 10): ");
                int branchId = scanner.nextInt();
                scanner.nextLine(); // צריכת ENTER

                // הפעלת תפריט המלאי
                MenuController inventoryMenu = new MenuController(inventoryController, branchId);
                inventoryMenu.runMenu();
                break;
            case 2:
                System.out.println("You have selected Supplier.");
                Suppliers.Presentation.MainMenu.main(new String[0]);
                break;
            default:
                System.out.println("Invalid choice! Please run the program again.");
        }

        scanner.close();
    }
}