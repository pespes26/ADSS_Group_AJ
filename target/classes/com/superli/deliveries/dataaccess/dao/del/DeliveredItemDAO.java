package com.superli.deliveries.dataaccess.dao.del;

import com.superli.deliveries.dto.del.DeliveredItemDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface DeliveredItemDAO {
    Optional<DeliveredItemDTO> findById(String id) throws SQLException;
    List<DeliveredItemDTO> findAll() throws SQLException;
    DeliveredItemDTO save(DeliveredItemDTO t) throws SQLException;
    void deleteById(String id) throws SQLException;
}