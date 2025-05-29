package Inventory.DAO;
import Inventory.DTO.ItemDTO;

import java.sql.*;
public class JdbcItemDAO implements IItemsDAO {
    private static final String DB_URL = "jdbc:sqlite:Inventory.db";

    static {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            String createTableSql = "CREATE TABLE IF NOT EXISTS Items (\n"
                    + " item_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                    + " catalog_number INTEGER NOT NULL,\n"
                    + " branch_id INTEGER NOT NULL,\n"
                    + " location TEXT,\n"
                    + "is_defect BOOLEAN DEFAULT 0,"
                    + "expiration_date TEXT,\n"
                    + " FOREIGN KEY (catalog_number) REFERENCES Products(catalog_number)\n"
                    + ");";

            stmt.execute(createTableSql);
            System.out.println(" The 'Items' table was created (if it did not already exist).");

        } catch (SQLException e) {
            System.err.println("Error creating the 'Items' table:");
            e.printStackTrace();
        }
    }

    public void Insert(ItemDTO dto) throws SQLException{
        String sql = "INSERT INTO items (catalog_number, branch_id,location,is_defect,expiration_date) VALUES (?,?,?,?,?)";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Inventory.db")) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, dto.getCatalogNumber());
                pstmt.setInt(2, dto.getBranchId());
                pstmt.setString(3, dto.getLocation());
                pstmt.setBoolean(4, dto.IsDefective());
                pstmt.setString(5, dto.getExpirationdate());
                pstmt.executeUpdate();
            }
        }
    }
    @Override
    public void Update(ItemDTO dto) throws SQLException {
        String sql = "UPDATE items SET catalog_number = ?,branch_id = ?, location = ?, is_defect = ? , expiration_date = ? WHERE item_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, dto.getCatalogNumber());
            pstmt.setInt(2, dto.getBranchId());
            pstmt.setString(3, dto.getLocation());
            pstmt.setBoolean(4, dto.IsDefective());
            pstmt.setString(5, dto.getExpirationdate());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println(" No Item found with Item_id = " + dto.getItemId());
            } else {
                System.out.println(" The Item was updated successfully.");
            }
        }
    }
    public void DeleteByItemId(int Id)throws SQLException{
        String sql = "DELETE FROM items WHERE order_id = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Inventory.db");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Id);
            pstmt.executeUpdate();
        }
    }

    public ItemDTO GetById(int id) throws SQLException{
        String sql = "SELECT * FROM items WHERE item_id = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Inventory.db");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ItemDTO dto = new ItemDTO();

                    dto.setItemId(rs.getInt("item_id"));
                    dto.setCatalogNumber(rs.getInt("catalog_number"));
                    dto.setBranchId(rs.getInt("branch_id"));
                    dto.setLocation(rs.getString("location"));
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
        String sql = "UPDATE Items SET is_defect = 1 WHERE item_id = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Inventory.db");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, itemId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                System.out.println("⚠️ No item found with item_id = " + itemId);
            } else {
                System.out.println("✅ Item with item_id = " + itemId + " marked as defective.");
            }
        }
    }




}
