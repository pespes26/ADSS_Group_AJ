package Inventory.Domain;

import Inventory.DAO.JdbcProductDAO;
import Inventory.DTO.ProductDTO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import Inventory.DTO.ItemDTO;
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

            for (ItemDTO item : branch.getItems().values()) {
                if (item.IsDefective()) {
                    Product product = products.get(item.getCatalogNumber());
                    if (product != null) {
                        has_defects = true;
                        report.append(counter++).append(". Item ID: ").append(item.getItemId())
                                .append(", Name: ").append(product.getProductName())
                                .append(", Category: ").append(product.getCategory())
                                .append(", Sub-Category: ").append(product.getSubCategory())
                                .append(", Size: ").append(product.getSize())
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
        List<ItemDTO> expired_items = new ArrayList<>();

        for (Branch branch : branches.values()) {
            if (branch.getBranchId() != current_branch_id) continue;

            for (ItemDTO item : branch.getItems().values()) {
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
            for (ItemDTO item : expired_items) {
                Product product = products.get(item.getCatalogNumber());
                if (product != null) {
                    report.append(counter++).append(". Item ID: ").append(item.getItemId())
                            .append(", Name: ").append(product.getProductName())
                            .append(", Expired on: ").append(item.getItemExpiringDate())
                            .append(", Category: ").append(product.getCategory())
                            .append(", Sub-Category: ").append(product.getSubCategory())
                            .append(", Size: ").append(product.getSize())
                            .append(", Location: ").append(item.getStorageLocation())
                            .append("\n");
                }
            }
        }

        return report.toString();
    }




    /**
     * Generates an inventory report filtered by categories and sizes.
     *
     * @param categories the categories to include
     * @param branchId the branch ID
     * @param sizeFilters list of sizes to include (1-small, 2-medium, 3-big)
     * @return formatted report string
     */
    public String inventoryReportByCategories(String[] categories, int branchId, List<Integer> sizeFilters) {
        StringBuilder report = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        boolean anyCategoryFound = false;

        Branch branch = branches.get(branchId);
        if (branch == null) {
            return "Branch with ID " + branchId + " does not exist.";
        }

        for (String categoryName : categories) {
            report.append("Category: ").append(categoryName).append("\n");

            Map<String, Map<Integer, List<ItemDTO>>> subCategoryMap = new TreeMap<>();

            for (ItemDTO item : branch.getItems().values()) {
                Product product = products.get(item.getCatalogNumber());
                if (product != null
                        && product.getCategory().equalsIgnoreCase(categoryName)
                        && sizeFilters.contains(product.getSize())) {

                    String subCategory = product.getSubCategory();
                    int size = product.getSize();
                    subCategoryMap
                            .computeIfAbsent(subCategory, k -> new TreeMap<>())
                            .computeIfAbsent(size, k -> new ArrayList<>())
                            .add(item);
                }
            }

            if (subCategoryMap.isEmpty()) {
                report.append("  No matching items found for category.\n");
            } else {
                anyCategoryFound = true;
                appendReportData(subCategoryMap, report, formatter);
            }

            report.append("------------------------------------------------------------\n");
        }

        if (!anyCategoryFound) {
            return "No valid categories found or no items matching size filters.";
        }

        return report.toString();
    }

    /**
     * Generates an inventory report filtered by sub-categories and sizes.
     *
     * @param subCategories the sub-categories to include
     * @param branchId the branch ID
     * @param sizeFilters list of sizes to include
     * @return formatted report string
     */
    public String inventoryReportBySubCategories(String[] subCategories, int branchId, List<Integer> sizeFilters) {
        StringBuilder report = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        boolean anySubCategoryFound = false;

        Branch branch = branches.get(branchId);
        if (branch == null) {
            return "Branch with ID " + branchId + " does not exist.";
        }

        for (String subCategoryName : subCategories) {
            report.append("Sub-Category: ").append(subCategoryName).append("\n");

            Map<Integer, List<ItemDTO>> sizeMap = new TreeMap<>();

            for (ItemDTO item : branch.getItems().values()) {
                Product product = products.get(item.getCatalogNumber());
                if (product != null
                        && product.getSubCategory().equalsIgnoreCase(subCategoryName)
                        && sizeFilters.contains(product.getSize())) {

                    int size = product.getSize();
                    sizeMap.computeIfAbsent(size, k -> new ArrayList<>()).add(item);
                }
            }

            if (sizeMap.isEmpty()) {
                report.append("  No matching items found for sub-category.\n");
            } else {
                anySubCategoryFound = true;
                appendReportDataSingleLevel(sizeMap, report, formatter);
            }

            report.append("------------------------------------------------------------\n");
        }

        if (!anySubCategoryFound) {
            return "No valid sub-categories found or no items matching size filters.";
        }

        return report.toString();
    }

    /**
     * Generates an inventory report filtered by catalog numbers and sizes.
     *
     * @param catalogNumbers the catalog numbers to include
     * @param branchId the branch ID
     * @param sizeFilters list of sizes to include
     * @return formatted report string
     */
    public String inventoryReportByCatalogNumbers(String[] catalogNumbers, int branchId, List<Integer> sizeFilters) {
        StringBuilder report = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        boolean anyCatalogFound = false;

        Branch branch = branches.get(branchId);
        if (branch == null) {
            return "Branch with ID " + branchId + " does not exist.";
        }

        Set<Integer> catalogSet = new HashSet<>();
        for (String s : catalogNumbers) {
            try {
                catalogSet.add(Integer.parseInt(s.trim()));
            } catch (NumberFormatException ignored) {}
        }

        for (Integer catalogNumber : catalogSet) {
            report.append("Catalog Number: ").append(catalogNumber).append("\n");

            List<ItemDTO> matchedItems = new ArrayList<>();

            for (ItemDTO item : branch.getItems().values()) {
                if (item.getCatalogNumber() == catalogNumber) {
                    Product product = products.get(item.getCatalogNumber());
                    if (product != null && sizeFilters.contains(product.getSize())) {
                        matchedItems.add(item);
                    }
                }
            }

            if (matchedItems.isEmpty()) {
                report.append("  No matching items found for catalog number.\n");
            } else {
                anyCatalogFound = true;
                matchedItems.sort(Comparator.comparing(item -> {
                    try {
                        return LocalDate.parse(item.getItemExpiringDate(), formatter);
                    } catch (Exception e) {
                        return LocalDate.MAX;
                    }
                }));

                int count = 1;
                for (ItemDTO item : matchedItems) {
                    Product product = products.get(item.getCatalogNumber());
                    if (product != null) {
                        report.append("    ").append(count++).append(". ")
                                .append("Item ID: ").append(item.getItemId())
                                .append(", Name: ").append(product.getProductName())
                                .append(", Location: ").append(item.getStorageLocation())
                                .append(", Expiring: ").append(item.getItemExpiringDate())
                                .append("\n");
                    }
                }
            }

            report.append("------------------------------------------------------------\n");
        }

        if (!anyCatalogFound) {
            return "No valid catalog numbers found or no items matching size filters.";
        }

        return report.toString();
    }

    private void appendReportData(Map<String, Map<Integer, List<ItemDTO>>> subCategoryMap, StringBuilder report, DateTimeFormatter formatter) {
        for (Map.Entry<String, Map<Integer, List<ItemDTO>>> subEntry : subCategoryMap.entrySet()) {
            report.append("  Sub-Category: ").append(subEntry.getKey()).append("\n");
            for (Map.Entry<Integer, List<ItemDTO>> sizeEntry : subEntry.getValue().entrySet()) {
                int size = sizeEntry.getKey();
                String sizeLabel = switch (size) {
                    case 1 -> "Small";
                    case 2 -> "Medium";
                    case 3 -> "Big";
                    default -> "Unknown Size";
                };
                report.append("    Size: ").append(sizeLabel).append("\n");

                List<ItemDTO> sortedItems = sizeEntry.getValue();
                sortedItems.sort(Comparator.comparing(item -> {
                    try {
                        return LocalDate.parse(item.getItemExpiringDate(), formatter);
                    } catch (Exception e) {
                        return LocalDate.MAX;
                    }
                }));

                int count = 1;
                for (ItemDTO item : sortedItems) {
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

    private void appendReportDataSingleLevel(Map<Integer, List<ItemDTO>> sizeMap, StringBuilder report, DateTimeFormatter formatter) {
        for (Map.Entry<Integer, List<ItemDTO>> sizeEntry : sizeMap.entrySet()) {
            int size = sizeEntry.getKey();
            String sizeLabel = switch (size) {
                case 1 -> "Small";
                case 2 -> "Medium";
                case 3 -> "Big";
                default -> "Unknown Size";
            };
            report.append("  Size: ").append(sizeLabel).append("\n");

            List<ItemDTO> sortedItems = sizeEntry.getValue();
            sortedItems.sort(Comparator.comparing(item -> {
                try {
                    return LocalDate.parse(item.getItemExpiringDate(), formatter);
                } catch (Exception e) {
                    return LocalDate.MAX;
                }
            }));

            int count = 1;
            for (ItemDTO item : sortedItems) {
                Product product = products.get(item.getCatalogNumber());
                if (product != null) {
                    report.append("    ").append(count++).append(". ")
                            .append("Item ID: ").append(item.getItemId())
                            .append(", Name: ").append(product.getProductName())
                            .append(", Location: ").append(item.getStorageLocation())
                            .append(", Expiring: ").append(item.getItemExpiringDate())
                            .append("\n");
                }
            }
        }
    }



    
    public String generateShortageInventoryReport(int branch_id) {
        StringBuilder report = new StringBuilder();
        boolean found = false;

        Branch branch = branches.get(branch_id);
        if (branch == null) {
            return "Branch " + branch_id + " not found.";
        }

        // Count how many non-defective items exist for each catalog number in the branch
        Map<Integer, Integer> stockCountMap = new HashMap<>();
        for (ItemDTO item : branch.getItems().values()) {
            if (!item.IsDefective()) {
                int catalog_number = item.getCatalogNumber();
                stockCountMap.put(catalog_number, stockCountMap.getOrDefault(catalog_number, 0) + 1);
            }
        }

        report.append("Reorder Alert Report for Branch ").append(branch_id).append(":\n");

        // Get all products from the database
        JdbcProductDAO productDAO = null;
        List<ProductDTO> dbProducts = productDAO.getAllProducts();
        for (ProductDTO productDTO : dbProducts) {
            int catalog_number = productDTO.getCatalogNumber();
            Product product = products.get(catalog_number);
            if (product == null) continue; // skip if not in memory

            int inStock = stockCountMap.getOrDefault(catalog_number, 0);
            int min_required = Math.max(1, (int) (0.5 * product.getProductDemandLevel() + 0.5 * product.getSupplyTime()));

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
                .filter(item -> item.getCatalogNumber() == catalog_number && !item.IsDefective())
                .count();

        Product product = products.get(catalog_number);
        if (product == null) return false;

        int min_required = product.getMinimumQuantityForAlert();

        return count < min_required;
    }






}
