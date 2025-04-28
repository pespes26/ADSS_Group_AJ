package Domain;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

/**
 * Controller responsible for managing products in the inventory system.
 * Provides operations for updating product pricing, supply details,
 * and tracking purchased items.
 */
public class ProductController {

    private final HashMap<Integer, Product> products; // Map of all products, keyed by catalog number
    private final HashMap<Integer, Item> purchased_items; // Map of purchased items, keyed by item ID
    private final HashMap<Integer, Branch> branches; // Map of all branches, keyed by branch ID

    /**
     * Constructs a ProductController with given maps of products and purchased items.
     *
     * @param products A map of all products, keyed by catalog number.
     * @param purchased_items A map of all purchased items, keyed by catalog number.
     */
    public ProductController(HashMap<Integer, Product> products, HashMap<Integer, Item> purchased_items) {
        this.products = products;
        this.purchased_items = purchased_items;
        this.branches = new HashMap<>();
    }

    /**
     * Injects the branches map (needed to properly calculate per-branch operations).
     * This must be called once during system initialization.
     */
    public void setBranches(HashMap<Integer, Branch> branches) {
        this.branches.clear();
        this.branches.putAll(branches);
    }

    /**
     * Updates the cost price of a product by its catalog number.
     * Also recalculates the cost price after supplier discount and sale prices.
     *
     * @param catalog_number The catalog number of the product to update.
     * @param new_price The new base cost price (before supplier discount).
     * @return true if a matching product was found and updated, false otherwise.
     */
    public boolean updateCostPriceByCatalogNumber(int catalog_number, double new_price) {
        boolean found = false;
        for (Product p : products.values()) {
            if (p.getCatalogNumber() == catalog_number) {
                p.setCostPriceBeforeSupplierDiscount(new_price);
                double costAfter = new_price * (1 - p.getSupplierDiscount() / 100.0);
                p.setCostPriceAfterSupplierDiscount(costAfter);

                double saleBefore = costAfter * 2;
                double saleAfter = saleBefore * (1 - p.getStoreDiscount() / 100.0);
                p.setSalePriceBeforeStoreDiscount(saleBefore);
                p.setSalePriceAfterStoreDiscount(saleAfter);

                found = true;
            }
        }
        return found;
    }

    /**
     * Updates the supply time and/or demand level for a specific product.
     *
     * @param catalog_number The catalog number of the product to update.
     * @param supply_time The new supply time in days. If null, this value is not updated.
     * @param demand The new demand level. If null, this value is not updated.
     * @return true if the product was found and updated, false otherwise.
     */
    public boolean updateProductSupplyDetails(int catalog_number, Integer supply_time, Integer demand) {
        Product product = products.get(catalog_number);
        if (product == null) {
            return false;
        }

        if (supply_time != null) {
            product.setSupplyTime(supply_time);
        }
        if (demand != null) {
            product.setProductDemandLevel(demand);
        }
        return true;
    }

    /**
     * Checks if there is at least one product with the given category.
     *
     * @param category The category name to search for.
     * @return true if a product with the given category exists, false otherwise.
     */
    public boolean hasCategory(String category) {
        for (Product product : products.values()) {
            if (product.getCategory().equalsIgnoreCase(category)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if there is at least one product with the given subcategory.
     *
     * @param sub_category The subcategory name to search for.
     * @return true if a product with the given subcategory exists, false otherwise.
     */
    public boolean hasSubCategory(String sub_category) {
        for (Product product : products.values()) {
            if (product.getSubCategory().equalsIgnoreCase(sub_category)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether a given catalog number does not exist in the product inventory.
     *
     * @param catalog_number The catalog number to check.
     * @return true if the catalog number is not found in the product list, false otherwise.
     */
    public boolean isUnknownCatalogNumber(int catalog_number) {
        return !products.containsKey(catalog_number);
    }

    /**
     * Displays the quantities of a specific product in a given branch,
     * separated by warehouse and store locations.
     * If the product or branch doesn't exist, an appropriate message is returned.
     * Ignores defective items when calculating quantities.
     *
     * @param catalog_number the catalog number of the product
     * @param branch_id the branch ID
     * @return a formatted string showing warehouse and store quantities
     */
    public String showProductQuantities(int catalog_number, int branch_id) {
        Product product = products.get(catalog_number);
        if (product == null) {
            return "Invalid Product Catalog Number: " + catalog_number + ". This Product Catalog Number does not exist in Branch " + branch_id + ".";
        }

        Branch branch = branches.get(branch_id);
        if (branch == null) {
            return "Branch ID " + branch_id + " not found.";
        }

        int warehouse_quantity = 0;
        int store_quantity = 0;

        for (Item item : branch.getItems().values()) {
            if (item.getCatalogNumber() == catalog_number && !item.isDefect()) {
                if (item.getStorageLocation().equalsIgnoreCase("Warehouse")) {
                    warehouse_quantity++;
                } else if (item.getStorageLocation().equalsIgnoreCase("InteriorStore")) {
                    store_quantity++;
                }
            }
        }

        if (warehouse_quantity == 0 && store_quantity == 0) {
            return "No items found for Product Catalog Number: " + catalog_number + " in Branch " + branch_id;
        }

        return "Branch: " + branch_id + "\n"
                + "Product Catalog Number: " + catalog_number + "\n"
                + "Warehouse quantity: " + warehouse_quantity + "\n"
                + "Store quantity: " + store_quantity;
    }

    /**
     * Displays the sale prices for a specific product purchased in a given branch.
     * Each sale includes the price and the sale date.
     * If no purchases are found, an appropriate message is returned.
     *
     * @param catalog_number the catalog number of the product
     * @param currentBranchId the ID of the current branch
     * @return a formatted string listing purchase prices and dates
     */
    public String showProductPurchasesPrices(int catalog_number, int currentBranchId) {
        DecimalFormat df = new DecimalFormat("#.00");
        StringBuilder result = new StringBuilder();
        boolean found = false;
        int count = 1;

        for (Item item : purchased_items.values()) {
            if (item.getCatalogNumber() == catalog_number && item.getBranchId() == currentBranchId) {
                Product product = products.get(catalog_number);
                if (product != null) {
                    found = true;
                    result.append(count++).append(". ")
                            .append(df.format(product.getSalePriceAfterStoreDiscount()))
                            .append(" ₪ (Sale Date: ");
                    if (item.getSaleDate() != null) {
                        result.append(item.getSaleDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    } else {
                        result.append("No Date");
                    }
                    result.append(")\n");
                }
            }
        }

        if (!found) {
            return "No purchased items found in Branch " + currentBranchId + " with Product Catalog Number: " + catalog_number;
        }

        return "Sale prices for Product Catalog Number " + catalog_number + " (Branch " + currentBranchId + "):\n" + result;
    }

    /**
     * Adds a new product to the inventory system.
     *
     * @param product the Product object to add
     */
    public void addProduct(Product product) {
        products.put(product.getCatalogNumber(), product);
    }







}
