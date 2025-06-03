package Integration_And_Unit_Tests;

import Suppliers.DAO.*;

public class TestDatabaseInitializer {
    public static void initialize() {
        // Initialize all tables first
        JdbcSupplierDAO supplierDAO = new JdbcSupplierDAO();
        JdbcAgreementDAO agreementDAO = new JdbcAgreementDAO();
        JdbcProductSupplierDAO productSupplierDAO = new JdbcProductSupplierDAO();
        JdbcDiscountDAO discountDAO = new JdbcDiscountDAO();
        JdbcOrderDAO orderDAO = new JdbcOrderDAO();

        // Create tables
        supplierDAO.createTableIfNotExists();
        agreementDAO.createTableIfNotExists();
        productSupplierDAO.createProductSupplierTableIfNotExists();
        discountDAO.createTableIfNotExists();
        orderDAO.createTableIfNotExists();
    }
}
