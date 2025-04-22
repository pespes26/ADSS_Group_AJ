package Presentation;
import Domain.Controller;
import java.util.Scanner;

public class AgreementMenuHandler {
    public static Integer getValidAgreementID(Scanner scanner, Controller controller) {
        int agreementID = Inputs.read_int(scanner, "Enter Agreement ID: ");

        if (!controller.thereIsAgreement(agreementID)) {
            System.out.println("Agreement does not exist.");
            return null; // מייצג כישלון
        }

        System.out.println("Agreement found.");
        return agreementID; // מייצג הצלחה
    }

    public static void deleteAgreement(Scanner scanner, Controller controller) {
        Integer agreementID = getValidAgreementID(scanner, controller);
        if (agreementID != null) {
            controller.deleteAgreement(agreementID);
            System.out.println("Agreement deleted.");
        }
    }

    public static void createNewAgreement(Scanner scanner, Controller controller, int supplierID) {
        System.out.println("Let's create a new agreement...");

        int agreementID;
        while (true) {
            agreementID = Inputs.read_int(scanner, "Enter Agreement ID: ");
            if (!controller.thereIsAgreement(agreementID)) {
                break;
            }
            System.out.println("This Agreement ID already exists. Try another one.");
        }
        scanner.nextLine(); // קולט את ה־\n שנשאר אחרי nextInt()

        System.out.print("Enter Delivery Days (comma separated, e.g. Mon,Wed,Fri): ");
        String[] deliveryDays = scanner.nextLine().split(",");

        System.out.print("Self Pickup? (Y/N): ");
        String selfPickupInput = scanner.next();
        boolean selfPickup = selfPickupInput.equalsIgnoreCase("Y");

        controller.createAgreement(agreementID, supplierID, deliveryDays, selfPickup);
        System.out.println("Agreement created successfully!.");

        //new:

        System.out.print("Would you like to add products to the agreement? (Y/N): ");
        String addProductInput = scanner.next();

        if (addProductInput.equalsIgnoreCase("Y")) {
            addProductsLoop(scanner, controller, supplierID, agreementID);;
        }
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



    public static void editSpecificAgreementMenu(Scanner scanner, Controller controller, int supplierID) {
        System.out.println("Let's edit agreement...");
        Integer agreementID = getValidAgreementID(scanner, controller);
        if (agreementID != null) {
            int choice = -1;
            while (choice != 0) {
                System.out.println("\nWhat would you like to do next?");
                System.out.println("1. Add new product to this agreement ");
                System.out.println("2. remove product from this agreement ");
                System.out.println("3. Edit product supply terms ");
                System.out.println("4. Edit the delivery days ");
                System.out.println("5. Change selfPickup status ");
                System.out.print("0.Return to main menu: ");
                System.out.print("Enter your choice: ");

                choice = Inputs.read_input_for_choice(scanner);

                switch (choice) {
                    case 1:
                        System.out.println("Adding new product to agreement...");
                        ProductMenuHandler.addNewProduct(scanner, controller,supplierID, agreementID);
                        System.out.println("Product added successfully.\n");
                        break;

                    case 2:
                        System.out.println("Removing product from agreement...");
                        ProductMenuHandler.removeProduct(scanner, controller);
                        System.out.println("Product removed successfully.\n");
                        break;

                    case 3:
                        System.out.println("Editing product supply terms...");
                        ProductMenuHandler.editProductTerms(scanner, controller);
                        System.out.println("Supply terms updated successfully.\n");
                        break;

                    case 4:
                        System.out.println("Editing delivery days...");
                        editDeliveryDays(scanner, controller);
                        System.out.println("Delivery days updated.\n");
                        break;

                    case 5:
                        System.out.println("Toggling self-pickup status...");
                        controller.toggleSelfPickup(agreementID);
                        System.out.println("Self-pickup status changed.\n");
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
    public static void editDeliveryDays(Scanner scanner, Controller controller) {
        Integer agreementID =  getValidAgreementID(scanner, controller);
        if (agreementID != null) {
            System.out.print("Enter new delivery days (comma separated, e.g., Mon,Wed,Fri): ");
            String[] newDays = scanner.nextLine().split(",");
            controller.updateDeliveryDays(agreementID, newDays);
            System.out.println("Delivery days updated.");
        }
    }

    public static void toggleSelfPickup(Scanner scanner, Controller controller) {
        Integer agreementID =  getValidAgreementID(scanner, controller);
        controller.toggleSelfPickup(agreementID);
        System.out.println("Self-pickup status toggled.");
    }



    public static void agreementMenu(Scanner scanner, Controller controller, Integer supplierID) {
        if (supplierID == null) return;

        int choice;
        do {
            System.out.println("\nWhat would you like to do in the agreement menu?");
            System.out.println("1. Delete agreement");
            System.out.println("2. Add new agreement");
            System.out.println("3. Edit existing agreement");
            System.out.println("0. return to previous menu");
            System.out.print("Enter your choice: ");

            choice = Inputs.read_input_for_choice(scanner);//input checking

            switch (choice) {
                case 1 -> {
                    System.out.println("Deleting agreement...");
                    deleteAgreement(scanner, controller);
                }
                case 2 -> {
                    createNewAgreement(scanner, controller, supplierID);
                }
                case 3 -> {
//                    Integer agreementID = AgreementMenuHandler.getValidAgreementID(scanner, controller);
                    editSpecificAgreementMenu(scanner, controller, supplierID);
                }
                case 0 -> System.out.println("Returning to main menu...");
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 0);
    }
}

