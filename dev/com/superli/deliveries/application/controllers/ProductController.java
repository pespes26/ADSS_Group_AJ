package com.superli.deliveries.application.controllers;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.superli.deliveries.application.services.ProductService;
import com.superli.deliveries.domain.core.Product;

public class ProductController {
    private final ProductService productService;
    private final Scanner scanner;

    public ProductController(ProductService productService) {
        this.productService = productService;
        this.scanner = new Scanner(System.in);
    }

    public void runMenu() {
        while (true) {
            System.out.println("\n=== Product Menu ===");
            System.out.println("1. Show all products");
            System.out.println("2. Add new product");
            System.out.println("3. Edit product details");
            System.out.println("4. Delete product");
            System.out.println("5. Search products by name");
            System.out.println("6. Search products by weight range");
            System.out.println("0. Back to main menu");
            System.out.print("Your choice: ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1" -> showAllProducts();
                case "2" -> addProduct();
                case "3" -> editProduct();
                case "4" -> deleteProduct();
                case "5" -> searchProductsByName();
                case "6" -> searchProductsByWeightRange();
                case "0" -> {
                    System.out.println("Returning to main menu...");
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void showAllProducts() {
        List<Product> products = productService.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("No products found.");
        } else {
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║            ALL PRODUCTS            ║");
            System.out.println("╚════════════════════════════════════╝");

            for (int i = 0; i < products.size(); i++) {
                Product product = products.get(i);
                System.out.println("\n[" + (i+1) + "] PRODUCT: " + product.getName() + " (ID: " + product.getProductId() + ")");
                System.out.println("    Weight: " + product.getWeight() + " kg");
                System.out.println("    " + "-".repeat(40));
            }
        }
    }

    private void addProduct() {
        System.out.print("Enter Product ID: ");
        String id = scanner.nextLine().trim();

        if (productService.getProductById(id).isPresent()) {
            System.out.println("Product with this ID already exists.");
            return;
        }

        System.out.print("Enter Product Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter Weight (kg): ");
        try {
            float weight = Float.parseFloat(scanner.nextLine().trim());
            if (weight <= 0) {
                System.out.println("Weight must be positive.");
                return;
            }

            Product product = new Product(id, name, weight);
            productService.saveProduct(product);
            System.out.println("Product added successfully.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid weight value. Please enter a valid number.");
        }
    }

    private void editProduct() {
        showAllProducts();
        System.out.print("\nEnter Product ID to edit: ");
        String id = scanner.nextLine().trim();

        Optional<Product> productOpt = productService.getProductById(id);
        if (productOpt.isEmpty()) {
            System.out.println("Product not found.");
            return;
        }

        Product product = productOpt.get();
        System.out.println("\nCurrent Product Information:");
        System.out.println("ID: " + product.getProductId());
        System.out.println("Name: " + product.getName());
        System.out.println("Weight: " + product.getWeight() + " kg");

        System.out.print("\nEnter new name (or press Enter to keep current): ");
        String newName = scanner.nextLine().trim();
        if (!newName.isEmpty()) {
            product = new Product(product.getProductId(), newName, product.getWeight());
        }

        System.out.print("Enter new weight (or press Enter to keep current): ");
        String weightInput = scanner.nextLine().trim();
        if (!weightInput.isEmpty()) {
            try {
                float newWeight = Float.parseFloat(weightInput);
                if (newWeight <= 0) {
                    System.out.println("Weight must be positive. Update cancelled.");
                    return;
                }
                product = new Product(product.getProductId(), product.getName(), newWeight);
            } catch (NumberFormatException e) {
                System.out.println("Invalid weight value. Update cancelled.");
                return;
            }
        }

        productService.saveProduct(product);
        System.out.println("Product updated successfully.");
    }

    private void deleteProduct() {
        showAllProducts();
        System.out.print("\nEnter Product ID to delete: ");
        String id = scanner.nextLine().trim();

        if (productService.getProductById(id).isEmpty()) {
            System.out.println("Product not found.");
            return;
        }

        System.out.print("Are you sure you want to delete this product? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        if (confirmation.equals("yes")) {
            productService.deleteProduct(id);
            System.out.println("Product deleted successfully.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private void searchProductsByName() {
        System.out.print("Enter product name to search: ");
        String searchTerm = scanner.nextLine().trim();
        
        List<Product> products = productService.getAllProductsFilteredByName(searchTerm);
        if (products.isEmpty()) {
            System.out.println("No products found matching the search term.");
        } else {
            System.out.println("\nSearch Results:");
            for (Product product : products) {
                System.out.println("ID: " + product.getProductId());
                System.out.println("Name: " + product.getName());
                System.out.println("Weight: " + product.getWeight() + " kg");
                System.out.println("-".repeat(40));
            }
        }
    }

    private void searchProductsByWeightRange() {
        try {
            System.out.print("Enter minimum weight (kg): ");
            float minWeight = Float.parseFloat(scanner.nextLine().trim());
            
            System.out.print("Enter maximum weight (kg): ");
            float maxWeight = Float.parseFloat(scanner.nextLine().trim());

            if (minWeight > maxWeight) {
                System.out.println("Minimum weight cannot be greater than maximum weight.");
                return;
            }

            List<Product> products = productService.getProductsByWeightRange(minWeight, maxWeight);
            if (products.isEmpty()) {
                System.out.println("No products found in the specified weight range.");
            } else {
                System.out.println("\nProducts in weight range " + minWeight + " - " + maxWeight + " kg:");
                for (Product product : products) {
                    System.out.println("ID: " + product.getProductId());
                    System.out.println("Name: " + product.getName());
                    System.out.println("Weight: " + product.getWeight() + " kg");
                    System.out.println("-".repeat(40));
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid weight value. Please enter valid numbers.");
        }
    }
} 