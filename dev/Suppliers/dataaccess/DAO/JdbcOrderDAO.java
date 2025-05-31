package Suppliers.dataaccess.DAO;

import Suppliers.DTO.OrderDTO;
import Suppliers.DTO.OrderItemDTO;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JdbcOrderDAO implements IOrderDAO {
    private static final String DB_URL = "jdbc:sqlite:suppliers.db";

    public void createTableIfNotExists() {
        String sqlOrders = "CREATE TABLE IF NOT EXISTS orders (\n" +
                " order_id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                " phone_number INTEGER NOT NULL,\n" +
                " order_date TEXT NOT NULL\n" +
                ");";

        String sqlItems = "CREATE TABLE IF NOT EXISTS order_items (\n" +
                " order_id INTEGER NOT NULL,\n" +
                " product_id INTEGER NOT NULL,\n" +
                " quantity INTEGER NOT NULL,\n" +
                " supplier_id INTEGER NOT NULL,\n" +
                " FOREIGN KEY(order_id) REFERENCES orders(order_id)\n" +
                ");";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlOrders);
            stmt.execute(sqlItems);
        } catch (SQLException e) {
            System.err.println("Error creating orders or order_items table:");
            e.printStackTrace();
        }
    }

    @Override
    public void insert(OrderDTO dto) throws SQLException {
        String insertOrderSql = "INSERT INTO orders (phone_number, order_date) VALUES (?, ?)";
        String insertItemSql = "INSERT INTO order_items (order_id, product_id, quantity, supplier_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement orderStmt = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement itemStmt = conn.prepareStatement(insertItemSql)) {

            conn.setAutoCommit(false);

            orderStmt.setLong(1, dto.getPhoneNumber());
            orderStmt.setString(2, dto.getOrderDate().toString());
            orderStmt.executeUpdate();

            ResultSet generatedKeys = orderStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                dto.setOrderID(generatedKeys.getInt(1));
            }

            for (OrderItemDTO item : dto.getItems()) {
                itemStmt.setInt(1, dto.getOrderID());
                itemStmt.setInt(2, item.getProductId());
                itemStmt.setInt(3, item.getQuantity());
                itemStmt.setInt(4, item.getSupplierId());
                itemStmt.addBatch();
            }

            itemStmt.executeBatch();
            conn.commit();
        }
    }

    @Override
    public void deleteById(int orderId) throws SQLException {
        String deleteItemsSql = "DELETE FROM order_items WHERE order_id = ?";
        String deleteOrderSql = "DELETE FROM orders WHERE order_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement itemStmt = conn.prepareStatement(deleteItemsSql);
             PreparedStatement orderStmt = conn.prepareStatement(deleteOrderSql)) {

            itemStmt.setInt(1, orderId);
            itemStmt.executeUpdate();

            orderStmt.setInt(1, orderId);
            orderStmt.executeUpdate();
        }
    }

    @Override
    public OrderDTO getById(int orderId) throws SQLException {
        String orderSql = "SELECT * FROM orders WHERE order_id = ?";
        String itemsSql = "SELECT product_id, quantity, supplier_id FROM order_items WHERE order_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement orderStmt = conn.prepareStatement(orderSql);
             PreparedStatement itemStmt = conn.prepareStatement(itemsSql)) {

            orderStmt.setInt(1, orderId);
            ResultSet orderRs = orderStmt.executeQuery();

            if (!orderRs.next()) return null;

            long phone = orderRs.getLong("phone_number");
            LocalDateTime date = LocalDateTime.parse(orderRs.getString("order_date"));

            List<OrderItemDTO> items = new ArrayList<>();
            itemStmt.setInt(1, orderId);
            ResultSet itemRs = itemStmt.executeQuery();
            while (itemRs.next()) {
                int productId = itemRs.getInt("product_id");
                int quantity = itemRs.getInt("quantity");
                int supplierId = itemRs.getInt("supplier_id");
                items.add(new OrderItemDTO(productId, quantity, supplierId));
            }

            OrderDTO dto = new OrderDTO(phone, date, items);
            dto.setOrderID(orderId);
            return dto;
        }
    }

    @Override
    public List<OrderDTO> getAll() throws SQLException {
        List<OrderDTO> orders = new ArrayList<>();
        String sql = "SELECT order_id FROM orders";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                orders.add(getById(rs.getInt("order_id")));
            }
        }

        return orders;
    }

    @Override
    public List<OrderDTO> getBySupplierId(int supplierId) throws SQLException {
        List<OrderDTO> allOrders = getAll();
        List<OrderDTO> filteredOrders = new ArrayList<>();

        for (OrderDTO order : allOrders) {
            boolean hasSupplier = order.getItems().stream().anyMatch(i -> i.getSupplierId() == supplierId);
            if (hasSupplier) filteredOrders.add(order);
        }

        return filteredOrders;
    }
}
