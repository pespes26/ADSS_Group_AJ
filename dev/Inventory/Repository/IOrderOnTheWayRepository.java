package Inventory.Repository;

import Inventory.DTO.OrderOnTheWayDTO;
import java.sql.SQLException;
import java.util.List;

public interface IOrderOnTheWayRepository {
    void insert(OrderOnTheWayDTO dto) throws SQLException;
    void update(OrderOnTheWayDTO dto) throws SQLException;
    void deleteById(int id) throws SQLException;
    OrderOnTheWayDTO getById(int id) throws SQLException;
    List<OrderOnTheWayDTO> getAll() throws SQLException;
}