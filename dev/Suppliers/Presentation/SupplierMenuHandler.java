package Suppliers.Presentation;
import Suppliers.DAO.*;
import Suppliers.DTO.SupplierDTO;
import Suppliers.Domain.*;
import Suppliers.Repository.ISupplierRepository;
import Suppliers.Repository.SupplierRepositoryImpl;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class SupplierMenuHandler {
//    public AgreementManagementController agreementManagementController;
    public static SupplierManagementController supplierManagementController;


    public SupplierMenuHandler() {
        // אתחול DAO-ים
        ISupplierDAO supplierDAO = new JdbcSupplierDAO();
        IAgreementDAO agreementDAO = new JdbcAgreementDAO();
        IProductSupplierDAO productSupplierDAO = new JdbcProductSupplierDAO();
        IDiscountDAO discountDAO = new JdbcDiscountDAO();

        // יצירת Repository
        ISupplierRepository supplierRepository = new SupplierRepositoryImpl(supplierDAO,agreementDAO,productSupplierDAO,discountDAO);
         this.supplierManagementController = new SupplierManagementController(supplierRepository);

    }


    public SupplierManagementController getSupplierManagementController() {
        return supplierManagementController;
    }

    public static int createSupplier(Scanner scanner) throws SQLException {
        System.out.println("Let's create a new supplier!");


        System.out.print("Enter Supplier Name: ");
        String supplierName = scanner.next();

        int companyID = Inputs.read_int(scanner, "Enter Company ID: ");

        int bankAccount = Inputs.read_int(scanner, "Enter Bank Account: ");

        //===============================
        String paymentMethod = getPaymentMethod(scanner);
        //===============================

        long phoneNumber = Inputs.read_long(scanner, "Enter Phone Number: ");

        System.out.print("Enter Email: ");
        String email = scanner.next();

        //===============================
        String paymentCondition = getPaymentCondition(scanner);
        //===============================
        SupplierDTO supplierDTO = new SupplierDTO(supplierName, companyID, bankAccount, paymentMethod, paymentCondition, phoneNumber, email);
        supplierManagementController.createSupplier(supplierDTO);

        System.out.println("Supplier created successfully!");
        return supplierDTO.getSupplier_id();
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






public void DeleteSupplier(Scanner scanner, int supplierIdToDelete) throws SQLException {
    System.out.println("OK, let's delete a supplier!");
//
//    List<SupplierDTO> supplierDTOList = supplierManagementController.getAllSuppliersDTOs();
//
//    if (supplierDTOList.isEmpty()) {
//        System.out.println("No suppliers found.");
//        return;
//    }
//
//    int counter = 1;
//    System.out.println("List of suppliers:");
//    for (SupplierDTO supplierDTO : supplierDTOList) {
//        System.out.println(counter++ + ". Supplier ID: " + supplierDTO.getSupplier_id());
//        System.out.println("   Supplier Name: " + supplierDTO.getSupplierName());
//        System.out.println("   Company ID: " + supplierDTO.getCompany_id());
//        System.out.println("----------------------------------");
//    }
//
//    System.out.print("Choose the number of the supplier to delete (1 to " + supplierDTOList.size() + "): ");
//    int selectedIndex;
//
//    try {
//        selectedIndex = Integer.parseInt(scanner.nextLine());
//    } catch (NumberFormatException e) {
//        System.out.println("Invalid input. Please enter a number.");
//        return;
//    }
//
//    if (selectedIndex < 1 || selectedIndex > supplierDTOList.size()) {
//        System.out.println("Invalid choice. Please select a number within the given range.");
//        return;
//    }
//
//    SupplierDTO selectedSupplier = supplierDTOList.get(selectedIndex - 1);
//    int supplierIdToDelete = selectedSupplier.getSupplier_id();

    supplierManagementController.deleteSupplier(supplierIdToDelete);
    System.out.println("Supplier with ID " + supplierIdToDelete + " has been deleted.");
}


    public static void afterSupplierCreatedMenu(Scanner scanner, int supplier_ID) throws SQLException {/// ////////////////to do
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
                    AgreementMenuHandler.createNewAgreement(scanner,supplier_ID);
                    break;
                case 2:
                    System.out.println("Returning to main menu.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public static SupplierDTO showSuppliers(Scanner scanner) throws SQLException {

        List<SupplierDTO> supplierDTOList = supplierManagementController.getAllSuppliersDTOs();

        if (supplierDTOList.isEmpty()) {
            System.out.println("No suppliers found.");
            return null;
        }

        if (scanner.hasNextLine()) scanner.nextLine();


        System.out.println("List of suppliers:");
        for (int i = 0; i < supplierDTOList.size(); i++) {
            SupplierDTO dto = supplierDTOList.get(i);
            System.out.println((i + 1) + ". Supplier ID: " + dto.getSupplier_id());
            System.out.println("   Supplier Name: " + dto.getSupplierName());
            System.out.println("   Company ID: " + dto.getCompany_id());
            System.out.println("--------------------------------------------------");
        }

        int selectedIndex = -1;

        while (true) {
            System.out.print("Choose the number of the supplier (1 to " + supplierDTOList.size() + "): ");
            String input = scanner.nextLine();

            try {
                selectedIndex = Integer.parseInt(input);

                if (selectedIndex >= 1 && selectedIndex <= supplierDTOList.size()) {
                    break;  // קלט תקין
                } else {
                    System.out.println("Invalid choice. Please select a number within the range.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }

        return supplierDTOList.get(selectedIndex - 1);
    }



    public void searchSupplierMenu(Scanner scanner) throws SQLException {
        System.out.println("Let's search supplier!");
        SupplierDTO supplierDTO = showSuppliers(scanner);

        if (supplierDTO == null) {
            System.out.println("No valid supplier selected. Returning to main menu.");
            return;
        }


        int supplierID = supplierDTO.getSupplier_id();
        if(supplierDTO != null) {
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
                        AgreementMenuHandler.agreementMenu(scanner, supplierID);
                        break;
                    case 2:
                        DeleteSupplier(scanner, supplierID);
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
