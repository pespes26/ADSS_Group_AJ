package Suppliers.Domain;

import Suppliers.Service.AgreementService;
import Suppliers.Service.OrderService;
import Suppliers.Service.ProductService;
import Suppliers.Service.SupplierService;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Central controller that coordinates between different services:
 * SupplierService, AgreementService, ProductService, and OrderService.
 * Provides a unified interface for the system's main operations.
 */
public class Controller {
    private final AgreementService agreementService;
    private final OrderService orderService;
    private final ProductService productService;
    private final SupplierService supplierService;

    public Controller(){
        agreementService = new AgreementService();
        orderService = new OrderService();
        productService = new ProductService();
        supplierService = new SupplierService();
    }

    //===================================SupplierService================================================================\

    /**
     * Creates a new supplier and stores it in the supplier service.
     */
    public void createSupplier(String supplierName, int supplier_id, int company_id, int bankAccount, String paymentMethod, long phoneNumber, String email, String paymentCondition){
        supplierService.createSupplier(supplierName, supplier_id, company_id, bankAccount, paymentMethod, phoneNumber, email, paymentCondition);
    }

    /**
     * Deletes a supplier, its agreements, and its associated products.
     */
    public void deleteSupplier(int supplier_ID) {
        if (supplierService.thereIsSupplier(supplier_ID)) {
            List<Integer> catalogNumbers = deleteAllAgreementFromSupplier(supplier_ID);
            for (int catalogNumber : catalogNumbers) {
                productService.delete_by_catalogAndSupplierId(catalogNumber, supplier_ID);
            }
            supplierService.deleteSupplier(supplier_ID);
        }
    }

    /**
     * Returns the supplier object by its ID.
     */
    public Supplier getSupplierById(int id){
        Supplier supplier = supplierService.getSupplierById(id);
        if (supplier != null){
            return supplier;
        }
        return null;
    }

    /**
     * Deletes all agreements for a given supplier and returns their product catalog numbers.
     */
    public List<Integer> deleteAllAgreementFromSupplier(int supplier_ID) {
        List<Integer> catalogNumbers = new ArrayList<>();

        int[] agreementIDs = supplierService.deleteAllAgreementFromSupplier(supplier_ID);
        for (int agreementID : agreementIDs) {
            int[] agreementCatalogNumbers = agreementService.deleteAgreementWithSupplier(agreementID);
            for (int catalogNumber : agreementCatalogNumbers) {
                catalogNumbers.add(catalogNumber);
            }
        }

        return catalogNumbers;
    }

    /**
     * Checks if a supplier with the given ID exists.
     */
    public boolean thereIsSupplier(int id){
        return supplierService.thereIsSupplier(id);
    }

    //===================================AgreementService================================================================\

    /**
     * Creates a new agreement and associates it with the supplier.
     */
    public void createAgreement(int agreement_ID, int supplier_ID, String[] deliveryDays, boolean selfPickup){
        Supplier supplier = supplierService.getSupplierById(supplier_ID);
        Agreement agreement = this.agreementService.createAgreementWithSupplier(agreement_ID, supplier_ID, deliveryDays, selfPickup);
        supplier.addNewAgreement(agreement);
    }

    /**
     * Deletes an agreement and removes all its associated products.
     */
    public void deleteAgreement(int agreement_ID) {
        Agreement agreement = agreementService.getAgreementByID(agreement_ID);
        if (agreement != null) {
            int supplierID = agreement.getSupplier_ID();
            int[] allCatalogNumbers = this.agreementService.deleteAgreementWithSupplier(agreement_ID);
            for (int catalogNumber : allCatalogNumbers) {
                productService.delete_by_catalogAndSupplierId(catalogNumber, supplierID);
            }
        }
    }

    /**
     * Adds a product to an agreement.
     */
    public void addProductToAgreement(int product_id, Product newProduct, int agreementID){
        Agreement agreement = this.agreementService.getAgreementByID(agreementID);
        if (agreement != null && newProduct != null) {
            agreement.addNewSupplierProduct(product_id, newProduct);
        }
    }

    /**
     * Returns true if the agreement ID does NOT exist.
     */
    public boolean thereIsAgreement(int agreement_ID){
        return !this.agreementService.thereIsAgreement(agreement_ID);
    }

    /**
     * Updates the delivery days in an agreement.
     */
    public void updateDeliveryDays(int agreementID, String[] newDays){
        Agreement agreement = this.agreementService.getAgreementByID(agreementID);
        if (agreement != null) {
            agreement.updateDeliveryDays(newDays);
        }
    }

    /**
     * Toggles the self-pickup flag for an agreement.
     */
    public boolean toggleSelfPickup(int agreementID){
        Agreement agreement = this.agreementService.getAgreementByID(agreementID);
        return agreement.updateSelfDeliveryOption();
    }

    //===================================ProductService================================================================\

    /**
     * Creates and stores a new product.
     */
    public Product createProduct(int catalog_Number, int product_id, double price, String unitsOfMeasure, int supplierID){
        return productService.createProduct(catalog_Number, product_id, price, unitsOfMeasure, supplierID);
    }

    /**
     * Checks if there are no products in the system.
     */
    public boolean existsZeroProducts(){
        return productService.existsZeroProducts();
    }

    /**
     * Deletes a product from an agreement and from the product system.
     */
    public void delete_by_catalogAndSupplierId(int catalog_Number, int supplierID, int agreementID){
        Agreement agreement = this.agreementService.getAgreementByID(agreementID);
        if (agreement != null) {
            agreement.removeProductByProductCatalogNumber(catalog_Number);
            productService.delete_by_catalogAndSupplierId(catalog_Number, supplierID);
        }
    }

    /**
     * Checks if a product with the same catalog number and supplier ID exists.
     */
    public boolean existsProductWithCatalogAndSupplierId(int catalog_Number, int supplierID){
        return productService.existsProductWithCatalogAndSupplierId(catalog_Number, supplierID);
    }

    /**
     * Updates or adds a discount rule for a specific product in an agreement.
     */
    public void updateOrAddDiscountRule(int catalog, double discount, int amount, int agreementID){
        Agreement agreement = this.agreementService.getAgreementByID(agreementID);
        if (agreement != null) {
            Product product = agreement.getProductByCatalog(catalog);
            if (product != null) {
                product.updateOrAddDiscountRule(discount, amount);
            }
        }
    }

    /**
     * Checks if a product with the given ID exists.
     */
    public boolean existsProductWithID(int id){
        return productService.productExistsProductWithID(id);
    }

    /**
     * Checks if a product exists by its catalog number.
     */
    public boolean productExistsByCatalog(int catalog){
        return productService.productExistsByCatalog(catalog);
    }

    /**
     * Updates the price of a product in an agreement.
     */
    public void updateProductPrice(int catalog, double newPrice, Integer agreementID){
        Agreement agreement = this.agreementService.getAgreementByID(agreementID);
        if (agreement != null) {
            Product product = agreement.getProductByCatalog(catalog);
            product.setPrice(newPrice);
        }
    }

    /**
     * Updates the unit of measure for a product in an agreement.
     */
    public void updateProductUnit(int catalogNumber, String newUnit, Integer agreementID){
        Agreement agreement = this.agreementService.getAgreementByID(agreementID);
        if (agreement != null) {
            Product product = agreement.getProductByCatalog(catalogNumber);
            product.setUnitsOfMeasure(newUnit);
        }
    }

    //===================================OrderService================================================================\

    /**
     * Creates a new order with the given details.
     */
    public void createOrder(int orderID, long phoneNumber, LocalDateTime orderDate, Map<Integer, Integer> productsInOrder, int supplierID){
        orderService.createOrder(orderID, phoneNumber, orderDate, productsInOrder, supplierID);
    }

    /**
     * Retrieves the products in a given order.
     */
    public Map<Integer, Integer> getProductsInOrder(int orderID){
        return orderService.getProductsInOrder(orderID);
    }

    /**
     * Checks if an order with the given ID exists.
     */
    public boolean thereIsOrder(int order_ID) {
        return orderService.thereIsOrder(order_ID);
    }

    /**
     * Calculates the best price for each product in an order.
     */
    public Map<Integer, Map.Entry<Integer, Double>> orderWithBestPrice(Map<Integer, Integer> productsInOrder) {
        Map<Integer, Map.Entry<Integer, Double>> orderedProducts = new HashMap<>();

        for (Integer productID : productsInOrder.keySet()) {
            int amount = productsInOrder.get(productID);
            double bestOffer = productService.best_price(productID, amount);
            orderedProducts.put(productID, new AbstractMap.SimpleEntry<>(amount, bestOffer));
        }

        return orderedProducts;
    }

    /**
     * Returns the formatted date of an order.
     */
    public String getFormattedOrderDate(int orderID){
        Order order = orderService.searchOrderById(orderID);
        return order.getFormattedOrderDate();
    }

    /**
     * Returns the phone number associated with an order.
     */
    public long getPhoneNumber(int orderID){
        Order order = orderService.searchOrderById(orderID);
        return order.getPhoneNumber();
    }

    /**
     * Checks if a product with the same catalog number already exists in any of the supplier's agreements.
     */
    public boolean thereIsProductWithSameCatalogNumberInAgreement(int catalogNumber, int supplierID) {
        Supplier supplier = getSupplierById(supplierID);
        if (supplier != null) {
            int[] allAgreementIDs = supplier.getAllAgreementIDs();
            for (int agreementID : allAgreementIDs) {
                Agreement agreement = this.agreementService.getAgreementByID(agreementID);
                if (agreement != null && agreement.hasProductWithCatalogNumber(catalogNumber)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if a product with the same product ID already exists in any of the supplier's agreements.
     */
    public boolean thereIsProductWithSameProductIDInAgreement(int productID, int supplierID){
        Supplier supplier = getSupplierById(supplierID);
        if (supplier != null) {
            int[] allAgreementIDs = supplier.getAllAgreementIDs();
            for (int agreementID : allAgreementIDs) {
                Agreement agreement = this.agreementService.getAgreementByID(agreementID);
                if (agreement != null && agreement.hasProductWithProductID(productID)) {
                    return true;
                }
            }
        }
        return false;
    }

}
