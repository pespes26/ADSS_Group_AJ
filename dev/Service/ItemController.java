package Service;

import Domain.Item;
import Domain.Product;

import java.text.DecimalFormat;
import java.util.*;

public class ItemController {
    private final HashMap<Integer, Item> items;
    private final HashMap<Integer, Product> products;
    private final HashMap<Integer, Item> purchasedItems;

    public ItemController(HashMap<Integer, Item> items, HashMap<Integer, Product> products, HashMap<Integer, Item> purchasedItems) {
        this.items = items;
        this.products = products;
        this.purchasedItems = purchasedItems;
    }

    public boolean addItem(String csvInput) {
        try {
            String[] fields = csvInput.split(",");
            int itemId = Integer.parseInt(fields[0]);
            String productName = fields[1];
            String expiringDate = fields[2];
            String location = fields[3];
            String section = fields[4];
            int catalogNumber = Integer.parseInt(fields[5]);
            String category = fields[6];
            String subCategory = fields[7];
            int size = Integer.parseInt(fields[8]);
            double costPriceBefore = Double.parseDouble(fields[9]);
            int demand = Integer.parseInt(fields[10]);
            int supplyTime = Integer.parseInt(fields[11]);
            String manufacturer = fields[12];
            int supplierDiscount = Integer.parseInt(fields[13]);
            int storeDiscount = Integer.parseInt(fields[14]);

            Item item = new Item();
            item.setItemId(itemId);
            item.setItemExpiringDate(expiringDate);
            item.setStorageLocation(location);
            item.setSectionInStore(section);
            item.setItemSize(size);
            item.setCatalogNumber(catalogNumber);
            item.setDefect(false);
            items.put(itemId, item);

            if (!products.containsKey(catalogNumber)) {
                Product product = new Product();
                product.setCatalogNumber(catalogNumber);
                product.setProductName(productName);
                product.setCategory(category);
                product.setSubCategory(subCategory);
                product.setProductDemandLevel(demand);
                product.setSupplyTime(supplyTime);
                product.setManufacturer(manufacturer);
                product.setSupplierDiscount(supplierDiscount);
                product.setCostPriceBeforeSupplierDiscount(costPriceBefore);

                double costAfter = costPriceBefore * (1 - supplierDiscount / 100.0);
                double saleBefore = costAfter * 2;
                double saleAfter = saleBefore * (1 - storeDiscount / 100.0);

                product.setCostPriceAfterSupplierDiscount(costAfter);
                product.setStoreDiscount(storeDiscount);
                product.setSalePriceBeforeStoreDiscount(saleBefore);
                product.setSalePriceAfterStoreDiscount(saleAfter);

                product.setQuantityInWarehouse(0);
                product.setQuantityInStore(0);

                products.put(catalogNumber, product);
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean itemExists(int itemId) {
        return items.containsKey(itemId);
    }

    public void removeItemByPurchase(int itemId) {
        Item item = items.remove(itemId);
        if (item != null) {
            purchasedItems.put(itemId, item);
        }
    }

    public double getSalePriceAfterDiscount(int itemId) {
        Item item = items.get(itemId);
        if (item == null) return 0.0;

        Product product = products.get(item.getCatalogNumber());
        if (product == null) return 0.0;

        return product.getSalePriceAfterStoreDiscount();
    }

    public void removeItemByDefect(int itemId) {
        items.remove(itemId);
    }

    public boolean markItemAsDefective(int itemId) {
        Item item = items.get(itemId);
        if (item == null) return false;

        item.setDefect(true);
        return true;
    }


    public boolean checkReorderAlert(int itemId) {
        Item item = items.get(itemId);
        if (item == null) return false;

        int catalogNumber = item.getCatalogNumber();
        Product product = products.get(catalogNumber);
        if (product == null) return false;

        int minRequired = (int) (0.5 * product.getProductDemandLevel() + 0.5 * product.getSupplyTime());

        long count = items.values().stream()
                .filter(i -> i.getCatalogNumber() == catalogNumber && !i.isDefect())
                .count();

        return count < minRequired;
    }

    public String getItemName(int itemId) {
        Item item = items.get(itemId);
        if (item == null) return "";
        Product product = products.get(item.getCatalogNumber());
        return product != null ? product.getProductName() : "";
    }

    public boolean updateItemLocation(int itemId, String loc, String section) {
        Item item = items.get(itemId);
        if (item != null) {
            if (loc != null) item.setStorageLocation(loc);
            if (section != null) item.setSectionInStore(section);
            return true;
        }
        return false;
    }


    public String showCurrentAmountPerLocationByCatalogNumber(int catalogNumber) {
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

    public String getPurchasePricesByCatalogNumber(int catalogNumber) {
        List<Double> prices = new ArrayList<>();
        for (Item item : purchasedItems.values()) {
            if (item.getCatalogNumber() == catalogNumber) {
                Product product = products.get(catalogNumber);
                if (product != null) {
                    double price = product.getSalePriceAfterStoreDiscount();
                    if (price >= 0) {
                        prices.add(price);
                    }
                }
            }
        }

        if (prices.isEmpty()) {
            return "No purchased items found with Product Catalog Number: " + catalogNumber;
        }

        StringBuilder result = new StringBuilder("Sale prices for Product Catalog Number " + catalogNumber + ":\n");
        for (int i = 0; i < prices.size(); i++) {
            result.append((i + 1)).append(". ").append(prices.get(i)).append("\n");
        }

        return result.toString();
    }

    public String showItemDetails(int itemId) {
        Item item = items.get(itemId);
        if (item == null) {
            return "Item with ID " + itemId + " not found in stock.";
        }

        Product product = products.get(item.getCatalogNumber());
        if (product == null) {
            return "Product with Product Catalog Number " + item.getCatalogNumber() + " not found.";
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


}
