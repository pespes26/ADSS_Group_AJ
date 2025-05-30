package Inventory.DAO;

import Inventory.DTO.ProductDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcProductDAO implements IProductDAO {
    private static final String DB_URL = "jdbc:sqlite:Inventory.db";

    static {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement statement = conn.createStatement()) {

            String createTableSql = "CREATE TABLE IF NOT EXISTS products (\n"
                    + " catalog_number INTEGER PRIMARY KEY,\n"
                    + " product_name TEXT NOT NULL,\n"
                    + " category TEXT,\n"
                    + " sub_category TEXT,\n"
                    + " size INTEGER,\n"
                    + " product_demand_level INTEGER,\n"
                    + " supply_time INTEGER,\n"
                    + " quantity_in_store INTEGER DEFAULT 0,\n"
                    + " quantity_in_warehouse INTEGER DEFAULT 0,\n"
                    + " minimum_quantity_for_alert INTEGER,\n"
                    + " supplier_name TEXT,\n"
                    + " cost_price_before_supplier_discount REAL,\n"
                    + " supplier_discount REAL DEFAULT 0.0,\n"
                    + " store_discount REAL DEFAULT 0.0\n"
                    + ");";

            statement.execute(createTableSql);
            System.out.println("The 'products' table was created (if it did not already exist).");

        } catch (SQLException e) {
            System.err.println("Error creating the 'products' table:");
            e.printStackTrace();
        }
    }

    public void Insert(ProductDTO dto) throws SQLException {
        String sql = "INSERT INTO products (catalog_number, product_name, category, sub_category, size, product_demand_level, supply_time, quantity_in_store, quantity_in_warehouse, minimum_quantity_for_alert, supplier_name, cost_price_before_supplier_discount, supplier_discount, store_discount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, dto.getCatalogNumber());
            preparedStatement.setString(2, dto.getProductName());
            preparedStatement.setString(3, dto.getCategory());
            preparedStatement.setString(4, dto.getSubCategory());
            preparedStatement.setInt(5, dto.getSize());
            preparedStatement.setInt(6, dto.getProductDemandLevel());
            preparedStatement.setInt(7, dto.getSupplyTime());
            preparedStatement.setInt(8, dto.getQuantityInStore());
            preparedStatement.setInt(9, dto.getQuantityInWarehouse());
            preparedStatement.setInt(10, dto.getMinimumQuantityForAlert());
            preparedStatement.setString(11, dto.getManufacturer()); // supplier_name
            preparedStatement.setDouble(12, dto.getCostPriceBeforeSupplierDiscount());
            preparedStatement.setDouble(13, dto.getSupplierDiscount());
            preparedStatement.setDouble(14, dto.getStoreDiscount());

            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void Update(ProductDTO dto) throws SQLException {
        String sql = "UPDATE Products SET product_name = ?, category = ?, sub_category = ?, size = ?, manufacturer = ?, price = ?, supplier_discount = ?, store_discount = ? WHERE catalog_number = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement prepared_statement = conn.prepareStatement(sql)) {

            prepared_statement.setString(1, dto.getProductName());
            prepared_statement.setString(2, dto.getCategory());
            prepared_statement.setString(3, dto.getSubCategory());
            prepared_statement.setString(4, dto.getSubSubCategory());
            prepared_statement.setString(5, dto.getManufacturer());
            prepared_statement.setDouble(6, dto.getCostPriceBeforeSupplierDiscount());
            prepared_statement.setDouble(7, dto.getSupplierDiscount());
            prepared_statement.setDouble(8, dto.getStoreDiscount());
            prepared_statement.setInt(9, dto.getCatalogNumber());

            int rowsAffected = prepared_statement.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("The product with catalog number = " + dto.getCatalogNumber() + " not found");
            } else {
                System.out.println("The product was updated successfully");
            }
        }
    }

    public void DeleteByCatalogNumber(int catalogNumber) throws SQLException {
        String sql = "DELETE FROM Products WHERE catalog_number = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement prepared_statement = conn.prepareStatement(sql)) {

            prepared_statement.setInt(1, catalogNumber);
            prepared_statement.executeUpdate();
        }
    }

    public ProductDTO GetProductByCatalogNumber(int catalogNumber) throws SQLException {
        String sql = "SELECT * FROM Products WHERE catalog_number = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement prepared_statement = conn.prepareStatement(sql)) {

            prepared_statement.setInt(1, catalogNumber);

            try (ResultSet rs = prepared_statement.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProductDTO(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        List<ProductDTO> products = new ArrayList<>();
        String sql = "SELECT * FROM Products";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                ProductDTO dto = mapResultSetToProductDTO(rs);
                products.add(dto);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving products from DB: " + e.getMessage());
        }

        return products;
    }

    private ProductDTO mapResultSetToProductDTO(ResultSet rs) throws SQLException {
        ProductDTO dto = new ProductDTO();
        dto.setCatalogNumber(rs.getInt("catalog_number"));
        dto.setProductName(rs.getString("product_name"));
        dto.setCategory(rs.getString("category"));
        dto.setSubCategory(rs.getString("sub_category"));
        dto.setSubSubCategory(rs.getString("size"));
        dto.setManufacturer(rs.getString("supplier_name"));
        dto.setCostPriceBeforeSupplierDiscount(rs.getDouble("cost_price_before_supplier_discount"));
        dto.setSupplierDiscount(rs.getDouble("supplier_discount"));
        dto.setStoreDiscount(rs.getDouble("store_discount"));
        return dto;
    }
}
