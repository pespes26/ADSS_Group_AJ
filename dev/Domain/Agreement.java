package Domain;
//import path.to.ProductService;

import java.util.HashMap;
import java.util.Map;

public class Agreement {
    int agreement_ID; //new
    int supplier_ID;
    String[] DeliveryDays;
    boolean SelfPickup;
    HashMap<Integer, Product> SupplierProducts; //<123,>

    public Agreement(int agreement_ID, int supplier_ID, String[] DeliveryDays, boolean SelfPickup) {
        this.agreement_ID = agreement_ID;
        this.supplier_ID = supplier_ID;
        this.DeliveryDays = DeliveryDays;
        this.SelfPickup = SelfPickup;
        SupplierProducts = new HashMap<>();
    }

    public void addNewSupplierProduct(int productID, Product newProduct){// שיטה שמקבלת ID של מוצר מצד הסופר, יוצרת מופע של אותו המוצר עם כל הפרטים המזהים. ומכניסה אותו למבנה נתונים
        this.SupplierProducts.put(productID, newProduct);
    }

    public int getSupplier_ID() {return supplier_ID;}

    public void updateProductPrice(int productID, int priceNew){// שיטה שמעדכנת את מחיר המוצר באמצעות פונקציה פנימית בתוך "מוצר"
        Product product = SupplierProducts.get(productID);
        product.setPrice(priceNew);//////////לממש
    }

    public void updateProductDiscount(int productID){// שיטה שמעדכנת את ההנחה באמצעות פוקנצייה פנימית בתוך " מוצר"
        Product product = SupplierProducts.get(productID);
        product.setDiscount();//////////לממש
    }

    public void updateDeliveryDays(String[] newDeliveryDays) {
        this.DeliveryDays = newDeliveryDays;
    }

    public void updateSelfDeliveryOption(){
        this.SelfPickup = !this.SelfPickup;
    }

    public int removeProduct(int productID){ // מוחקת את המוצר מההסכם. ומחזירה את המספר הקטלוגי שלו לצורך מחיקה בתוך ProductService
        Product product = SupplierProducts.get(productID);
        int CatalogNumber = product.getCatalog_Number();//////////לממש
        SupplierProducts.remove(productID);
        return CatalogNumber;
    }


    //========================new Functions=================
    public int[] removeAllProducts(){
        int size = this.SupplierProducts.size();
        int[] productCatalogNumbers = new int[size];

        int index = 0;
        for (Map.Entry<Integer,Product> entry : new HashMap<>(this.SupplierProducts).entrySet()) { // העתקת מבנה הנתונים לעותק ומעבר על כל זוג.
        Product product = entry.getValue(); // שמירת המוצר הנוכחי

        int catalog_number = product.getCatalog_Number(); //שליפת המספר הקטלוגי
        productCatalogNumbers[index] = catalog_number;//שמירת המספר הקטלוגי
        index++;//העלאת האינדקס
        SupplierProducts.remove(entry.getKey());// מחיקה ממבנה הנתונים המקורי
        }
        return productCatalogNumbers;
    }











}
