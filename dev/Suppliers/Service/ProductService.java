package Suppliers.Service;


import Suppliers.Domain.Product;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * ProductService handles the management of products in the system.
 * It supports creating products, deleting them, checking for existence,
 * and calculating the best price after applying discounts.
 */
public class ProductService {
    private List<Product> productList;

    /**
     * Initializes the product service with an empty product list.
     */
    public ProductService(){
        this.productList = new ArrayList<>();
    }

    /**
     * Creates a new product and adds it to the internal product list.
     *
     * @param catalog_Number   the product's catalog number
     * @param product_id       unique product ID
     * @param price            base price of the product
     * @param unitsOfMeasure   unit of measurement (e.g., kg, piece)
     * @param supplierID       the supplier's ID who provides this product
     * @return the created Product object
     */
    public Product createProduct(int catalog_Number, int product_id, double price, String unitsOfMeasure, int supplierID) {
        Product product = new Product(catalog_Number, product_id, price, unitsOfMeasure, supplierID);
        this.productList.add(product);
        return product;
    }

    /**
     * Deletes a product from the list based on its catalog number and supplier ID.
     *
     * @param catalog     catalog number of the product
     * @param supplierId  ID of the supplier providing the product
     */
    public void delete_by_catalogAndSupplierId(int catalog, int supplierId) {
        Iterator<Product> iterator = productList.iterator();
        while (iterator.hasNext()) {
            Product product = iterator.next();
            if (product.getSupplierID() == supplierId && product.getCatalog_Number() == catalog) {
                iterator.remove(); // Safe removal while iterating
            }
        }
    }

    /**
     * Checks if a product with the given product ID exists.
     *
     * @param ProductID product ID to search
     * @return true if such a product exists, false otherwise
     */
    public boolean productExistsProductWithID(int ProductID){
        for (Product product : productList) {
            if (product.getProduct_id() == ProductID) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a product exists based on its catalog number.
     *
     * @param catalog the catalog number to check
     * @return true if the product exists, false otherwise
     */
    public boolean productExistsByCatalog(int catalog) {
        for (Product product : productList) {
            if (product.getCatalog_Number() == catalog) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether there are zero products in the system.
     *
     * @return true if no products exist, false otherwise
     */
    public boolean existsZeroProducts() {
        return productList.isEmpty();
    }

    /**
     * Calculates the best total price for a product ID based on the given amount,
     * applying the most suitable discount rule.
     *
     * @param product_id the ID of the product
     * @param amount     the quantity being ordered
     * @return the best total price, or -1 if the product is not found
     */
    public double best_price(int product_id, int amount){
        double final_price = Double.MAX_VALUE;
        for (Product product : productList) {
            if (product.getProduct_id() == product_id) {
                double current_price = product.get_price_after_discount(amount);
                if (current_price < final_price){
                    final_price = current_price;
                }
            }
        }
        if (final_price == Double.MAX_VALUE){
            return -1;
        }
        return final_price * amount;
    }

    /**
     * Checks if a product exists by its catalog number and supplier ID.
     *
     * @param catalog_Number the product's catalog number
     * @param supplierID     the ID of the supplier
     * @return true if a product exists with the given criteria, false otherwise
     */
    public boolean existsProductWithCatalogAndSupplierId(int catalog_Number, int supplierID){
        for (Product product : productList) {
            if (product.getCatalog_Number() == catalog_Number && product.getSupplierID() == supplierID) {
                return true;
            }
        }
        return false;
    }
}
