package Inventory.DAO;
import Inventory.DTO.ProductDTO;
import java.sql.SQLException;

public interface IProductDAO {
    void Insert(ProductDTO dto) throws SQLException;

    void Update(ProductDTO dto) throws SQLException;

    void DeleteByCatalogNumber(int catalog_Number)throws SQLException;

    public ProductDTO GetProductByCatalogNumber(int catalog_number) throws SQLException;



}
