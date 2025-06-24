package com.superli.deliveries.application;

import java.util.Scanner; // DELIVERIES

import com.superli.deliveries.presentation.HR.DataSeeder;
import com.superli.deliveries.presentation.HR.HRDetailsView; // HR

public class MainApp {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        DataSeeder.seedAllData();
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
