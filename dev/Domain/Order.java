package Domain;


import java.util.Date;
import java.util.Map;

public class Order {
    int orderID;
    int phoneNumber;
    Date orderDate;
    Map<Integer, Integer> productsInOrder; // יהיו פה זוגות של <מזהה מוצר,כמות>
//    HashMap<Integer, KeyPair> deliveryDays;
    public Order(int orderID, int phoneNumber, Date orderDate, Map<Integer, Integer> productsInOrder) {
        this.orderID = orderID;
        this.phoneNumber = phoneNumber;
        this.orderDate = orderDate;
        this.productsInOrder = productsInOrder;
    }

}
