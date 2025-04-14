package Domain;
//import path.to.ProductService;
import Service.ProductService;

import java.util.HashMap;

public class Agreement {
    int supplierId;
    String[] DeliveryDays;
    boolean SelfPickup;
    HashMap<Integer, Product> SupplierProducts; //<123,>

    public Agreement(int supplierId, String[] DeliveryDays, boolean SelfPickup) {
        this.supplierId = supplierId;
        this.DeliveryDays = DeliveryDays;
        this.SelfPickup = SelfPickup;
        SupplierProducts = new HashMap<>();
    }

    public addNewSupplierProduct(int productID){// שיטה שמקבלת ID של מוצר מצד הסופר, יוצרת מופע של אותו המוצר עם כל הפרטים המזהים. ומכניסה אותו למבנה נתונים
        Product newProduct = ProductService.createProduct(productID);//////////לממש
        this.SupplierProducts.put(productID, newProduct);
    }

    public int getSupplierId() {return supplierId;}

    public void updateProductPrice(int productID, int priceNew){// שיטה שמעדכנת את מחיר המוצר באמצעות פונקציה פנימית בתוך "מוצר"
        Product product = SupplierProducts.get(productID);
        product.setPrice(priceNew);//////////לממש
    }

    public void updateProductDiscount(int productID){// שיטה שמעדכנת את ההנחה באמצעות פוקנצייה פנימית בתוך " מוצר"
        Product product = SupplierProducts.get(productID);
        product.setDiscount();//////////לממש
    }


    public void removeProduct(int productID){ // ProductService-שיטה שמוחקת מוצר ממבנה הנתונים במחלקה ששומר את כל המוצרים של הספק. וגם מוחק את המוצר מרשימת המוצרים במבנה נתונים שב-
        Product product = SupplierProducts.get(productID);
        int CatalogNumber = product.getCatalog_Number();//////////לממש
        ProductService.delete_by_catalog(CatalogNumber);//////////לממש
        SupplierProducts.remove(productID);
    }

    public void updateDeliveryDays(String[] newDeliveryDays) {
        this.DeliveryDays = newDeliveryDays;
    }

    public void updateSelfDeliveryOption(){
        this.SelfPickup = !this.SelfPickup;
    }











}
