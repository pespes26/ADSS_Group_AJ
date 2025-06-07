package com.superli.deliveries.dataaccess.dao;

import com.superli.deliveries.dto.RoleDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface EmployeeRoleDAO {
    void assignRole(int employeeId, int roleId) throws SQLException;
    void removeRole(int employeeId, int roleId) throws SQLException;
    List<RoleDTO> findRolesForEmployee(int employeeId) throws SQLException;
}
