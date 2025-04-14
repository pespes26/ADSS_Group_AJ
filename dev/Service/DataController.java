package Service;
import Domain.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;


/**
 * The DataController class is part of the Service layer and manages all operations related to product data,
 * including importing, updating inventory, handling purchases, managing defects, and generating reports.
 */
public class DataController {
    private HashMap<Integer,Product> products; //Saves all the current products in the store. Key: product ID, Value: an object of product
    private HashMap<Integer,Product> purchase_products; //Saves all purchase products
    private HashMap<String, HashMap<String, HashMap<String, HashMap<String, Integer>>>> productsAmountMapByCategory; //Saves all product quantities in the following format:: Map<category, Map<sub-category,Map<size, Map<location, amount>>>> (location- wareHouse, interiorStore)
    private Set<Integer> catalog_numbers_Set; //Saves all the catalogs numbers we have in store

    public DataController(){
        products = new HashMap<>();
        purchase_products = new HashMap<>();
        productsAmountMapByCategory = new HashMap<>();
        catalog_numbers_Set = new HashSet<>();
    }

    /**
     * Imports product data from a CSV file and populates the products and inventory maps.
     *
     * @param path the path to the CSV file
     */
    public void ImportData(String path){
        try (CSVReader reader = new CSVReader(new FileReader(path))) {
            String[] nextRecord;
            while ((nextRecord = reader.readNext()) != null) { //reading record by record
                Product product = new Product();
                String input_id = nextRecord[0];
                if (input_id.startsWith("\uFEFF")) { //remove useless chars from csv file
                    input_id = input_id.substring(1);
                }
                input_id = input_id.trim();
                nextRecord[0] = input_id;
                setProductDetails(product, nextRecord); //build product
                catalog_numbers_Set.add(product.getClassification().getCatalogNumber()); //add catalog number to catalog set
                products.put(product.getProductId(), product);

                //add to productsAmountMap
                String category = product.getClassification().getCategory();
                String subCategory = product.getClassification().getSubcategory();
                String size = String.valueOf(product.getClassification().getProductSize());
                String location = product.getStored(); //interiorStore or wareHouse

                updateProductInventoryCount(true, category, subCategory,size,location);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates the count of a product in the inventory map.
     *
     * @param add        whether to increment (true) or decrement (false) the count
     * @param category   the product's category
     * @param sub_category the product's sub-category
     * @param size       the product size (as string)
     * @param location   the location of the product ("wareHouse" or "interiorStore")
     */
    public void updateProductInventoryCount(boolean add, String category, String sub_category, String size, String location) {
        // Check if the category exists
        if (!productsAmountMapByCategory.containsKey(category)) {
            productsAmountMapByCategory.put(category, new HashMap<>());
        }

        // Check if the sub category exists
        HashMap<String, HashMap<String, HashMap<String, Integer>>> subCategoryMap = productsAmountMapByCategory.get(category);
        if (!subCategoryMap.containsKey(sub_category)) {
            subCategoryMap.put(sub_category, new HashMap<>());
        }

        // Check if the size exists
        HashMap<String, HashMap<String, Integer>> size_map = subCategoryMap.get(sub_category);
        if (!size_map.containsKey(size)) {
            size_map.put(size, new HashMap<>());
        }

        HashMap<String, Integer> location_map = size_map.get(size);
        if (add) { //increment
            location_map.put(location, location_map.getOrDefault(location, 0) + 1);
            if (location.equals("wareHouse")) {
                location_map.put("interiorStore", 0); //initiate interior store with 0
            } else {
                location_map.put("wareHouse", 0); //initiate wareHouse with 0
            }
        } else { //decrement
            location_map.put(location, location_map.getOrDefault(location, 0) - 1);
        }
    }


    /**
     * Sets the details of a product object from a provided array of strings.
     *
     * @param product  the product object to populate
     * @param details  array of 15 strings representing the product fields
     */
    public void setProductDetails(Product product, String[] details){
        product.setProductId(Integer.parseInt(details[0]));
        product.setProductName(details[1]);
        product.setExpiringDate(details[2]);
        product.setStored(details[3]);
        product.setSection(details[4]);

        Classification classification = new Classification();
        classification.setCatalogNum(Integer.parseInt(details[5]));
        classification.setCategory(details[6]);
        classification.setSubcategory(details[7]);
        classification.setProductSize(Integer.parseInt(details[8]));
        classification.setCostPrice(Double.parseDouble(details[9]));
        classification.setProductDemand(Integer.parseInt(details[10]));
        classification.setSupplyTime(Integer.parseInt(details[11]));
        classification.setMinAmountForAlert((int)(0.5*classification.getProductDemand() + 0.5*classification.getSupplyTime()));
        classification.setManufacturer(details[12]);
        classification.setSupplierDiscount(Integer.parseInt(details[13]));
        classification.setStoreDiscount(Integer.parseInt(details[14]));

        product.setClassification(classification);
    }

    /**
     * Adds a product to the system after validating its catalog number and classification.
     *
     * @param product_details  a CSV-style string with all product details
     * @return true if the product was added successfully, false if the catalog number is invalid
     */
    public boolean addProductController(String product_details) {
        Product product = new Product();
        String[] product_details_array = product_details.split(",");
        setProductDetails(product, product_details_array);
        //checks that catalog number is valid
        if (productsAmountMapByCategory.containsKey(product.getClassification().getCategory()) &&
                productsAmountMapByCategory.get(product.getClassification().getCategory()).containsKey(product.getClassification().getSubcategory()) &&
                productsAmountMapByCategory.get(product.getClassification().getCategory()).get(product.getClassification().getSubcategory()).containsKey(String.valueOf(product.getClassification().getProductSize()))) {

            if (catalog_numbers_Set.contains(product.getClassification().getCatalogNumber())) { //checks if catalog number is exist
                products.put(product.getProductId(), product);
                updateProductInventoryCount(true, product.getClassification().getCategory(), product.getClassification().getSubcategory(), String.valueOf(product.getClassification().getProductSize()), product.getStored());
                return true;
            }
            else{ //catalog number is wrong
                return false;
            }
        }
        else{ //new kind of product
            if(!catalog_numbers_Set.contains(product.getClassification().getCatalogNumber())){ //if catalog number is not exist in set, add product
                products.put(product.getProductId(), product);
                updateProductInventoryCount(true, product.getClassification().getCategory(), product.getClassification().getSubcategory(), String.valueOf(product.getClassification().getProductSize()), product.getStored());
                return true;
            }
            else{
                return false;
            }
        }
    }

    /**
     * Marks a product as defective.
     *
     * @param product_ID the ID of the product to mark
     */
    public void markDefect(int product_ID){
        products.get(product_ID).setDefect(true);
        System.out.println("The product with the ID " + product_ID + " has been marked as defective.");
    }

    /**
     * Handles the purchase of a product: sets sale price, moves to purchase list, and removes from stock.
     *
     * @param product_ID  the ID of the purchased product
     * @param sale_price  the sale price of the product
     */
    public void handlePurchaseProduct(int product_ID, double sale_price){
        Product p = products.get(product_ID);
        purchase_products.put(product_ID, p);
        purchase_products.get(product_ID).getClassification().setSalePrice(sale_price);
        products.remove(product_ID);
        updateProductInventoryCount(false,p.getClassification().getCategory(),
                p.getClassification().getSubcategory(),
                String.valueOf(p.getClassification().getProductSize()),
                p.getStored()); //decrement by 1 in categories map
    }

    /**
     * Handles the removal of a product due to defect and updates inventory accordingly.
     *
     * @param product_ID the ID of the defective product
     */
    public void handleDefectProduct(int product_ID){
        Product p = products.get(product_ID);
        products.remove(product_ID);
        System.out.println("The product with the ID " + product_ID + " has been removed from the store.");
        updateProductInventoryCount(false,p.getClassification().getCategory(),
                p.getClassification().getSubcategory(),
                String.valueOf(p.getClassification().getProductSize()), p.getStored()); //decrement by 1 in categories map
    }

    /**
     * Updates the cost price of a product.
     *
     * @param product_ID the ID of the product
     * @param new_price  the new cost price to set
     */
    public void updatePriceController(int product_ID, double new_price){
        products.get(product_ID).getClassification().setCostPrice(new_price);
        System.out.println("The price of product ID " + product_ID + " has been updated to " + new_price);
    }

    /**
     * Generates a report of all defective products currently in stock.
     *
     * @return a formatted string with the defective products list
     */
    public String defectReportController(){
        StringBuilder defectReport = new StringBuilder();
        int counter = 1;
        for (Product p : products.values()){
            if(p.isDefect()) {
                defectReport.append(counter).append(". ").append("Product ID: ").append(p.getProductId())
                        .append(", Product Name: ").append(p.getProductName()).append(", Stored: ")
                        .append(p.getStored()).append(", Section:").append(p.getSection())
                        .append(" , Category: ").append(p.getClassification().getCategory())
                        .append(", Sub-Category: ").append(p.getClassification().getSubcategory())
                        .append(", Size: ").append(p.getClassification().getProductSize()).append("\n");
                counter++;
            }
        }
        if(counter == 1){
            defectReport = new StringBuilder("There are no defective products.");}
        return defectReport.toString();
    }


    /**
     * Generates a detailed inventory report for the given categories.
     *
     * The report organizes products hierarchically by:
     *   - Category
     *     - Sub-category
     *       - Product size (Small, Medium, Big)
     *
     * For each group, it lists all products with their ID, name, location, and expiration date.
     *
     * @param categories An array of category names for which to generate the report.
     * @return A formatted string containing the inventory report grouped by category, sub-category, and product size.
     */
    public String inventoryReportController(String[] categories) {
        StringBuilder report = new StringBuilder("Here is the Category Report:\n");

        for (String categoryName : categories) {
            report.append("Category: ").append(categoryName).append("\n");

            // Map to group products by sub-category and size
            Map<String, Map<Integer, List<Product>>> subcategoryMap = new TreeMap<>();

            for (Product p : products.values()) {
                Classification c = p.getClassification();
                if (c.getCategory().equalsIgnoreCase(categoryName)) {
                    String subCategory = c.getSubcategory();
                    int size = c.getProductSize();

                    subcategoryMap
                            .computeIfAbsent(subCategory, k -> new TreeMap<>())
                            .computeIfAbsent(size, k -> new ArrayList<>())
                            .add(p);
                }
            }

            if (subcategoryMap.isEmpty()) {
                report.append("  No products found in this category.\n");
            } else {
                for (Map.Entry<String, Map<Integer, List<Product>>> subEntry : subcategoryMap.entrySet()) {
                    report.append("  Sub-Category: ").append(subEntry.getKey()).append("\n");

                    for (Map.Entry<Integer, List<Product>> sizeEntry : subEntry.getValue().entrySet()) {
                        int size = sizeEntry.getKey();
                        String sizeLabel = switch (size) {
                            case 1 -> "Small";
                            case 2 -> "Medium";
                            case 3 -> "Big";
                            default -> "Unknown Size";
                        };

                        report.append("    Size: ").append(sizeLabel).append("\n");

                        int count = 1;
                        for (Product p : sizeEntry.getValue()) {
                            Classification c = p.getClassification();
                            report.append("      ").append(count++).append(". ")
                                    .append("ID: ").append(p.getProductId())
                                    .append(", Name: ").append(p.getProductName())
                                    .append(", Stored: ").append(p.getStored())
                                    .append(", Expiring: ").append(p.getExpiringDate())
                                    .append("\n");
                        }
                    }
                }
            }

            report.append("------------------------------------------------------------\n");
        }

        return report.toString();
    }


    /**
     * Returns a formatted string with the details of a product.
     *
     * @param product_ID the ID of the product
     * @return a string representing all product details
     */
    public String productsDetails(int product_ID) {
        Product p = products.get(product_ID);

        String details = "Product ID: " + p.getProductId() + "\n"
                + "Product name: " + p.getProductName() + "\n"
                + "Expiring Date: " + p.getExpiringDate() + "\n"
                + "Location: " + p.getStored() + ", Section: " + p.getSection() + "\n"
                + "Catalog Number: " + p.getClassification().getCatalogNumber() + ", Category: "
                + p.getClassification().getCategory() + ", Sub-Category: " + p.getClassification().getSubcategory() + "\n"
                + "Size: " + p.getClassification().getProductSize() + "\n"
                + "Product cost price: " + p.getClassification().getCostPrice() + "\n"
                + "Product demand: " + p.getClassification().getProductDemand() + "\n"
                + "Supply time: " + p.getClassification().getSupplyTime() + " days\n"
                + "Minimum amount for alert: " + p.getClassification().getMinAmountForAlert() + "\n"
                + "Manufacturer: " + p.getClassification().getManufacturer() + "\n"
                + "Supplier Discount: " + p.getClassification().getSupplierDiscount() + "\n"
                + "Store Discount: " + p.getClassification().getStoreDiscount() + "\n"
                + "Defective: " + (p.isDefect() ? "Yes" : "No") + "\n";

        return details;
    }

    /**
     * Checks if the current stock of a product is below its minimum alert threshold.
     *
     * @param product_ID the ID of the product
     * @return true if current stock is below alert level, false otherwise
     */
    public boolean checkForAlert(int product_ID){
        Product p = products.get(product_ID);
        int wareHouse_amount = productsAmountMapByCategory.get(p.getClassification().getCategory()).get(p.getClassification().getSubcategory()).get(String.valueOf(p.getClassification().getProductSize())).get("wareHouse");
        int interior_store_amount = productsAmountMapByCategory.get(p.getClassification().getCategory()).get(p.getClassification().getSubcategory()).get(String.valueOf(p.getClassification().getProductSize())).get("interiorStore");
        int currentAmount = wareHouse_amount + interior_store_amount - 1; //remove current product from amount
        return currentAmount <= p.getClassification().getMinAmountForAlert();
    }

    /**
     * Retrieves the name of a product by its ID.
     *
     * @param product_ID the ID of the product
     * @return the name of the product
     */
    public String getProductName(int product_ID){
        return products.get(product_ID).getProductName();
    }

    /**
     * Applies a store discount to all products of a specific category.
     *
     * @param category the category name
     * @param discount the discount percentage to apply
     */
    public void setDiscountForCategory(String category, int discount){
        for(Product p : products.values()){
            if(p.getClassification().getCategory().equals(category)){
                p.getClassification().setStoreDiscount(discount);
            }
        }
        System.out.println("Discount of " + discount + "% has been set for category " + category);
    }

    /**
     * Applies a store discount to all products of a specific sub-category.
     *
     * @param sub_category the sub-category name
     * @param discount     the discount percentage to apply
     */
    public void setDiscountForSubCategory(String sub_category, int discount){
        for(Product p : products.values()){
            if(p.getClassification().getSubcategory().equals(sub_category)){
                p.getClassification().setStoreDiscount(discount);
            }
        }
        System.out.println("Discount of " + discount + "% has been set for sub-category " + sub_category);
    }

    /**
     * Applies a store discount to all products with a specific catalog number.
     *
     * @param catalog_number the catalog number
     * @param discount       the discount percentage to apply
     */
    public void setDiscountForCatalogNum(int catalog_number, int discount){
        for(Product p : products.values()){
            if(p.getClassification().getCatalogNumber() == catalog_number){
                p.getClassification().setStoreDiscount(discount);
            }
        }
        System.out.println("Discount of " + discount + "% has been set for catalog number " + catalog_number);
    }

    /**
     * Returns the sale price of a purchased product.
     *
     * @param product_ID the ID of the product
     * @return the sale price set at purchase time
     */
    public double getProductPurchasePrice(int product_ID){
        return purchase_products.get(product_ID).getClassification().getSalePrice();
    }


    public String showCurrentAmountPerLocationByCatalogNumber(int catalog_number) {
        // Check if catalog number exists in the system
        if (!catalog_numbers_Set.contains(catalog_number)) {
            return "Invalid catalog number: " + catalog_number + ". This catalog number does not exist in the Inventory.";
        }

        int warehouse_quantity = 0;
        int store_quantity = 0;

        for (Product p : products.values()) {
            if (p.getClassification().getCatalogNumber() == catalog_number) {
                String location = p.getStored();
                if (location.equalsIgnoreCase("wareHouse")) {
                    warehouse_quantity++;
                } else if (location.equalsIgnoreCase("interiorStore")) {
                    store_quantity++;
                }
            }
        }

        if (warehouse_quantity == 0 && store_quantity == 0) {
            return "No products found for catalog number: " + catalog_number;
        }

        return "Catalog Number: " + catalog_number + "\n"
                + "Warehouse quantity: " + warehouse_quantity + "\n"
                + "Store quantity: " + store_quantity;
    }



}
