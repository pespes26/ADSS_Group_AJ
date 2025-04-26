package PresentationLayer;

import DomainLayer.HRManager;
import DomainLayer.ControllerManager;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        HRManager hr = new HRManager();
        ControllerManager.setHRManager(hr);

        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome! Please select an option:");
        System.out.println("1. Load sample data");
        System.out.println("2. Start manually");

        int choice = 0;
        while (choice != 1 && choice != 2) {
            System.out.print("Enter choice (1 or 2): ");
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
            } else {
                scanner.next();
            }
        }

        if (choice == 1) {
            SampleDataLoader.loadSampleData(hr);
            System.out.println("Sample data loaded successfully.");
        } else {
            System.out.println("Starting manual data entry...");
        }

        UI ui = new UI();
        ui.mainLoginMenu();
    }
}
