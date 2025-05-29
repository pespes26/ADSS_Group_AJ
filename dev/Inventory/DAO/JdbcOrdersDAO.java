package Inventory.DAO;

import Inventory.DTO.OrderDTO;
import java.sql.*;

public class JdbcOrdersDAO implements IOrdersDAO {
    private static final String DB_URL = "jdbc:sqlite:Inventory.db";

    static {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            String createTableSql = "CREATE TABLE IF NOT EXISTS orders (\n"
                    + " order_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                    + " product_Catalog_Number INTEGER NOT NULL,\n"
                    + " quantity INTEGER NOT NULL,\n"
                    + " order_date TEXT DEFAULT (datetime('now'))\n"
                    + ");";

            stmt.execute(createTableSql);
            System.out.println("✅ Orders table created (if it did not already exist).");

        } catch (SQLException e) {
            System.err.println("❌ Error creating Orders table:");
            e.printStackTrace();
        }
    }

    @Override
    public void insert(OrderDTO dto) throws SQLException {
        String sql = "INSERT INTO orders (product_Catalog_Number, quantity) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, dto.getProductCatalogNumber());
            pstmt.setInt(2, dto.getQuantity());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void update(OrderDTO dto) throws SQLException {
        String sql = "UPDATE orders SET product_Catalog_Number = ?, quantity = ? WHERE order_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, dto.getProductCatalogNumber());
            pstmt.setInt(2, dto.getQuantity());
            pstmt.setInt(3, dto.getOrderId());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("⚠️ No order found with order_id = " + dto.getOrderId());
            } else {
                System.out.println("✅ Order updated successfully.");
            }
        }
    }

    @Override
    public void deletebyorderid(int orderId) throws SQLException {
        String sql = "DELETE FROM orders WHERE order_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public OrderDTO getbyid(int orderId) throws SQLException {
        String sql = "SELECT * FROM orders WHERE order_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    OrderDTO dto = new OrderDTO();
                    dto.setOrderId(rs.getInt("order_id"));
                    dto.setProductCatalogNumber(rs.getInt("product_Catalog_Number"));
                    dto.setQuantity(rs.getInt("quantity"));
                    dto.setOrderDate(rs.getString("order_date"));
                    return dto;
                }
            }
        }

        return null;
    }
}
