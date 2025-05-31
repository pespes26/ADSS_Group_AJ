//package Suppliers.Test;
//
//import Suppliers.DTO.AgreementDTO;
//import Suppliers.Domain.Agreement;
//import Suppliers.Domain.AgreementManagementController;
//import Suppliers.Domain.AgreementRepositoryImpl;
//import Suppliers.Domain.IAgreementRepository;
//import Suppliers.dataaccess.DAO.IAgreementDAO;
//import Suppliers.dataaccess.DAO.JdbcAgreementDAO;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class AgreementManagementControllerTset {
//    private AgreementManagementController controller;
//    private IAgreementRepository repository;
//
//    @BeforeEach
//    public void setUp() {
//        IAgreementDAO dao = new JdbcAgreementDAO();
//        repository = new AgreementRepositoryImpl(dao);
//        controller = new AgreementManagementController();
//        controller.agreementRepository = repository; // Set the repository manually
//    }
//
//    @Test
//    public void testCreateAndGetAgreement() {
//        AgreementDTO dto = new AgreementDTO();
//        dto.setSupplier_ID(1);
//        dto.setSelfPickup(true);
//        dto.setDeliveryDays(new String[]{"Sunday", "Wednesday"});
//
//        controller.createAgreementWithSupplier(dto);
//        int id = dto.getAgreement_ID();
//
//        assertTrue(id > 0);
//
//        Agreement agreement = repository.getAgreementByID(id);
//        assertNotNull(agreement);
//        assertEquals(1, agreement.getSupplier_ID());
//        assertTrue(agreement.isSelfPickup());
//    }
//
//    @Test
//    public void testUpdateDeliveryDays() {
//        AgreementDTO dto = new AgreementDTO();
//        dto.setSupplier_ID(2);
//        dto.setSelfPickup(false);
//        dto.setDeliveryDays(new String[]{"Monday"});
//
//        controller.createAgreementWithSupplier(dto);
//        int id = dto.getAgreement_ID();
//
//        controller.setDeliveryDays(id, new String[]{"Monday", "Thursday"});
//        Agreement updated = repository.getAgreementByID(id);
//        assertArrayEquals(new String[]{"Monday", "Thursday"}, updated.getDeliveryDays());
//    }
//
//    @Test
//    public void testUpdateSelfPickup() {
//        AgreementDTO dto = new AgreementDTO();
//        dto.setSupplier_ID(3);
//        dto.setSelfPickup(false);
//        dto.setDeliveryDays(new String[]{"Tuesday"});
//
//        controller.createAgreementWithSupplier(dto);
//        int id = dto.getAgreement_ID();
//
//        controller.setSelfPickup(id, true);
//        Agreement updated = repository.getAgreementByID(id);
//        assertTrue(updated.isSelfPickup());
//    }
//
//    @Test
//    public void testDeleteAgreementWithSupplier() {
//        AgreementDTO dto = new AgreementDTO();
//        dto.setSupplier_ID(4);
//        dto.setSelfPickup(true);
//        dto.setDeliveryDays(new String[]{"Friday"});
//
//        controller.createAgreementWithSupplier(dto);
//        int id = dto.getAgreement_ID();
//
//        controller.deleteAgreementWithSupplier(id, 4);
//        Agreement deleted = repository.getAgreementByID(id);
//        assertNull(deleted);
//    }
//}
