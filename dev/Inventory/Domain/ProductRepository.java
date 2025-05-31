package Inventory.Domain;

import Inventory.DTO.ProductDTO;

import java.sql.SQLException;
import java.util.List;

public interface ProductRepository {


    void addProduct(ProductDTO dto) throws SQLException;

    void UpdateProduct(ProductDTO dto);

    ProductDTO getProductByCatalogNumber(int catalogNumber) throws SQLException;

    List<ProductDTO> getAllProducts();

    void delete(int catalogNumber) throws SQLException;

    void updateStoreQuantity(int newQuantity,ProductDTO dto) throws SQLException;

    void updateWarehouseQuantity(int newQuantity,ProductDTO dto ) throws SQLException;

    void updateStoreDiscount(ProductDTO dto, double discount) throws SQLException;

    void updateSupplierDiscount(ProductDTO dto, double discount) throws SQLException;





    int getStoreQuantity(int catalogNumber) throws SQLException;

    int getWarehouseQuantity(int catalogNumber) throws SQLException;


}