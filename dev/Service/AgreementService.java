package Service;

import Domain.Agreement;

import java.util.HashMap;

public class AgreementService {
    private HashMap<String, Agreement> agreementHashMap;

    public AgreementService() {
        this.agreementHashMap = new HashMap<>();
    }

    // יצירת הסכם עם ספק
    public void createAgreementWithSupplier(String supplierId) {
        Agreement agreement = new Agreement(supplierId); // הנחה שיש קונסטרקטור כזה
        this.agreementHashMap.put(supplierId, agreement);
    }

    // מחיקת הסכם עם ספק
    public void deleteAgreementWithSupplier(String supplierId) {
        agreementHashMap.remove(supplierId);
    }

    // חיפוש הסכם לפי מזהה ספק
    public Agreement findAgreementWithSupplier(String supplierId) {
        return agreementHashMap.get(supplierId);
    }
}
