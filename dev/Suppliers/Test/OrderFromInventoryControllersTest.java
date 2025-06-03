//package Suppliers.Test;
//
//import Suppliers.DTO.*;
//import Suppliers.Domain.IInventoryOrderRepository;
//import Suppliers.Domain.InventoryOrderRepositoryImpl;
//import Suppliers.Domain.OrderByShortageController;
//import Suppliers.Domain.PeriodicOrderController;
//import Suppliers.Presentation.SystemInitializer;
//import Suppliers.dataaccess.DAO.*;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertArrayEquals;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//
//public class OrderFromInventoryControllersTest {
//    private static PeriodicOrderController periodicOrderController;
//    private static OrderByShortageController orderByShortageController;
//    private static int supplierID1;
//    private static int supplierID2;
//    private static int supplierID3;
//    private static int agreementID1;
//    private static int agreementID2;
//    private static int agreementID3;
//
//
//    @BeforeAll
//    public static void setUp() throws SQLException {
//        SystemInitializer.initializeAllTables();
//
//        String DB_URL = "jdbc:sqlite:suppliers.db";
//        try (Connection conn = DriverManager.getConnection(DB_URL);
//             Statement stmt = conn.createStatement()) {
//
//            // שמירה על סדר נכון במקרה של קשרים בין טבלאות
//            stmt.executeUpdate("DELETE FROM product_supplier");
//            stmt.executeUpdate("DELETE FROM discounts");
//            stmt.executeUpdate("DELETE FROM agreements");
//            stmt.executeUpdate("DELETE FROM suppliers");
//            stmt.executeUpdate("DELETE FROM orders");
//        }
//
//        IAgreementDAO agreementDAO = new JdbcAgreementDAO();
//        ISupplierDAO supplierDAO = new JdbcSupplierDAO();
//        IProductSupplierDAO productSupplierDAO = new JdbcProductSupplierDAO();
//        IDiscountDAO discountDAO = new JdbcDiscountDAO();
//        IOrderDAO orderDAO = new JdbcOrderDAO();
//        IInventoryOrderRepository orderRepository = new InventoryOrderRepositoryImpl(
//                productSupplierDAO, discountDAO, orderDAO, supplierDAO, agreementDAO);
//
//        periodicOrderController = new PeriodicOrderController(orderRepository);
//        orderByShortageController = new OrderByShortageController(orderRepository);
//
//        if (!supplierDAO.getAll().isEmpty()) {
//            supplierID1 = supplierDAO.getIdByName("MAOR");
//            supplierID2 = supplierDAO.getIdByName("RAN");
//            supplierID3 = supplierDAO.getIdByName("YALI");
//
//            List<AgreementDTO> list1 = agreementDAO.getBySupplierId(supplierID1);
//            for (AgreementDTO agreementDTO : list1) {
//                agreementID1 = agreementDTO.getAgreement_ID();
//            }
//
//            List<AgreementDTO> list2 = agreementDAO.getBySupplierId(supplierID2);
//            for (AgreementDTO agreementDTO : list2) {
//                agreementID1 = agreementDTO.getAgreement_ID();
//            }
//
//            List<AgreementDTO> list3 = agreementDAO.getBySupplierId(supplierID3);
//            for (AgreementDTO agreementDTO : list3) {
//                agreementID1 = agreementDTO.getAgreement_ID();
//            }
//
//        }
//
//        supplierID1 = supplierDAO.insertAndGetID(new SupplierDTO("MAOR", 1234, 0, "Cash", "Prepaid", 5551234, "data@mail.com"));
//        supplierID2 = supplierDAO.insertAndGetID(new SupplierDTO("RAN", 1254, 1, "Bank Transfer", "Standing Order", 5671234, "Data2@mail.com"));
//        supplierID3 = supplierDAO.insertAndGetID(new SupplierDTO("YALI", 1256, 2, "Bank Transfer", "Standing Order", 5678234, "BlaBlaData34@mail.com"));
//
//        agreementID1 = agreementDAO.insertAndGetID(new AgreementDTO(supplierID1, new String[]{"Mon", "Wed", "Fri"}, false));
//        agreementID2 = agreementDAO.insertAndGetID(new AgreementDTO(supplierID2, new String[]{"Tue", "Thu"}, true));
//        agreementID3 = agreementDAO.insertAndGetID(new AgreementDTO(supplierID3, new String[]{"Sun", "Wed"}, false));
//
////        productSupplierDAO.insert(new ProductSupplierDTO(34, 1204, supplierID1, agreementID1, 6.5, "L"));
////        productSupplierDAO.insert(new ProductSupplierDTO(89, 1204, supplierID2, agreementID2, 6.5, "g"));
////        productSupplierDAO.insert(new ProductSupplierDTO(75, 1204, supplierID3, agreementID3, 0.5, "g"));
////
////        discountDAO.insert(new DiscountDTO(1204, supplierID1, agreementID1, 20, 10.0));//same discount
////        discountDAO.insert(new DiscountDTO(1204, supplierID2, agreementID2, 25, 12.0));//same discount but biggest amount
////        discountDAO.insert(new DiscountDTO(1506, supplierID1, agreementID3, 50, 15.0));
//    }
//
//    @Test
//    public void givenValidData_whenGetPeriodicOrderProductDetails_thenReturnExpectedResults() throws SQLException {
//        IProductSupplierDAO productSupplierDAO = new JdbcProductSupplierDAO();
//        IDiscountDAO discountDAO = new JdbcDiscountDAO();
//        productSupplierDAO.insert(new ProductSupplierDTO(34, 1204, supplierID1, agreementID1, 6.5, "L"));
//        discountDAO.insert(new DiscountDTO(1204, supplierID1, agreementID1, 20, 10.0));//same discount
//
//        InventoryProductPeriodic productPeriodic1 = new InventoryProductPeriodic(supplierID1, agreementID1, 1204, 30);
//
//        List<InventoryProductPeriodic> productsPeriodic = new ArrayList<>();
//        productsPeriodic.add(productPeriodic1);
//
//        long phoneNumber = 50222819;
//        List<OrderProductDetails> orderProductDetails = periodicOrderController.getPeriodicOrderProductDetails(productsPeriodic, phoneNumber);
//        for (OrderProductDetails orderProductDetail : orderProductDetails) {
//            assertEquals(1204, orderProductDetail.getProductId());
//            assertEquals(30, orderProductDetail.getQuantity());
//            assertEquals(supplierID1, orderProductDetail.getSupplierId());
//            assertEquals(6.5, orderProductDetail.getPrice(), 0.0001);
//            assertEquals("MAOR", orderProductDetail.getSupplierName());
//            assertArrayEquals(new String[]{"Mon", "Wed", "Fri"}, orderProductDetail.getDeliveryDays());
//            assertEquals(10.0, orderProductDetail.getDiscount());
//        }
//    }
//
//    @Test
//    public void givenValidData_whenGetShortageOrderProductDetails_thenReturnExpectedResults() throws SQLException {
//
//        HashMap<Integer, Integer> products = new HashMap<>();
//        products.put(1204, 20);
//
//        long phoneNumber = 50222819;
//
//        List<OrderProductDetails> orderProductDetails = orderByShortageController.getShortageOrderProductDetails(products, phoneNumber);
//        for (OrderProductDetails orderProductDetail : orderProductDetails) {
//            assertEquals(1204, orderProductDetail.getProductId());
//            assertEquals(20, orderProductDetail.getQuantity());
//            assertEquals(supplierID1, orderProductDetail.getSupplierId());
//            assertEquals(6.5, orderProductDetail.getPrice(), 0.0001);
//            assertEquals("MAOR", orderProductDetail.getSupplierName());
//            assertArrayEquals(new String[]{"Mon", "Wed", "Fri"}, orderProductDetail.getDeliveryDays());
//            assertEquals(10.0, orderProductDetail.getDiscount());
//        }
//    }
//
//    @Test
//    public void givenSameProductWithTwoDifferentDiscounts_whenGetShortageOrderProductDetails_thenReturnTheBestPrice() throws SQLException {
//        IProductSupplierDAO productSupplierDAO = new JdbcProductSupplierDAO();
//        IDiscountDAO discountDAO = new JdbcDiscountDAO();
//        productSupplierDAO.insert(new ProductSupplierDTO(89, 1204, supplierID2, agreementID2, 6.5, "g"));
//        discountDAO.insert(new DiscountDTO(1204, supplierID2, agreementID2, 25, 12.0));//same discount but biggest amount
//
//
//        HashMap<Integer, Integer> products = new HashMap<>();
//        products.put(1204, 30);
//
//        long phoneNumber = 50222819;
//
//        List<OrderProductDetails> orderProductDetails = orderByShortageController.getShortageOrderProductDetails(products, phoneNumber);
//        for (OrderProductDetails orderProductDetail : orderProductDetails) {
//            assertEquals(supplierID2, orderProductDetail.getSupplierId());
//            assertEquals(12.0, orderProductDetail.getDiscount());
//        }
//    }
//
//    @Test
//    public void givenCheapestSupplierHasNoDiscount_whenGetShortageOrderProductDetails_thenReturnTheBestPrice() throws SQLException {
//        IProductSupplierDAO productSupplierDAO = new JdbcProductSupplierDAO();
//        IDiscountDAO discountDAO = new JdbcDiscountDAO();
//        productSupplierDAO.insert(new ProductSupplierDTO(75, 1204, supplierID3, agreementID3, 0.5, "g"));
//        discountDAO.insert(new DiscountDTO(1506, supplierID3, agreementID3, 50, 15.0));
//
//        HashMap<Integer, Integer> products = new HashMap<>();
//        products.put(1204, 30);
//
//        long phoneNumber = 50222819;
//
//        List<OrderProductDetails> orderProductDetails = orderByShortageController.getShortageOrderProductDetails(products, phoneNumber);
//        for (OrderProductDetails orderProductDetail : orderProductDetails) {
//            assertEquals(supplierID3, orderProductDetail.getSupplierId());
//            assertEquals(0.0, orderProductDetail.getDiscount());
//        }
//    }
//}
//
//
//
//
//
//
//




package Suppliers.Test;

import Suppliers.DAO.*;
import Inventory.DTO.InventoryProductPeriodic;
import Suppliers.DTO.*;
import Suppliers.Domain.*;
import Suppliers.Repository.IInventoryOrderRepository;
import Suppliers.Repository.InventoryOrderRepositoryImpl;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class OrderFromInventoryControllersTest {

    private void clearDatabase() throws SQLException {
        String DB_URL = "jdbc:sqlite:suppliers.db";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM product_supplier");
            stmt.executeUpdate("DELETE FROM discounts");
            stmt.executeUpdate("DELETE FROM agreements");
            stmt.executeUpdate("DELETE FROM suppliers");
            stmt.executeUpdate("DELETE FROM orders");
        }
    }

    private TestContext setupContext() throws SQLException {
        clearDatabase();

        ISupplierDAO supplierDAO = new JdbcSupplierDAO();
        IAgreementDAO agreementDAO = new JdbcAgreementDAO();
        IProductSupplierDAO productSupplierDAO = new JdbcProductSupplierDAO();
        IDiscountDAO discountDAO = new JdbcDiscountDAO();
        IOrderDAO orderDAO = new JdbcOrderDAO();

        IInventoryOrderRepository orderRepository = new InventoryOrderRepositoryImpl(productSupplierDAO, discountDAO, orderDAO, supplierDAO, agreementDAO);

        PeriodicOrderController periodicOrderController = new PeriodicOrderController(orderRepository);
        OrderByShortageController shortageController = new OrderByShortageController(orderRepository);

        return new TestContext(supplierDAO, agreementDAO, productSupplierDAO, discountDAO, periodicOrderController, shortageController);
    }

    static class TestContext {
        public final ISupplierDAO supplierDAO;
        public final IAgreementDAO agreementDAO;
        public final IProductSupplierDAO productSupplierDAO;
        public final IDiscountDAO discountDAO;
        public final PeriodicOrderController periodicOrderController;
        public final OrderByShortageController shortageController;

        public TestContext(ISupplierDAO supplierDAO, IAgreementDAO agreementDAO, IProductSupplierDAO productSupplierDAO, IDiscountDAO discountDAO, PeriodicOrderController periodicOrderController, OrderByShortageController shortageController) {
            this.supplierDAO = supplierDAO;
            this.agreementDAO = agreementDAO;
            this.productSupplierDAO = productSupplierDAO;
            this.discountDAO = discountDAO;
            this.periodicOrderController = periodicOrderController;
            this.shortageController = shortageController;
        }
    }

    @Test
    public void givenValidData_whenGetPeriodicOrderProductDetails_thenReturnExpectedResults() throws SQLException {
        TestContext ctx = setupContext();

        int supplierID = ctx.supplierDAO.insertAndGetID(new SupplierDTO("MAOR", 1234, 0, "Cash", "Prepaid", 5551234, "mail@a.com"));
        int agreementID = ctx.agreementDAO.insertAndGetID(new AgreementDTO(supplierID, new String[]{"Mon", "Wed", "Fri"}, false));
        ctx.productSupplierDAO.insert(new ProductSupplierDTO(1204,34, supplierID, agreementID, 6.5, "L"));
        ctx.discountDAO.insert(new DiscountDTO(1204, supplierID, agreementID, 20, 10.0));

        InventoryProductPeriodic product = new InventoryProductPeriodic(supplierID, agreementID, 1204, 30);
        List<OrderProductDetailsDTO> result = ctx.periodicOrderController.getPeriodicOrderProductDetails(List.of(product), 50222819);

        assertEquals(1, result.size());
        OrderProductDetailsDTO detail = result.get(0);
        assertEquals(supplierID, detail.getSupplierId());
        assertEquals(6.5, detail.getPrice(), 0.0001);
        assertEquals("MAOR", detail.getSupplierName());
        assertArrayEquals(new String[]{"Mon", "Wed", "Fri"}, detail.getDeliveryDays());
        assertEquals(10.0, detail.getDiscount());

    }

    @Test
    public void givenSameProductWithTwoDifferentDiscounts_whenGetShortageOrderProductDetails_thenReturnBestDiscount() throws SQLException {
        TestContext ctx = setupContext();

        int supplierID1 = ctx.supplierDAO.insertAndGetID(new SupplierDTO("S1", 1000, 0, "Cash", "Prepaid", 1111, "s1@mail.com"));
        int supplierID2 = ctx.supplierDAO.insertAndGetID(new SupplierDTO("S2", 1001, 1, "Bank", "Order", 2222, "s2@mail.com"));

        int agreement1 = ctx.agreementDAO.insertAndGetID(new AgreementDTO(supplierID1, new String[]{"Mon"}, false));
        int agreement2 = ctx.agreementDAO.insertAndGetID(new AgreementDTO(supplierID2, new String[]{"Tue"}, false));

        ctx.productSupplierDAO.insert(new ProductSupplierDTO( 1204,11, supplierID1, agreement1, 6.5, "L"));
        ctx.productSupplierDAO.insert(new ProductSupplierDTO( 1204,22, supplierID2, agreement2, 6.5, "L"));

        ctx.discountDAO.insert(new DiscountDTO(1204, supplierID1, agreement1, 10, 8.0));
        ctx.discountDAO.insert(new DiscountDTO(1204, supplierID2, agreement2, 10, 12.0));

        HashMap<Integer, Integer> products = new HashMap<>();
        products.put(1204, 15);

        List<OrderProductDetailsDTO> result = ctx.shortageController.getShortageOrderProductDetails(products, 50222819);
        OrderProductDetailsDTO detail = result.get(0);
        assertEquals(supplierID2, detail.getSupplierId());
        assertEquals(6.5, detail.getPrice(), 0.0001);
        assertEquals("S2", detail.getSupplierName());
        assertArrayEquals(new String[]{"Tue"}, detail.getDeliveryDays());
        assertEquals(12.0, detail.getDiscount());
    }

    @Test
    public void givenCheapestSupplierHasNoDiscount_whenGetShortageOrderProductDetails_thenReturnCheapestPrice() throws SQLException {
        TestContext ctx = setupContext();

        int supplierID1 = ctx.supplierDAO.insertAndGetID(new SupplierDTO("S1", 1000, 0, "Cash", "Prepaid", 1111, "s1@mail.com"));
        int supplierID2 = ctx.supplierDAO.insertAndGetID(new SupplierDTO("S2", 1001, 1, "Bank", "Order", 2222, "s2@mail.com"));

        int agreement1 = ctx.agreementDAO.insertAndGetID(new AgreementDTO(supplierID1, new String[]{"Mon"}, false));
        int agreement2 = ctx.agreementDAO.insertAndGetID(new AgreementDTO(supplierID2, new String[]{"Tue"}, false));

        ctx.productSupplierDAO.insert(new ProductSupplierDTO( 1204,11, supplierID1, agreement1, 6.5, "L"));
        ctx.productSupplierDAO.insert(new ProductSupplierDTO( 1204,22, supplierID2, agreement2, 0.5, "L"));

        ctx.discountDAO.insert(new DiscountDTO(1204, supplierID1, agreement1, 10, 10.0));

        HashMap<Integer, Integer> products = new HashMap<>();
        products.put(1204, 5);

        List<OrderProductDetailsDTO> result = ctx.shortageController.getShortageOrderProductDetails(products, 50222819);
        assertEquals(supplierID2, result.get(0).getSupplierId());
        assertEquals(0.0, result.get(0).getDiscount());
    }


    @Test
    public void givenValidShortage_whenSavingShortageOrder_thenInsertSucceeds() throws SQLException {
        TestContext ctx = setupContext();

        // יצירת ספק, הסכם, מוצר, הנחה
        int supplierId = ctx.supplierDAO.insertAndGetID(new SupplierDTO("SUP", 111, 0, "Cash", "Prepaid", 1234, "sup@mail.com"));
        int agreementId = ctx.agreementDAO.insertAndGetID(new AgreementDTO(supplierId, new String[]{"Mon", "Thu"}, false));
        ctx.productSupplierDAO.insert(new ProductSupplierDTO(1204, 777, supplierId, agreementId, 5.0, "kg"));
        ctx.discountDAO.insert(new DiscountDTO(1204, supplierId, agreementId, 10, 15.0));

        // יצירת קלט להזמנה לפי חוסרים
        HashMap<Integer, Integer> shortageProducts = new HashMap<>();
        shortageProducts.put(1204, 12); // כמות מעל סף ההנחה

        long phoneNumber = 500000000;

        // קריאה לשירות שמחזיר פרטי הזמנה
        List<OrderProductDetailsDTO> orderDetails = ctx.shortageController.getShortageOrderProductDetails(shortageProducts, phoneNumber);

        // שליפת ההזמנות שנשמרו ובדיקת נכונות
        List<OrderDTO> orders = new JdbcOrderDAO().getAll();
        assertEquals(1, orders.size());
        OrderDTO order = orders.get(0);
        assertEquals(phoneNumber, order.getPhoneNumber());
    }

}
