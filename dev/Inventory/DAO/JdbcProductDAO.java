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
                    + " supplier_name TEXT,\n"
                    + " product_size INTEGER,\n"
                    + " product_demand_level INTEGER,\n"
                    + " supply_days_in_week TEXT,\n"
                    + " supply_time INTEGER,\n"
                    + " quantity_in_store INTEGER DEFAULT 0,\n"
                    + " quantity_in_warehouse INTEGER DEFAULT 0,\n"
                    + " minimum_quantity_for_alert INTEGER,\n"
                    + " cost_price_before_supplier_discount REAL,\n"
                    + " supplier_discount REAL DEFAULT 0.0,\n"
                    + " store_discount REAL DEFAULT 0.0\n"
                    + ");";

            statement.execute(createTableSql);
            System.out.println("✅ The 'products' table was created or already exists.");

        } catch (SQLException e) {
            System.err.println("❌ Error creating the 'products' table:");
            e.printStackTrace();
        }
    }

    public void Insert(ProductDTO dto) throws SQLException {
        String sql = "INSERT INTO Products (" +
                "catalog_number, product_name, category, sub_category, supplier_name, product_size, " +
                "product_demand_level, supply_days_in_week, supply_time, quantity_in_store, quantity_in_warehouse, " +
                "minimum_quantity_for_alert, cost_price_before_supplier_discount, " +
                "supplier_discount, store_discount) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, dto.getCatalogNumber());
            pstmt.setString(2, dto.getProductName());
            pstmt.setString(3, dto.getCategory());
            pstmt.setString(4, dto.getSubCategory());
            pstmt.setString(5, dto.getSupplierName());
            pstmt.setInt(6, dto.getSize());
            pstmt.setInt(7, dto.getProductDemandLevel());
            pstmt.setString(8, dto.getSupplyDaysInWeek());
            pstmt.setInt(9, dto.getSupplyTime());
            pstmt.setInt(10, dto.getQuantityInStore());
            pstmt.setInt(11, dto.getQuantityInWarehouse());
            pstmt.setInt(12, dto.getMinimumQuantityForAlert());
            pstmt.setDouble(13, dto.getCostPriceBeforeSupplierDiscount());
            pstmt.setDouble(14, dto.getSupplierDiscount());
            pstmt.setDouble(15, dto.getStoreDiscount());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void Update(ProductDTO dto) throws SQLException {
        String sql = "UPDATE Products SET " +
                "product_name = ?, " +
                "category = ?, " +
                "sub_category = ?, " +
                "supplier_name = ?, " +
                "product_size = ?, " +
                "cost_price_before_supplier_discount = ?, " +
                "supplier_discount = ?, " +
                "store_discount = ?, " +
                "supply_days_in_week = ?, " +
                "supply_time = ?, " +
                "product_demand_level = ?, " +
                "quantity_in_store = ?, " +
                "quantity_in_warehouse = ?, " +
                "minimum_quantity_for_alert = ? " +
                "WHERE catalog_number = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dto.getProductName());
            ps.setString(2, dto.getCategory());
            ps.setString(3, dto.getSubCategory());
            ps.setString(4, dto.getSupplierName());
            ps.setInt(5, dto.getSize());
            ps.setDouble(6, dto.getCostPriceBeforeSupplierDiscount());
            ps.setDouble(7, dto.getSupplierDiscount());
            ps.setDouble(8, dto.getStoreDiscount());
            ps.setString(9, dto.getSupplyDaysInWeek());
            ps.setInt(10, dto.getSupplyTime());
            ps.setInt(11, dto.getProductDemandLevel());
            ps.setInt(12, dto.getQuantityInStore());
            ps.setInt(13, dto.getQuantityInWarehouse());
            ps.setInt(14, dto.getMinimumQuantityForAlert());
            ps.setInt(15, dto.getCatalogNumber());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("The product with catalog number = " + dto.getCatalogNumber() + " not found");
            } else {
                System.out.println("The product was updated successfully");
            }
        }
    }

    public void UpdateProductSupplyTime(ProductDTO dto) throws SQLException {
        String sql = "UPDATE Products SET supply_time = ? WHERE catalog_number = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, dto.getSupplyTime());
            ps.setInt(2, dto.getCatalogNumber());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("The product with catalog number = " + dto.getCatalogNumber() + " not found");
            } else {
                System.out.println("Supply time updated successfully");
            }
        }
    }

    public void UpdateDemand(ProductDTO dto) throws SQLException {
        String sql = "UPDATE Products SET product_demand_level = ? WHERE catalog_number = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, dto.getProductDemandLevel());
            ps.setInt(2, dto.getCatalogNumber());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("The product with catalog number = " + dto.getCatalogNumber() + " not found");
            } else {
                System.out.println("Demand level updated successfully");
            }
        }
    }

    public void DeleteByCatalogNumber(int catalogNumber) throws SQLException {
        String sql = "DELETE FROM Products WHERE catalog_number = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, catalogNumber);
            ps.executeUpdate();
        }
    }

    public ProductDTO GetProductByCatalogNumber(int catalogNumber) throws SQLException {
        String sql = "SELECT * FROM Products WHERE catalog_number = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, catalogNumber);

            try (ResultSet rs = ps.executeQuery()) {
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
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                products.add(mapResultSetToProductDTO(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error retrieving products from DB: " + e.getMessage());
        }

        return products;
    }

    private ProductDTO mapResultSetToProductDTO(ResultSet rs) throws SQLException {
        ProductDTO dto = new ProductDTO();
        dto.setCatalogNumber(rs.getInt("catalog_number"));
        dto.setProductName(rs.getString("product_name"));
        dto.setCategory(rs.getString("category"));
        dto.setSubCategory(rs.getString("sub_category"));
        dto.setSupplierName(rs.getString("supplier_name"));
        dto.setSize(rs.getInt("product_size"));
        dto.setProductDemandLevel(rs.getInt("product_demand_level"));
        dto.setSupplyDaysInWeek(rs.getString("supply_days_in_week")); // auto-calculates supplyTime
        dto.setQuantityInStore(rs.getInt("quantity_in_store"));
        dto.setQuantityInWarehouse(rs.getInt("quantity_in_warehouse"));
        dto.setMinimumQuantityForAlert(rs.getInt("minimum_quantity_for_alert"));
        dto.setCostPriceBeforeSupplierDiscount(rs.getDouble("cost_price_before_supplier_discount"));
        dto.setSupplierDiscount(rs.getDouble("supplier_discount"));
        dto.setStoreDiscount(rs.getDouble("store_discount"));
        return dto;
    }
}
