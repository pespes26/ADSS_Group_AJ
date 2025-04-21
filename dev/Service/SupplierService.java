package Service;
import Domain.Supplier;
import java.util.ArrayList;
import java.util.List;

public class SupplierService {
    private List<Supplier> supplierList;

    public SupplierService() {
        supplierList = new ArrayList<>();
    }


    public void  createSupplier(String supplierName, int supplier_id, int company_id, int bankAccount, String paymentMethod, int phoneNumber, String email, String paymentDay) {
        Supplier supplier = new Supplier(supplierName, supplier_id, company_id, bankAccount, paymentMethod, phoneNumber, email, paymentDay);
        this.supplierList.add(supplier);
    }

    public void deleteSupplier(int supplier_ID) {
        Supplier supplier = getSupplierById(supplier_ID);
        if (supplier != null) {
            deleteAllAgreementFromSupplier(supplier_ID);
            supplierList.remove(getSupplierById(supplier_ID));
        }
    }


    public boolean hasSuppliers() { //use to check if in the first order we have supplier
        return !supplierList.isEmpty();
    }


    public boolean thereIsSupplier(int id){
        for (Supplier supplier : supplierList) {
            if (supplier.getSupplier_id() == id) {
                return true;
            }
        }
        return false;
    }

    public Supplier getSupplierById(int id) {
        for(Supplier supplier : supplierList){
            if(supplier.getSupplier_id()==id){
                return supplier;
            }
        }
        return null;
    }


    public boolean deleteOneAgreementFromSupplier(int supplier_ID, int agreement_ID) {
        Supplier supplier = supplierList.get(supplier_ID);
        if (supplier!= null) {
            supplier.removeAgreement(agreement_ID);
            return true;
        }
        return false;
    }

     ///new - use for check supplier
    public List<Supplier> getAllSuppliers() {
        return new ArrayList<>(supplierList);
    }


    public int[] deleteAllAgreementFromSupplier(int supplier_ID) {
        Supplier supplier = supplierList.get(supplier_ID);
        if (supplier != null) {
            int[] agreementIDS = supplier.removeAllAgreementFromSupplier();
            return agreementIDS;
        }
        return new int[0];
    }
}
