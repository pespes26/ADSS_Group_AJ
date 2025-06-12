package com.superli.deliveries.dataaccess.dao.HR;

import com.superli.deliveries.dto.HR.AvailableShiftDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface AvailableShiftDAO {
    Optional<AvailableShiftDTO> findById(int id) throws SQLException;
    List<AvailableShiftDTO> findAll() throws SQLException;
    AvailableShiftDTO save(AvailableShiftDTO t) throws SQLException;
    void deleteById(int id) throws SQLException;
}