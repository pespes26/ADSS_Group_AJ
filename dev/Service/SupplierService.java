package Service;

import Domain.Supplier;

import java.util.List;

public class SupplierService {
    private List<Supplier> supplierList;
    public void createSupplier() {
        Supplier supplier = new Supplier();
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
}
