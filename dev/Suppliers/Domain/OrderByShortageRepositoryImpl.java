package Suppliers.Domain;

import Suppliers.DTO.DiscountDTO;
import Suppliers.DTO.OrderDTO;
import Suppliers.DTO.ProductSupplierDTO;
import Suppliers.DTO.ShortageOrderProductDetails;
import Suppliers.dataaccess.DAO.IDiscountDAO;
import Suppliers.dataaccess.DAO.IOrderDAO;
import Suppliers.dataaccess.DAO.IProductSupplierDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderByShortageRepositoryImpl implements IOrderByShortageRepository {
    private final IProductSupplierDAO productSupplierDAO;
    private final IDiscountDAO discountDAO;
    private final IOrderDAO orderDAO;

    public OrderByShortageRepositoryImpl(IProductSupplierDAO productSupplierDAO,IDiscountDAO discountDAO,IOrderDAO orderDAO){
        this.productSupplierDAO = productSupplierDAO;
        this.discountDAO = discountDAO;
        this.orderDAO = orderDAO;
    }


    @Override
    public ShortageOrderProductDetails getShortageOrderProductDetails(Map<Integer, Integer> requestedProducts) throws SQLException {
        List<DiscountDTO> discounts = new ArrayList<>();
        List<ProductSupplierDTO> cheapestWithoutDiscount = new ArrayList<>();
        Map<Integer, Integer> missingProducts = new HashMap<>(); // שונה ממערך לרשימה עם הכמויות

        for (Map.Entry<Integer, Integer> entry : requestedProducts.entrySet()) {
            int productId = entry.getKey();
            int quantity = entry.getValue();

            // ניסיון להשיג הנחה
            DiscountDTO discount = discountDAO.getBestDiscount(productId, quantity);
            if (discount != null) {
                discounts.add(discount);
            } else {
                // ניסיון למצוא את הספק הזול ביותר
                ProductSupplierDTO cheapest = productSupplierDAO.getCheapestProductSupplier(productId);
                if (cheapest != null) {
                    cheapestWithoutDiscount.add(cheapest);
                } else {
                    // מוצר לא קיים בכלל - מוסיפים עם הכמות
                    missingProducts.put(productId, quantity);
                }
            }
        }

        return new ShortageOrderProductDetails(discounts, cheapestWithoutDiscount, missingProducts);
    }



    @Override
    public void createOrder(OrderDTO order) throws SQLException {
        orderDAO.insert(order);
    }
}
