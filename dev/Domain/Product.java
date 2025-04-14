package Domain;

public class Product {
    int CatalogNumber;
    int IDproduct;
    double Price;
    List<אחוז הנחה,מאיזה כמות>;
    String unitsOfMeasure;

 #להוסיף שיטה שמחזירה את ההנחה בהינתו כמות
    int calcDiscount(int amount);
}
