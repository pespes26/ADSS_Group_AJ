//package Suppliers.Test;
//import Suppliers.DTO.SupplierDTO;
//import Suppliers.Domain.ISupplierRepository;
//import Suppliers.Domain.SupplierManagementController;
//import Suppliers.Domain.SupplierRepositoryImpl;
//import Suppliers.dataaccess.DAO.IAgreementDAO;
//import Suppliers.dataaccess.DAO.ISupplierDAO;
//import Suppliers.dataaccess.DAO.JdbcAgreementDAO;
//import Suppliers.dataaccess.DAO.JdbcSupplierDAO;
//import org.junit.jupiter.api.*;
//
//import java.sql.SQLException;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//public class SupplierManagementControllerIntegrationTest {
//
//    private static SupplierManagementController controller;
//
//    @BeforeAll
//    public static void setup() {
//        ISupplierDAO supplierDAO = new JdbcSupplierDAO();
//        IAgreementDAO agreementDAO = new JdbcAgreementDAO();
//        ISupplierRepository supplierRepository = new SupplierRepositoryImpl(supplierDAO, agreementDAO);
//        controller = new SupplierManagementController();
//        controller.supplierRepository = supplierRepository;
//    }
//
//    @Test
//    @Order(1)
//    public void testCreateSupplier() throws SQLException {
//        SupplierDTO dto = new SupplierDTO(
//                "Test Supplier",
//                1001,
//                2002,
//                "Bank Transfer",
//                "Prepaid",
//                1234567890L,
//                "test@example.com"
//        );
//
//        controller.createSupplier(dto);
//
//        List<SupplierDTO> allSuppliers = controller.getAllSuppliersDTOs();
//
//        boolean found = allSuppliers.stream()
//                .anyMatch(s -> s.getSupplierName().equals("Test Supplier") &&
//                        s.getEmail().equals("test@example.com") &&
//                        s.getCompany_id() == 1001);
//
//        assertTrue(found, "Created supplier should be found in the database.");
//    }
//
//    @Test
//    @Order(2)
//    public void testDeleteSupplier() throws SQLException {
//        List<SupplierDTO> allSuppliers = controller.getAllSuppliersDTOs();
//
//        SupplierDTO toDelete = allSuppliers.stream()
//                .filter(s -> s.getSupplierName().equals("Test Supplier"))
//                .findFirst()
//                .orElse(null);
//
//        assertNotNull(toDelete, "Supplier to delete should exist");
//
//        controller.deleteSupplier(toDelete.getSupplier_id());
//
//        List<SupplierDTO> afterDelete = controller.getAllSuppliersDTOs();
//
//        boolean stillExists = afterDelete.stream()
//                .anyMatch(s -> s.getSupplier_id() == toDelete.getSupplier_id());
//
//        assertFalse(stillExists, "Deleted supplier should no longer exist in the database.");
//    }
//
//    @Test
//    @Order(3)
//    public void testGetAllSuppliersDTOs() throws SQLException {
//        List<SupplierDTO> suppliers = controller.getAllSuppliersDTOs();
//        assertNotNull(suppliers, "Supplier list should not be null");
//    }
//}
