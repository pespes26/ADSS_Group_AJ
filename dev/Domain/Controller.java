package Domain;

import Service.AgreementService;
import Service.OrderService;
import Service.ProductService;
import Service.SupplierService;

import java.util.Date;
import java.util.Map;

public class Controller {
    private AgreementService agreementService;
    private OrderService orderService;
    private ProductService productService  ;
    private SupplierService supplierService;

//===================================SupplierService================================================================\

    public Supplier createSupplier(String supplierName, int supplier_id, int company_id, int bankAccount, String paymentMethod, int phoneNumber, String email, String paymentDay){
        return supplierService.createSupplier(supplierName, supplier_id,  company_id,  bankAccount, paymentMethod, phoneNumber,  email, paymentDay);
    }
    //--------------------------
    public void deleteSupplier(int supplier_ID){
        supplierService.deleteSupplier(supplier_ID);
    }
    //--------------------------
    public Supplier getSupplierById(int id){
        Supplier supplier = supplierService.getSupplierById(id);
        if (supplier != null){
            return supplier;

        }
        System.out.println("Supplier not found");
        return null;
    }
    //--------------------------
    public void deleteOneAgreementFromSupplier(int supplier_ID, int agreement_ID){
          supplierService.deleteOneAgreementFromSupplier(supplier_ID, agreement_ID);
    }
    //--------------------------
    public void deleteAllAgreementFromSupplier(int supplier_ID){
        supplierService.deleteAllAgreementFromSupplier(supplier_ID);
    }

    //--------------------------
    public boolean searchSupplierByID(int id){
        return supplierService.searchSupplierByID(id);
    }

//===================================AgreementService================================================================\

    public Agreement createAgreement(int agreement_ID, int supplier_ID, String[] deliveryDays, boolean selfPickup){
        return this.agreementService.createAgreementWithSupplier(agreement_ID, supplier_ID, deliveryDays, selfPickup);
    }
    //--------------------------
    public void deleteAgreement(int agreement_ID) {
        this.agreementService.deleteAgreementWithSupplier(agreement_ID);
    }
    //--------------------------
    public Agreement getAgreementWithSupplier(int agreement_ID){
        Agreement agreement = agreementService.findAgreementWithSupplier(agreement_ID);
        if (agreement != null){
            System.out.println("Agreement found!");
            return agreement;
        }
        System.out.println("Agreement not found");
        return null;
    }
    //--------------------------
    public void addProductToAgreement(int agreementID, int catalog_Number, int product_id, double price, String unitsOfMeasure){
        this.agreementService.addProductToAgreement(agreementID, catalog_Number, product_id, price, unitsOfMeasure);
    }
    //--------------------------
    public void deleteAgreementWithSupplier(int agreement_ID){
        this.agreementService.deleteAgreementWithSupplier(agreement_ID);
    }
    //--------------------------
    public void removeProductFromAgreement(int agreementID, int product_id){
        this.agreementService.removeProductFromAgreement(agreementID, product_id);
    }

//===================================ProductService================================================================\
    public Product createProduct(int catalog_Number, int product_id, double price, String unitsOfMeasure){
        return productService.createProduct(catalog_Number, product_id, price, unitsOfMeasure);
    }

    public boolean deleteProductByID(int id){
        return productService.delete_by_id(id);//// לשאול את רן למה הוא שם את זה עם השדה boolean
    }
    //--------------------------
    public Product getProductByID(int id){
        Product product = productService.searchProduct_by_id(id);
        if (product != null){
            return product;
        }
        else {
            System.out.println("Product not found");
        }
        return null;
    }

//===================================OrderService================================================================\
    public void createOrder(int orderID, int phoneNumber, Date orderDate, Map<Integer, Integer> productsInOrder){
        orderService.createOrder(orderID, phoneNumber, orderDate, productsInOrder);
    }
    //--------------------------
    public Map<Integer, Map.Entry<Integer, Double>> getProductsInOrder(int orderID){
        return orderService.getProductsInOrder(orderID);
    }

}
