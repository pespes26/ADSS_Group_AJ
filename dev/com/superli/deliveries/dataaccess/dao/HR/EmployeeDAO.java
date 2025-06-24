package com.superli.deliveries.dataaccess.dao.HR;

import com.superli.deliveries.dto.HR.EmployeeDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface EmployeeDAO {
    List<EmployeeDTO> findAll() throws SQLException;
    Optional<EmployeeDTO> findById(String id) throws SQLException;
    EmployeeDTO save(EmployeeDTO employee) throws SQLException;
    void deleteById(String id) throws SQLException;

    // Domain-specific queries
    List<EmployeeDTO> findByRole(String role) throws SQLException;

}