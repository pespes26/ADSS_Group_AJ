package Inventory.DAO;

import Inventory.DataBase.DatabaseConnector;
import Inventory.DTO.ItemDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcItemDAO implements IItemsDAO {

    static {
        try (Connection conn = DatabaseConnector.connect();
             Statement statement = conn.createStatement()) {

            String createTableSql = """
                    CREATE TABLE IF NOT EXISTS Items (
                     item_id INTEGER PRIMARY KEY AUTOINCREMENT,
                     catalog_number INTEGER NOT NULL,
                     branch_id INTEGER NOT NULL,
                     storage_location TEXT,
                     section_in_store TEXT,
                     is_defect BOOLEAN DEFAULT 0,
                     expiration_date TEXT,
                     sale_date TEXT,
                     FOREIGN KEY (catalog_number) REFERENCES Products(catalog_number)
                    );""";

            statement.execute(createTableSql);
            System.out.println("The 'Items' table was created (if it did not already exist).");

        } catch (SQLException e) {
            System.err.println("Error creating the 'Items' table:");
            e.printStackTrace();
        }
    }

    @Override
    public void Insert(ItemDTO dto) throws SQLException {
        String sql = "INSERT INTO items (catalog_number, branch_id, storage_location, is_defect, expiration_date) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstatement = conn.prepareStatement(sql)) {

            pstatement.setInt(1, dto.getCatalogNumber());
            pstatement.setInt(2, dto.getBranchId());
            pstatement.setString(3, dto.getStorageLocation());
            pstatement.setBoolean(4, dto.IsDefective());
            pstatement.setString(5, dto.getItemExpiringDate());
            pstatement.executeUpdate();
        }
    }

    @Override
    public void Update(ItemDTO dto) throws SQLException {
        String sql = "UPDATE items SET catalog_number = ?, branch_id = ?, storage_location = ?, is_defect = ?, expiration_date = ? WHERE item_id = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstatement = conn.prepareStatement(sql)) {

            pstatement.setInt(1, dto.getCatalogNumber());
            pstatement.setInt(2, dto.getBranchId());
            pstatement.setString(3, dto.getStorageLocation());
            pstatement.setBoolean(4, dto.IsDefective());
            pstatement.setString(5, dto.getItemExpiringDate());
            pstatement.setInt(6, dto.getItemId());

            int rowsAffected = pstatement.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("No item found with item_id = " + dto.getItemId());
            } else {
                System.out.println("The item was updated successfully.");
            }
        }
    }

    @Override
    public void DeleteByItemId(int id) throws SQLException {
        String sql = "DELETE FROM items WHERE item_id = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstatement = conn.prepareStatement(sql)) {

            pstatement.setInt(1, id);
            pstatement.executeUpdate();
        }
    }

    @Override
    public ItemDTO GetById(int id) throws SQLException {
        String sql = "SELECT * FROM items WHERE item_id = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstatement = conn.prepareStatement(sql)) {

            pstatement.setInt(1, id);
            try (ResultSet rs = pstatement.executeQuery()) {
                if (rs.next()) {
                    ItemDTO dto = new ItemDTO();
                    dto.setItemId(rs.getInt("item_id"));
                    dto.setCatalogNumber(rs.getInt("catalog_number"));
                    dto.setBranchId(rs.getInt("branch_id"));
                    dto.setLocation(rs.getString("storage_location"));
                    dto.setIsDefective(rs.getBoolean("is_defect"));
                    dto.setExpirationDate(rs.getString("expiration_date"));
                    return dto;
                }
            }
        }

        return null;
    }

    @Override
    public void signAsDefective(int itemId) throws SQLException {
        String sql = "UPDATE items SET is_defect = 1 WHERE item_id = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstatement = conn.prepareStatement(sql)) {

            pstatement.setInt(1, itemId);
            int rowsAffected = pstatement.executeUpdate();

            if (rowsAffected == 0) {
                System.out.println("No item found with item_id = " + itemId);
            } else {
                System.out.println("Item with item_id = " + itemId + " marked as defective.");
            }
        }
    }

    @Override
    public List<ItemDTO> getAllItems() throws SQLException {
        List<ItemDTO> items = new ArrayList<>();
        String sql = "SELECT * FROM items";

        try (Connection conn = DatabaseConnector.connect();
             Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                ItemDTO dto = new ItemDTO();
                dto.setItemId(rs.getInt("item_id"));
                dto.setCatalogNumber(rs.getInt("catalog_number"));
                dto.setBranchId(rs.getInt("branch_id"));
                dto.setLocation(rs.getString("storage_location"));
                dto.setIsDefective(rs.getBoolean("is_defect"));
                dto.setExpirationDate(rs.getString("expiration_date"));
                items.add(dto);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving items from DB: " + e.getMessage());
            throw e;
        }

        return items;
    }
}
