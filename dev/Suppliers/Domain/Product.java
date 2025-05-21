package Suppliers.Domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a product offered by a supplier.
 * Each product has a unique catalog number, product ID, price, unit of measure,
 * and a list of discount rules based on quantity.
 */
public class Product {
    int supplierID;
    int Catalog_Number;
    int product_id;
    double Price;
    String unitsOfMeasure;

    /**
     * Represents a discount rule for a product.
     * The discount is applied when the amount ordered is greater than or equal to the specified quantity.
     */
    public record DiscountRule(double discount, int amount) {} // This is the content of the discount, implemented using a record instead of a separate class

    List<DiscountRule> discountRules = new ArrayList<>(); // List of discounts

    /**
     * Constructs a new product with the given attributes.
     *
     * @param catalog_Number the catalog number of the product
     * @param product_id the unique product ID
     * @param price the price of the product
     * @param unitsOfMeasure the unit of measurement (e.g., kg, unit)
     * @param supplierID the supplier's ID who supplies this product
     */
    public Product(int catalog_Number, int product_id, double price, String unitsOfMeasure, int supplierID){
        this.supplierID = supplierID;
        this.Catalog_Number = catalog_Number;
        this.product_id = product_id;
        this.Price = price;
        this.unitsOfMeasure = unitsOfMeasure;
    }

    /**
     * Sets the unit of measurement for this product.
     *
     * @param unitsOfMeasure the new unit of measurement
     */
    public void setUnitsOfMeasure(String unitsOfMeasure) {
        this.unitsOfMeasure = unitsOfMeasure;
    }

    /**
     * Returns the product's ID.
     *
     * @return the product ID
     */
    public int getProduct_id() {
        return product_id;
    }

    /**
     * Returns the product's catalog number.
     *
     * @return the catalog number
     */
    public int getCatalog_Number() {
        return Catalog_Number;
    }

    /**
     * Returns the product's price.
     *
     * @return the price
     */
    public double getPrice() {
        return Price;
    }

    /**
     * Sets a new price for the product.
     *
     * @param new_price the new price
     */
    public void setPrice(double new_price){
        this.Price = new_price;
    }

    /**
     * Updates the discount for a given quantity if one exists, or adds a new one otherwise.
     *
     * @param discount the discount percentage
     * @param amount   the quantity from which the discount should apply
     */
    public void updateOrAddDiscountRule(double discount, int amount){
        for (int i = 0; i < discountRules.size(); i++) {
            if (discountRules.get(i).amount() == amount) {
                discountRules.set(i, new DiscountRule(discount, amount));
                return;
            }
        }
        discountRules.add(new DiscountRule(discount, amount));
    }

    /**
     * Returns the supplier ID of the product.
     *
     * @return the supplier ID
     */
    public int getSupplierID() {
        return supplierID;
    }

    /**
     * Calculates the best discount applicable for a given quantity.
     * If multiple rules apply, the highest discount is chosen.
     *
     * @param amount the quantity of items ordered
     * @return the highest applicable discount percentage
     */
    public double calcDiscount(int amount){
        double current_discount = 0;
        for (int i = 0; i < discountRules.size(); i++) { // Check all the discounts
            DiscountRule rule = discountRules.get(i); // Get the current discount rule from the list
            if (amount >= rule.amount) { // Check if the amount is greater than the rule's amount
                if (rule.discount > current_discount) { // Update the discount if higher
                    current_discount = rule.discount;
                }
            }
        }
        return current_discount; // Return the highest applicable discount
    }

    /**
     * Calculates the total price after applying the best discount for a given quantity.
     *
     * @param amount the quantity of the product
     * @return the total price after applying the discount
     */
    public double get_price_after_discount(int amount){
        double discount = calcDiscount(amount); // Calculate the applicable discount
        discount = 1 - (discount / 100.0); // Convert to multiplier
        return getPrice() * discount; // Return price after discount
    }
}