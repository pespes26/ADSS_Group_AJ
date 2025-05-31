package Inventory.Domain;

import Inventory.DAO.IProductDAO;
import Inventory.DAO.JdbcProductDAO;
import Inventory.DTO.ProductDTO;

import java.sql.SQLException;
import java.util.List;

public class ProductRepositoryImpl implements ProductRepository {
    private final IProductDAO productdao;

    public ProductRepositoryImpl() {
        this.productdao = new JdbcProductDAO();
    }

    @Override
    public void addProduct(ProductDTO dto) throws SQLException {
        try {
            productdao.Insert(dto);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void UpdateProduct(ProductDTO dto){
        try {
            productdao.Update(dto);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ProductDTO getProductByCatalogNumber(int catalogNumber) throws SQLException {
        try {
            return productdao.GetProductByCatalogNumber(catalogNumber);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        return productdao.getAllProducts();
    }

    @Override
    public void delete(int catalogNumber) throws SQLException {
        productdao.DeleteByCatalogNumber(catalogNumber);
    }

    @Override
    public void updateStoreQuantity(int newQuantity,ProductDTO dto) throws SQLException {
        dto.setQuantityInStore(newQuantity);
        productdao.Update(dto);
    }

    @Override
    public void updateWarehouseQuantity(int newQuantity,ProductDTO dto) throws SQLException {
        dto.setQuantityInStore(newQuantity);
        productdao.Update(dto);
    }

    @Override
    public void updateStoreDiscount(ProductDTO dto, double discount) throws SQLException {
        dto.setStoreDiscount(discount);
        productdao.Update(dto);
    }

    @Override
    public void updateSupplierDiscount(ProductDTO dto, double discount) throws SQLException {
        dto.setSupplierDiscount(discount);
        productdao.Update(dto);
    }









    @Override
    public int getStoreQuantity(int catalogNumber) throws SQLException {
        ProductDTO dto=productdao.GetProductByCatalogNumber(catalogNumber);
        return dto.getQuantityInStore();
    }

    @Override
    public int getWarehouseQuantity(int catalogNumber) throws SQLException {
        ProductDTO dto = productdao.GetProductByCatalogNumber(catalogNumber);
        return dto.getQuantityInWarehouse();
    }



}
