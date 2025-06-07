package com.superli.deliveries.dataaccess.dao;

import com.superli.deliveries.dto.EmployeeDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface EmployeeDAO {
    Optional<EmployeeDTO> findById(int id) throws SQLException;
    List<EmployeeDTO> findAll() throws SQLException;
    EmployeeDTO save(EmployeeDTO t) throws SQLException;
    void deleteById(int id) throws SQLException;
}