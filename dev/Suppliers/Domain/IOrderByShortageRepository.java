package Suppliers.Domain;

import Suppliers.DTO.OrderDTO;
import Suppliers.DTO.ShortageOrderProductDetails;

import java.sql.SQLException;
import java.util.Map;

public interface IOrderByShortageRepository {

    ShortageOrderProductDetails getShortageOrderProductDetails(Map<Integer, Integer> requestedProducts) throws SQLException;

    /**
     * Creates a new order and persists it.
     */
    void createOrder(OrderDTO order) throws SQLException;
}

