package Inventory.DAO;

import Inventory.DTO.OrderDTO;
import Inventory.DataBase.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcOrdersDAO implements IOrdersDAO {

    static {
        try (Connection conn = DatabaseConnector.connect();
             Statement statement = conn.createStatement()) {

            String createTableSql = """
                    CREATE TABLE IF NOT EXISTS orders (
                        order_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        product_catalog_number INTEGER NOT NULL,
                        quantity INTEGER NOT NULL,
                        supplier_discount REAL DEFAULT 0.0,
                        supply_time INTEGER DEFAULT 0,
                        order_date TEXT DEFAULT (datetime('now'))
                    );
                    """;

            statement.execute(createTableSql);
            System.out.println("Orders table created (if it did not already exist).");

        } catch (SQLException e) {
            System.err.println("Error creating orders table:");
            e.printStackTrace();
        }
    }

    @Override
    public void insertOrder(OrderDTO dto) throws SQLException {
        String sql = "INSERT INTO orders (product_catalog_number, quantity) VALUES (?, ?)";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement prepared_statement = conn.prepareStatement(sql)) {

            prepared_statement.setInt(1, dto.getProductCatalogNumber());
            prepared_statement.setInt(2, dto.getQuantity());

            prepared_statement.executeUpdate();
        }
    }

    @Override
    public void updateOrder(OrderDTO dto) throws SQLException {
        String sql = "UPDATE orders SET product_catalog_number = ?, quantity = ? WHERE order_id = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement prepared_statement = conn.prepareStatement(sql)) {

            prepared_statement.setInt(1, dto.getProductCatalogNumber());
            prepared_statement.setInt(2, dto.getQuantity());
            prepared_statement.setInt(3, dto.getOrderId());

            int rowsAffected = prepared_statement.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("No order found with order_id = " + dto.getOrderId());
            } else {
                System.out.println("Order updated successfully.");
            }
        }
    }

    @Override
    public void deleteOrderById(int orderId) throws SQLException {
        String sql = "DELETE FROM orders WHERE order_id = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement prepared_statement = conn.prepareStatement(sql)) {

            prepared_statement.setInt(1, orderId);
            prepared_statement.executeUpdate();
        }
    }

    @Override
    public OrderDTO getOrderById(int orderId) throws SQLException {
        String sql = "SELECT * FROM orders WHERE order_id = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement prepared_statement = conn.prepareStatement(sql)) {

            prepared_statement.setInt(1, orderId);

            try (ResultSet rs = prepared_statement.executeQuery()) {
                if (rs.next()) {
                    OrderDTO dto = new OrderDTO();
                    dto.setOrderId(rs.getInt("order_id"));
                    dto.setProductCatalogNumber(rs.getInt("product_catalog_number"));
                    dto.setQuantity(rs.getInt("quantity"));
                    dto.setOrderDate(rs.getString("order_date"));
                    return dto;
                }
            }
        }

        return null;
    }

    @Override
    public List<OrderDTO> getAllOrders() throws SQLException {
        List<OrderDTO> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders";

        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                OrderDTO dto = new OrderDTO();
                dto.setOrderId(rs.getInt("order_id"));
                dto.setProductCatalogNumber(rs.getInt("product_catalog_number"));
                dto.setQuantity(rs.getInt("quantity"));
                dto.setSupplierDiscount(rs.getDouble("supplier_discount"));
                dto.setSupplyTime(rs.getInt("supply_time"));
                dto.setOrderDate(rs.getString("order_date"));

                orders.add(dto);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving orders from DB: " + e.getMessage());
            throw e;
        }

        return orders;
    }


}