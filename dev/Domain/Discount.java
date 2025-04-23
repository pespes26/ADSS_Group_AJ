package Domain;

import java.time.LocalDate;

public class Discount {
    private double discount_rate;  // Discount rate as a percentage (e.g., 10 for 10%)
    private LocalDate start_date;  // Start date of the discount
    private LocalDate end_date;  // End date of the discount\


    // Constructor for creating a new discount
    public Discount(double discount_rate, LocalDate start_date, LocalDate end_date) {
        this.setDiscountRate(discount_rate);
        this.setStartDate(start_date);
        this.setEndDate(end_date);
    }



// Getters and Setters

    public double getDiscountRate() {
        return discount_rate;
    }

    public void setDiscountRate(double discount_rate) {
        if (discount_rate < 0 || discount_rate > 100) {
            System.out.println("Discount rate must be between 0 and 100");
            return;
        }
        this.discount_rate = discount_rate;
    }

    public LocalDate getStartDate() {
        return start_date;
    }

    public void setStartDate(LocalDate start_date) {
        if (start_date == null) {
            System.out.println("Error: Start date cannot be null.");
            return;  // Exit the method if start date is null
        }
        if (end_date != null && start_date.isAfter(end_date)) {
            System.out.println("Error: Start date cannot be after end date.");
            return;  // Exit the method if start date is after the end date
        }
        this.start_date = start_date;  // Set the start date only if it's valid
    }

    public LocalDate getEndDate() {
        return end_date;
    }

    public void setEndDate(LocalDate end_date) {
        if (end_date == null) {
            System.out.println("Error: End date cannot be null.");
            return;  // Exit the method if end date is null
        }
        if (start_date != null && end_date.isBefore(start_date)) {
            System.out.println("Error: End date cannot be before start date.");
            return;  // Exit the method if end date is before the start date
        }
        this.end_date = end_date;  // Set the end date only if it's valid
    }

    // Method to check if the discount is currently active
    public boolean isActive() {
        LocalDate today = LocalDate.now();
        return !today.isBefore(start_date) && !today.isAfter(end_date);
    }

    // Helper method to determine if a discount is expired
    public boolean isExpired() {
        return LocalDate.now().isAfter(end_date);
    }

    @Override
    public String toString() {
        return "Discount{" +
                "discount_rate=" + discount_rate +
                "%, start_date=" + start_date +
                ", end_date=" + end_date +
                '}';
    }
}


