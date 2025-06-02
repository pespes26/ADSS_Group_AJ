package Suppliers.Test;

import Suppliers.DTO.*;
import Suppliers.Domain.IInventoryOrderRepository;
import Suppliers.Domain.InventoryOrderRepositoryImpl;
import Suppliers.Domain.OrderByShortageController;
import Suppliers.Domain.PeriodicOrderController;
import Suppliers.Presentation.SystemInitializer;
import Suppliers.dataaccess.DAO.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderFromInventoryControllersTest {
    private static PeriodicOrderController periodicOrderController;
    private static OrderByShortageController orderByShortageController;
    private static int supplierID1;
    private static int supplierID2;
    private static int supplierID3;
    private static int agreementID1;
    private static int agreementID2;
    private static int agreementID3;


    @BeforeAll
    public static void setUp() throws SQLException {
        SystemInitializer.initializeAllTables();

        String DB_URL = "jdbc:sqlite:suppliers.db";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            // שמירה על סדר נכון במקרה של קשרים בין טבלאות
            stmt.executeUpdate("DELETE FROM product_supplier");
            stmt.executeUpdate("DELETE FROM discounts");
            stmt.executeUpdate("DELETE FROM agreements");
            stmt.executeUpdate("DELETE FROM suppliers");
            stmt.executeUpdate("DELETE FROM orders"); // אם יש לך טבלת הזמנות
        }

        IAgreementDAO agreementDAO = new JdbcAgreementDAO();
        ISupplierDAO supplierDAO = new JdbcSupplierDAO();
        IProductSupplierDAO productSupplierDAO = new JdbcProductSupplierDAO();
        IDiscountDAO discountDAO = new JdbcDiscountDAO();
        IOrderDAO orderDAO = new JdbcOrderDAO();
        IInventoryOrderRepository orderRepository = new InventoryOrderRepositoryImpl(
                productSupplierDAO, discountDAO, orderDAO, supplierDAO, agreementDAO);

        periodicOrderController = new PeriodicOrderController(orderRepository);
        orderByShortageController = new OrderByShortageController(orderRepository);

        if (!supplierDAO.getAll().isEmpty()) {
            supplierID1 = supplierDAO.getIdByName("MAOR");
            supplierID2 = supplierDAO.getIdByName("RAN");
            supplierID3 = supplierDAO.getIdByName("YALI");

            List<AgreementDTO> list1 = agreementDAO.getBySupplierId(supplierID1);
            for (AgreementDTO agreementDTO : list1) { agreementID1 = agreementDTO.getAgreement_ID();}

            List<AgreementDTO> list2 = agreementDAO.getBySupplierId(supplierID2);
            for (AgreementDTO agreementDTO : list2) { agreementID1 = agreementDTO.getAgreement_ID();}

            List<AgreementDTO> list3 = agreementDAO.getBySupplierId(supplierID3);
            for (AgreementDTO agreementDTO : list3) { agreementID1 = agreementDTO.getAgreement_ID();}

        }

        supplierID1 = supplierDAO.insertAndGetID(new SupplierDTO("MAOR", 1234, 0, "Cash", "Prepaid", 5551234, "data@mail.com"));
        supplierID2 = supplierDAO.insertAndGetID(new SupplierDTO("RAN", 1254, 1, "Bank Transfer", "Standing Order", 5671234, "Data2@mail.com"));
        supplierID3 = supplierDAO.insertAndGetID(new SupplierDTO("YALI", 1256, 2, "Bank Transfer", "Standing Order", 5678234, "BlaBlaData34@mail.com"));

        agreementID1 = agreementDAO.insertAndGetID(new AgreementDTO(supplierID1, new String[]{"Mon", "Wed", "Fri"}, false));
        agreementID2 = agreementDAO.insertAndGetID(new AgreementDTO(supplierID2, new String[]{"Tue", "Thu"}, true));
        agreementID3 = agreementDAO.insertAndGetID(new AgreementDTO(supplierID3, new String[]{"Sun", "Wed"}, false));

        productSupplierDAO.insert(new ProductSupplierDTO(34, 1204, supplierID1, agreementID1, 6.5, "L"));
        productSupplierDAO.insert(new ProductSupplierDTO(89, 1204, supplierID2, agreementID2, 6.5, "g"));
        productSupplierDAO.insert(new ProductSupplierDTO(75, 1506, supplierID3, agreementID3, 4.5, "g"));

        discountDAO.insert(new DiscountDTO(1204, supplierID1, agreementID1, 20, 10.0));//same discount
        discountDAO.insert(new DiscountDTO(1204,  supplierID2, agreementID2, 25, 12.0));//same discount but biggest amount
        discountDAO.insert(new DiscountDTO(1506, supplierID1, agreementID3, 50, 15.0));

    }

    @Test
    public void givenValidData_whenGetPeriodicOrderProductDetails_thenReturnExpectedResults() throws SQLException {

        InventoryProductPeriodic productPeriodic1 = new InventoryProductPeriodic(supplierID1,agreementID1,1204,30);

        List<InventoryProductPeriodic> productsPeriodic = new ArrayList<>();
        productsPeriodic.add(productPeriodic1);

        long phoneNumber = 50222819;
        List<OrderProductDetails> orderProductDetails = periodicOrderController.getPeriodicOrderProductDetails(productsPeriodic,phoneNumber);
        for (OrderProductDetails orderProductDetail : orderProductDetails) {
            assertEquals(1204, orderProductDetail.getProductId());
            assertEquals(30, orderProductDetail.getQuantity());
            assertEquals(supplierID1, orderProductDetail.getSupplierId());
            assertEquals(6.5, orderProductDetail.getPrice(), 0.0001);
            assertEquals("MAOR", orderProductDetail.getSupplierName());
            assertArrayEquals(new String[]{"Mon", "Wed", "Fri"}, orderProductDetail.getDeliveryDays());
            assertEquals(10.0, orderProductDetail.getDiscount());
        }
    }

    @Test
    public void givenValidData_whengetShortageOrderProductDetails_thenReturnExpectedResults() throws SQLException {

        HashMap<Integer,Integer> products = new HashMap<>();
        products.put(1204,20);

        long phoneNumber = 50222819;

        List<OrderProductDetails> orderProductDetails = orderByShortageController.getShortageOrderProductDetails(products, phoneNumber);
        for (OrderProductDetails orderProductDetail : orderProductDetails) {
            assertEquals(1204, orderProductDetail.getProductId());
            assertEquals(20, orderProductDetail.getQuantity());
            assertEquals(supplierID1, orderProductDetail.getSupplierId());
            assertEquals(6.5, orderProductDetail.getPrice(), 0.0001);
            assertEquals("MAOR", orderProductDetail.getSupplierName());
            assertArrayEquals(new String[]{"Mon", "Wed", "Fri"}, orderProductDetail.getDeliveryDays());
            assertEquals(10.0, orderProductDetail.getDiscount());
        }
    }

    @Test
    public void givenSameProductWithTwoDifferentDiscounts_whenGetShortageOrderProductDetails_thenReturnTheBestPrice()  throws SQLException {

        HashMap<Integer,Integer> products = new HashMap<>();
        products.put(1204,30);

        long phoneNumber = 50222819;

        List<OrderProductDetails> orderProductDetails = orderByShortageController.getShortageOrderProductDetails(products, phoneNumber);
        for (OrderProductDetails orderProductDetail : orderProductDetails) {
            assertEquals(supplierID2, orderProductDetail.getSupplierId());
            assertEquals(12.0, orderProductDetail.getDiscount());
        }
    }



}
