package Presentation;

import Service.*;

import java.util.Arrays;
import java.util.Scanner;
import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.util.Map;

/**
 * Presentation layer of the application.
 * Handles user input/output and interacts with the Service layer (DataController).
 * Provides a console-based menu for managing stock, viewing product details,
 * updating prices, generating reports, and more.
 */
public class Menu {
    /** Global scanner object used for user input */
    public static Scanner scan;

    /**
     * Entry point of the program.
     * Loads data from the YAML-configured CSV file, and displays a menu to the user
     * for managing the inventory system.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        DataController dataController = new DataController();
        String path = getPathFromConfig();
        dataController.ImportData(path);

        int choice = 0;

        scan = new Scanner(System.in);
        while (choice != 14) {
            String menu = """
                    Menu:
                    1.Show item details\
                    
                    2.Add a new product\
                    
                    3.Remove a product\
                    
                    4.Show the purchase prices of a product\
                    
                    5.Update the cost price of a product\
                    
                    6.Mark item as a defect\
                    
                    7.Generate inventory report by categories\
                    
                    8.Generate a defective and expired products report\
                    
                    9.Set a discount\
                    
                    10.Show the current quantity of a product in warehouse and store\
                    
                    11.Show low-stock products that need reordering\
                    
                    12.Change product supply details\
                    
                    13.Change product location\
                    
                    14.Exit""";

            System.out.println();
            System.out.println(menu);

            choice = scan.nextInt();


            if (choice != 12) {
                scan.nextLine();
            }

            int product_ID;

            switch (choice) {
                case 1: //Show item details
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
                        break;
                    }

                case 2: //Add a new product
                    boolean validCatalogNum = false;
                    while(!validCatalogNum) {
                        String productDetails = getProductDetails(); //from user
                        validCatalogNum = dataController.addProductController(productDetails);
                        if(!validCatalogNum){
                            System.out.println("Invalid catalog number. Please insert the product details again.");
                        }
                    }
                    break;
                case 3: // Remove a product
                    System.out.println("Enter the ID of the product that you want to remove: ");
                    product_ID = scan.nextInt();
                    scan.nextLine();

                    if (!dataController.productExists(product_ID)) {
                        System.out.println("This Product ID is not in the stock.");
                        break;
                    }

                    int reason = 0;
                    while (reason != 1 && reason != 2) {
                        System.out.println("What is the reason for removing the product?");
                        System.out.println("(1)Purchase\n(2)Defect");
                        reason = scan.nextInt();
                        scan.nextLine();

                        if (reason == 1) {
                            System.out.println("Enter the sale price for this product: ");
                            double price = scan.nextDouble();
                            scan.nextLine();

                            boolean alert = dataController.checkForAlert(product_ID);
                            if (alert) {
                                System.out.println("ALERT! " + dataController.getProductName(product_ID)
                                        + " has reached critical amount. Please order new supply.");
                            }

                            dataController.handlePurchaseProduct(product_ID, price);
                        } else if (reason == 2) {
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
                    break;

                case 4: //Show the purchase prices of a product
                    try {
                        System.out.println("Enter the catalog number of the product:");
                        int catalogNumber = scan.nextInt();
                        scan.nextLine();
                        String result = dataController.getPurchasePricesByCatalogNumber(catalogNumber);
                        System.out.println(result);
                    } catch (Exception e) {
                        System.out.println("An error occurred while retrieving purchase prices.");
                    }
                    break;
                case 5: // Update the cost price of a product
                    try {
                        System.out.println("Enter product catalog number: ");
                        int catalogNumber = scan.nextInt();
                        scan.nextLine();

                        System.out.println("Enter the new cost price: ");
                        double newPrice = scan.nextDouble();
                        scan.nextLine();

                        dataController.updateCostPriceByCatalogNumber(catalogNumber, newPrice);
                    } catch (Exception e) {
                        System.out.println("Something went wrong while updating the price.");
                    }
                    break;
                case 6: //Mark item as a defect
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
                case 7: //Generate inventory report by categories
                    System.out.println("Enter category names, separated by commas (e.g., Beverages,Meat & Fish):");
                    String stringCategories = scan.nextLine();
                    String[] categories = Arrays.stream(stringCategories.split(","))
                            .map(String::trim)
                            .toArray(String[]::new);
                    System.out.println(dataController.inventoryReportByCategories(categories));
                    break;

                case 8: // Generate a defective and expired products report
                    System.out.println("Here is the defective and expired products report:");
                    System.out.println(dataController.defectAndExpiredReport());
                    break;

                case 9: //Set a Discount
                    System.out.println("Apply discount on:\n(1)Category\n(2)Sub-Category\n(3)Catalog Number(4)Supplier Discount");
                    choice = scan.nextInt();
                    scan.nextLine();
                    System.out.println("Enter the discount in %: ");
                    int discount = scan.nextInt();
                    scan.nextLine();
                    boolean continue_flag = true;
                    while (continue_flag) {
                        if (choice == 1) {
                            System.out.println("Enter the name of the category: ");
                            String category = scan.nextLine();
                            dataController.setDiscountForCategory(category, discount);
                            continue_flag = false;
                        } else if (choice == 2) {
                            System.out.println("Enter the name of the sub-category: ");
                            String sub_category = scan.nextLine();
                            dataController.setDiscountForSubCategory(sub_category, discount);
                            continue_flag = false;
                        } else if (choice == 3) {
                            System.out.println("Enter the catalog number: ");
                            int cNum = scan.nextInt();
                            scan.nextLine();
                            dataController.setDiscountForCatalogNumber(cNum, discount);
                            continue_flag = false;
                        } else if (choice == 4) {
                            System.out.println("Enter the catalog number: ");
                            int cNum = scan.nextInt();
                            scan.nextLine();
                            dataController.setSupplierDiscount(cNum, discount);
                            continue_flag = false;
                        }


                        else {
                            System.out.println("Invalid choice. Please enter 1, 2 or 3");
                        }
                    }
                    break;


                case 10: // Show the current quantity of a product in warehouse and store
                    System.out.println("Enter catalog number:");
                    int catalog_number = scan.nextInt();
                    scan.nextLine();
                    System.out.println(dataController.showCurrentAmountPerLocationByCatalogNumber(catalog_number));
                    break;

                case 11: // Show low-stock products that need reordering
                    System.out.println("Low-stock products (below their minimum amount):");
                    System.out.println(dataController.generateReorderAlertReport());
                    break;

                case 12://change product supply details
                    try {
                    System.out.println("Enter product catalog number: ");
                    int catalogNumber = scan.nextInt();
                    scan.nextLine();

                    System.out.println("Enter the product supply time: ");
                    int supply_time = scan.nextInt();
                    scan.nextLine();

                    System.out.println("Enter the product demand:");
                    int demand = scan.nextInt();
                    scan.nextLine();

                    dataController.updateProductSupplyDetails(catalogNumber,supply_time,demand);
                    } catch (Exception e) {
                        System.out.println("Something went wrong while updating the details.");
                    }
                    break;
                case 13://change Product location
                    try {
                    System.out.println("Enter product ID: ");
                    int id_num = scan.nextInt();
                    scan.nextLine();

                    System.out.println("Enter the product location: ");
                    String loc = scan.nextLine();
                    scan.nextLine();

                    System.out.println("Enter the product section: ");
                    String sec = scan.nextLine();
                    scan.nextLine();
                    dataController.updateProductLocation(id_num,loc,sec);
                } catch (Exception e) {
                System.out.println("Something went wrong while updating the location.");
                }
                break;



                case 14: //Exit
                    System.out.println("Thank you! Have a nice day :) ");
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Collects product details from user input, and returns them as a single comma-separated string.
     *
     * @return A CSV-style string containing all product fields, in the expected order
     */
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

    /**
     * Loads the product data file path from the YAML configuration file ("config.yaml").
     *
     * @return the path to the CSV data file, or empty string if loading fails
     */
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


