package Suppliers.Repository;

import Suppliers.DTO.SupplierDTO;
import Suppliers.Domain.Supplier;

import java.sql.SQLException;
import java.util.List;

public interface ISupplierRepository {
    void createSupplier(SupplierDTO supplierDTO) throws SQLException;
    void deleteSupplier(int supplier_ID);
     Supplier getSupplierById(int id);
     void deleteAllAgreementFromSupplier(int supplier_ID) throws SQLException;
    List<Supplier> getAllSuppliers();
    List<SupplierDTO> getAllSuppliersDTOs() throws SQLException;

}
