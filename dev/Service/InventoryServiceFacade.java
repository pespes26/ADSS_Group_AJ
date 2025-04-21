package Service;

import Domain.*;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class InventoryServiceFacade {
    private final ItemController itemController;
    private final ProductController productController;
    private final DiscountController discountController;
    private final ReportController reportController;

    private final HashMap<Integer, Item> items;
    private final HashMap<Integer, Product> products;
    private final HashMap<Integer, Item> purchased_items;
    private final HashMap<String, HashMap<String, HashMap<String, HashMap<String, Integer>>>> products_amount_map_by_category;

    public InventoryServiceFacade() {
        this.items = new HashMap<>();
        this.products = new HashMap<>();
        this.purchased_items = new HashMap<>();
        this.products_amount_map_by_category = new HashMap<>();

        this.itemController = new ItemController(items, products, purchased_items);
        this.productController = new ProductController(products, purchased_items);
        this.discountController = new DiscountController(products);
        this.reportController = new ReportController(items, products);
    }

    public void importData(String path) {
        try (CSVReader reader = new CSVReader(new FileReader(path))) {
            String[] productFieldsFromCSV;
            while ((productFieldsFromCSV = reader.readNext()) != null) {
                String input_id = productFieldsFromCSV[0];
                if (input_id.startsWith("\uFEFF")) {
                    input_id = input_id.substring(1);
                }
                input_id = input_id.trim();
                productFieldsFromCSV[0] = input_id;

                Item item = new Item();
                item.setItemId(Integer.parseInt(productFieldsFromCSV[0]));
                item.setItemExpiringDate(productFieldsFromCSV[2]);
                item.setStorageLocation(productFieldsFromCSV[3]);
                item.setSectionInStore(productFieldsFromCSV[4]);
                item.setItemSize(Integer.parseInt(productFieldsFromCSV[8]));
                item.setCatalogNumber(Integer.parseInt(productFieldsFromCSV[5]));
                item.setDefect(false);

                items.put(item.getItemId(), item);

                int catalogNumber = Integer.parseInt(productFieldsFromCSV[5]);
                if (!products.containsKey(catalogNumber)) {
                    Product product = buildProductFromCSV(productFieldsFromCSV);
                    products.put(catalogNumber, product);
                }

                String category = productFieldsFromCSV[6];
                String subCategory = productFieldsFromCSV[7];
                String size = productFieldsFromCSV[8];
                String location = productFieldsFromCSV[3];

                updateProductInventoryCount(true, category, subCategory, size, location);
            }
        } catch (IOException | CsvValidationException e) {
            System.err.println("Failed to import data: " + e.getMessage());
        }
    }

    public void updateProductInventoryCount(boolean add, String category, String subCategory, String size, String location) {
        products_amount_map_by_category.putIfAbsent(category, new HashMap<>());
        HashMap<String, HashMap<String, HashMap<String, Integer>>> subCategoryMap = products_amount_map_by_category.get(category);
        subCategoryMap.putIfAbsent(subCategory, new HashMap<>());
        HashMap<String, HashMap<String, Integer>> sizeMap = subCategoryMap.get(subCategory);
        sizeMap.putIfAbsent(size, new HashMap<>());
        HashMap<String, Integer> locationMap = sizeMap.get(size);

        if (add) {
            locationMap.put(location, locationMap.getOrDefault(location, 0) + 1);
            locationMap.putIfAbsent(location.equalsIgnoreCase("warehouse") ? "interiorStore" : "warehouse", 0);
        } else {
            locationMap.put(location, locationMap.getOrDefault(location, 0) - 1);
        }

        for (Product product : products.values()) {
            if (product.getCategory().equalsIgnoreCase(category) && product.getSubCategory().equalsIgnoreCase(subCategory)) {
                if (location.equalsIgnoreCase("warehouse")) {
                    product.setQuantityInWarehouse(add ? product.getQuantityInWarehouse() + 1 : product.getQuantityInWarehouse() - 1);
                } else if (location.equalsIgnoreCase("interiorStore")) {
                    product.setQuantityInStore(add ? product.getQuantityInStore() + 1 : product.getQuantityInStore() - 1);
                }
            }
        }
    }

    private Product buildProductFromCSV(String[] fields) {
        int catalogNumber = Integer.parseInt(fields[5]);
        Product product = new Product();
        product.setCatalogNumber(catalogNumber);
        product.setProductName(fields[1]);
        product.setCategory(fields[6]);
        product.setSubCategory(fields[7]);
        product.setProductDemandLevel(Integer.parseInt(fields[10]));
        product.setSupplyTime(Integer.parseInt(fields[11]));
        product.setManufacturer(fields[12]);

        double costBefore = Double.parseDouble(fields[9]);
        int supplierDiscount = Integer.parseInt(fields[13]);
        int storeDiscount = Integer.parseInt(fields[14]);

        double costAfter = costBefore * (1 - supplierDiscount / 100.0);
        double saleBefore = costAfter * 2;
        double saleAfter = saleBefore * (1 - storeDiscount / 100.0);

        product.setSupplierDiscount(supplierDiscount);
        product.setCostPriceBeforeSupplierDiscount(costBefore);
        product.setCostPriceAfterSupplierDiscount(costAfter);
        product.setStoreDiscount(storeDiscount);
        product.setSalePriceBeforeStoreDiscount(saleBefore);
        product.setSalePriceAfterStoreDiscount(saleAfter);
        product.setQuantityInWarehouse(0);
        product.setQuantityInStore(0);

        return product;
    }

    public ItemController getItemController() {
        return itemController;
    }

    public ProductController getProductController() {
        return productController;
    }

    public DiscountController getDiscountController() {
        return discountController;
    }

    public ReportController getReportController() {
        return reportController;
    }
}
