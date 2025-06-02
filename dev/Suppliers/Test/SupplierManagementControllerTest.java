package Suppliers.Test;

import Suppliers.DTO.SupplierDTO;
import Suppliers.Domain.*;
import Suppliers.dataaccess.DAO.*;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class SupplierManagementControllerTest {
    private void clearDatabase() throws SQLException {
        String DB_URL = "jdbc:sqlite:suppliers.db";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM product_supplier");
            stmt.executeUpdate("DELETE FROM discounts");
            stmt.executeUpdate("DELETE FROM agreements");
            stmt.executeUpdate("DELETE FROM suppliers");
        }
    }

    private TestContext setupContext() throws SQLException {
        clearDatabase();

        ISupplierDAO supplierDAO = new JdbcSupplierDAO();
        IAgreementDAO agreementDAO = new JdbcAgreementDAO();
        IProductSupplierDAO productSupplierDAO = new JdbcProductSupplierDAO();
        IDiscountDAO discountDAO = new JdbcDiscountDAO();

        ISupplierRepository supplierRepository = new SupplierRepositoryImpl(supplierDAO, agreementDAO,productSupplierDAO, discountDAO);

        SupplierManagementController supplierManagementController = new SupplierManagementController(supplierRepository);

        return new TestContext(supplierDAO, agreementDAO, productSupplierDAO, discountDAO, supplierManagementController);
    }

    static class TestContext {
        public final ISupplierDAO supplierDAO;
        public final IAgreementDAO agreementDAO;
        public final IProductSupplierDAO productSupplierDAO;
        public final IDiscountDAO discountDAO;
        public final SupplierManagementController supplierManagementController;

        public TestContext(ISupplierDAO supplierDAO, IAgreementDAO agreementDAO, IProductSupplierDAO productSupplierDAO, IDiscountDAO discountDAO,SupplierManagementController supplierManagementController) {
            this.supplierDAO = supplierDAO;
            this.agreementDAO = agreementDAO;
            this.productSupplierDAO = productSupplierDAO;
            this.discountDAO = discountDAO;
            this.supplierManagementController = supplierManagementController;
        }
    }

    @Test
    public void givenNewSupplier_whenCreate_thenCanBeRetrievedFromDAO() throws SQLException {
        TestContext ctx = setupContext();

        SupplierDTO supplierDTO = new SupplierDTO("MAOR", 1234, 0, "Cash", "Prepaid", 5551234, "data@mail.com");
        ctx.supplierManagementController.createSupplier(supplierDTO);


        int supplierID = ctx.supplierDAO.getIdByName("MAOR");
        SupplierDTO retrievedSupplier = ctx.supplierDAO.getById(supplierID);

        assertNotNull(retrievedSupplier);
        assertEquals("MAOR", retrievedSupplier.getSupplierName());
        assertEquals("Cash", retrievedSupplier.getPaymentMethod());
        assertEquals("Prepaid" , retrievedSupplier.getPaymentCondition());
        assertEquals("data@mail.com", retrievedSupplier.getEmail());
        assertEquals(supplierID, retrievedSupplier.getSupplier_id());

    }

    @Test
    public void givenSupplierExists_whenDelete_thenSupplierRemoved() throws SQLException {
        TestContext ctx = setupContext();

        SupplierDTO supplier = new SupplierDTO("TO_DELETE", 9999, 1, "Bank", "Net", 1111111, "del@mail.com");
        ctx.supplierManagementController.createSupplier(supplier);

        int supplierId = supplier.getSupplier_id();

        ctx.supplierManagementController.deleteSupplier(supplierId);

        SupplierDTO retrieved = ctx.supplierDAO.getById(supplierId);
        assertNull(retrieved);
    }



}
