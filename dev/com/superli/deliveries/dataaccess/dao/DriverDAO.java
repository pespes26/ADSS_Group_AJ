package com.superli.deliveries.dataaccess.dao;

import com.superli.deliveries.dto.DriverDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface DriverDAO {
    Optional<DriverDTO> findById(int id) throws SQLException;
    List<DriverDTO> findAll() throws SQLException;
    DriverDTO save(DriverDTO t) throws SQLException;
    void deleteById(int id) throws SQLException;
}