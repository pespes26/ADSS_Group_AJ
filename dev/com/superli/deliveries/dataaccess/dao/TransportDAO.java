package com.superli.deliveries.dataaccess.dao;

import com.superli.deliveries.dto.TransportDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface TransportDAO {
    Optional<TransportDTO> findById(int id) throws SQLException;
    List<TransportDTO> findAll() throws SQLException;
    TransportDTO save(TransportDTO transport) throws SQLException;
    void deleteById(int id) throws SQLException;
    void clearAll() throws SQLException;
}
