package Inventory.Domain;

import Inventory.DTO.ItemDTO;
import Inventory.DTO.ProductDTO;
import Inventory.Repository.IProductRepository;
import Inventory.Repository.ItemRepositoryImpl;
import Inventory.Repository.ProductRepositoryImpl;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Controller responsible for managing products in the inventory system.
 * Provides operations for updating product pricing, supply details,
 * and tracking purchased items.
 */
public class ProductController {
    private final IProductRepository productRepository;
    private final HashMap<Integer, Product> products; // Map of all products, keyed by catalog number
    private final HashMap<Integer, ItemDTO> purchased_items; // Map of purchased items, keyed by item ID
    private final HashMap<Integer, Branch> branches; // Map of all branches, keyed by branch ID

    /**
     * Constructs a ProductController with given maps of products and purchased items.
     *
     * @param products A map of all products, keyed by catalog number.
     * @param purchased_items A map of all purchased items, keyed by item ID.
     */
    public ProductController(HashMap<Integer, Product> products, HashMap<Integer, ItemDTO> purchased_items) {
        this.products = products;
        this.purchased_items = purchased_items;
        this.branches = new HashMap<>();
        this.productRepository = new ProductRepositoryImpl();
    }

    /**
     * Sets the branch mapping for the system. This method clears the existing branches
     * and replaces them with the provided mapping.
     *
     * @param branches a HashMap containing Branch objects, keyed by branch ID
     */
    public void setBranches(HashMap<Integer, Branch> branches) {
        this.branches.clear();
        this.branches.putAll(branches);
    }

    /**
     * Updates the cost price of a product identified by catalog number and recalculates
     * all derived prices based on the product's supplier and store discounts.
     *
     * @param catalog_number the catalog number of the product to update
     * @param new_price the new base cost price of the product
     * @return true if the product was found and updated; false otherwise
     * @throws SQLException if an error occurs while persisting changes to the database
     */
    public boolean updateCostPriceByCatalogNumber(int catalog_number, double new_price) throws SQLException {
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


    public boolean updateProductSupplyDetails(int catalog_number, String supplyDaysInTheWeek, Integer demand) throws SQLException {
        Product product = products.get(catalog_number);
        if (product == null) return false;

        if (supplyDaysInTheWeek != null)
            product.setSupplyDaysInTheWeek(supplyDaysInTheWeek);
        if (demand != null)
            product.setProductDemandLevel(demand);

        ProductDTO dto = new ProductDTO(
                product.getCatalogNumber(),
                product.getProductName(),
                product.getCategory(),
                product.getSubCategory(),
                product.getSupplierName(),
                product.getSize(),
                product.getCostPriceBeforeSupplierDiscount(),
                product.getSupplierDiscount(),
                product.getStoreDiscount(),
                product.getSupplyDaysInTheWeek(),
                product.getProductDemandLevel()
        );

        productRepository.updateProduct(dto);
        return true;
    }

    /**
     * Checks whether at least one product exists with the given category.
     *
     * @param category the category name to check
     * @return true if a product with the specified category is found; false otherwise
     */
    public boolean hasCategory(String category) {
        return products.values().stream()
                .anyMatch(product -> product.getCategory().equalsIgnoreCase(category));
    }

    /**
     * Checks whether at least one product exists with the given sub-category.
     *
     * @param sub_category the sub-category name to check
     * @return true if a product with the specified sub-category is found; false otherwise
     */
    public boolean hasSubCategory(String sub_category) {
        return products.values().stream()
                .anyMatch(product -> product.getSubCategory().equalsIgnoreCase(sub_category));
    }

    /**
     * Determines if a given catalog number does not exist in the product inventory.
     *
     * @param catalog_number the catalog number to verify
     * @return true if the catalog number is not found; false if the product exists
     */
    public boolean isUnknownCatalogNumber(int catalog_number) {
        return !products.containsKey(catalog_number);
    }

    /**
     * Retrieves the count of non-defective items for a specific product in the specified branch,
     * separated into warehouse and store quantities, using data from the database (via ItemRepository).
     *
     * @param catalog_number the catalog number of the product to query
     * @param branch_id      the ID of the branch in which to count items
     * @return a formatted string indicating warehouse and store quantities,
     *         or an error message if the product does not exist or no items are found
     */
    public String showProductQuantities(int catalog_number, int branch_id) {
        ProductDTO productDTO;
        try {
            productDTO = productRepository.getProductByCatalogNumber(catalog_number);
            if (productDTO == null) {
                return "Invalid Product Catalog Number: " + catalog_number + ". This product does not exist.";
            }
        } catch (Exception e) {
            return "❌ Failed to fetch product info from DB: " + e.getMessage();
        }

        // Use ItemRepository to fetch items from DB
        ItemRepositoryImpl itemRepository = new ItemRepositoryImpl();
        List<ItemDTO> allItems = itemRepository.getAllItems();

        int warehouse_quantity = 0;
        int store_quantity = 0;

        for (ItemDTO item : allItems) {
            if (item.getCatalogNumber() == catalog_number &&
                    item.getBranchId() == branch_id &&
                    !item.IsDefective()) {

                String location = item.getStorageLocation();
                if ("Warehouse".equalsIgnoreCase(location)) {
                    warehouse_quantity++;
                } else if ("InteriorStore".equalsIgnoreCase(location)) {
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

    public String showProductPurchasesPrices(int catalog_number, int currentBranchId) {
        DecimalFormat df = new DecimalFormat("#.00");
        StringBuilder result = new StringBuilder();
        boolean found = false;
        int count = 1;

        for (ItemDTO item : purchased_items.values()) {
            if (item.getCatalogNumber() == catalog_number && item.getBranchId() == currentBranchId) {
                Product product = products.get(catalog_number);
                if (product != null) {
                    found = true;
                    result.append(count++).append(". ")
                            .append(df.format(product.getSalePriceAfterStoreDiscount()))
                            .append(" ₪ (Sale Date: ");
                    if (item.getSale_date() != null) {
                        result.append(item.getSale_date().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
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
     * Adds a new product to the system's in-memory product collection.
     *
     * @param product the Product object to add, identified by its catalog number
     */
    public void addProduct(Product product) {
        products.put(product.getCatalogNumber(), product);
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }

    public boolean productExists(int catalogNumber) {
        return products.containsKey(catalogNumber);
    }

}
