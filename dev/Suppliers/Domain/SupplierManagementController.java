package Suppliers.Domain;

import Suppliers.DTO.AgreementDTO;
import Suppliers.DTO.SupplierDTO;

import java.sql.SQLException;
import java.util.List;

public class SupplierManagementController {

    public ISupplierRepository supplierRepository;

    public void createSupplier(SupplierDTO supplierDTO) throws SQLException {
        supplierRepository.createSupplier(supplierDTO);
    }

    public void deleteSupplier(int supplierId) throws SQLException {
        supplierRepository.deleteSupplier(supplierId);
    }

    public List<SupplierDTO> getAllSuppliersDTOs() throws SQLException {
        List<SupplierDTO> supplierDTOList =  supplierRepository.getAllSuppliersDTOs();
        return supplierDTOList;
    }

}
