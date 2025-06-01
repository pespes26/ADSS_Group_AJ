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
                     expiration_date Text,
                     sale_date Date,                     
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
        String sql = "INSERT INTO items (catalog_number, branch_id, storage_location, is_defect, expiration_date,sale_date) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstatement = conn.prepareStatement(sql)) {

            pstatement.setInt(1, dto.getCatalogNumber());
            pstatement.setInt(2, dto.getBranchId());
            pstatement.setString(3, dto.getStorageLocation());
            pstatement.setBoolean(4, dto.IsDefective());
            pstatement.setString(5, dto.getItemExpiringDate());
            pstatement.setObject(6, dto.getSale_date(), Types.DATE);
            pstatement.executeUpdate();
        }
    }

    @Override
    public void Update(ItemDTO dto) throws SQLException {
        String sql = "UPDATE items SET catalog_number = ?, branch_id = ?, storage_location = ?,section_in_store = ?, is_defect = ?, expiration_date = ? WHERE item_id = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstatement = conn.prepareStatement(sql)) {

            pstatement.setInt(1, dto.getCatalogNumber());
            pstatement.setInt(2, dto.getBranchId());
            pstatement.setString(3, dto.getStorageLocation());
            pstatement.setString(4, dto.getSectionInStore());
            pstatement.setBoolean(5, dto.IsDefective());

            pstatement.setString(6, dto.getItemExpiringDate());
            pstatement.setInt(7, dto.getItemId());

            int rowsAffected = pstatement.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("No item found with item_id = " + dto.getItemId());
            } else {
                System.out.println("The item was updated successfully.");
            }
        }
    }


    public void UpdateStorageLocation(ItemDTO dto) throws SQLException {
        String sql = "UPDATE items SET storage_location = ? WHERE item_id = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstatement = conn.prepareStatement(sql)) {



            pstatement.setString(1, dto.getStorageLocation());
            pstatement.setInt(2, dto.getItemId());

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




    public int GetId(ItemDTO item1) throws SQLException {
        String sql = "SELECT item_id FROM items " +
                "WHERE catalog_number = ? AND branch_id = ? AND storage_location = ? " +
                "AND is_defect = ? AND expiration_date = ? " +
                "ORDER BY item_id DESC LIMIT 1";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstatement = conn.prepareStatement(sql)) {

            pstatement.setInt(1, item1.getCatalogNumber());
            pstatement.setInt(2, item1.getBranchId());
            pstatement.setString(3, item1.getStorageLocation());
            pstatement.setBoolean(4, item1.IsDefective());
            pstatement.setString(5, item1.getItemExpiringDate());

            try (ResultSet rs = pstatement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("item_id");
                }
            }
        }

        return 0;
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


    @Override
    public List<ItemDTO> getItemsByProductId(int productId) {
        List<ItemDTO> items = new ArrayList<>();
        String sql = "SELECT * FROM Items WHERE catalog_number = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, productId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    ItemDTO item = new ItemDTO();
                    item.setItemId(rs.getInt("item_id"));
                    item.setCatalogNumber(rs.getInt("catalog_number"));
                    item.setBranchId(rs.getInt("branch_id"));
                    item.setLocation(rs.getString("storage_location"));
                    item.setSection_in_store(rs.getString("section_in_store"));
                    item.setIsDefective(rs.getBoolean("is_defect"));
                    item.setExpirationDate(rs.getString("expiration_date"));
                    Date saleDate = rs.getDate("sale_date");
                    if (saleDate != null)
                        item.setSaleDate(saleDate.toLocalDate());

                    items.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving items by product ID: " + e.getMessage());
        }

        return items;
    }

    @Override
    public List<ItemDTO> findDefectiveItems() {
        List<ItemDTO> items = new ArrayList<>();
        String sql = "SELECT * FROM Items WHERE is_defect = 1";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement statement = conn.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                ItemDTO item = new ItemDTO();
                item.setItemId(rs.getInt("item_id"));
                item.setCatalogNumber(rs.getInt("catalog_number"));
                item.setBranchId(rs.getInt("branch_id"));
                item.setLocation(rs.getString("storage_location"));
                item.setSection_in_store(rs.getString("section_in_store"));
                item.setIsDefective(rs.getBoolean("is_defect"));
                item.setExpirationDate(rs.getString("expiration_date"));
                Date saleDate = rs.getDate("sale_date");
                if (saleDate != null)
                    item.setSaleDate(saleDate.toLocalDate());

                items.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving defective items: " + e.getMessage());
        }

        return items;
    }
}
