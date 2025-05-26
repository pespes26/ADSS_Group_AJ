package Suppliers.dataaccess.DAO;

import Suppliers.DTO.SupplierDTO;

import java.sql.SQLException;
import java.util.List;

public class JdbcSupplierDAO implements ISupplierDAO {
    @Override
    public void insert(SupplierDTO dto) throws SQLException {

    }

    @Override
    public void update(SupplierDTO dto) throws SQLException {

    }

    @Override
    public void deleteById(int supplierId) throws SQLException {

    }

    @Override
    public SupplierDTO getById(int supplierId) throws SQLException {
        return null;
    }

    @Override
    public List<SupplierDTO> getAll() throws SQLException {
        return List.of();
    }
}
