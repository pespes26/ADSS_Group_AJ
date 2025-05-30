package Suppliers.Presentation;
import Suppliers.DTO.AgreementDTO;
import Suppliers.Domain.Controller;
import Suppliers.Domain.AgreementManagementController;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AgreementMenuHandler {
    public static AgreementManagementController agreementManagementController;

    public AgreementMenuHandler() {
        agreementManagementController = new AgreementManagementController();
    }



    public static void deleteAgreement(Scanner scanner, int supplierId) {
        List<AgreementDTO> agreementDTOList = agreementManagementController.getAgreementsBySupplierID(supplierId);

        if (agreementDTOList.isEmpty()) {
            System.out.println("No agreements found for supplier with ID: " + supplierId);
            return;
        }

        int counter = 1;
        System.out.println("List of agreements for supplier #" + supplierId + ":");

        for (AgreementDTO agreementDTO : agreementDTOList) {
            System.out.println(counter++ + ". Agreement ID: " + agreementDTO.getAgreement_ID());
            System.out.println("   Supplier ID: " + agreementDTO.getSupplier_ID());
            System.out.println("   Self Pickup: " + (agreementDTO.isSelfPickup() ? "Yes" : "No"));
            System.out.println("   Delivery Days: " + agreementDTO.getDeliveryDays());
            System.out.println("--------------------------------------------------");
        }

        System.out.print("Choose the number of the agreement you want to delete (1 to " + agreementDTOList.size() + "): ");

        int selectedIndex;
        try {
            selectedIndex = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        if (selectedIndex < 1 || selectedIndex > agreementDTOList.size()) {
            System.out.println("Invalid choice. Please select a number within the given range.");
            return;
        }

        AgreementDTO selectedAgreement = agreementDTOList.get(selectedIndex - 1);
        agreementManagementController.deleteAgreementWithSupplier(selectedAgreement.getAgreement_ID(), supplierId);

        System.out.println("Agreement deleted.");
    }

    public static void createNewAgreement(Scanner scanner, int supplierID) {
        System.out.println("Let's create a new agreement...");

        // קבלת ימי אספקה מהמשתמש
        String[] deliveryDays = getDeliveryDays(scanner);

        System.out.print("Self Pickup? (Y/N): ");
        String selfPickupInput = scanner.next();
        boolean selfPickup = selfPickupInput.equalsIgnoreCase("Y");

        AgreementDTO agreementDTO = new AgreementDTO(supplierID, deliveryDays,selfPickup);

        agreementManagementController.createAgreementWithSupplier(agreementDTO);
        System.out.println("Agreement created successfully!.");

        System.out.print("Would you like to add products to the agreement? (Y/N): ");
        String addProductInput = scanner.next();

        if (addProductInput.equalsIgnoreCase("Y")) {
            addProductsLoop(scanner, controller, supplierID, agreementID);
        }
    }


    public static String[] getDeliveryDays(Scanner scanner) {
        String[] weekDays = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        List<String> selectedDays = new ArrayList<>();

        System.out.println("Select delivery days (enter numbers separated by space, then press ENTER):");
        for (int i = 0; i < weekDays.length; i++) {
            System.out.printf("%d. %s%n", i + 1, weekDays[i]);
        }
        System.out.print("Your choices (e.g., 1 3 5): ");

        // כאן אנחנו מבטיחים שהשורה לא תיבלע
        scanner.nextLine();
        String input = scanner.nextLine(); // עכשיו יקלוט את הבחירה

        String[] parts = input.trim().split("\\s+");

        for (String part : parts) {
            try {
                int index = Integer.parseInt(part) - 1;
                if (index >= 0 && index < weekDays.length) {
                    selectedDays.add(weekDays[index]);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input skipped: " + part);
            }
        }

        return selectedDays.toArray(new String[0]);
    }



    public static void addProductsLoop(Scanner scanner, Controller controller, int supplierID, int agreementID) {
        String input;
        boolean firstTime = true;

        do {
            if (firstTime) {
                System.out.println("\nLet's add a product to the agreement:");
                firstTime = false;
            }

            ProductMenuHandler.addNewProduct(scanner, controller, supplierID, agreementID);

            System.out.print("Would you like to add another product? (Y/N): ");
            input = scanner.next();
        } while (input.equalsIgnoreCase("Y"));

        System.out.println("Finished adding products.\n");
    }


    public static AgreementDTO showAgreements(Scanner scanner, int supplierId) {
        List<AgreementDTO> agreementDTOList = agreementManagementController.getAgreementsBySupplierID(supplierId);

        if (agreementDTOList.isEmpty()) {
            System.out.println("No agreements found for supplier with ID: " + supplierId);
            return null;
        }

        int counter = 1;
        System.out.println("List of agreements for supplier #" + supplierId + ":");

        for (AgreementDTO agreementDTO : agreementDTOList) {
            System.out.println(counter++ + ". Agreement ID: " + agreementDTO.getAgreement_ID());
            System.out.println("   Supplier ID: " + agreementDTO.getSupplier_ID());
            System.out.println("   Self Pickup: " + (agreementDTO.isSelfPickup() ? "Yes" : "No"));
            System.out.println("   Delivery Days: " + agreementDTO.getDeliveryDays());
            System.out.println("--------------------------------------------------");
        }

        System.out.print("Choose the number of the agreement (1 to " + agreementDTOList.size() + "): ");

        int selectedIndex;
        try {
            selectedIndex = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return null;
        }

        if (selectedIndex < 1 || selectedIndex > agreementDTOList.size()) {
            System.out.println("Invalid choice. Please select a number within the given range.");
            return null;
        }

        AgreementDTO selectedAgreement = agreementDTOList.get(selectedIndex - 1);
        return selectedAgreement;
    }


    public static void editSpecificAgreementMenu(Scanner scanner, int supplierID) {
        System.out.println("Let's edit agreement...");
        AgreementDTO agreementDTO = showAgreements(scanner, supplierID);
        if (agreementDTO != null) {
            int agreementID = agreementDTO.getAgreement_ID();
            int choice = -1;
            while (choice != 0) {
                System.out.println("\nWhat would you like to do next?");
                System.out.println("1. Add new product to this agreement ");
                System.out.println("2. remove product from this agreement ");
                System.out.println("3. Edit product supply terms ");
                System.out.println("4. Edit the delivery days ");
                System.out.println("5. Change selfPickup status ");
                System.out.print("0.Return to main menu: ");
                System.out.print("Enter your choice: \n");

                choice = Inputs.read_input_for_choice(scanner);

                switch (choice) {
                    case 1:
                        System.out.println("Let's add a new product to the agreement...");
                        ProductMenuHandler.addNewProduct(scanner, controller, supplierID, agreementID);
                        System.out.println("Product added successfully.\n");
                        break;

                    case 2:
                        System.out.println("Let's remove a product from the agreement...");
                        ProductMenuHandler.removeProduct(scanner, controller, agreementID, supplierID);
                        break;

                    case 3:
                        System.out.println("Let's edit the product's supply terms...");
                        ProductMenuHandler.editProductTerms(scanner, controller, agreementID);
                        System.out.println("Supply terms updated successfully.\n");
                        break;

                    case 4:
                        System.out.println("Let's edit the delivery days...");
                        editDeliveryDays(scanner, agreementID);
                        System.out.println("Delivery days updated successfully.\n");
                        break;

                    case 5:
                        System.out.println("Toggling self-pickup status...");
                        toggleSelfPickup(scanner,agreementID, agreementDTO.getSelfPickup());
//                        System.out.println("Self-pickup status changed.\n");
                        break;

                    case 0:
                        System.out.println("Returning to main menu...");
                        break;

                    default:
                        System.out.println("Invalid choice. Please try again.\n");


                }
            }
        }
    }
    public static void editDeliveryDays(Scanner scanner,Integer agreementID) {
        if (agreementID != null) {
            String[] newDays = getDeliveryDays(scanner); // שימוש בפונקציה האינטראקטיבית
            agreementManagementController.setDeliveryDays(agreementID,newDays);
            System.out.println("Delivery days updated to: " + String.join(", ", newDays));
        }
    }


    public static void toggleSelfPickup(Scanner scanner,int agreementID, boolean currentSelfPickup) {
            boolean newStatus = ! currentSelfPickup;
            agreementManagementController.setSelfPickup(agreementID,newStatus);
            System.out.println("Self-pickup status updated. New status: " + (newStatus ? "Enabled" : "Disabled\n"));
    }




    public static void agreementMenu(Scanner scanner, Integer supplierID) {
        if (supplierID == null) return;

        int choice;
        do {
            System.out.println("\nWhat would you like to do in the agreement menu?");
            System.out.println("1. Delete agreement");
            System.out.println("2. Add new agreement");
            System.out.println("3. Edit existing agreement");
            System.out.println("0. return to previous menu\n");
            System.out.print("Enter your choice: ");

            choice = Inputs.read_input_for_choice(scanner);//input checking

            switch (choice) {
                case 1 -> {
                    System.out.println("Deleting agreement...");
                    deleteAgreement(scanner, supplierID);
                }
                case 2 -> {
                    createNewAgreement(scanner, supplierID);
                }
                case 3 -> {
//                    Integer agreementID = AgreementMenuHandler.getValidAgreementID(scanner, controller);
                    editSpecificAgreementMenu(scanner, supplierID);
                }
                case 0 -> System.out.println("Returning to previous menu...");
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 0);
    }
}

