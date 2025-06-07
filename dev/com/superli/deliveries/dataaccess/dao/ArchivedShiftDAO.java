package com.superli.deliveries.dataaccess.dao;

import com.superli.deliveries.dto.ShiftDTO;

import java.sql.SQLException;
import java.util.List;

public interface ArchivedShiftDAO {
    void archive(ShiftDTO shift) throws SQLException;
    List<ShiftDTO> findAllArchivedShifts() throws SQLException;
}