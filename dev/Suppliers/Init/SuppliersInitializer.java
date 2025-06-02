package Suppliers.Init;

import Suppliers.DAO.*;
import Suppliers.DTO.AgreementDTO;
import Suppliers.DTO.DiscountDTO;
import Suppliers.DTO.ProductSupplierDTO;
import Suppliers.DTO.SupplierDTO;

import java.sql.SQLException;
import java.util.LinkedHashMap;

/**
 * DataInitializer is responsible for populating the system with sample data.
 * This includes suppliers, agreements, products (with discount rules), and orders.
 */
public class SuppliersInitializer {
    private final JdbcSupplierDAO supplierDAO;
    private final JdbcAgreementDAO agreementDAO;
    private final JdbcProductSupplierDAO productSupplierDAO;
    private final JdbcDiscountDAO discountDAO;
    private final JdbcOrderDAO orderDAO;

    public SuppliersInitializer() {
        this.supplierDAO = new JdbcSupplierDAO();
        this.agreementDAO = new JdbcAgreementDAO();
        this.productSupplierDAO = new JdbcProductSupplierDAO();
        this.discountDAO = new JdbcDiscountDAO();
        this.orderDAO = new JdbcOrderDAO();
    }

    public static void initializeAllTables() {
        new JdbcSupplierDAO().createTableIfNotExists();
        new JdbcAgreementDAO().createTableIfNotExists();
        new JdbcProductSupplierDAO().createProductSupplierTableIfNotExists();
        new JdbcDiscountDAO().createTableIfNotExists();
        new JdbcOrderDAO().createTableIfNotExists();
    }

    public LinkedHashMap<Integer, Integer> initializeDatabase(boolean withSampleData) throws SQLException {
        agreementDAO.createTableIfNotExists();

        if (withSampleData) {
            return insertSampleData();  // מחזיר את המפה
        }

        return new LinkedHashMap<>(); // במקרה שאין נתונים לדוגמה
    }

    private LinkedHashMap<Integer, Integer> insertSampleData() throws SQLException {
        int supplierID1 = supplierDAO.insertAndGetID(new SupplierDTO("Prigat", 0123, 9987, "Cash", "Prepaid", 5551234, "data@mail.com"));
        int supplierID2 = supplierDAO.insertAndGetID(new SupplierDTO("Tnuva", 0124, 9007, "Bank Transfer", "Standing Order", 5671234, "OneMoreData@mail.com"));
        int supplierID3 = supplierDAO.insertAndGetID(new SupplierDTO("Osem", 0125, 9107, "Bank Transfer", "Standing Order", 5678234, "BlaBlaData@mail.com"));
        int supplierID4 = supplierDAO.insertAndGetID(new SupplierDTO("Heinz", 0126, 9207, "Cash", "Prepaid", 5678235, "BlaBlaData1@mail.com"));
        int supplierID5 = supplierDAO.insertAndGetID(new SupplierDTO("Sano", 0127, 9307, "Bank Transfer", "Standing Order", 5678236, "BlaBlaData2@mail.com"));
        int supplierID6 = supplierDAO.insertAndGetID(new SupplierDTO("Elite", 0121, 9407, "Bank Transfer", "Standing Order", 5678237, "BlaBlaData3@mail.com"));
        int supplierID7 = supplierDAO.insertAndGetID(new SupplierDTO("Neviot", 0122, 9507, "Cash", "Prepaid", 5678238, "BlaBlaData4@mail.com"));
        int supplierID8 = supplierDAO.insertAndGetID(new SupplierDTO("Telma", 0120, 9607, "Bank Transfer", "Standing Order", 5678239, "BlaBlaData5@mail.com"));

        int agreementID1 = agreementDAO.insertAndGetID(new AgreementDTO(supplierID1, new String[]{"MONDAY, WEDNESDAY, FRIDAY"}, false));
        int agreementID2 = agreementDAO.insertAndGetID(new AgreementDTO(supplierID2, new String[]{"TUESDAY, THURSDAY"}, true));
        int agreementID3 = agreementDAO.insertAndGetID(new AgreementDTO(supplierID3, new String[]{"SUNDAY", "WEDNESDAY"}, false));
        int agreementID4 = agreementDAO.insertAndGetID(new AgreementDTO(supplierID4, new String[]{"THURSDAY"}, false));
        int agreementID5 = agreementDAO.insertAndGetID(new AgreementDTO(supplierID5, new String[]{"MONDAY"}, true));
        int agreementID6 = agreementDAO.insertAndGetID(new AgreementDTO(supplierID6, new String[]{"TUESDAY"}, false));
        int agreementID7 = agreementDAO.insertAndGetID(new AgreementDTO(supplierID7, new String[]{"WEDNESDAY"}, false));
        int agreementID8 = agreementDAO.insertAndGetID(new AgreementDTO(supplierID8, new String[]{"FRIDAY"}, true));

        LinkedHashMap<Integer, Integer> supplierIDAndAgreementsID = new LinkedHashMap<>();
        supplierIDAndAgreementsID.put(supplierID1, agreementID1);
        supplierIDAndAgreementsID.put(supplierID2, agreementID2);
        supplierIDAndAgreementsID.put(supplierID3, agreementID3);
        supplierIDAndAgreementsID.put(supplierID4, agreementID4);
        supplierIDAndAgreementsID.put(supplierID5, agreementID5);
        supplierIDAndAgreementsID.put(supplierID6, agreementID6);
        supplierIDAndAgreementsID.put(supplierID7, agreementID7);
        supplierIDAndAgreementsID.put(supplierID8, agreementID8);

        productSupplierDAO.insert(new ProductSupplierDTO( 1004,0, supplierID1, agreementID1, 6.5, "L"));
        productSupplierDAO.insert(new ProductSupplierDTO( 1005,1, supplierID2, agreementID2, 8.0, "g"));
        productSupplierDAO.insert(new ProductSupplierDTO( 1006,2, supplierID3, agreementID3, 4.5, "g"));
        productSupplierDAO.insert(new ProductSupplierDTO( 1007, 3,supplierID4, agreementID4, 6, "L"));
        productSupplierDAO.insert(new ProductSupplierDTO( 1008,4, supplierID5, agreementID5, 9.5, "g"));
        productSupplierDAO.insert(new ProductSupplierDTO( 1009,5, supplierID6, agreementID6, 20, "g"));
        productSupplierDAO.insert(new ProductSupplierDTO( 1010,6, supplierID7, agreementID7, 5, "g"));
        productSupplierDAO.insert(new ProductSupplierDTO( 1011, 7,supplierID8, agreementID8, 3, "L"));
        productSupplierDAO.insert(new ProductSupplierDTO( 1012,8, supplierID8, agreementID8, 7.5, "g"));
        productSupplierDAO.insert(new ProductSupplierDTO(1013,9, supplierID8, agreementID8, 12, "g"));

        discountDAO.insert(new DiscountDTO(1004, supplierID1, agreementID1, 20, 10.0));
        discountDAO.insert(new DiscountDTO(1005, supplierID2, agreementID2, 10, 5.0));
        discountDAO.insert(new DiscountDTO(1006, supplierID3, agreementID3, 50, 15.0));
        discountDAO.insert(new DiscountDTO(1007, supplierID4, agreementID4, 20, 8.0));
        discountDAO.insert(new DiscountDTO(1008, supplierID5, agreementID5, 10, 12.0));
        discountDAO.insert(new DiscountDTO(1009, supplierID6, agreementID6, 50, 20.0));
        discountDAO.insert(new DiscountDTO(1010, supplierID7, agreementID7, 20, 7.5));
        discountDAO.insert(new DiscountDTO(1011, supplierID8, agreementID8, 10, 5.0));
        discountDAO.insert(new DiscountDTO(1012, supplierID8, agreementID8, 50, 10.0));
        discountDAO.insert(new DiscountDTO(1013, supplierID8, agreementID8, 20, 18.0));

        return supplierIDAndAgreementsID;
    }

    public void clearAllData() {
        // סדר מחיקה: הנחות → מוצרי ספק → הסכמים → ספקים
        discountDAO.clearTable();
        productSupplierDAO.clearTable();
        agreementDAO.clearTable();
        supplierDAO.clearTable();
        orderDAO.clearTable();



        System.out.println("✅ All supplier-related data deleted successfully.");
    }



}
