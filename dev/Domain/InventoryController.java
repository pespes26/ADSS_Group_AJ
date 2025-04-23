package Domain;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Controller responsible for managing the overall inventory system.
 * It serves as a central point that coordinates the item, product, discount,
 * and report controllers, and handles data import from external sources.
 */
public class InventoryController {
    private final ItemController item_controller;
    private final ProductController product_controller;
    private final DiscountController discount_controller;
    private final ReportController report_controller;

    private final HashMap<Integer, Item> items; // All items in the inventory, keyed by item ID
    private final HashMap<Integer, Product> products; // All products in the system, keyed by catalog number
    private final HashMap<String, HashMap<String, HashMap<String, HashMap<String, Integer>>>> products_amount_map_by_category;

    /**
     * Initializes the inventory controller and all of its internal sub-controllers.
     * Also initializes the internal maps that manage items, products, and product inventory structure.
     */
    public InventoryController() {
        this.items = new HashMap<>();
        this.products = new HashMap<>();
        HashMap<Integer, Item> purchased_items = new HashMap<>();
        this.products_amount_map_by_category = new HashMap<>();

        this.item_controller = new ItemController(items, products, purchased_items);
        this.product_controller = new ProductController(products, purchased_items);
        this.discount_controller = new DiscountController(products);
        this.report_controller = new ReportController(items, products);
    }

    /**
     * Imports item and product data from a CSV file into the system.
     * Each line in the CSV represents a single item and its associated product.
     * Products are only added if their catalog number does not already exist in the system.
     * Also updates the product inventory count by category, sub-category, size, and location.
     *
     * @param path The path to the CSV file to import.
     */
    public void importData(String path) {
        try (CSVReader reader = new CSVReader(new FileReader(path))) {
            String[] productFieldsFromCSV;
            while ((productFieldsFromCSV = reader.readNext()) != null) {
                String input_id = productFieldsFromCSV[0];
                if (input_id.startsWith("\uFEFF")) {
                    input_id = input_id.substring(1);
                }
                input_id = input_id.trim();
                productFieldsFromCSV[0] = input_id;

                Item item = new Item();
                item.setItemId(Integer.parseInt(productFieldsFromCSV[0]));
                item.setItemExpiringDate(productFieldsFromCSV[2]);
                item.setStorageLocation(productFieldsFromCSV[3]);
                item.setSectionInStore(productFieldsFromCSV[4]);
                item.setItemSize(Integer.parseInt(productFieldsFromCSV[8]));
                item.setCatalog_number(Integer.parseInt(productFieldsFromCSV[5]));
                item.setDefect(false);

                items.put(item.getItemId(), item);

                int catalog_number = Integer.parseInt(productFieldsFromCSV[5]);
                if (!products.containsKey(catalog_number)) {
                    Product product = buildProductFromCSV(productFieldsFromCSV);
                    products.put(catalog_number, product);
                }

                String category = productFieldsFromCSV[6];
                String sub_category = productFieldsFromCSV[7];
                String size = productFieldsFromCSV[8];
                String location = productFieldsFromCSV[3];

                updateProductInventoryCount(true, category, sub_category, size, location);
            }
        } catch (IOException | CsvValidationException e) {
            System.err.println("Failed to import data: " + e.getMessage());
        }
    }

    /**
     * Updates the internal inventory count of products grouped by category, sub-category, size, and location.
     * Also updates the quantity counters inside each matching product (warehouse or store).
     *
     * @param add         Indicates whether to increment (true) or decrement (false) the item count.
     * @param category    The category of the product.
     * @param sub_category The sub-category of the product.
     * @param size        The size of the item (as a string, e.g., "1", "2", "3").
     * @param location    The storage location ("warehouse" or "interiorStore").
     */
    public void updateProductInventoryCount(boolean add, String category, String sub_category, String size, String location) {
        products_amount_map_by_category.putIfAbsent(category, new HashMap<>());
        HashMap<String, HashMap<String, HashMap<String, Integer>>> sub_category_map = products_amount_map_by_category.get(category);
        sub_category_map.putIfAbsent(sub_category, new HashMap<>());
        HashMap<String, HashMap<String, Integer>> sizeMap = sub_category_map.get(sub_category);
        sizeMap.putIfAbsent(size, new HashMap<>());
        HashMap<String, Integer> location_map = sizeMap.get(size);

        if (add) {
            location_map.put(location, location_map.getOrDefault(location, 0) + 1);
            location_map.putIfAbsent(location.equalsIgnoreCase("warehouse") ? "interiorStore" : "warehouse", 0);
        } else {
            location_map.put(location, location_map.getOrDefault(location, 0) - 1);
        }

        for (Product product : products.values()) {
            if (product.getCategory().equalsIgnoreCase(category) && product.getSubCategory().equalsIgnoreCase(sub_category)) {
                if (location.equalsIgnoreCase("warehouse")) {
                    product.setQuantityInWarehouse(add ? product.getQuantityInWarehouse() + 1 : product.getQuantityInWarehouse() - 1);
                } else if (location.equalsIgnoreCase("interiorStore")) {
                    product.setQuantityInStore(add ? product.getQuantityInStore() + 1 : product.getQuantityInStore() - 1);
                }
            }
        }
    }

    /**
     * Creates and initializes a new Product object from a line of CSV input fields.
     * This method parses the CSV fields and calculates pricing and discount data accordingly.
     * Expected CSV field indices:
     * [1]  product name
     * [5]  catalog number
     * [6]  category
     * [7]  sub-category
     * [9]  cost price before discount
     * [10] product demand level
     * [11] supply time
     * [12] manufacturer
     * [13] supplier discount
     * [14] store discount
     *
     * @param fields A string array representing a line from the CSV file.
     * @return A fully initialized Product object.
     */
    private Product buildProductFromCSV(String[] fields) {
        int catalogNumber = Integer.parseInt(fields[5]);
        Product product = new Product();
        product.setCatalogNumber(catalogNumber);
        product.setProductName(fields[1]);
        product.setCategory(fields[6]);
        product.setSubCategory(fields[7]);
        product.setProductDemandLevel(Integer.parseInt(fields[10]));
        product.setSupplyTime(Integer.parseInt(fields[11]));
        product.setManufacturer(fields[12]);

        double cost_before = Double.parseDouble(fields[9]);
        int supplier_discount = Integer.parseInt(fields[13]);
        int store_discount = Integer.parseInt(fields[14]);

        double cost_after = cost_before * (1 - supplier_discount / 100.0);
        double sale_before = cost_after * 2;
        double sale_after = sale_before * (1 - store_discount / 100.0);

        product.setSupplierDiscount(supplier_discount);
        product.setCostPriceBeforeSupplierDiscount(cost_before);
        product.setCostPriceAfterSupplierDiscount(cost_after);
        product.setStoreDiscount(store_discount);
        product.setSalePriceBeforeStoreDiscount(sale_before);
        product.setSalePriceAfterStoreDiscount(sale_after);
        product.setQuantityInWarehouse(0);
        product.setQuantityInStore(0);

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
}