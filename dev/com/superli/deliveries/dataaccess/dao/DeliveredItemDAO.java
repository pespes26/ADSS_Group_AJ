package com.superli.deliveries.dataaccess.dao;

import com.superli.deliveries.dto.DeliveredItemDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface DeliveredItemDAO {
    Optional<DeliveredItemDTO> findById(int id) throws SQLException;
    List<DeliveredItemDTO> findAll() throws SQLException;
    DeliveredItemDTO save(DeliveredItemDTO t) throws SQLException;
    void deleteById(int id) throws SQLException;
}