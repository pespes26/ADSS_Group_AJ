package Inventory.Domain;



import Inventory.DAO.IItemsDAO;
import Inventory.DAO.IProductDAO;
import Inventory.DAO.JdbcItemDAO;
import Inventory.DAO.JdbcProductDAO;
import Inventory.DTO.ItemDTO;
import Inventory.DTO.ProductDTO;

import java.sql.SQLException;
import java.util.*;


/**
 * Manages inventory operations: items, products, discounts, branches, and reports.
 * Supports data import from CSV and updates product stock levels across branches.
 */

public class InventoryController {
    private final ItemController item_controller;
    private final ProductController product_controller;
    private final DiscountController discount_controller;
    private final ReportController report_controller;

    private final HashMap<Integer, Product> products;
    private final HashMap<Integer, Branch> branches;
    private final HashMap<String, HashMap<String, HashMap<String, HashMap<String, Integer>>>> products_amount_map_by_category;

    public InventoryController() {
        this.products = new HashMap<>();
        this.branches = new HashMap<>();
        this.products_amount_map_by_category = new HashMap<>();

        HashMap<Integer, Item> purchased_items = new HashMap<>();
        this.item_controller = new ItemController(branches, products, purchased_items);
        this.product_controller = new ProductController(products, purchased_items);
        this.discount_controller = new DiscountController(products);
        this.report_controller = new ReportController(branches, products);
    }





    /**
     * Returns the map of all existing branches in the system.
     * <p>
     * Each branch is identified by its unique branch ID.
     * Used to access branch-specific inventories and operations.
     *
     * @return a map of branch IDs to {@code Branch} objects.
     */
    public HashMap<Integer, Branch> getBranches() {
        return branches;
    }

    /**
     * Updates the stock count of a product in a specific branch and location.
     * <p>
     * Supports adding or removing items based on category, sub-category, size, and location.
     * Also updates product warehouse/store quantities accordingly.
     *
     * @param add true to add stock, false to remove stock
     * @param branchId the ID of the branch where the update applies
     * @param category the product's category
     * @param sub_category the product's sub-category
     * @param size the size of the product
     * @param location the location ("Warehouse" or "InteriorStore")
     */
    public void updateProductInventoryCount(boolean add, int branchId, String category, String sub_category, String size, String location) {
        products_amount_map_by_category.putIfAbsent(category, new HashMap<>());
        HashMap<String, HashMap<String, HashMap<String, Integer>>> subCategoryMap = products_amount_map_by_category.get(category);
        subCategoryMap.putIfAbsent(sub_category, new HashMap<>());
        HashMap<String, HashMap<String, Integer>> sizeMap = subCategoryMap.get(sub_category);
        sizeMap.putIfAbsent(size, new HashMap<>());
        HashMap<String, Integer> locationMap = sizeMap.get(size);

        if (add) {
            locationMap.put(location, locationMap.getOrDefault(location, 0) + 1);
            locationMap.putIfAbsent(location.equalsIgnoreCase("warehouse") ? "interiorStore" : "warehouse", 0);
        } else {
            locationMap.put(location, locationMap.getOrDefault(location, 0) - 1);
        }

        Branch branch = branches.get(branchId);
        if (branch != null) {
            for (Item item : branch.getItems().values()) {
                Product product = products.get(item.getCatalogNumber());
                if (product != null && product.getCategory().equalsIgnoreCase(category)
                        && product.getSubCategory().equalsIgnoreCase(sub_category)) {
                    if (location.equalsIgnoreCase("Warehouse")) {
                        product.setQuantityInWarehouse(add ? product.getQuantityInWarehouse() + 1 : product.getQuantityInWarehouse() - 1);
                    } else if (location.equalsIgnoreCase("interiorStore")) {
                        product.setQuantityInStore(add ? product.getQuantityInStore() + 1 : product.getQuantityInStore() - 1);
                    }
                }
            }
        }
    }


    public void loadFromDatabase() {
        System.out.println("Loading data from database using DAO and DTO...");

        try {
            // Load products from DB
            IProductDAO productDAO = new JdbcProductDAO();
            List<ProductDTO> productDTOs = productDAO.getAllProducts();

            for (ProductDTO dto : productDTOs) {
                Product product = new Product();

                product.setCatalogNumber(dto.getCatalogNumber());
                product.setProductName(dto.getProductName());
                product.setCategory(dto.getCategory());
                product.setSubCategory(dto.getSubCategory());
                product.setProductDemandLevel(dto.getProductDemandLevel());
                product.setSupplyTime(dto.getSupplyTime());
                product.setManufacturer(dto.getManufacturer());
                product.setSize(dto.getSize());
                product.setCostPriceBeforeSupplierDiscount(dto.getCostPriceBeforeSupplierDiscount());
                product.setSupplierDiscount(dto.getSupplierDiscount());
                product.setCostPriceAfterSupplierDiscount(dto.getCostPriceAfterSupplierDiscount());
                product.setStoreDiscount(dto.getStoreDiscount());
                product.setSalePriceBeforeStoreDiscount(dto.getSalePriceBeforeStoreDiscount());
                product.setSalePriceAfterStoreDiscount(dto.getSalePriceAfterStoreDiscount());
                product.setQuantityInWarehouse(dto.getQuantityInWarehouse());
                product.setQuantityInStore(dto.getQuantityInStore());
                product.setMinimumQuantityForAlert(dto.getMinimumQuantityForAlert());

                products.put(product.getCatalogNumber(), product);
            }

            // Load items from DB
            IItemsDAO itemDAO = new JdbcItemDAO();
            List<ItemDTO> itemDTOs = itemDAO.getAllItems();

            for (ItemDTO dto : itemDTOs) {
                Item item = new Item();

                item.setItemId(dto.getItemId());
                item.setBranchId(dto.getBranchId());
                item.setItemExpiringDate(dto.getItemExpiringDate());
                item.setStorageLocation(dto.getStorageLocation());
                item.setCatalog_number(dto.getCatalogNumber());
                item.setDefect(dto.IsDefective());

                int branchId = item.getBranchId();
                int catalogNumber = item.getCatalogNumber();

                branches.putIfAbsent(branchId, new Branch(branchId));
                branches.get(branchId).addItem(item);

                if (!products.containsKey(catalogNumber)) {
                    System.err.println("Warning: Item references missing Product (Catalog #" + catalogNumber + ")");
                    continue;
                }

                Product product = products.get(catalogNumber);
                updateProductInventoryCount(
                        true,
                        branchId,
                        product.getCategory(),
                        product.getSubCategory(),
                        String.valueOf(product.getSize()),
                        item.getStorageLocation()
                );
            }

            System.out.println("Data loaded successfully from SQLite via DAOs and DTOs.");

        } catch (SQLException e) {
            System.err.println("❌ Failed to load data from database: " + e.getMessage());
            e.printStackTrace();
        }
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

    /**
     * Handles the full process of adding a new product and its associated items to the inventory.
     *
     * @param catalogNumber the new product's catalog number
     * @param branchId the branch ID where the items will be stored
     * @param scan the Scanner object for reading user input
     */
    public void addNewProductAndItems(int catalogNumber, int branchId, Scanner scan) {
        System.out.println("Product with Catalog Number " + catalogNumber + " does not exist.");
        System.out.println("Please enter full product details.");

        // Collect new product details from the user
        System.out.print("Enter Product Name: ");
        String productName = scan.nextLine().trim();

        System.out.print("Enter Category: ");
        String category = scan.nextLine().trim();

        System.out.print("Enter Sub-Category: ");
        String subCategory = scan.nextLine().trim();

        System.out.print("Enter Product Size (numeric value between 1-3): ");
        int size = Integer.parseInt(scan.nextLine().trim());

        System.out.print("Enter Cost Price Before Supplier Discount: ");
        double costPriceBefore = Double.parseDouble(scan.nextLine().trim());

        System.out.print("Enter Product Demand Level (1–5): ");
        int demandLevel = Integer.parseInt(scan.nextLine().trim());

        System.out.print("Enter Supply Time (days): ");
        int supplyTime = Integer.parseInt(scan.nextLine().trim());

        System.out.print("Enter Manufacturer: ");
        String manufacturer = scan.nextLine().trim();

        System.out.print("Enter Supplier Discount (%): ");
        int supplierDiscount = Integer.parseInt(scan.nextLine().trim());

        System.out.print("Enter Store Discount (%): ");
        int storeDiscount = Integer.parseInt(scan.nextLine().trim());

        // Calculate final prices
        double costAfterSupplierDiscount = costPriceBefore * (1 - supplierDiscount / 100.0);
        double salePriceBeforeStoreDiscount = costAfterSupplierDiscount * 2;
        double salePriceAfterStoreDiscount = salePriceBeforeStoreDiscount * (1 - storeDiscount / 100.0);

        Product newProduct = new Product();
        populateProductData(newProduct, catalogNumber, productName, category, subCategory,
                demandLevel, supplyTime, manufacturer,
                costPriceBefore, supplierDiscount, storeDiscount, size);

        // Add the new product to the system
        product_controller.addProduct(newProduct);

        System.out.println("\nProduct added successfully!");

        // Collect item details
        System.out.print("How many items would you like to add for this product? ");
        int quantityToAdd = Integer.parseInt(scan.nextLine().trim());

        System.out.print("Enter storage location for all items (Warehouse or InteriorStore): ");
        String storageLocation = scan.nextLine().trim();

        System.out.print("Enter expiry date for all items (format: dd/MM/yyyy): ");
        String expiryDate = scan.nextLine().trim();

        int nextItemId = item_controller.getNextAvailableItemId();

        for (int i = 0; i < quantityToAdd; i++) {
            int currentItemId = nextItemId + i;
            item_controller.addItem(
                    currentItemId,
                    branchId,
                    catalogNumber,
                    storageLocation,
                    expiryDate
            );
        }

        System.out.println("\n-----------------------------------------");
        System.out.println(quantityToAdd + " items successfully added for Product Catalog Number " + catalogNumber + ".");
        System.out.println("-----------------------------------------\n");
    }


    /**
     * Populates a Product object with all its fields based on the provided data.
     *
     * @param product The product to populate.
     * @param catalogNumber Catalog number.
     * @param productName Product name.
     * @param category Category.
     * @param subCategory Sub-category.
     * @param demandLevel Demand level (1–5).
     * @param supplyTime Supply time (days).
     * @param manufacturer Manufacturer name.
     * @param costPriceBefore Cost price before supplier discount.
     * @param supplierDiscount Supplier discount (%).
     * @param storeDiscount Store discount (%).
     * @param size Product size.
     */
    private void populateProductData(Product product, int catalogNumber, String productName, String category, String subCategory,
                                     int demandLevel, int supplyTime, String manufacturer,
                                     double costPriceBefore, int supplierDiscount, int storeDiscount, int size) {

        product.setCatalogNumber(catalogNumber);
        product.setProductName(productName);
        product.setCategory(category);
        product.setSubCategory(subCategory);
        product.setProductDemandLevel(demandLevel);
        product.setSupplyTime(supplyTime);
        product.setManufacturer(manufacturer);
        product.setSize(size);

        double costAfterSupplierDiscount = costPriceBefore * (1 - supplierDiscount / 100.0);
        double salePriceBeforeStoreDiscount = costAfterSupplierDiscount * 2;
        double salePriceAfterStoreDiscount = salePriceBeforeStoreDiscount * (1 - storeDiscount / 100.0);

        product.setSupplierDiscount(supplierDiscount);
        product.setCostPriceBeforeSupplierDiscount(costPriceBefore);
        product.setCostPriceAfterSupplierDiscount(costAfterSupplierDiscount);
        product.setStoreDiscount(storeDiscount);
        product.setSalePriceBeforeStoreDiscount(salePriceBeforeStoreDiscount);
        product.setSalePriceAfterStoreDiscount(salePriceAfterStoreDiscount);
        product.setQuantityInWarehouse(0);
        product.setQuantityInStore(0);

        int minRequired = (int) (0.5 * demandLevel + 0.5 * supplyTime);
        product.setMinimumQuantityForAlert(minRequired);
    }



}