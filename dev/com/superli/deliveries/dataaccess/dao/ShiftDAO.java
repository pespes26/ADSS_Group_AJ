package com.superli.deliveries.dataaccess.dao;

import com.superli.deliveries.dto.ShiftDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ShiftDAO {
    Optional<ShiftDTO> findById(int id) throws SQLException;
    List<ShiftDTO> findAll() throws SQLException;
    ShiftDTO save(ShiftDTO shift) throws SQLException;
    void deleteById(int id) throws SQLException;
    void clearAll() throws SQLException;
}