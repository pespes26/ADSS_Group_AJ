package Service;

import Domain.Supplier;

import java.util.List;

public class SupplierService {
    private List<Supplier> supplierList;
    public AgreementService agreementService;

//    public void createSupplier(String supplierName, int supplier_id, int company_id, int bankAccount, String paymentMethod, int phoneNumber, String email, String paymentDay, HashMap<Integer, Agreement> agreements) {
//        Supplier supplier = new Supplier(supplierName, supplier_id, company_id, bankAccount, paymentMethod, phoneNumber, email, paymentDay, agreements);
//        this.supplierList.add(supplier);
//    }

    public Supplier createSupplier(String supplierName, int supplier_id, int company_id, int bankAccount, String paymentMethod, int phoneNumber, String email, String paymentDay) {
        Supplier supplier = new Supplier(supplierName, supplier_id, company_id, bankAccount, paymentMethod, phoneNumber, email, paymentDay);
        this.supplierList.add(supplier);
        return supplier;
    }

    public void deleteSupplier(int supplier_ID) {
        Supplier supplier = searchSupplier_by_id(supplier_ID);
        if (supplier != null) {
            deleteAllAgreementFromSupplier(supplier_ID);
            supplierList.remove(searchSupplier_by_id(supplier_ID));
        }
    }

    public Supplier searchSupplier_by_id(int id) {
        for(Supplier supplier : supplierList){
            if(supplier.getSupplier_id()==id){
                return supplier;
            }
        }
        return null;
    }

    public void deleteOneAgreementFromSupplier(int supplier_ID, int agreement_ID) {
        Supplier supplier = supplierList.get(supplier_ID);
        if (supplier!= null) {
            supplier.removeAgreement(agreement_ID);
            this.agreementService.deleteAgreementWithSupplier(agreement_ID);
        }
    }

    public void deleteAllAgreementFromSupplier(int supplier_ID) {
        Supplier supplier = supplierList.get(supplier_ID);
        if (supplier != null) {
            int[] agreementIDS = supplier.removeAllAgreementFromSupplier();
            for (int agreementID : agreementIDS) {
                this.agreementService.deleteAgreementWithSupplier(agreementID);
            }
        }
    }
}
