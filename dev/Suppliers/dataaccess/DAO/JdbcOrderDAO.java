package Suppliers.dataaccess.DAO;

import Suppliers.DTO.OrderDTO;

import java.sql.SQLException;
import java.util.List;

public class JdbcOrderDAO implements IOrderDAO {
    @Override
    public void insert(OrderDTO dto) throws SQLException {

    }

    @Override
    public void update(OrderDTO dto) throws SQLException {

    }

    @Override
    public void deleteById(int orderId) throws SQLException {

    }

    @Override
    public OrderDTO getById(int orderId) throws SQLException {
        return null;
    }

    @Override
    public List<OrderDTO> getAll() throws SQLException {
        return List.of();
    }

    @Override
    public List<OrderDTO> getBySupplierId(int supplierId) throws SQLException {
        return List.of();
    }
}
