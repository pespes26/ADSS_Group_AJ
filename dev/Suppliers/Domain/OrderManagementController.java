package Suppliers.Domain;

import Suppliers.DTO.OrderDTO;
import Suppliers.DAO.IOrderDAO;

import java.sql.SQLException;
import java.util.List;

public class OrderManagementController {
    private final IOrderDAO orderDAO;

    public OrderManagementController(IOrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }

    public List<OrderDTO> getAllOrders() throws SQLException {
        return orderDAO.getAll();
    }
}
