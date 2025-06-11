package com.superli.deliveries.dataaccess.dao;

import com.superli.deliveries.dto.ShiftDTO;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ShiftDAO {
    List<ShiftDTO> findAll() throws SQLException;
    Optional<ShiftDTO> findById(String id) throws SQLException;
    void save(ShiftDTO shift) throws SQLException;
    void deleteById(String id) throws SQLException;
    
    // Domain-specific queries
    List<ShiftDTO> findByDate(LocalDate date) throws SQLException;
    List<ShiftDTO> findByDayOfWeek(DayOfWeek day) throws SQLException;
    List<ShiftDTO> findByEmployeeId(String employeeId) throws SQLException;
}