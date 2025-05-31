package Suppliers.dataaccess.DAO;

import Suppliers.DTO.OrderDTO;

import java.sql.SQLException;
import java.util.List;

public interface IOrderDAO {

    void insert(OrderDTO dto) throws SQLException;

    void deleteById(int orderId) throws SQLException;

    OrderDTO getById(int orderId) throws SQLException;

    List<OrderDTO> getAll() throws SQLException;

    List<OrderDTO> getBySupplierId(int supplierId) throws SQLException;
}
