package Domain;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Controller responsible for managing individual items, their relation to products,
 * and handling item creation and storage logic.
 */
public class ItemController {
    private final HashMap<Integer, Item> items;
    private final HashMap<Integer, Product> products;
    private final HashMap<Integer, Item> purchased_items;


    /**
     * Constructs an ItemController with existing collections of items, products, and purchased items.
     *
     * @param items A map of existing items, keyed by item ID.
     * @param products A map of existing products, keyed by catalog number.
     * @param purchased_items A map of purchased items, keyed by item ID.
     */
    public ItemController(HashMap<Integer, Item> items, HashMap<Integer, Product> products, HashMap<Integer, Item> purchased_items) {
        this.items = items;
        this.products = products;
        this.purchased_items = purchased_items;
    }

    /**
     * Adds a new item to the inventory using data provided in a CSV-formatted string.
     * If the associated product (by catalog number) does not already exist, a new product is created and added to the system.
     * Expected CSV format (15 fields, in order):
     * item_Id, product_name, expiring_date, location, section, catalog_number, category, sub_category,
     * size, cost_price_before, demand, supply_time, manufacturer, supplier_discount, store_discount
     * @param csvInput A comma-separated string containing all 15 fields in the specified order.
     * @return true if the item was added successfully; false if there was an error (e.g., parsing failure).
     */
    public boolean addItem(String csvInput) {
        try {
            String[] fields = csvInput.split(",");
            int item_Id = Integer.parseInt(fields[0]);
            String product_name = fields[1];
            String expiring_date = fields[2];
            String location = fields[3];
            String section = fields[4];
            int catalog_number = Integer.parseInt(fields[5]);
            String category = fields[6];
            String sub_category = fields[7];
            int size = Integer.parseInt(fields[8]);
            double cost_price_before = Double.parseDouble(fields[9]);
            int demand = Integer.parseInt(fields[10]);
            int supply_time = Integer.parseInt(fields[11]);
            String manufacturer = fields[12];
            int supplier_discount = Integer.parseInt(fields[13]);
            int store_discount = Integer.parseInt(fields[14]);

            Item item = new Item();
            item.setItemId(item_Id);
            item.setItemExpiringDate(expiring_date);
            item.setStorageLocation(location);
            item.setSectionInStore(section);
            item.setItemSize(size);
            item.setCatalog_number(catalog_number);
            item.setDefect(false);
            items.put(item_Id, item);

            if (!products.containsKey(catalog_number)) {
                Product product = new Product();
                product.setCatalogNumber(catalog_number);
                product.setProductName(product_name);
                product.setCategory(category);
                product.setSubCategory(sub_category);
                product.setProductDemandLevel(demand);
                product.setSupplyTime(supply_time);
                product.setManufacturer(manufacturer);
                product.setSupplierDiscount(supplier_discount);
                product.setCostPriceBeforeSupplierDiscount(cost_price_before);

                double cost_after = cost_price_before * (1 - supplier_discount / 100.0);
                double sale_before = cost_after * 2;
                double sale_after = sale_before * (1 - store_discount / 100.0);

                product.setCostPriceAfterSupplierDiscount(cost_after);
                product.setStoreDiscount(store_discount);
                product.setSalePriceBeforeStoreDiscount(sale_before);
                product.setSalePriceAfterStoreDiscount(sale_after);

                product.setQuantityInWarehouse(0);
                product.setQuantityInStore(0);

                int min_required = (int) (0.5 * demand + 0.5 * supply_time);
                product.setMinimumQuantityForAlert(min_required);

                products.put(catalog_number, product);
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks whether an item with the given item ID exists in the current inventory.
     *
     * @param item_Id The unique identifier of the item to check.
     * @return true if the item exists in the inventory; false otherwise.
     */
    public boolean itemExists(int item_Id) {
        return items.containsKey(item_Id);
    }

    /**
     * Removes an item from the inventory due to a purchase,
     * and moves it to the map of purchased items.
     *
     * @param item_Id The unique identifier of the purchased item.
     */
    public void removeItemByPurchase(int item_Id) {
        Item item = items.remove(item_Id);
        if (item != null) {
            purchased_items.put(item_Id, item);
        }
    }

    /**
     * Retrieves the sale price of a product after applying the store discount,
     * based on the catalog number of the given item.
     *
     * @param item_Id The unique identifier of the item.
     * @return The sale price after store discount, or 0.0 if the item or product is not found.
     */
    public double getSalePriceAfterDiscount(int item_Id) {
        Item item = items.get(item_Id);
        if (item == null) return 0.0;

        Product product = products.get(item.getCatalogNumber());
        if (product == null) return 0.0;

        return product.getSalePriceAfterStoreDiscount();
    }

    /**
     * Removes an item from the inventory due to being marked as defective.
     *
     * @param item_Id The unique identifier of the defective item to remove.
     */
    public void removeItemByDefect(int item_Id) {
        items.remove(item_Id);
    }

    /**
     * Marks a specific item as defective.
     *
     * @param item_Id The unique identifier of the item to mark as defective.
     * @return true if the item exists and was marked as defective; false otherwise.
     */
    public boolean markItemAsDefective(int item_Id) {
        Item item = items.get(item_Id);
        if (item == null) return false;

        item.setDefect(true);
        return true;
    }


    /**
     * Checks whether a reorder alert should be triggered for the product
     * associated with the given item ID.
     * A reorder alert is triggered if the number of non-defective items
     * with the same catalog number is less than the product's minimum required quantity,
     * which is retrieved using {@code getMinimumQuantityForAlert()}.
     *
     * @param item_Id The ID of the item whose product should be checked.
     * @return true if a reorder alert should be raised; false otherwise (e.g., sufficient stock or item not found).
     */
    public boolean checkReorderAlert(int item_Id) {
        Item item = items.get(item_Id);
        if (item == null) return false;

        int catalog_number = item.getCatalogNumber();
        Product product = products.get(catalog_number);
        if (product == null) return false;

        int min_required = product.getMinimumQuantityForAlert();

        long count = items.values().stream()
                .filter(i -> i.getCatalogNumber() == catalog_number && !i.isDefect())
                .count();

        return count < min_required;
    }

    /**
     * Retrieves the product name associated with a specific item.
     *
     * @param item_Id The unique identifier of the item.
     * @return The name of the product the item belongs to, or an empty string if not found.
     */
    public String getItemName(int item_Id) {
        Item item = items.get(item_Id);
        if (item == null) return "";
        Product product = products.get(item.getCatalogNumber());
        return product != null ? product.getProductName() : "";
    }

    /**
     * Updates the location and/or section of a specific item in the inventory.
     * @param item_Id  The unique identifier of the item to update.
     * @param location The new storage location (e.g., "Warehouse" or "InteriorStore"). If null, location remains unchanged.
     * @param section  The new section within the location (e.g., "A1"). If null, section remains unchanged.
     * @return true if the item exists and was updated; false otherwise.
     */
    public boolean updateItemLocation(int item_Id, String location, String section) {
        Item item = items.get(item_Id);
        if (item != null) {
            if (location != null) item.setStorageLocation(location);
            if (section != null) item.setSectionInStore(section);
            return true;
        }
        return false;
    }


    /**
     * Generates a detailed string summary of an item's information,
     * including its properties and related product details.
     *
     * @param item_Id The unique identifier of the item.
     * @return A detailed description of the item and its associated product,
     *         or an error message if the item or product was not found.
     */
    public String showItemDetails(int item_Id) {
        Item item = items.get(item_Id);
        if (item == null) {
            return "Item with ID " + item_Id + " not found in stock.";
        }

        Product product = products.get(item.getCatalogNumber());
        if (product == null) {
            return "Product with Product Catalog Number " + item.getCatalogNumber() + " not found.";
        }

        DecimalFormat df = new DecimalFormat("#.00");

        return "Item ID: " + item.getItemId() + "\n"
                + "Product name: " + product.getProductName() + "\n"
                + "Expiring Date: " + item.getItemExpiringDate() + "\n"
                + "Location: " + item.getStorageLocation() + ", Section: " + item.getSectionInStore() + "\n"
                + "Product Catalog Number: " + product.getCatalogNumber() + ", Category: "
                + product.getCategory() + ", Sub-Category: " + product.getSubCategory() + "\n"
                + "Size: " + item.getItemSize() + "\n"
                + "Supplier Discount: " + product.getSupplierDiscount() + "%\n"
                + "Cost price before supplier discount: " + df.format(product.getCostPriceBeforeSupplierDiscount()) + "\n"
                + "Cost price after supplier discount: " + df.format(product.getCostPriceAfterSupplierDiscount()) + "\n"
                + "Store Discount: " + product.getStoreDiscount() + "%\n"
                + "Sale price before store discount: " + df.format(product.getSalePriceBeforeStoreDiscount()) + "\n"
                + "Sale price after store discount: " + df.format(product.getSalePriceAfterStoreDiscount()) + "\n"
                + "Product demand: " + product.getProductDemandLevel() + "\n"
                + "Supply time: " + product.getSupplyTime() + " days\n"
                + "Manufacturer: " + product.getManufacturer() + "\n"
                + "Defective: " + (item.isDefect() ? "Yes" : "No") + "\n";
    }

}
