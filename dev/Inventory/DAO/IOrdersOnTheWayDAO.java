package Inventory.DAO;

import Inventory.DTO.OrderOnTheWayDTO;

import java.sql.SQLException;

public interface IOrdersOnTheWayDAO {
    void insertOrderOnTheWay(OrderOnTheWayDTO dto) throws SQLException;
    void updateOrderOnTheWay(OrderOnTheWayDTO dto) throws SQLException;
    void deleteOrderOnTheWayById(int id) throws SQLException;
    OrderOnTheWayDTO getOrderOnTheWayById(int id) throws SQLException;

}
