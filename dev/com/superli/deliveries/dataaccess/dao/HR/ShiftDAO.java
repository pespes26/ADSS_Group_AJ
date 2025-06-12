package com.superli.deliveries.dataaccess.dao.HR;

import com.superli.deliveries.domain.core.ShiftType;
import com.superli.deliveries.dto.HR.ShiftDTO;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ShiftDAO {
    List<ShiftDTO> findAll() throws SQLException;
    void save(ShiftDTO shift) throws SQLException;
    List<ShiftDTO> findByEmployeeId(String employeeId) throws SQLException;
    List<ShiftDTO> findByDayOfWeekAndShiftType(DayOfWeek day, ShiftType st) throws SQLException;
    void clearAllAndArchive() throws SQLException;
}


