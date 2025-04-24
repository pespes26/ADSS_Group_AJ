package Service;
import Domain.Supplier;
import java.util.ArrayList;
import java.util.List;

public class SupplierService {
    private final List<Supplier> supplierList;

    public SupplierService() {
        supplierList = new ArrayList<>();
    }


    public void  createSupplier(String supplierName, int supplier_id, int company_id, int bankAccount, String paymentMethod, int phoneNumber, String email, String paymentCondition) {
        Supplier supplier = new Supplier(supplierName, supplier_id, company_id, bankAccount, paymentMethod, phoneNumber, email, paymentCondition);
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


    public int[] deleteAllAgreementFromSupplier(int supplier_ID) {
        for (Supplier supplier : supplierList) {
            if (supplier.getSupplier_id() == supplier_ID) {
                return supplier.removeAllAgreementFromSupplier();
            }
        }
        return new int[0]; // אם לא נמצא ספק עם ה-ID הנתון
    }

}
