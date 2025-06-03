package Inventory.Repository;

import Inventory.DAO.IPeriodicOrderDAO;
import Inventory.DAO.JdbcPeriodicOrderDAO;
import Inventory.DTO.OrderOnTheWayDTO;
import Inventory.DTO.PeriodicOrderDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Concrete implementation of the IPeriodicOrderRepository interface,
 * delegating work to the DAO layer for database operations.
 */
public class PeriodicOrderRepositoryImpl implements IPeriodicOrderRepository {

    private final IPeriodicOrderDAO periodicOrderDAO;
    private final IOrderOnTheWayRepository orderOnTheWayRepository;

    public PeriodicOrderRepositoryImpl() {
        this.periodicOrderDAO = new JdbcPeriodicOrderDAO();
        this.orderOnTheWayRepository = new OrderOnTheWayRepositoryImpl();
    }

    @Override
    public List<PeriodicOrderDTO> getPeriodicOrdersInTransit() throws SQLException {
        List<OrderOnTheWayDTO> ordersInTransit = orderOnTheWayRepository.getOrdersInTransit();
        return ordersInTransit.stream()
                .filter(OrderOnTheWayDTO::isPeriodic)
                .map(order -> {
                    try {
                        return periodicOrderDAO.getPeriodicOrderById(order.getOrderId());
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(order -> order != null)
                .collect(Collectors.toList());
    }

    @Override
    public List<PeriodicOrderDTO> getAllPeriodicOrders() throws SQLException {
        return periodicOrderDAO.getAllPeriodicOrders();
    }

    @Override
    public PeriodicOrderDTO getPeriodicOrderById(int orderId) throws SQLException {
        return periodicOrderDAO.getPeriodicOrderById(orderId);
    }

    @Override
    public void insertPeriodicOrder(PeriodicOrderDTO dto) throws SQLException {
        periodicOrderDAO.insertPeriodicOrder(dto);
    }

    @Override
    public void updatePeriodicOrder(PeriodicOrderDTO dto) throws SQLException {
        periodicOrderDAO.updatePeriodicOrder(dto);
    }

    @Override
    public void deletePeriodicOrderById(int orderId) throws SQLException {
        periodicOrderDAO.deletePeriodicOrderById(orderId);
    }

    @Override
    public void saveAll(List<PeriodicOrderDTO> orders) throws SQLException {
        for (PeriodicOrderDTO order : orders) {
            periodicOrderDAO.insertPeriodicOrder(order);
        }
    }
}
