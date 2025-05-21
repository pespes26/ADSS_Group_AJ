package Inventory.Domain;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.*;

/**
 * Controller responsible for managing individual items, their relation to products,
 * and handling item creation and storage logic.
 */
public class ItemController {
    private final HashMap<Integer, Branch> branches;
    private final HashMap<Integer, Product> products;
    private final HashMap<Integer, Item> purchased_items;

    /**
     * Constructs an ItemController with branches, product map, and purchased item records.
     *
     * @param branches A map of all store branches, keyed by branch ID.
     * @param products A map of existing products, keyed by catalog number.
     * @param purchased_items A map of purchased items, keyed by item ID.
     */
    public ItemController(HashMap<Integer, Branch> branches, HashMap<Integer, Product> products, HashMap<Integer, Item> purchased_items) {
        this.branches = branches;
        this.products = products;
        this.purchased_items = purchased_items;
    }

    /**
     * Returns the next available unique Item ID.
     *
     * <p>
     * This method scans all existing items and finds the highest ID,
     * then returns the next number (highest ID + 1).
     * Guarantees that no duplicate item IDs are generated.
     *
     * @return the next available Item ID
     */
    public int getNextAvailableItemId() {
        if (purchased_items.isEmpty()) {
            return 1; // Start from 1 if no items exist
        }
        int maxId = purchased_items.keySet().stream().max(Integer::compareTo).orElse(0);
        return maxId + 1;
    }




    /**
     * Adds a single item directly to the inventory (without using CSV).
     *
     * @param itemId the unique ID of the new item
     * @param branchId the branch where the item is stored
     * @param catalogNumber the catalog number of the associated product
     * @param storageLocation the location where the item is stored (Warehouse or InteriorStore)
     * @param expiryDate the expiry date of the item in dd/MM/yyyy format
     */
    public void addItem(int itemId, int branchId, int catalogNumber, String storageLocation, String expiryDate) {
        Item newItem = new Item();
        newItem.setItemId(itemId);
        newItem.setBranchId(branchId);
        newItem.setCatalog_number(catalogNumber);
        newItem.setStorageLocation(storageLocation);
        newItem.setItemExpiringDate(expiryDate);
        newItem.setDefect(false); // new items are not defective by default

        // Insert into the branch
        Branch branch = branches.computeIfAbsent(branchId, k -> new Branch(branchId));
        branch.getItems().put(itemId, newItem);

        // Also insert into the general purchased items map
        purchased_items.put(itemId, newItem);
    }





    public boolean itemExistsInBranch(int item_Id, int branch_id) {
        Branch branch = branches.get(branch_id);
        if (branch == null) return false;
        return branch.getItems().containsKey(item_Id);
    }



    public void removeItemByDefect(int item_Id, int branch_id) {
        Branch branch = branches.get(branch_id);
        if (branch != null && branch.getItems().containsKey(item_Id)) {
            Item item = branch.getItem(item_Id);
            if (item != null) {
                item.setDefect(true);
                branch.removeItem(item_Id);
            }
        }
    }

    public void removeItemByPurchase(int item_Id, int branchId) {
        Branch branch = branches.get(branchId);
        if (branch != null && branch.getItems().containsKey(item_Id)) {
            Item item = branch.getItem(item_Id);
            branch.removeItem(item_Id);

            if (item != null) {
                item.setSaleDate(LocalDate.now());
                purchased_items.put(item_Id, item);
            }
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
        for (Branch branch : branches.values()) {
            Item item = branch.getItems().get(item_Id);
            if (item != null) {
                Product product = products.get(item.getCatalogNumber());
                if (product != null) return product.getSalePriceAfterStoreDiscount();
            }
        }
        return 0.0;
    }




    /**
     * Marks a specific item as defective.
     *
     * @param item_Id The unique identifier of the item to mark as defective.
     * @return true if the item exists and was marked as defective; false otherwise.
     */
    public boolean markItemAsDefective(int item_Id, int branch_id) {
        Branch branch = branches.get(branch_id);
        if (branch != null) {
            Item item = branch.getItems().get(item_Id);
            if (item != null) {
                item.setDefect(true);
                return true;
            }
        }
        return false;
    }





    /**
     * Retrieves the product name associated with a specific item.
     *
     * @param item_Id The unique identifier of the item.
     * @return The name of the product the item belongs to, or an empty string if not found.
     */
    public String getItemName(int item_Id, int branch_id) {
        Branch branch = branches.get(branch_id);
        if (branch == null) return "";
        Item item = branch.getItems().get(item_Id);
        if (item == null) return "";
        Product product = products.get(item.getCatalogNumber());
        return product != null ? product.getProductName() : "";
    }


    /**
     * Updates the location and/or section of a specific item in a specific branch.
     *
     * @param item_Id  The unique identifier of the item to update.
     * @param branch_Id The ID of the branch where the item is located.
     * @param location The new storage location (e.g., "Warehouse" or "InteriorStore"). If null, location remains unchanged.
     * @param section  The new section within the location (e.g., "A1"). If null, section remains unchanged.
     * @return true if the item exists in the specified branch and was updated; false otherwise.
     */
    public boolean updateItemLocation(int item_Id, int branch_Id, String location, String section) {
        Branch branch = branches.get(branch_Id);
        if (branch == null) {
            return false;
        }
        Item item = branch.getItems().get(item_Id);
        if (item != null) {
            if (location != null) item.setStorageLocation(location);
            if (section != null) item.setSectionInStore(section);
            return true;
        }
        return false;
    }


    /**
     * Returns a detailed string with information about a specific item in a branch.
     * <p>
     * Displays product name, expiration date, location, catalog number, pricing details,
     * discounts, supply time, and defect status.
     *
     * @param item_Id the ID of the item
     * @param branch_id the ID of the branch where the item is located
     * @return a formatted string with item and product details, or an error message if not found
     */

    public String showItemDetails(int item_Id, int branch_id) {
        Branch branch = branches.get(branch_id);
        if (branch == null) {
            return "There is not item in branch id with ID " + branch_id + " does not exist.";
        }

        Item item = branch.getItem(item_Id);
        if (item == null) {
            return "Item with ID " + item_Id + " not found in branch " + branch_id + ".";
        }

        Product product = products.get(item.getCatalogNumber());
        if (product == null) {
            return "Product with catalog number " + item.getCatalogNumber() + " not found.";
        }

        DecimalFormat df = new DecimalFormat("#.00");

        return "Item ID: " + item.getItemId() + "\n"
                + "Product name: " + product.getProductName() + "\n"
                + "Expiring Date: " + item.getItemExpiringDate() + "\n"
                + "Location: " + item.getStorageLocation() + ", Section: " + item.getSectionInStore() + "\n"
                + "Product Catalog Number: " + product.getCatalogNumber() + ", Category: "
                + product.getCategory() + ", Sub-Category: " + product.getSubCategory() + "\n"
                + "Size: " + product.getSize() + "\n"
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

    /**
     * Retrieves the catalog number of a specific item in a branch.
     * <p>
     * Used for linking an item to its corresponding product.
     *
     * @param item_Id the ID of the item
     * @param branch_id the ID of the branch where the item is located
     * @return the catalog number if found, or -1 if not found
     */
    public int getCatalogNumber(int item_Id, int branch_id) {
        Branch branch = branches.get(branch_id);
        if (branch == null || !branch.getItems().containsKey(item_Id)) return -1;
        return branch.getItems().get(item_Id).getCatalogNumber();
    }

}


