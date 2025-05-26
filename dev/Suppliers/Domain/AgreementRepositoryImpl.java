package Suppliers.Domain;

import Suppliers.DTO.AgreementDTO;
import Suppliers.dataaccess.DAO.IAgreementDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class AgreementRepositoryImpl implements IAgreementRepository {

    private static final int MAX_CACHE_SIZE = 500;
    /** Identity Map: מפתח = ID, ערך = Entity */
    private final HashMap<Integer, Agreement> cache = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(HashMap.Entry<Integer, Agreement> eldest) {
            return size() > MAX_CACHE_SIZE;
        }
    };

    private final IAgreementDAO agreementDao;



    public AgreementRepositoryImpl(IAgreementDAO dao) {
        this.agreementDao = dao;
    }

    @Override
    public void createAgreementWithSupplier(AgreementDTO agreementDTO) {
        try {
            agreementDao.insert(agreementDTO);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Agreement getAgreementByID(int id) {
        // 1. קודם מחפש במטמון
        Agreement cached = cache.get(id);
        if (cached != null) return cached;

        // 2. לא קיים? מביא מה-DB דרך DAO
        try {
            AgreementDTO dto = this.agreementDao.getById(id);
            if (dto == null) return null;                    // לא נמצא ב-DB
            Agreement entity = new Agreement(dto.getAgreement_ID(), dto.getSupplier_ID(), dto.getDeliveryDays(), dto.isSelfPickup());
            cache.put(id, entity);                           // מוסיף למטמון
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("DB error", e);
        }
    }

    @Override
    public void deleteAgreementWithSupplier(int agreementId, int supplierId) { //לשקול להוסיף עמודה הבטבלת עם הסכם עם ID ספק
        try {
            agreementDao.deleteById(agreementId);   // מחיקה פיזית מה-DB
            cache.remove(agreementId);     // הסרה מה-Identity Map
        } catch (SQLException e) {
            throw new RuntimeException("שגיאה במחיקת הסכם מהמסד", e);
        }
    }

    @Override
    public List<AgreementDTO> getBySupplierId(int supplierId) {
        List<AgreementDTO> agreementDTOList;
        try {
            agreementDTOList = agreementDao.getBySupplierId(supplierId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return agreementDTOList;
    }

    @Override
    public void updateDeliveryDays(int agreementId, String[] deliveryDays) {
        try {
            agreementDao.updateDeliveryDays(agreementId, deliveryDays);
            if (cache.containsKey(agreementId)) {
                cache.get(agreementId).setDeliveryDays(deliveryDays);
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error", e);
        }
    }

    @Override
    public void updateSelfPickup(int agreementId, boolean selfPickup) {
        try {
            agreementDao.updateSelfPickup(agreementId, selfPickup);
            if (cache.containsKey(agreementId)) {
                cache.get(agreementId).setSelfPickup(selfPickup);
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error", e);
        }

    }

    @Override
    public List<Agreement> getAllAgreement() {
        try {
            List<AgreementDTO> dtos = agreementDao.getAllAgreement();
            List<Agreement> agreements = new ArrayList<>();

            for (AgreementDTO dto : dtos) {
                Agreement agreement = new Agreement(dto.getAgreement_ID(), dto.getSupplier_ID(), dto.getDeliveryDays(), dto.isSelfPickup());
                agreements.add(agreement);

                // שמירה ב-Cache
                cache.put(agreement.getAgreement_ID(), agreement);
            }

            return agreements;
        } catch (SQLException e) {
            throw new RuntimeException("error", e);
        }
    }
}
