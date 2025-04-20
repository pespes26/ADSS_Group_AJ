package Domain;

import Service.AgreementService;
import Service.OrderService;
import Service.ProductService;
import Service.SupplierService;

import java.util.AbstractMap;
import java.util.Date;
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

    public void createSupplier(String supplierName, int supplier_id, int company_id, int bankAccount, String paymentMethod, int phoneNumber, String email, String paymentDay){
        supplierService.createSupplier(supplierName, supplier_id,  company_id,  bankAccount, paymentMethod, phoneNumber,  email, paymentDay);
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

//===================================AgreementService================================================================\

    public void createAgreement(int agreement_ID, int supplier_ID, String[] deliveryDays, boolean selfPickup){
        this.agreementService.createAgreementWithSupplier(agreement_ID, supplier_ID, deliveryDays, selfPickup);
    }
    //--------------------------
    public void deleteAgreement(int agreement_ID) {
        int[] allCatalogNumbers = this.agreementService.deleteAgreementWithSupplier(agreement_ID);
        for (int catalogNumber : allCatalogNumbers) {
            productService.delete_by_catalog(catalogNumber); // Delete each product from the product system
        }
    }
    //--------------------------
//    public Agreement getAgreementWithSupplier(int agreement_ID){
//        Agreement agreement = agreementService.getAgreementByID(agreement_ID);
//        if (agreement != null){
//            System.out.println("Agreement found!");
//            return agreement;
//        }
//        System.out.println("Agreement not found");
//        return null;
//    }
    //--------------------------
    public void addProductToAgreement(int agreementID, int catalog_Number, int product_id, double price, String unitsOfMeasure){
        Agreement agreement = this.agreementService.getAgreementByID(agreementID);
        if (agreement != null) {
            Product newProduct = productService.createProduct(catalog_Number, product_id, price, unitsOfMeasure); // Create the product
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

    public void toggleSelfPickup(int agreementID){
        Agreement agreement  = this.agreementService.getAgreementByID(agreementID);
        if (agreement != null) {
            agreement.updateSelfDeliveryOption();
        }
    }


//===================================ProductService================================================================\
    public Product createProduct(int catalog_Number, int product_id, double price, String unitsOfMeasure){
        return productService.createProduct(catalog_Number, product_id, price, unitsOfMeasure);
    }

    public boolean deleteProductByID(int id){
        return productService.delete_by_id(id);//// לשאול את רן למה הוא שם את זה עם השדה boolean
    }

    public boolean deleteProductByCatalog(int catalog_Number){
        return productService.delete_by_catalog(catalog_Number);//// לשאול את רן למה הוא שם את זה עם השדה boolean
    }

    public Product getProductByID(int id){
        return this.productService.searchProduct_by_id(id);
    }

    public Product getProductByCatalog(int catalog){
        return this.productService.searchProduct_by_catalog(catalog);
    }

    public void add_discountRule(int catalog, double discount, int amount){
        Product product = getProductByCatalog(catalog);
        if (product != null){
            product.add_discountRule(discount, amount);
        }
    }

    public boolean existsProductWithID(int id){
        return productService.existsProductWithID(id);
    }

    public boolean productExistsByCatalog(int catalog){
        return productService.productExistsByCatalog(catalog);
    }

    public void updateProductPrice(int catalog, double newPrice){
        Product product = getProductByCatalog(catalog);
        product.setPrice(newPrice);
    }
    public void updateProductUnit(int catalogNumber, String newUnit){
        Product product = getProductByCatalog(catalogNumber);
        product.setUnitsOfMeasure(newUnit);
    }
    //--------------------------
//    public Product getProductByID(int id){
//        Product product = productService.searchProduct_by_id(id);
//        if (product != null){
//            return product;
//        }
//        else {
//            System.out.println("Product not found");
//        }
//        return null;
//    }

//===================================OrderService================================================================\
    public void createOrder(int orderID, int phoneNumber, Date orderDate, Map<Integer, Integer> productsInOrder){
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

}
