package com.superli.deliveries.dataaccess.dao;

import com.superli.deliveries.dto.ProductDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ProductDAO {
    Optional<ProductDTO> findById(int id) throws SQLException;
    List<ProductDTO> findAll() throws SQLException;
    ProductDTO save(ProductDTO t) throws SQLException;
    void deleteById(int id) throws SQLException;
}