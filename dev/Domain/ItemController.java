package Domain;

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


    public boolean addItem(String csvInput) {
        try {
            String[] fields = csvInput.split(",");
            int item_Id = Integer.parseInt(fields[0]);
            int branch_id = Integer.parseInt(fields[1]);

            String product_name = fields[2];
            String expiring_date = fields[3];
            String location = fields[4];
            String section = fields[5];
            int catalog_number = Integer.parseInt(fields[6]);
            String category = fields[7];
            String sub_category = fields[8];
            int size = Integer.parseInt(fields[9]);
            double cost_price_before = Double.parseDouble(fields[10]);
            int demand = Integer.parseInt(fields[11]);
            int supply_time = Integer.parseInt(fields[12]);
            String manufacturer = fields[13];
            int supplier_discount = Integer.parseInt(fields[14]);
            int store_discount = Integer.parseInt(fields[15]);

            Item item = new Item();
            item.setItemId(item_Id);
            item.setBranchId(branch_id);
            item.setItemExpiringDate(expiring_date);
            item.setStorageLocation(location);
            item.setSectionInStore(section);
            item.setItemSize(size);
            item.setCatalog_number(catalog_number);
            item.setDefect(false);

            Branch branch = branches.computeIfAbsent(branch_id, k -> new Branch(branch_id));
            branch.getItems().put(item_Id, item);

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






    public boolean itemExistsInBranch(int item_Id, int branch_id) {
        Branch branch = branches.get(branch_id);
        if (branch == null) return false;
        return branch.getItems().containsKey(item_Id);
    }



    public void removeItemByDefect(int item_Id, int branch_id) {
        Branch branch = branches.get(branch_id);
        if (branch != null && branch.getItems().containsKey(item_Id)) {
            branch.getItems().get(item_Id).setDefect(true);
        }
    }

    public void removeItemByPurchase(int item_Id, int branchId) {
        Branch branch = branches.get(branchId);
        if (branch != null && branch.getItems().containsKey(item_Id)) {
            Item item = branch.getItems().remove(item_Id);
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
            return "Branch with ID " + branch_id + " does not exist.";
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
