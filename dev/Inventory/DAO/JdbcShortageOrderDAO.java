package Inventory.DAO;

import Inventory.DTO.ShortageOrderDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcShortageOrderDAO implements IShortageOrderDAO {

    private static final String DB_URL = "jdbc:sqlite:Inventory.db";

    static {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement statement = conn.createStatement()) {

            String createTableSql = """
                CREATE TABLE IF NOT EXISTS shortage_orders (
                    order_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    product_catalog_number INTEGER NOT NULL,
                    quantity INTEGER NOT NULL,
                    cost_price_before_supplier_discount REAL NOT NULL,
                    supplier_discount REAL NOT NULL,
                    order_date TEXT DEFAULT (datetime('now')),
                    branch_id INTEGER NOT NULL,
                    days_in_the_week TEXT DEFAULT '',
                    supplier_id INTEGER NOT NULL,
                    supplier_name TEXT DEFAULT ''
                );
            """;

            statement.execute(createTableSql);
            System.out.println("✅ shortage_orders table created (if it didn't already exist).");

        } catch (SQLException e) {
            System.err.println("❌ Error creating shortage_orders table:");
            e.printStackTrace();
        }
    }

    @Override
    public void insertShortageOrder(ShortageOrderDTO dto) throws SQLException {
        String sql = """
            INSERT INTO shortage_orders (
                product_catalog_number,
                quantity,
                cost_price_before_supplier_discount,
                supplier_discount,
                order_date,
                branch_id,
                days_in_the_week,
                supplier_id,
                supplier_name
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, dto.getProductCatalogNumber());
            preparedStatement.setInt(2, dto.getQuantity());
            preparedStatement.setDouble(3, dto.getCostPriceBeforeSupplierDiscount());
            preparedStatement.setDouble(4, dto.getSupplierDiscount());
            preparedStatement.setString(5, dto.getOrderDate());
            preparedStatement.setInt(6, dto.getBranchId());
            preparedStatement.setString(7, dto.getDaysInTheWeek());
            preparedStatement.setInt(8, dto.getSupplierId());
            preparedStatement.setString(9, dto.getSupplierName());

            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void updateShortageOrder(ShortageOrderDTO dto) throws SQLException {
        String sql = """
            UPDATE shortage_orders
            SET product_catalog_number = ?, 
                quantity = ?, 
                cost_price_before_supplier_discount = ?,
                supplier_discount = ?, 
                order_date = ?, 
                branch_id = ?, 
                days_in_the_week = ?, 
                supplier_id = ?,
                supplier_name = ?
            WHERE order_id = ?
        """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, dto.getProductCatalogNumber());
            preparedStatement.setInt(2, dto.getQuantity());
            preparedStatement.setDouble(3, dto.getCostPriceBeforeSupplierDiscount());
            preparedStatement.setDouble(4, dto.getSupplierDiscount());
            preparedStatement.setString(5, dto.getOrderDate());
            preparedStatement.setInt(6, dto.getBranchId());
            preparedStatement.setString(7, dto.getDaysInTheWeek());
            preparedStatement.setInt(8, dto.getSupplierId());
            preparedStatement.setString(9, dto.getSupplierName());
            preparedStatement.setInt(10, dto.getOrderId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("⚠️ No shortage order found with order_id = " + dto.getOrderId());
            } else {
                System.out.println("✅ The shortage order was updated successfully.");
            }
        }
    }

    @Override
    public void deleteShortageOrderById(int id) throws SQLException {
        String sql = "DELETE FROM shortage_orders WHERE order_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public ShortageOrderDTO getShortageOrderById(int id) throws SQLException {
        String sql = "SELECT * FROM shortage_orders WHERE order_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return new ShortageOrderDTO(
                            rs.getInt("order_id"),
                            rs.getInt("product_catalog_number"),
                            rs.getInt("quantity"),
                            rs.getDouble("cost_price_before_supplier_discount"),
                            rs.getDouble("supplier_discount"),
                            rs.getString("order_date"),
                            rs.getInt("branch_id"),
                            rs.getString("days_in_the_week"),
                            rs.getInt("supplier_id"),
                            rs.getString("supplier_name")
                    );
                }
            }
        }

        return null;
    }

    @Override
    public List<ShortageOrderDTO> getAllShortageOrders() throws SQLException {
        List<ShortageOrderDTO> orders = new ArrayList<>();
        String sql = "SELECT * FROM shortage_orders";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement preparedStatement = conn.prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery()) {

            while (rs.next()) {
                ShortageOrderDTO dto = new ShortageOrderDTO(
                        rs.getInt("order_id"),
                        rs.getInt("product_catalog_number"),
                        rs.getInt("quantity"),
                        rs.getDouble("cost_price_before_supplier_discount"),
                        rs.getDouble("supplier_discount"),
                        rs.getString("order_date"),
                        rs.getInt("branch_id"),
                        rs.getString("days_in_the_week"),
                        rs.getInt("supplier_id"),
                        rs.getString("supplier_name")
                );
                orders.add(dto);
            }
        }

        return orders;
    }

    public List<ShortageOrderDTO> getAllShortageOrdersByBranch(int branchId) throws SQLException {
        List<ShortageOrderDTO> orders = new ArrayList<>();
        String sql = "SELECT * FROM shortage_orders WHERE branch_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, branchId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    ShortageOrderDTO dto = new ShortageOrderDTO(
                            rs.getInt("order_id"),
                            rs.getInt("product_catalog_number"),
                            rs.getInt("quantity"),
                            rs.getDouble("cost_price_before_supplier_discount"),
                            rs.getDouble("supplier_discount"),
                            rs.getString("order_date"),
                            rs.getInt("branch_id"),
                            rs.getString("days_in_the_week"),
                            rs.getInt("supplier_id"),
                            rs.getString("supplier_name")
                    );
                    orders.add(dto);
                }
            }
        }

        return orders;
    }

    public String getLastOrderDateForProduct(int catalogNumber, int branchId) throws SQLException {
        String sql = """
            SELECT order_date 
            FROM shortage_orders 
            WHERE product_catalog_number = ? AND branch_id = ?
            ORDER BY datetime(order_date) DESC 
            LIMIT 1
        """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, catalogNumber);
            preparedStatement.setInt(2, branchId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("order_date");
                }
            }
        }

        return null; // לא נמצאה הזמנה קודמת
    }
}
