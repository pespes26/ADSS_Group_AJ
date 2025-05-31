package Suppliers.DTO;

import java.util.List;
import java.util.Map;

public class ShortageOrderProductDetails {
    private List<DiscountDTO> discounts;
    private List<ProductSupplierDTO> cheapestProductsWithoutDiscount;
    private Map<Integer, Integer> missingProducts;

    public ShortageOrderProductDetails(List<DiscountDTO> discounts, List<ProductSupplierDTO> cheapestProductsWithoutDiscount, Map<Integer, Integer> missingProducts) {
        this.discounts = discounts;
        this.cheapestProductsWithoutDiscount = cheapestProductsWithoutDiscount;
        this.missingProducts = missingProducts;
    }

    public List<DiscountDTO> getDiscounts() {
        return discounts;
    }

    public List<ProductSupplierDTO> getCheapestProductsWithoutDiscount() {
        return cheapestProductsWithoutDiscount;
    }

    public Map<Integer, Integer> getMissingProducts() {
        return missingProducts;
    }
}
