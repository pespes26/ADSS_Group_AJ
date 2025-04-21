package Service;

import Domain.Item;
import Domain.Product;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class ReportController {
    private final HashMap<Integer, Item> items;
    private final HashMap<Integer, Product> products;

    public ReportController(HashMap<Integer, Item> items, HashMap<Integer, Product> products) {
        this.items = items;
        this.products = products;
    }

    public String defectAndExpiredReport() {
        StringBuilder report = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate today = LocalDate.now();

        report.append("Defective Items:\n");
        int counter = 1;
        boolean hasDefects = false;

        for (Item item : items.values()) {
            if (item.isDefect()) {
                Product product = products.get(item.getCatalogNumber());
                if (product != null) {
                    hasDefects = true;
                    report.append(counter++).append(". Item ID: ").append(item.getItemId())
                            .append(", Name: ").append(product.getProductName())
                            .append(", Category: ").append(product.getCategory())
                            .append(", Sub-Category: ").append(product.getSubCategory())
                            .append(", Size: ").append(item.getItemSize())
                            .append(", Location: ").append(item.getStorageLocation())
                            .append(", Section: ").append(item.getSectionInStore())
                            .append("\n");
                }
            }
        }

        if (!hasDefects) {
            report.append("No items marked as defective.\n");
        }

        report.append("\nExpired Items:\n");
        List<Item> expiredItems = new ArrayList<>();

        for (Item item : items.values()) {
            try {
                LocalDate expDate = LocalDate.parse(item.getItemExpiringDate(), formatter);
                if (expDate.isBefore(today)) {
                    expiredItems.add(item);
                }
            } catch (DateTimeParseException ignored) {}
        }

        expiredItems.sort(Comparator.comparing(item -> {
            try {
                return LocalDate.parse(item.getItemExpiringDate(), formatter);
            } catch (DateTimeParseException e) {
                return LocalDate.MAX;
            }
        }));

        if (expiredItems.isEmpty()) {
            report.append("No items have expired.\n");
        } else {
            counter = 1;
            for (Item item : expiredItems) {
                Product product = products.get(item.getCatalogNumber());
                if (product != null) {
                    report.append(counter++).append(". Item ID: ").append(item.getItemId())
                            .append(", Name: ").append(product.getProductName())
                            .append(", Expired on: ").append(item.getItemExpiringDate())
                            .append(", Category: ").append(product.getCategory())
                            .append(", Sub-Category: ").append(product.getSubCategory())
                            .append(", Size: ").append(item.getItemSize())
                            .append(", Location: ").append(item.getStorageLocation())
                            .append("\n");
                }
            }
        }

        return report.toString();
    }

    public String inventoryReportByCategories(String[] categories) {
        StringBuilder report = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        boolean anyCategoryFound = false;

        for (String categoryName : categories) {
            report.append("Category: ").append(categoryName).append("\n");

            Map<String, Map<Integer, List<Item>>> subcategoryMap = new TreeMap<>();

            for (Item item : items.values()) {
                Product product = products.get(item.getCatalogNumber());
                if (product != null && product.getCategory().equalsIgnoreCase(categoryName)) {
                    String subCategory = product.getSubCategory();
                    int size = item.getItemSize();
                    subcategoryMap
                            .computeIfAbsent(subCategory, k -> new TreeMap<>())
                            .computeIfAbsent(size, k -> new ArrayList<>())
                            .add(item);
                }
            }

            if (subcategoryMap.isEmpty()) {
                report.append("  Category does not exist.\n");
            } else {
                anyCategoryFound = true;
                for (Map.Entry<String, Map<Integer, List<Item>>> subEntry : subcategoryMap.entrySet()) {
                    report.append("  Sub-Category: ").append(subEntry.getKey()).append("\n");
                    for (Map.Entry<Integer, List<Item>> sizeEntry : subEntry.getValue().entrySet()) {
                        int size = sizeEntry.getKey();
                        String sizeLabel = switch (size) {
                            case 1 -> "Small";
                            case 2 -> "Medium";
                            case 3 -> "Big";
                            default -> "Unknown Size";
                        };
                        report.append("    Size: ").append(sizeLabel).append("\n");

                        List<Item> sortedItems = sizeEntry.getValue();
                        sortedItems.sort(Comparator.comparing(item -> {
                            try {
                                return LocalDate.parse(item.getItemExpiringDate(), formatter);
                            } catch (Exception e) {
                                return LocalDate.MAX;
                            }
                        }));

                        int count = 1;
                        for (Item item : sortedItems) {
                            Product product = products.get(item.getCatalogNumber());
                            if (product != null) {
                                report.append("      ").append(count++).append(". ")
                                        .append("Item ID: ").append(item.getItemId())
                                        .append(", Name: ").append(product.getProductName())
                                        .append(", Location: ").append(item.getStorageLocation())
                                        .append(", Expiring: ").append(item.getItemExpiringDate())
                                        .append("\n");
                            }
                        }
                    }
                }
            }

            report.append("------------------------------------------------------------\n");
        }

        if (!anyCategoryFound) {
            return "No valid categories found. All entered categories do not exist.";
        }

        return report.toString();
    }

    public String generateReorderAlertReport() {
        StringBuilder report = new StringBuilder();
        boolean found = false;

        Map<Integer, Integer> stockCountMap = new HashMap<>();
        for (Item item : items.values()) {
            int catalogNumber = item.getCatalogNumber();
            stockCountMap.put(catalogNumber, stockCountMap.getOrDefault(catalogNumber, 0) + 1);
        }

        for (Map.Entry<Integer, Product> entry : products.entrySet()) {
            int catalogNumber = entry.getKey();
            Product product = entry.getValue();

            int minRequired = (int) (0.5 * product.getProductDemandLevel() + 0.5 * product.getSupplyTime());
            int inStock = stockCountMap.getOrDefault(catalogNumber, 0);

            if (inStock < minRequired) {
                found = true;
                int missing = minRequired - inStock;
                report.append("Product Catalog Number: ").append(catalogNumber)
                        .append(", Name: ").append(product.getProductName())
                        .append(", Total in stock: ").append(inStock)
                        .append(", Minimum required: ").append(minRequired)
                        .append(", Missing: ").append(missing).append("\n");
            }
        }

        if (!found) {
            return "All the products are above their minimum required amount.";
        }

        return report.toString();
    }
}
