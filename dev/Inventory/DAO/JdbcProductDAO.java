package Inventory.DAO;
import Inventory.DTO.ProductDTO;
import java.sql.*;
public class JdbcProductDAO implements IProductDAO {
    private static final String DB_URL = "jdbc:sqlite:Inventory.db";
    static {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            String createTableSql = "CREATE TABLE IF NOT EXISTS Products (\n"
                    + " catalog_number INTEGER PRIMARY KEY,\n"
                    + " product_name TEXT NOT NULL,\n"
                    + " category TEXT NOT NULL,\n"
                    + " sub_category TEXT,"
                    + "sub_sub_category TEXT,"
                    + " manufacturer TEXT NOT NULL,\n"
                    + "price REAL NOT NULL,\n"
                    + "supplier_discount REAL DEFAULT 0.0,\n"
                    + "store_discount REAL DEFAULT 0.0,\n"
                    + ");";

            stmt.execute(createTableSql);
            System.out.println("The 'Products' table was created (if it did not already exist).");

        } catch (SQLException e) {
            System.err.println("Error creating the 'Products' table:");
            e.printStackTrace();
        }
    }

    public void Insert(ProductDTO dto) throws SQLException{
        String sql = "INSERT INTO Products (catalog_number,product_name,category,sub_category,sub_sub_category,manufacturer,price,supplier_discount,store_discount) VALUES (?,?,?,?,?,?,?,?,?)";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Inventory.db")) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, dto.getCatalogNumber());
                pstmt.setString(2, dto.getProductName());
                pstmt.setString(3, dto.getCategory());
                pstmt.setString(4, dto.getSubCategory());
                pstmt.setString(5, dto.getSubSubCategory());
                pstmt.setString(6, dto.getManufacturer());
                pstmt.setDouble(7, dto.getPrice());
                pstmt.setDouble(8, dto.getSupplierDiscount());
                pstmt.setDouble(9, dto.getStoreDiscount());


                pstmt.executeUpdate();
            }


        }

    }


    @Override
    public void Update(ProductDTO dto) throws SQLException {
        String sql = "UPDATE Products SET  product_name = ?, category = ? ,sub_category = ? ,sub_sub_category = ? ,manufacturer = ? , price = ? , supplier_discount = ? , store_discount = ?   WHERE Catalog_Number = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {


            pstmt.setString(1, dto.getProductName());
            pstmt.setString(2, dto.getCategory());
            pstmt.setString(3, dto.getSubCategory());
            pstmt.setString(4, dto.getSubSubCategory());
            pstmt.setString(5, dto.getManufacturer());
            pstmt.setDouble(6, dto.getPrice());
            pstmt.setDouble(7, dto.getSupplierDiscount());
            pstmt.setDouble(8, dto.getStoreDiscount());
            pstmt.setInt(9, dto.getCatalogNumber());


            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("⚠️ The product with the catalog number = " + dto.getCatalogNumber() + "not found");
            } else {
                System.out.println("✅ The product update successfully");
            }
        }
    }

    public void DeleteByCatalogNumber(int catalog_Number)throws SQLException{
        String sql = "DELETE FROM Products WHERE Catalog_Number = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Inventory.db");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, catalog_Number);
            pstmt.executeUpdate();
        }
    }

    public ProductDTO GetProductByCatalogNumber(int catalog_number) throws SQLException{
        String sql = "SELECT * FROM Products WHERE Catalog_Number = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Inventory.db");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, catalog_number);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ProductDTO dto = new ProductDTO();
                    dto.setCatalogNumber(rs.getInt("Catalog_Number"));
                    dto.setProductName(rs.getString("product_name"));
                    dto.setCategory(rs.getString("category"));
                    dto.setSubCategory(rs.getString("sub_category"));
                    dto.setSubSubCategory(rs.getString("sub_sub_category"));
                    dto.setManufacturer(rs.getString("manufacturer"));
                    dto.setPrice(rs.getDouble("price"));
                    dto.setSupplierDiscount(rs.getDouble("Supplier discount"));
                    dto.setStoreDiscount(rs.getDouble("Store discount"));


                    return dto;
                }
            }
        }

        return null;


    }







}