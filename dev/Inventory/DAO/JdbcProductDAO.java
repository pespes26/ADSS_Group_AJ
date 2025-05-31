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
                    + " size_item INTEGER,\n"
                    + " product_demand_level INTEGER,\n"
                    + " supply_time INTEGER,\n"
                    + " quantity_in_store INTEGER DEFAULT 0,\n"
                    + " quantity_in_warehouse INTEGER DEFAULT 0,\n"
                    + " minimum_quantity_for_alert INTEGER,\n"
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
        String sql = "INSERT INTO Products (" +
                "catalog_number, product_name, category, sub_category, size_item, " +
                "product_demand_level, supply_time, quantity_in_store, quantity_in_warehouse, " +
                "minimum_quantity_for_alert, cost_price_before_supplier_discount, " +
                "supplier_discount, store_discount) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Inventory.db");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, dto.getCatalogNumber());
            pstmt.setString(2, dto.getProductName());
            pstmt.setString(3, dto.getCategory());
            pstmt.setString(4, dto.getSubCategory());
            pstmt.setInt(5, dto.getSize());
            pstmt.setInt(6, dto.getProductDemandLevel());
            pstmt.setInt(7, dto.getSupplyTime());
            pstmt.setInt(8, dto.getQuantityInStore());
            pstmt.setInt(9, dto.getQuantityInWarehouse());
            pstmt.setInt(10, dto.getMinimumQuantityForAlert());
            pstmt.setDouble(11, dto.getCostPriceBeforeSupplierDiscount());
            pstmt.setDouble(12, dto.getSupplierDiscount());
            pstmt.setDouble(13, dto.getStoreDiscount());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void Update(ProductDTO dto) throws SQLException {
        String sql = "UPDATE Products SET product_name = ?, category = ?, sub_category = ?, size_item = ?, cost_price_before_supplier_discount = ?, supplier_discount = ?, store_discount = ?, supply_time = ?, product_demand_level=?  WHERE catalog_number = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement prepared_statement = conn.prepareStatement(sql)) {

            prepared_statement.setString(1, dto.getProductName());
            prepared_statement.setString(2, dto.getCategory());
            prepared_statement.setString(3, dto.getSubCategory());
            prepared_statement.setString(4, dto.getSubSubCategory());

            prepared_statement.setDouble(5, dto.getCostPriceBeforeSupplierDiscount());
            prepared_statement.setDouble(6, dto.getSupplierDiscount());
            prepared_statement.setDouble(7, dto.getStoreDiscount());
            prepared_statement.setInt(8, dto.getSupplyTime());
            prepared_statement.setInt(9, dto.getProductDemandLevel());
            prepared_statement.setInt(10, dto.getCatalogNumber());

            int rowsAffected = prepared_statement.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("The product with catalog number = " + dto.getCatalogNumber() + " not found");
            } else {
                System.out.println("The product was updated successfully");
            }
        }
    }

    public void UpdateProductSupplyTime(ProductDTO dto) throws SQLException {
        String sql = "UPDATE Products SET supply_time = ?  WHERE catalog_number = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement prepared_statement = conn.prepareStatement(sql)) {

            prepared_statement.setInt(1, dto.getSupplyTime());
            prepared_statement.setInt(2, dto.getCatalogNumber());


            int rowsAffected = prepared_statement.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("The product with catalog number = " + dto.getCatalogNumber() + " not found");
            } else {
                System.out.println("The product was updated successfully");
            }
        }
    }

    public void UpdateDemand(ProductDTO dto) throws SQLException {
        String sql = "UPDATE Products SET product_demand_level= ?  WHERE catalog_number = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement prepared_statement = conn.prepareStatement(sql)) {

            prepared_statement.setInt(1, dto.getProductDemandLevel());
            prepared_statement.setInt(2, dto.getCatalogNumber());


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
        dto.setSubSubCategory(rs.getString("size_item"));
        dto.setCostPriceBeforeSupplierDiscount(rs.getDouble("cost_price_before_supplier_discount"));
        dto.setSupplierDiscount(rs.getDouble("supplier_discount"));
        dto.setStoreDiscount(rs.getDouble("store_discount"));
        return dto;
    }
}
