package com.superli.deliveries.dataaccess.dao;

import com.superli.deliveries.dto.EmployeeDTO;

import java.sql.SQLException;
import java.util.List;

public interface ArchivedEmployeeDAO {
    void archive(EmployeeDTO employee) throws SQLException;
    List<EmployeeDTO> findAllArchivedEmployees() throws SQLException;
}
