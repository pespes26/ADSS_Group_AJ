package Suppliers.Presentation;

import Suppliers.DTO.AgreementDTO;
import Suppliers.Domain.Controller;
import Suppliers.dataaccess.DAO.JdbcAgreementDAO;
import Suppliers.dataaccess.DAO.JdbcDiscountDAO;
import Suppliers.dataaccess.DAO.JdbcProductSupplierDAO;
import Suppliers.dataaccess.DAO.JdbcSupplierDAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DataInitializer is responsible for populating the system with sample data.
 * This includes suppliers, agreements, products (with discount rules), and orders.
 */
public class SystemInitializer {
    private final JdbcSupplierDAO supplierDAO;
    private final JdbcAgreementDAO agreementDAO;

    public SystemInitializer() {
        this.supplierDAO = new JdbcSupplierDAO();
        this.agreementDAO = new JdbcAgreementDAO();
    }

    public static void SystemInitializer() {
    }

    public static void initializeAllTables() {
        new JdbcSupplierDAO().createTableIfNotExists();
        new JdbcAgreementDAO().createTableIfNotExists();
        new JdbcProductSupplierDAO().createProductSupplierTableIfNotExists();
        new JdbcDiscountDAO().createTableIfNotExists();
    }

    public void initializeDatabase(boolean withSampleData) throws SQLException {
//        supplierDAO.createTableIfNotExists();
        agreementDAO.createTableIfNotExists();

        if (withSampleData) {
            insertSampleData();
        }
    }

    private void insertSampleData() throws SQLException {
//        supplierDAO.insert(new SupplierDTO(1, "ספק ניסיוני", "test@example.com", "123456789"));
        agreementDAO.insert(new AgreementDTO( 101, new String[]{"Mon", "Wed", "Fri"}, false));
        agreementDAO.insert(new AgreementDTO( 101, new String[]{"Tue", "Thu"}, true));
        agreementDAO.insert(new AgreementDTO( 102, new String[]{"Sun", "Wed"}, false));
        agreementDAO.insert(new AgreementDTO( 103, new String[]{"Mon", "Tue"}, false));

    }
    public void clearAllData() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:suppliers.db");
             Statement stmt = conn.createStatement()) {

            stmt.execute("DELETE FROM agreements;");
            stmt.execute("DELETE FROM suppliers;");
            // הוסף כאן עוד טבלאות אם יש

            System.out.println("All data deleted from tables.");

        } catch (SQLException e) {
            System.err.println("Failed to clear data from tables:");
            e.printStackTrace();
        }
    }
}
