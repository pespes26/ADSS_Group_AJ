package Domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Controller class for generating inventory-related reports.
 * Manages reports for defective, expired, and low-stock items per branch.
 */
public class ReportController {
    private final HashMap<Integer, Branch> branches;
    private final HashMap<Integer, Product> products;

    /**
     * Constructs a new ReportController.
     *
     * @param branches Map of branch IDs to Branch objects.
     * @param products Map of product catalog numbers to Product objects.
     */
    public ReportController(HashMap<Integer, Branch> branches, HashMap<Integer, Product> products) {
        this.branches = branches;
        this.products = products;
    }


    /**
     * Generates a report of defective and expired items in a specific branch.
     * Lists defective items first, followed by expired items sorted by expiration date.
     * Handles parsing and ignores invalid expiration dates.
     *
     * @param current_branch_id the branch ID to generate the report for
     * @return a formatted string containing defective and expired items, or messages if none exist
     */
    public String defectAndExpiredReport(int current_branch_id) {
        StringBuilder report = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate today = LocalDate.now();

        report.append("Defective Items in Branch ").append(current_branch_id).append(":\n");
        int counter = 1;
        boolean has_defects = false;

        for (Branch branch : branches.values()) {
            if (branch.getBranchId() != current_branch_id) continue;

            for (Item item : branch.getItems().values()) {
                if (item.isDefect()) {
                    Product product = products.get(item.getCatalogNumber());
                    if (product != null) {
                        has_defects = true;
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
        }

        if (!has_defects) {
            report.append("No defective items found in Branch ").append(current_branch_id).append(".\n");
        }

        report.append("\nExpired Items in Branch ").append(current_branch_id).append(":\n");
        List<Item> expired_items = new ArrayList<>();

        for (Branch branch : branches.values()) {
            if (branch.getBranchId() != current_branch_id) continue;

            for (Item item : branch.getItems().values()) {
                try {
                    LocalDate expiring_date = LocalDate.parse(item.getItemExpiringDate(), formatter);
                    if (expiring_date.isBefore(today)) {
                        expired_items.add(item);
                    }
                } catch (DateTimeParseException ignored) {}
            }
        }

        expired_items.sort(Comparator.comparing(item -> {
            try {
                return LocalDate.parse(item.getItemExpiringDate(), formatter);
            } catch (DateTimeParseException e) {
                return LocalDate.MAX;
            }
        }));

        if (expired_items.isEmpty()) {
            report.append("No expired items found in Branch ").append(current_branch_id).append(".\n");
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
     * Generates a detailed inventory report for specified categories in a given branch.
     * Lists items grouped by sub-category and size, sorted by expiration date.
     * If a category does not exist, a message is added for it.
     * Returns an overall report or an error if no categories are found.
     *
     * @param categories an array of category names to report on
     * @param branch_id the branch ID to generate the report for
     * @return a formatted string report by categories, or an error message
     */
    public String inventoryReportByCategories(String[] categories, int branch_id) {
        StringBuilder report = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        boolean any_category_found = false;

        Branch selected_branch = branches.get(branch_id);
        if (selected_branch == null) {
            return "Branch with ID " + branch_id + " does not exist.";
        }

        for (String category_name : categories) {
            report.append("Category: ").append(category_name).append("\n");

            Map<String, Map<Integer, List<Item>>> sub_category_map = new TreeMap<>();

            for (Item item : selected_branch.getItems().values()) {
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
                for (Map.Entry<String, Map<Integer, List<Item>>> sub_entry : sub_category_map.entrySet()) {
                    report.append("  Sub-Category: ").append(sub_entry.getKey()).append("\n");
                    for (Map.Entry<Integer, List<Item>> size_entry : sub_entry.getValue().entrySet()) {
                        int size = size_entry.getKey();
                        String size_label = switch (size) {
                            case 1 -> "Small";
                            case 2 -> "Medium";
                            case 3 -> "Big";
                            default -> "Unknown Size";
                        };
                        report.append("    Size: ").append(size_label).append("\n");

                        List<Item> sorted_items = size_entry.getValue();
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
     * Generates a reorder alert report for products in a specific branch.
     * Lists products whose current stock is below their minimum required quantity.
     * Calculates minimum requirement based on product demand and supply time.
     * Includes active discount details if available.
     *
     * @param branch_id the branch ID to generate the reorder report for
     * @return a formatted report string, or a message if no products require reordering
     */
    public String generateReorderAlertReport(int branch_id) {
        StringBuilder report = new StringBuilder();
        boolean found = false;

        Branch branch = branches.get(branch_id);
        if (branch == null) {
            return "Branch " + branch_id + " not found.";
        }

        Map<Integer, Integer> stockCountMap = new HashMap<>();
        for (Item item : branch.getItems().values()) {
            if (!item.isDefect()) {
                int catalog_number = item.getCatalogNumber();
                stockCountMap.put(catalog_number, stockCountMap.getOrDefault(catalog_number, 0) + 1);
            }
        }

        for (int catalog_number : branch.getCatalogNumbers()) {
            if (products.containsKey(catalog_number)) {
                int count = stockCountMap.getOrDefault(catalog_number, 0);
                products.get(catalog_number).setTotalQuantity(count);
            }
        }

        report.append("Reorder Alert Report for Branch ").append(branch_id).append(":\n");

        for (int catalog_number : branch.getCatalogNumbers()) {
            Product product = products.get(catalog_number);
            if (product == null) continue;

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
            return "All the products in Branch " + branch_id + " are above their minimum required amount.";
        }

        return report.toString();
    }

    /**
     * Checks if a specific product in a branch has fallen below the minimum quantity
     * and needs a reorder alert after removal.
     *
     * @param branch_id the ID of the branch
     * @param catalog_number the catalog number of the product
     * @return true if a reorder alert should be triggered, false otherwise
     */
    public boolean shouldTriggerAlertAfterRemoval(int branch_id, int catalog_number) {
        Branch branch = branches.get(branch_id);
        if (branch == null) return false;

        long count = branch.getItems().values().stream()
                .filter(item -> item.getCatalogNumber() == catalog_number && !item.isDefect())
                .count();

        Product product = products.get(catalog_number);
        if (product == null) return false;

        int min_required = product.getMinimumQuantityForAlert();

        return count < min_required;
    }






}
