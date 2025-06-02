package Inventory.Repository;

import Inventory.DTO.ShortageOrderDTO;

import java.sql.SQLException;
import java.util.List;

public interface IShortageOrderRepository {
    void insert(ShortageOrderDTO dto) throws SQLException;
    void update(ShortageOrderDTO dto) throws SQLException;
    void deleteById(int id) throws SQLException;
    ShortageOrderDTO getById(int id) throws SQLException;
    List<ShortageOrderDTO> getAll() throws SQLException;
}
