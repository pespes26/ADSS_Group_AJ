package Inventory.DAO;

import Inventory.DTO.ItemDTO;
import java.sql.SQLException;

public interface IItemsDAO {
    void Insert(ItemDTO dto) throws SQLException;
    void Update(ItemDTO dto) throws SQLException;
    void DeleteByItemId(int Id) throws SQLException;

    ItemDTO GetById(int Id) throws SQLException;

    void signAsDefective(int itemId) throws SQLException;


}
