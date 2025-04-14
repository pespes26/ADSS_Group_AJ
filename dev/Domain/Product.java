package Domain;

import java.util.ArrayList;
import java.util.List;

public class Product {
    int Catalog_Number;
    int product_id;
    double Price;
    String unitsOfMeasure;

    public record DiscountRule(int discount, int amount) {} //this is the content of the discount
    List<DiscountRule> discountRules = new ArrayList<>(); //array of discounts
    //addDiscountRule(int discount, int amount);


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
