package Inventory.DAO;

import Inventory.DTO.OrderOnTheWayDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcOrderOnTheWayDAO implements IOrderOnTheWayDAO {
    private static final String DB_URL = "jdbc:sqlite:inventory.db";

    static {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            String createTable = """
                CREATE TABLE IF NOT EXISTS orders_on_the_way (
                    order_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    product_catalog_number INTEGER NOT NULL,
                    quantity INTEGER NOT NULL,
                    supplier_id INTEGER NOT NULL,
                    branch_id INTEGER NOT NULL,
                    expected_delivery_date TEXT NOT NULL,
                    order_creation_date TEXT DEFAULT (datetime('now')),
                    agreement_id INTEGER NOT NULL,
                    is_periodic INTEGER DEFAULT 1
                );
            """;
            stmt.execute(createTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insertOrder(OrderOnTheWayDTO dto) throws SQLException {
        String sql = "INSERT INTO orders_on_the_way (product_catalog_number, quantity, supplier_id, branch_id, expected_delivery_date, order_creation_date, agreement_id, is_periodic) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dto.getProductCatalogNumber());
            ps.setInt(2, dto.getQuantity());
            ps.setInt(3, dto.getSupplierId());
            ps.setInt(4, dto.getBranchId());
            ps.setString(5, dto.getExpectedDeliveryDate());
            ps.setString(6, dto.getOrderCreationDate());
            ps.setInt(7, dto.getAgreementId());
            ps.setInt(8, dto.isPeriodic() ? 1 : 0);
            ps.executeUpdate();
        }
    }

    @Override
    public void updateOrder(OrderOnTheWayDTO dto) throws SQLException {
        String sql = "UPDATE orders_on_the_way SET product_catalog_number=?, quantity=?, supplier_id=?, branch_id=?, expected_delivery_date=?, order_creation_date=?, agreement_id=?, is_periodic=? WHERE order_id=?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dto.getProductCatalogNumber());
            ps.setInt(2, dto.getQuantity());
            ps.setInt(3, dto.getSupplierId());
            ps.setInt(4, dto.getBranchId());
            ps.setString(5, dto.getExpectedDeliveryDate());
            ps.setString(6, dto.getOrderCreationDate());
            ps.setInt(7, dto.getAgreementId());
            ps.setInt(8, dto.isPeriodic() ? 1 : 0);
            ps.setInt(9, dto.getOrderId());
            ps.executeUpdate();
        }
    }

    @Override
    public void deleteOrderById(int id) throws SQLException {
        String sql = "DELETE FROM orders_on_the_way WHERE order_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public OrderOnTheWayDTO getOrderById(int id) throws SQLException {
        String sql = "SELECT * FROM orders_on_the_way WHERE order_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new OrderOnTheWayDTO(
                        rs.getInt("order_id"),
                        rs.getInt("product_catalog_number"),
                        rs.getInt("quantity"),
                        rs.getInt("supplier_id"),
                        rs.getInt("branch_id"),
                        rs.getString("expected_delivery_date"),
                        rs.getString("order_creation_date"),
                        rs.getInt("agreement_id"),
                        rs.getInt("is_periodic") == 1
                );
            }
        }
        return null;
    }

    @Override
    public List<OrderOnTheWayDTO> getAllOrders() throws SQLException {
        List<OrderOnTheWayDTO> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders_on_the_way";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                orders.add(new OrderOnTheWayDTO(
                        rs.getInt("order_id"),
                        rs.getInt("product_catalog_number"),
                        rs.getInt("quantity"),
                        rs.getInt("supplier_id"),
                        rs.getInt("branch_id"),
                        rs.getString("expected_delivery_date"),
                        rs.getString("order_creation_date"),
                        rs.getInt("agreement_id"),
                        rs.getInt("is_periodic") == 1
                ));
            }
        }
        return orders;
    }
}