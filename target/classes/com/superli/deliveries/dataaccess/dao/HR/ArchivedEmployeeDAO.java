package com.superli.deliveries.dataaccess.dao.HR;

import com.superli.deliveries.dto.HR.ArchivedEmployeeDTO;

import java.sql.SQLException;
import java.util.List;

public interface ArchivedEmployeeDAO {
    List<ArchivedEmployeeDTO> findAll() throws SQLException;
    void save(ArchivedEmployeeDTO archivedEmployee) throws SQLException;
}