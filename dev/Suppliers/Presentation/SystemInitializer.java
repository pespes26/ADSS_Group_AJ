package Suppliers.Presentation;

import Inventory.DAO.JdbcProductDAO;
import Inventory.DTO.ProductDTO;
import Suppliers.DTO.AgreementDTO;
import Suppliers.DTO.DiscountDTO;
import Suppliers.DTO.ProductSupplierDTO;
import Suppliers.DTO.SupplierDTO;
import Suppliers.dataaccess.DAO.*;

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
    private final JdbcProductSupplierDAO productSupplierDAO ;
    private final JdbcDiscountDAO discountDAO;

    public SystemInitializer() {
        this.supplierDAO = new JdbcSupplierDAO();
        this.agreementDAO = new JdbcAgreementDAO();
        this.productSupplierDAO = new JdbcProductSupplierDAO();
        this.discountDAO = new JdbcDiscountDAO();

    }

    public static void SystemInitializer() {
    }

    public static void initializeAllTables() {
        new JdbcSupplierDAO().createTableIfNotExists();
        new JdbcAgreementDAO().createTableIfNotExists();
        new JdbcProductSupplierDAO().createProductSupplierTableIfNotExists();
        new JdbcDiscountDAO().createTableIfNotExists();
        new JdbcOrderDAO().createTableIfNotExists();
    }

    public void initializeDatabase(boolean withSampleData) throws SQLException {
//        supplierDAO.createTableIfNotExists();
        agreementDAO.createTableIfNotExists();

        if (withSampleData) {
            insertSampleData();
        }
    }

    private void insertSampleData() throws SQLException {

        int supplierID1 =  supplierDAO.insertAndGetID(new SupplierDTO("Prigat", 0123, 9987,"Cash","Prepaid",5551234, "data@mail.com" ));
        int supplierID2 =  supplierDAO.insertAndGetID(new SupplierDTO("Tnuva", 0124, 9007,"Bank Transfer","Standing Order",5671234, "OneMoreData@mail.com" ));
        int supplierID3 =  supplierDAO.insertAndGetID(new SupplierDTO("Osem", 0125, 9107,"Bank Transfer","Standing Order",5678234, "BlaBlaData@mail.com" ));


        int agreementID1 = agreementDAO.insertAndGetID(new AgreementDTO( supplierID1, new String[]{"Mon", "Wed", "Fri"}, false));
        int agreementID2 = agreementDAO.insertAndGetID(new AgreementDTO( supplierID2, new String[]{"Tue", "Thu"}, true));
        int agreementID3 = agreementDAO.insertAndGetID(new AgreementDTO( supplierID3, new String[]{"Sun", "Wed"}, false));


        productSupplierDAO.insert(new ProductSupplierDTO(0,1004, 1,agreementID1,6.5,"L"));
        productSupplierDAO.insert(new ProductSupplierDTO(1,1005, 2,agreementID2,8.0,"g"));
        productSupplierDAO.insert(new ProductSupplierDTO(2,1006, 3,agreementID3,4.5,"g"));

        discountDAO.insert(new DiscountDTO(1004, 0, 0, 20,10.0 ));
        discountDAO.insert(new DiscountDTO(1005, 1, 1, 10,5.0 ));
        discountDAO.insert(new DiscountDTO(1006, 2, 2, 50,15.0 ));


    }
//                    new ProductDTO(1004, "Orange Juice 1L", "Beverages", "Juices", "Prigat", 1, 6.5, 10.0),
//                new ProductDTO(1005, "Butter 200g", "Dairy", "Butter", "Tnuva", 1, 8.0, 5.0),
//                new ProductDTO(1006, "White Rice 1kg", "Grocery", "Rice", "Osem", 1, 4.5, 15.0),

}
