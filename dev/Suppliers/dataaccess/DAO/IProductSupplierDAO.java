package Suppliers.dataaccess.DAO;

import Suppliers.DTO.ProductSupplierDTO;

import java.sql.SQLException;
import java.util.List;

public interface IProductSupplierDAO {

    void insert(ProductSupplierDTO dto) throws SQLException;

    void update(ProductSupplierDTO dto) throws SQLException;

    void delete(int productId, int supplierId) throws SQLException;

    ProductSupplierDTO get(int productId, int supplierId) throws SQLException;

    List<ProductSupplierDTO> getSuppliersByProduct(int productId) throws SQLException;

    List<ProductSupplierDTO> getProductsBySupplier(int supplierId) throws SQLException;
}
