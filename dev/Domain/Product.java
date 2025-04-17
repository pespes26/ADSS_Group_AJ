package Domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Product {
    int Catalog_Number;
    int product_id;
    double Price;
    String unitsOfMeasure;
    public record DiscountRule(int discount, int amount) {} //this is the content of the discount   // בגדול זה במקום ליצור עוד מחלקה
    List<DiscountRule> discountRules = new ArrayList<>(); //array of discounts


    public Product(int catalog_Number, int product_id, double price, String unitsOfMeasure){
        this.Catalog_Number = catalog_Number;
        this.product_id = product_id;
        this.Price = price;
        this.unitsOfMeasure = unitsOfMeasure;
    }

    public Product(int catalog_Number, int product_id, double price, String unitsOfMeasure, List<DiscountRule> discountRules) {
        this.Catalog_Number = catalog_Number;
        this.product_id = product_id;
        this.Price = price;
        this.unitsOfMeasure = unitsOfMeasure;
        if (discountRules != null) {
            this.discountRules.addAll(discountRules);

        }
    }

    public void add_discountRule(int discount, int amount){
        discountRules.add(new DiscountRule(discount, amount));
    }

    public List<DiscountRule> getDiscountRules() {
        return Collections.unmodifiableList(discountRules); //return in a safe way the discountRules "read only"
    }

    public int getProduct_id() {
        return product_id;
    }

    public int getCatalog_Number() {
        return Catalog_Number;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double new_price){ //new !!!!!! set_price
        this.Price = new_price;
    }


    ///new !!!!!!!!!  setDiscount
    public boolean setDiscount(int discount, int amount){
        for (int i = 0; i < discountRules.size(); i++){
            if(discountRules.get(i).amount() == amount){
                discountRules.set(i, new DiscountRule(discount, amount));
                return true;
            }
        }
        discountRules.add(new DiscountRule(discount, amount));
        return false;
    }

    //#להוסיף שיטה שמחזירה את ההנחה בהינתו כמות
    public int calcDiscount(int amount){
        int current_discount = 0;
        for (int i=0;i<discountRules.size();i++){ //check all the discounts
            DiscountRule rule = discountRules.get(i);  // get the current discount rule from the list
            if(amount>=rule.amount){ //check if the amount is bigger then the discountRules in the list-> great for the discount
                if(rule.discount > current_discount){ //update the sum of the discount
                    current_discount = rule.discount;
                }
            }
        }
        return current_discount; //return the discount
    }
}
