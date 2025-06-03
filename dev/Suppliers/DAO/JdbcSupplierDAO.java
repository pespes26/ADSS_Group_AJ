package Suppliers.DAO;

import Suppliers.DTO.SupplierDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcSupplierDAO implements ISupplierDAO {

    private static final String DB_URL = "jdbc:sqlite:suppliers.db";

    public void createTableIfNotExists() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {            String sql = "CREATE TABLE IF NOT EXISTS suppliers (\n"
                    + " supplier_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                    + " supplier_name TEXT NOT NULL,\n"
                    + " company_id INTEGER NOT NULL,\n"
                    + " bank_account INTEGER NOT NULL,\n"
                    + " payment_method TEXT NOT NULL,\n"
                    + " payment_condition TEXT NOT NULL,\n"
                    + " phone_number INTEGER NOT NULL,\n"
                    + " email TEXT NOT NULL,\n"
                    + " is_active INTEGER DEFAULT 1\n"
                    + ");";

            stmt.execute(sql);

        } catch (SQLException e) {
            System.err.println("Error while creating suppliers table:");
            e.printStackTrace();
        }
    }    @Override
    public void insert(SupplierDTO dto) throws SQLException {
        String sql = "INSERT INTO suppliers (supplier_name, company_id, bank_account, payment_method, payment_condition, phone_number, email, is_active) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dto.getSupplierName());
            pstmt.setInt(2, dto.getCompany_id());
            pstmt.setInt(3, dto.getBankAccount());
            pstmt.setString(4, dto.getPaymentMethod());
            pstmt.setString(5, dto.getPaymentCondition());
            pstmt.setLong(6, dto.getPhoneNumber());
            pstmt.setString(7, dto.getEmail());
            pstmt.setInt(8, dto.isActive() ? 1 : 0);

            pstmt.executeUpdate();
        }
    }

    @Override
    public int insertAndGetID(SupplierDTO dto) throws SQLException {
        String sql = "INSERT INTO suppliers (supplier_name, company_id, bank_account, payment_method, payment_condition, phone_number, email) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, dto.getSupplierName());
                pstmt.setInt(2, dto.getCompany_id());
                pstmt.setInt(3, dto.getBankAccount());
                pstmt.setString(4, dto.getPaymentMethod());
                pstmt.setString(5, dto.getPaymentCondition());
                pstmt.setLong(6, dto.getPhoneNumber());
                pstmt.setString(7, dto.getEmail());

                pstmt.executeUpdate();
            }

            // קבל את ה-ID האחרון שהוזן
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    dto.setSupplier_id(generatedId);
                    return generatedId;
                }
            }
        }

        return 0;
    }


    @Override    public void update(SupplierDTO dto) throws SQLException {
        String sql = "UPDATE suppliers SET supplier_name = ?, company_id = ?, bank_account = ?, payment_method = ?, " +
                "payment_condition = ?, phone_number = ?, email = ?, is_active = ? WHERE supplier_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dto.getSupplierName());
            pstmt.setInt(2, dto.getCompany_id());
            pstmt.setInt(3, dto.getBankAccount());
            pstmt.setString(4, dto.getPaymentMethod());
            pstmt.setString(5, dto.getPaymentCondition());
            pstmt.setLong(6, dto.getPhoneNumber());
            pstmt.setString(7, dto.getEmail());
            pstmt.setInt(8, dto.isActive() ? 1 : 0);
            pstmt.setInt(9, dto.getSupplier_id());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void deleteById(int supplierId) throws SQLException {
        String sql = "DELETE FROM suppliers WHERE supplier_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, supplierId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public SupplierDTO getById(int supplierId) throws SQLException {
        String sql = "SELECT * FROM suppliers WHERE supplier_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, supplierId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {                SupplierDTO supplierDTO = new SupplierDTO(
                        rs.getString("supplier_name"),
                        rs.getInt("company_id"),
                        rs.getInt("bank_account"),
                        rs.getString("payment_method"),
                        rs.getString("payment_condition"),
                        rs.getLong("phone_number"),
                        rs.getString("email")
                );
                supplierDTO.setActive(rs.getInt("is_active") == 1);
                supplierDTO.setSupplier_id(rs.getInt("supplier_id"));
                return supplierDTO;
            }
            return null;
        }
    }

    @Override
    public List<SupplierDTO> getAll() throws SQLException {
        List<SupplierDTO> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM suppliers";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {                SupplierDTO dto = new SupplierDTO(
                        rs.getString("supplier_name"),
                        rs.getInt("company_id"),
                        rs.getInt("bank_account"),
                        rs.getString("payment_method"),
                        rs.getString("payment_condition"),
                        rs.getLong("phone_number"),
                        rs.getString("email")
                );
                dto.setActive(rs.getInt("is_active") == 1);
                dto.setSupplier_id(rs.getInt("supplier_id"));
                suppliers.add(dto);
            }
        }

        return suppliers;
    }

    public void clearTable() {
        String sql = "DELETE FROM suppliers";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:suppliers.db");
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("✅ Cleared all records from 'suppliers' table.");
        } catch (SQLException e) {
            System.err.println("❌ Failed to clear 'suppliers' table: " + e.getMessage());
        }
    }

    @Override
    public int getIdByName(String name) throws SQLException {
        String sql = "SELECT supplier_id FROM suppliers WHERE supplier_name = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("supplier_id");
            }
        }
        return 0; // או תזרוק שגיאה אם לא נמצא
    }





}
