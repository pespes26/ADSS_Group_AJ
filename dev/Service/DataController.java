package Service;
import Domain.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;



public class DataController {
    private HashMap<Integer,Product> products; //Saves all the current products in the store. Key: product ID, Value: an object of product
    private HashMap<Integer,Product> purchase_products; //Saves all purchase products
    private HashMap<String, HashMap<String, HashMap<String, HashMap<String, Integer>>>> productsAmountMapByCategory; //Saves all product quantities in the following format:: Map<category, Map<sub-category,Map<size, Map<location, amount>>>> (location- wareHouse, interiorStore)
    private Set<Integer> catalog_numbers_Set; //Saves all the catalogs numbers we have in store

    public DataController(){
        products = new HashMap<>();
        purchase_products = new HashMap<>();
        productsAmountMapByCategory = new HashMap<>();
        catalog_numbers_Set = new HashSet<>();
    }


    public void ImportData(String path){
        try (CSVReader reader = new CSVReader(new FileReader(path))) {
            String[] nextRecord;
            while ((nextRecord = reader.readNext()) != null) { //reading record by record
                Product product = new Product();
                String input_id = nextRecord[0];
                if (input_id.startsWith("\uFEFF")) { //remove useless chars from csv file
                    input_id = input_id.substring(1);
                }
                input_id = input_id.trim();
                nextRecord[0] = input_id;
                setProductDetails(product, nextRecord); //build product
                catalog_numbers_Set.add(product.getClassification().getCatalogNumber()); //add catalog number to catalog set
                products.put(product.getProductId(), product);

                //add to productsAmountMap
                String category = product.getClassification().getCategory();
                String subCategory = product.getClassification().getSubcategory();
                String size = String.valueOf(product.getClassification().getProductSize());
                String location = product.getStored(); //interiorStore or wareHouse

                updateProductInventoryCount(true, category, subCategory,size,location);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Updates the count of a product in the inventory map based on the specified parameters.
     * The inventory map is structured as a nested HashMap:
     * category -> subCategory -> size -> location -> count.
     *
     * @param add        whether to increment (true) or decrement (false) the count
     * @param category   the category of the product
     * @param sub_category the subcategory of the product
     * @param size       the size of the product
     * @param location   the location of the product ("wareHouse" or "interiorStore")
     */


    public void updateProductInventoryCount(boolean add, String category, String sub_category, String size, String location) {
        // Check if the category exists
        if (!productsAmountMapByCategory.containsKey(category)) {
            productsAmountMapByCategory.put(category, new HashMap<>());
        }

        // Check if the sub category exists
        HashMap<String, HashMap<String, HashMap<String, Integer>>> subCategoryMap = productsAmountMapByCategory.get(category);
        if (!subCategoryMap.containsKey(sub_category)) {
            subCategoryMap.put(sub_category, new HashMap<>());
        }

        // Check if the size exists
        HashMap<String, HashMap<String, Integer>> size_map = subCategoryMap.get(sub_category);
        if (!size_map.containsKey(size)) {
            size_map.put(size, new HashMap<>());
        }

        HashMap<String, Integer> location_map = size_map.get(size);
        if (add) { //increment
            location_map.put(location, location_map.getOrDefault(location, 0) + 1);
            if (location.equals("wareHouse")) {
                location_map.put("interiorStore", 0); //initiate interior store with 0
            } else {
                location_map.put("wareHouse", 0); //initiate wareHouse with 0
            }
        } else { //decrement
            location_map.put(location, location_map.getOrDefault(location, 0) - 1);
        }
    }


    // the details array contains those 15 items: p_id, p_name, p_expiring_date, p_location,
    // p_section, p_catalog_number, p_category, p_sub_category, p_size, p_cost,
    // p_demand, p_supply_time, p_manufacturer, p_supplier_discount, p_store_discount
    public void setProductDetails(Product product, String[] details){
        product.setProductId(Integer.parseInt(details[0]));
        product.setProductName(details[1]);
        product.setExpiringDate(details[2]);
        product.setStored(details[3]);
        product.setSection(details[4]);

        Classification classification = new Classification();
        classification.setCatalogNum(Integer.parseInt(details[5]));
        classification.setCategory(details[6]);
        classification.setSubcategory(details[7]);
        classification.setProductSize(Integer.parseInt(details[8]));
        classification.setCostPrice(Double.parseDouble(details[9]));
        classification.setProductDemand(Integer.parseInt(details[10]));
        classification.setSupplyTime(Integer.parseInt(details[11]));
        classification.setMinAmountForAlert((int)(0.5*classification.getProductDemand() + 0.5*classification.getSupplyTime()));
        classification.setManufacturer(details[12]);
        classification.setSupplierDiscount(Integer.parseInt(details[13]));
        classification.setStoreDiscount(Integer.parseInt(details[14]));

        product.setClassification(classification);
    }

    //return true - when product is valid and added to dataBase.
    //return false - the product's catalog number is wrong
    public boolean addProductController(String product_details) {
        Product product = new Product();
        String[] product_details_array = product_details.split(",");
        setProductDetails(product, product_details_array);
        //checks that catalog number is valid
        if (productsAmountMapByCategory.containsKey(product.getClassification().getCategory()) &&
                productsAmountMapByCategory.get(product.getClassification().getCategory()).containsKey(product.getClassification().getSubcategory()) &&
                productsAmountMapByCategory.get(product.getClassification().getCategory()).get(product.getClassification().getSubcategory()).containsKey(String.valueOf(product.getClassification().getProductSize()))) {

            if (catalog_numbers_Set.contains(product.getClassification().getCatalogNumber())) { //checks if catalog number is exist
                products.put(product.getProductId(), product);
                updateProductInventoryCount(true, product.getClassification().getCategory(), product.getClassification().getSubcategory(), String.valueOf(product.getClassification().getProductSize()), product.getStored());
                return true;
            }
            else{ //catalog number is wrong
                return false;
            }
        }
        else{ //new kind of product
            if(!catalog_numbers_Set.contains(product.getClassification().getCatalogNumber())){ //if catalog number is not exist in set, add product
                products.put(product.getProductId(), product);
                updateProductInventoryCount(true, product.getClassification().getCategory(), product.getClassification().getSubcategory(), String.valueOf(product.getClassification().getProductSize()), product.getStored());
                return true;
            }
            else{
                return false;
            }
        }
    }

    public void markDefect(int product_ID){
        products.get(product_ID).setDefect(true);
    }

    public void handlePurchaseProduct(int product_ID, double sale_price){
        Product p = products.get(product_ID);
        purchase_products.put(product_ID, p);
        purchase_products.get(product_ID).getClassification().setSalePrice(sale_price);
        products.remove(product_ID);
        updateProductInventoryCount(false,p.getClassification().getCategory(), p.getClassification().getSubcategory(), String.valueOf(p.getClassification().getProductSize()), p.getStored()); //decrement by 1 in categories map
    }

    public void handleDefectProduct(int product_ID){
        Product p = products.get(product_ID);
        products.remove(product_ID);
        updateProductInventoryCount(false,p.getClassification().getCategory(), p.getClassification().getSubcategory(), String.valueOf(p.getClassification().getProductSize()), p.getStored()); //decrement by 1 in categories map
    }

    public void updatePriceController(int product_ID, double new_price){
        products.get(product_ID).getClassification().setCostPrice(new_price);
    }

    public String defectReportController(){
        StringBuilder defectReport = new StringBuilder();
        int counter = 1;
        for (Product p : products.values()){
            if(p.isDefect()) {
                defectReport.append(counter).append(". ").append("Product ID: ").append(p.getProductId())
                        .append(", Product Name: ").append(p.getProductName()).append(", Stored: ")
                        .append(p.getStored()).append(", Section:").append(p.getSection())
                        .append(" , Category: ").append(p.getClassification().getCategory())
                        .append(", Sub-Category: ").append(p.getClassification().getSubcategory())
                        .append(", Size: ").append(p.getClassification().getProductSize()).append("\n");
                counter++;
            }
        }
        if(counter == 1){
            defectReport = new StringBuilder("There are no defective products.");}
        return defectReport.toString();
    }

    public String inventoryReportController(String[] categories){
        String report = "";
        // Iterate over the keys of the top-level map (categories)
        for (String category : categories) {
            System.out.println(category + ": " + productsAmountMapByCategory.get(category).size());
            for (String subCategory : productsAmountMapByCategory.get(category).keySet()){ //Iterate over sub-categories
                System.out.println("---" + subCategory + ": " + productsAmountMapByCategory.get(category).get(subCategory).size());
                for(String size : productsAmountMapByCategory.get(category).get(subCategory).keySet()){ //Iterate over sizes
                    System.out.println("------Size(" + size + "):" + " Store - " + productsAmountMapByCategory.get(category).get(subCategory).get(size).get("interiorStore")+
                            ", WareHouse - " + productsAmountMapByCategory.get(category).get(subCategory).get(size).get("wareHouse"));
                }
                System.out.println();
            }
            System.out.println("----------------------------------------------------------------------------------------------");
        }
        return report;
    }

    public String productsDetails(int product_ID){
        String details;
        Product p = products.get(product_ID);
        details = "Product ID: " + p.getProductId() + ", Product name: " + p.getProductName() + "\n" + "Expiring Date: " + p.getExpiringDate() + "\nLocation: "
                + p.getStored() + ", Section: " + p.getSection() + "\nCatalog Number: "
                + p.getClassification().getCatalogNumber() + ", Category: " + p.getClassification().getCategory() + ", Sub-Category: " + p.getClassification().getSubcategory() +
                ", Size: " + p.getClassification().getProductSize() + "\nProduct cost price: "
                + p.getClassification().getCostPrice() + "\nProduct demand: " + p.getClassification().getProductDemand() + "\nSupply time: " + p.getClassification().getSupplyTime() +
                "\nMinimum time For Alert: " + p.getClassification().getMinAmountForAlert() + "\nManufacturer: "
                + p.getClassification().getManufacturer() + "\nSupplier Discount: " + p.getClassification().getSupplierDiscount() + "\nStore Discount: " + p.getClassification().getStoreDiscount() + "\n";

        return details;
    }

    public boolean checkForAlert(int product_ID){
        Product p = products.get(product_ID);
        int wareHouse_amount = productsAmountMapByCategory.get(p.getClassification().getCategory()).get(p.getClassification().getSubcategory()).get(String.valueOf(p.getClassification().getProductSize())).get("wareHouse");
        int interior_store_amount = productsAmountMapByCategory.get(p.getClassification().getCategory()).get(p.getClassification().getSubcategory()).get(String.valueOf(p.getClassification().getProductSize())).get("interiorStore");
        int currentAmount = wareHouse_amount + interior_store_amount - 1; //remove current product from amount
        return currentAmount <= p.getClassification().getMinAmountForAlert();
    }

    public String getProductName(int product_ID){
        return products.get(product_ID).getProductName();
    }

    public void setDiscountForCategory(String category, int discount){
        for(Product p : products.values()){
            if(p.getClassification().getCategory().equals(category)){
                p.getClassification().setStoreDiscount(discount);
            }
        }
    }

    public void setDiscountForSubCategory(String sub_category, int discount){
        for(Product p : products.values()){
            if(p.getClassification().getSubcategory().equals(sub_category)){
                p.getClassification().setStoreDiscount(discount);
            }
        }
    }

    public void setDiscountForCatalogNum(int catalog_number, int discount){
        for(Product p : products.values()){
            if(p.getClassification().getCatalogNumber() == catalog_number){
                p.getClassification().setStoreDiscount(discount);
            }
        }
    }

    public double getProductPurchasePrice(int product_ID){
        return purchase_products.get(product_ID).getClassification().getSalePrice();
    }
}
