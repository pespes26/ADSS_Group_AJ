package Service;

import Domain.Item;
import Domain.Product;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class ProductController {
    private final HashMap<Integer, Product> products;
    private final HashMap<Integer, Item> purchasedItems;

    public ProductController(HashMap<Integer, Product> products, HashMap<Integer, Item> purchasedItems) {
        this.products = products;
        this.purchasedItems = purchasedItems;
    }

    public boolean updateCostPriceByCatalogNumber(int catalogNumber, double newPrice) {
        boolean found = false;

        for (Product p : products.values()) {
            if (p.getCatalogNumber() == catalogNumber) {
                p.setCostPriceBeforeSupplierDiscount(newPrice);
                double costAfter = newPrice * (1 - p.getSupplierDiscount() / 100.0);
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

    public boolean updateProductSupplyDetails(int catalogNumber, Integer supplyTime, Integer demand) {
        Product product = products.get(catalogNumber);
        if (product == null) {
            return false;
        }

        if (supplyTime != null) {
            product.setSupplyTime(supplyTime);
        }

        if (demand != null) {
            product.setProductDemandLevel(demand);
        }

        return true;
    }

    public Product getProduct(int catalogNumber) {
        return products.get(catalogNumber);
    }

    public boolean hasCategory(String category) {
        for (Product product : products.values()) {
            if (product.getCategory().equalsIgnoreCase(category)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasSubCategory(String subCategory) {
        for (Product product : products.values()) {
            if (product.getSubCategory().equalsIgnoreCase(subCategory)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasCatalogNumber(int catalogNumber) {
        return products.containsKey(catalogNumber);
    }

    public String showProductQuantities(int catalogNumber) {
        Product product = products.get(catalogNumber);
        if (product == null) {
            return "Invalid Product Catalog Number: " + catalogNumber + ". This Product Catalog Number does not exist in the inventory.";
        }

        int warehouseQuantity = product.getQuantityInWarehouse();
        int storeQuantity = product.getQuantityInStore();

        if (warehouseQuantity == 0 && storeQuantity == 0) {
            return "No items found for Product Catalog Number: " + catalogNumber;
        }

        return "Product Catalog Number: " + catalogNumber + "\n"
                + "Warehouse quantity: " + warehouseQuantity + "\n"
                + "Store quantity: " + storeQuantity;
    }

    public String showProductPurchasesPrices(int catalogNumber) {
        DecimalFormat df = new DecimalFormat("#.00");
        StringBuilder result = new StringBuilder();
        boolean found = false;
        int count = 1;

        for (Item item : purchasedItems.values()) {
            if (item.getCatalogNumber() == catalogNumber) {
                Product product = products.get(catalogNumber);
                if (product != null) {
                    found = true;
                    result.append(count++).append(". ")
                            .append(df.format(product.getSalePriceAfterStoreDiscount())).append("\n");
                }
            }
        }

        if (!found) {
            return "No purchased items found with Product Catalog Number: " + catalogNumber;
        }

        return "Sale prices for Product Catalog Number " + catalogNumber + ":\n" + result;
    }


}
