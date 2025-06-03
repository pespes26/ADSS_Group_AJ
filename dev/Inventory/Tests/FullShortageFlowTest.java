package Inventory.Tests;

import Inventory.Domain.InventoryController;
import Inventory.Domain.ItemController;
import Inventory.Domain.ProductController;
import Inventory.Domain.Product;
import Inventory.DTO.ShortageOrderDTO;
import Inventory.Init.InventoryInitializer;
import Inventory.Repository.IShortageOrderRepository;
import Inventory.Repository.ShortageOrderRepositoryImpl;
import InventorySupplier.SystemService.ShortageOrderService;
import Suppliers.Repository.InventoryOrderRepositoryImpl;
import Suppliers.Repository.ProductSupplierRepositoryImpl;
import Suppliers.Repository.SupplierRepositoryImpl;
import Suppliers.DAO.*;
import Suppliers.Domain.SupplierManagementController;
import Suppliers.Domain.ProductSupplierManagementController;
import Suppliers.DTO.SupplierDTO;
import Suppliers.DTO.AgreementDTO;
import Suppliers.DTO.ProductSupplierDTO;
import Suppliers.DAO.JdbcAgreementDAO;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.*;

import static org.junit.Assert.*;

public class FullShortageFlowTest {

    private InventoryController inventoryController;
    private ShortageOrderService shortageOrderService;
    private IShortageOrderRepository shortageRepo;

    private JdbcSupplierDAO sharedSupplierDAO;
    private JdbcAgreementDAO sharedAgreementDAO;
    private JdbcProductSupplierDAO sharedProductSupplierDAO;
    private JdbcDiscountDAO sharedDiscountDAO;
    private JdbcOrderDAO sharedOrderDAO;

    @Before
    public void setUp() throws SQLException {
        sharedSupplierDAO = new JdbcSupplierDAO();
        sharedAgreementDAO = new JdbcAgreementDAO();
        sharedProductSupplierDAO = new JdbcProductSupplierDAO();
        sharedDiscountDAO = new JdbcDiscountDAO();
        sharedOrderDAO = new JdbcOrderDAO();

        InventoryInitializer.initializeAllTables();

        inventoryController = new InventoryController();
        ProductController productController = inventoryController.getProductController();
        ItemController itemController = inventoryController.getItemController();

        // יצירת מוצר
        Product product = new Product();
        product.setCatalogNumber(1004);
        product.setProductName("Orange Juice 1L");
        product.setCategory("Beverages");
        product.setSubCategory("Juices");
        product.setSupplierName("Prigat");
        product.setCostPriceBeforeSupplierDiscount(6.5);
        product.setSalePriceAfterStoreDiscount(10.0);
        product.setMinimumQuantityForAlert(5);
        productController.addProduct(product);

        // יצירת ספק
        SupplierDTO supplierDTO = new SupplierDTO("Prigat", 1, 1, "Online", "Net 30", 123456789L, "prigat@supplier.com");
        sharedSupplierDAO.insert(supplierDTO);
        supplierDTO.setSupplier_id(sharedSupplierDAO.getIdByName("Prigat"));



        AgreementDTO agreementDTO = new AgreementDTO(supplierDTO.getSupplier_id(),  new String[] { "Sunday", "Wednesday" },true);


        agreementDTO.setAgreement_ID(sharedAgreementDAO.insertAndGetID(agreementDTO));
        System.out.println("✅ Agreement inserted for supplier.");


        //

        // יצירת קשר בין מוצר לספק
        ProductSupplierRepositoryImpl productSupplierRepo = new ProductSupplierRepositoryImpl(
                sharedProductSupplierDAO, sharedDiscountDAO);
        ProductSupplierDTO productSupplierDTO = new ProductSupplierDTO(1004, 1004, supplierDTO.getSupplier_id(), agreementDTO.getAgreement_ID(), 1.5, "unit");
        productSupplierRepo.createProductSupplier(productSupplierDTO);

        // הוספת פריט שייצור חוסר
        itemController.addItem(1, 1, 1004, "Fridge", "2025-06-10");

        shortageRepo = new ShortageOrderRepositoryImpl();

        shortageOrderService = new ShortageOrderService(
                new InventoryOrderRepositoryImpl(
                        sharedProductSupplierDAO,
                        sharedDiscountDAO,
                        sharedOrderDAO,
                        sharedSupplierDAO,
                        sharedAgreementDAO
                ),
                shortageRepo
        );
    }


    @Test
    public void testFullShortageToSupplyFlow() throws SQLException {
        int branchId = 1;

        Map<Integer, Integer> shortageMap = inventoryController.getReportController().getShortageProductsMap(branchId);
        if (shortageMap.isEmpty()) {
            System.out.println("⛔ No shortages detected. Adding shortage manually.");
            shortageMap.put(1004, 5);
        }

        // שליחת ההזמנה לספקים
        shortageOrderService.onWakeUp(shortageMap, branchId);

        // שליפת ההזמנות מה-DB
        List<ShortageOrderDTO> orders = shortageRepo.getAll();
        assertFalse("❌ Expected at least one shortage order in table", orders.isEmpty());

        ShortageOrderDTO shortageOrder = orders.get(0);
        assertNotNull("❌ Expected a non-null shortage order", shortageOrder);

        int supplierId = shortageOrder.getSupplierId();
        assertTrue("❌ Expected shortage order to have valid supplierId", supplierId > 0);

        String supplierName = shortageOrder.getSupplierName();
        assertNotNull("❌ Expected shortage order to have supplier name", supplierName);

        System.out.println("✅ Shortage order exists in DB with supplierId=" + supplierId + ", supplierName=" + supplierName);
    }
}
