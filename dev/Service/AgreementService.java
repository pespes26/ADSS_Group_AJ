package Service;

import Domain.Agreement;
import Domain.Product;

import java.util.HashMap;

public class AgreementService {
    private HashMap<Integer, Agreement> agreementHashMap;
    public ProductService productService; // new!!!!

    public AgreementService() {
        this.agreementHashMap = new HashMap<>();
    }

    // יצירת הסכם עם ספק
    public void createAgreementWithSupplier(int agreement_ID, int supplier_ID, String[] DeliveryDays, boolean SelfPickup) {
        Agreement agreement = new Agreement(agreement_ID, supplier_ID, DeliveryDays, SelfPickup); // הנחה שיש קונסטרקטור כזה
        this.agreementHashMap.put(agreement_ID, agreement);
    }

    // חיפוש הסכם לפי מזהה ספק
    public Agreement findAgreementWithSupplier(int agreement_ID) {
        return agreementHashMap.get(agreement_ID);
    }


    //========================new Functions=================
    public void addProductToAgreement(int productID, int agreementID) {
        Agreement agreement = agreementHashMap.get(agreementID);
        if (agreement != null) {
            Product newProduct = productService.createProduct(productID);//////////רן צריך לממש
            agreement.addNewSupplierProduct(productID, newProduct);
        }
    }

    public void removeProductFromAgreement(int agreement_ID, int product_ID) { /// מוחקת מוצר בתוך הסכם. ולאחר מכן מוחקת את המוצר בתוך ProductService
        Agreement agreement = findAgreementWithSupplier(agreement_ID);
        if (agreement != null) {
            int catalogNumber = agreement.removeProduct(product_ID);
            productService.delete_by_catalog(catalogNumber);
        }
    }

    public void deleteAgreementWithSupplier(int agreement_ID) { // מחיקת הסכם אשר גוררת מחיקת כל המוצרים שבהסכם.
        Agreement agreement = findAgreementWithSupplier(agreement_ID);
        if (agreement != null) {
            int[] allCatalogNumbers = agreement.removeAllProducts(); //מוחק את כל המוצרים בהסכם, ושמור את המספר הקטלוגי שלהם
            for (int catalogNumber : allCatalogNumbers) {
                productService.delete_by_catalog(catalogNumber);
            }
        }
    }

}
