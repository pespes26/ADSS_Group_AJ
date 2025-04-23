package Domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Controller responsible for generating reports about items in the inventory,
 * specifically items that are either defective or expired.
 */
public class ReportController {
    private final HashMap<Integer, Item> items;     // All items in the inventory, keyed by item ID
    private final HashMap<Integer, Product> products; // All products in the system, keyed by catalog number

    /**
     * Constructs a ReportController with access to the items and products in the system.
     *
     * @param items    A map of all items in the inventory, keyed by item ID.
     * @param products A map of all products in the system, keyed by catalog number.
     */
    public ReportController(HashMap<Integer, Item> items, HashMap<Integer, Product> products) {
        this.items = items;
        this.products = products;
    }

    /**
     * Generates a detailed textual report of:
     * 1. Items marked as defective.
     * 2. Items that are expired based on the current system date.
     * The report includes for each item:
     * - Item ID
     * - Product name
     * - Category and sub-category
     * - Size
     * - Storage location and section
     * - Expiry date (only for expired items)
     * Items with invalid or unparsable expiry dates are silently skipped in the expired section.
     *
     * @return A formatted string containing the defect and expired item report.
     */
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
        List<Item> expired_items = new ArrayList<>();

        for (Item item : items.values()) {
            try {
                LocalDate expiring_date = LocalDate.parse(item.getItemExpiringDate(), formatter);
                if (expiring_date.isBefore(today)) {
                    expired_items.add(item);
                }
            } catch (DateTimeParseException ignored) {}
        }

        expired_items.sort(Comparator.comparing(item -> {
            try {
                return LocalDate.parse(item.getItemExpiringDate(), formatter);
            } catch (DateTimeParseException e) {
                return LocalDate.MAX;
            }
        }));

        if (expired_items.isEmpty()) {
            report.append("No items have expired.\n");
        } else {
            counter = 1;
            for (Item item : expired_items) {
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

    /**
     * Generates a structured inventory report based on the given list of category names.
     * The report is grouped hierarchically by:
     * 1. Category
     * 2. Sub-category
     * 3. Item size (Small / Medium / Big)
     * Within each size group, items are sorted by expiration date (earliest first).
     * For each item, the report includes:
     * - Item ID
     * - Product name
     * - Storage location
     * - Expiry date
     * Categories that do not exist in the inventory are noted explicitly.
     * If none of the categories exist, a global message is returned.
     *
     * @param categories An array of category names to include in the report.
     * @return A formatted string representing the inventory report by categories.
     */
    public String inventoryReportByCategories(String[] categories) {
        StringBuilder report = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        boolean any_category_found = false;

        for (String category_name : categories) {
            report.append("Category: ").append(category_name).append("\n");

            Map<String, Map<Integer, List<Item>>> sub_category_map = new TreeMap<>();

            for (Item item : items.values()) {
                Product product = products.get(item.getCatalogNumber());
                if (product != null && product.getCategory().equalsIgnoreCase(category_name)) {
                    String sub_category = product.getSubCategory();
                    int size = item.getItemSize();
                    sub_category_map
                            .computeIfAbsent(sub_category, k -> new TreeMap<>())
                            .computeIfAbsent(size, k -> new ArrayList<>())
                            .add(item);
                }
            }

            if (sub_category_map.isEmpty()) {
                report.append("  Category does not exist.\n");
            } else {
                any_category_found = true;
                for (Map.Entry<String, Map<Integer, List<Item>>> subEntry : sub_category_map.entrySet()) {
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

                        List<Item> sorted_items = sizeEntry.getValue();
                        sorted_items.sort(Comparator.comparing(item -> {
                            try {
                                return LocalDate.parse(item.getItemExpiringDate(), formatter);
                            } catch (Exception e) {
                                return LocalDate.MAX;
                            }
                        }));

                        int count = 1;
                        for (Item item : sorted_items) {
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

        if (!any_category_found) {
            return "No valid categories found. All entered categories do not exist.";
        }

        return report.toString();
    }

    /**
     * Generates a reorder alert report for all products whose current total stock
     * is below the minimum required threshold.
     * The minimum required quantity is calculated as:
     * 0.5 × product demand level + 0.5 × supply time
     * For each product, the total stock is calculated from existing items and
     * stored in the product using {@code setTotalQuantity}.
     * The report includes, for each product that falls below the threshold:
     * - Catalog number
     * - Product name
     * - Total quantity in stock
     * - Minimum required quantity
     * - Quantity missing
     * - Active discount details (if present and currently valid)
     *
     * @return A formatted string report listing all products that need restocking,
     *         or a message indicating all products meet minimum requirements.
     */
    public String generateReorderAlertReport() {
        StringBuilder report = new StringBuilder();
        boolean found = false;

        Map<Integer, Integer> stockCountMap = new HashMap<>();
        for (Item item : items.values()) {
            int catalog_number = item.getCatalogNumber();
            stockCountMap.put(catalog_number, stockCountMap.getOrDefault(catalog_number, 0) + 1);
        }

        for (Map.Entry<Integer, Integer> entry : stockCountMap.entrySet()) {
            int catalog_number = entry.getKey();
            int count = entry.getValue();
            if (products.containsKey(catalog_number)) {
                products.get(catalog_number).setTotalQuantity(count);
            }
        }

        for (Map.Entry<Integer, Product> entry : products.entrySet()) {
            int catalog_number = entry.getKey();
            Product product = entry.getValue();

            int min_required = (int) (0.5 * product.getProductDemandLevel() + 0.5 * product.getSupplyTime());
            int inStock = product.getTotalQuantity();

            if (inStock < min_required) {
                found = true;
                int missing = min_required - inStock;

                report.append("Product Catalog Number: ").append(catalog_number)
                        .append(", Name: ").append(product.getProductName())
                        .append(", Total in stock: ").append(inStock)
                        .append(", Minimum required: ").append(min_required)
                        .append(", Missing: ").append(missing);

                Discount discount = product.getDiscount();
                if (discount != null && discount.isActive()) {
                    report.append(", Active Discount: ").append(discount.getDiscountRate()).append("%")
                            .append(" (").append(discount.getStartDate()).append(" to ").append(discount.getEndDate()).append(")");
                }

                report.append("\n");
            }
        }

        if (!found) {
            return "All the products are above their minimum required amount.";
        }

        return report.toString();
    }
}
