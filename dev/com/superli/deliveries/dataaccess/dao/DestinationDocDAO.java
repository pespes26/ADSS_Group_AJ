package com.superli.deliveries.dataaccess.dao;

import com.superli.deliveries.dto.DestinationDocDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface DestinationDocDAO {
    Optional<DestinationDocDTO> findById(int id) throws SQLException;
    List<DestinationDocDTO> findAll() throws SQLException;
    DestinationDocDTO save(DestinationDocDTO t) throws SQLException;
    void deleteById(int id) throws SQLException;
}