package Suppliers.Presentation;

import Suppliers.DTO.AgreementDTO;
import Suppliers.DTO.DiscountDTO;
import Suppliers.DTO.ProductSupplierDTO;
import Suppliers.DTO.SupplierDTO;
import Suppliers.dataaccess.DAO.*;

import java.sql.SQLException;

/**
 * DataInitializer is responsible for populating the system with sample data.
 * This includes suppliers, agreements, products (with discount rules), and orders.
 */
public class SuppliersInitializer {
    private final JdbcSupplierDAO supplierDAO;
    private final JdbcAgreementDAO agreementDAO;
    private final JdbcProductSupplierDAO productSupplierDAO ;
    private final JdbcDiscountDAO discountDAO;

    public SuppliersInitializer() {
        this.supplierDAO = new JdbcSupplierDAO();
        this.agreementDAO = new JdbcAgreementDAO();
        this.productSupplierDAO = new JdbcProductSupplierDAO();
        this.discountDAO = new JdbcDiscountDAO();

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
        int supplierID4 =  supplierDAO.insertAndGetID(new SupplierDTO("Heinz", 0126, 9207,"Cash","Prepaid",5678235, "BlaBlaData1@mail.com" ));
        int supplierID5 =  supplierDAO.insertAndGetID(new SupplierDTO("Sano", 0127, 9307,"Bank Transfer","Standing Order",5678236, "BlaBlaData2@mail.com" ));
        int supplierID6 =  supplierDAO.insertAndGetID(new SupplierDTO("Elite", 0121, 9407,"Bank Transfer","Standing Order",5678237, "BlaBlaData3@mail.com" ));
        int supplierID7 =  supplierDAO.insertAndGetID(new SupplierDTO("Neviot", 0122, 9507,"Cash","Prepaid",5678238, "BlaBlaData4@mail.com" ));
        int supplierID8 =  supplierDAO.insertAndGetID(new SupplierDTO("Telma", 0120, 9607,"Bank Transfer","Standing Order",5678239, "BlaBlaData5@mail.com" ));


        int agreementID1 = agreementDAO.insertAndGetID(new AgreementDTO( supplierID1, new String[]{"MONDAY", "WEDNESDAY", "FRIDAY"}, false));
        int agreementID2 = agreementDAO.insertAndGetID(new AgreementDTO( supplierID2, new String[]{"TUESDAY", "THURSDAY"}, true));
        int agreementID3 = agreementDAO.insertAndGetID(new AgreementDTO( supplierID3, new String[]{"SUNDAY", "WEDNESDAY"}, false));
        int agreementID4 = agreementDAO.insertAndGetID(new AgreementDTO( supplierID4, new String[]{"THURSDAY"}, false));
        int agreementID5 = agreementDAO.insertAndGetID(new AgreementDTO( supplierID5, new String[]{"MONDAY"}, true));
        int agreementID6 = agreementDAO.insertAndGetID(new AgreementDTO( supplierID6, new String[]{"TUESDAY"}, false));
        int agreementID7 = agreementDAO.insertAndGetID(new AgreementDTO( supplierID7, new String[]{"WEDNESDAY"}, false));
        int agreementID8 = agreementDAO.insertAndGetID(new AgreementDTO( supplierID8, new String[]{"FRIDAY"}, true));

        productSupplierDAO.insert(new ProductSupplierDTO(0,1004, 1,agreementID1,6.5,"L"));
        productSupplierDAO.insert(new ProductSupplierDTO(1,1005, 2,agreementID2,8.0,"g"));
        productSupplierDAO.insert(new ProductSupplierDTO(2,1006, 3,agreementID3,4.5,"g"));
        productSupplierDAO.insert(new ProductSupplierDTO(3,1007, 1,agreementID4,6,"L"));
        productSupplierDAO.insert(new ProductSupplierDTO(4,1008, 2,agreementID2,9.5,"g"));
        productSupplierDAO.insert(new ProductSupplierDTO(5,1009, 3,agreementID5,20,"g"));
        productSupplierDAO.insert(new ProductSupplierDTO(6,1010, 1,agreementID6,5,"g"));
        productSupplierDAO.insert(new ProductSupplierDTO(7,1011, 2,agreementID7,3,"L"));
        productSupplierDAO.insert(new ProductSupplierDTO(8,1012, 3,agreementID5,7.5,"g"));
        productSupplierDAO.insert(new ProductSupplierDTO(9,1013, 1,agreementID8,12,"g"));


        discountDAO.insert(new DiscountDTO(1004, 0, 1, 20,10.0 ));
        discountDAO.insert(new DiscountDTO(1005, 1, 2, 10,5.0 ));
        discountDAO.insert(new DiscountDTO(1006, 2, 3, 50,15.0 ));
        discountDAO.insert(new DiscountDTO(1007, 0, 4, 20,8.0 ));
        discountDAO.insert(new DiscountDTO(1008, 1, 2, 10,12.0 ));
        discountDAO.insert(new DiscountDTO(1009, 2, 5, 50,20.0 ));
        discountDAO.insert(new DiscountDTO(1010, 0, 6, 20,7.5 ));
        discountDAO.insert(new DiscountDTO(1011, 1, 7, 10,5.0 ));
        discountDAO.insert(new DiscountDTO(1012, 2, 5, 50,10.0 ));
        discountDAO.insert(new DiscountDTO(1013, 0, 8, 20,18.0 ));

    }

    public void clearAllData() {
        // סדר מחיקה: הנחות → מוצרי ספק → הסכמים → ספקים
        discountDAO.clearTable();
        productSupplierDAO.clearTable();
        agreementDAO.clearTable();
        supplierDAO.clearTable();

        System.out.println("✅ All supplier-related data deleted successfully.");
    }



}
