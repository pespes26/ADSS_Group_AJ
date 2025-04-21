package Service;

import Domain.Discount;
import Domain.Product;

import java.time.LocalDate;
import java.util.HashMap;

public class DiscountController {
    private final HashMap<Integer, Product> products;

    public DiscountController(HashMap<Integer, Product> products) {
        this.products = products;
    }

    public boolean setStoreDiscountForCategory(String category, Discount discount) {
        return applyDiscountToGroup(discount, category, null, -1, DiscountType.STORE);
    }

    public boolean setStoreDiscountForSubCategory(String subCategory, Discount discount) {
        return applyDiscountToGroup(discount, null, subCategory, -1, DiscountType.STORE);
    }

    public boolean setStoreDiscountForCatalogNumber(int catalogNumber, Discount discount) {
        return applyDiscountToGroup(discount, null, null, catalogNumber, DiscountType.STORE);
    }

    public boolean setSupplierDiscountForCategory(String category, Discount discount) {
        return applyDiscountToGroup(discount, category, null, -1, DiscountType.SUPPLIER);
    }

    public boolean setSupplierDiscountForSubCategory(String subCategory, Discount discount) {
        return applyDiscountToGroup(discount, null, subCategory, -1, DiscountType.SUPPLIER);
    }

    public boolean setSupplierDiscountForCatalogNumber(int catalogNumber, Discount discount) {
        return applyDiscountToGroup(discount, null, null, catalogNumber, DiscountType.SUPPLIER);
    }

    private boolean applyDiscountToGroup(Discount discount, String category, String subCategory, int catalogNumber, DiscountType type) {

        LocalDate today = LocalDate.now();
        if (discount.getStartDate() == null || discount.getEndDate() == null || discount.getEndDate().isBefore(discount.getStartDate())) {
            return false;
        }

        boolean applied = false;
        for (Product product : products.values()) {
            boolean match = (product.getCategory().equalsIgnoreCase(category))
                    || (product.getSubCategory().equalsIgnoreCase(subCategory))
                    || (catalogNumber != -1 && product.getCatalogNumber() == catalogNumber);

            if (match) {
                if (type == DiscountType.STORE) {
                    product.setStoreDiscount(discount.getDiscountRate());
                } else {
                    product.setSupplierDiscount(discount.getDiscountRate());
                }

                product.setDiscount(discount);
                recalculatePrices(product);
                applied = true;

                if (catalogNumber != -1) break;
            }
        }

        return applied;
    }

    private void recalculatePrices(Product product) {
        double costAfter = product.getCostPriceBeforeSupplierDiscount() * (1 - product.getSupplierDiscount() / 100.0);
        double saleBefore = costAfter * 2;
        double saleAfter = saleBefore * (1 - product.getStoreDiscount() / 100.0);

        product.setCostPriceAfterSupplierDiscount(costAfter);
        product.setSalePriceBeforeStoreDiscount(saleBefore);
        product.setSalePriceAfterStoreDiscount(saleAfter);
    }
}
