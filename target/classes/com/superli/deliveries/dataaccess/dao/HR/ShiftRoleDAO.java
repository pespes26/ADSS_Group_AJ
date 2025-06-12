package com.superli.deliveries.dataaccess.dao.HR;



import java.sql.SQLException;
import java.util.List;
import com.superli.deliveries.dto.HR.*;

public interface ShiftRoleDAO {
    void save(ShiftRoleDTO shiftRole) throws SQLException;
    void delete(String dayOfWeek, String shiftType, int roleId) throws SQLException;
    List<ShiftRoleDTO> findAll() throws SQLException;
    ShiftRoleDTO findByKey(String dayOfWeek, String shiftType, int roleId) throws SQLException;
}
