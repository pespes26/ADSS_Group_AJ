package Inventory.DAO;
import Inventory.DTO.OrderDTO;




import java.sql.SQLException;

public interface IOrdersDAO {
    void insert(OrderDTO dto) throws SQLException;
    void update(OrderDTO dto) throws SQLException;
    void deletebyorderid(int Id) throws SQLException;

    OrderDTO getbyid(int id) throws SQLException;


}
