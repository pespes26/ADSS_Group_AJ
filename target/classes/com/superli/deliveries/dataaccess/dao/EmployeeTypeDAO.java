package com.superli.deliveries.dataaccess.dao;

import com.superli.deliveries.dto.EmployeeTypeDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface EmployeeTypeDAO {
    Optional<EmployeeTypeDTO> findById(int id) throws SQLException;
    List<EmployeeTypeDTO> findAll() throws SQLException;
}