package Domain;

import Service.ProductService;

import java.util.HashMap;

public class Agreement {
    int supplierId;
    String[] DeliveryDays;
    boolean SelfPickup;
    HashMap<Integer, Product> SupplierProducts;

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

    public void updateProductPrice(int productID){// שיטה שמעדכנת את מחיר המוצר באמצעות פונקציה פנימית בתוך "מוצר"
        Product product = SupplierProducts.get(productID);
        product.setPrice();//////////לממש
    }

    public void updateProductDiscount(int productID){// שיטה שמעדכנת את ההנחה באמצעות פוקנצייה פנימית בתוך " מוצר"
        Product product = SupplierProducts.get(productID);
        product.setDiscount();//////////לממש
    }


    public void removeProduct(int productID){ // ProductService-שיטה שמוחקת מוצר ממבנה הנתונים במחלקה ששומר את כל המוצרים של הספק. וגם מוחק את המוצר מרשימת המוצרים במבנה נתונים שב-
        Product product = SupplierProducts.get(productID);
        int CatalogNumber = product.getCatalogNumber;//////////לממש
        ProductService.deleteSupplierProducts(CatalogNumber);//////////לממש
        SupplierProducts.remove(productID);
    }

    public void updateDeliveryDays(String[] newDeliveryDays) {
        this.DeliveryDays = newDeliveryDays;
    }

    public void updateSelfDeliveryOption(){
        this.SelfPickup = !this.SelfPickup;
    }











}
