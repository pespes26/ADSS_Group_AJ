package Presentation;

import java.util.Scanner;

public class Inputs {
    public static int read_int(Scanner scanner, String message){
        int value;
        while (true){
            System.out.print(message);
            if (scanner.hasNextInt()){
                value = scanner.nextInt();
                break;
            }
            else {
                System.out.println("Invalid choice. Please Enter number again: ");
                scanner.next();// מדלג על הקלט הבעייתי (כדי שלא ניתקע בלולאה)
            }
        }
        return value;
    }

    public static double read_double(Scanner scanner, String message){
        double value;
        while (true){
            System.out.print(message);
            if (scanner.hasNextDouble()){
                value = scanner.nextDouble();
                break;
            }
            else {
                System.out.println("Invalid choice. Please Enter number again: ");
                scanner.next();// מדלג על הקלט הבעייתי (כדי שלא ניתקע בלולאה)
            }
        }
        return value;
    }

    public static int read_input_for_choice(Scanner scanner){
        while (true){
            if (scanner.hasNextInt()){
                return scanner.nextInt();
            }
            else {
                System.out.print("Invalid choice. Please Enter a number: ");
                scanner.next();
            }
        }
    }

    public static long read_long(Scanner scanner, String message) {
        long value;
        while (true) {
            System.out.print(message);
            if (scanner.hasNextLong()) {
                value = scanner.nextLong();
                break;
            } else {
                System.out.println("Invalid choice. Please enter a valid number again:");
                scanner.next(); // מדלג על הקלט הבעייתי (כדי שלא ניתקע בלולאה)
            }
        }
        return value;
    }

}
