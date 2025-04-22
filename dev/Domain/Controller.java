package Domain;

import Service.AgreementService;
import Service.OrderService;
import Service.ProductService;
import Service.SupplierService;

import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class Controller {
    private final AgreementService agreementService;
    private final OrderService orderService;
    private final ProductService productService  ;
    private final SupplierService supplierService;

    public Controller(){
        agreementService = new AgreementService();
        orderService = new OrderService();
        productService = new ProductService();
        supplierService = new SupplierService();
    }

//===================================SupplierService================================================================\

    public void createSupplier(String supplierName, int supplier_id, int company_id, int bankAccount, String paymentMethod, int phoneNumber, String email, String paymentCondition){
        supplierService.createSupplier(supplierName, supplier_id,  company_id,  bankAccount, paymentMethod, phoneNumber,  email, paymentCondition);
    }
    //-------------------------
    public void deleteSupplier(int supplier_ID){
        if (supplierService.thereIsSupplier(supplier_ID)){
            deleteAllAgreementFromSupplier(supplier_ID);
            supplierService.deleteSupplier(supplier_ID);
        }
    }
    //--------------------------
    public Supplier getSupplierById(int id){
        Supplier supplier = supplierService.getSupplierById(id);
        if (supplier != null){
            return supplier;
        }
        return null;
    }
    //--------------------------

    public void deleteOneAgreementFromSupplier(int supplier_ID, int agreement_ID){// אולי למחוק
          if (this.supplierService.deleteOneAgreementFromSupplier(supplier_ID, agreement_ID)){
              this.agreementService.deleteAgreementWithSupplier(agreement_ID);
          }
    }

    //--------------------------
    public void deleteAllAgreementFromSupplier(int supplier_ID){
        int[] agreementIDS = supplierService.deleteAllAgreementFromSupplier(supplier_ID);
        for (int agreementID : agreementIDS) {
            this.agreementService.deleteAgreementWithSupplier(agreementID);
        }
    }

    //--------------------------
    public boolean thereIsSupplier(int id){
        return supplierService.thereIsSupplier(id);
    }


    //--------------------------
    public boolean hasSuppliers() {//use for order (check if possible to make order)
        return supplierService.hasSuppliers();
    }


//===================================AgreementService================================================================\

    public void createAgreement(int agreement_ID, int supplier_ID, String[] deliveryDays, boolean selfPickup){
        Supplier supplier = supplierService.getSupplierById(supplier_ID);
        Agreement agreement = this.agreementService.createAgreementWithSupplier(agreement_ID, supplier_ID, deliveryDays, selfPickup);
        supplier.addNewAgreement(agreement);
    }

    //--------------------------
    public void deleteAgreement(int agreement_ID) {
        int[] allCatalogNumbers = this.agreementService.deleteAgreementWithSupplier(agreement_ID);
        for (int catalogNumber : allCatalogNumbers) {
            productService.delete_by_catalog(catalogNumber); // Delete each product from the product system
        }
    }
    //--------------------------

    public void addProductToAgreement(int product_id, Product newProduct, int agreementID){
        Agreement agreement = this.agreementService.getAgreementByID(agreementID);
        if (agreement != null && newProduct !=null) {
            agreement.addNewSupplierProduct(product_id, newProduct); // Add the product to the agreement
        }
    }
    //--------------------------
    public void deleteAgreementWithSupplier(int agreement_ID){ //אולי למחוק
        this.agreementService.deleteAgreementWithSupplier(agreement_ID);
    }
    //--------------------------
    public void removeProductFromAgreement(int agreementID, int product_id){
        Integer catalogNumber = this.agreementService.removeProductFromAgreement(agreementID, product_id);
        if (catalogNumber != null) {
            productService.delete_by_catalog(catalogNumber); // Remove the product from the system
        }
    }

    public boolean thereIsAgreement(int agreement_ID){
        return this.agreementService.thereIsAgreement(agreement_ID);
    }

    public void updateDeliveryDays(int agreementID, String[] newDays){
        Agreement agreement  = this.agreementService.getAgreementByID(agreementID);
        if (agreement != null) {
            agreement.updateDeliveryDays(newDays);
        }
    }

    public boolean toggleSelfPickup(int agreementID){
        Agreement agreement  = this.agreementService.getAgreementByID(agreementID);
            return agreement.updateSelfDeliveryOption();

    }


//===================================ProductService================================================================\
    public Product createProduct(int catalog_Number, int product_id, double price, String unitsOfMeasure){
        return productService.createProduct(catalog_Number, product_id, price, unitsOfMeasure);
    }

    public void deleteProductByID(int id){
        productService.delete_by_id(id);
    }

    public void deleteProductByCatalog(int catalog_Number, int agreementID){
        Agreement agreement = this.agreementService.getAgreementByID(agreementID);
        if (agreement != null) {
            agreement.removeProductByProductCatalogNumber(catalog_Number);
            productService.delete_by_catalog(catalog_Number); //מחיקת מוצר מהמערך של המוצרים, רק עבור מספר הסכם מזהה
        }
    }

    public Product getProductByID(int id){
        return this.productService.searchProduct_by_id(id);
    }

    public Product getProductByCatalog(int catalog){
        return this.productService.searchProduct_by_catalog(catalog);
    }

    public void add_discountRule(int catalog, double discount, int amount,int agreementID){
        Agreement agreement = this.agreementService.getAgreementByID(agreementID);
        if (agreement != null) {
            Product product = agreement.getProductByCatalog(catalog);
            if (product != null) {
                product.add_discountRule(discount, amount);
            }
        }
    }

    public boolean existsProductWithID(int id){
        return productService.existsProductWithID(id);
    }

    public boolean productExistsByCatalog(int catalog){
        return productService.productExistsByCatalog(catalog);
    }

    /////new function
    public boolean productExistsByCatalogAndProductId(int catalog, int productId){
        return productService.productExistsByCatalogAndProductId(catalog, productId);
    }

    public void updateProductPrice(int catalog, double newPrice, Integer agreementID){
        Agreement agreement = this.agreementService.getAgreementByID(agreementID);
        if (agreement != null) {
            Product product = agreement.getProductByCatalog(catalog);
            product.setPrice(newPrice);
        }
    }
    public void updateProductUnit(int catalogNumber, String newUnit, Integer agreementID){
        Agreement agreement = this.agreementService.getAgreementByID(agreementID);
        if (agreement != null) {
            Product product = agreement.getProductByCatalog(catalogNumber);
            product.setUnitsOfMeasure(newUnit);
        }
    }

//===================================OrderService================================================================\
    public void createOrder(int orderID, int phoneNumber, LocalDateTime orderDate, Map<Integer, Integer> productsInOrder){
        orderService.createOrder(orderID, phoneNumber, orderDate, productsInOrder);
    }
    //--------------------------
    public Map<Integer, Integer> getProductsInOrder(int orderID){
        return orderService.getProductsInOrder(orderID);
    }
    public boolean thereIsOrder(int order_ID) {
        return orderService.thereIsOrder(order_ID);
    }
    //--------------------------
    public Map<Integer, Map.Entry<Integer, Double>> orderWithBestPrice(Map<Integer, Integer> productsInOrder) {
        Map<Integer, Map.Entry<Integer, Double>> orderedProducts = new HashMap<>();

        for (Integer productID : productsInOrder.keySet()) {
            int amount = productsInOrder.get(productID);
            double bestOffer = productService.best_price(productID, amount);
            orderedProducts.put(productID, new AbstractMap.SimpleEntry<>(amount, bestOffer));
        }

        return orderedProducts;
    }

    //-------------------------
    public String getFormattedOrderDate(int orderID){
        Order order = orderService.searchOrderById(orderID);
        return order.getFormattedOrderDate();
    }

    public int getPhoneNumber(int orderID){
        Order order = orderService.searchOrderById(orderID);
        return order.getPhoneNumber();
    }
   /* public boolean canCreateOrder() {
        // בדיקה אם יש ספקים במערכת
        if (!hasSuppliers()) {
            System.out.println("No suppliers in the system.");
            return false;
        }

        // בדיקה אם יש לפחות ספק עם הסכמים ועם מוצרים בתוכם
        for (Supplier supplier : supplierService.getAllSuppliers()) {
            if (supplier.hasAgreements()) {
                for (Agreement agreement : supplier.getAgreements().values()) {
                    if (agreement.hasProducts()) {
                        return true; // אם יש לפחות הסכם עם מוצר, אפשר לבצע הזמנה
                    }
                }
            }
        }

        System.out.println("Cannot create order - no agreements or products available.");
        return false;
    }*/

    public boolean thereIsProductWithSameCatalogNumber(int catalogNumber, int supplierID) {
        Supplier supplier = getSupplierById(supplierID);
        if (supplier != null) {
            int[] allAgreementIDs = supplier.getAllAgreementIDs();
            for (int agreementID : allAgreementIDs) {
                Agreement agreement = this.agreementService.getAgreementByID(agreementID);
                if (agreement != null && agreement.hasProductWithCatalogNumber(catalogNumber)) {
                    return true; // מצאנו מוצר עם אותו מספר קטלוגי
                }
            }
        }
        return false; // לא נמצא בשום הסכם של הספק
    }

    public boolean thereIsProductWithSameProductID(int productID, int supplierID){
        Supplier supplier = getSupplierById(supplierID);
        if (supplier != null) {
            int[] allAgreementIDs = supplier.getAllAgreementIDs();
            for (int agreementID : allAgreementIDs) {
                Agreement agreement = this.agreementService.getAgreementByID(agreementID);
                if (agreement != null && agreement.hasProductWithProductID(productID)) {
                    return true; // מצאנו מוצר עם אותו ProductID
                }
            }
        }
        return false; // לא נמצא בשום הסכם של הספק
    }

}
