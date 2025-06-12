package com.superli.deliveries.dataaccess.dao.HR;

import com.superli.deliveries.dto.HR.EmployeeDTO;

import java.sql.SQLException;
import java.util.List;

public interface ArchivedEmployeeDAO {
    List<EmployeeDTO> findAllArchivedEmployees() throws SQLException;
}
