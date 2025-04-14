package Service;

import Domain.Order;

import java.util.Date;
import java.util.HashMap;

public class OrderService {
    private HashMap<Integer, Order> orderHashMap; // שדה של אשמפ, מחזיק זוגות(הזמנה,מספר מזהה)

    public OrderService() {
        orderHashMap = new HashMap<>(); //אתחול השדה
    }

    public void createOrder(int orderID, int phoneNumber, Date orderDate) {
        Order order = new Order(orderID, phoneNumber, orderDate,   ); // יצירת מופע חדש להזמנה בהינתן מספר מזהה שלה
        this.orderHashMap.put(orderID, order); //הכנסת ההזמנה לרשימת ההזמנות
    }

    public Order searchOrderById(int orderID) {
        return orderHashMap.get(orderID); // חיפוש הזמנה לי ID מזהה שלה
    }
}
