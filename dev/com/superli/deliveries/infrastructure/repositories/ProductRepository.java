package com.superli.deliveries.storage;

import com.superli.deliveries.domain.Product;
import com.superli.deliveries.domain.ports.IProductRepository;

import java.util.*;

/**
 * In-memory repository implementation for managing Product objects.
 * Implements the IProductRepository interface.
 */
public class ProductRepository implements IProductRepository {

    private final Map<String, Product> productMap; // Key: productId

    public ProductRepository() {
        this.productMap = new HashMap<>();
    }

    @Override
    public void save(Product product) {
        Objects.requireNonNull(product, "Product cannot be null");
        Objects.requireNonNull(product.getProductId(), "Product ID cannot be null");
        productMap.put(product.getProductId(), product);
        System.out.println("Product saved/updated: " + product.getProductId());
    }

    @Override
    public Optional<Product> findById(String productId) {
        return Optional.ofNullable(productMap.get(productId));
    }

    @Override
    public Collection<Product> findAll() {
        return Collections.unmodifiableCollection(productMap.values());
    }

    @Override
    public Optional<Product> deleteById(String productId) {
        if (productId != null) {
            Product removed = productMap.remove(productId);
            if(removed != null) {
                System.out.println("Product removed: " + productId);
            }
            return Optional.ofNullable(removed);
        }
        return Optional.empty();
    }

    @Override
    public void clearAll() {
        productMap.clear();
        System.out.println("ProductRepository cleared.");
    }
}