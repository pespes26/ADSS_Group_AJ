package Inventory.Repository;

import Inventory.DAO.IProductDAO;
import Inventory.DAO.JdbcProductDAO;
import Inventory.DTO.ProductDTO;

import java.sql.SQLException;
import java.util.List;

public class ProductRepositoryImpl implements IProductRepository {
    private final IProductDAO productdao;

    public ProductRepositoryImpl() {
        this.productdao = new JdbcProductDAO();
    }

    @Override
    public void addProduct(ProductDTO product) throws SQLException {
        productdao.Insert(product);
    }

    @Override
    public void updateProduct(ProductDTO product) throws SQLException {
        productdao.Update(product);
    }

    @Override
    public void deleteProduct(int catalogNumber) throws SQLException {
        productdao.DeleteByCatalogNumber(catalogNumber);
    }

    @Override
    public ProductDTO getProductByCatalogNumber(int catalogNumber) throws SQLException {
        return productdao.GetProductByCatalogNumber(catalogNumber);
    }

    @Override
    public List<ProductDTO> getAllProducts() throws SQLException {
        return productdao.getAllProducts();
    }

    @Override
    public boolean productExists(int catalogNumber) throws SQLException {
        ProductDTO product = productdao.GetProductByCatalogNumber(catalogNumber);
        return product != null;
    }

    @Override
    public void updateQuantityInStore(int catalogNumber, int quantity) throws SQLException {
        ProductDTO product = productdao.GetProductByCatalogNumber(catalogNumber);
        if (product != null) {
            product.setQuantityInStore(quantity);
            productdao.Update(product);
        }
    }

    @Override
    public void updateQuantityInWarehouse(int catalogNumber, int quantity) throws SQLException {
        ProductDTO product = productdao.GetProductByCatalogNumber(catalogNumber);
        if (product != null) {
            product.setQuantityInWarehouse(quantity);
            productdao.Update(product);
        }
    }

    @Override
    public void updateQuantities(int catalogNumber, int storeQuantity, int warehouseQuantity) throws SQLException {
        ProductDTO product = productdao.GetProductByCatalogNumber(catalogNumber);
        if (product != null) {
            product.setQuantityInStore(storeQuantity);
            product.setQuantityInWarehouse(warehouseQuantity);
            productdao.Update(product);
        }
    }
}
