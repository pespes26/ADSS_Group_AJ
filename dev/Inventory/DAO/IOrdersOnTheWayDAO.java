package Inventory.DAO;

import Inventory.DTO.OrderOnTheWayDTO;

import java.sql.SQLException;

public interface IOrdersOnTheWayDAO {
    void Insert(OrderOnTheWayDTO dto) throws SQLException;

    void Update(OrderOnTheWayDTO dto) throws SQLException;

    void DeleteByOrderId(int Id) throws SQLException;

    OrderOnTheWayDTO GetById(int Id) throws SQLException;

}
