package Inventory.DAO;

import Inventory.DTO.OrderOnTheWayDTO;
import java.sql.SQLException;
import java.util.List;

public interface IOrderOnTheWayDAO {
    void insertOrder(OrderOnTheWayDTO dto) throws SQLException;
    void updateOrder(OrderOnTheWayDTO dto) throws SQLException;
    void deleteOrderById(int id) throws SQLException;
    OrderOnTheWayDTO getOrderById(int id) throws SQLException;
    List<OrderOnTheWayDTO> getAllOrders() throws SQLException;
}