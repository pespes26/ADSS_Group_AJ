package Suppliers.Domain;

import Suppliers.DTO.AgreementDTO;

import java.util.List;

public interface IAgreementRepository {
    void createAgreementWithSupplier(AgreementDTO agreementDTO);
    Agreement getAgreementByID(int id);
    void deleteAgreementWithSupplier(int agreementId);
    List<AgreementDTO> getBySupplierId(int supplierId);
    void updateDeliveryDays(int agreementId, String[] deliveryDays);
    void updateSelfPickup(int agreementId, boolean selfPickup);
    List<Agreement> getAllAgreement();
}
