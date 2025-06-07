package com.superli.deliveries.dataaccess.dao;

import com.superli.deliveries.dto.TruckDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface TruckDAO {
    Optional<TruckDTO> findById(int id) throws SQLException;
    List<TruckDTO> findAll() throws SQLException;
    TruckDTO save(TruckDTO t) throws SQLException;
    void deleteById(int id) throws SQLException;
}