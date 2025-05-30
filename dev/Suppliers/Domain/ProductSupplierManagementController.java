package Suppliers.Domain;

import Suppliers.DTO.DiscountDTO;
import Suppliers.DTO.ProductSupplierDTO;

import java.sql.SQLException;
import java.util.List;

public class ProductSupplierManagementController {
    public IProductSupplierRepository psRepository;

    public void createProductSupplier(ProductSupplierDTO productSupplierDTO) throws SQLException {
        psRepository.createProductSupplier(productSupplierDTO);
    }

    public void deleteProductSupplier(int productID, int catalogNumber, int supplierID,int agreementID ) throws SQLException {
        psRepository.deleteProductSupplier(productID, catalogNumber,  supplierID, agreementID );
    }

    public ProductSupplierDTO getProductSupplier(int productID, int catalogNumber, int supplierID) throws SQLException {
        return psRepository.getProductSupplier(productID, catalogNumber, supplierID);
    }

    public List<ProductSupplierDTO> getProductSuppliers(int catalogNumber, int supplierID) throws SQLException {
        return psRepository.getProductDTOsFromAgreement(catalogNumber, supplierID);
    }

    public void setProductPrice(int productID, int catalogNumber, int supplierID, int newPrice) throws SQLException {
        psRepository.setProductPrice(productID, catalogNumber, supplierID, newPrice);
    }

    public void updateUnit(int productID, int catalogNumber, int agreementID, String newUnit) throws SQLException {
        psRepository.updateProductUnit( catalogNumber, newUnit, agreementID);
    }

    public void addDiscount(DiscountDTO discountDTO) throws SQLException {
        psRepository.updateOrAddDiscountRule(discountDTO);
    }

}

