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



//    public static void populateInitialData(Controller controller) {
//        // ===== Create Suppliers =====
//        controller.createSupplier("Tnuva", 101, 12345, 11111, "Bank Transfer", 501234567, "tnuva@example.com", "Prepaid");
//        controller.createSupplier("Strauss", 102, 12346, 22222, "Credit Card", 502345678, "strauss@example.com", "Standing Order");
//        controller.createSupplier("Osem", 103, 12347, 33333, "Check", 503456789, "osem@example.com", "Pay at delivery");
//        controller.createSupplier("CocaCola", 104, 12348, 44444, "Bank Transfer", 504567890, "cocacola@example.com", "Prepaid");
//
//        // ===== Create Agreements for each supplier =====
//        controller.createAgreement(201, 101, new String[]{"Mon", "Wed", "Fri"}, false);
//        controller.createAgreement(202, 101, new String[]{"Tue", "Thu"}, true);
//        controller.createAgreement(203, 102, new String[]{"Sun", "Wed"}, false);
//        controller.createAgreement(204, 103, new String[]{"Mon", "Tue"}, false);
//        controller.createAgreement(205, 104, new String[]{"Wed", "Sat"}, true);
//
//        // ===== Create Products with Discount Rules and add to agreements =====
//        Product milk = controller.createProduct(1001, 5001, 5.5, "Liter", 101);
//        milk.updateOrAddDiscountRule(10, 5);   // 5% discount for 10+ units
//        milk.updateOrAddDiscountRule(20, 10);  // 10% discount for 20+ units
//        controller.addProductToAgreement(5001, milk, 201);
//
//        Product chocolate = controller.createProduct(1002, 5002, 8.0, "Bar", 101);
//        chocolate.updateOrAddDiscountRule(5, 3); // 3% discount for 5+ units
//        controller.addProductToAgreement(5002, chocolate, 202);
//
//        Product hummus = controller.createProduct(1003, 6001, 6.2, "Pack", 102);
//        hummus.updateOrAddDiscountRule(10, 5); // 5% discount for 10+ units
//        controller.addProductToAgreement(6001, hummus, 203);
//
//        Product pasta = controller.createProduct(1004, 7001, 4.3, "Bag", 103);
//        pasta.updateOrAddDiscountRule(15, 7); // 7% discount for 15+ units
//        controller.addProductToAgreement(7001, pasta, 204);
//
//        Product coke = controller.createProduct(1005, 8001, 6.0, "Bottle", 104);
//        coke.updateOrAddDiscountRule(12, 4);  // 4% discount for 12+ units
//        coke.updateOrAddDiscountRule(25, 10); // 10% discount for 25+ units
//        controller.addProductToAgreement(8001, coke, 205);
//
//        Product chips = controller.createProduct(1006, 8002, 3.5, "Bag", 104);
//        controller.addProductToAgreement(8002, chips, 205);
//
//        // ===== Create Sample Orders =====
//        Map<Integer, Integer> order1 = new HashMap<>();
//        order1.put(5001, 12); // milk
//        order1.put(5002, 6);  // chocolate
//        controller.createOrder(1, 501234567, LocalDateTime.now(), order1);
//
//        Map<Integer, Integer> order2 = new HashMap<>();
//        order2.put(7001, 10); // pasta
//        order2.put(8001, 15); // coke
//        order2.put(8002, 5);  // chips
//        controller.createOrder(2, 504567890, LocalDateTime.now(), order2);
//
//        Map<Integer, Integer> order3 = new HashMap<>();
//        order3.put(6001, 20); // hummus
//        controller.createOrder(3, 502345678, LocalDateTime.now(), order3);
//    }
}
