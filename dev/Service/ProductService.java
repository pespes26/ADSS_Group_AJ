package Service;

import Domain.Product;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProductService {
    private List<Product> productList;
    public ProductService(){
        this.productList = new ArrayList<>();
    }

    public Product createProduct(int catalog_Number, int product_id, double price, String unitsOfMeasure, int supplierID) { //create a product
        Product product = new Product(catalog_Number, product_id, price, unitsOfMeasure, supplierID);
        this.productList.add(product);
        return product;
    }


    public void delete_by_catalogAndSupplierId(int catalog, int supplierId) {
        Iterator<Product> iterator = productList.iterator();
        while (iterator.hasNext()) {
            Product product = iterator.next();
            if (product.getSupplierID() == supplierId && product.getCatalog_Number() == catalog) {
                iterator.remove(); // הסרה בטוחה תוך כדי איטרציה
            }
        }
    }


    public boolean productExistsProductWithID(int ProductID){
        for (Product product : productList) {
            if (product.getProduct_id() == ProductID) {
                return true;
            }
        }
        return false;
    }


    public boolean productExistsByCatalog(int catalog) {
        for (Product product : productList) {
            if (product.getCatalog_Number() == catalog) {
                return true;
            }
        }
        return false;
    }


    public boolean existsZeroProducts() {
        return productList.isEmpty();
    }


    public double best_price(int product_id, int amount){
        //Product best_product = null; //init the best product to null
        double final_price = Double.MAX_VALUE; //init the final_price to MAX_VALUE
        for (Product product : productList) {
            if (product.getProduct_id() == product_id) { //check by id product
                double current_price = product.get_price_after_discount(amount);
                // check if chipper : (use "get_price_after_discount" to calculate the price after optional discount)
               if(current_price < final_price){
                   final_price = current_price;
               }
            }
        }
        if (final_price == Double.MAX_VALUE){//can't create
            return -1;
        }
//        return final_price;
        return final_price * amount;
    }

    public boolean existsProductWithCatalogAndSupplierId(int catalog_Number, int supplierID){
        for (Product product : productList) {
            if (product.getCatalog_Number() == catalog_Number && product.getSupplierID() == supplierID) {
                return true;
            }
        }
        return false;
    }

    //new - get catalog_number and product_id
//    public boolean productExistsByCatalogAndProductId(int catalog, int productId) {
//        for (Product product : productList) {
//            if (product.getCatalog_Number() == catalog && product.getProduct_id() == productId) {
//                return true;
//            }
//        }
//        return false;
//    }

//    public void delete_by_id(int id) {
//        if (existsProductWithID(id)) {
//            productList.remove(searchProduct_by_id(id));
//        }
//    }

    //search by id
//    public Product searchProduct_by_id(int id){
//        for(Product product : productList){ //check product in product_list
//            if(product.getProduct_id()==id){ //equal the id
//                return product; //return the product
//            }
//        }
//        return null;
//    }

//    public void delete_by_catalog(int catalog) {
//        if(productExistsByCatalog(catalog)) {
//            productList.remove(getProductByCatalog(catalog));
//        }
//    }

//    public Product getProductByCatalog(int catalog){
//        for(Product product : productList){ //check product in product_list
//            if(product.getCatalog_Number()==catalog){ //equal the catalog_number
//                return product; //return the product
//            }
//        }
//        return null;
//    }

}
