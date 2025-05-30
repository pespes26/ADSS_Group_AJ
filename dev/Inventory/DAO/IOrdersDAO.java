package Inventory.DAO;

import Inventory.DTO.OrderDTO;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface for managing order operations in the database.
 */
public interface IOrdersDAO {

    /**
     * Inserts a new order into the database.
     *
     * @param dto the order to insert
     * @throws SQLException if a database error occurs
     */
    void insertOrder(OrderDTO dto) throws SQLException;

    /**
     * Updates an existing order in the database.
     *
     * @param dto the updated order data
     * @throws SQLException if the order doesn't exist or update fails
     */
    void updateOrder(OrderDTO dto) throws SQLException;

    /**
     * Deletes an order from the database by its ID.
     *
     * @param orderId the order ID
     * @throws SQLException if deletion fails
     */
    void deleteOrderById(int orderId) throws SQLException;

    /**
     * Retrieves an order from the database by its ID.
     *
     * @param orderId the order ID
     * @return the order data, or null if not found
     * @throws SQLException if a query error occurs
     */
    OrderDTO getOrderById(int orderId) throws SQLException;

    /**
     * Retrieves all orders from the database.
     *
     * @return a list of all orders
     * @throws SQLException if a query error occurs
     */
    List<OrderDTO> getAllOrders() throws SQLException;
}