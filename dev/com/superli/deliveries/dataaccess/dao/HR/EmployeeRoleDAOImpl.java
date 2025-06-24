package com.superli.deliveries.dataaccess.dao.HR;

import com.superli.deliveries.dto.HR.RoleDTO;
import com.superli.deliveries.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRoleDAOImpl implements EmployeeRoleDAO {

    private final Connection conn;

    public EmployeeRoleDAOImpl() {
        try {
            this.conn = Database.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to DB", e);
        }
    }

    public EmployeeRoleDAOImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void assignRole(int employeeId, int roleId) throws SQLException {
        String sql = """
            INSERT INTO employee_roles (employee_id, role_id)
            VALUES (?, ?)
            ON CONFLICT(employee_id, role_id) DO NOTHING
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.setInt(2, roleId);
            ps.executeUpdate();
        }
    }

    @Override
    public void removeRoleFromEmployee(int employeeId, int roleId) throws SQLException {
        String sql = "DELETE FROM employee_roles WHERE employee_id = ? AND role_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.setInt(2, roleId);
            ps.executeUpdate();
        }
    }

    @Override
    public List<RoleDTO> findRolesForEmployee(int employeeId) throws SQLException {
        List<RoleDTO> roles = new ArrayList<>();
        String sql = """
            SELECT r.id, r.name
            FROM roles r
            JOIN employee_roles er ON r.id = er.role_id
            WHERE er.employee_id = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RoleDTO role = new RoleDTO(
                            rs.getInt("id"),
                            rs.getString("name")
                    );
                    roles.add(role);
                }
            }
        }

        return roles;
    }

    @Override
    public void removeRolesByEmployeeId(int employeeId) throws SQLException {
        String sql = "DELETE FROM employee_roles WHERE employee_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.executeUpdate();
        }
    }

}