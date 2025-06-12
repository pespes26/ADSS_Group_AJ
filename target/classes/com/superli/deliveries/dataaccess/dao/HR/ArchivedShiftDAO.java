package com.superli.deliveries.dataaccess.dao.HR;

import com.superli.deliveries.dto.HR.ArchivedShiftDTO;
import com.superli.deliveries.dto.HR.ShiftDTO;

import java.sql.SQLException;
import java.util.List;

public interface ArchivedShiftDAO {
    List<ArchivedShiftDTO> findAllArchivedShifts() throws SQLException;
}