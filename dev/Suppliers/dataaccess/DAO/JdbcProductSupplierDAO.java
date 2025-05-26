package Suppliers.dataaccess.DAO;

import Suppliers.DTO.ProductSupplierDTO;

import java.sql.SQLException;
import java.util.List;

public class JdbcProductSupplierDAO implements IProductSupplierDAO {
    @Override
    public void insert(ProductSupplierDTO dto) throws SQLException {

    }

    @Override
    public void update(ProductSupplierDTO dto) throws SQLException {

    }

    @Override
    public void delete(int productId, int supplierId) throws SQLException {

    }

    @Override
    public ProductSupplierDTO get(int productId, int supplierId) throws SQLException {
        return null;
    }

    @Override
    public List<ProductSupplierDTO> getSuppliersByProduct(int productId) throws SQLException {
        return List.of();
    }

    @Override
    public List<ProductSupplierDTO> getProductsBySupplier(int supplierId) throws SQLException {
        return List.of();
    }
}
