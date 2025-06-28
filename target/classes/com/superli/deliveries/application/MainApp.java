package com.superli.deliveries.application;

import java.sql.SQLException;
import java.util.Scanner;

import com.superli.deliveries.util.Database;
import com.superli.deliveries.util.DatabaseResetUtil;
import com.superli.deliveries.presentation.HR.DataSeeder;
import com.superli.deliveries.presentation.HR.HRDetailsView;

public class MainApp {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // תפריט ראשוני – לבחור איך להפעיל את המערכת
        System.out.println("=== System Startup Options ===");
        System.out.println("1. Run with prepared sample data (DataSeeder)");
        System.out.println("2. Start with empty database (manual setup)");
        System.out.print("Choose option: ");
        String setupChoice = sc.nextLine().trim();

        switch (setupChoice) {
            case "1":
                DataSeeder.seedAllData(); // מילוי נתונים מוכנים
                break;

            case "2":
                // הפעל את מחלקת Database כדי ליצור טבלאות
                try {
                    Database.getConnection(); // מחמם את הסכימה
                } catch (SQLException e) {
                    System.err.println("Error initializing database: " + e.getMessage());
                    System.exit(1);
                }

                // לאחר מכן – נקה את הטבלאות הקיימות
                DatabaseResetUtil.resetAllTables();
                System.out.println("All tables cleared. Starting with empty database.");
                break;


            default:
                System.out.println("Invalid choice. Exiting.");
                System.exit(1);
        }

        // תפריט ראשי של המערכת
        while (true) {
            System.out.println("\n=== Main System Menu ===");
            System.out.println("1. HR Management System");
            System.out.println("2. Deliveries Management System");
            System.out.println("0. Exit");
            System.out.print("Choose system: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    HRDetailsView hrSystem = new HRDetailsView();
                    hrSystem.mainLoginMenu();
                    break;

                case "2":
                    Main.runDeliveriesSystem();
                    break;

                case "0":
                    System.out.println("Exiting program. Goodbye!");
                    System.exit(0);

                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
