package Presentation;
import Service.*;

import java.util.Scanner;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class Menu {
    public static Scanner scan;
    public static void main(String[] args) {
        DataController dataController = new DataController();
        String path = getPathFromConfig();
        dataController.ImportData(path);

        int choice = 0;

        scan = new Scanner(System.in);
        while (choice != 10) {
            String menu = """
                    Menu:
                    1.Add a new product to the stock\
                    
                    2.Remove a product from the stock\
                    
                    3.Show product Details\
                    
                    4.Show the purchase price of a product\
                    
                    5.Update a price of a product\
                    
                    6.Mark product as a defect\
                    
                    7.Generate an inventory report by category\
                    
                    8.Generate a defective products report\
                    
                    9.Update a discount\
                    
                    10.Exit""";

            System.out.println(menu);

            choice = scan.nextInt();


            if (choice != 10) {
                scan.nextLine();
            }

            int product_ID;

            switch (choice) {
                case 1: //Add a new product to the stock
                    boolean validCatalogNum = false;
                    while(!validCatalogNum) {
                        String productDetails = getProductDetails(); //from user
                        validCatalogNum = dataController.addProductController(productDetails);
                        if(!validCatalogNum){
                            System.out.println("Invalid catalog number. Please insert the product details again.");
                        }
                    }
                    break;
                case 2: //Remove a product from the stock
                    try {
                        System.out.println("Enter the ID of the product: ");
                        product_ID = scan.nextInt();
                        scan.nextLine();
                        int remove_choice = 3;
                        while (remove_choice != 1 && remove_choice != 2) {
                            System.out.println("(1)Purchase\n(2)Defect");
                            remove_choice = scan.nextInt();
                            scan.nextLine();
                            if (remove_choice == 1) { //Purchase
                                System.out.println("Enter the sale price for this product: ");
                                double price = scan.nextDouble();
                                scan.nextLine();
                                boolean alert = dataController.checkForAlert(product_ID);
                                //check if need alert
                                if (alert) {
                                    System.out.println("ALERT! " + dataController.getProductName(product_ID)
                                            + " has reached critical amount. Please order new supply.");
                                }
                                dataController.handlePurchaseProduct(product_ID, price);
                            } else if (remove_choice == 2) { //Defect
                                boolean alert = dataController.checkForAlert(product_ID);
                                if (alert) {
                                    System.out.println("ALERT! " + dataController.getProductName(product_ID)
                                            + " has reached critical amount. Please order new supply to this product.");
                                }
                                dataController.handleDefectProduct(product_ID);
                            } else {
                                System.out.println("Invalid choice. You can only enter 1 or 2");
                            }
                        }
                    }
                    catch (NullPointerException e){
                        System.out.println("This Product ID is not in the stock.");
                    }
                    break;
                case 3: //Show Product Details
                    try {
                        System.out.println("Enter product ID: ");
                        product_ID = scan.nextInt();
                        scan.nextLine();
                        String details = dataController.productsDetails(product_ID);
                        System.out.println(details);
                        break;
                    }
                    catch (NullPointerException e) {
                        System.out.println("This Product ID is not in tne stock.");
                    }
                case 4: //Show the purchase price of a product
                    try{
                        System.out.println("Enter the ID of the product:");
                        product_ID = scan.nextInt();
                        scan.nextLine();
                        double salePrice = dataController.getProductPurchasePrice(product_ID);
                        System.out.println("Product " + product_ID + " was sold for " + salePrice);
                    }
                    catch (NullPointerException e){
                        System.out.println("This Product ID was not purchased before.");
                    }
                    break;
                case 5: //Update a price of a product
                    try {
                        System.out.println("Enter product ID: ");
                        product_ID = scan.nextInt();
                        scan.nextLine();
                        System.out.println("New price: ");
                        double newPrice = scan.nextDouble();
                        scan.nextLine();
                        dataController.updatePriceController(product_ID, newPrice);
                    }
                    catch (NullPointerException e){
                        System.out.println("This Product ID not in stock.");
                    }
                    break;
                case 6:
                    try {
                        System.out.println("Enter product ID: ");
                        product_ID = scan.nextInt();
                        scan.nextLine();
                        dataController.markDefect(product_ID);
                        break;
                    }
                    catch (NullPointerException e){
                        System.out.println("This Product ID not in stock.");
                    }
                case 7: //Generate an inventory report by category
                    System.out.println("Enter a category");
                    String stringCategories = scan.nextLine(); //assume that the user writes the categories in this format: "Ca1 Ca2 Ca3..."
                    String[] categories = stringCategories.split(" ");
                    System.out.println("Inventory Report\n");
                    System.out.println(dataController.inventoryReportController(categories));

                case 8: //Generate a defective products report
                    System.out.println("Here is the defective report:");
                    System.out.println(dataController.defectReportController());
                    break;

                case 9: //Update Discount
                    System.out.println("Apply discount on:\n(1)Category\n(2)Sub-Category\n(3)Catalog Number");
                    choice = scan.nextInt();
                    scan.nextLine();
                    System.out.println("Enter Discount in %: ");
                    int discount = scan.nextInt();
                    scan.nextLine();
                    boolean continue_flag = true;
                    while (continue_flag) {
                        if (choice == 1) {
                            System.out.println("Enter the name of the Category: ");
                            String category = scan.nextLine();
                            dataController.setDiscountForCategory(category, discount);
                            continue_flag = false;
                        } else if (choice == 2) {
                            System.out.println("Enter Sub-Category: ");
                            String sub_category = scan.nextLine();
                            dataController.setDiscountForSubCategory(sub_category, discount);
                            continue_flag = false;
                        } else if (choice == 3) {
                            System.out.println("Enter Catalog Number: ");
                            int cNum = scan.nextInt();
                            scan.nextLine();
                            dataController.setDiscountForCatalogNum(cNum, discount);
                            continue_flag = false;
                        } else {
                            System.out.println("Invalid choice. Please enter 1, 2 or 3");
                        }
                    }
                    break;

                case 10: //Exit
                    System.out.println("Thank you! Have a nice day :) ");
                    break;
                default:
                    break;
            }
        }
    }

    public static String getProductDetails(){ //get all product details from user
        System.out.println("Enter the Product ID: ");
        int p_id = scan.nextInt();
        scan.nextLine();

        System.out.println("Enter the Product name: ");
        String p_name = scan.nextLine();

        System.out.println("Enter the product expiring date: ");
        String p_expiring_date = scan.nextLine();

        System.out.println("Enter the product location: ");
        String p_location = scan.nextLine();

        System.out.println("Enter the product section: ");
        String p_section = scan.nextLine();

        System.out.println("Enter the product catalog number: ");
        int p_catalog_number = scan.nextInt();
        scan.nextLine();
        System.out.println("Enter the product category: ");
        String p_category = scan.nextLine();

        System.out.println("Enter the product sub-category: ");
        String p_sub_category = scan.nextLine();

        System.out.println("Enter the product size: ");
        int p_size = scan.nextInt();

        System.out.println("Enter the product cost: ");
        double p_cost = scan.nextDouble();

        System.out.println("Enter the product demand: ");
        int p_demand = scan.nextInt();

        System.out.println("Enter the product supply time: ");
        int p_supply_time = scan.nextInt();

        scan.nextLine();
        System.out.println("Enter the product manufacturer: ");
        String p_manufacturer = scan.nextLine();

        System.out.println("Enter the product supplier discount: ");
        int p_supplier_discount = scan.nextInt();

        System.out.println("Enter the product store discount: ");
        int p_store_discount = scan.nextInt();

        return p_id + "," + p_name + "," + p_expiring_date + "," + p_location + "," + p_section + ","
                + p_catalog_number + "," + p_category + "," + p_sub_category + "," + p_size + ","
                + p_cost + "," + p_demand + "," + p_supply_time + ","
                + p_manufacturer + "," + p_supplier_discount + "," + p_store_discount;

    }

    public static String getPathFromConfig(){
        Yaml yaml = new Yaml();
        String path = "";
        try (InputStream inputStream = Menu.class.getClassLoader().getResourceAsStream("config.yaml")) {
            if (inputStream == null) {
                throw new IllegalArgumentException("file not found! " + "config.yaml");
            } else {
                // Parse the YAML file
                Map<String, Object> config = yaml.load(inputStream);
                // Access the 'path' value
                path = (String) config.get("path");
                return path;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;

    }

}


