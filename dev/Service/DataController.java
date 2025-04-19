package Service;
import Domain.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.time.format.DateTimeParseException;

/**
 * The DataController class is part of the Service layer and manages all operations related to product data,
 * including importing, updating inventory, handling purchases, managing defects, and generating reports.
 */
public class DataController {
    private HashMap<Integer,Product> products; //Saves all the current products in the store. Key: product ID, Value: an object of product
    private HashMap<Integer,Product> purchase_products; //Saves all purchase products
    private HashMap<String, HashMap<String, HashMap<String, HashMap<String, Integer>>>> products_amount_map_by_category; //Saves all product quantities in the following format:: Map<category, Map<sub-category,Map<size, Map<location, amount>>>> (location- wareHouse, interiorStore)
    private Set<Integer> catalog_numbers_set; //Saves all the catalogs numbers we have in store

    public DataController(){
        products = new HashMap<>();
        purchase_products = new HashMap<>();
        products_amount_map_by_category = new HashMap<>();
        catalog_numbers_set = new HashSet<>();
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
                catalog_numbers_set.add(product.getClassification().getCatalogNumber()); //add catalog number to catalog set
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
        if (!products_amount_map_by_category.containsKey(category)) {
            products_amount_map_by_category.put(category, new HashMap<>());
        }

        // Check if the sub category exists
        HashMap<String, HashMap<String, HashMap<String, Integer>>> subCategoryMap = products_amount_map_by_category.get(category);
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
        classification.setSupplierDiscount(Integer.parseInt(details[13]));
        classification.setCostPrice(Double.parseDouble(details[9]));
        classification.setProductDemand(Integer.parseInt(details[10]));
        classification.setSupplyTime(Integer.parseInt(details[11]));
        classification.setMinAmountForAlert((int)(0.5*classification.getProductDemand() + 0.5*classification.getSupplyTime()));
        classification.setManufacturer(details[12]);

        classification.setStoreDiscount(Integer.parseInt(details[14]));
        classification.setDisplayPrice(classification.getCostPrice());
        classification.setDisplayPriceWithoutDiscount(classification.getCostPrice());

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
        if (products_amount_map_by_category.containsKey(product.getClassification().getCategory()) &&
                products_amount_map_by_category.get(product.getClassification().getCategory()).containsKey(product.getClassification().getSubcategory()) &&
                products_amount_map_by_category.get(product.getClassification().getCategory()).get(product.getClassification().getSubcategory()).containsKey(String.valueOf(product.getClassification().getProductSize()))) {

            if (catalog_numbers_set.contains(product.getClassification().getCatalogNumber())) { //checks if catalog number is exist
                products.put(product.getProductId(), product);
                updateProductInventoryCount(true, product.getClassification().getCategory(), product.getClassification().getSubcategory(), String.valueOf(product.getClassification().getProductSize()), product.getStored());
                return true;
            }
            else{ //catalog number is wrong
                return false;
            }
        }
        else{ //new kind of product
            if(!catalog_numbers_set.contains(product.getClassification().getCatalogNumber())){ //if catalog number is not exist in set, add product
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
        if (!products.containsKey(product_ID)) {
            System.out.println("ERROR: Product ID " + product_ID + " not found. Cannot mark as defect.");
            return;
        }
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
        if (!products.containsKey(product_ID)) {
            System.out.println("ERROR: Product ID " + product_ID + " not found. Cannot proceed with purchase.");
            return;
        }
        Product p = products.get(product_ID);
        purchase_products.put(product_ID, p);
        purchase_products.get(product_ID).getClassification().setSalePrice(sale_price);
        products.remove(product_ID);
        updateProductInventoryCount(false, p.getClassification().getCategory(),
                p.getClassification().getSubcategory(),
                String.valueOf(p.getClassification().getProductSize()),
                p.getStored());
    }


    /**
     * Handles the removal of a product due to defect and updates inventory accordingly.
     *
     * @param product_ID the ID of the defective product
     */
    public void handleDefectProduct(int product_ID){
        if (!products.containsKey(product_ID)) {
            System.out.println("ERROR: Product ID " + product_ID + " not found. Cannot mark as defect.");
            return;
        }
        Product p = products.get(product_ID);
        products.remove(product_ID);
        System.out.println("The product with the ID " + product_ID + " has been removed from the store.");
        updateProductInventoryCount(false, p.getClassification().getCategory(),
                p.getClassification().getSubcategory(),
                String.valueOf(p.getClassification().getProductSize()), p.getStored());
    }



    /**
     * Updates the cost price of all products with the given catalog number.
     *
     * @param catalogNumber the catalog number of the product(s)
     * @param newPrice the new price to set
     */
    public void updateCostPriceByCatalogNumber(int catalogNumber, double newPrice) {
        boolean found = false;
        for (Product p : products.values()) {
            if (p.getClassification().getCatalogNumber() == catalogNumber) {
                p.getClassification().setCostPrice(newPrice);
                found = true;
            }
        }

        if (found) {
            System.out.println("The price of all products with catalog number " + catalogNumber + " has been updated to " + newPrice);
        } else {
            System.out.println("No products with catalog number " + catalogNumber + " found in stock.");
        }
    }

    public void updateProductSupplyDetails(int catalogNumber, Integer supplyTime, Integer demand) {
        boolean found = false;
        for (Product p : products.values()) {
            if (p.getClassification().getCatalogNumber() == catalogNumber) {
                if (supplyTime != null) {
                    p.getClassification().setSupplyTime(supplyTime);
                    System.out.println("Updated supply time for product ID " + p.getProductId());
                }
                if (demand != null) {
                    p.getClassification().setProductDemand(demand);
                    System.out.println("Updated demand for product ID " + p.getProductId());
                }
                found = true;
            }
        }

        if (!found) {
            System.out.println("No products with catalog number " + catalogNumber + " found in stock.");
        }
    }

    public void updateProductLocation(int id, String loc, String section) {
        boolean found = false;
        for (Product p : products.values()) {
            if (p.getProductId() == id) {
                if (loc != null) {
                    p.setStored(loc);
                    System.out.println("The location of product with ID " + id + " has been updated to " + loc);
                }
                if (section != null) {
                    p.setSection(section);
                    System.out.println("The section of product with ID " + id + " has been updated to " + section);
                }
                found = true;
            }
        }

        if (!found) {
            System.out.println("No products with ID " + id + " found in stock.");
        }
    }

    /**
     * Generates a report of all defective and expired products currently in stock.
     * The report includes two sections:
     *   1. Products that were manually marked as defective.
     *   2. Products whose expiration date has passed, sorted from oldest to newest.
     *
     * @return A formatted string containing both defective and expired product listings.
     */
    public String defectAndExpiredReport() {
        StringBuilder report = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate today = LocalDate.now();
        // Section 1: Defective products
        report.append("Defective Products (Marked):\n");
        int counter = 1;
        boolean hasDefects = false;
        for (Product p : products.values()) {
            if (p.isDefect()) {
                hasDefects = true;
                report.append(counter++).append(". Product ID: ").append(p.getProductId())
                        .append(", Name: ").append(p.getProductName())
                        .append(", Category: ").append(p.getClassification().getCategory())
                        .append(", Sub-Category: ").append(p.getClassification().getSubcategory())
                        .append(", Size: ").append(p.getClassification().getProductSize())
                        .append(", Stored: ").append(p.getStored())
                        .append(", Section: ").append(p.getSection())
                        .append("\n");
            }
        }
        if (!hasDefects) {
            report.append("No products marked as defective.\n");
        }
        // Section 2: Expired products (sorted by date)
        report.append("\nExpired Products (Date has passed):\n");
        List<Product> expiredProducts = new ArrayList<>();
        for (Product p : products.values()) {
            try {
                LocalDate expDate = LocalDate.parse(p.getExpiringDate(), formatter);
                if (expDate.isBefore(today)) {
                    expiredProducts.add(p);
                }
            } catch (DateTimeParseException e) {
                // Ignore invalid date
            }
        }
        expiredProducts.sort(Comparator.comparing(p -> {
            try {
                return LocalDate.parse(p.getExpiringDate(), formatter);
            } catch (DateTimeParseException e) {
                return LocalDate.MAX;
            }
        }));
        if (expiredProducts.isEmpty()) {
            report.append("No products have expired.\n");
        } else {
            counter = 1;
            for (Product p : expiredProducts) {
                report.append(counter++).append(". Product ID: ").append(p.getProductId())
                        .append(", Name: ").append(p.getProductName())
                        .append(", Expired on: ").append(p.getExpiringDate())
                        .append(", Category: ").append(p.getClassification().getCategory())
                        .append(", Sub-Category: ").append(p.getClassification().getSubcategory())
                        .append(", Size: ").append(p.getClassification().getProductSize())
                        .append(", Stored: ").append(p.getStored())
                        .append("\n");
            }
        }
        return report.toString();
    }


    /**
     * Generates an inventory report for the specified product categories.
     *
     * <p>The report includes products grouped by sub-category and size (Small, Medium, Big),
     * and within each group, products are sorted by their expiration date in ascending order.</p>
     *
     * <p>For each category provided:
     * <ul>
     *   <li>If products exist in the category, the report includes a detailed breakdown by sub-category and size.</li>
     *   <li>If no products are found for the category, the report notes that the category does not exist.</li>
     * </ul>
     *
     * If none of the specified categories contain any products, a message is returned indicating that all categories are invalid.
     *
     * @param categories an array of category names to include in the report
     * @return a formatted string representing the inventory report for the provided categories
     */
    public String inventoryReportByCategories(String[] categories) {
        StringBuilder report = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        boolean anyCategoryFound = false; // Tracks whether at least one valid category is found

        for (String categoryName : categories) {
            report.append("Category: ").append(categoryName).append("\n");

            // Group products by sub-category and size
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
            // If no products matched this category
            if (subcategoryMap.isEmpty()) {
                report.append("  Category does not exist.\n");
            } else {
                anyCategoryFound = true;
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

                        List<Product> sortedProducts = sizeEntry.getValue();
                        sortedProducts.sort(Comparator.comparing(p -> {
                            try {
                                return LocalDate.parse(p.getExpiringDate(), formatter);
                            } catch (Exception e) {
                                return LocalDate.MAX;
                            }
                        }));

                        int count = 1;
                        for (Product p : sortedProducts) {
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
        // If none of the entered categories matched any products
        if (!anyCategoryFound) {
            return "No valid categories found. All entered categories do not exist.";
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

        return "Product ID: " + p.getProductId() + "\n"
                + "Product name: " + p.getProductName() + "\n"
                + "Expiring Date: " + p.getExpiringDate() + "\n"
                + "Location: " + p.getStored() + ", Section: " + p.getSection() + "\n"
                + "Catalog Number: " + p.getClassification().getCatalogNumber() + ", Category: "
                + p.getClassification().getCategory() + ", Sub-Category: " + p.getClassification().getSubcategory() + "\n"
                + "Size: " + p.getClassification().getProductSize() + "\n"
                + "Product cost price: " + p.getClassification().getCostPrice() + "\n"
                + "display price: " + p.getClassification().getDisplayPrice() + "\n"
                + "display price without discount: " + p.getClassification().getDisplayPriceWithoutDiscount()+"\n"
                + "Product demand: " + p.getClassification().getProductDemand() + "\n"
                + "Supply time: " + p.getClassification().getSupplyTime() + " days\n"
                + "Minimum amount for alert: " + p.getClassification().getMinAmountForAlert() + "\n"
                + "Manufacturer: " + p.getClassification().getManufacturer() + "\n"
                + "Supplier Discount: " + p.getClassification().getSupplierDiscount() + "\n"
                + "Store Discount: " + p.getClassification().getStoreDiscount() + "\n"
                + "Defective: " + (p.isDefect() ? "Yes" : "No") + "\n";
    }

    /**
     * Checks whether the specified product should trigger a stock alert.
     *
     * <p>The function verifies if the product exists, calculates the total quantity
     * of similar products (by category, sub-category, and size), and determines
     * whether the quantity after one unit is removed falls below or equals
     * the minimum threshold defined for alerts.</p>
     *
     * @param product_ID the unique identifier of the product to check
     * @return true if an alert should be triggered, false otherwise
     */
    public boolean checkForAlert(int product_ID) {
        Product p = products.get(product_ID);
        if (p == null) return false;

        String category = p.getClassification().getCategory();
        String subCategory = p.getClassification().getSubcategory();
        String size = String.valueOf(p.getClassification().getProductSize());

        int currentAmount = getTotalQuantity(category, subCategory, size);
        if (currentAmount == -1) return false;

        return currentAmount - 1 <= p.getClassification().getMinAmountForAlert();
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
     * Applies a store discount to all products within a specified category.
     *
     * <p>If the category exists in the system, the discount is applied to all
     * relevant products. Otherwise, a message is displayed indicating that
     * the category does not exist.</p>
     *
     * @param category the name of the category to apply the discount to
     * @param discount the discount percentage to apply
     */
    public void setDiscountForCategory(String category, int discount){
        boolean found = false;
        for(Product p : products.values()){
            if(p.getClassification().getCategory().equals(category)){
                p.getClassification().setStoreDiscount(discount);
                p.getClassification().setDisplayPrice(p.getClassification().getCostPrice());
                p.getClassification().setDisplayPriceWithoutDiscount(p.getClassification().getCostPrice());
                found = true;
            }
        }
        if (found) {
            System.out.println("Discount of " + discount + "% has been set for category " + category);
        } else {
            System.out.println("The category \"" + category + "\" does not exist in the system.");
        }
    }

    /**
     * Applies a store discount to all products within a specified sub-category.
     *
     * <p>If the sub-category exists in the system, the discount is applied to all
     * relevant products. Otherwise, a message is displayed indicating that
     * the sub-category does not exist.</p>
     *
     * @param sub_category the name of the sub-category to apply the discount to
     * @param discount the discount percentage to apply
     */
    public void setDiscountForSubCategory(String sub_category, int discount){
        boolean found = false;
        for(Product p : products.values()){
            if(p.getClassification().getSubcategory().equals(sub_category)){
                p.getClassification().setStoreDiscount(discount);
                p.getClassification().setDisplayPrice(p.getClassification().getCostPrice());
                p.getClassification().setDisplayPriceWithoutDiscount(p.getClassification().getCostPrice());
                found = true;
            }
        }
        if (found) {
            System.out.println("Discount of " + discount + "% has been set for sub-category " + sub_category);
        } else {
            System.out.println("The sub-category \"" + sub_category + "\" does not exist in the system.");
        }
    }

    /**
     * Applies a store discount to the product with the specified catalog number.
     *
     * <p>If a product with the given catalog number exists in the system,
     * the discount is applied. Otherwise, a message is displayed indicating
     * that the catalog number does not exist.</p>
     *
     * @param catalog_number the catalog number of the product
     * @param discount the discount percentage to apply
     */
    public void setSupplierDiscount(int catalog_number, int discount){
        boolean found = false;
        for(Product p : products.values()){
            if(p.getClassification().getCatalogNumber() == catalog_number){
                p.getClassification().setSupplierDiscount(discount);
                found = true;
            }
        }
    }

    public void setDiscountForCatalogNumber(int catalog_number, int discount){
        boolean found = false;
        for(Product p : products.values()){
            if(p.getClassification().getCatalogNumber() == catalog_number){
                p.getClassification().setStoreDiscount(discount);
                p.getClassification().setDisplayPrice(p.getClassification().getCostPrice());
                p.getClassification().setDisplayPriceWithoutDiscount(p.getClassification().getCostPrice());
                found = true;
            }
        }
        if (found) {
            System.out.println("Discount of " + discount + "% has been set for catalog number " + catalog_number);
        } else {
            System.out.println("The catalog number \"" + catalog_number + "\" does not exist in the system.");
        }
    }


    /**
     * Returns a list of sale prices for all purchased products with the given catalog number.
     *
     * @param catalogNumber the catalog number to search for
     * @return a formatted string of purchase prices, or a message if none found
     */
    public String getPurchasePricesByCatalogNumber(int catalogNumber) {
        List<Double> prices = new ArrayList<>();

        for (Product p : purchase_products.values()) {
            if (p.getClassification().getCatalogNumber() == catalogNumber) {
                double price = p.getClassification().getSalePrice();
                if (price >= 0) {
                    prices.add(price);
                }
            }
        }

        if (prices.isEmpty()) {
            return "No purchased products found with catalog number: " + catalogNumber;
        }

        StringBuilder result = new StringBuilder("Sale prices for catalog number " + catalogNumber + ":\n");
        for (int i = 0; i < prices.size(); i++) {
            result.append((i + 1)).append(". ").append(prices.get(i)).append("\n");
        }
        return result.toString();
    }

    /**
     * Returns the current stock count of a specific product, identified by its catalog number,
     * separated by storage location ("wareHouse" and "interiorStore").
     * This function iterates through all products in the inventory, and for each product that matches
     * the given catalog number, it increments a counter according to its storage location.
     * If the catalog number does not exist in the system, an error message is returned.
     * If no matching products are found, a message indicating this is returned as well.
     *
     * @param catalog_number the catalog number of the product to look for
     * @return a formatted string showing the quantity of the product in the warehouse and in the store,
     *         or an appropriate message if the catalog number is invalid or has no stock
     */
    public String showCurrentAmountPerLocationByCatalogNumber(int catalog_number) {
        // Check if the catalog number exists in the system
        if (!catalog_numbers_set.contains(catalog_number)) {
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

    /**
     * Retrieves the total current quantity of a product in both warehouse and interior store,
     * based on its category, sub-category, and size.
     *
     * @param category the category of the product
     * @param subCategory the sub-category of the product
     * @param size the size of the product (as string)
     * @return total quantity (warehouse + store), or -1 if data not found
     */
    private int getTotalQuantity(String category, String subCategory, String size) {
        Map<String, HashMap<String, HashMap<String, Integer>>> subMap = products_amount_map_by_category.get(category);
        if (subMap == null) return -1;

        Map<String, HashMap<String, Integer>> sizeMap = subMap.get(subCategory);
        if (sizeMap == null) return -1;

        Map<String, Integer> locationMap = sizeMap.get(size);
        if (locationMap == null) return -1;

        int warehouseQty = locationMap.getOrDefault("wareHouse", 0);
        int storeQty = locationMap.getOrDefault("interiorStore", 0);

        return warehouseQty + storeQty;
    }

    /**
     * Generates a report of products that are currently understocked based on their minimum required quantity.
     * The report is grouped by catalog number, summing all instances of the same product across different entries.
     * For each understocked product, the report shows:
     * - Catalog number
     * - Product name
     * - Total quantity currently in stock
     * - Minimum required quantity
     * - Number of units missing to meet the minimum
     *
     * @return A formatted string listing all understocked products and their shortage,
     *         or a message indicating that no products are below their minimum level.
     */
    public String generateReorderAlertReport() {
        StringBuilder report = new StringBuilder();
        boolean found = false;

        // Map: catalog number → list of products
        Map<Integer, List<Product>> catalogMap = new HashMap<>();
        for (Product p : products.values()) {
            int catalogNum = p.getClassification().getCatalogNumber();
            catalogMap.computeIfAbsent(catalogNum, k -> new ArrayList<>()).add(p);
        }

        // מוצרים שעדיין קיימים במלאי
        for (Map.Entry<Integer, List<Product>> entry : catalogMap.entrySet()) {
            int catalogNumber = entry.getKey();
            List<Product> productList = entry.getValue();
            if (productList.isEmpty()) continue;

            Product sample = productList.get(0); // נציג
            String name = sample.getProductName();
            int minRequired = sample.getClassification().getMinAmountForAlert();
            int totalInStock = productList.size();

            if (totalInStock < minRequired) {
                found = true;
                int missing = minRequired - totalInStock;
                report.append("Catalog Number: ").append(catalogNumber)
                        .append(", Name: ").append(name)
                        .append(", Total in stock: ").append(totalInStock)
                        .append(", Minimum required: ").append(minRequired)
                        .append(", Missing: ").append(missing).append("\n");
            }
        }

        // מוצרים שנמחקו לגמרי מהמלאי
        for (int catalogNumber : catalog_numbers_set) {
            if (!catalogMap.containsKey(catalogNumber)) {
                int minRequired = 0;
                String productName = "Unknown";

                for (Product p : purchase_products.values()) {
                    if (p.getClassification().getCatalogNumber() == catalogNumber) {
                        minRequired = p.getClassification().getMinAmountForAlert();
                        productName = p.getProductName();
                        break;
                    }
                }

                if (minRequired > 0) {
                    found = true;
                    report.append("Catalog Number: ").append(catalogNumber)
                            .append(", Name: ").append(productName)
                            .append(", Total in stock: 0")
                            .append(", Minimum required: ").append(minRequired)
                            .append(", Missing: ").append(minRequired).append("\n");
                }
            }
        }

        if (!found) {
            return "All the products are above their minimum required amount.";
        }
        return report.toString();
    }


    /**
     * Checks whether a product with the specified product ID exists in the inventory.
     *
     * @param productId the unique identifier of the product to check
     * @return true if the product exists in the inventory, false otherwise
     */
    public boolean productExists(int productId) {
        return products.containsKey(productId);
    }


}
