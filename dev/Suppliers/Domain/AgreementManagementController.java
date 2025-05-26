package Suppliers.Domain;

import Suppliers.DTO.AgreementDTO;

import java.util.List;

public class AgreementManagementController {
    public IAgreementRepository agreementRepository;

    public void createAgreementWithSupplier(AgreementDTO agreementDTO) {
        agreementRepository.createAgreementWithSupplier(agreementDTO);
    }
    public void deleteAgreementWithSupplier(int agreementId, int supplierId){
        agreementRepository.deleteAgreementWithSupplier(agreementId,supplierId);
    }

    public void getAgreementByID(int id){// מחזירים לפה מופע ממש של הסכם בהינתן ID
        Agreement agreement = agreementRepository.getAgreementByID(id);
    }

    public void setDeliveryDays(int agreementID, String[] deliveryDays) {
        agreementRepository.updateDeliveryDays(agreementID,deliveryDays);
    }

    public void setSelfPickup(int agreementID ,boolean selfPickup) {
        agreementRepository.updateSelfPickup(agreementID,selfPickup);
    }

    public void getAgreementsBySupplierID(int supplierId){
        List<AgreementDTO> agreementDTOList =  agreementRepository.getBySupplierId(supplierId);
    }

}
