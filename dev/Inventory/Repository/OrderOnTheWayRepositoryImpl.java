package Inventory.Repository;

import Inventory.DAO.IOrderOnTheWayDAO;
import Inventory.DAO.JdbcOrderOnTheWayDAO;
import Inventory.DTO.OrderOnTheWayDTO;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class OrderOnTheWayRepositoryImpl implements IOrderOnTheWayRepository {
    private final IOrderOnTheWayDAO dao = new JdbcOrderOnTheWayDAO();

    @Override
    public List<OrderOnTheWayDTO> getOrdersInTransit() throws SQLException {
        return dao.getAllOrders().stream()
            .filter(order -> order.getStatus().equalsIgnoreCase("IN_TRANSIT"))
            .collect(Collectors.toList());
    }

    @Override
    public List<OrderOnTheWayDTO> getPeriodicOrdersInTransit(int branchId) throws SQLException {
        return dao.getAllOrders().stream()
            .filter(order -> order.getBranchId() == branchId 
                        && order.getStatus().equals("IN_TRANSIT") 
                        && order.isPeriodic())
            .collect(Collectors.toList());
    }

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
