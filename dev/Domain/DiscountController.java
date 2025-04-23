package Domain;

import java.util.HashMap;

/**
 * Controller responsible for applying discounts to products in the inventory,
 * based on category, sub-category, or catalog number.
 */
public class DiscountController {
    private final HashMap<Integer, Product> products; // All products in the system, keyed by catalog number

    /**
     * Constructs a DiscountController with access to the map of products.
     *
     * @param products A map of all products in the system, keyed by catalog number.
     */
    public DiscountController(HashMap<Integer, Product> products) {
        this.products = products;
    }

    /**
     * Applies a store discount to all products that match a specific category.
     *
     * @param category The category name to apply the discount to.
     * @param discount The discount object containing discount value and validity period.
     * @return true if at least one product was updated; false otherwise.
     */
    public boolean setStoreDiscountForCategory(String category, Discount discount) {
        return applyDiscountToGroup(discount, category, null, -1, DiscountType.STORE);
    }

    /**
     * Applies a store discount to all products within a specific sub-category.
     *
     * @param sub_category The name of the sub-category to apply the discount to.
     * @param discount     The discount object containing the discount value and validity period.
     * @return true if at least one product was updated; false otherwise.
     */
    public boolean setStoreDiscountForSubCategory(String sub_category, Discount discount) {
        return applyDiscountToGroup(discount, null, sub_category, -1, DiscountType.STORE);
    }

    /**
     * Applies a store discount to a single product identified by its catalog number.
     *
     * @param catalogNumber The catalog number of the product to apply the discount to.
     * @param discount      The discount object containing the discount value and validity period.
     * @return true if the product was updated; false otherwise.
     */
    public boolean setStoreDiscountForCatalogNumber(int catalogNumber, Discount discount) {
        return applyDiscountToGroup(discount, null, null, catalogNumber, DiscountType.STORE);
    }

    /**
     * Applies a supplier discount to all products within a specific category.
     *
     * @param category The name of the category to apply the discount to.
     * @param discount The discount object containing the discount value and validity period.
     * @return true if at least one product was updated; false otherwise.
     */
    public boolean setSupplierDiscountForCategory(String category, Discount discount) {
        return applyDiscountToGroup(discount, category, null, -1, DiscountType.SUPPLIER);
    }

    /**
     * Applies a supplier discount to all products within a specific sub-category.
     *
     * @param sub_category The name of the sub-category to apply the discount to.
     * @param discount     The discount object containing the discount value and validity period.
     * @return true if at least one product was updated; false otherwise.
     */
    public boolean setSupplierDiscountForSubCategory(String sub_category, Discount discount) {
        return applyDiscountToGroup(discount, null, sub_category, -1, DiscountType.SUPPLIER);
    }

    /**
     * Applies a supplier discount to a single product identified by its catalog number.
     *
     * @param catalogNumber The catalog number of the product to apply the discount to.
     * @param discount      The discount object containing the discount value and validity period.
     * @return true if the product was updated; false otherwise.
     */
    public boolean setSupplierDiscountForCatalogNumber(int catalogNumber, Discount discount) {
        return applyDiscountToGroup(discount, null, null, catalogNumber, DiscountType.SUPPLIER);
    }

    /**
     * Applies a discount to products matching the specified filter: category, sub-category, or catalog number.
     * The method first validates the discount's date range.
     * A product is considered a match if:
     * - Its category matches the given category (case-insensitive), or
     * - Its sub-category matches the given sub-category (case-insensitive), or
     * - Its catalog number matches the given catalog number (if catalogNumber != -1).
     * The discount type determines whether the discount is applied as a store discount or supplier discount.
     * The method also recalculates cost and sale prices after applying the discount.
     *
     * @param discount      The discount object containing rate, start date, and end date.
     * @param category      The category name to match (nullable).
     * @param sub_category  The sub-category name to match (nullable).
     * @param catalogNumber The specific catalog number to match, or -1 if unused.
     * @param type          The type of discount to apply (STORE or SUPPLIER).
     * @return true if at least one product was updated; false if the discount is invalid or no products matched.
     */
    private boolean applyDiscountToGroup(Discount discount, String category, String sub_category, int catalogNumber, DiscountType type) {
        if (discount.getStartDate() == null || discount.getEndDate() == null || discount.getEndDate().isBefore(discount.getStartDate())) {
            return false;
        }

        boolean applied = false;
        for (Product product : products.values()) {
            boolean match = (product.getCategory().equalsIgnoreCase(category))
                    || (product.getSubCategory().equalsIgnoreCase(sub_category))
                    || (catalogNumber != -1 && product.getCatalogNumber() == catalogNumber);

            if (match) {
                if (type == DiscountType.STORE) {
                    product.setStoreDiscount(discount.getDiscountRate());
                } else {
                    product.setSupplierDiscount(discount.getDiscountRate());
                }

                product.setDiscount(discount);
                recalculatePrices(product);
                applied = true;

                if (catalogNumber != -1) break;
            }
        }

        return applied;
    }

    /**
     * Recalculates the product's pricing after applying supplier and/or store discounts.
     * - Cost price after supplier discount is calculated as:
     *   cost_price_before × (1 - supplier_discount%)
     * - Sale price before store discount is calculated as:
     *   cost_after × 2
     * - Sale price after store discount is calculated as:
     *   sale_before × (1 - store_discount%)
     * The method updates the product's pricing fields accordingly.
     *
     * @param product The product whose prices need to be updated.
     */
    private void recalculatePrices(Product product) {
        double cost_after = product.getCostPriceBeforeSupplierDiscount() * (1 - product.getSupplierDiscount() / 100.0);
        double sale_before = cost_after * 2;
        double sale_after = sale_before * (1 - product.getStoreDiscount() / 100.0);

        product.setCostPriceAfterSupplierDiscount(cost_after);
        product.setSalePriceBeforeStoreDiscount(sale_before);
        product.setSalePriceAfterStoreDiscount(sale_after);
    }
}