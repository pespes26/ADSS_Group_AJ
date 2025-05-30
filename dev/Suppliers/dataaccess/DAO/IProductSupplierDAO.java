package Suppliers.dataaccess.DAO;

import Suppliers.DTO.ProductSupplierDTO;

import java.sql.SQLException;
import java.util.List;

public interface IProductSupplierDAO {

 void insert(ProductSupplierDTO dto) throws SQLException;

    void update(ProductSupplierDTO dto) throws SQLException;

    void deleteOneProduct(int productId, int catalogNumber, int supplierId) throws SQLException;

    void deleteAllProductsFromSupplier(int Supplier_ID) throws SQLException;

    void deleteAllProductsFromAgreement(int Agreement_ID) throws SQLException;

     void updateProductUnit(int catalogNumber, String newUnit, int agreementID);

    void SetPrice(int catalogNumber, int newPrice, int agreementID);

    ProductSupplierDTO getOneProduct(int productId, int catalogNumber, int supplierId) throws SQLException;

     List<ProductSupplierDTO> getProductsByAgreement(int supplier_ID, int agreement_ID) throws SQLException;
}
