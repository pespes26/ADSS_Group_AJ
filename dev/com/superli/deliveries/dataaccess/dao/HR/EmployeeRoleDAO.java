package com.superli.deliveries.dataaccess.dao.HR;

import com.superli.deliveries.dto.HR.RoleDTO;

import java.sql.SQLException;
import java.util.List;

public interface EmployeeRoleDAO {
    void assignRole(int employeeId, int roleId) throws SQLException;
    void removeRoleFromEmployee(int employeeId, int roleId) throws SQLException;
    List<RoleDTO> findRolesForEmployee(int employeeId) throws SQLException;
    void removeRolesByEmployeeId(int employeeId) throws SQLException;
}
