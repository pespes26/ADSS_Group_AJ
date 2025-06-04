package com.superli.deliveries.application.services;

import com.superli.deliveries.domain.core.Product;
import com.superli.deliveries.domain.ports.IProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing products in the context of deliveries.
 * Provides access to product information needed for weight calculations.
 */
public class ProductService {

    private final IProductRepository productRepository;

    // Temporary in-memory storage if repository is not available
    private final List<Product> tempProducts;

    public ProductService(IProductRepository productRepository) {
        this.productRepository = productRepository;
        this.tempProducts = new ArrayList<>();

        // Initialize with some sample products for testing
        initializeSampleProducts();
    }

    /**
     * Gets a product by its ID.
     * @param productId The product ID to search for
     * @return Optional containing the product if found
     */
    public Optional<Product> getProductById(String productId) {
        // Try to get from repository first
        if (productRepository != null) {
            return productRepository.findById(productId);
        }

        // Fall back to in-memory list
        return tempProducts.stream()
                .filter(p -> p.getProductId().equals(productId))
                .findFirst();
    }

    /**
     * Gets all available products.
     * @return List of all products
     */
    public List<Product> getAllProducts() {
        // Try to get from repository first
        if (productRepository != null) {
            return new ArrayList<>(productRepository.findAll());
        }

        // Fall back to in-memory list
        return new ArrayList<>(tempProducts);
    }

    /**
     * Saves a product.
     * @param product The product to save
     */
    public void saveProduct(Product product) {
        if (productRepository != null) {
            productRepository.save(product);
        } else {
            // Remove existing product with same ID if exists
            tempProducts.removeIf(p -> p.getProductId().equals(product.getProductId()));
            tempProducts.add(product);
        }
    }

    /**
     * Initializes some sample products for testing.
     * This would normally be handled by a proper data initialization process.
     */
    private void initializeSampleProducts() {
        // Create some sample products with realistic weights
        tempProducts.add(new Product("P001", "Milk 1L", 1.03f));
        tempProducts.add(new Product("P002", "Bread Loaf", 0.75f));
        tempProducts.add(new Product("P003", "Rice 5kg Bag", 5.0f));
        tempProducts.add(new Product("P004", "Water 6-pack", 6.0f));
        tempProducts.add(new Product("P005", "Flour 1kg", 1.0f));
        tempProducts.add(new Product("P006", "Sugar 1kg", 1.0f));
        tempProducts.add(new Product("P007", "Olive Oil 750ml", 0.75f));
        tempProducts.add(new Product("P008", "Pasta 500g", 0.5f));
        tempProducts.add(new Product("P009", "Coffee 250g", 0.25f));
        tempProducts.add(new Product("P010", "Canned Beans 400g", 0.4f));

        // Add some bulkier items
        tempProducts.add(new Product("P011", "Washing Machine", 75.0f));
        tempProducts.add(new Product("P012", "Refrigerator", 95.0f));
        tempProducts.add(new Product("P013", "Microwave Oven", 15.0f));
        tempProducts.add(new Product("P014", "Office Chair", 18.0f));
        tempProducts.add(new Product("P015", "Desk", 45.0f));
    }
}