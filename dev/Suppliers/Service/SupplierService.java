//package Suppliers.Service;
//
//import Suppliers.Domain.Supplier;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * SupplierService is responsible for managing supplier-related operations in the system.
// * This includes creating, deleting, retrieving, and validating suppliers, as well as
// * managing agreements associated with each supplier.
// */
//public class SupplierService {
//    private final List<Supplier> supplierList;
//
//    /**
//     * Constructs a new SupplierService with an empty list of suppliers.
//     */
//    public SupplierService() {
//        supplierList = new ArrayList<>();
//    }
//
//    /**
//     * Creates a new supplier and adds it to the supplier list.
//     *
//     * @param supplierName      the name of the supplier
//     * @param supplier_id       unique ID of the supplier
//     * @param company_id        the company ID associated with the supplier
//     * @param bankAccount       the bank account number of the supplier
//     * @param paymentMethod     the method of payment (e.g., Cash, Bank Transfer)
//     * @param phoneNumber       supplier's contact number
//     * @param email             supplier's email address
//     * @param paymentCondition  payment condition (e.g., Prepaid, Standing Order)
//     */
//    public void createSupplier(String supplierName, int supplier_id, int company_id, int bankAccount, String paymentMethod, long phoneNumber, String email, String paymentCondition) {
//        Supplier supplier = new Supplier(supplierName, supplier_id, company_id, bankAccount, paymentMethod, phoneNumber, email, paymentCondition);
//        this.supplierList.add(supplier);
//    }
//
//    /**
//     * Deletes a supplier from the system by ID.
//     * Also removes all agreements associated with that supplier.
//     *
//     * @param supplier_ID the ID of the supplier to delete
//     */
//    public void deleteSupplier(int supplier_ID) {
//        Supplier supplier = getSupplierById(supplier_ID);
//        if (supplier != null) {
//            deleteAllAgreementFromSupplier(supplier_ID);
//            supplierList.remove(getSupplierById(supplier_ID));
//        }
//    }
//
//    /**
//     * Checks whether there is at least one supplier in the system.
//     * Used to validate order creation feasibility.
//     *
//     * @return true if suppliers exist, false otherwise
//     */
//    public boolean hasSuppliers() {
//        return !supplierList.isEmpty();
//    }
//
//    /**
//     * Checks if a supplier with the given ID exists.
//     *
//     * @param id the supplier's ID to check
//     * @return true if the supplier exists, false otherwise
//     */
//    public boolean thereIsSupplier(int id){
//        for (Supplier supplier : supplierList) {
//            if (supplier.getSupplier_id() == id) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    /**
//     * Retrieves a supplier object by ID.
//     *
//     * @param id the ID of the supplier to retrieve
//     * @return the Supplier object if found, otherwise null
//     */
//    public Supplier getSupplierById(int id) {
//        for(Supplier supplier : supplierList){
//            if(supplier.getSupplier_id() == id){
//                return supplier;
//            }
//        }
//        return null;
//    }
//
//    /**
//     * Deletes all agreements associated with a given supplier.
//     *
//     * @param supplier_ID the ID of the supplier whose agreements should be removed
//     * @return array of removed agreement IDs
//     */
//    public int[] deleteAllAgreementFromSupplier(int supplier_ID) {
//        for (Supplier supplier : supplierList) {
//            if (supplier.getSupplier_id() == supplier_ID) {
//                return supplier.removeAllAgreementFromSupplier();
//            }
//        }
//        return new int[0]; // If supplier with the given ID was not found
//    }
//}
