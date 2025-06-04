package com.superli.deliveries.domain.ports;

import com.superli.deliveries.domain.core.Product;
import java.util.Collection;
import java.util.Optional;

/**
 * Interface defining the contract for accessing Product data.
 */
public interface IProductRepository {

    /**
     * Saves (adds or updates) a product. Identified by product ID.
     *
     * @param product The Product object to save. Cannot be null.
     */
    void save(Product product);

    /**
     * Finds a product by its unique ID.
     *
     * @param productId The ID of the product to find.
     * @return An Optional containing the Product if found, or an empty Optional otherwise.
     */
    Optional<Product> findById(String productId);

    /**
     * Finds all products currently stored.
     *
     * @return A Collection of all Product objects. May be empty but not null.
     */
    Collection<Product> findAll();

    /**
     * Deletes a product by its unique ID.
     *
     * @param productId The ID of the product to delete.
     * @return An Optional containing the removed product if it was found and deleted,
     * or an empty Optional otherwise.
     */
    Optional<Product> deleteById(String productId);

    /**
     * Clears all product data. Mainly for testing purposes.
     */
    void clearAll();
}