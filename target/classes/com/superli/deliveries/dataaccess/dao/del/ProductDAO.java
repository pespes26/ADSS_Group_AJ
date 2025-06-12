package com.superli.deliveries.dataaccess.dao.del;

import com.superli.deliveries.dto.del.ProductDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ProductDAO {
    List<ProductDTO> findAll() throws SQLException;
    Optional<ProductDTO> findById(String id) throws SQLException;
    ProductDTO save(ProductDTO product) throws SQLException;
    void deleteById(String id) throws SQLException;
    
    // Domain-specific queries
    List<ProductDTO> findByCategory(String category) throws SQLException;
    List<ProductDTO> findByWeightRange(float minWeight, float maxWeight) throws SQLException;
    List<ProductDTO> findAvailableProducts() throws SQLException;
}