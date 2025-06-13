package com.superli.deliveries.dataaccess.dao.HR;

import com.superli.deliveries.dto.HR.AvailableShiftDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface AvailableShiftDAO {
    AvailableShiftDTO save(AvailableShiftDTO shift) throws SQLException;

    Optional<AvailableShiftDTO> findById(int id);

    List<AvailableShiftDTO> findAll() throws SQLException;

    boolean exists(int employeeId, String dayOfWeek, String shiftType) throws SQLException;

    void deleteById(int id);

    void deleteByCompositeKey(int employeeId, String dayOfWeek, String shiftType) throws SQLException;
}
