package Service;

import Domain.Agreement;
import Domain.Supplier;

import java.util.HashMap;
import java.util.List;

public class SupplierService {
    private List<Supplier> supplierList;
    public AgreementService agreementService;

    public void createSupplier(String supplierName, int supplier_id, int company_id, int bankAccount, String paymentMethod, int phoneNumber, String email, HashMap<Integer, Agreement> agreements) {
        Supplier supplier = new Supplier(supplierName, supplier_id, company_id, bankAccount, paymentMethod, phoneNumber, email, agreements);
        this.supplierList.add(supplier);

    }
    public boolean deleteSupplier(int id) {
        return supplierList.remove(searchSupplier_by_id(id));
    }
    public Supplier searchSupplier_by_id(int id) {
        for(Supplier supplier : supplierList){
            if(supplier.getSupplier_id()==id){
                return supplier;
            }
        }
        return null;
    }

    public void deleteAgreementFromSupplier(int supplier_ID, int agreement_ID) {
        Supplier supplier = supplierList.get(supplier_ID);
        if (supplier!= null) {
            supplier.removeAgreement(agreement_ID);
            this.agreementService.deleteAgreementWithSupplier(agreement_ID);
        }
    }
}
