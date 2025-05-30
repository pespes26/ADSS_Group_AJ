package Inventory.DAO;
import Inventory.DTO.OrderOnTheWayDTO;
import java.sql.*;
public class JdbcOrdersOnTheWayDAO implements IOrdersOnTheWayDAO {
    private static final String DB_URL = "jdbc:sqlite:Inventory.db";
    static {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement statement = conn.createStatement()) {

            String createTableSql = """
                    CREATE TABLE IF NOT EXISTS orders_on_the_way (
                     order_id INTEGER PRIMARY KEY AUTOINCREMENT,
                     product_catalog_number INTEGER NOT NULL,
                     quantity INTEGER NOT NULL,
                     price REAL NOT NULL,
                    discount REAL NOT NULL,
                     order_date TEXT DEFAULT (datetime('now'))
                    );""";

            statement.execute(createTableSql);
            System.out.println("OrdersOnTheWay table created (if it didn't already exist).");

        } catch (SQLException e) {
            System.err.println("Error creating OrdersOnTheWay table:");
            e.printStackTrace();
        }
    }

    public void Insert(OrderOnTheWayDTO dto) throws SQLException{
        String sql = "INSERT INTO orders_on_the_way (product_Catalog_Number, quantity,price,discount) VALUES (?,?,?,?)";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Inventory.db")) {
            try (PreparedStatement prepared_statement = conn.prepareStatement(sql)) {
                prepared_statement.setInt(1, dto.getProductCatalogNumber());
                prepared_statement.setInt(2, dto.getQuantity());
                prepared_statement.setDouble(3, dto.getPrice());
                prepared_statement.setDouble(4, dto.getDiscount());
                prepared_statement.executeUpdate();
            }


        }

    }


    @Override
    public void Update(OrderOnTheWayDTO dto) throws SQLException {
        String sql = "UPDATE orders_on_the_way SET product_catalog_number = ?, quantity = ?, price = ? ,discount = ? WHERE order_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement prepared_statement = conn.prepareStatement(sql)) {

            prepared_statement.setInt(1, dto.getProductCatalogNumber());
            prepared_statement.setInt(2, dto.getQuantity());
            prepared_statement.setDouble(3, dto.getPrice());
            prepared_statement.setDouble(4, dto.getDiscount());


            int rowsAffected = prepared_statement.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println(" No order found with order_id = " + dto.getOrderId());
            } else {
                System.out.println("The order was updated successfully.");
            }
        }
    }

    public void DeleteByOrderId(int Id)throws SQLException{
        String sql = "DELETE FROM orders_on_the_way WHERE order_id = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Inventory.db");
             PreparedStatement prepared_statement = conn.prepareStatement(sql)) {

            prepared_statement.setInt(1, Id);
            prepared_statement.executeUpdate();
        }
    }

    public OrderOnTheWayDTO GetById(int id) throws SQLException{
        String sql = "SELECT * FROM orders WHERE order_id = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Inventory.db");
             PreparedStatement prepared_statement = conn.prepareStatement(sql)) {

            prepared_statement.setInt(1, id);

            try (ResultSet rs = prepared_statement.executeQuery()) {
                if (rs.next()) {
                    OrderOnTheWayDTO dto = new OrderOnTheWayDTO();
                    dto.setOrderId(rs.getInt("order_id"));
                    dto.setProductCatalogNumber(rs.getInt("product_catalog_number"));
                    dto.setQuantity(rs.getInt("quantity"));
                    dto.setPrice(rs.getDouble("cost_price_before_supplier_discount"));
                    dto.setDiscount(rs.getDouble("supplier_discount"));
                    dto.setOrderDate(rs.getString("order_date"));
                    return dto;
                }
            }
        }

        return null;


    }







}
