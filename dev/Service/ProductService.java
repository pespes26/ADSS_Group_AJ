package Service;

import Domain.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductService {
    private List<Product> productList;
    public ProductService(){
        this.productList = new ArrayList<>();
    }
    public void createProduct() { ////dooooooooooo

        }

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
}
