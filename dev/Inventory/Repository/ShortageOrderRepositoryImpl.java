package Inventory.Repository;

import Inventory.DAO.IShortageOrderDAO;
import Inventory.DAO.JdbcShortageOrderDAO;
import Inventory.DTO.ShortageOrderDTO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ShortageOrderRepositoryImpl implements IShortageOrderRepository {

    private final IShortageOrderDAO dao;

    public ShortageOrderRepositoryImpl() {
        this.dao = new JdbcShortageOrderDAO();
    }

    @Override
    public void insert(ShortageOrderDTO dto) throws SQLException {
        dao.insertShortageOrder(dto);
    }

    @Override
    public void update(ShortageOrderDTO dto) throws SQLException {
        dao.updateShortageOrder(dto);
    }

    @Override
    public void deleteById(int id) throws SQLException {
        dao.deleteShortageOrderById(id);
    }

    @Override
    public ShortageOrderDTO getById(int id) throws SQLException {
        return dao.getShortageOrderById(id);
    }

    @Override
    public List<ShortageOrderDTO> getAll() throws SQLException {
        return dao.getAllShortageOrders();
    }
}
