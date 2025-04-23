package Domain;

import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * Controller responsible for managing products in the inventory system.
 * Provides operations for updating product pricing, supply details,
 * and tracking purchased items.
 */
public class ProductController {

    private final HashMap<Integer, Product> products; // Map of all products, keyed by catalog number
    private final HashMap<Integer, Item> purchased_items; // Map of purchased items, keyed by item ID

    /**
     * Constructs a ProductController with given maps of products and purchased items.
     *
     * @param products A map of all products, keyed by catalog number.
     * @param purchased_items A map of all purchased items, keyed by catalog number.
     */
    public ProductController(HashMap<Integer, Product> products, HashMap<Integer, Item> purchased_items) {
        this.products = products;
        this.purchased_items = purchased_items;
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
     * Returns a detailed summary of the quantities of a specific product in the warehouse and store.
     *
     * @param catalog_number The catalog number of the product.
     * @return A message describing the product's quantities or an error message if the product does not exist.
     */
    public String showProductQuantities(int catalog_number) {
        Product product = products.get(catalog_number);
        if (product == null) {
            return "Invalid Product Catalog Number: " + catalog_number + ". This Product Catalog Number does not exist in the inventory.";
        }

        int warehouseQuantity = product.getQuantityInWarehouse();
        int storeQuantity = product.getQuantityInStore();

        if (warehouseQuantity == 0 && storeQuantity == 0) {
            return "No items found for Product Catalog Number: " + catalog_number;
        }

        return "Product Catalog Number: " + catalog_number + "\n"
                + "Warehouse quantity: " + warehouseQuantity + "\n"
                + "Store quantity: " + storeQuantity;
    }

    /**
     * Returns a list of sale prices (after store discount) for all purchased items
     * associated with a specific product catalog number.
     *
     * @param catalog_number The catalog number of the product to retrieve purchase prices for.
     * @return A formatted string listing the sale prices for each purchased item,
     *         or a message indicating no purchases were found for the given catalog number.
     */
    public String showProductPurchasesPrices(int catalog_number) {
        DecimalFormat df = new DecimalFormat("#.00");
        StringBuilder result = new StringBuilder();
        boolean found = false;
        int count = 1;

        for (Item item : purchased_items.values()) {
            if (item.getCatalogNumber() == catalog_number) {
                Product product = products.get(catalog_number);
                if (product != null) {
                    found = true;
                    result.append(count++).append(". ")
                            .append(df.format(product.getSalePriceAfterStoreDiscount())).append("\n");
                }
            }
        }

        if (!found) {
            return "No purchased items found with Product Catalog Number: " + catalog_number;
        }

        return "Sale prices for Product Catalog Number " + catalog_number + ":\n" + result;
    }


}
