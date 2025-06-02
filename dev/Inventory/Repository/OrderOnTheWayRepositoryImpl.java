package Inventory.Repository;

import Inventory.DAO.IOrderOnTheWayDAO;
import Inventory.DAO.JdbcOrderOnTheWayDAO;
import Inventory.DTO.OrderOnTheWayDTO;
import java.sql.SQLException;
import java.util.List;

public class OrderOnTheWayRepositoryImpl implements IOrderOnTheWayRepository {
    private final IOrderOnTheWayDAO dao = new JdbcOrderOnTheWayDAO();

    @Override
    public void insert(OrderOnTheWayDTO dto) throws SQLException {
        dao.insertOrder(dto);
    }

    @Override
    public void update(OrderOnTheWayDTO dto) throws SQLException {
        dao.updateOrder(dto);
    }

    @Override
    public void deleteById(int id) throws SQLException {
        dao.deleteOrderById(id);
    }

    @Override
    public OrderOnTheWayDTO getById(int id) throws SQLException {
        return dao.getOrderById(id);
    }

    @Override
    public List<OrderOnTheWayDTO> getAll() throws SQLException {
        return dao.getAllOrders();
    }
}
