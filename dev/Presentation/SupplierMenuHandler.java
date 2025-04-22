package Presentation;
import Domain.Controller;
import java.util.Scanner;

public class SupplierMenuHandler {
    public static int createSupplier(Scanner scanner, Controller controller) {
        System.out.println("Let's create a new supplier!");

        int supplierID = Inputs.read_int(scanner, "Enter Supplier ID: ");

        System.out.print("Enter Supplier Name: ");
        String supplierName = scanner.next();

        int companyID = Inputs.read_int(scanner, "Enter Company ID: ");

        int bankAccount = Inputs.read_int(scanner, "Enter Bank Account: ");

        //===============================
        String paymentMethod = getPaymentMethod(scanner);
        //===============================

        int phoneNumber = Inputs.read_int(scanner, "Enter Phone Number: ");

        System.out.print("Enter Email: ");
        String email = scanner.next();

        //===============================
        String paymentCondition = getPaymentCondition(scanner);
        //===============================

        controller.createSupplier(supplierName, supplierID, companyID, bankAccount, paymentMethod, phoneNumber, email, paymentCondition);

        System.out.println("Supplier created successfully!");
        return supplierID;
    }


    public static String getPaymentMethod(Scanner scanner) {
        System.out.println("Select payment method:");
        System.out.println("1. Check");
        System.out.println("2. Cash");
        System.out.println("3. Bank Transfer");

        int choice = -1;
        while (choice < 1 || choice > 3) {
            System.out.print("Enter your choice (1-3): ");

            choice = Inputs.read_input_for_choice(scanner);

            if (choice < 1 || choice > 3) {
                System.out.println("Invalid choice. Please try again.");
            }
        }

        switch (choice) {
            case 1: return "Check";
            case 2: return "Cash";
            case 3: return "Bank Transfer";
            default: return "Unknown"; // זה לא אמור לקרות בגלל הלולאה
        }
    }


    public static String getPaymentCondition(Scanner scanner) {
        System.out.println("Select payment condition:");
        System.out.println("1. Prepaid");
        System.out.println("2. Pay at delivery");
        System.out.println("3. Standing order (Direct debit)");
        System.out.print("Enter your choice (1-3): ");

        int paymentChoice = Inputs.read_input_for_choice(scanner);
         // לניקוי תו מעבר שורה אם צריך

        switch (paymentChoice) {
            case 1:
                return "Prepaid";
            case 2:
                return "Pay at delivery";
            case 3:
                return "Standing order";
            default:
                System.out.println("Invalid choice. Defaulting to 'Unknown'.");
                return "Unknown";
        }
    }


    public static Integer getValidSupplierID(Scanner scanner, Controller controller) {

        int supplierID = Inputs.read_int(scanner, "Enter Supplier ID: ");

        if (!controller.thereIsSupplier(supplierID)) {
            System.out.println("Supplier does not exist.");
            return null; // מייצג כישלון
        }

//        System.out.println("Supplier found.");
        return supplierID; // מייצג הצלחה
    }

    public static void DeleteSupplier(Scanner scanner, Controller controller){
        System.out.println("OK Let's Delete a supplier!");
        Integer supplierID = getValidSupplierID(scanner, controller);
        if (supplierID != null) {
            controller.deleteSupplier(supplierID);
        }
    }

    public static void afterSupplierCreatedMenu(Scanner scanner, Controller controller, int supplier_ID) {/// ////////////////to do
        int choice = -1;
        while (choice != 2) {
            System.out.println("\nWhat would you like to do next?");
            System.out.println("1. Create a new agreement with this supplier");
            System.out.println("2. Return to main menu");
            System.out.print("Enter your choice: ");

            choice = Inputs.read_input_for_choice(scanner);//input checking

            switch (choice) {
                case 1:
                    //System.out.println("Creating a new agreement...");
                    AgreementMenuHandler.createNewAgreement(scanner,controller,supplier_ID);
                    break;
                case 2:
                    System.out.println("Returning to main menu.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public static void searchSupplierMenu(Scanner scanner,Controller controller) {
        System.out.println("Let's search supplier!");
        Integer supplierID = getValidSupplierID(scanner, controller);
        if(supplierID != null) {
            int choice = -1;
            while (choice != 0) {
                System.out.println("\nWhat would you like to do next?");
                System.out.println("1. Manage agreements for this supplier");
                System.out.println("2. Delete this supplier");
                System.out.println("0. Return to main menu");
                System.out.print("Enter your choice: ");

                choice = Inputs.read_input_for_choice(scanner);

                switch (choice) {
                    case 1:
                        System.out.println("Manage agreements...");
                        AgreementMenuHandler.agreementMenu(scanner, controller, supplierID);
                        break;
                    case 2:
                        DeleteSupplier(scanner, controller);
                        System.out.println("Supplier deleted.");
                        return;
                    case 0:
                        System.out.println("Return to Main Menu. ");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        }
    }


}
