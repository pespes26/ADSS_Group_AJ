package Service;

import Domain.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductService {
    private List<Product> productList;
    public ProductService(){
        this.productList = new ArrayList<>();
    }

/*    public ProductService(List<Product> initialProducts) {
        this.productList = (initialProducts != null) ? new ArrayList<>(initialProducts) : new ArrayList<>();
    }*/

    public Product createProduct(int catalog_Number, int product_id, double price, String unitsOfMeasure) { //create a product
        Product product = new Product(catalog_Number, product_id, price, unitsOfMeasure);
        this.productList.add(product);
        return product;
        }

    public boolean delete_by_id(int id) {
        return productList.remove(searchProduct_by_id(id));
    }

    public boolean delete_by_catalog(int catalog) {
        return productList.remove(searchProduct_by_catalog(catalog));
    }

    public Product searchProduct_by_catalog(int catalog){
        for(Product product : productList){ //check product in product_list
            if(product.getCatalog_Number()==catalog){ //equal the catalog_number
                return product; //return the product
            }
        }
        return null;
    }

    //search by id
    public Product searchProduct_by_id(int id){
        for(Product product : productList){ //check product in product_list
            if(product.getProduct_id()==id){ //equal the id
                return product; //return the product
            }
        }
        return null;
    }

    /////////////
    public int best_price(int product_id, int amount){
        Product best_product = null; //init the best product to null
        for (Product product : productList) {
            if (product.getProduct_id() == product_id) { //check by id product
                //if is the firs time -> best_product = product
                //if not the first time -> check if chipper
               if(best_product == null || product.getPrice() < best_product.getPrice()){
                   best_product = product;
               }
            }
        }
        return best_product.getPrice();
    }
}
