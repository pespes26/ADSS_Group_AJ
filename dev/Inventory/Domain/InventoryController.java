package Inventory.Domain;



import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;


/**
 * Manages inventory operations: items, products, discounts, branches, and reports.
 * Supports data import from CSV and updates product stock levels across branches.
 */

public class InventoryController {
    private final ItemController item_controller;
    private final ProductController product_controller;
    private final DiscountController discount_controller;
    private final ReportController report_controller;

    private final HashMap<Integer, Product> products;
    private final HashMap<Integer, Branch> branches;
    private final HashMap<String, HashMap<String, HashMap<String, HashMap<String, Integer>>>> products_amount_map_by_category;

    public InventoryController() {
        this.products = new HashMap<>();
        this.branches = new HashMap<>();
        this.products_amount_map_by_category = new HashMap<>();

        HashMap<Integer, Item> purchased_items = new HashMap<>();
        this.item_controller = new ItemController(branches, products, purchased_items);
        this.product_controller = new ProductController(products, purchased_items);
        this.discount_controller = new DiscountController(products);
        this.report_controller = new ReportController(branches, products);
    }

    /**
     * Imports inventory data from a CSV file.
     * Each line in the CSV represents an item with various attributes.
     *
     * @param path The path to the CSV file.
     */
    public void importData(String path) {
        try (CSVReader reader = new CSVReader(new FileReader(path))) {
            String[] itemFieldsFromCSV;
            while ((itemFieldsFromCSV = reader.readNext()) != null) {
                String input_id = itemFieldsFromCSV[0];
                if (input_id.startsWith("\uFEFF")) {
                    input_id = input_id.substring(1);
                }
                itemFieldsFromCSV[0] = input_id.trim();

                Item item = buildItemFromCSV(itemFieldsFromCSV);
                int branchId = item.getBranchId();

                branches.putIfAbsent(branchId, new Branch(branchId));
                branches.get(branchId).addItem(item);

                int catalog_number = item.getCatalogNumber();
                if (!products.containsKey(catalog_number)) {
                    Product product = buildProductFromCSV(itemFieldsFromCSV);
                    products.put(catalog_number, product);
                }

                String category = itemFieldsFromCSV[7];
                String sub_category = itemFieldsFromCSV[8];
                String size = itemFieldsFromCSV[9];
                String location = itemFieldsFromCSV[4];

                updateProductInventoryCount(true, branchId, category, sub_category, size, location);

            }
        } catch (IOException | CsvValidationException e) {
            System.err.println("Failed to import data: " + e.getMessage());
        }
    }

    /**
     * Builds an Item object from a CSV line.
     *
     * @param fields The fields from the CSV line.
     * @return The constructed Item object.
     */
    private Item buildItemFromCSV(String[] fields) {
        Item item = new Item();
        item.setItemId(Integer.parseInt(fields[0]));
        item.setBranchId(Integer.parseInt(fields[1]));
        item.setItemExpiringDate(fields[3]);
        item.setStorageLocation(fields[4]);
        item.setSectionInStore(fields[5]);
        item.setCatalog_number(Integer.parseInt(fields[6]));
        item.setDefect(false);
        return item;
    }

    /**
     * Returns the map of all existing branches in the system.
     * <p>
     * Each branch is identified by its unique branch ID.
     * Used to access branch-specific inventories and operations.
     *
     * @return a map of branch IDs to {@code Branch} objects.
     */
    public HashMap<Integer, Branch> getBranches() {
        return branches;
    }

    /**
     * Updates the stock count of a product in a specific branch and location.
     * <p>
     * Supports adding or removing items based on category, sub-category, size, and location.
     * Also updates product warehouse/store quantities accordingly.
     *
     * @param add true to add stock, false to remove stock
     * @param branchId the ID of the branch where the update applies
     * @param category the product's category
     * @param sub_category the product's sub-category
     * @param size the size of the product
     * @param location the location ("Warehouse" or "InteriorStore")
     */
    public void updateProductInventoryCount(boolean add, int branchId, String category, String sub_category, String size, String location) {
        products_amount_map_by_category.putIfAbsent(category, new HashMap<>());
        HashMap<String, HashMap<String, HashMap<String, Integer>>> subCategoryMap = products_amount_map_by_category.get(category);
        subCategoryMap.putIfAbsent(sub_category, new HashMap<>());
        HashMap<String, HashMap<String, Integer>> sizeMap = subCategoryMap.get(sub_category);
        sizeMap.putIfAbsent(size, new HashMap<>());
        HashMap<String, Integer> locationMap = sizeMap.get(size);

        if (add) {
            locationMap.put(location, locationMap.getOrDefault(location, 0) + 1);
            locationMap.putIfAbsent(location.equalsIgnoreCase("warehouse") ? "interiorStore" : "warehouse", 0);
        } else {
            locationMap.put(location, locationMap.getOrDefault(location, 0) - 1);
        }

        Branch branch = branches.get(branchId);
        if (branch != null) {
            for (Item item : branch.getItems().values()) {
                Product product = products.get(item.getCatalogNumber());
                if (product != null && product.getCategory().equalsIgnoreCase(category)
                        && product.getSubCategory().equalsIgnoreCase(sub_category)) {
                    if (location.equalsIgnoreCase("Warehouse")) {
                        product.setQuantityInWarehouse(add ? product.getQuantityInWarehouse() + 1 : product.getQuantityInWarehouse() - 1);
                    } else if (location.equalsIgnoreCase("interiorStore")) {
                        product.setQuantityInStore(add ? product.getQuantityInStore() + 1 : product.getQuantityInStore() - 1);
                    }
                }
            }
        }
    }


    /**
     * Builds a {@code Product} object from a CSV input line.
     * <p>
     * Initializes the product's catalog number, name, category, size, supply time,
     * manufacturer, discounts, and calculated prices.
     * Default warehouse and store quantities are set to zero.
     *
     * @param fields an array of CSV field values representing the product details
     * @return a fully populated {@code Product} object
     */
    private Product buildProductFromCSV(String[] fields) {
        int catalogNumber = Integer.parseInt(fields[6]);
        String productName = fields[2];
        String category = fields[7];
        String subCategory = fields[8];
        int demandLevel = Integer.parseInt(fields[11]);
        int supplyTime = Integer.parseInt(fields[12]);
        String manufacturer = fields[13];
        int size = Integer.parseInt(fields[9]);
        double costPriceBefore = Double.parseDouble(fields[10]);
        int supplierDiscount = Integer.parseInt(fields[14]);
        int storeDiscount = Integer.parseInt(fields[15]);

        Product product = new Product();
        populateProductData(product, catalogNumber, productName, category, subCategory,
                demandLevel, supplyTime, manufacturer,
                costPriceBefore, supplierDiscount, storeDiscount, size);
        return product;
    }

    /**
     * Provides access to the internal ItemController instance.
     *
     * @return The item controller.
     */
    public ItemController getItemController() {
        return item_controller;
    }

    /**
     * Provides access to the internal ProductController instance.
     *
     * @return The product controller.
     */
    public ProductController getProductController() {
        return product_controller;
    }

    /**
     * Provides access to the internal DiscountController instance.
     *
     * @return The discount controller.
     */
    public DiscountController getDiscountController() {
        return discount_controller;
    }

    /**
     * Provides access to the internal ReportController instance.
     *
     * @return The report controller.
     */
    public ReportController getReportController() {
        return report_controller;
    }

    /**
     * Handles the full process of adding a new product and its associated items to the inventory.
     *
     * @param catalogNumber the new product's catalog number
     * @param branchId the branch ID where the items will be stored
     * @param scan the Scanner object for reading user input
     */
    public void addNewProductAndItems(int catalogNumber, int branchId, Scanner scan) {
        System.out.println("Product with Catalog Number " + catalogNumber + " does not exist.");
        System.out.println("Please enter full product details.");

        // Collect new product details from the user
        System.out.print("Enter Product Name: ");
        String productName = scan.nextLine().trim();

        System.out.print("Enter Category: ");
        String category = scan.nextLine().trim();

        System.out.print("Enter Sub-Category: ");
        String subCategory = scan.nextLine().trim();

        System.out.print("Enter Product Size (numeric value between 1-3): ");
        int size = Integer.parseInt(scan.nextLine().trim());

        System.out.print("Enter Cost Price Before Supplier Discount: ");
        double costPriceBefore = Double.parseDouble(scan.nextLine().trim());

        System.out.print("Enter Product Demand Level (1–5): ");
        int demandLevel = Integer.parseInt(scan.nextLine().trim());

        System.out.print("Enter Supply Time (days): ");
        int supplyTime = Integer.parseInt(scan.nextLine().trim());

        System.out.print("Enter Manufacturer: ");
        String manufacturer = scan.nextLine().trim();

        System.out.print("Enter Supplier Discount (%): ");
        int supplierDiscount = Integer.parseInt(scan.nextLine().trim());

        System.out.print("Enter Store Discount (%): ");
        int storeDiscount = Integer.parseInt(scan.nextLine().trim());

        // Calculate final prices
        double costAfterSupplierDiscount = costPriceBefore * (1 - supplierDiscount / 100.0);
        double salePriceBeforeStoreDiscount = costAfterSupplierDiscount * 2;
        double salePriceAfterStoreDiscount = salePriceBeforeStoreDiscount * (1 - storeDiscount / 100.0);

        Product newProduct = new Product();
        populateProductData(newProduct, catalogNumber, productName, category, subCategory,
                demandLevel, supplyTime, manufacturer,
                costPriceBefore, supplierDiscount, storeDiscount, size);

        // Add the new product to the system
        product_controller.addProduct(newProduct);

        System.out.println("\nProduct added successfully!");

        // Collect item details
        System.out.print("How many items would you like to add for this product? ");
        int quantityToAdd = Integer.parseInt(scan.nextLine().trim());

        System.out.print("Enter storage location for all items (Warehouse or InteriorStore): ");
        String storageLocation = scan.nextLine().trim();

        System.out.print("Enter expiry date for all items (format: dd/MM/yyyy): ");
        String expiryDate = scan.nextLine().trim();

        int nextItemId = item_controller.getNextAvailableItemId();

        for (int i = 0; i < quantityToAdd; i++) {
            int currentItemId = nextItemId + i;
            item_controller.addItem(
                    currentItemId,
                    branchId,
                    catalogNumber,
                    storageLocation,
                    expiryDate
            );
        }

        System.out.println("\n-----------------------------------------");
        System.out.println(quantityToAdd + " items successfully added for Product Catalog Number " + catalogNumber + ".");
        System.out.println("-----------------------------------------\n");
    }


    /**
     * Populates a Product object with all its fields based on the provided data.
     *
     * @param product The product to populate.
     * @param catalogNumber Catalog number.
     * @param productName Product name.
     * @param category Category.
     * @param subCategory Sub-category.
     * @param demandLevel Demand level (1–5).
     * @param supplyTime Supply time (days).
     * @param manufacturer Manufacturer name.
     * @param costPriceBefore Cost price before supplier discount.
     * @param supplierDiscount Supplier discount (%).
     * @param storeDiscount Store discount (%).
     * @param size Product size.
     */
    private void populateProductData(Product product, int catalogNumber, String productName, String category, String subCategory,
                                     int demandLevel, int supplyTime, String manufacturer,
                                     double costPriceBefore, int supplierDiscount, int storeDiscount, int size) {

        product.setCatalogNumber(catalogNumber);
        product.setProductName(productName);
        product.setCategory(category);
        product.setSubCategory(subCategory);
        product.setProductDemandLevel(demandLevel);
        product.setSupplyTime(supplyTime);
        product.setManufacturer(manufacturer);
        product.setSize(size);

        double costAfterSupplierDiscount = costPriceBefore * (1 - supplierDiscount / 100.0);
        double salePriceBeforeStoreDiscount = costAfterSupplierDiscount * 2;
        double salePriceAfterStoreDiscount = salePriceBeforeStoreDiscount * (1 - storeDiscount / 100.0);

        product.setSupplierDiscount(supplierDiscount);
        product.setCostPriceBeforeSupplierDiscount(costPriceBefore);
        product.setCostPriceAfterSupplierDiscount(costAfterSupplierDiscount);
        product.setStoreDiscount(storeDiscount);
        product.setSalePriceBeforeStoreDiscount(salePriceBeforeStoreDiscount);
        product.setSalePriceAfterStoreDiscount(salePriceAfterStoreDiscount);
        product.setQuantityInWarehouse(0);
        product.setQuantityInStore(0);

        int minRequired = (int) (0.5 * demandLevel + 0.5 * supplyTime);
        product.setMinimumQuantityForAlert(minRequired);
    }



}